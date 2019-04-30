package ar.com.sancorsalud.asociados.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SalesmanZoneAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.manager.ZoneController;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DateUtils;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class NotificationReadDetailActivity extends BaseActivity {

    private static final String TAG = "NOTIF_READ_ACT";

    private TextView mTitleTextView;
    private ImageView mPriorityImgView;
    private TextView mPriorityTextView;

    private TextView mOwnerTextView;
    private TextView mDateTextView;
    private TextView mText;
    private TextView mLink;

    private Notification mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_read_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.activity_notification);

        mMainContainer = findViewById(R.id.main);
        mTitleTextView = (TextView) findViewById(R.id.title_txt);
        mPriorityImgView = (ImageView) findViewById(R.id.priority_img);
        mPriorityTextView = (TextView) findViewById(R.id.priority_txt);

        mOwnerTextView = (TextView) findViewById(R.id.owner_txt);
        mDateTextView = (TextView) findViewById(R.id.date_txt);
        mText = (TextView) findViewById(R.id.text_txt);
        mLink = (TextView) findViewById(R.id.link_txt);

        if (getIntent().getExtras() != null) {

            mNotification = (Notification) getIntent().getSerializableExtra(ConstantsUtil.ARG);
            Log.e(TAG, "NOTIFICATION NOTIF ID: " + mNotification.notificationId);
            printPriorityData();

            mTitleTextView.setText(mNotification.title);
            mOwnerTextView.setText(mNotification.owner);

            String day = ParserUtils.parseDate(mNotification.date, "dd") + " " + DateUtils.shortMonthText(mNotification.date);
            String hour = ParserUtils.parseDate(mNotification.date, "hh:mm");
            mDateTextView.setText(day + " " + hour);

            //mText.setText(mNotification.description);
            mText.setText(Html.fromHtml(mNotification.description));

            // algunos link vienen con el string 'Directorio de PDF inexistente', vaya a saber uno porque
            if (mNotification.link != null && !mNotification.link.isEmpty() && !mNotification.link.equals("Directorio de PDF inexistente")) {
                mLink.setVisibility(View.VISIBLE);
            } else {
                mLink.setVisibility(View.GONE);
            }

            mLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = RestConstants.HOST + RestConstants.POST_GET_FILE + "?id=" + mNotification.link + "&token=" + HRequest.authorizationHeaderValue;
                    Log.d(TAG, url + " ----------");
                    openViewOnBrowser(url);
                }
            });

            NotificationController.getInstance().markAsRead(mNotification);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.e(TAG, "onCreateOptionsMenu!!! --------------------");
        getMenuInflater().inflate(R.menu.notif_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "onOptionsItemSelected!!! --------------------");

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
                        DialogHelper.showMessage(NotificationReadDetailActivity.this, getResources().getString(R.string.notification_remove_error), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    }
                });

            }
        });
    }
}
