package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.affiliation.WorkflowAuthorization;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

/**
 * Created by francisco on 17/11/17.
 */

public class AffiliationWorkflowFragment extends BaseFragment {

    private static final String TAG = "AF_WORKFLW_FRG";
    private static final String ARG_PA_ID = "paId";

    // PROMOTION
    private EditText mAuthPromotionTxt;
    private Button mAuthPromotionButton;
    private boolean hasToShowPromotionButton = false;
    private boolean nextFromPromotion = false;
    private boolean addPromotionMode = true;
    private AuthorizationPromotion authPromotion;

    // COBRANZA
    private EditText mAuthCobranzaTxt;
    private Button mAuthCobranzaButton;
    private boolean hasToShowCobranzaButton = false;

    private boolean nextFromCobranza = false;
    private boolean addCobranzaMode = true;
    private AuthorizationCobranza authCobranza;

    private long mPaId;


    public static AffiliationWorkflowFragment newInstance(long paId) {
        AffiliationWorkflowFragment fragment = new AffiliationWorkflowFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PA_ID, paId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPaId = getArguments().getLong(ARG_PA_ID);
            Log.e(TAG, "PA_ID: " + mPaId + "  ************************");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_workflow, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mAuthPromotionTxt = (EditText) view.findViewById(R.id.auth_promotion_input);
        mAuthPromotionButton = (Button) view.findViewById(R.id.auth_promotion_button);

        mAuthCobranzaTxt = (EditText) view.findViewById(R.id.auth_cobranza_input);
        mAuthCobranzaButton = (Button) view.findViewById(R.id.auth_cobranza_button);

        setupListeners();
        verifyAuthorizationPromotion(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult **********************************");

        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_AUTH_PROMO) {
            mAuthPromotionButton.setVisibility(View.GONE);
            verifyAuthorizationPromotion(true);

        } else if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_AUTH_COBRZ) {
            mAuthCobranzaButton.setVisibility(View.GONE);
            verifyAuthorizationCobranza(true);
        }
    }


    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private boolean validateForm() {
        return (nextFromPromotion && nextFromCobranza);
    }


    private void verifyAuthorizationPromotion(final boolean promotionOnlyRequest) {

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.verif_auth_promotion_loading);

            Log.e(TAG, "verifyAuthorizationPromotion  ....--------------------------------");
            WorkflowController.getInstance().verifiyAuthorizationPromotionRequest(mPaId, new Response.Listener<WorkflowAuthorization>() {
                @Override
                public void onResponse(WorkflowAuthorization response) {
                    dismissProgressDialog();

                    if (response != null) {

                        authPromotion = new AuthorizationPromotion();
                        authPromotion.paId = mPaId;

                        if (response.control != null) {

                            if (response.control) {
                                mAuthPromotionTxt.setText((response.mssg != null && !response.mssg.isEmpty()) ? response.mssg : "");

                                switch (response.state) {
                                    case ConstantsUtil.PROMOTION_VERIFICATION_STATE:
                                        nextFromPromotion = false;
                                        hasToShowPromotionButton = false;
                                        break;
                                    case ConstantsUtil.PROMOTION_APROVE_STATE:
                                        nextFromPromotion = true;
                                        hasToShowPromotionButton = false;
                                        break;
                                    case ConstantsUtil.PROMOTION_REJECTED_STATE:
                                        nextFromPromotion = false;
                                        hasToShowPromotionButton = false;
                                        break;
                                    case ConstantsUtil.PROMOTION_BACK_TO_COMERCIAL_STATE:
                                        mAuthPromotionTxt.setText(getResources().getString(R.string.promotion_requires_auth));

                                        nextFromPromotion = false;
                                        hasToShowPromotionButton = true;
                                        addPromotionMode = false;
                                        break;

                                    default:
                                        nextFromPromotion = false;
                                        hasToShowPromotionButton = true;
                                        addPromotionMode = true;

                                        Boolean authorization = response.authorization;
                                        if (authorization != null && authorization) {
                                            mAuthPromotionTxt.setText(getResources().getString(R.string.promotion_requires_auth));
                                        }
                                        break;
                                }

                                if (!promotionOnlyRequest) {
                                    verifyAuthorizationCobranza(false);
                                } else {
                                    renderPromotionButton(hasToShowPromotionButton);
                                }

                            } else {
                                // NO AUTH_ REQUIRED
                                nextFromPromotion = true;
                                hasToShowPromotionButton = false;
                                mAuthPromotionTxt.setText(getResources().getString(R.string.promotion_no_requires_auth));
                                if (!promotionOnlyRequest) {
                                    verifyAuthorizationCobranza(false);
                                } else {
                                    renderPromotionButton(hasToShowPromotionButton);
                                }
                            }

                        } else {
                            SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_promotion).show();
                            nextFromPromotion = false;

                            if (!promotionOnlyRequest) {
                                verifyAuthorizationCobranza(false);
                            } else {
                                renderPromotionButton(hasToShowPromotionButton);
                            }
                        }

                    } else {
                        SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_promotion).show();
                        nextFromPromotion = false;

                        if (!promotionOnlyRequest) {
                            verifyAuthorizationCobranza(false);
                        } else {
                            renderPromotionButton(hasToShowPromotionButton);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    nextFromPromotion = false;
                    SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_promotion).show();
                    //DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_verif_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
            nextFromPromotion = false;
        }
    }


    private void renderPromotionButton(boolean hasToShowPromotionButton) {
        if (hasToShowPromotionButton) {
            mAuthPromotionButton.setVisibility(View.VISIBLE);
        } else {
            mAuthPromotionButton.setVisibility(View.GONE);
        }
    }

    private void renderCobranzaButton(boolean hasToShowCobranzaButton) {
        if (hasToShowCobranzaButton) {
            mAuthCobranzaButton.setVisibility(View.VISIBLE);
        } else {
            mAuthCobranzaButton.setVisibility(View.GONE);
        }
    }


    private void verifyAuthorizationCobranza(final boolean cobranzaOnlyRequest) {
        if (AppController.getInstance().isNetworkAvailable()) {
            //showProgressDialog(R.string.verif_auth_cobranza_loading);

            Log.e(TAG, "verifyAuthorizationCobranza  ....--------------------------------");

            /*
            if (!cobranzaOnlyRequest) {
                renderPromotionButton(hasToShowPromotionButton);
            }
            */


            WorkflowController.getInstance().verifiyAuthorizationCobranzaRequest(mPaId, true, new Response.Listener<WorkflowAuthorization>() {
                @Override
                public void onResponse(WorkflowAuthorization response) {
                    dismissProgressDialog();

                    if (response != null) {

                        authCobranza = new AuthorizationCobranza();
                        authCobranza.paId = mPaId;

                        if (response.control != null) {

                            if (response.control) {
                                mAuthCobranzaTxt.setText((response.mssg != null && !response.mssg.isEmpty()) ? response.mssg : "");

                                switch (response.state) {
                                    case ConstantsUtil.COBRANZA_VERIFICATION_STATE:
                                        nextFromCobranza = false;
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_APROVE_STATE:
                                        nextFromCobranza = true;
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_REJECTED_STATE:
                                        nextFromCobranza = false;
                                        hasToShowCobranzaButton = false;
                                        break;
                                    case ConstantsUtil.COBRANZA_BACK_TO_COMERCIAL_STATE:
                                        mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_requires_auth));

                                        nextFromCobranza = false;
                                        hasToShowCobranzaButton = true;
                                        addCobranzaMode = false;
                                        break;

                                    default:
                                        nextFromCobranza = false;
                                        hasToShowCobranzaButton = true;
                                        addCobranzaMode = true;

                                        Boolean authorization = response.authorization;
                                        if (authorization != null && authorization) {
                                            mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_requires_auth));
                                        }
                                        break;
                                }

                                if (!cobranzaOnlyRequest) {
                                    renderPromotionButton(hasToShowPromotionButton);
                                }
                                renderCobranzaButton(hasToShowCobranzaButton);

                            } else {
                                // NO AUTH_ REQUIRED
                                nextFromCobranza = true;
                                hasToShowCobranzaButton = false;
                                mAuthCobranzaTxt.setText(getResources().getString(R.string.cobranza_no_requires_auth));

                                if (!cobranzaOnlyRequest) {
                                    renderPromotionButton(hasToShowPromotionButton);
                                }
                                renderCobranzaButton(hasToShowCobranzaButton);
                            }

                        } else {
                            SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                            nextFromCobranza = false;
                            if (!cobranzaOnlyRequest) {
                                renderPromotionButton(hasToShowPromotionButton);
                            }
                            renderCobranzaButton(hasToShowCobranzaButton);
                        }

                    } else {
                        SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                        nextFromCobranza = false;
                        if (!cobranzaOnlyRequest) {
                            renderPromotionButton(hasToShowPromotionButton);
                        }
                        renderCobranzaButton(hasToShowCobranzaButton);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    nextFromCobranza = false;
                    if (!cobranzaOnlyRequest) {
                        renderPromotionButton(hasToShowPromotionButton);
                    }
                    renderCobranzaButton(hasToShowCobranzaButton);

                    SnackBarHelper.makeError(mMainContainer, R.string.error_verif_auth_cobranza).show();
                    //DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_verif_auth_promotion), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {

            if (!cobranzaOnlyRequest) {
                renderPromotionButton(hasToShowPromotionButton);
            }

            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
            nextFromCobranza = false;
        }
    }


    private void setupListeners() {

        mAuthPromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mAuthPromotionButton on click");
                if (addPromotionMode) {
                    IntentHelper.goToAuthorizationPromotionAddActivity(getActivity(), authPromotion);
                } else {
                    IntentHelper.goToAuthorizationPromotionUpdateActivity(getActivity(), mPaId);
                }
            }
        });


        mAuthCobranzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mAuthCobranzanButton on click");
                if (addCobranzaMode) {
                    IntentHelper.goToAuthorizationCobranzaAddActivity(getActivity(), authCobranza);
                } else {
                    IntentHelper.goToAuthorizationCobranzaUpdateActivity(getActivity(), mPaId);
                }
            }
        });
    }
}
