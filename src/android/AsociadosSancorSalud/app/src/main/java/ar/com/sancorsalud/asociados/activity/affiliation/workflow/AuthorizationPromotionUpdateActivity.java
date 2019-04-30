package ar.com.sancorsalud.asociados.activity.affiliation.workflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class AuthorizationPromotionUpdateActivity extends BaseAuthorizationUpdateActivity {

    private static final String TAG = "PROM_UPD_ACT";

    private AuthorizationPromotion mAuth;
    private long mPaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, getResources().getString(R.string.promotion_auth_data));

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        mMainContainer = findViewById(R.id.main_content);
        sendButton = (Button) findViewById(R.id.send_button);

        if (getIntent().getExtras() != null) {

            mPaId =  getIntent().getLongExtra(ConstantsUtil.PA_ID, -1L);

            attachFilesSubDir = "/promotion/card/" + mPaId + "/files";
            mImageProvider = new ImageProvider(this);

            getAuthorizationDataList();
        }
    }


    @Override
    public void getAuthorizationDataList() {

        if (AppController.getInstance().isNetworkAvailable()) {
            Log.e(TAG, "getAuthorizationDataList ....--------------------------------");

            WorkflowController.getInstance().getAuthorizationPromotionRequest(mPaId, new Response.Listener<List<AuthorizationPromotion>>() {
                @Override
                public void onResponse(List<AuthorizationPromotion> resultList) {
                    dismissProgressDialog();
                    if (resultList != null) {

                        if (resultList != null && !resultList.isEmpty()) {
                            Log.e(TAG, "TO ok  getting auth data");

                            // update pojo
                            mAuth = resultList.get(0);
                            mAuth.paId = mPaId;

                            mBaseAuth = mAuth;
                            fillAllPhysicalFiles();
                        }

                    } else {
                        DialogHelper.showMessage(AuthorizationPromotionUpdateActivity.this, getResources().getString(R.string.error_get_auth_promotion));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(AuthorizationPromotionUpdateActivity.this, getResources().getString(R.string.error_get_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(AuthorizationPromotionUpdateActivity.this, null);
        }
    }


    @Override
    public void updateAuthorizationData() {

        // FILES
        mAuth.files = mFileAdapter.getAttachFiles();

        if (!mCommentsEditText.getText().toString().trim().isEmpty()) {
            mAuth.comment = mCommentsEditText.getText().toString().trim();
        }

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.auth_promotion_loading);

                Log.e(TAG, "Update AuthorizationPromotion  ....--------------------------------");
                WorkflowController.getInstance().updateAuthorizationPromotionRequest(mAuth, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void result) {
                        dismissProgressDialog();

                        SnackBarHelper.makeSucessful(mScrollView, R.string.success_update_promotion_authorization).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent();
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            }
                        }, 2000L);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        DialogHelper.showMessage(AuthorizationPromotionUpdateActivity.this, getResources().getString(R.string.error_update_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    }
                });


        } else {
            DialogHelper.showNoInternetErrorMessage(AuthorizationPromotionUpdateActivity.this, null);
        }
    }
}
