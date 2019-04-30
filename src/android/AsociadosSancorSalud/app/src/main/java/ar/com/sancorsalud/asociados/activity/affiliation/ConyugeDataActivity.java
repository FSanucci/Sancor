package ar.com.sancorsalud.asociados.activity.affiliation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.fragments.affiliation.ConyugeOSFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.ConyugeOSMonotributoFragment;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class ConyugeDataActivity extends BaseActivity {

    private static final String TAG = "CONYU_ACT";

    private ImageView mDopPage1;
    private ImageView mDopPage2;

    private View mNextButton;
    private View mPrevButton;

    private TextView prevText;
    private TextView nextText;

    private int mCurrentPage = 0;
    private int mCantFragments = 0;

    private ConyugeData mConyugeData;
    private BaseFragment mCurrentFragment;
    private View mProgressView;

    private boolean editableCard = true;

    private ObraSocial os = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conyuge_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_conyuge);

        mMainContainer = findViewById(R.id.main);
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);

        prevText = (TextView) findViewById(R.id.prev_title);
        nextText = (TextView) findViewById(R.id.next_title);

        mDopPage1 = (ImageView) findViewById(R.id.page1);
        mDopPage2 = (ImageView) findViewById(R.id.page2);
        mProgressView = findViewById(R.id.progress);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        setupListeners();

        if (getIntent().getExtras() != null) {
            mConyugeData = (ConyugeData) getIntent().getSerializableExtra(ConstantsUtil.CONYUGE_DATA);

            if (mConyugeData != null) {
                if (mConyugeData.obraSocial != null) {
                    mDopPage1.setVisibility(View.VISIBLE);
                    mCantFragments +=1;
                } else {
                    mDopPage1.setVisibility(View.GONE);
                }

                if (mConyugeData.osMonotributo != null) {
                    mCantFragments +=1;
                    mDopPage2.setVisibility(View.VISIBLE);
                } else {
                    mDopPage2.setVisibility(View.GONE);
                }

                Log.e(TAG, "cant frg" + mCantFragments);
                if (mCantFragments == 1){
                    nextText.setText(getResources().getString(R.string.quote_save));
                }

                initializeFragment();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void setActionButtons(boolean val){
        mNextButton.setEnabled(val);
        mPrevButton.setEnabled(val);
    }

    private void initializeFragment() {

        if (mConyugeData.obraSocial != null) {
            mCurrentFragment = ConyugeOSFragment.newInstance(mConyugeData);
            switchToFragment(mCurrentFragment);
        } else if (mConyugeData.osMonotributo != null) {
            mCurrentFragment = ConyugeOSMonotributoFragment.newInstance(mConyugeData);
            switchToFragment(mCurrentFragment);
        }
    }

    private void setupListeners() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 0) { // P1
                    if (mCurrentFragment instanceof ConyugeOSFragment) {
                        mConyugeData.obraSocial = ((ConyugeOSFragment) mCurrentFragment).getConyugeOSData();
                        updateConyugeData(true, false);

                    } else if (mCurrentFragment instanceof ConyugeOSMonotributoFragment) {
                        mConyugeData.osMonotributo  = ((ConyugeOSMonotributoFragment) mCurrentFragment).getConyugeOSMonotributoData();
                        updateConyugeData(null, true);
                    }
                }else{
                    // last screen just save and finish
                    if (mCurrentFragment instanceof ConyugeOSMonotributoFragment) {
                        mConyugeData.osMonotributo = ((ConyugeOSMonotributoFragment) mCurrentFragment).getConyugeOSMonotributoData();
                        updateConyugeData(null, true);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 0) {
                    finish();
                } else if (mCurrentPage == 1) {  // P2

                    if (mCurrentFragment instanceof ConyugeOSMonotributoFragment) {
                        mConyugeData.osMonotributo  = ((ConyugeOSMonotributoFragment) mCurrentFragment).getConyugeOSMonotributoData();
                        updateConyugeData(false, true);
                    }
                }
            }
        });

    }

    private void switchToFragment(BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }


    private void updateConyugeData(final Boolean toNext, final boolean isMonoData) {

        Log.e(TAG, "updateConyugeData !!! ------------------------------------");
        Log.e(TAG, "isMonoData:" + isMonoData);

        setActionButtons(false);

        os = null;
        if (isMonoData){
            os = mConyugeData.osMonotributo;
        }else {
            os = mConyugeData.obraSocial;
        }

        if (editableCard) {
            if (AppController.getInstance().isNetworkAvailable()) {
                showProgress(true);

                CardController.getInstance().updateObraSocialData(os, new Response.Listener<Long>() {
                    @Override
                    public void onResponse(Long osId) {
                        Log.e(TAG, "updating mConyugeOS OS ok  ....");
                        showProgress(false);

                        if (osId != null){
                            os.id = osId;
                        }

                        if (isMonoData) {
                            updateConyugeMonotributoFiles();
                        } else {
                            onMove(toNext);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "updating Conyuge OS data error .....");
                        SnackBarHelper.makeError(mMainContainer, R.string.affiliation_conyuge_os_update_error).show();
                        onMove(toNext);
                    }
                });
            } else {
                Log.e(TAG, "updating Conyuge OS data error .....");
                SnackBarHelper.makeError(mMainContainer, R.string.no_internet_coneccion).show();
                onMove(toNext);
            }
        }else{

            onMove(toNext);
        }
    }


    private void updateConyugeMonotributoFiles(){
        Intent i = new Intent();
        i.putExtra(ConstantsUtil.RESULT_CONYUGE_DATA, mConyugeData);
        i.putExtra(ConstantsUtil.UPDATE_CONYUGE_MONO_FILES, true);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void updateConyugeData(){
        Intent i = new Intent();
        i.putExtra(ConstantsUtil.RESULT_CONYUGE_DATA, mConyugeData);
        setResult(Activity.RESULT_OK, i);
        finish();
    }


    private void onMove(Boolean toNext) {
        showProgress(false);
        setActionButtons(true);

        if (toNext != null && mCantFragments > 1 ) {
            if (toNext) {
                toNextPage();
            } else {
                toBackPage();
            }
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //finish();
                    updateConyugeData();
                }
            }, 1500L);
        }
    }

    private void updateButtonVisivility() {

        if (mCurrentPage == 0) {
            prevText.setText(getResources().getString(R.string.quote_back));
            nextText.setText(getResources().getString(R.string.quote_next));
        } else if (mCurrentPage == 1) {
            prevText.setText(getResources().getString(R.string.quote_prev));
            nextText.setText(getResources().getString(R.string.quote_save));
        }
    }

    private void toNextPage() {
        if (mCurrentPage == 0) { // P1
            highlightDot(mDopPage2);
            showSecondPage();
        }
        updateButtonVisivility();
    }

    private void toBackPage() {
        if (mCurrentPage == 1) {  // P2
            highlightDot(mDopPage1);
            showFirstPage();
        }
        updateButtonVisivility();
    }


    private void highlightDot(ImageView dot) {
        mDopPage1.setColorFilter(ContextCompat.getColor(ConyugeDataActivity.this, R.color.colorDarkGrey));
        mDopPage2.setColorFilter(ContextCompat.getColor(ConyugeDataActivity.this, R.color.colorDarkGrey));
        dot.setColorFilter(ContextCompat.getColor(ConyugeDataActivity.this, R.color.colorAccent));
    }

    private void showFirstPage() {
        if (mCurrentFragment instanceof ConyugeOSFragment) {
            mCurrentFragment = ConyugeOSFragment.newInstance(mConyugeData);

            switchToFragment(mCurrentFragment);
            mCurrentPage = 0;
        }
    }

    private void showSecondPage() {
        if (mCurrentFragment instanceof ConyugeOSMonotributoFragment) {
            mCurrentFragment = ConyugeOSMonotributoFragment.newInstance(mConyugeData);

            switchToFragment(mCurrentFragment);
            mCurrentPage = 1;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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




