package ar.com.sancorsalud.asociados.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.notifications.AddNotificationFragment;
import ar.com.sancorsalud.asociados.fragments.notifications.NotificationsZoneLeaderFragment;
import ar.com.sancorsalud.asociados.fragments.zoneleader.ManualQuoteListFragment;
import ar.com.sancorsalud.asociados.fragments.zoneleader.PASelectionFragment;
import ar.com.sancorsalud.asociados.fragments.zoneleader.SalesmanListFragment;
import ar.com.sancorsalud.asociados.fragments.zoneleader.ZoneleaderIndicatorsFragment;
import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

public class ZoneLeaderMainActivity extends MainActivity {

    private TextView mPendingClientsBadgeTextView;
    private TextView mProcessedClientsBadgeTextView;

    private BroadcastReceiver badgeAssigmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgressDialog();

            String action = intent.getAction();
            Log.e(TAG, "Broadcast action: " + action);

            if (action.equals(ConstantsUtil.REFRESH_ZONE_LEADER_ASSIGMENT_BADGE)) {
                Counter counter = (Counter)intent.getExtras().getSerializable(ConstantsUtil.BADGE_COUNT);

                if(counter.pendingAssignments > 0){
                    mPendingClientsBadgeTextView.setVisibility(View.VISIBLE);
                    mPendingClientsBadgeTextView.setText(""+counter.pendingAssignments);

                }else{
                    mPendingClientsBadgeTextView.setVisibility(View.GONE);
                }

                if(counter.totalAssignments > 0){
                    mProcessedClientsBadgeTextView.setVisibility(View.VISIBLE);
                    mProcessedClientsBadgeTextView.setText(""+counter.totalAssignments);

                }else{
                    mProcessedClientsBadgeTextView.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "Zone Leader onCreate !!!!!!!!!!!");

        AppController.getInstance().updateZoneLeaderAssigmentData();

        if (!Storage.getInstance().hasStartedZoneLeaderBadgeRequestCycle()) {
            Storage.getInstance().setHasStartedZoneLeaderBadgeRequestCycle(true);
            AppController.getInstance().updateZoneLeadeBadgesCycle();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume !!!!!!!!!!!");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsUtil.REFRESH_ZONE_LEADER_ASSIGMENT_BADGE);
        registerReceiver(badgeAssigmentReceiver, filter);


        AppController.getInstance().updateZoneLeaderAssigmentData();
    }


    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(badgeAssigmentReceiver);
        }catch ( Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    protected void initFragment() {
        gotoAssignmentListFragment();
    }

    @Override
    protected int menuContentViewResourceId(){
        return R.layout.nav_menu_zone_leader;
    }

    @Override
    protected void setupMenu() {
        super.setupMenu();

        mPendingClientsBadgeTextView = (TextView) mSideMenuContentView.findViewById(R.id.badge_pending_prospective_client_txt);
        mProcessedClientsBadgeTextView = (TextView) mSideMenuContentView.findViewById(R.id.badge_processed_prospective_client_txt);

        View menuAddProspectiveClient = mSideMenuContentView.findViewById(R.id.menu_item_add_prospective_client);
        menuAddProspectiveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.goToAddPAActivity(ZoneLeaderMainActivity.this);
            }
        });

        View menuLogut = mSideMenuContentView.findViewById(R.id.menu_item_logout);
        menuLogut.setOnClickListener(createLogoutListener());


        View menuProspectiveClients = mSideMenuContentView.findViewById(R.id.menu_item_prospective_client);
        menuProspectiveClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAssignmentListFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        View menuSalesman = mSideMenuContentView.findViewById(R.id.menu_item_salesman);
        menuSalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPromoterListFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        View menuManualQuote = mSideMenuContentView.findViewById(R.id.menu_item_manual_quote);
        menuManualQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoManualQuoteListFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        View menuIndicator = mSideMenuContentView.findViewById(R.id.menu_item_indicator);
        menuIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIndicatorsFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        View menuAddNotification = mSideMenuContentView.findViewById(R.id.menu_item_add_notification);
        menuAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddNotificationFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void gotoNotificationFragment() {
        replaceFragment(NotificationsZoneLeaderFragment.class,R.string.menu_notifications);
    }

    private void gotoAddNotificationFragment() {
        replaceFragment(AddNotificationFragment.class,R.string.menu_add_notification);
    }

    protected void gotoAssignmentListFragment() {
        replaceFragment(PASelectionFragment.class,R.string.menu_prospective_client);
    }

    protected void gotoPromoterListFragment() {
        replaceFragment(SalesmanListFragment.class,R.string.menu_salesman);
    }

    protected void gotoManualQuoteListFragment() {
        replaceFragment(ManualQuoteListFragment.class,R.string.menu_manual_quote);
    }

    private void gotoIndicatorsFragment() {
        replaceFragment(ZoneleaderIndicatorsFragment.class,R.string.menu_indicator);
    }



}
