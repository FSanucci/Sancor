package ar.com.sancorsalud.asociados.activity.affiliation.workflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.WorkflowAuthorization;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

/**
 * Created by francisco on 5/12/17.
 */

public class WorkflowCobranzaForPAActivity extends BaseActivity {

    private static final String TAG = "WORKFLW_PA_ACT";

    private EditText mAuthCobranzaTxt;
    private Button mAuthCobranzaButton;

    private boolean addCobranzaMode = true;
    private AuthorizationCobranza authCobranza;
    private boolean hasToShowCobranzaButton = false;

    private ProspectiveClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow_cobranza);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_cobranzas);

        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
            Log.e (TAG, "client DNI: " + mClient.dni);

            mMainContainer = findViewById(R.id.main_content);
            mAuthCobranzaTxt = (EditText) findViewById(R.id.auth_cobranza_input);
            mAuthCobranzaButton = (Button) findViewById(R.id.auth_cobranza_button);

            setupListeners();
            verifyAuthorizationCobranza();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult **********************************");

        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_AUTH_COBRZ) {
            mAuthCobranzaButton.setVisibility(View.GONE);
            verifyAuthorizationCobranza();
        }
    }


    private void setupListeners() {

        mAuthCobranzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mAuthCobranzanButton on click");
                if (addCobranzaMode) {
                    IntentHelper.goToAuthorizationCobranzaAddActivity(WorkflowCobranzaForPAActivity.this, authCobranza);
                } else {
                    IntentHelper.goToAuthorizationCobranzaUpdateActivity(WorkflowCobranzaForPAActivity.this, mClient.id);
                }
            }
        });
    }

    private void verifyAuthorizationCobranza() {

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.verif_auth_cobranza_loading);
            Log.e(TAG, "verifyAuthorizationCobranza  ....--------------------------------");

            WorkflowController.getInstance().verifiyAuthorizationCobranzaRequest(mClient.id, false, new Response.Listener<WorkflowAuthorization>() {
                @Override
                public void onResponse(WorkflowAuthorization response) {
                    dismissProgressDialog();

                    if (response != null) {

                        authCobranza = new AuthorizationCobranza();
                        authCobranza.paId = mClient.id;

                        if (response.control != null) {

                            if (response.control) {
                                mAuthCobranzaTxt.setText((response.mssg != null && !response.mssg.isEmpty()) ? response.mssg : "");

                                switch (response.state) {
                                    case ConstantsUtil.COBRANZA_VERIFICATION_STATE:
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_APROVE_STATE:
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_REJECTED_STATE:
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_BACK_TO_COMERCIAL_STATE:
                                        mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_requires_auth));

                                        hasToShowCobranzaButton = true;
                                        addCobranzaMode = false;
                                        break;

                                    default:
                                        hasToShowCobranzaButton = true;
                                        addCobranzaMode = true;

                                        Boolean authorization = response.authorization;
                                        if (authorization != null && authorization) {
                                            mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_requires_auth));
                                        }
                                        break;
                                }

                                renderCobranzaButton(hasToShowCobranzaButton);

                            } else {
                                // NO AUTH_ REQUIRED
                                Log.e (TAG, "No requeire autorizacion ................");
                                hasToShowCobranzaButton = false;
                                mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_no_requires_auth));
                                renderCobranzaButton(hasToShowCobranzaButton);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent();
                                        i.putExtra(ConstantsUtil.RESULT_AUTH_COBRZ, true);
                                        setResult(Activity.RESULT_OK, i);
                                        finish();
                                    }
                                }, 1000L);

                            }

                        } else {
                            SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                            renderCobranzaButton(hasToShowCobranzaButton);
                        }

                    } else {
                        SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                        renderCobranzaButton(hasToShowCobranzaButton);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    renderCobranzaButton(hasToShowCobranzaButton);

                    SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                    //DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_verif_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(WorkflowCobranzaForPAActivity.this, null);
        }
    }

    private void renderCobranzaButton(boolean hasToShowCobranzaButton) {
        if (hasToShowCobranzaButton) {
            mAuthCobranzaButton.setVisibility(View.VISIBLE);
        } else {
            mAuthCobranzaButton.setVisibility(View.GONE);
        }
    }

}
