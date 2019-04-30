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
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.BaseAuthorization;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
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

public class BaseAuthorizationAddActivity extends BaseActivity {

    private static final String TAG = "AUTH_ACT";
    private static final String FILE_PREFIX = "file";
    protected ScrollView mScrollView;

    // ATTACHS  Files
    protected RecyclerView mFilesRecyclerView;
    protected AttachFilesAdapter mFileAdapter;
    protected Button addFileButton;

    protected Button sendButton;
    protected BaseAuthorization mBaseAuth;

    // --- TEMPLATE METHOD ------------------------------------------------ //
    public void addAuthorization(){

    }

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

    public void initializeForm() {
        mFileAdapter = new AttachFilesAdapter(mBaseAuth.files, true);

        mFilesRecyclerView = (RecyclerView) findViewById(R.id.files_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFilesRecyclerView.getContext());
        mFilesRecyclerView.setLayoutManager(attachLayoutManager);
        mFilesRecyclerView.setAdapter(mFileAdapter);
        mFilesRecyclerView.setHasFixedSize(true);

        addFileButton = (Button) findViewById(R.id.file_button);
    }

    protected void setupListeners() {

        setupImageProvider();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAuthorization();
            }
        });


        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFileType = FILE_PREFIX;
                mImageProvider.showImagePicker(attachFilesSubDir + "/" + FILE_PREFIX, FILE_PREFIX + FileHelper.getFilePrefix());
            }
        });

        mFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile file = mBaseAuth.files.get(position);
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
    }
}
