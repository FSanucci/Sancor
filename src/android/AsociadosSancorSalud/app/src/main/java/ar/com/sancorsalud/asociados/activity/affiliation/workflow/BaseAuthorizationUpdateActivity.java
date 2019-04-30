package ar.com.sancorsalud.asociados.activity.affiliation.workflow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.CommentsAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.BaseAuthorization;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.ImageProviderListener;
import ar.com.sancorsalud.asociados.utils.LoadResourceUriCallback;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

/**
 * Created by francisco on 28/9/17.
 */

public class BaseAuthorizationUpdateActivity extends BaseActivity {

    private static final String TAG = "BASE_AUTH";
    private static final String FILE_PREFIX = "attach";

    protected ScrollView mScrollView;
    protected EditText mLastState;

    // ATTACHS  Files
    protected RecyclerView mFilesRecyclerView;
    protected AttachFilesAdapter mFileAdapter;
    protected Button addFileButton;

    protected List<AttachFile> tmpFiles = new ArrayList<AttachFile>();

    // COMMENTS
    protected RecyclerView mCommentsRecyclerView;
    protected CommentsAdapter mCommentsAdapter;

    protected BaseAuthorization mBaseAuth;

    protected EditText mCommentsEditText;
    protected Button sendButton;


    // --- TEMPLATE METHODS ------------------ //

    public void getAuthorizationDataList(){}
    public void updateAuthorizationData(){}

    // ---Helper methods ------------------ //

    @Override
    public void updateFileList(AttachFile attachFile) {
        if (attachFileType.equals(FILE_PREFIX)) {
            mFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position){
        if (attachFileType.equals(FILE_PREFIX)) {
            mFileAdapter.removeItem(position);
        }
    }

    protected void fillAllPhysicalFiles() {

        showProgressDialog(R.string.auth_data_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll Auth Data PhysicalFiles ....");
                fillAttachFiles();
            }
        }).start();
    }

    protected void fillAttachFiles() {

        if (!mBaseAuth.files.isEmpty()) {

            Log.e(TAG, "fillAttachsFiles !!....");
            final AttachFile file = mBaseAuth.files.remove(0);
            if (file.fileNameAndExtension == null  || file.fileNameAndExtension .isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + FILE_PREFIX, file.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Front File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpFiles.add(resultFile);
                            fillAttachFiles();

                        } else {
                            Log.e(TAG, "Error getting file ....");
                            tmpFiles.add(file);
                            fillAttachFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpFiles.add(file);
                        fillAttachFiles();
                    }
                });
            }else{
                tmpFiles.add(file);
                fillAttachFiles();
            }

        }else {
            mBaseAuth.files.addAll(tmpFiles);
            tmpFiles = new ArrayList<AttachFile>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initialize();
                }
            });
        }
    }

    protected void initialize(){
        initializeForm();
        setupListeners();
    }

    protected void initializeForm() {
        Log.e(TAG, "initializeForm!!!!!:");

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        mLastState = (EditText) findViewById(R.id.last_state_input);

        mCommentsEditText = (EditText) findViewById(R.id.comments_input);
        sendButton = (Button) findViewById(R.id.send_button);

        initFiles();
        initCommnets();
    }

    private void initFiles() {
        mFileAdapter = new AttachFilesAdapter(mBaseAuth.files, true);

        mFilesRecyclerView = (RecyclerView) findViewById(R.id.files_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFilesRecyclerView.getContext());
        mFilesRecyclerView.setLayoutManager(attachLayoutManager);
        mFilesRecyclerView.setAdapter(mFileAdapter);
        mFilesRecyclerView.setHasFixedSize(true);

        addFileButton = (Button) findViewById(R.id.file_button);
    }

    private void initCommnets() {
        mCommentsAdapter = new CommentsAdapter(mBaseAuth.commentList);

        mCommentsRecyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCommentsRecyclerView.getContext());
        mCommentsRecyclerView.setLayoutManager(attachLayoutManager);
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsRecyclerView.setHasFixedSize(true);
    }


    protected void setupListeners() {
        setupImageProvider();

        // buttons
        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFileType = FILE_PREFIX;
                mImageProvider.showImagePicker(attachFilesSubDir + "/" + FILE_PREFIX , FILE_PREFIX + FileHelper.getFilePrefix());
            }
        });

        // ADAPTERS
        mFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile file =  mBaseAuth.files.get(position);
                Log.e(TAG, "file path:" + file.filePath);
                loadFile(file);
            }
        });

        mFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "file onItemDeleteClick()!!!!!:" + position);
                attachFileType = FILE_PREFIX;
                AttachFile file = mBaseAuth.files.get(position);
                if (file.id != -1) {
                    removeFile(file, position);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAuthorizationData();
            }
        });
    }
}
