package ar.com.sancorsalud.asociados.fragments.notifications;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.NotificationsDetailActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.manager.ZoneController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AddNotificationFragment extends BaseFragment {

    private static final String TAG = "ADD_NOTIF_FRG";
    private static final String NOTIF_FILE_PREFIX = "notif";

    private EditText mTitleEditText;
    private EditText mTextEditText;
    private EditText mZoneEditText;

    private EditText mPromotersEditText;
    private SpinnerDropDownAdapter mPromotersAdapter;
    private int mSelectedPromoter = -1;
    private ArrayList<QuoteOption> mPromoters;

    private EditText mPriorityEditText;
    private SpinnerDropDownAdapter mPrioritiesAdapter;
    private int mSelectedPriority = -1;
    private ArrayList<QuoteOption> mPriorities;

    private Button mAddButton;
    private ScrollView mScrollView;
    private View view;

    // Files
    private RecyclerView mFilesRecyclerView;
    private AttachFilesAdapter mFileAdapter;
    private Button addFileButton;
    private List<AttachFile> tmpAttachFiles = new ArrayList<AttachFile>();

    private Notification mNotification;
    private Zone mZone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_add_notification, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final User user = UserController.getInstance().getSignedUser();

        attachFilesSubDir = user.id + "/notif/docs";
        mImageProvider = new ImageProvider(getActivity());
        mScrollView = (ScrollView) view.findViewById(R.id.scroll);

        mTitleEditText = (EditText) view.findViewById(R.id.title_input);
        mTextEditText = (EditText) view.findViewById(R.id.text_input);

        mZoneEditText = (EditText) view.findViewById(R.id.zone_input);
        mPromotersEditText = (EditText) view.findViewById(R.id.promoters_input);
        mPriorityEditText = (EditText) view.findViewById(R.id.priority_input);

        mNotification = new Notification();

        // ATTACH FILES
        mFileAdapter = new AttachFilesAdapter(mNotification.files, true);
        mFilesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFilesRecyclerView.getContext());
        mFilesRecyclerView.setLayoutManager(attachLayoutManager);
        mFilesRecyclerView.setAdapter(mFileAdapter);
        mFilesRecyclerView.setHasFixedSize(true);

        addFileButton = (Button) view.findViewById(R.id.file_button);
        mAddButton = (Button) view.findViewById(R.id.add_button);
        this.view = view;

        mPromoters = QuoteOptionsController.getInstance().getPromoters();
        fillPromotersAdapter();

        mPriorities = QuoteOptionsController.getInstance().getPriorities();
        fillPrioritiesAdapter();

        ZoneController.getInstance().getItems(new Response.Listener<ArrayList<Zone>>() {
            @Override
            public void onResponse(ArrayList<Zone> zoneList) {
                mZone = findUserZone(zoneList, user);
                mZoneEditText.setText(mZone.name);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        setupListeners(user);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume -------");
        super.onResume();

       if (Storage.getInstance().hasSendNotification()) {
           // reset form

           mTitleEditText.setText("");
           mTextEditText.setText("");

           mSelectedPromoter = -1;
           mPromotersEditText.setText("");

           mSelectedPriority = -1;
           mPriorityEditText.setText("");

           mFileAdapter.removeAllItems();

           // reset flag
           Storage.getInstance().setHasSendNotification(false);
       }
    }

    @Override
    public void updateFileList(AttachFile attachFile) {
        if (attachFileType.equals(NOTIF_FILE_PREFIX)) {
            mFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position) {
        try {
            if (attachFileType.equals(NOTIF_FILE_PREFIX)) {
                mFileAdapter.removeItem(position);
            }
        } catch (Throwable e) {
        }
    }


    private void setupListeners(final User user) {
        setupImageProvider();

        View promoterButton = mMainContainer.findViewById(R.id.promoter_button);
        promoterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showPromotersAlert();
            }
        });
        promoterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedPromoter = -1;
                mPromotersEditText.setText("");
                return true;
            }
        });


        View priorityButton = mMainContainer.findViewById(R.id.priority_button);
        priorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showPrioritiesAlert();
            }
        });
        priorityButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedPriority = -1;
                mPriorityEditText.setText("");
                return true;
            }
        });


        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFileType = NOTIF_FILE_PREFIX;
                mImageProvider.showImagePicker(attachFilesSubDir + "/" + NOTIF_FILE_PREFIX, NOTIF_FILE_PREFIX + FileHelper.getFilePrefix());
            }
        });

        // ADAPTERS
        mFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "file onItemDeleteClick()!!!!!:" + position);
                attachFileType = NOTIF_FILE_PREFIX;
                AttachFile file = mNotification.files.get(position);
                if (file.id != -1) {
                    removeFile(file, position);
                }
            }
        });

        mFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile file = mNotification.files.get(position);
                Log.e(TAG, "file path:" + file.filePath);
                loadFile(file);
            }
        });


        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()){

                    showProgressDialog(R.string.notification_add_loading);

                    mNotification.owner = new Long(user.id).toString();
                    mNotification.title =  mTitleEditText.getText().toString().trim();
                    mNotification.description =  mTextEditText.getText().toString().trim();
                    mNotification.date = new Date();
                    mNotification.priority =  Notification.getPriority(mPriorities.get(mSelectedPriority).title);

                    mNotification.files = mFileAdapter.getAttachFiles();

                    NotificationController.getInstance().addNotification(mNotification, new Response.Listener<Long>() {
                        @Override
                        public void onResponse(Long notifId) {
                            dismissProgressDialog();
                            Log.e(TAG , "ok adding Notification ---------- " + notifId);
                            mNotification.id = notifId;
                            toPromotersSelectionDetail(mNotification.id, mZone);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();
                            Log.e(TAG , "Error adding Notification ----------");
                            SnackBarHelper.makeError(mScrollView, R.string.error_notification_add).show();
                        }
                    });
                }
            }
        });
    }


    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private boolean validateForm() {
        //return true;
        boolean isValid = true;
        isValid = isValid & validateField(mTitleEditText, R.string.notification_title_error, R.id.title_wrapper);
        isValid = isValid & validateField(mTextEditText, R.string.notification_text_error, R.id.text_wrapper);
        isValid = isValid & validateField(mPromotersEditText, R.string.notification_title_error, R.id.promoters_wrapper);
        isValid = isValid & validateField(mPriorityEditText, R.string.notification_priority_error, R.id.priority_wrapper);
        return isValid;
    }

    private void fillPromotersAdapter() {
        ArrayList<String> dataStr = new ArrayList<String>();
        for (QuoteOption q : mPromoters) {
            dataStr.add(q.optionName());
        }
        mPromotersAdapter = new SpinnerDropDownAdapter(getActivity(), dataStr, mSelectedPromoter);
    }

    private void showPromotersAlert() {

        mPromotersAdapter.setSelectedIndex(mSelectedPromoter);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccione_tipo_promotor))
                .setAdapter(mPromotersAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedPromoter = i;
                        QuoteOption opt = mPromoters.get(mSelectedPromoter);
                        mPromotersEditText.setText(opt.optionName());
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mPromotersAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }



    private void fillPrioritiesAdapter() {
        ArrayList<String> dataStr = new ArrayList<String>();
        for (QuoteOption q : mPriorities) {
            dataStr.add(q.optionName());
        }
        mPrioritiesAdapter = new SpinnerDropDownAdapter(getActivity(), dataStr, mSelectedPriority);
    }

    private void showPrioritiesAlert() {

        mPrioritiesAdapter.setSelectedIndex(mSelectedPriority);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccione_prioridad))
                .setAdapter(mPrioritiesAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedPriority = i;
                        QuoteOption opt = mPriorities.get(mSelectedPriority);
                        mPriorityEditText.setText(opt.optionName());
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mPrioritiesAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private Zone findUserZone(ArrayList<Zone> zoneList , User user) {
        Zone userZone = null;

        ArrayList<String> dataStr = new ArrayList<String>();
        for (Zone zone : zoneList) {
            if (zone.id == user.zones.get(0)) {
                userZone = zone;
                break;
            }
        }
        return userZone;
    }

    private void toPromotersSelectionDetail(long notificationId, Zone zone){
        IntentHelper.goToNotificationPromoterSelectionActivity(getActivity(),notificationId, zone);
    }


}
