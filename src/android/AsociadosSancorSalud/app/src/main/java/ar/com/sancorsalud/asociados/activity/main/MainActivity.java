package ar.com.sancorsalud.asociados.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;

import ar.com.sancorsalud.asociados.BuildConfig;
import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.fragments.zoneleader.PASelectionFragment;
import ar.com.sancorsalud.asociados.interfaces.IFragment;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public abstract class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = "MAIN";

    public static final int PROMOTER_SELECTION_REQUEST = 101;

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected Fragment mCurrentFragment;
    protected LinearLayout mSideMenuContentView;
    protected TextView mNotificationBadgeTextView;

    protected abstract void initFragment();

    protected abstract int menuContentViewResourceId();

    private BroadcastReceiver badgeNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgressDialog();

            String action = intent.getAction();
            Log.e(TAG, "Broadcast action: " + action);

            if (action.equals(ConstantsUtil.REFRESH_NOTIFICATION_BADGE)) {
                int count = intent.getExtras().getInt(ConstantsUtil.BADGE_COUNT);

                if (count > 0) {
                    mNotificationBadgeTextView.setVisibility(View.VISIBLE);
                    mNotificationBadgeTextView.setText("" + count);
                } else {
                    mNotificationBadgeTextView.setVisibility(View.GONE);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        TextView usernameTextField = (TextView) findViewById(R.id.menu_header_txt);
        usernameTextField.setText(UserController.getInstance().getSignedUser().firstname + " " + UserController.getInstance().getSignedUser().lastname);

        TextView versionTextField = (TextView) findViewById(R.id.menu_version_txt);
        versionTextField.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME));


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a mDrawerLayout has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a mDrawerLayout has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the mDrawerLayout toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.openDrawer(GravityCompat.START);

        setupMenu();
        initFragment();
    }



    protected void setupMenu() {
        mSideMenuContentView = (LinearLayout) LayoutInflater.from(this).inflate(menuContentViewResourceId(), null);

        ScrollView container = (ScrollView) findViewById(R.id.scroll_box);
        container.addView(mSideMenuContentView);

        mNotificationBadgeTextView = (TextView) mSideMenuContentView.findViewById(R.id.badge_notification_txt);
        mNotificationBadgeTextView.setVisibility(View.GONE);

        View menuNotification = mSideMenuContentView.findViewById(R.id.menu_item_notification);
        menuNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNotificationFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    protected void updateNotificationBadges() {
        NotificationController.getInstance().updateNotificationsCounter(false, new Response.Listener<Integer>() {
            @Override
            public void onResponse(Integer count) {
                if (count > 0) {
                    mNotificationBadgeTextView.setVisibility(View.VISIBLE);
                    mNotificationBadgeTextView.setText("" + count);
                } else {
                    mNotificationBadgeTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    // --- template method ---------------------- //
    protected void gotoNotificationFragment() {

    }

    @Override
    public void onBackPressed() {
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            finish();
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Base Actyivity onResume .......");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsUtil.REFRESH_NOTIFICATION_BADGE);
        registerReceiver(badgeNotificationReceiver, filter);

        ((IFragment) mCurrentFragment).onActiveFragment();
        updateNotificationBadges();
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(badgeNotificationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PROMOTER_SELECTION_REQUEST) {

            String message = data.getStringExtra(ConstantsUtil.ASSIGN_PROCESS);
            Log.e(TAG, "Result:" + message);

            if (message != null && !message.trim().isEmpty() && mCurrentFragment instanceof PASelectionFragment) {
                ((PASelectionFragment) mCurrentFragment).refreshDataList();
            }
        }

        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    protected View.OnClickListener createLogoutListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "log_out---------------------");

                showMessageWithOption(getResources().getString(R.string.log_out_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // reset flags
                        Storage.getInstance().setHasChangePAQuantityList(false);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        UserController.getInstance().logout();
                        IntentHelper.goToSplashActivity(MainActivity.this);
                    }
                });
            }
        };
    }

    // -- Fragments ------------------------------------------------------------------------------//
    protected void replaceFragment(Fragment fragment, String tag) {
        if (mCurrentFragment != null) {
            ((BaseFragment) mCurrentFragment).onCancellAllRequets();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment, tag);
        ft.commit();
        mCurrentFragment = fragment;
    }

    protected void replaceFragment(Class<?> clazz, int titleId) {
        try {
            if (mCurrentFragment == null || !(clazz.isInstance(mCurrentFragment))) {
                if (mCurrentFragment != null) {
                    ((BaseFragment) mCurrentFragment).onCancellAllRequets();
                }

                Fragment newFragment = (Fragment) clazz.newInstance();
                replaceFragment(newFragment, newFragment.getClass().getSimpleName());
                setActionBarTitle(getResources().getString(titleId));
            }
        } catch (Exception e) {

        }
    }

}
