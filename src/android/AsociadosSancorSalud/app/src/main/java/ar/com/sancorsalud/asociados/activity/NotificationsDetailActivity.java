package ar.com.sancorsalud.asociados.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.NotificationAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SalesmanZoneAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.manager.ZoneController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.affiliation.TitularDoc;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DateUtils;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class NotificationsDetailActivity extends BaseActivity {

    private static final String TAG = "NOTIF_DET_ACT";

    private TextView mTitleTextView;
    private ImageView mPriorityImgView;
    private TextView mPriorityTextView;

    private TextView mOwnerTextView;
    private TextView mDateTextView;
    private TextView mText;
    private TextView mLink;

    // Attach files
    private RecyclerView mAttachFilesRecyclerView;
    private AttachFilesAdapter mAttachFilesAdapter;
    private List<AttachFile> tmpAttachFiles = new ArrayList<AttachFile>();

    private RelativeLayout mPriorityBox;
    private EditText mPriorityEditText;
    private SpinnerDropDownAdapter mPrioritiesAdapter;
    private int mSelectedPriority = -1;
    private ArrayList<QuoteOption> mPriorities;

    private RelativeLayout mZoneBox;
    private EditText mZoneEditText;
    private SpinnerDropDownAdapter mZonesAdapter;
    private int mSelectedZone = -1;
    private ArrayList<Zone> mZones;

    private ImageView mAllItemsImg;
    private TextView allTextLabel;
    private boolean allSelected = false;

    protected RecyclerView mRecyclerView;
    private ArrayList<Salesman> mSalesmanList = new ArrayList<Salesman>();
    private SalesmanZoneAdapter mSalesmanAdapter = null;
    private ArrayList<Salesman> mSalesmanSendList = new ArrayList<>();

    private Button mSendButton;
    private Notification mNotification;
    private boolean isZoneLeaderRole = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.activity_notification);

        mMainContainer = (RelativeLayout) findViewById(R.id.main);
        mTitleTextView = (TextView) findViewById(R.id.title_txt);

        mPriorityImgView = (ImageView) findViewById(R.id.priority_img);
        mPriorityTextView = (TextView) findViewById(R.id.priority_txt);

        mOwnerTextView = (TextView) findViewById(R.id.owner_txt);
        mDateTextView = (TextView) findViewById(R.id.date_txt);

        mText = (TextView) findViewById(R.id.text_txt);
        mLink = (TextView) findViewById(R.id.link_txt);

        mZoneBox = (RelativeLayout) findViewById(R.id.zones_box);
        mZoneEditText = (EditText) findViewById(R.id.zone_input);

        mPriorityBox = (RelativeLayout) findViewById(R.id.priority_box);
        mPriorityEditText = (EditText) findViewById(R.id.priority_input);

        mSendButton = (Button) findViewById(R.id.send_button);

        mAllItemsImg = (ImageView) findViewById(R.id.all_items);
        allTextLabel = (TextView) findViewById(R.id.all_txt);

        mSalesmanAdapter = new SalesmanZoneAdapter(mSalesmanList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager salesmanLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(salesmanLayoutManager);
        mRecyclerView.setAdapter(mSalesmanAdapter);
        mRecyclerView.setHasFixedSize(true);

        if (getIntent().getExtras() != null) {

            mNotification = (Notification) getIntent().getSerializableExtra(ConstantsUtil.ARG);
            Log.e(TAG, "NOTIFICATION NOTIF ID: " + mNotification.notificationId);

            isZoneLeaderRole = getIntent().getBooleanExtra(ConstantsUtil.IS_ZONE_LEADER_ROLE, false);
            Log.e(TAG, "isZoneLeaderRole: " + isZoneLeaderRole);

            final User user = UserController.getInstance().getSignedUser();
            attachFilesSubDir = user.id + "/notif/docs";
            mImageProvider = new ImageProvider(this);

            setupListeners();

            if (isZoneLeaderRole) {
                showPriorityViews();
                printPriorityData();
            } else {
                hidePriorityViews();
            }

            mTitleTextView.setText(mNotification.title);
            mOwnerTextView.setText(mNotification.owner);

            String day = ParserUtils.parseDate(mNotification.date, "dd") + " " + DateUtils.shortMonthText(mNotification.date);
            String hour = ParserUtils.parseDate(mNotification.date, "hh:mm");
            mDateTextView.setText(day + " " + hour);

            mText.setText(Html.fromHtml(mNotification.description));

            // algunos link vienen con el string 'Directorio de PDF inexistente', vaya a saber uno porque
            if (mNotification.link != null && !mNotification.link.isEmpty() && !mNotification.link.equals("Directorio de PDF inexistente")) {
                mLink.setVisibility(View.VISIBLE);
            } else {
                mLink.setVisibility(View.GONE);
            }

            fillAllPhysicalFiles();
        }
    }


    private void fillAllPhysicalFiles() {
        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fill All Notification Attachs Files ....");
                fillAttachFiles();
            }
        }).start();
    }

    private void fillAttachFiles() {

        if (!mNotification.files.isEmpty()) {

            Log.e(TAG, "fillAttachiles !!....");
            final AttachFile attachFile = mNotification.files.remove(0);

            Log.e(TAG, "attachFile.id: " + attachFile.id);

            if (attachFile.fileNameAndExtension == null || attachFile.fileNameAndExtension.isEmpty()) {
                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir, attachFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        Log.e(TAG, "ok getting DES File !!!!");

                        if (resultFile != null) {

                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpAttachFiles.add(resultFile);
                            fillAttachFiles();

                        } else {
                            Log.e(TAG, "Null DES file ....");
                            tmpAttachFiles.add(attachFile);
                            fillAttachFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpAttachFiles.add(attachFile);
                        fillAttachFiles();
                    }
                });
            } else {
                tmpAttachFiles.add(attachFile);
                fillAttachFiles();
            }
        } else {

            // recreate desFiles
            mNotification.files.addAll(tmpAttachFiles);
            tmpAttachFiles = new ArrayList<AttachFile>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initAttachFiles();
                    checkUserRole();
                }
            });
        }
    }

    private void initAttachFiles() {

        Log.e(TAG, "initAttachFiles ...");
        for (AttachFile f : mNotification.files) {
            Log.e(TAG, "File: " + f.id + " :: " + f.fileNameAndExtension);
        }

        mAttachFilesAdapter = new AttachFilesAdapter(mNotification.files, false);

        mAttachFilesRecyclerView = (RecyclerView) findViewById(R.id.attach_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mAttachFilesRecyclerView.getContext());
        mAttachFilesRecyclerView.setLayoutManager(attachLayoutManager);
        mAttachFilesRecyclerView.setAdapter(mAttachFilesAdapter);
        mAttachFilesRecyclerView.setHasFixedSize(true);

        mAttachFilesAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile attachFile = mNotification.files.get(position);
                Log.e(TAG, "attachFile path:" + attachFile.filePath);
                loadFile(attachFile);
            }
        });
    }

    private void checkUserRole() {
        if (isZoneLeaderRole) {

            mPriorities = QuoteOptionsController.getInstance().getPriorities();
            fillPrioritiesAdapter();

            ZoneController.getInstance().getItems(new Response.Listener<ArrayList<Zone>>() {
                @Override
                public void onResponse(ArrayList<Zone> response) {
                    mZones = response;
                    fillZoneAdapter();
                    NotificationController.getInstance().markAsRead(mNotification);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mZones = null;
                    NotificationController.getInstance().markAsRead(mNotification);
                }
            });
        } else {
            NotificationController.getInstance().markAsRead(mNotification);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu --------------------");

        if (isZoneLeaderRole) {

            getMenuInflater().inflate(R.menu.notif_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "onOptionsItemSelected --------------------");

        int id = item.getItemId();
        switch (id) {
            case R.id.action_remove: {
                Log.e(TAG, "option remove");
                removeNotification();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void printPriorityData() {

        if (mNotification.priority == Notification.Priority.HIGHT) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            mPriorityImgView.setColorFilter(porterDuffColorFilter);
            mPriorityTextView.setText(getResources().getString(R.string.notification_hight_priority));

        } else if (mNotification.priority == Notification.Priority.MEDIUM) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            mPriorityImgView.setColorFilter(porterDuffColorFilter);
            mPriorityTextView.setText(getResources().getString(R.string.notification_medium_priority));

        } else if (mNotification.priority == Notification.Priority.LOW) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
            mPriorityImgView.setColorFilter(porterDuffColorFilter);
            mPriorityTextView.setText(getResources().getString(R.string.notification_low_priority));
        }
    }

    private void showPriorityViews() {
        hideAllTextData();

        mPriorityImgView.setVisibility(View.VISIBLE);
        mPriorityTextView.setVisibility(View.VISIBLE);
        mZoneBox.setVisibility(View.VISIBLE);
        mPriorityBox.setVisibility(View.VISIBLE);
        mSendButton.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hidePriorityViews() {
        hideAllTextData();

        mPriorityImgView.setVisibility(View.GONE);
        mPriorityTextView.setVisibility(View.GONE);
        mZoneBox.setVisibility(View.GONE);
        mPriorityBox.setVisibility(View.GONE);
        mSendButton.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void setupListeners() {

        mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Antes se usaba un webView, pero ahora el pdf a visualizar es un servicio
                //IntentHelper.goToWebDetailActivity(NotificationsDetailActivity.this, mNotification.link);
                String url = RestConstants.HOST + RestConstants.POST_GET_FILE + "?id=" + mNotification.link + "&token=" + HRequest.authorizationHeaderValue;
                Log.d(TAG, url + " ----------");
                openViewOnBrowser(url);

            }
        });

        if (isZoneLeaderRole) {
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

            View zoneButton = mMainContainer.findViewById(R.id.zone_button);
            zoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showZonesAlert();
                }
            });

            zoneButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedZone = -1;
                    mZoneEditText.setText("");
                    return true;
                }
            });


            mAllItemsImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allSelected = !allSelected;

                    if (allSelected) {
                        mAllItemsImg.setImageResource(R.drawable.ic_check);
                        mSalesmanAdapter.onSelectAll();

                    } else {
                        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
                        mSalesmanAdapter.onUnSelectAll();
                    }
                }
            });

            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateForm()) {
                        Log.e(TAG, "validateForm ok");
                        onSelectToSendNotification();
                    } else {
                        Log.e(TAG, "validateForm error");
                    }
                }
            });

        }
    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private boolean validateNotEmptySalesman() {
        if (mSalesmanAdapter.getSelectedItems().size() == 0) {
            SnackBarHelper.makeError(mMainContainer, R.string.notification_empty_salesman_selection).show();
            return false;
        }
        return true;
    }

    public boolean validateForm() {
        boolean isValid = true;

        isValid = isValid & validateField(mZoneEditText, R.string.notification_zone_error, R.id.zones_wrapper);
        isValid = isValid & validateField(mPriorityEditText, R.string.notification_priority_error, R.id.priority_wrapper);
        isValid = isValid & validateNotEmptySalesman();

        return isValid;
    }

    private void removeNotification() {

        showMessageWithOption(getResources().getString(R.string.notification_remove_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // NO option
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // YES option
                dialog.dismiss();

                NotificationController.getInstance().removeNotification(mNotification.notificationId, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        Log.e(TAG, "Remove Notification  ok");
                        SnackBarHelper.makeSucessful(mMainContainer, R.string.notification_success_removed).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent();
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            }
                        }, 1500L);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Remove Notification  error");
                        DialogHelper.showMessage(NotificationsDetailActivity.this, getResources().getString(R.string.notification_remove_error), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    }
                });

            }
        });
    }

    private void onSelectToSendNotification() {

        HashMap<Long, Boolean> itemsSelected = mSalesmanAdapter.getSelectedItems();
        if (itemsSelected.size() != 0) {

            mSalesmanSendList = new ArrayList<>();
            for (Salesman salesman : mSalesmanList) {
                Boolean selected = itemsSelected.get(salesman.id);
                if (selected != null && selected == true) {
                    mSalesmanSendList.add(salesman);
                }
            }
            if (!mSalesmanSendList.isEmpty()) {
                showProgressDialog(R.string.notifications_sending_data);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "send notification to salesmans selected ....");
                        sendNotificationList();
                    }
                }).start();
            }
        }
    }

    private void sendNotificationList() {
        Log.e(TAG, "sendNotificationList ...");

        if (!mSalesmanSendList.isEmpty()) {

            final Salesman salesman = mSalesmanSendList.remove(0);
            if (salesman != null) {

                Log.e(TAG, "Mssg id : " + mNotification.notificationId);
                Log.e(TAG, "Sending to: " + salesman.id);

                final Zone zone = mZones.get(mSelectedZone);
                Log.e(TAG, "Zone : " + zone.id);

                NotificationController.getInstance().sendNotification(mNotification.notificationId, zone.id, salesman.id, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        Log.e(TAG, "ok send Notification to: " + salesman.id);
                        sendNotificationList();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error send Notification to: " + salesman.id);
                        sendNotificationList();
                    }
                });
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    SnackBarHelper.makeSucessful(mMainContainer, R.string.notification_success_sended).show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500L);
                }
            });
        }
    }


    private void fillZoneAdapter() {
        ArrayList<String> dataStr = new ArrayList<String>();
        for (Zone zone : mZones) {
            dataStr.add(zone.name);
        }
        mZonesAdapter = new SpinnerDropDownAdapter(this, dataStr, mSelectedZone);
    }

    private void showZonesAlert() {

        mZonesAdapter.setSelectedIndex(mSelectedZone);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NotificationsDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.seleccione_zona))
                .setAdapter(mZonesAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedZone = i;
                        final Zone zone = mZones.get(mSelectedZone);
                        mZoneEditText.setText(zone.name);
                        NotificationsDetailActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mZonesAdapter.notifyDataSetChanged();
                            }
                        });

                        fillSalesmanByZone(zone);
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
        mPrioritiesAdapter = new SpinnerDropDownAdapter(this, dataStr, mSelectedPriority);
    }

    private void showPrioritiesAlert() {

        mPrioritiesAdapter.setSelectedIndex(mSelectedPriority);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NotificationsDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.seleccione_prioridad))
                .setAdapter(mPrioritiesAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedPriority = i;
                        QuoteOption opt = mPriorities.get(mSelectedPriority);
                        mPriorityEditText.setText(opt.optionName());
                        NotificationsDetailActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mPrioritiesAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void fillSalesmanByZone(Zone zone) {

        Log.e(TAG, "fillSalesmanByZone" + zone.id);
        SalesmanController.getInstance().getSalesmanListByZone(zone.id, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> salesmaList) {

                if (salesmaList != null && !salesmaList.isEmpty()) {
                    mSalesmanAdapter.onRefresh(salesmaList);
                    showAllTextData();
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Error getting salesman list for zone");
                    mSalesmanAdapter.onRefresh(new ArrayList<Salesman>());
                    hideAllTextData();
                    mSendButton.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                Log.e(TAG, "Error getting salesman list for zone");
                hideAllTextData();
                mSendButton.setVisibility(View.GONE);
            }
        });
    }

    private void hideAllTextData() {
        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
        mAllItemsImg.setVisibility(View.GONE);
        allTextLabel.setVisibility(View.GONE);
    }

    private void showAllTextData() {
        mAllItemsImg.setVisibility(View.VISIBLE);
        allTextLabel.setVisibility(View.VISIBLE);
    }
}
