package ar.com.sancorsalud.asociados.activity.affiliation.workflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class AuthorizationCobranzaAddActivity extends BaseAuthorizationAddActivity {

    private static final String TAG = "COBRZ_ADD_ACT";

    private EditText mCommentsEditText;
    private AuthorizationCobranza mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, getResources().getString(R.string.cobranza_auth_data));

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        mMainContainer = findViewById(R.id.main_content);
        sendButton = (Button) findViewById(R.id.send_button);

        if (getIntent().getExtras() != null) {

            mAuth = (AuthorizationCobranza) getIntent().getSerializableExtra(ConstantsUtil.AUTH_COBRANZA);
            mBaseAuth = mAuth;

            attachFilesSubDir = "/cobranza/card/" + mAuth.paId + "/files";
            mImageProvider = new ImageProvider(this);

            initializeForm();
            setupListeners();
        }
    }

    @Override
    public void initializeForm() {
        super.initializeForm();
        mCommentsEditText = (EditText) findViewById(R.id.comments_input);
    }

    @Override
    public void addAuthorization() {

        // FILES
        mAuth.files = mFileAdapter.getAttachFiles();

        if (!mCommentsEditText.getText().toString().trim().isEmpty()) {
            mAuth.comment = mCommentsEditText.getText().toString().trim();
        }

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.auth_promotion_loading);

                Log.e(TAG, "add AuthorizationPromotion  ....--------------------------------");
                WorkflowController.getInstance().saveAuthorizationCobranzaRequest(mAuth, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void result) {
                        dismissProgressDialog();

                        SnackBarHelper.makeSucessful(mScrollView, R.string.success_saved_promotion_authorization).show();
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
                        DialogHelper.showMessage(AuthorizationCobranzaAddActivity.this, getResources().getString(R.string.error_save_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    }
                });

        } else {
            DialogHelper.showNoInternetErrorMessage(AuthorizationCobranzaAddActivity.this, null);
        }
    }
}
