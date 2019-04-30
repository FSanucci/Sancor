package ar.com.sancorsalud.asociados.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.NotificationAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SalesmanZoneAdapter;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class NotificationSelectionPromoterActivity extends BaseListActivity {

    private static final String TAG = "NOTIF_SELECT_ACT";


    private ImageView mAllItemsImg;
    private TextView allTextLabel;
    private boolean allSelected = false;

    private RecyclerView mRecyclerView;
    private ArrayList<Salesman> mSalesmanList = new ArrayList<Salesman>();
    private SalesmanZoneAdapter mSalesmanAdapter = null;
    private ArrayList<Salesman> mSalesmanSendList = new ArrayList<>();

    private long mNotificationId = -1L;
    private Zone mZone;

    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_selection_promoters);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.notification_add);
        setupLayouts();

        mMainContainer = findViewById(R.id.main_container);

        mAllItemsImg = (ImageView) findViewById(R.id.all_items);
        allTextLabel = (TextView) findViewById(R.id.all_txt);
        mSendButton = (Button) findViewById(R.id.send_button);


        if (getIntent().getExtras() != null) {

            mNotificationId = getIntent().getLongExtra(ConstantsUtil.NOTIFICATION_ID, -1L);
            Log.e(TAG, "NOTIFICATION NOTIF ID: " + mNotificationId);

            mZone = (Zone) getIntent().getSerializableExtra(ConstantsUtil.ARG);
            Log.e(TAG, "Zone: " + mZone.id);

            allTextLabel.setText(getString(R.string.notifications_all_in_zone, mZone.name));

            setupListeners();
            reloadData();
        }
    }

    protected RecyclerView.Adapter getAdapter() {
        if (mSalesmanAdapter == null)
            mSalesmanAdapter = new SalesmanZoneAdapter(mSalesmanList);
        return mSalesmanAdapter;
    }

    @Override
    protected void reloadData() {
        hideAll();
        mSendButton.setVisibility(View.GONE);
        reloadData(true);
    }

    @Override
    protected void onRefreshData() {
        mSendButton.setVisibility(View.GONE);
        onCancellAllRequets();
        showProgress(false);
        reloadData(true);
    }

    protected void reloadData(boolean force) {
        hideAll();
        showProgress(true);
        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
        Log.e(TAG, "fillSalesmanByZone" + mZone.id);

        SalesmanController.getInstance().getSalesmanListByZone(mZone.id, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> salesmaList) {
                showProgress(false);
                onEndedRefresh();

                if (salesmaList.isEmpty()) {
                    showEmptyListLayout();
                    mSalesmanAdapter.onRefresh(new ArrayList<Salesman>());
                    hideAllTextData();
                    mSendButton.setVisibility(View.GONE);
                } else {
                    showDataListLayout();
                    Log.e(TAG, "list count: " + mSalesmanList.size());
                    mSalesmanList = salesmaList;
                    mSalesmanAdapter.onRefresh(salesmaList);
                    showAllTextData();
                    mSendButton.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                showProgress(false);
                onEndedRefresh();
                showErrorListLayout();

                Log.e(TAG, "Error getting salesman list for zone");
                hideAllTextData();
                mSendButton.setVisibility(View.GONE);
                SnackBarHelper.makeError(mMainContainer, R.string.error_notifications_loading_promoters).show();
            }
        });
    }

    @Override
    protected void didClickItem(int position) {
    }

    @Override
    protected void didLongClickItem(int position) {
    }


    private void hideAllTextData(){
        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
        mAllItemsImg.setVisibility(View.GONE);
        allTextLabel.setVisibility(View.GONE);
    }
    private void showAllTextData(){
        mAllItemsImg.setVisibility(View.VISIBLE);
        allTextLabel.setVisibility(View.VISIBLE);
    }

    protected void setupListeners(){
        super.setupListeners();

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
                onSelectToSendNotification();
            }
        });
    }

    private void onSelectToSendNotification(){

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


    private void sendNotificationList(){
        Log.e(TAG, "sendNotificationList ...");

        if (!mSalesmanSendList.isEmpty()) {

            final Salesman salesman = mSalesmanSendList.remove(0);
            if (salesman != null ) {

                Log.e (TAG, "Mssg id : " + mNotificationId);
                Log.e (TAG, "Sending to: " + salesman.id);

                NotificationController.getInstance().sendNotification(mNotificationId, mZone.id, salesman.id, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        Log.e(TAG , "ok send Notification to: "  + salesman.id);
                        sendNotificationList();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG , "Error send Notification to: "  + salesman.id);
                        sendNotificationList();
                    }
                });
            }
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    SnackBarHelper.makeSucessful(mMainContainer, R.string.notification_success_sended).show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IntentHelper.goToNotificationsSendedActivity(NotificationSelectionPromoterActivity.this);
                        }
                    }, 1500L);
                }
            });
        }
    }

}

/*
public class NotificationSelectionPromoterActivity extends BaseActivity {

    private static final String TAG = "NOTIF_SELECT_ACT";

    private Button mSendButton;

    private ImageView mAllItemsImg;
    private TextView allTextLabel;
    private boolean allSelected = false;

    private RecyclerView mRecyclerView;
    private ArrayList<Salesman> mSalesmanList = new ArrayList<Salesman>();
    private SalesmanZoneAdapter mSalesmanAdapter = null;
    private ArrayList<Salesman> mSalesmanSendList = new ArrayList<>();

    private long mNotificationId = -1L;
    private Zone mZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_selection_promoters);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.notification_add);

        mMainContainer = (RelativeLayout) findViewById(R.id.main);

        mAllItemsImg = (ImageView) findViewById(R.id.all_items);
        allTextLabel = (TextView) findViewById(R.id.all_txt);
        mSendButton = (Button) findViewById(R.id.send_button);

        mSalesmanAdapter = new SalesmanZoneAdapter(mSalesmanList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager salesmanLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(salesmanLayoutManager);
        mRecyclerView.setAdapter(mSalesmanAdapter);
        mRecyclerView.setHasFixedSize(true);

        if (getIntent().getExtras() != null) {

            mNotificationId = getIntent().getLongExtra(ConstantsUtil.NOTIFICATION_ID, -1L);
            Log.e(TAG, "NOTIFICATION NOTIF ID: " + mNotificationId);

            mZone = (Zone) getIntent().getSerializableExtra(ConstantsUtil.ARG);
            Log.e(TAG, "Zone: " + mZone.id);

            allTextLabel.setText(getString(R.string.notifications_all_in_zone, mZone.name));

            setupListeners();
            fillSalesmanByZone();
        }

    }


    private void setupListeners() {
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
                onSelectToSendNotification();
            }
        });
    }

    private void fillSalesmanByZone(){

        Log.e (TAG, "fillSalesmanByZone" + mZone.id);

        showProgressDialog(R.string.notifications_loading_promoters);
        SalesmanController.getInstance().getSalesmanListByZone(mZone.id, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> salesmaList) {
                dismissProgressDialog();

                if (salesmaList != null && !salesmaList.isEmpty()){

                    mSalesmanList = salesmaList;
                    Log.e(TAG, "list count: " + mSalesmanList.size());

                    mSalesmanAdapter.onRefresh(salesmaList);
                    showAllTextData();
                    mSendButton.setVisibility(View.VISIBLE);
                }else{
                    Log.e(TAG, "Error getting salesman list for zone");
                    mSalesmanAdapter.onRefresh(new ArrayList<Salesman>());
                    hideAllTextData();
                    mSendButton.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                dismissProgressDialog();

                Log.e(TAG, "Error getting salesman list for zone");
                hideAllTextData();
                mSendButton.setVisibility(View.GONE);
                SnackBarHelper.makeError(mMainContainer, R.string.error_notifications_loading_promoters).show();
            }
        });
    }

    private void hideAllTextData(){
        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
        mAllItemsImg.setVisibility(View.GONE);
        allTextLabel.setVisibility(View.GONE);
    }
    private void showAllTextData(){
        mAllItemsImg.setVisibility(View.VISIBLE);
        allTextLabel.setVisibility(View.VISIBLE);
    }



    private void onSelectToSendNotification(){

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


    private void sendNotificationList(){
        Log.e(TAG, "sendNotificationList ...");

        if (!mSalesmanSendList.isEmpty()) {

            final Salesman salesman = mSalesmanSendList.remove(0);
            if (salesman != null ) {

                Log.e (TAG, "Mssg id : " + mNotificationId);
                Log.e (TAG, "Sending to: " + salesman.id);

                NotificationController.getInstance().sendNotification(mNotificationId, mZone.id, salesman.id, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        Log.e(TAG , "ok send Notification to: "  + salesman.id);
                        sendNotificationList();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG , "Error send Notification to: "  + salesman.id);
                        sendNotificationList();
                    }
                });
            }
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    SnackBarHelper.makeSucessful(mMainContainer, R.string.notification_success_sended).show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IntentHelper.goToNotificationsSendedActivity(NotificationSelectionPromoterActivity.this);
                        }
                    }, 1500L);
                }
            });
        }
    }
}
*/
