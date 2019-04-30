package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.NotificationReadDetailActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.RequotationDetailAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.Recotizacion;
import ar.com.sancorsalud.asociados.model.affiliation.TicketPago;
import ar.com.sancorsalud.asociados.model.affiliation.VerifyAuthorizationCardResponse;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.ParseNumber;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AffiliationPlanDataFragment extends BaseFragment {

    private static final String TAG = "AF_PLAN_FRG";

    private static final String ARG_FICHA = "ficha";
    private static final String ARG_FECHA_INICO_SERV = "fechaIniServ";
    private static final String ARG_TICKET_PAGO = "ticketPago";
    private static final String ARG_PLAN = "plan";

    private static final String TICKET_PAGO_FILE_PREFIX = "ticketPago";
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    private View mMainContainer;
    private EditText mDateEditText;
    private View mDateButton;
    private Button mQuotationButton;

    private View mDetailContainer;

    private TextView mPlanDescTxt;
    private TextView mPlanDiftxt;

    private TextView mPlanDesc2Txt;
    private TextView mPlanValueTxt;

    private LinearLayout aproveBox;
    private Button mAproveButton;

    private ProgressBar mProgressView;
    private RequotationDetailAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private long mCardId;
    private Recotizacion mRecotizacion;
    private SimpleDateFormat mDateFormat;
    private Date mDate;
    private String mFechaIniServ;

    // TICKET PAGO
    private LinearLayout mTicketSection;
    private EditText mAltaTypeEditText;
    private TextInputLayout mAltaTypeTextInputLayout;
    private SpinnerDropDownAdapter mAltaTypeAlertAdapter;
    private int mSelectedAltaType = -1;
    private ArrayList<QuoteOption> mAltaTypes;

    private EditText mValorPlanEditText;
    private EditText mNroTicketEditText;
    private EditText mFechaPagoEditText;
    private EditText mImporteAbonadoEditText;

    private EditText mNroDesEditText;

    // TICKET PAGO  Files
    private RecyclerView mTicketPagoFrontRecyclerView;
    private AttachFilesAdapter mTicketPagoFileAdapter;
    private Button addTicketPagoButton;

    private TextView mPagoPlanDescTextView;
    private TextView mPagoPlanValueTextView;
    private Button mUpdateTicketPagoButton;

    private TicketPago mTicketPago;
    private Plan mPlan;
    public List<AttachFile> tmpTicketPagoFiles = new ArrayList<AttachFile>();

    private View view;
    private boolean isValidToAprove = false;
    private boolean editableDate = false;
    private boolean editableCard = true;

    private boolean hasUpdateTicketPago = false;



    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //String out = new StringBuilder().append(String.format("%02d", dayOfMonth)).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(year).toString();
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "Validity Date: " + out);

            Date today = new Date();
            try {
                Date date = ParserUtils.parseDate(out, DATE_FORMAT);

                //mDateEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));
                //mDate = date;

                if (date.before(today)) {
                    mDateEditText.setText(ParserUtils.parseDate(today, DATE_FORMAT));
                    mDate = today;
                } else {
                    mDateEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));
                    mDate = date;
                }


            } catch (Exception e) {
                mDateEditText.setText(ParserUtils.parseDate(today, DATE_FORMAT));
                mDate = today;
            }
            mDateEditText.requestFocus();
        }
    };

    private DatePickerDialog.OnDateSetListener payDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "Pay Date: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                mFechaPagoEditText.setText(mDateFormat.format(date));

            } catch (Exception e) {
                mFechaPagoEditText.setText(mDateFormat.format(today));
            }
            mFechaPagoEditText.requestFocus();
        }
    };

    public static AffiliationPlanDataFragment newInstance(long cardId, String fechaInicioServicio, TicketPago ticketPago, Plan plan) {
        AffiliationPlanDataFragment fragment = new AffiliationPlanDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FICHA, cardId);
        args.putString(ARG_FECHA_INICO_SERV, fechaInicioServicio);
        args.putSerializable(ARG_TICKET_PAGO, ticketPago);
        args.putSerializable(ARG_PLAN, plan);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mCardId = (long) getArguments().getSerializable(ARG_FICHA);
            mFechaIniServ = getArguments().getString(ARG_FECHA_INICO_SERV);
            mTicketPago = (TicketPago) getArguments().getSerializable(ARG_TICKET_PAGO);
            mPlan = (Plan) getArguments().getSerializable(ARG_PLAN);
            Log.e(TAG, "Plan Value: " + mPlan.valor);

            mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_plan_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        attachFilesSubDir = "/ticketPago/" + mCardId;
        mImageProvider = new ImageProvider(getActivity());

        mDateEditText = (EditText) view.findViewById(R.id.date_input);
        mDateButton = view.findViewById(R.id.date_button);
        mQuotationButton = (Button) view.findViewById(R.id.quotation_button);

        mDetailContainer = view.findViewById(R.id.detail_container);

        mPlanDescTxt = (TextView) view.findViewById(R.id.plan_desc);
        mPlanDiftxt = (TextView) view.findViewById(R.id.plan_dif);

        mPlanDesc2Txt = (TextView) view.findViewById(R.id.plan_desc2);
        mPlanValueTxt = (TextView) view.findViewById(R.id.plan_value);

        aproveBox = (LinearLayout) view.findViewById(R.id.aprove_buton_box);
        mAproveButton = (Button) view.findViewById(R.id.aprove_button);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.plan_details_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressView = (ProgressBar) view.findViewById(R.id.progress);
        this.view = view;

        ScrollView mScrollView = (ScrollView) view.findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        mAltaTypes = new ArrayList<>();
        QuoteOption altaTypeSelection = new QuoteOption("-1", getResources().getString(R.string.field_alta_type));
        mAltaTypes.add(altaTypeSelection);
        mAltaTypes.addAll(QuoteOptionsController.getInstance().getAltaTypes());

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        // TICKET PAGO
        mTicketSection = (LinearLayout) mMainContainer.findViewById(R.id.ticket_section);
        mAltaTypeEditText = (EditText) mMainContainer.findViewById(R.id.alta_type_input);
        mAltaTypeTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.alta_type_wrapper);

        mValorPlanEditText = (EditText) mMainContainer.findViewById(R.id.ticket_plan_value);
        mNroTicketEditText = (EditText) mMainContainer.findViewById(R.id.ticket_number);
        mFechaPagoEditText = (EditText) mMainContainer.findViewById(R.id.pago_validity_date_input);
        mImporteAbonadoEditText = (EditText) mMainContainer.findViewById(R.id.ticket_importe);
        mImporteAbonadoEditText.setEnabled(false);

        mNroDesEditText = (EditText) mMainContainer.findViewById(R.id.des_number);

        mPagoPlanDescTextView = (TextView) mMainContainer.findViewById(R.id.pago_plan_desc);
        mPagoPlanValueTextView = (TextView) mMainContainer.findViewById(R.id.pago_plan_value);

        addTicketPagoButton = (Button) view.findViewById(R.id.ticket_button);
        mUpdateTicketPagoButton = (Button) mMainContainer.findViewById(R.id.update_ticket_button);
        mTicketPagoFileAdapter = new AttachFilesAdapter(new ArrayList<AttachFile>(), true);

        checkEditableCardMode();
        verifyAltaInmediata();
    }

    public boolean isValidSection() {
        return validateForm();
    }


    @Override
    public void updateFileList(final AttachFile attachFile) {
        if (mTicketPago != null && attachFileType.equals(TICKET_PAGO_FILE_PREFIX)) {
            mTicketPagoFileAdapter.addItem(attachFile);
            mTicketPago.ticketPagoFiles = mTicketPagoFileAdapter.getAttachFiles();
        }
    }

    @Override
    public void onRemovedFile(int position) {
        try {
            if (mTicketPago != null && attachFileType.equals(TICKET_PAGO_FILE_PREFIX)) {
                mTicketPagoFileAdapter.removeItem(position);
            }
        } catch (Throwable e) {
        }
    }

    // --- helper methods ---------------------------------------------------- //

    private boolean validateForm() {
        return true;
    }

    private void checkEditableCardMode() {
        if (!editableCard) {
            disableView(mAproveButton);
            disableView(addTicketPagoButton);
        }

        mValorPlanEditText.setFocusable(editableCard);
        mPagoPlanValueTextView.setFocusable(editableCard);

        mNroTicketEditText.setFocusable(editableCard);
        mNroDesEditText.setFocusable(editableCard);
    }


    private void verifyAltaInmediata() {
        Log.e(TAG, "verifyAltaInmediata -----");

        showProgress(true);
        HRequest request = RestApiServices.verifyAltaInmediataRequest(mCardId, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                showProgress(false);

                if (response != null) {
                    if (response) {
                        Log.e(TAG, " ok verifyAltaInmediata -----");
                        editableDate = true;
                        onVerifyAltaInmediata();
                    } else {
                        editableDate = false;
                        onVerifyAltaInmediata();
                    }
                } else {
                    SnackBarHelper.makeError(mAproveButton, R.string.error_verify_alta_inmed_request).show();
                    editableDate = false;
                    onVerifyAltaInmediata();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                editableDate = false;
                onVerifyAltaInmediata();

                DialogHelper.showStandardErrorMessage(getActivity(), null);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    private void onVerifyAltaInmediata(){
        initializeForm();
        setupListeners();
    }

    private void initializeForm() {
        refreshContent();
    }

    private void refreshContent() {
        Log.e(TAG, "refresh content....");

        mPlanDescTxt.setText("");
        mPlanValueTxt.setText("");

        if (mRecotizacion != null) {

            // on re quotation values loaded
            aproveBox.setVisibility(View.VISIBLE);
            mDetailContainer.setVisibility(View.VISIBLE);

            // aprobe button will be render if all ticket data is filled
            mAproveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mAproveButton.setEnabled(true);

            mPlanDescTxt.setText(mRecotizacion.descripcionConcepto);
            mPlanDiftxt.setText("$" + mRecotizacion.diferencia_a_pagar);

            mPlanDesc2Txt.setText(mRecotizacion.descripcionConcepto);
            mPlanValueTxt.setText("$" + mRecotizacion.valor);

            mAdapter = new RequotationDetailAdapter(mRecotizacion.detalle);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
            mAdapter.notifyDataSetChanged();

            checkShowTicketPago();

        } else {
            // on load Activity
            aproveBox.setVisibility(View.GONE);
            mDetailContainer.setVisibility(View.GONE);

            //mAproveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDarkGrey));
            //mAproveButton.setEnabled(false);

            if (mFechaIniServ != null && !mFechaIniServ.isEmpty()) {
                mDateEditText.setText(mFechaIniServ);
            } else {
                mDateEditText.setText(ParserUtils.parseDate(new Date(), DATE_FORMAT));
            }
            mDate = ParserUtils.parseDate(mDateEditText.getText().toString(), DATE_FORMAT);
        }
    }

    private void checkShowTicketPago() {

        Log.e(TAG, "checkShowTicketPago -----------------");

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgress(true);
            CardController.getInstance().verifyTicketPagoAnticipado(mCardId, new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean dataResult) {
                    if (dataResult != null) {
                        Log.e(TAG, "verifyTicketPagoAnticipado: " + dataResult);
                        showProgress(false);

                        if (dataResult) {
                            mTicketSection.setVisibility(View.VISIBLE);
                            if (mTicketPago == null) {
                                mTicketPago = new TicketPago();
                            }
                            mTicketPago.importe = mRecotizacion.diferencia_a_pagar;
                            fillTicketPagoFiles();

                        } else {
                            // No ticket section available so render aprove button
                            isValidToAprove = true;
                            mTicketSection.setVisibility(View.GONE);
                        }

                    } else {
                        showProgress(false);
                        SnackBarHelper.makeError(mMainContainer, R.string.error_verify_ticket_pago).show();
                        mTicketSection.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "verifyTicketPagoAnticipado error .....");
                    SnackBarHelper.makeError(mMainContainer, R.string.error_verify_ticket_pago).show();
                    mTicketSection.setVisibility(View.GONE);
                }
            });
        } else {
            SnackBarHelper.makeError(mMainContainer, R.string.no_internet_coneccion).show();
            mTicketSection.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        setupImageProvider();

        if (editableCard) {

            if (editableDate) {
                mDateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard(v);
                        showCalendar(mDate, dateSetListener);
                    }
                });
            }

            addTicketPagoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = TICKET_PAGO_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + TICKET_PAGO_FILE_PREFIX, TICKET_PAGO_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            View altaTypeButton = mMainContainer.findViewById(R.id.alta_type_button);
            altaTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showAltaTypeAlert();
                }
            });

            altaTypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedAltaType = -1;
                    mAltaTypeEditText.setText("");
                    return true;
                }
            });

            View payDateButton = mMainContainer.findViewById(R.id.pago_validity_date_button);
            payDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar(mFechaPagoEditText.getText().toString().trim(), payDateListener);
                }
            });


            mValorPlanEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!mValorPlanEditText.getText().toString().trim().isEmpty()) {
                        mPagoPlanValueTextView.setText("$" + mValorPlanEditText.getText().toString().trim());
                    } else {
                        mPagoPlanValueTextView.setText("");
                    }
                }
            });

            mTicketPagoFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "ticketPagoFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = TICKET_PAGO_FILE_PREFIX;
                    AttachFile ticketPagoFile = mTicketPago.ticketPagoFiles.get(position);
                    if (ticketPagoFile.id != -1) {
                        removeFile(ticketPagoFile, position);
                    }
                }
            });

            mUpdateTicketPagoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "mUpdateTicketPagoButton !!!!");
                    validateTicketPago();
                    if (isValidToAprove) {
                        updateTicketPago();
                    }else{
                        SnackBarHelper.makeError(mPagoPlanValueTextView, R.string.error_affiliation_mandatory_ticket_pago_data).show();
                    }
                }
            });

            mQuotationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "toQuotation...");
                    quotAction();
                }
            });

            mAproveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTicketSection.getVisibility() == View.VISIBLE) {
                        validateTicketPago();
                        if (hasUpdateTicketPago) {  //Always have to update ticketPago before call aprobe action
                            onAproveWithTicketPagoAction();
                        }else{
                            SnackBarHelper.makeError(mPagoPlanValueTextView, R.string.error_affiliation_update_ticket_pago_data).show();
                        }
                    }
                    else {
                        Log.e(TAG, "toAprove....");
                        onAproveAction();
                    }
                }
            });
        }

        mTicketPagoFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile ticketPagoFile = mTicketPago.ticketPagoFiles.get(position);
                Log.e(TAG, "ticketPagoFile path:" + ticketPagoFile.filePath);
                loadFile(ticketPagoFile);
            }
        });
    }

    private void updateTicketPago(){

        // update AffiliationCard
        ((AffiliationActivity) getActivity()).silenceUpdateAffiliationData(mRecotizacion, mDate, mTicketPago, new UpdateServiceCallback() {
            @Override
            public void onSuccesUpdatedData() {
                Log.e(TAG, "onSuccesUpdatedData--------");
                SnackBarHelper.makeSucessful(mUpdateTicketPagoButton, R.string.success_affiliation_request).show();
                aproveBox.setVisibility(View.VISIBLE);
                hasUpdateTicketPago = true;
            }

            @Override
            public void onErrorUpdatedData() {
                showProgress(false);
                Log.e(TAG, "onErrorUpdatedData--------");
                SnackBarHelper.makeError(mUpdateTicketPagoButton, R.string.error_affiliation_ticket_pago_request).show();
                aproveBox.setVisibility(View.GONE);
                hasUpdateTicketPago = false;
            }
        });
    }

    private void quotAction(){
        showProgress(true);
        HRequest request = RestApiServices.createRecotizacionRequest(mCardId, mDate, new Response.Listener<Recotizacion>() {
            @Override
            public void onResponse(Recotizacion response) {
                showProgress(false);
                mRecotizacion = response;
                refreshContent();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRecotizacion = null;
                showProgress(false);
                SnackBarHelper.makeError(mAproveButton, R.string.error_affiliation_quot_update_request).show();
                refreshContent();
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request, true, RestConstants.QUOTATION_TIMEOUT_MS);
    }

    private void onAproveWithTicketPagoAction(){
        verifyAuthorizationCard();
    }

    private void onAproveAction(){
        showProgress(true);
        // update card fecha ingreso
        ((AffiliationActivity) getActivity()).silenceUpdateAffiliationData(mRecotizacion, mDate, new UpdateServiceCallback() {
            @Override
            public void onSuccesUpdatedData() {
                Log.e(TAG, "onSuccesUpdatedData--------");
                verifyAuthorizationCard();
            }

            @Override
            public void onErrorUpdatedData() {
                showProgress(false);
                Log.e(TAG, "onErrorUpdatedData--------");
                SnackBarHelper.makeError(mAproveButton, R.string.error_affiliation_request).show();
            }
        });

        Log.e(TAG, "onAproveAction OK !!!!");
    }

    private void verifyAuthorizationCard() {
        Log.e(TAG, "verifyAuthorizationCard -----");

        HRequest request = RestApiServices.verifyAuthorizationCardRequest(mCardId, new Response.Listener<VerifyAuthorizationCardResponse>() {
            @Override
            public void onResponse(VerifyAuthorizationCardResponse verifyResponse) {
                showProgress(false);

                if (verifyResponse != null) {

                    List<String> mssgs = verifyResponse.messages;
                    if (mssgs != null && mssgs.size() > 0){
                        String mssLog = "";
                        if (mssgs != null && !mssgs.isEmpty()) {
                            for (String mssg : mssgs) {
                                mssLog += mssg + "\n";
                            }
                            //Log.e(TAG, "mssLog: " + mssLog);
                            DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_verify_card_request), mssLog);
                        }
                    }else{
                        SnackBarHelper.makeSucessful(mAproveButton, R.string.affiliation_card_to_aprove).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((AffiliationActivity) getActivity()).toMain();
                            }
                        }, 2000L);
                    }

                } else {
                    SnackBarHelper.makeError(mAproveButton, R.string.error_verify_card_request).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_verify_card_request), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                showProgress(false);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    // TODO not used any more  backend change status internally when verify authorization
    /*
    private void changeCardStateRequest() {
        HRequest request = RestApiServices.createChangeCardStateRequest(mCardId, ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT, null, "comentario", new Response.Listener<ExistenceStatus>() {
            @Override
            public void onResponse(ExistenceStatus responseStatus) {
                SnackBarHelper.makeSucessful(mAproveButton, R.string.affiliation_card_to_aprove).show();
                showProgress(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((AffiliationActivity) getActivity()).toMain();
                    }
                }, 2000L);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.showStandardErrorMessage(getActivity(), null);
                showProgress(false);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }
    */


    private void fillTicketPagoFiles() {

        if (!mTicketPago.ticketPagoFiles.isEmpty()) {
            Log.e(TAG, "ticketPagoFiles !!....");

            final AttachFile ticketFile = mTicketPago.ticketPagoFiles.remove(0);
            if (ticketFile.fileNameAndExtension == null || ticketFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + TICKET_PAGO_FILE_PREFIX, ticketFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        if (resultFile != null) {
                            Log.e(TAG, "ok getting ticket Pago File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpTicketPagoFiles.add(resultFile);
                            fillTicketPagoFiles();

                        } else {
                            Log.e(TAG, "Error getting file ....");
                            tmpTicketPagoFiles.add(ticketFile);
                            fillTicketPagoFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpTicketPagoFiles.add(ticketFile);
                        fillTicketPagoFiles();
                    }
                });
            } else {
                tmpTicketPagoFiles.add(ticketFile);
                fillTicketPagoFiles();
            }
        } else {
            mTicketPago.ticketPagoFiles.addAll(tmpTicketPagoFiles);
            tmpTicketPagoFiles = new ArrayList<AttachFile>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initializeTicketPago();
                }
            });
        }
    }

    private void initializeTicketPago() {

        mPagoPlanDescTextView.setText(getResources().getString(R.string.affiliation_plan_data, mPlan.descripcionPlan));
        mPagoPlanValueTextView.setText("$");

        boolean isValidTicket = true;
        if (mTicketPago.ticketPagoFiles.size() == 0) {
            isValidTicket = false;
        }
        initTicketPagoFiles();

        if (mTicketPago != null) {
            int i = 0;
            if (mTicketPago.formaAlta != null) {
                for (QuoteOption q : mAltaTypes) {
                    if (mTicketPago.formaAlta.id.equalsIgnoreCase(q.id)) {
                        mSelectedAltaType = i;
                        mAltaTypeEditText.setText(q.optionName());
                    }
                    i++;
                }
            } else {
                isValidTicket = false;
            }

            if (mTicketPago.planValue == 0) {
                mValorPlanEditText.setText("");
                isValidTicket = false;
            } else {
                mValorPlanEditText.setText(String.format("%.2f", mTicketPago.planValue).replace(",", "."));
                mPagoPlanValueTextView.setText("$" + mValorPlanEditText.getText().toString().trim());
            }

            if (mTicketPago.ticketNumber != null) {
                mNroTicketEditText.setText(mTicketPago.ticketNumber);
            } else {
                isValidTicket = false;
            }

            if (mTicketPago.pagoDate != null) {
                mFechaPagoEditText.setText(mTicketPago.getPagoDate());
            } else {
                isValidTicket = false;
            }

            if (mRecotizacion.diferencia_a_pagar == 0) {
                mImporteAbonadoEditText.setText("");
                isValidTicket = false;
            } else {
/************************ Start MERGE (sprint-05-12-2018 --> development)  ************************/
                //mImporteAbonadoEditText.setText(String.format("%.2f", mRecotizacion.diferencia_a_pagar).replace(",", ".") );
                mImporteAbonadoEditText.setText("$" + String.format("%.2f", mTicketPago.importe).replace(",", ".") );
/************************* End MERGE (sprint-05-12-2018 --> development)  *************************/
            }

            if (mTicketPago.desNumber != null) {
                mNroDesEditText.setText(mTicketPago.desNumber);
            } else {
                isValidTicket = false;
            }

            if (isValidTicket) {
                isValidToAprove = true;
            } else {
                isValidToAprove = false;
            }
        }
    }

    private void initTicketPagoFiles() {
        mTicketPagoFileAdapter.setItems(mTicketPago.ticketPagoFiles);

        mTicketPagoFrontRecyclerView = (RecyclerView) view.findViewById(R.id.ticket_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mTicketPagoFrontRecyclerView.getContext());
        mTicketPagoFrontRecyclerView.setLayoutManager(attachLayoutManager);
        mTicketPagoFrontRecyclerView.setAdapter(mTicketPagoFileAdapter);
        mTicketPagoFrontRecyclerView.setHasFixedSize(true);
    }

    private void validateTicketPago(){
        isValidToAprove = true;

        if (mTicketPago == null) {
            mTicketPago = new TicketPago();
        }

        if (!mAltaTypeEditText.getText().toString().isEmpty() && mSelectedAltaType != -1) {
            mTicketPago.formaAlta = mAltaTypes.get(mSelectedAltaType);
        } else {
            mTicketPago.formaAlta = null;
            isValidToAprove = false;
        }

        if (!mValorPlanEditText.getText().toString().isEmpty()) {
            mTicketPago.planValue = ParseNumber.parseDouble(mValorPlanEditText.getText().toString());
        } else {
            mTicketPago.planValue = 0;
            isValidToAprove = false;
        }

        if (!mNroTicketEditText.getText().toString().isEmpty()) {
            mTicketPago.ticketNumber = mNroTicketEditText.getText().toString();
        } else {
            mTicketPago.ticketNumber = null;
            isValidToAprove = false;
        }

        if (!mFechaPagoEditText.getText().toString().isEmpty()) {
            try {
                mTicketPago.pagoDate = ParserUtils.parseDate(mFechaPagoEditText.getText().toString(), DATE_FORMAT);
            } catch (Exception e) {
                mTicketPago.pagoDate = null;
                isValidToAprove = false;
            }
        }

        if (!mImporteAbonadoEditText.getText().toString().isEmpty()) {
            //mTicketPago.importe = Double.parseDouble(mImporteAbonadoEditText.getText().toString());

            String importeStr = mImporteAbonadoEditText.getText().toString();
            String moneyChar = importeStr.substring(0,1);
            if (moneyChar.equals("$")){
                mTicketPago.importe = Double.parseDouble(importeStr.substring(1, importeStr.length()));
            } else {
                mTicketPago.importe = Double.parseDouble(importeStr);
            }
        }else{
            mTicketPago.importe = 0f;
            isValidToAprove = false;
        }

        if (!mNroDesEditText.getText().toString().isEmpty()) {
            mTicketPago.desNumber = mNroDesEditText.getText().toString();
        } else {
            mTicketPago.desNumber = null;
            isValidToAprove = false;
        }

        // UPDATE FILES
        if (mTicketPagoFileAdapter != null) {
            mTicketPago.ticketPagoFiles = mTicketPagoFileAdapter.getAttachFiles();
            if (mTicketPago.ticketPagoFiles.size() == 0){
                isValidToAprove = false;
            }
        }else{
            isValidToAprove = false;
        }
    }

    private void showAltaTypeAlert() {
        ArrayList<String> altaTypeStr = new ArrayList<String>();
        for (QuoteOption q : mAltaTypes) {
            altaTypeStr.add(q.optionName());
        }

        mAltaTypeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), altaTypeStr, mSelectedAltaType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mAltaTypeAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedAltaType = i;
                        if (i == 0){
                            mSelectedAltaType = -1;
                            mAltaTypeEditText.setText("");
                        }else {
                            QuoteOption opt = mAltaTypes.get(mSelectedAltaType);
                            mAltaTypeEditText.setText(opt.optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAltaTypeAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCalendar(Date date, DatePickerDialog.OnDateSetListener listener) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date == null ? today : date);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));

        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay);
        dateDialog.getDatePicker().setMinDate(today.getTime());
        dateDialog.show();
    }

    private void showCalendar(String data, DatePickerDialog.OnDateSetListener listener) {
        Date today = new Date();
        Date date = null;
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

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay);
        // must enable old dates
        //dateDialog.getDatePicker().setMinDate(today.getTime());
        dateDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 && getActivity() != null) {
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
        } catch (Exception e) {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
