package ar.com.sancorsalud.asociados.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.ProspectiveClientController;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.CloseReasons;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.utils.ConstantsUtil.VIEW_EDIT_PROFILE_REQUEST_CODE;

public class PACardDetailActivity extends BaseActivity {

    private static final String TAG = "PACARD_ACT";

    private EditText mSegmentoEditText;
    private EditText mGroupEditText;
    private EditText mFechaCargadEditText;
    private EditText mFechaInicoEditText;

    private TextView mPlanInputText;
    private TextView mPlanValueText;

    private View mContainer;

    private View mCloseButton;
    private View mCardButton;
    private View mSubteButton;

    private ArrayList<CloseReasons> mReasons;
    private CloseReasons mSelectedReason;
    private SpinnerDropDownAdapter mReasonAlertAdapter;

    private ArrayList<QuoteOption> mSegmentos;
    private ArrayList<QuoteOption> mFormasIngreso;
    private ProgressBar mProgressView;

    private ProspectiveClient mClient;
    private AffiliationCardInfo mCard;

    private Toolbar toolbar;

    private boolean canEditClient = false;

    private long mCardId = -1L;
    private boolean isDesregulado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pa_card_detail);

        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        mMainContainer = findViewById(R.id.main);

        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
            mCardId = mClient.cardId;
            Log.e(TAG, "CARD ID!: " + Long.valueOf(mClient.cardId).toString());

            setupToolbar(toolbar, mClient.getFullName());
        }

        mSegmentoEditText = (EditText) findViewById(R.id.segment_input);
        setTypeTextNoSuggestions(mSegmentoEditText);

        mGroupEditText = (EditText) findViewById(R.id.group_input);
        setTypeTextNoSuggestions(mGroupEditText);

        mFechaCargadEditText = (EditText) findViewById(R.id.fecha_carga_input);
        mFechaInicoEditText = (EditText) findViewById(R.id.fecha_inicio_input);

        mPlanInputText = (TextView) findViewById(R.id.plan_id_input);
        //mPlanValueText = (TextView) findViewById(R.id.plan_value_input);

        mContainer = findViewById(R.id.container);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        mSegmentos = QuoteOptionsController.getInstance().getSegmentos();
        mFormasIngreso = QuoteOptionsController.getInstance().getFormasIngreso();


        mCloseButton = findViewById(R.id.close_button);
        mCardButton = findViewById(R.id.card_button);
        mSubteButton = findViewById(R.id.subte_button);

        setupListener();
        loadProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume ...............");
        if (Storage.getInstance().hasToReloadPA()){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed -----------------------------");
        showProgress(false);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult------");

        if (requestCode == VIEW_EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            mClient = (ProspectiveClient) data.getSerializableExtra(ConstantsUtil.RESULT_PA);
            setupToolbar(toolbar, mClient.getFullName());
            loadProfile();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit: {
                if (canEditClient)
                    IntentHelper.gotoEditPAActivity(PACardDetailActivity.this, mClient, false);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // --- helper methods --------------------------------------------------------------- //

    private void setupListener() {

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReasons == null)
                    retrieveReasons();
                else showReasonsDialog(mReasons);
            }
        });

        mCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "toLoadCard.....");

                String sFechaCarga = mFechaCargadEditText.getText().toString();

                if (mClient.state != null && mClient.state == ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT) {
                    Storage.getInstance().setCardEditableMode(false);
                } else {
                    Storage.getInstance().setCardEditableMode(true);
                }

                IntentHelper.goToInitLoadActivity(PACardDetailActivity.this, mClient, sFechaCarga);
            }
        });

        mSubteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDesregulado) {
                    IntentHelper.goToSubteNoGravActivity(PACardDetailActivity.this, mClient.id);
                } else {
                    IntentHelper.goToSubteGravActivity(PACardDetailActivity.this, mClient.id);
                }
            }
        });
    }

    private void loadProfile() {

        enableActionButtons(false);

        // Check back navigation
        showProgress(true);
        canEditClient = false;

        ProspectiveClientController.getInstance().getProspectiveClientProfile(mClient, new Response.Listener<ProspectiveClient>() {
            @Override
            public void onResponse(ProspectiveClient clientResponse) {
                mClient = clientResponse;
                canEditClient = true;
                mClient.cardId = mCardId;

                Log.e(TAG, "loadProfile  clientID: " + mClient.id + "..................");
                Log.e(TAG, "loadProfile  client cardID: " + mCardId  +  "..................");


                loadCard();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                enableActionButtons(true);

                DialogHelper.showStandardErrorMessage(PACardDetailActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        });
    }


    private void fillCardForm(Quotation quotation) {

        StringBuffer data = null;
        if (mCard.segmento != null && mCard.formaIngreso != null) {
            data = getSegmentoAndFormaIngreso(mCard.segmento, mCard.formaIngreso);
            isDesregulado = mCard.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO);

        } else if (quotation != null) {
            data = getSegmentoAndFormaIngreso(quotation.segmento, quotation.formaIngreso);
            isDesregulado = quotation.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO);
        }

        if (data != null) {
            mSegmentoEditText.setText(data.toString());
        }

        if (mCard.cantMembers > 0) { // at least titular must be in card member
            mGroupEditText.setText(Integer.valueOf(mCard.cantMembers).toString());
        } else if (quotation != null) { // in quotation titular is not in integrantes
            mGroupEditText.setText(Integer.valueOf(quotation.integrantes.size() + 1).toString());
        }

        if (mCard.fechaCarga != null) {
            mFechaCargadEditText.setText(ParserUtils.parseDate(mCard.fechaCarga, "yyyy-MM-dd"));
        } else if (quotation != null) {
            mFechaCargadEditText.setText("");
        }
        if (mCard.fechaInicioServicio != null) {
            mFechaInicoEditText.setText(ParserUtils.parseDate(mCard.fechaInicioServicio, "yyyy-MM-dd"));
        } else {
            Date today = new Date();
            if (quotation.inputDate != null) {
                Date inputDate = ParserUtils.parseDate(quotation.inputDate, "yyyy-MM-dd");

                if (inputDate.before(today)) {
                    mFechaInicoEditText.setText(ParserUtils.parseDate(today, "yyyy-MM-dd"));
                } else {
                    // Date that user estimate in quotation process
                    mFechaInicoEditText.setText(quotation.inputDate);
                }
            }
        }

        if (mCard.plan != null && mCard.plan.descripcionPlan != null) {
            mPlanInputText.setText(getString(R.string.plan_data, mCard.plan.descripcionPlan));
            //mPlanValueText.setText("$" + Double.valueOf(mCard.plan.diferenciaAPagar).toString());
        }

        enableActionButtons(true);
    }

    private StringBuffer getSegmentoAndFormaIngreso(QuoteOption segmento, QuoteOption formaIngreso) {

        StringBuffer data = new StringBuffer();

        int selectedSegmento = mSegmentos.indexOf(segmento);
        if (selectedSegmento != -1)
            data.append(segmento.title);

        int selectedFormaIngreso = mFormasIngreso.indexOf(formaIngreso);
        if (selectedFormaIngreso != -1)
            data.append(" - ").append(formaIngreso.title);

        return data;
    }

    private void loadCard() {

        Log.e(TAG, "loadCard cardID: " + mClient.cardId + "..................");

        if (AppController.getInstance().isNetworkAvailable()) {

            CardController.getInstance().getAffiliationInfo(mClient.cardId, new Response.Listener<AffiliationCardInfo>() {
                @Override
                public void onResponse(AffiliationCardInfo cardResult) {

                    if (cardResult != null) {
                        Log.e(TAG, "cardResult ok .....");
                        mCard = cardResult;
                        if (mCard != null) {
                            loadPlan();
                        }

                    } else {
                        showProgress(false);
                        enableActionButtons(true);

                        DialogHelper.showMessage(PACardDetailActivity.this, getResources().getString(R.string.error_card_detail));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    showProgress(false);
                    enableActionButtons(true);

                    Log.e(TAG, "Error al obtener la ficha: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(PACardDetailActivity.this, getResources().getString(R.string.error_card_detail), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            showProgress(false);
            enableActionButtons(true);

            DialogHelper.showNoInternetErrorMessage(PACardDetailActivity.this, null);
        }
    }

    private void loadPlan() {
        if (AppController.getInstance().isNetworkAvailable()) {

            Log.e(TAG, "loadPlan for Client ID:" + mClient.id + "..................");
            mProgressView.setVisibility(View.VISIBLE);
            long dni = mClient.dni;

            QuotationController.getInstance().quotationParametesForQuotedClient(dni, true, ConstantsUtil.QUOTED_PLAN_SELECTED_FILTER, new Response.Listener<Quotation>() {
                @Override
                public void onResponse(Quotation quotation) {
                    Log.e(TAG, "quotation parameters  ok .....");
                    showProgress(false);

                    if (quotation != null && quotation.client != null && quotation.client.quotedDataList != null && !quotation.client.quotedDataList.isEmpty()) {

                        QuotedClientData quotedClientData = quotation.client.quotedDataList.get(0);
                        if (!quotedClientData.planes.isEmpty()) {
                            mCard.plan = quotedClientData.planes.get(0);
                        }
                        fillCardForm(quotation);

                    } else {
                        fillCardForm(null);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    enableActionButtons(true);

                    Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                }
            });

        } else {
            showProgress(false);
            enableActionButtons(true);

            DialogHelper.showNoInternetErrorMessage(PACardDetailActivity.this, null);
        }
    }


    private void retrieveReasons() {
        showProgress(true);

        HRequest request = RestApiServices.createGetCloseReasonsRequest(new Response.Listener<ArrayList<CloseReasons>>() {
            @Override
            public void onResponse(ArrayList<CloseReasons> list) {
                mReasons = list;
                showReasonsDialog(mReasons);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PACardDetailActivity.this, null);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    private void closeProspectiveClient(CloseReasons reason) {
        showProgress(true);

        HRequest request = RestApiServices.createCloseProspectiveClientRequest(mClient.id, reason.reasonId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void list) {
                finishActivityWithCode(ConstantsUtil.RESULT_DATA_UPDATED);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PACardDetailActivity.this, null);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }

    private void showReasonsDialog(ArrayList<CloseReasons> list) {
        mSelectedReason = null;
        ArrayList<String> reasonsStr = new ArrayList<String>();
        for (CloseReasons r : list) {
            reasonsStr.add(r.reasonDescription);
        }

        mReasonAlertAdapter = new SpinnerDropDownAdapter(this, reasonsStr, -1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.close_reason_title))
                .setSingleChoiceItems(mReasonAlertAdapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mReasonAlertAdapter.setSelectedIndex(i);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mReasonAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });


        builder.setPositiveButton(
                R.string.option_confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mReasonAlertAdapter.getSelectedIndex() >= 0 && mReasonAlertAdapter.getSelectedIndex() < mReasons.size()) {
                            mSelectedReason = mReasons.get(mReasonAlertAdapter.getSelectedIndex());
                            if (mSelectedReason != null)
                                closeProspectiveClient(mSelectedReason);
                        } else {
                            showProgress(false);
                        }
                    }
                });

        builder.setNegativeButton(
                R.string.option_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProgress(false);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showProgress(false);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        mContainer.setVisibility(show ? View.GONE : View.VISIBLE);

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

    private void enableActionButtons(boolean est) {
        mCloseButton.setEnabled(est);
        mCardButton.setEnabled(est);
        mSubteButton.setEnabled(est);
    }
}
