package ar.com.sancorsalud.asociados.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.notifications.NotificationsSalesmanFragment;
import ar.com.sancorsalud.asociados.fragments.salesman.MyAppointmentsFragment;
import ar.com.sancorsalud.asociados.fragments.salesman.MyAssignedPAFragment;
import ar.com.sancorsalud.asociados.fragments.salesman.SalesmanIndicatorFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

public class SalesmanMainActivity extends MainActivity {

    private TextView mPendingClientsBadgeTextView;
    private ImageView mCardAssigmentBadgeView;

    private MyAssignedPAFragment mdProspectiveClientsFragment;

    private boolean hasRegisterBadgeAssigmentReceiver = false;


    private BroadcastReceiver badgeAssigmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgressDialog();

            String action = intent.getAction();
            Log.e(TAG, "Broadcast action: " + action);

            if (action.equals(ConstantsUtil.REFRESH_SALESMAN_ASSIGMENT_BADGE)) {
                Counter counter = (Counter)intent.getExtras().getSerializable(ConstantsUtil.BADGE_COUNT);

                if (counter.pendingAssignments > 0){
                    mPendingClientsBadgeTextView.setVisibility(View.VISIBLE);
                    mPendingClientsBadgeTextView.setText(""+ counter.pendingAssignments);
                }else{
                    mPendingClientsBadgeTextView.setVisibility(View.GONE);
                }

                if (counter.totalCards > 0){
                    mCardAssigmentBadgeView.setVisibility(View.VISIBLE);
                }else{
                    mCardAssigmentBadgeView.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "SalesMan onCreate !!!!!!!!!!!");

        loadOptionsForClient();


        AppController.getInstance().updateSalesmanAssigmentData();

        if (!Storage.getInstance().hasStartedSalesmanBadgeRequestCycle()) {
            Storage.getInstance().setHasStartedSalesmanBadgeRequestCycle(true);
            AppController.getInstance().updateSalesmanBadgesCycle();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "SalesMan onResume !!!!!!!!!!!");

        if (!hasRegisterBadgeAssigmentReceiver) {
            Log.e(TAG, "Registering  badgeAssigmentReceiver ... ");
            hasRegisterBadgeAssigmentReceiver = true;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConstantsUtil.REFRESH_SALESMAN_ASSIGMENT_BADGE);
            registerReceiver(badgeAssigmentReceiver, filter);

            // only one page is active
            AppController.getInstance().updateSalesmanAssigmentData();
        }
    }

    @Override
    public void onBackPressed() {
        AppController.getInstance().getRestEngine().cancelPendingRequests();
        super.onBackPressed();
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy !!!!!!!!!!!");
        try {
            unregisterReceiver(badgeAssigmentReceiver);
        }catch ( Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }


    protected void initFragment() {
        Log.e(TAG, "initFragment:---------" );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            boolean reloadData =  bundle.getBoolean(ConstantsUtil.RELOAD_DATA, false);
            boolean cardsInProcess =  bundle.getBoolean(ConstantsUtil.CARDS_IN_PROCESS, false);


            if (reloadData){
                Log.e(TAG, "reload Data !!!:--------- ");
                mdProspectiveClientsFragment = MyAssignedPAFragment.newInstance(reloadData, cardsInProcess);
                replaceFragment(mdProspectiveClientsFragment, getResources().getString(R.string.menu_assigned));
            }

        }else {
            Log.e(TAG, "NO clientID:--------- ");
            gotoProspectiveClientListFragment();
        }
    }

    @Override
    protected int menuContentViewResourceId(){
        return R.layout.nav_menu_salesman;
    }

    @Override
    protected void setupMenu() {
        super.setupMenu();

        mPendingClientsBadgeTextView = (TextView) mSideMenuContentView.findViewById(R.id.menu_item_pending_prospective_clients_txt);
        mPendingClientsBadgeTextView.setVisibility(View.GONE);

        mCardAssigmentBadgeView = (ImageView) mSideMenuContentView.findViewById(R.id.menu_item_card_assigments);
        mCardAssigmentBadgeView.setVisibility(View.GONE);

        View menuAddProspectiveClient = mSideMenuContentView.findViewById(R.id.menu_item_add_prospective_client);
        menuAddProspectiveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.goToAddPAActivity(SalesmanMainActivity.this);
            }
        });

        View menuLogut = mSideMenuContentView.findViewById(R.id.menu_item_logout);
        menuLogut.setOnClickListener(createLogoutListener());

        View menuPendingProspectiveClients = mSideMenuContentView.findViewById(R.id.menu_item_prospective_clients);
        menuPendingProspectiveClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPendingClientsBadgeTextView.setVisibility(View.GONE);
                mCardAssigmentBadgeView.setVisibility(View.GONE);

                gotoProspectiveClientListFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        View menuSchedule = mSideMenuContentView.findViewById(R.id.menu_item_schedule);
        menuSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoScheduleFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        View menuAdditional = mSideMenuContentView.findViewById(R.id.menu_item_additional);
        menuAdditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.goToQuoteExistentAdicionalesOptativosActivity(SalesmanMainActivity.this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        View menuTransferecia = mSideMenuContentView.findViewById(R.id.menu_item_transferencia);
        menuTransferecia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.goToTransferenciaSegmentoActivity(SalesmanMainActivity.this);
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
    }

    @Override
    protected void gotoNotificationFragment() {
        replaceFragment(NotificationsSalesmanFragment.class,R.string.menu_notifications);
    }

    private void gotoProspectiveClientListFragment() {
        replaceFragment(MyAssignedPAFragment.class,R.string.menu_assigned);
    }

    private void gotoScheduleFragment() {
        replaceFragment(MyAppointmentsFragment.class,R.string.menu_schedule);
    }

    private void gotoIndicatorsFragment() {
        replaceFragment(SalesmanIndicatorFragment.class,R.string.menu_indicator);
    }

    private void loadOptionsForClient(){
        // Every time a promoter is logged reset and load OS list = f(promoter)
        // reload formas pago
        // make a delay because much process in this screen
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuoteOptionsController.getInstance().resetOSList();

                        QuoteOptionsController.getInstance().loadOSDesreguladas();
                        QuoteOptionsController.getInstance().loadOsCondicionMonotributo();


                        QuoteOptionsController.getInstance().getFormasPago(true);

                    }
                }).start();
            }
        }, 8000L);

    }

}
