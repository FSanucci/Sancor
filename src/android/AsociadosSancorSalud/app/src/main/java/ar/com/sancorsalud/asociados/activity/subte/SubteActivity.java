package ar.com.sancorsalud.asociados.activity.subte;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.model.Station;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;

/**
 * Created by sergiocirasa on 11/10/17.
 */

public abstract class SubteActivity extends BaseActivity {

    public static final String PA = "pa_id";

    protected HashMap<String, Station> stations;
    protected HashMap<String, String> stationsName;
    protected HashMap<String, View> stationsView;

    protected ProgressBar mProgressView;
    protected View mMainContainer;
    protected long mProspectiveClientId;

    public abstract HRequest createService(Response.Listener<HashMap<String, Station>> listener, Response.ErrorListener errorListener);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            mProspectiveClientId = getIntent().getLongExtra(PA,-1);
        }
    }

    protected void getContent() {

        showProgress(true);
        HRequest request = createService(new Response.Listener<HashMap<String, Station>>() {
            @Override
            public void onResponse(HashMap<String, Station> responseData) {
                stations = responseData;
                reloadView();
                showProgress(false);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(SubteActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }

        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    private void reloadView() {
        for (String key : stations.keySet()) {
            Station station = stations.get(key);
            if (station.state) {
                View v = stationsView.get(key);
                v.setBackground(getDrawable(R.drawable.station_dot));
                startAnimation(v);
            }
        }
    }

    private void startAnimation(View v) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(500);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    public void showPopup(View v) {

        Station s = stations.get(v.getTag());
        if (s != null && s.state) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setTitle(stationsName.get(v.getTag()));

            View content = inflater.inflate(R.layout.dialog_station, null);
            TextView dateTextView = (TextView) content.findViewById(R.id.date);
            TextView usernameTextView = (TextView) content.findViewById(R.id.name);
            TextView commentTextView = (TextView) content.findViewById(R.id.comment);

            if (s.date != null)
                dateTextView.setText(buildSection(R.string.station_date, " " + ParserUtils.parseDate(s.date, "dd/MM/yyyy")));

            usernameTextView.setText(buildSection(R.string.station_username, " " + s.username));
            commentTextView.setText(buildSection(R.string.station_comment, " " + s.largeComment));

            builder.setView(content)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private SpannableStringBuilder buildSection(int strId, String value) {
        SpannableStringBuilder sb = new SpannableStringBuilder(getString(strId) + value);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, 0, getString(strId).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        mMainContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
