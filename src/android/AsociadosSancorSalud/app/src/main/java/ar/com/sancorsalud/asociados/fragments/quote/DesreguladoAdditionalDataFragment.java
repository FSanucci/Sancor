package ar.com.sancorsalud.asociados.fragments.quote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.AportesAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerFamiliaresACargoAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.interfaces.QuoteListener;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.quotation.ConyugeQuotation;
import ar.com.sancorsalud.asociados.model.quotation.DesreguladoQuotation;
import ar.com.sancorsalud.asociados.model.FamiliarACargo;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class DesreguladoAdditionalDataFragment extends BaseFragment {

    private static final String TAG = "DESR_FRG";

    private static final String ARG_QUOTATION = "quotation";
    private static final String ARG_CONYUGE_QUOTATION = "conyQuotation";

    // Titular data
    private View mMainContainer;
    private Quotation mQuotation;
    private QuoteListener mCallback;

    private LinearLayout mMonoBox;
    private View mMonotibutoErrorView;
    private RadioButton mAportaMonotributoRadioButton;
    private RadioButton mNoAportaMonotributoRadioButton;

    // Titular Aportes monotributo
    private View mMonotributoImportantNote;
    private EditText mCantidadIntegrantesEditText;
    private TextInputLayout mCantidadIntegrantesTextInputLayout;

    // all familiares list
    private ArrayList<String> mFamiliares;

    // Titular Familiares
    private SpinnerFamiliaresACargoAdapter mTitularFamiliaresAlertAdapter;
    private ArrayList<Integer> mSelectedTitularFamiliares;
    private View mFliaTitularBox;
    private RelativeLayout fliaTitularButton;

    // Conyuge Data
    private Quotation mConyugeQuotation;

    private TextView mConyugeTextView;
    private EditText mConyugeDniEditText;
    private View mConyugeDniContainer;

    // Conyuge OS Desregula
    private RelativeLayout mConyugeOSDesregulaContainer;
    private EditText mConyugeOsDesregulaEditText;
    private SpinnerDropDownAdapter mConyugeOsDesregulaAlertAdapter;
    private int mSelectedConyugeOsDesregula = -1;

    // Conyuge Aportes legales
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mConyugeAportesRecyclerView;
    private AportesAdapter mConyugeAportesAdapter;
    private ArrayList<Aporte> mConyugeAportes;

    private RelativeLayout mConyugeOSButton;
    private Button mConyugeAddAporteButton;

    // Conyuge Monotributo
    private View mConyugeMonoBox;
    private View mConyugeMonotributoContainer;
    private View mConyugeMonotibutoErrorView;

    private RadioButton mConyugeAportaMonotributoRadioButton;
    private RadioButton mConyugeNoAportaMonotributoRadioButton;

    private TextInputLayout mConyugeCantidadIntegrantesTextInputLayout;
    private EditText mConyugeCantidadIntegrantesEditText;

    // Conyuge familares
    private SpinnerFamiliaresACargoAdapter mConyugeFamiliaresAlertAdapter;
    private ArrayList<Integer> mSelectedConyugeFamiliares;

    private View mFliaConyugeBox;
    private RelativeLayout mFliaConyugeButton;

    //private ArrayList<QuoteOption> mTiposUnificacion;
    //private ArrayList<QuoteOption> mAportantes;
    //private ArrayList<QuoteOption> mConyugueAportantes;
    private ArrayList<QuoteOption> mOSDesreguladas;

    private Member mConyuge;
    private boolean hasInvalidAssociatedConyuge = false;

    private EditText mManualPlanEditText;
    private EditText mManualPlanPriceText;

    public DesreguladoAdditionalDataFragment() {
        // Required empty public constructor
    }

    public static DesreguladoAdditionalDataFragment newInstance(Quotation param1, Quotation param2) {
        DesreguladoAdditionalDataFragment fragment = new DesreguladoAdditionalDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, param1);
        args.putSerializable(ARG_CONYUGE_QUOTATION, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
            mConyugeQuotation = (Quotation) getArguments().getSerializable(ARG_CONYUGE_QUOTATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_desregulado_aditional_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        mMonoBox = (LinearLayout) mMainContainer.findViewById(R.id.mono_box);

        mAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.si_aporta_mono);
        mNoAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.no_aporta_mono);
        mMonotibutoErrorView = mMainContainer.findViewById(R.id.aporta_monotributo_error);

        mCantidadIntegrantesEditText = (EditText) mMainContainer.findViewById(R.id.cantidad_integrante_input);
        mCantidadIntegrantesTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.cantidad_integrante_wrapper);
        mMonotributoImportantNote = mMainContainer.findViewById(R.id.field_aporte_imp_note);

        // tiular familiares
        mFliaTitularBox = mMainContainer.findViewById(R.id.familiares_titular_container);
        fliaTitularButton = (RelativeLayout) mMainContainer.findViewById(R.id.familiares_titular_button);

        mConyugeTextView = (TextView) mMainContainer.findViewById(R.id.conyuge_section_title);
        mConyugeDniContainer = mMainContainer.findViewById(R.id.conyuge_dni_wrapper);
        mConyugeDniEditText = (EditText) mMainContainer.findViewById(R.id.conyuge_dni_input);

        // Conyuge OS Desregula
        mConyugeOSDesregulaContainer = (RelativeLayout) mMainContainer.findViewById(R.id.conyuge_os_desregula_container);
        mConyugeOsDesregulaEditText = (EditText) mMainContainer.findViewById(R.id.conyuge_os_desregula_input);

        // Conyugue Aportes legales
        mConyugeOSButton = (RelativeLayout) mMainContainer.findViewById(R.id.conyuge_os_desregula_button);
        mConyugeAddAporteButton = (Button) mMainContainer.findViewById(R.id.conyuge_add_aporte_button);

        mConyugeAportesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mConyugeAportesRecyclerView.getContext());
        mConyugeAportesRecyclerView.setLayoutManager(mLinearLayoutManager);

        mConyugeAportesAdapter = new AportesAdapter();
        mConyugeAportes = new ArrayList<Aporte>();

        mConyugeAportesAdapter.setItems(mConyugeAportes);
        mConyugeAportesRecyclerView.setAdapter(mConyugeAportesAdapter);
        mConyugeAportesRecyclerView.setHasFixedSize(true);

        // Conyuge monotributo

        mConyugeMonoBox = (View) mMainContainer.findViewById(R.id.conyuge_mono_box);
        mConyugeMonotributoContainer = (View) mMainContainer.findViewById(R.id.conyuge_mono_container);

        mConyugeAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.conyuge_si_aporta_mono);
        mConyugeNoAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.conyuge_no_aporta_mono);
        mConyugeMonotibutoErrorView = mMainContainer.findViewById(R.id.conyuge_aporta_monotributo_error);

        mConyugeCantidadIntegrantesTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.conyuge_cantidad_integrante_wrapper);
        mConyugeCantidadIntegrantesEditText = (EditText) mMainContainer.findViewById(R.id.conyuge_cantidad_integrante_input);

        //mAportantes = QuoteOptionsController.getInstance().getAportantesGrupo();
        //mConyugueAportantes = QuoteOptionsController.getInstance().getAportantesGrupo();

        // Conyuge familiares
        mFliaConyugeBox = mMainContainer.findViewById(R.id.familiares_conyuge_container);
        mFliaConyugeButton = (RelativeLayout) mMainContainer.findViewById(R.id.familiares_conyuge_button);

        //mTiposUnificacion = QuoteOptionsController.getInstance().getTipoUnificacion();


        mOSDesreguladas = new ArrayList<>();
        QuoteOption osSelection = new QuoteOption("-1", getResources().getString(R.string.field_os_desregula));
        mOSDesreguladas.add(osSelection);
        // OS = f(staff, segment)
        mOSDesreguladas.addAll(QuoteOptionsController.getInstance().getOSDesreguladas());

        mManualPlanEditText = (EditText) mMainContainer.findViewById(R.id.manual_plan_input);
        mManualPlanPriceText = (EditText) mMainContainer.findViewById(R.id.manual_plan_price_input);


        initializeForm();
        setupListeners();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (QuoteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement QuoteListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult-------------");

        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_CODE) {
            Aporte aporte = (Aporte) data.getSerializableExtra(ConstantsUtil.RESULT_APORTE);
            if (aporte != null) {
                mConyugeAportesAdapter.addItem(aporte);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupListeners() {

        // titular
        mAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMonotibutoErrorView.setVisibility(View.GONE);

                // TODO check if conyuge has to select os by monotributo and set mMonotributoSegmentList
                showMonotributoExtraFields(true);
            }
        });

        mNoAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMonotibutoErrorView.setVisibility(View.GONE);
                showMonotributoExtraFields(false);
            }
        });

        fliaTitularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showTitularFamiliaresAlert();
            }
        });

        // Conyuge
        mConyugeOSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showOsDesregulaAlert();
            }
        });

        mConyugeOSButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedConyugeOsDesregula = -1;
                mConyugeOsDesregulaEditText.setText("");

                return true;
            }
        });

        mConyugeAddAporteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoAddAporte(DesreguladoAdditionalDataFragment.this);
            }
        });

        mConyugeAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConyugeMonotibutoErrorView.setVisibility(View.GONE);
                showConyugeMonotributoExtraFields(true);
            }
        });

        mConyugeNoAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConyugeMonotibutoErrorView.setVisibility(View.GONE);
                showConyugeMonotributoExtraFields(false);
            }
        });

        mFliaConyugeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showConyugeFamiliaresAlert();
            }
        });


        View seeQuote = mMainContainer.findViewById(R.id.see_quote_button);
        seeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (isValidSection()) {

                    mQuotation.desregulado = getDereguladoQuotation();
                    Log.e(TAG, "DESREG QUOTATIN READY OK ......");
                    mCallback.showQuotation(mQuotation);
                }
            }
        });

    }

    private void initializeForm() {

        if (mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) {
            mMainContainer.findViewById(R.id.manual_plan_input_wrapper).setVisibility(View.VISIBLE);
            mMainContainer.findViewById(R.id.manual_plan_price_wrapper).setVisibility(View.VISIBLE);

            if (mQuotation.manualPlanName != null)
                mManualPlanEditText.setText(mQuotation.manualPlanName);

            if (mQuotation.manualPlanPrice != null)
                mManualPlanPriceText.setText(Double.valueOf(mQuotation.manualPlanPrice).toString());
        } else {
            mMainContainer.findViewById(R.id.manual_plan_input_wrapper).setVisibility(View.GONE);
            mMainContainer.findViewById(R.id.manual_plan_price_wrapper).setVisibility(View.GONE);
        }

        if (mQuotation.desregulado == null)
            return;

        if (mQuotation.desregulado.osDeregulado == null)
            return;

        Log.e(TAG, "Titular Quotation desregula OS: type: " + mQuotation.desregulado.osDeregulado.extra + " *******************");
        if (mQuotation.desregulado.osDeregulado.extra.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
            mMonoBox.setVisibility(View.VISIBLE);
        } else {
            mMonoBox.setVisibility(View.GONE);
        }

        mAportaMonotributoRadioButton.setChecked(false);
        mNoAportaMonotributoRadioButton.setChecked(true);

        mMonotributoImportantNote.setVisibility(View.GONE);
        mCantidadIntegrantesTextInputLayout.setVisibility(View.GONE);
        mCantidadIntegrantesEditText.setText("");

        mConyugeAportaMonotributoRadioButton.setChecked(false);
        mConyugeNoAportaMonotributoRadioButton.setChecked(true);

        // get conyuge type of member
        mConyuge = mQuotation.getConyuge();
        if (mConyuge == null)
            mConyuge = mQuotation.getConcubino();

        // Nedd to have a conyuge to unificar data
        if (mConyuge != null) {
            mConyugeTextView.setText(getResources().getString(R.string.field_dato_conyuge, mConyuge.parentesco.title));
            mConyugeDniEditText.setText("" + ((mConyuge.dni != -1) ? mConyuge.dni : ""));
        }


        if (mQuotation.integrantes == null || mQuotation.integrantes.size() == 0) {
            mFliaTitularBox.setVisibility(View.GONE);
        } else {
            mFamiliares = new ArrayList<String>();
            for (Member q : mQuotation.integrantes) {
                if ((q.parentesco.id.equals(ConstantsUtil.MENOR_TUTELA_MEMBER)) || (q.parentesco.id.equals(ConstantsUtil.FAMILIAR_A_CARGO_MEMBER))) {
                    mFamiliares.add(q.parentesco.title + " (" + q.age + ")");
                }
            }
            if (mFamiliares.size() > 0) {
                mFliaTitularBox.setVisibility(View.VISIBLE);

                // find already assigned familiares  to titular
                if (mQuotation.titular != null && !mQuotation.titular.familiaresACargoList.isEmpty()) {
                    mSelectedTitularFamiliares = getSelectedFamiliaresIndex(mQuotation.titular);
                }
            } else {
                mFliaTitularBox.setVisibility(View.GONE);
            }
        }


        // Unification Data
        if (mQuotation.unificaAportes != null) {
            if (mQuotation.unificaAportes) {

                if (mQuotation.unificationType != -1) {
                    if (mQuotation.unificationType == 0) {
                        // Conyuge Nuevo
                        showConyugeData();
                    } else {
                        // Conyuge Asociado
                        hideConyugeSection();
                        fillConyugeQuotationData();
                    }
                }

            } else {
                hideConyugeSection();
            }
        }

        if (mMonoBox.getVisibility() == View.VISIBLE) {
            if (mQuotation.desregulado.aportaMonotributo != null && mQuotation.desregulado.aportaMonotributo) {
                mAportaMonotributoRadioButton.setChecked(true);
                mNoAportaMonotributoRadioButton.setChecked(false);

                mMonotributoImportantNote.setVisibility(View.VISIBLE);
                mCantidadIntegrantesTextInputLayout.setVisibility(View.VISIBLE);
                mCantidadIntegrantesEditText.setText("" + mQuotation.desregulado.nroAportantes);


            } else {
                mAportaMonotributoRadioButton.setChecked(false);
                mNoAportaMonotributoRadioButton.setChecked(true);
            }
        } else {
            mAportaMonotributoRadioButton.setChecked(false);
            mNoAportaMonotributoRadioButton.setChecked(true);
        }
    }

    public void updateConyugeAportes(ArrayList<Aporte> list) {
        mConyugeAportes = list;
        if (mConyugeAportes != null) {
            mConyugeAportesAdapter.setItems(mConyugeAportes);
        } else mConyugeAportesAdapter.removeAllItems();
    }


    public void setQuotation(Quotation q) {
        mQuotation = q;
        initializeForm();
    }

    public DesreguladoQuotation getDereguladoQuotation() {

        if (mQuotation.desregulado == null) {
            mQuotation.desregulado = new DesreguladoQuotation();
        }

        mQuotation.desregulado.aportaMonotributo = mAportaMonotributoRadioButton.isChecked();

        if (mQuotation.desregulado.aportaMonotributo) {
            if (!mCantidadIntegrantesEditText.getText().toString().isEmpty()) {
                mQuotation.desregulado.nroAportantes = Integer.parseInt(mCantidadIntegrantesEditText.getText().toString());
            }
        }

        // UNIFICACION
        mQuotation.desregulado.unificaAportes = mQuotation.unificaAportes;
        mQuotation.desregulado.titularMainAffilliation = mQuotation.titularMainAffilliation;
        mQuotation.desregulado.unificationType = mQuotation.unificationType;

        if (mQuotation.desregulado.unificaAportes != null && mQuotation.desregulado.unificaAportes) {

            if (mQuotation.desregulado.conyuge == null)
                mQuotation.desregulado.conyuge = new ConyugeQuotation();

            // last chance to update conyuge/concubino DNI
            if (mConyugeDniEditText.getText().toString().isEmpty()) {
                mConyuge.dni = -1;
            } else {
                mConyuge.dni = Long.parseLong(mConyugeDniEditText.getText().toString());
            }
            mQuotation.desregulado.conyuge.dni = mConyuge.dni;

            // Conyuge Data
            if (mQuotation.desregulado.unificationType != -1) {
                if (mQuotation.desregulado.unificationType == 0) { // Conyuge nuevo

                    mQuotation.desregulado.conyuge.afiliado = false;

                    // Conyuge Aporte Legales
                    if (mSelectedConyugeOsDesregula != -1) {
                        mQuotation.desregulado.conyuge.osDeregulado = mOSDesreguladas.get(mSelectedConyugeOsDesregula);
                    } else {
                        mQuotation.desregulado.conyuge.osDeregulado = null;
                    }
                    mQuotation.desregulado.conyuge.aportesLegales = mConyugeAportesAdapter.getAportes();

                    // Conyuge check aporta monotributo
                    mQuotation.desregulado.conyuge.aportaMonotributo = mConyugeAportaMonotributoRadioButton.isChecked();

                    if (mQuotation.desregulado.conyuge.aportaMonotributo) {
                        if (!mConyugeCantidadIntegrantesEditText.getText().toString().isEmpty()) {
                            mQuotation.desregulado.conyuge.nroAportantes = Integer.parseInt(mConyugeCantidadIntegrantesEditText.getText().toString());
                        }
                    }

                } else {
                    // Conyuge asociado
                    mQuotation.desregulado.conyuge.afiliado = true;
                }

                // Always Define conyuge segment
                if ((mQuotation.desregulado.conyuge.aportesLegales != null && !mQuotation.desregulado.conyuge.aportesLegales.isEmpty())) {
                    mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.DESREGULADO_SEGMENTO, null);
                } else {
                    /*
                    if (mSelectedConyugeAportantesGrupo != -1) {
                        mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.MONOTRIBUTO_SEGMENTO, null);
                    } else {
                        mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.ZERO_SEGMENTO, null);
                    }
                    */
                    if (mQuotation.desregulado.conyuge.nroAportantes > 0) {
                        mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.MONOTRIBUTO_SEGMENTO, null);
                    } else {
                        mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.ZERO_SEGMENTO, null);
                    }
                }

                /*
                if ((mQuotation.desregulado.conyuge.aportesLegales!= null  && !mQuotation.desregulado.conyuge.aportesLegales.isEmpty()) && (mSelectedConyugeAportantesGrupo != -1)) {
                     mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.MONOTRIBUTO_SEGMENTO, null);
                }else if ((mQuotation.desregulado.conyuge.aportesLegales!= null  && !mQuotation.desregulado.conyuge.aportesLegales.isEmpty()) && (mSelectedConyugeAportantesGrupo == -1)) {
                    mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.DESREGULADO_SEGMENTO, null);
                }else if (mSelectedConyugeAportantesGrupo != -1){
                    mQuotation.desregulado.conyuge.segmento = new QuoteOption(ConstantsUtil.MONOTRIBUTO_SEGMENTO, null);
                }
                */


            } else {
                resetConyugeData();
            }
        }

        if (mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) {
            // add manual quotation data
            mQuotation.manualPlanName = mManualPlanEditText.getText().toString().trim();

            if (!mManualPlanPriceText.getText().toString().trim().isEmpty()) {
                mQuotation.manualPlanPrice = Double.parseDouble(mManualPlanPriceText.getText().toString().trim());
            }
        }

        return mQuotation.desregulado;
    }

    private void showMonotributoExtraFields(boolean est) {

        mCantidadIntegrantesEditText.setText("");
        resetError(R.id.cantidad_integrante_wrapper);

        mMonotributoImportantNote.setVisibility(est ? View.VISIBLE : View.GONE);
        mCantidadIntegrantesTextInputLayout.setVisibility(est ? View.VISIBLE : View.GONE);

    }

    private void showConyugeMonotributoExtraFields(boolean est) {

        mConyugeCantidadIntegrantesEditText.setText("");
        resetError(R.id.conyuge_cantidad_integrante_wrapper);

        mConyugeMonotributoContainer.setVisibility(View.VISIBLE);
        mConyugeCantidadIntegrantesTextInputLayout.setVisibility(est ? View.VISIBLE : View.GONE);
    }

    private void hideConyugeSection() {

        mConyugeTextView.setVisibility(View.GONE);
        mConyugeDniEditText.setVisibility(View.GONE);
        mConyugeDniContainer.setVisibility(View.GONE);

        // conyuge aportes
        mConyugeOSDesregulaContainer.setVisibility(View.GONE);
        mConyugeAportesRecyclerView.setVisibility(View.GONE);
        mConyugeAddAporteButton.setVisibility(View.GONE);

        // conyugue monotributo
        mConyugeMonoBox.setVisibility(View.GONE);
        mConyugeMonotributoContainer.setVisibility(View.GONE);
        mConyugeCantidadIntegrantesTextInputLayout.setVisibility(View.GONE);

        // conyugue familiares
        mFliaConyugeBox.setVisibility(View.GONE);
        //mTitularMainAffiliationBox.setVisibility(View.GONE);
    }


    private void showConyugeData() {

        mConyugeTextView.setVisibility(View.VISIBLE);
        mConyugeDniEditText.setVisibility(View.VISIBLE);
        mConyugeDniContainer.setVisibility(View.VISIBLE);

        mConyugeOSDesregulaContainer.setVisibility(View.VISIBLE);
        mConyugeAportesRecyclerView.setVisibility(View.VISIBLE);
        mConyugeAddAporteButton.setVisibility(View.VISIBLE);

        mConyugeMonoBox.setVisibility(View.VISIBLE);
        mConyugeMonotributoContainer.setVisibility(View.VISIBLE);
        mConyugeCantidadIntegrantesTextInputLayout.setVisibility(View.GONE);

        // associated Conyuge data box
        //mConyugeAssociatedBox.setVisibility(View.GONE);
        mFliaConyugeBox.setVisibility(mFamiliares.isEmpty() ? View.GONE : View.VISIBLE);

        if (mQuotation.desregulado == null) {
            mQuotation.desregulado = new DesreguladoQuotation();
        }

        if (mQuotation.desregulado.conyuge == null) {
            mQuotation.desregulado.conyuge = new ConyugeQuotation();
        }

        // Conyugue aportes legales
        if (mQuotation.desregulado.conyuge.osDeregulado != null) {
            mSelectedConyugeOsDesregula = mOSDesreguladas.indexOf(mQuotation.desregulado.conyuge.osDeregulado);

            if (mSelectedConyugeOsDesregula != -1) {
                mConyugeOsDesregulaEditText.setText(mQuotation.desregulado.conyuge.osDeregulado.title);

                Log.e(TAG, "Conyuge Quotation desregula OS: type: " + mQuotation.desregulado.conyuge.osDeregulado.extra + " *******************");
                if (mQuotation.desregulado.conyuge.osDeregulado.extra.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                    mConyugeMonoBox.setVisibility(View.VISIBLE);
                } else {
                    mConyugeMonoBox.setVisibility(View.GONE);
                }
            }

        } else {
            mSelectedConyugeOsDesregula = -1;
            mConyugeOsDesregulaEditText.setText("");
        }

        ArrayList<Aporte> aporteLegalesList = mQuotation.desregulado.conyuge.aportesLegales;
        if (aporteLegalesList != null) {
            updateConyugeAportes(aporteLegalesList);
        }


        if (mConyugeMonoBox.getVisibility() == View.VISIBLE) {

            // Conyuge monotributo
            if (mQuotation.desregulado.conyuge.aportaMonotributo != null && mQuotation.desregulado.conyuge.aportaMonotributo) {
                mConyugeAportaMonotributoRadioButton.setChecked(true);
                mConyugeNoAportaMonotributoRadioButton.setChecked(false);

                showConyugeMonotributoExtraFields(true);
                mConyugeCantidadIntegrantesEditText.setText("" + mQuotation.desregulado.conyuge.nroAportantes);

            } else {
                mConyugeAportaMonotributoRadioButton.setChecked(false);
                mConyugeNoAportaMonotributoRadioButton.setChecked(true);
                showConyugeMonotributoExtraFields(false);
            }
        } else {
            mConyugeAportaMonotributoRadioButton.setChecked(false);
            mConyugeNoAportaMonotributoRadioButton.setChecked(true);
        }

        if (mConyuge != null && !mConyuge.familiaresACargoList.isEmpty()) {
            mSelectedConyugeFamiliares = getSelectedFamiliaresIndex(mConyuge);
        }
    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private boolean validateCantAportantesField(EditText editText, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);

        if (editText.getText().length() == 0) {
            input.setError(getString(R.string.add_cant_aportante_error));
            setError(editText, input);
            return false;
        } else {
            int cantAportantes = Integer.valueOf(editText.getText().toString().trim());
            if (cantAportantes > mQuotation.integrantes.size() + 1) {
                input.setError(getString(R.string.wrong_cant_aportante_error));
                setError(editText, input);
                return false;
            } else if (cantAportantes <= 0) {
                input.setError(getString(R.string.null_cant_aportante_error));
                setError(editText, input);
                return false;
            } else input.setErrorEnabled(false);
        }
        return true;
    }

    private void setError(EditText editText, TextInputLayout input) {
        input.setErrorEnabled(true);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }


    public boolean isValidSection() {
        boolean isValid = true;

        if (!mAportaMonotributoRadioButton.isChecked() && !mNoAportaMonotributoRadioButton.isChecked()) {
            mMonotibutoErrorView.setVisibility(View.VISIBLE);
            isValid = false;
        } else mMonotibutoErrorView.setVisibility(View.GONE);

        if (mAportaMonotributoRadioButton.isChecked()) {

            isValid = isValid & validateCantAportantesField(mCantidadIntegrantesEditText, R.id.cantidad_integrante_wrapper);
        }

        if (mQuotation.unificationType != -1) {

            if (mQuotation.unificationType == 0) {  // Conyuge Nuevo

                if (isUndefinedConyugeAportaMonotrubuto() || mConyugeNoAportaMonotributoRadioButton.isChecked()) {

                    // check  aporte or monotributo inputs
                    if (mSelectedConyugeOsDesregula != -1 && mConyugeAportesAdapter.getAportes().isEmpty()) {
                        SnackBarHelper.makeError(mMainContainer, R.string.agregar_aporte).show();
                        isValid = false;
                    }
                    if (!mConyugeAportesAdapter.getAportes().isEmpty() && mSelectedConyugeOsDesregula == -1) {
                        isValid = isValid & validateField(mConyugeOsDesregulaEditText, R.string.select_option_error, R.id.conyuge_os_desregula_wrapper);
                    } else {
                        resetError(R.id.conyuge_os_desregula_wrapper);
                    }

                } else if ( mConyugeAportaMonotributoRadioButton.isChecked()) {
                    if ((mSelectedConyugeOsDesregula != -1 && !mConyugeAportesAdapter.getAportes().isEmpty())) {
                        SnackBarHelper.makeError(mMainContainer, R.string.agregar_os_mono_error).show();
                        isValid = false;
                    }else if ((mSelectedConyugeOsDesregula != -1 && mConyugeAportesAdapter.getAportes().isEmpty())) {
                        SnackBarHelper.makeError(mMainContainer, R.string.agregar_os_mono_error).show();
                        isValid = false;
                    }else if ((mSelectedConyugeOsDesregula == -1 && !mConyugeAportesAdapter.getAportes().isEmpty())) {
                        SnackBarHelper.makeError(mMainContainer, R.string.agregar_aporte_mono_error).show();
                        isValid = false;
                    }
                }

                if (!mConyugeAportaMonotributoRadioButton.isChecked() && !mConyugeNoAportaMonotributoRadioButton.isChecked()) {
                    mConyugeMonotibutoErrorView.setVisibility(View.VISIBLE);
                    isValid = false;
                } else mConyugeMonotibutoErrorView.setVisibility(View.GONE);

                if (mConyugeAportaMonotributoRadioButton.isChecked()) {
                    isValid = isValid & validateCantAportantesField(mConyugeCantidadIntegrantesEditText, R.id.conyuge_cantidad_integrante_wrapper);
                }

                if (hasInvalidAssociatedConyuge) {
                    isValid = false;
                }
            }
        }

        if (mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) {
            isValid = isValid & validateField(mManualPlanEditText, R.string.manual_input_plan_error, R.id.manual_plan_input_wrapper);
            isValid = isValid & validateField(mManualPlanPriceText, R.string.manual_input_plan_price_error, R.id.manual_plan_price_wrapper);
        }

        return isValid;
    }

    private boolean isUndefinedConyugeAportaMonotrubuto() {
        if (!mConyugeAportaMonotributoRadioButton.isChecked() && !mConyugeNoAportaMonotributoRadioButton.isChecked()) {
            return true;
        } else {
            return false;
        }
    }

    private void resetError(int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        input.setErrorEnabled(false);
    }

    private void resetConyugeData() {
        if (mQuotation.desregulado.conyuge == null) {
            mQuotation.desregulado.conyuge = new ConyugeQuotation();
        }

        mQuotation.desregulado.conyuge.osDeregulado = null;
        mQuotation.desregulado.conyuge.aportesLegales = new ArrayList<Aporte>();
        mQuotation.desregulado.conyuge.aportaMonotributo = null;
    }


    private void showOsDesregulaAlert() {

        ArrayList<String> desregStr = new ArrayList<String>();
        for (QuoteOption q : mOSDesreguladas) {
            desregStr.add(q.optionName());
        }

        mConyugeOsDesregulaAlertAdapter = new SpinnerDropDownAdapter(getActivity(), desregStr, mSelectedConyugeOsDesregula);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mConyugeOsDesregulaAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedConyugeOsDesregula = i;

                if (i == 0) {
                    mSelectedConyugeOsDesregula = -1;
                    mConyugeOsDesregulaEditText.setText("");
                } else {
                    mConyugeOsDesregulaEditText.setText(mOSDesreguladas.get(i).optionName());

                    Log.e(TAG, "Conyuge OS: type!: " + mOSDesreguladas.get(i).extra + " *******************");
                    if (mOSDesreguladas.get(i).extra.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                        mConyugeMonoBox.setVisibility(View.VISIBLE);
                    } else {
                        mConyugeMonoBox.setVisibility(View.GONE);
                        mConyugeAportaMonotributoRadioButton.setChecked(false);
                        mConyugeNoAportaMonotributoRadioButton.setChecked(true);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mConyugeOsDesregulaAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showTitularFamiliaresAlert() {

        mTitularFamiliaresAlertAdapter = new SpinnerFamiliaresACargoAdapter(getActivity(), mFamiliares, mSelectedTitularFamiliares);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.field_familiares_cargo))
                .setSingleChoiceItems(mTitularFamiliaresAlertAdapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTitularFamiliaresAlertAdapter.setSelectedIndex(i);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mTitularFamiliaresAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        builder.setPositiveButton(
                R.string.option_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mSelectedTitularFamiliares = mTitularFamiliaresAlertAdapter.getSelectedIndexes();
                        buldFamiliaresACargo(mQuotation.titular, mSelectedTitularFamiliares);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showConyugeFamiliaresAlert() {

        mConyugeFamiliaresAlertAdapter = new SpinnerFamiliaresACargoAdapter(getActivity(), mFamiliares, mSelectedConyugeFamiliares);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.field_familiares_cargo))
                .setSingleChoiceItems(mConyugeFamiliaresAlertAdapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConyugeFamiliaresAlertAdapter.setSelectedIndex(i);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mConyugeFamiliaresAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        builder.setPositiveButton(
                R.string.option_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mSelectedConyugeFamiliares = mConyugeFamiliaresAlertAdapter.getSelectedIndexes();
                        buldFamiliaresACargo(mConyuge, mSelectedConyugeFamiliares);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void fillConyugeQuotationData() {

        if (mQuotation.desregulado == null) {
            mQuotation.desregulado = new DesreguladoQuotation();
        }

        // reset conyuge data for actual quotation
        mQuotation.desregulado.conyuge = new ConyugeQuotation();

        if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.AUTONOMO_SEGMENTO)) {
            mQuotation.desregulado.conyuge.osDeregulado = null;
            mQuotation.desregulado.conyuge.aportesLegales = null;
            mQuotation.desregulado.conyuge.aportaMonotributo = false;
        } else if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.DESREGULADO_SEGMENTO)) {

            // transfer data to conyuge
            mQuotation.desregulado.conyuge.osDeregulado = mConyugeQuotation.desregulado.osDeregulado;
            mQuotation.desregulado.conyuge.aportesLegales = mConyugeQuotation.aportes;
            mQuotation.desregulado.conyuge.aportaMonotributo = mConyugeQuotation.desregulado.aportaMonotributo;
            mQuotation.desregulado.conyuge.nroAportantes = mConyugeQuotation.desregulado.nroAportantes;

            if (mConyuge != null) {
                mConyuge.familiaresACargoList = mConyugeQuotation.titular.familiaresACargoList;
            }

        } else if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {

            mQuotation.desregulado.conyuge.osDeregulado = null;
            mQuotation.desregulado.conyuge.aportesLegales = null;
            mQuotation.desregulado.conyuge.aportaMonotributo = true;
            mQuotation.desregulado.conyuge.nroAportantes = mConyugeQuotation.monotributo.nroAportantes;

            if (mConyuge != null) {
                mConyuge.familiaresACargoList = mConyugeQuotation.titular.familiaresACargoList;
            }

        } else {
            mQuotation.desregulado.conyuge.osDeregulado = null;
            mQuotation.desregulado.conyuge.aportesLegales = null;
            mQuotation.desregulado.conyuge.aportaMonotributo = false;
        }
    }

    private int findSelectedFamiliaresIndex(FamiliarACargo fc) {

        int index = -1;
        for (int i = 0; i < mQuotation.integrantes.size(); i++) {
            Member member = mQuotation.integrantes.get(i);
            if (member.parentesco.id.equals(fc.parentesco.id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private ArrayList<Integer> getSelectedFamiliaresIndex(Member member) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();

        for (FamiliarACargo fc : member.familiaresACargoList) {
            int index = findSelectedFamiliaresIndex(fc);
            if (index != -1) {
                indexList.add(index);
            }
        }

        return indexList;
    }

    private void buldFamiliaresACargo(Member member, ArrayList<Integer> selectedFamiliares) {
        member.familiaresACargoList = new ArrayList<FamiliarACargo>();

        for (int i : selectedFamiliares) {
            Member m = mQuotation.integrantes.get(i);
            Log.e(TAG, "added fc parentezco id:: " + m.parentesco.title + " edad: " + m.age);

            FamiliarACargo fc = new FamiliarACargo();
            fc.age = m.age;
            fc.cant = 1;
            fc.parentesco = new QuoteOption(m.parentesco.id, QuoteOptionsController.getInstance().getParentescoName(m.parentesco.id));

            member.familiaresACargoList.add(fc);
        }
    }

}
