package ar.com.sancorsalud.asociados.activity.affiliation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.PlanSelectAdapter;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.ContactData;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.affiliation.Address;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class InitLoadDataActivity extends BaseActivity {

    private static final String TAG = "INITLOAD_ACT";

    private View mFechaCargaButton;
    private EditText mFechaCargadEditText;
    private Button desButton;
    private Button affiliationButton;

    private SimpleDateFormat mDateFormat;

    private Client mClient;
    private Quotation mQuotation;

    private String mFechaCarga;

    private ProgressBar mProgressView;

    private long mCotizacionId = -1L;
    private long mDesCotizacionId = -1L;
    private Plan mPlan;

    private String desFilesSubDir = "";
    private String healthCertFilesSubDir="";
    private String attachsFilesSubDir = "";
    private String anexoFilesSubDir = "";

    private List<AttachFile> tmpDesFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpHealthCertFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpAttachsFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpAnexoFiles = new ArrayList<AttachFile>();

    private Des mDes;
    private AffiliationCard mAffiliationCard;

    private boolean editableCard = true;


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "Init Date: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                /*
                if (date.before(today)) {
                    mFechaCargadEditText.setText(ParserUtils.parseDate(today, DATE_FORMAT));
                } else {
                    mFechaCargadEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));
                }
                */
                mFechaCargadEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));

            } catch (Exception e) {
                mFechaCargadEditText.setText(mDateFormat.format(today));
            }
            mFechaCargadEditText.requestFocus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_load);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.init_load_data);

        mMainContainer = findViewById(R.id.main);
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);


        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
            Log.e(TAG, "CARD ID: " + Long.valueOf(mClient.cardId).toString());

            mFechaCarga = getIntent().getStringExtra(ConstantsUtil.FECHA_CARGA);

            desFilesSubDir = mClient.dni + "/des/des_files";
            healthCertFilesSubDir = mClient.dni + "/des/health_files";
            attachsFilesSubDir = mClient.dni + "/des/attach_files";
            anexoFilesSubDir = mClient.dni + "/des/anexo_files";
        }

        mFechaCargadEditText = (EditText) findViewById(R.id.fecha_carga_input);
        if (mFechaCarga != null) {
            // ALREADY DEFINED IN CARD
            mFechaCargadEditText.setText(mFechaCarga);
        } else {
            // NOT CARD ALREADY CREATED
            mFechaCargadEditText.setText(ParserUtils.parseDate(new Date(), DATE_FORMAT));
        }

        mFechaCargaButton = findViewById(R.id.fecha_carga_button);

        desButton = (Button) findViewById(R.id.des_button);
        affiliationButton = (Button) findViewById(R.id.affiliation_button);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        enableActionButtons(false);
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume ...............");

        if (Storage.getInstance().getAffiliationCardId() != null) {

            Log.e(TAG, "getting client card id form Storage: " +  Storage.getInstance().getAffiliationCardId());

            // reload client card id if affiliation has change and saved
            // When user goes to Affiliation circuit , generate card then back to des circuit PA must take the new cardId
            mClient.cardId = Storage.getInstance().getAffiliationCardId();
            // reset Data
            Storage.getInstance().setAffiliationCardId(null);
        }

        checkClientHasAlreadyPlanSelected();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed !!!!!!!! ....");
        // TODO NUEVO
        Storage.getInstance().setReloadPA(true);
        mProgressView.setVisibility(View.GONE);

        // mark to reload PA list  beacause pa back screen have an old card_id (PA Detail / PACardDetail)
        super.onBackPressed();
    }

    private void enableActionButtons(boolean est) {
        mFechaCargaButton.setEnabled(est);

        desButton.setEnabled(est);
        affiliationButton.setEnabled(est);
    }

    private void setupListeners() {

        if (editableCard) {
            mFechaCargaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar();
                }
            });
        }

        desButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "DES Buton.....");
                //resetAllFiles();

                if (mClient.cardId != -1L) {
                    // If exists card exist DES reload it, and check if not empty DES
                    Log.e(TAG, "Reloading DES for card id: " + mClient.cardId);
                    fillDesFromCardId();
                } else {
                    //desAddMode = true;
                    if (mDes == null) {
                        Log.e(TAG, "New DES ......");
                        mDes = new Des();
                        fillDesFormQuotationParameters();
                    } else {
                        toDes();
                    }
                }
            }
        });

        affiliationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "affiliationButton.....");

                if (mClient.cardId != -1L) {
                    Log.e(TAG, "Reloading affiliation CARD for card id: " + mClient.cardId);
                    // If exists card reload it
                    fillAffiliationFromCardId();
                } else {

                    if (mAffiliationCard == null) {
                        Log.e(TAG, "New Afiliation card ......");

                        mAffiliationCard = new AffiliationCard();
                        TitularData tData = new TitularData();
                        mAffiliationCard.titularData = tData;
                        //mAffiliationCard.loadFromQR = mQuotation.integrantes.get(0).loadFromQR;

                        fillAffiliationFromQuotationParameters(tData);
                    }

                    toAffiliation();
                }
            }
        });
    }

    private void fillDesFromCardId() {

        if (AppController.getInstance().isNetworkAvailable()) {

            mProgressView.setVisibility(View.VISIBLE);
            Log.e(TAG, "fillDesFromCardId ....--------------------------------");

            CardController.getInstance().getDesData(-1L, mClient.cardId, new Response.Listener<Des>() {
                @Override
                public void onResponse(Des desResult) {
                    mProgressView.setVisibility(View.GONE);

                    if (desResult != null) {
                        mDes = desResult;

                        mDes.clientDni = mClient.dni;
                        mDes.cotizacionId = mDesCotizacionId;
                        mDes.formaIngresoId = Long.valueOf(mQuotation.formaIngreso.id);
                        Log.e(TAG, "DES READED OK ID:  " + Long.valueOf(mDes.id));

                        fillAllPhysicalFiles();

                    } else {
                        //mDes = null;
                        DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_des_data));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressView.setVisibility(View.GONE);
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_des_data), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });
        } else {
            mProgressView.setVisibility(View.GONE);
            DialogHelper.showNoInternetErrorMessage(InitLoadDataActivity.this, null);
        }
    }


    public void fillAffiliationFromCardId() {

        if (AppController.getInstance().isNetworkAvailable()) {
            final Long cardId = mClient.cardId;
            Log.e(TAG, "fillAffiliationFromCardId: CARD ID: " + cardId);
            mProgressView.setVisibility(View.VISIBLE);

            CardController.getInstance().getAffiliationData(cardId, new Response.Listener<AffiliationCard>() {
                @Override
                public void onResponse(AffiliationCard affiliationCard) {
                    mProgressView.setVisibility(View.GONE);

                    if (affiliationCard != null) {
                        Log.e(TAG, "fillAffiliationFromCardId affiliationCardResult  ok .....");
                        mAffiliationCard = affiliationCard;
                        // already have an idCotizacion
                        mAffiliationCard.idCotizacion = mCotizacionId;

                        // MIX DATA
                        mAffiliationCard.plan = mPlan;

                        // TODO Back end must complete all data no mathers if Card is generated first from DES or from Affiliation
                        // TODO but Back end have bugs and not fill all card Data , so we must do a work Around to fill it

                        // Work Around Check possible Creation of Empty Card when logics comes First form DES that generate an empty card
                        if (mAffiliationCard.titularData == null) {
                            mAffiliationCard.titularData = new TitularData();
                            Log.e(TAG, "Null titular data  fillAffiliationFromQuotationParameters");
                            fillAffiliationFromQuotationParameters(mAffiliationCard.titularData);
                        } else {

                            if (mAffiliationCard.titularData.segmento == null || mAffiliationCard.titularData.formaIngreso == null) {

                                Log.e(TAG, "Null titular datasegmento/fingreso  fillAffiliationFromQuotationParameters");

                                // Cards in DB that have null segment/ fingreso reveals it comes form DES circuit, and not filled yet
                                fillAffiliationFromQuotationParameters(mAffiliationCard.titularData);
                            } else {

                                // UPDATE POSSIBLE INCORRECT DATA FROM LOADED CARD
                                if (mAffiliationCard.titularData.age == -1) {
                                    mAffiliationCard.titularData.age = mClient.age;
                                }
                                if (mQuotation.nombreEmpresa != null) {
                                    mAffiliationCard.titularData.nombreEmpresa = mQuotation.nombreEmpresa;
                                }

                                // UPDATE FECHA CARGA if user chages it
                                mAffiliationCard.titularData.fechaCarga = ParserUtils.parseDate(mFechaCargadEditText.getText().toString(), DATE_FORMAT);
                                Log.e(TAG, "Fecha Carga: " + mFechaCargadEditText.getText().toString());

                                // UPDATE FECHA INICIO if user chages it
                                String sInicioDate = null;
                                if (mAffiliationCard.titularData.fechaInicioServicio != null) {
                                    sInicioDate = ParserUtils.parseDate(mAffiliationCard.titularData.fechaInicioServicio, DATE_FORMAT);
                                    Log.e(TAG, "Fecha Ini serv: " + sInicioDate);
                                }
                                mAffiliationCard.titularData.fechaInicioServicio = getInitDate(sInicioDate);


                                // Set FORCED DOC TYPE
                                if (mAffiliationCard.titularData.docType == null || mAffiliationCard.titularData.docType.id == null || mAffiliationCard.titularData.docType.id.isEmpty() || mAffiliationCard.titularData.docType.extra == null || mAffiliationCard.titularData.docType.extra.isEmpty()) {
                                    mAffiliationCard.titularData.docType = new QuoteOption(ConstantsUtil.DOC_TYPE_IDENTITY, QuoteOptionsController.getInstance().getDocTypeName(ConstantsUtil.DOC_TYPE_IDENTITY));
                                }

                                // check Address
                                if (mAffiliationCard.titularAddress == null) {
                                    mAffiliationCard.titularAddress = new Address();

                                    Log.e(TAG, "Null titular adress  fillAffiliationAddressFromQuotationParameters");
                                    fillAffiliationAddressFromQuotationParameters(mAffiliationCard.titularAddress);
                                }

                                // check contact
                                if (mAffiliationCard.contactData == null) {
                                    mAffiliationCard.contactData = new ContactData();
                                    Log.e(TAG, "Null titular adress  fillAffiliationContactFromQuotationParameters");

                                    fillAffiliationContactFromQuotationParameters(mAffiliationCard.contactData);
                                }
                                mAffiliationCard.contactData.suggestedPhone = mClient.phoneNumber;
                                mAffiliationCard.contactData.suggestedDevice = mClient.celularNumber;

                                ar.com.sancorsalud.asociados.model.affiliation.Member conyugeMember = null;
                                for (ar.com.sancorsalud.asociados.model.affiliation.Member m : mAffiliationCard.members) {
                                    if (m.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || m.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))
                                        conyugeMember = m;
                                    break;
                                }

                                // Additional DATA is stored in other tables : Forma Pago, OS so it will persit whe user is that screen not early
                                // The confuse parat is that FormaPAgo and OS comes in Ficha Obtener but it stored in otyrher table rather than Ficha
                                checkEmptyAdditionalData(cardId, mAffiliationCard.titularData, conyugeMember);
                            }


                            /*
                            if (mAffiliationCard.stateId == 2){
                                DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_card_pending_state));
                            }
                            */

                            toAffiliation();
                        }

                    } else {
                        DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_card_detail));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressView.setVisibility(View.GONE);
                    Log.e(TAG, ((error != null && (error.getMessage() != null) ? error.getMessage() : "")));
                    DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_card_detail), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });


        } else {
            mProgressView.setVisibility(View.GONE);
            DialogHelper.showNoInternetErrorMessage(InitLoadDataActivity.this, null);
        }
    }


    private void checkClientHasAlreadyPlanSelected() {

        Log.e(TAG, "checkClientHasAlreadyPlanSelected....");

        if (AppController.getInstance().isNetworkAvailable()) {

            mProgressView.setVisibility(View.VISIBLE);

            QuotationController.getInstance().quotationParametesForQuotedClient(mClient.dni, true, ConstantsUtil.QUOTED_PLAN_SELECTED_FILTER, new Response.Listener<Quotation>() {
                @Override
                public void onResponse(Quotation quotation) {
                    if (quotation != null && quotation.client != null) {
                        Log.e(TAG, "quotation parameters ok for filter E: Elegida .....");

                        if (quotation.client.quotedDataList != null && quotation.client.quotedDataList.size() == 1 && quotation.client.quotedDataList.get(0).planes.size() == 1) {

                            Log.e(TAG, "Already have a plan saved .....");
                            mQuotation = quotation;
                            mCotizacionId = quotation.client.quotedDataList.get(0).planes.get(0).idCotizacion;
                            mDesCotizacionId = quotation.client.quotedDataList.get(0).id;

                            mPlan = quotation.client.quotedDataList.get(0).planes.get(0);

                            mProgressView.setVisibility(View.GONE);
                            enableActionButtons(true);

                        } else {
                            // new action
                            fillSavedPlanesToSelect();
                        }

                    } else {
                        mProgressView.setVisibility(View.GONE);
                        DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_check_plans));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressView.setVisibility(View.GONE);
                    DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_check_plans));
                }
            });

        } else {
            mProgressView.setVisibility(View.GONE);
            DialogHelper.showNoInternetErrorMessage(InitLoadDataActivity.this, null);
        }
    }

    private void fillSavedPlanesToSelect() {

        Log.e(TAG, "fillSavedPlanesToSelect .....");

        if (AppController.getInstance().isNetworkAvailable()) {

            //mProgressView.setVisibility(View.VISIBLE);
            //long dni = mClient.dni;

            QuotationController.getInstance().quotationParametesForQuotedClient(mClient.dni, true, ConstantsUtil.QUOTED_PLANS_SAVED_FILTER, new Response.Listener<Quotation>() {
                @Override
                public void onResponse(Quotation quotation) {
                    Log.e(TAG, "quotation saved parameters  ok for filter G: Guardadas .....");
                    //showProgress(false);

                    if (quotation != null && quotation.client != null) {
                        if (quotation.client.quotedDataList != null && quotation.client.quotedDataList.size() == 1 && !quotation.client.quotedDataList.get(0).planes.isEmpty()) {
                            mQuotation = quotation;

                            mProgressView.setVisibility(View.GONE);

                            mDesCotizacionId = quotation.client.quotedDataList.get(0).id;
                            showPlanestoSelect(quotation.client.quotedDataList.get(0).id, quotation.client.quotedDataList.get(0).planes);

                        } else {
                            mProgressView.setVisibility(View.GONE);
                            DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_empty_plans));
                        }

                    } else {
                        mProgressView.setVisibility(View.GONE);
                        DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_empty_plans));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressView.setVisibility(View.GONE);
                    DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_empty_plans));
                }
            });

        } else {
            mProgressView.setVisibility(View.GONE);
            DialogHelper.showNoInternetErrorMessage(InitLoadDataActivity.this, null);
        }
    }


    private void saveQuotationPlanSelected(final long quotedId, Plan plan) {

        if (AppController.getInstance().isNetworkAvailable()) {

            QuotationController.getInstance().saveQuotationPlanSelected(quotedId, plan, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void result) {
                    enableActionButtons(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogHelper.showMessage(InitLoadDataActivity.this, getResources().getString(R.string.error_init_load_saving_plan));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(InitLoadDataActivity.this, null);
        }
    }

    // --- PLANES TO SELECT--------------------------------------------------------------------- //

    private void showPlanestoSelect(final long quotedId, final List<Plan> planList) {

        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.activity_plan_selection, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(inflator);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        final PlanSelectAdapter mPlanSelectAdapter = new PlanSelectAdapter(getApplicationContext(), planList);
        RecyclerView mPlansRecyclerView = (RecyclerView) inflator.findViewById(R.id.plan_recycler_view);
        LinearLayoutManager plansLayoutManager = new LinearLayoutManager(mPlansRecyclerView.getContext());

        mPlansRecyclerView.setLayoutManager(plansLayoutManager);
        mPlansRecyclerView.setAdapter(mPlanSelectAdapter);
        mPlansRecyclerView.setHasFixedSize(true);

        Button mAcceptButton = (Button) inflator.findViewById(R.id.accept_button);
        Button mCancelButton = (Button) inflator.findViewById(R.id.cancel_button);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick !!!");

                if (mPlanSelectAdapter.getItemCount() > 0) {
                    int index = mPlanSelectAdapter.getSelectedIndex();
                    if (index != -1) {
                        dialog.dismiss();

                        Plan plan = planList.get(index);
                        Log.e(TAG, "plan idCotiz:" + plan.idCotizacion + "desc:" + plan.descripcionPlan + " costo: " + plan.diferenciaAPagar);
                        mCotizacionId = plan.idCotizacion;
                        mPlan = plan;

                        saveQuotationPlanSelected(quotedId, plan);
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "cancel onClick !!!");
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private Date getInitDate(String inputDate) {
        Date today = new Date();
        Date date = ParserUtils.parseDate(inputDate, DATE_FORMAT);

        if (inputDate != null) {
            Log.e(TAG, "in date: " + inputDate);
            Log.e(TAG, "in today: " + today);

            if (date.before(today)) {
                return today;
            } else {
                return date;
            }

        } else {
            return today;
        }
    }

    // --- DES ------------------------------------------------------------------------- //

    private void toDes() {

        // TODO OLD
        /*
        if (desAddMode) {
            IntentHelper.goToDesActivity(InitLoadDataActivity.this, mDes, true);
        } else {
            Log.e(TAG, "Edit only Attach Files to DES");
            IntentHelper.goToDesActivity(InitLoadDataActivity.this, mDes, false);
        }
        */

        // TODO NEW
        IntentHelper.goToDesActivity(InitLoadDataActivity.this, mDes);
    }

    private void fillDesFormQuotationParameters() {

        Log.e(TAG, "fillDesFormQuotationParameters.....");

        //mDes.clientDni = mQuotation.client.dni;
        mDes.clientDni = mClient.dni;

        mDes.cotizacionId = mDesCotizacionId;
        mDes.formaIngresoId = Long.valueOf(mQuotation.formaIngreso.id);

        fillDesDetailsFormQuotationParameters();
        toDes();
    }


    private void fillDesDetailsFormQuotationParameters() {

        Log.e(TAG, "fillDesDetailsFormQuotationParameters.....");

        mDes.details = new ArrayList<DesDetail>();
        if (mQuotation.titular != null) {

            DesDetail detail = new DesDetail();

            detail.firstname = mQuotation.client.firstname;
            detail.lastname = mQuotation.client.lastname;
            detail.age = mQuotation.titular.age;
            detail.parentesco = mQuotation.titular.parentesco;

            //detail.dni = mQuotation.client.dni;
            detail.dni = mClient.dni;
            detail.existent = mQuotation.titular.existent;
            detail.active = mQuotation.titular.active;

            mDes.details.add(detail);
        }

        for (Member member : mQuotation.integrantes) {
            DesDetail detail = new DesDetail();

            detail.firstname = member.firstname;
            detail.lastname = member.lastname;
            detail.age = member.age;
            detail.parentesco = member.parentesco;
            detail.dni = member.dni;
            detail.existent = member.existent;
            detail.active = member.active;

            mDes.details.add(detail);
        }
    }

    // --- Affiliation ------------------------------------------------------------------------- //

    private void toAffiliation() {

        Log.e(TAG, "toAffiliation...");

        long id = mClient.id;

        HRequest request = RestApiServices.createGetProspectiveClientProfileRequest(id, new Response.Listener<ProspectiveClient>(){

            @Override
            public void onResponse(ProspectiveClient response) {
                IntentHelper.goToAffiliationActivity(InitLoadDataActivity.this, mAffiliationCard, mClient.id, response.loadFromQR);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                IntentHelper.goToAffiliationActivity(InitLoadDataActivity.this, mAffiliationCard, mClient.id, false);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,true);

    }

    private void fillAffiliationFromQuotationParameters(TitularData tData) {

        Log.e(TAG, "fillAffiliationFromQuotationParameters...");

        // MIX  data
        mAffiliationCard.idCotizacion = mCotizacionId;
        mAffiliationCard.plan = mPlan;

        // TITULAR DATA
        tData.fechaCarga = ParserUtils.parseDate(mFechaCargadEditText.getText().toString(), DATE_FORMAT);
        tData.fechaInicioServicio = getInitDate(mQuotation.inputDate);

        tData.segmento = mQuotation.segmento;
        tData.formaIngreso = mQuotation.formaIngreso;
        tData.categoria = mQuotation.categoria;
        tData.coberturaProveniente = mQuotation.coberturaProveniente;
        tData.condicionIva = mQuotation.condicionIva;

        // FILL FROM ORIGINAL DATA
        tData.firstname = mQuotation.client.firstname;
        tData.lastname = mQuotation.client.lastname;
        tData.dni = mQuotation.client.dni;
        tData.docType = new QuoteOption(ConstantsUtil.DOC_TYPE_IDENTITY, QuoteOptionsController.getInstance().getDocTypeName(ConstantsUtil.DOC_TYPE_IDENTITY));

        // Age comes from PQ : Capitas
        tData.age = mQuotation.titular.age;
        if (tData.age == -1 || tData.age == 0) {
            tData.age = mClient.age;
        }

        tData.birthday = mQuotation.client.birthday;
        if (tData.birthday == null) {
            tData.birthday = mClient.birthday;
        }

        tData.nombreAfinidad = mQuotation.nombreAfinidad;
        tData.nombreEmpresa = mQuotation.nombreEmpresa;
        tData.existent = mQuotation.titular.existent;

        mAffiliationCard.titularData = tData;

        // TITULAR ADDRESS:  // FILL FROM ORIGINAL DATA
        Address tAddress = new Address();
        fillAffiliationAddressFromQuotationParameters(tAddress);

        // TITULAR CONTACT DATA :  // FILL FROM ORIGINAL DATA
        ContactData tContactData = new ContactData();
        fillAffiliationContactFromQuotationParameters(tContactData);

        // MEMBERS
        fillMembersFormQuotationParameters();

        // ADDITIONAL DATA NEED CARD ID, this id will be transport by affiliationActivity to fragments that need to set the cardId to the POJOS
        // cardId will be obtain by the first save card method
        fillAditionalData(mAffiliationCard.titularData);
    }


    private void fillAffiliationAddressFromQuotationParameters(Address tAddress) {
        tAddress.street = mClient.street;
        tAddress.number = mClient.streetNumber;
        tAddress.floor = mClient.floorNumber;
        tAddress.dpto = mClient.department;

        if (mQuotation.client.zip != null && !mQuotation.client.zip.isEmpty()) {
            tAddress.zipCode = mQuotation.client.zip;
        }else{
            tAddress.zipCode = mClient.zip;
        }

        mAffiliationCard.titularAddress = tAddress;
    }


    private void fillAffiliationContactFromQuotationParameters(ContactData tContactData) {
        tContactData.suggestedPhone = mClient.phoneNumber;
        tContactData.suggestedDevice = mClient.celularNumber;
        tContactData.email = mClient.email;

        mAffiliationCard.contactData = tContactData;
    }


    private void fillMembersFormQuotationParameters() {
        mAffiliationCard.members = new ArrayList<ar.com.sancorsalud.asociados.model.affiliation.Member>();

        for (Member member : mQuotation.integrantes) {

            ar.com.sancorsalud.asociados.model.affiliation.Member aMember = new ar.com.sancorsalud.asociados.model.affiliation.Member();
            //aMember.canDelete = false;
            aMember.parentesco = member.parentesco;
            aMember.firstname = member.firstname;
            aMember.lastname = member.lastname;
            aMember.dni = member.dni;
            aMember.age = member.age;
            aMember.beneficiarioSUF = false;
            aMember.existent = member.existent;

            if (aMember.docType == null) {
                aMember.docType = new QuoteOption(ConstantsUtil.DOC_TYPE_IDENTITY, QuoteOptionsController.getInstance().getDocTypeName(ConstantsUtil.DOC_TYPE_IDENTITY));
            }

            mAffiliationCard.members.add(aMember);
        }
    }


    private void fillAditionalData(TitularData tData) {

        Log.e(TAG, "fillAditionalData...");
        ConstantsUtil.Segmento seg = tData.getSegmento();
        ConstantsUtil.FormaIngreso ingreso = tData.getFormaIngreso();

        if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            AdditionalData1AutonomoIndividual additionalData1 = new AdditionalData1AutonomoIndividual();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            additionalData1.pago.cardType = mQuotation.pago.tarjeta;
            additionalData1.pago.bankEmiter = mQuotation.pago.banco;
            additionalData1.pago.bankCBU = mQuotation.pago.banco;

            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2AutonomoIndividual additionalData2 = new AdditionalData2AutonomoIndividual();
            additionalData2.sufTitular = null;
            additionalData2.sufConyuge = null;
            mAffiliationCard.additionalData2 = additionalData2;

        } else if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            AdditionalData1AutonomoEmpresa additionalData1 = new AdditionalData1AutonomoEmpresa();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2AutonomoEmpresa additionalData2 = new AdditionalData2AutonomoEmpresa();
            additionalData2.sufTitular = null;
            additionalData2.sufConyuge = null;
            mAffiliationCard.additionalData2 = additionalData2;

        } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {

            AdditionalData1DesreguladoIndividual additionalData1 = new AdditionalData1DesreguladoIndividual();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            additionalData1.pago.cardType = mQuotation.pago.tarjeta;
            additionalData1.pago.bankEmiter = mQuotation.pago.banco;
            additionalData1.pago.bankCBU = mQuotation.pago.banco;

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2DesreguladoIndividual additionalData2 = new AdditionalData2DesreguladoIndividual();
            additionalData2.obraSocial = new ObraSocial();
            additionalData2.obraSocial.osActual = mQuotation.desregulado.osDeregulado;
            additionalData2.obraSocial.osQuotation = mQuotation.desregulado.osDeregulado;
            mAffiliationCard.additionalData2 = additionalData2;

            // titular aporta monotributo
            AdditionalData3DesreguladoIndividual additionalData3 = new AdditionalData3DesreguladoIndividual();
            if (mQuotation.desregulado.aportaMonotributo != null && mQuotation.desregulado.aportaMonotributo) {
                mAffiliationCard.titularData.aportaMonotributo = true;
                additionalData3.osMonotributo = new ObraSocial();
            }
            mAffiliationCard.additionalData3 = additionalData3;

            // Check Conyuge DATA
            if (mQuotation.unificaAportes) {
                fillConyugeDataForDesregulado();
            }

        } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            AdditionalData1DesreguladoEmpresa additionalData1 = new AdditionalData1DesreguladoEmpresa();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2DesreguladoEmpresa additionalData2 = new AdditionalData2DesreguladoEmpresa();
            additionalData2.obraSocial = new ObraSocial();
            additionalData2.obraSocial.osActual = mQuotation.desregulado.osDeregulado;
            additionalData2.obraSocial.osQuotation = mQuotation.desregulado.osDeregulado;

            // titular aporta monotributo
            if (mQuotation.desregulado.aportaMonotributo != null && mQuotation.desregulado.aportaMonotributo) {
                mAffiliationCard.titularData.aportaMonotributo = true;
                AdditionalData3DesreguladoEmpresa additionalData3 = new AdditionalData3DesreguladoEmpresa();
                additionalData3.osMonotributo = new ObraSocial();
                mAffiliationCard.additionalData3 = additionalData3;
            }

            // Check Conyuge DATA
            if (mQuotation.unificaAportes) {
                fillConyugeDataForDesregulado();
            }

            mAffiliationCard.additionalData2 = additionalData2;


        } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            AdditionalData1MonotributoIndividual additionalData1 = new AdditionalData1MonotributoIndividual();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            additionalData1.pago.cardType = mQuotation.pago.tarjeta;
            additionalData1.pago.bankEmiter = mQuotation.pago.banco;
            additionalData1.pago.bankCBU = mQuotation.pago.banco;

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2MonotributoIndividual additionalData2 = new AdditionalData2MonotributoIndividual();
            additionalData2.obraSocial = new ObraSocial();


            mAffiliationCard.additionalData2 = additionalData2;

            mAffiliationCard.titularData.aportaMonotributo = true;

            if (mQuotation.unificaAportes) {
                fillConyugeDataForMonotributo();
            }

        } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            AdditionalData1MonotributoEmpresa additionalData1 = new AdditionalData1MonotributoEmpresa();
            additionalData1.pago = new Pago();
            additionalData1.pago.formaPago = mQuotation.pago.formaPago;

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
            mAffiliationCard.additionalData1 = additionalData1;

            AdditionalData2MonotributoEmpresa additionalData2 = new AdditionalData2MonotributoEmpresa();
            additionalData2.obraSocial = new ObraSocial();
            mAffiliationCard.additionalData2 = additionalData2;

            mAffiliationCard.titularData.aportaMonotributo = true;

            if (mQuotation.unificaAportes) {
                fillConyugeDataForMonotributo();
            }
        }
    }


    private void fillConyugeDataForDesregulado() {

        ConyugeData conyugeData = new ConyugeData();
        conyugeData.segmento = mQuotation.desregulado.conyuge.segmento;

        if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
            conyugeData.obraSocial = new ObraSocial();
            conyugeData.obraSocial.osActual = mQuotation.desregulado.conyuge.osDeregulado;
            conyugeData.obraSocial.osQuotation = mQuotation.desregulado.conyuge.osDeregulado;

            if (mQuotation.desregulado.conyuge.aportaMonotributo) {
                conyugeData.osMonotributo = new ObraSocial();
                // put the sames as desregulado option
                conyugeData.osMonotributo.osQuotation = mQuotation.desregulado.conyuge.osDeregulado;
            }

        } else if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
            conyugeData.osMonotributo = new ObraSocial();
        }

        mAffiliationCard.conyugeData = conyugeData;
    }

    private void fillConyugeDataForMonotributo() {
        ConyugeData conyugeData = new ConyugeData();
        conyugeData.segmento = mQuotation.monotributo.conyuge.segmento;

        if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
            conyugeData.obraSocial = new ObraSocial();
            conyugeData.obraSocial.osActual = mQuotation.monotributo.conyuge.osDeregulado;
            conyugeData.obraSocial.osQuotation = mQuotation.monotributo.conyuge.osDeregulado;

            if (mQuotation.monotributo.conyuge.aportaMonotributo) {
                conyugeData.osMonotributo = new ObraSocial();
                // put the sames as desregulado option
                conyugeData.osMonotributo.osQuotation = mQuotation.monotributo.conyuge.osDeregulado;
            }

        } else if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
            conyugeData.osMonotributo = new ObraSocial();
        }
        mAffiliationCard.conyugeData = conyugeData;
    }


    private void checkEmptyAdditionalData(Long cardId, TitularData tData, ar.com.sancorsalud.asociados.model.affiliation.Member conyugeMember) {

        Log.e(TAG, "checkEmptyAdditionalData ....");

        ConstantsUtil.Segmento seg = tData.getSegmento();
        ConstantsUtil.FormaIngreso ingreso = tData.getFormaIngreso();

        if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {

            AdditionalData1AutonomoIndividual additionalData1 = (AdditionalData1AutonomoIndividual) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.cardType = mQuotation.pago.tarjeta;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
                additionalData1.pago.bankEmiter = mQuotation.pago.banco;
                additionalData1.pago.bankCBU = mQuotation.pago.banco;
            }

        } else if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {

            AdditionalData1AutonomoEmpresa additionalData1 = (AdditionalData1AutonomoEmpresa) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            }

        } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {

            AdditionalData1DesreguladoIndividual additionalData1 = (AdditionalData1DesreguladoIndividual) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.cardType = mQuotation.pago.tarjeta;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
                additionalData1.pago.bankEmiter = mQuotation.pago.banco;
                additionalData1.pago.bankCBU = mQuotation.pago.banco;
            }

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity

            AdditionalData2DesreguladoIndividual additionalData2 = (AdditionalData2DesreguladoIndividual) mAffiliationCard.additionalData2;
            if (additionalData2.obraSocial == null) {
                if (mQuotation.desregulado != null && mQuotation.desregulado.osDeregulado != null) {
                    Log.e(TAG, "Null OS getting form QP ....");

                    additionalData2.obraSocial = new ObraSocial();
                    additionalData2.obraSocial.personCardId = tData.personCardId;
                    additionalData2.obraSocial.osActual = mQuotation.desregulado.osDeregulado;
                    additionalData2.obraSocial.osQuotation = mQuotation.desregulado.osDeregulado;
                }
            }

            AdditionalData3DesreguladoIndividual additionalData3 = (AdditionalData3DesreguladoIndividual) mAffiliationCard.additionalData3;
            // check titular aporta monotributo
            if (additionalData3.osMonotributo == null) {
                // try to get form quotation parameters if usr not already filled in ficha guardar
                if (mQuotation.desregulado.aportaMonotributo != null && mQuotation.desregulado.aportaMonotributo) {
                    additionalData3.osMonotributo = new ObraSocial();
                }
            }

            // Check empty Conyuge DATA
            if (mQuotation.unificaAportes) {
                checkConyugeDataForDesregulado();
            }


        } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {

            AdditionalData1DesreguladoEmpresa additionalData1 = (AdditionalData1DesreguladoEmpresa) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            }

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity

            AdditionalData2DesreguladoEmpresa additionalData2 = (AdditionalData2DesreguladoEmpresa) mAffiliationCard.additionalData2;
            if (additionalData2.obraSocial == null) {
                if (mQuotation.desregulado != null && mQuotation.desregulado.osDeregulado != null) {
                    Log.e(TAG, "Null OS getting form QP ....");

                    additionalData2.obraSocial = new ObraSocial();
                    additionalData2.obraSocial.personCardId = tData.personCardId;
                    additionalData2.obraSocial.osActual = mQuotation.desregulado.osDeregulado;
                    additionalData2.obraSocial.osQuotation = mQuotation.desregulado.osDeregulado;
                }
            }

            AdditionalData3DesreguladoEmpresa additionalData3 = (AdditionalData3DesreguladoEmpresa) mAffiliationCard.additionalData3;
            // check titular aporta monotributo
            if (additionalData3.osMonotributo == null) {
                // try to get form quotation parameters
                if (mQuotation.desregulado.aportaMonotributo != null && mQuotation.desregulado.aportaMonotributo) {
                    additionalData3.osMonotributo = new ObraSocial();
                }
            }

            // Check empty Conyuge DATA
            if (mQuotation.unificaAportes) {
                checkConyugeDataForDesregulado();
            }

        } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {

            AdditionalData1MonotributoIndividual additionalData1 = (AdditionalData1MonotributoIndividual) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.cardType = mQuotation.pago.tarjeta;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
                additionalData1.pago.bankEmiter = mQuotation.pago.banco;
                additionalData1.pago.bankCBU = mQuotation.pago.banco;
            }

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity

            AdditionalData2MonotributoIndividual additionalData2 = (AdditionalData2MonotributoIndividual) mAffiliationCard.additionalData2;
            if (additionalData2.obraSocial == null) {
                additionalData2.obraSocial = new ObraSocial();
                additionalData2.obraSocial.personCardId = tData.personCardId;
            }

            // Check empty Conyuge DATA
            if (mQuotation.unificaAportes) {
                checkConyugeDataForMonotributo();
            }

        } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {

            AdditionalData1MonotributoEmpresa additionalData1 = (AdditionalData1MonotributoEmpresa) mAffiliationCard.additionalData1;
            if (additionalData1.pago == null) {
                Log.e(TAG, "Null Pago getting form QP ....");

                additionalData1.pago = new Pago();
                additionalData1.pago.cardId = cardId;
                additionalData1.pago.formaPago = mQuotation.pago.formaPago;
            }

            // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity

            AdditionalData2MonotributoEmpresa additionalData2 = (AdditionalData2MonotributoEmpresa) mAffiliationCard.additionalData2;
            if (additionalData2.obraSocial == null) {
                additionalData2.obraSocial = new ObraSocial();
                additionalData2.obraSocial.personCardId = tData.personCardId;
            }

            if (mQuotation.unificaAportes) {
                checkConyugeDataForMonotributo();
            }
        }
    }


    private void checkConyugeDataForDesregulado() {

        ConyugeData conyugeData = mAffiliationCard.conyugeData;
        if (conyugeData.segmento == null) {
            conyugeData.segmento = mQuotation.desregulado.conyuge.segmento;
        }

        if (conyugeData.segmento != null) {
            if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
                if (conyugeData.obraSocial == null) {
                    Log.e(TAG, "Null CONYUGE OS getting form QP ....");

                    conyugeData.obraSocial = new ObraSocial();
                    conyugeData.obraSocial.osActual = mQuotation.desregulado.conyuge.osDeregulado;
                    conyugeData.obraSocial.osQuotation = mQuotation.desregulado.conyuge.osDeregulado;
                }

                if (conyugeData.osMonotributo == null && mQuotation.desregulado.conyuge.aportaMonotributo) {
                    Log.e(TAG, "Null CONYUGE MONOTRIB getting form QP ....");

                    conyugeData.osMonotributo = new ObraSocial();
                    conyugeData.osMonotributo.osQuotation = mQuotation.desregulado.conyuge.osDeregulado;
                }

            } else if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
                if (conyugeData.osMonotributo == null) {
                    conyugeData.osMonotributo = new ObraSocial();
                }
            }
        }
    }

    private void checkConyugeDataForMonotributo() {

        ConyugeData conyugeData = mAffiliationCard.conyugeData;
        if (conyugeData.segmento == null) {
            conyugeData.segmento = mQuotation.monotributo.conyuge.segmento;
        }

        if (conyugeData.segmento != null) {
            if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
                if (conyugeData.obraSocial == null) {
                    Log.e(TAG, "Null CONYUGE OS getting form QP ....");

                    conyugeData.obraSocial = new ObraSocial();
                    conyugeData.obraSocial.osActual = mQuotation.monotributo.conyuge.osDeregulado;
                    conyugeData.obraSocial.osQuotation = mQuotation.monotributo.conyuge.osDeregulado;
                }

                if (conyugeData.osMonotributo == null && mQuotation.monotributo.conyuge.aportaMonotributo) {
                    Log.e(TAG, "Null CONYUGE MONO getting form QP ....");

                    conyugeData.osMonotributo = new ObraSocial();
                    conyugeData.osMonotributo.osQuotation = mQuotation.monotributo.conyuge.osDeregulado;
                }

            } else if (conyugeData.segmento.id.equalsIgnoreCase(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
                if (conyugeData.osMonotributo == null) {
                    conyugeData.osMonotributo = new ObraSocial();
                }
            }
        }
    }


    // --- FILES ------------------------------------------------------------------------- //

    // Thread to load all physicall files DES, Anexo and ATTACHS secuencially
    public void fillAllPhysicalFiles() {

        Log.e (TAG, "fillAllPhysicalFiles .....");

        showProgressDialog(R.string.des_loadind_file);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll DES PhysicalFiles ....");
                fillDESFiles();
            }
        }).start();
    }

    public void fillDESFiles() {

        if (!mDes.desFiles.isEmpty()) {

            Log.e(TAG, "fillDESFiles !!....");
            final AttachFile desFile = mDes.desFiles.remove(0);

            Log.e (TAG, "desFile.id: " + desFile.id);


            if (desFile.fileNameAndExtension == null || desFile.fileNameAndExtension.isEmpty()) {
                CardController.getInstance().getFile(getApplicationContext(), desFilesSubDir, desFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        Log.e(TAG, "ok getting DES File !!!!");

                        if (resultFile != null) {

                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpDesFiles.add(resultFile);
                            fillDESFiles();

                        } else {
                            Log.e(TAG, "Null DES file ....");
                            tmpDesFiles.add(desFile);
                            fillDESFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpDesFiles.add(desFile);
                        fillDESFiles();
                    }
                });
            } else {
                tmpDesFiles.add(desFile);
                fillDESFiles();
            }
        } else {

            // recreate desFiles
            mDes.desFiles.addAll(tmpDesFiles);
            tmpDesFiles = new ArrayList<AttachFile>();

            fillHealthCertFiles();
        }
    }

    public void fillHealthCertFiles() {

        if (!mDes.healthCertFiles.isEmpty()) {

            Log.e(TAG, "fillHealthCertFiles !!....");
            final AttachFile healthCertFile = mDes.healthCertFiles.remove(0);
            Log.e (TAG, "healthCertFile.id: " + healthCertFile.id);

            if (healthCertFile.fileNameAndExtension == null || healthCertFile.fileNameAndExtension.isEmpty()) {
                CardController.getInstance().getFile(getApplicationContext(), healthCertFilesSubDir, healthCertFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        Log.e(TAG, "ok getting healthCert File !!!!");

                        if (resultFile != null) {

                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpHealthCertFiles.add(resultFile);
                            fillHealthCertFiles();

                        } else {
                            Log.e(TAG, "Null HEALTH Cert file ....");
                            tmpHealthCertFiles.add(healthCertFile);
                            fillHealthCertFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpHealthCertFiles.add(healthCertFile);
                        fillHealthCertFiles();
                    }
                });
            } else {
                tmpHealthCertFiles.add(healthCertFile);
                fillHealthCertFiles();
            }
        } else {

            // recreate desFiles
            mDes.healthCertFiles.addAll(tmpHealthCertFiles);
            tmpHealthCertFiles = new ArrayList<AttachFile>();

            fillAttachsFiles();
        }
    }

    public void fillAttachsFiles() {

        if (!mDes.attachsFiles.isEmpty()) {

            Log.e(TAG, "fillAttachsFiles ....");
            final AttachFile attachFile = mDes.attachsFiles.remove(0);

            if (attachFile.fileNameAndExtension == null || attachFile.fileNameAndExtension.isEmpty()) {
                CardController.getInstance().getFile(getApplicationContext(), attachsFilesSubDir, attachFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resFile) {
                        Log.e(TAG, "ok getting Attach File !!!!");

                        if (resFile != null) {

                            Log.e(TAG, "nameAndExtendion:" + resFile.fileNameAndExtension);
                            Log.e(TAG, "path:" + resFile.filePath);

                            tmpAttachsFiles.add(resFile);
                            fillAttachsFiles();

                        } else {
                            Log.e(TAG, "Null Attach file .... ....");
                            tmpAttachsFiles.add(attachFile);
                            fillAttachsFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpAttachsFiles.add(attachFile);
                        fillAttachsFiles();
                    }
                });
            } else {
                tmpAttachsFiles.add(attachFile);
                fillAttachsFiles();
            }
        } else {
            // recreate attachFils
            mDes.attachsFiles.addAll(tmpAttachsFiles);
            tmpAttachsFiles = new ArrayList<AttachFile>();
            fillAnexoFile();
        }
    }

    public void fillAnexoFile() {

        if (!mDes.anexoFiles.isEmpty()) {
            Log.e(TAG, "fillAnexoFiles ....");
            final AttachFile anexoFile = mDes.anexoFiles.remove(0);

            if (anexoFile.fileNameAndExtension == null || anexoFile.fileNameAndExtension.isEmpty()) {
                CardController.getInstance().getFile(getApplicationContext(), anexoFilesSubDir, anexoFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        Log.e(TAG, "ok getting Anexo File !!!!");

                        if (resultFile != null) {

                            Log.e(TAG, "nameAndExtendion:" + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:" + resultFile.filePath);

                            tmpAnexoFiles.add(resultFile);
                            fillAnexoFile();

                        } else {
                            Log.e(TAG, "Null Anexo file ....");
                            tmpAnexoFiles.add(anexoFile);
                            fillAnexoFile();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpAnexoFiles.add(anexoFile);
                        fillAnexoFile();
                    }
                });
            } else {
                tmpAnexoFiles.add(anexoFile);
                fillAnexoFile();
            }
        } else {
            // recreate attachFils
            mDes.anexoFiles.addAll(tmpAnexoFiles);
            tmpAttachsFiles = new ArrayList<AttachFile>();

            // ON END SECUENCE
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();

                    // TODO nuevo
                    if (mDes.details.isEmpty()) {
                        fillDesDetailsFormQuotationParameters();
                    }

                    toDes();
                }
            });
        }
    }

    private void showCalendar() {
        Date date = null;
        String data = mFechaCargadEditText.getText().toString().trim();
        if (data.isEmpty()) {
            date = new Date();
        } else {
            try {
                date = mDateFormat.parse(data);
            } catch (Exception e) {
                date = new Date();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
        dateDialog.show();
    }
}
