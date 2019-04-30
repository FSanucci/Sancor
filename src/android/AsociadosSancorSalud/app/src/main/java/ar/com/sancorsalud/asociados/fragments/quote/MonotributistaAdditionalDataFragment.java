package ar.com.sancorsalud.asociados.fragments.quote;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.interfaces.QuoteListener;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.quotation.ConyugeQuotation;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.quotation.MonotributoQuotation;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;


public class MonotributistaAdditionalDataFragment extends BaseFragment {

    private static final String TAG = "DESR_FRG";

    private static final String ARG_QUOTATION = "quotation";
    private static final String ARG_CONYUGE_QUOTATION = "conyQuotation";

    // Titular data
    private View mMainContainer;
    private Quotation mQuotation;
    private QuoteListener mCallback;

    // Titular Aportes monotributo
    private View mMonotributoImportantNote;
    private EditText mCantidadIntegrantesEditText;
    private TextInputLayout mCantidadIntegrantesTextInputLayout;

    // Conyuge Data
    private Quotation mConyugeQuotation;

    private TextView mConyugeTextView;
    private View mConyugeDniContainer;
    private EditText mConyugeDniEditText;

    // Conyuge Monotributo
    private View mConyugeMonotributoContainer;
    private View mConyugeMonotibutoErrorView;

    private RadioButton mConyugeAportaMonotributoRadioButton;
    private RadioButton mConyugeNoAportaMonotributoRadioButton;

    private TextInputLayout mConyugeCantidadIntegrantesTextInputLayout;
    private EditText mConyugeCantidadIntegrantesEditText;

    //private ArrayList<QuoteOption> mTiposUnificacion;
    private Member mConyuge;
    private boolean hasInvalidAssociatedConyuge = false;
    //private View aporteButton;

    private EditText mManualPlanEditText;
    private EditText mManualPlanPriceText;

    public MonotributistaAdditionalDataFragment() {
        // Required empty public constructor
    }

    public static MonotributistaAdditionalDataFragment newInstance(Quotation param1, Quotation param2) {
        MonotributistaAdditionalDataFragment fragment = new MonotributistaAdditionalDataFragment();
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
        mMainContainer = inflater.inflate(R.layout.fragment_monotributista_additional_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mCantidadIntegrantesEditText = (EditText) mMainContainer.findViewById(R.id.cantidad_integrante_input);
        mCantidadIntegrantesTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.cantidad_integrante_wrapper);
        mMonotributoImportantNote = mMainContainer.findViewById(R.id.field_aporte_imp_note);

        mConyugeTextView = (TextView) mMainContainer.findViewById(R.id.conyuge_section_title);
        mConyugeDniContainer = mMainContainer.findViewById(R.id.conyuge_dni_wrapper);
        mConyugeDniEditText = (EditText) mMainContainer.findViewById(R.id.conyuge_dni_input);

        // Conyuge monotributo
        mConyugeMonotributoContainer = (View) mMainContainer.findViewById(R.id.conyuge_mono_container);

        mConyugeAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.conyuge_si_aporta_mono);
        mConyugeNoAportaMonotributoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.conyuge_no_aporta_mono);
        mConyugeMonotibutoErrorView = mMainContainer.findViewById(R.id.conyuge_aporta_monotributo_error);

        mConyugeCantidadIntegrantesTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.conyuge_cantidad_integrante_wrapper);
        mConyugeCantidadIntegrantesEditText = (EditText) mMainContainer.findViewById(R.id.conyuge_cantidad_integrante_input);

        //mTiposUnificacion = QuoteOptionsController.getInstance().getTipoUnificacion();

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

    private void setupListeners() {

        mConyugeAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConyugeMonotibutoErrorView.setVisibility(View.GONE);
                showConyugeMonotributoExtraFields(true);

                //checkHasToShowTitularMainAffiliationCheckBox();
            }
        });

        mConyugeNoAportaMonotributoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConyugeMonotibutoErrorView.setVisibility(View.GONE);
                showConyugeMonotributoExtraFields(false);
            }
        });

        View seeQuote = mMainContainer.findViewById(R.id.see_quote_button);
        seeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (isValidSection()) {
                    mQuotation.monotributo = getMonotributoQuotation();
                    Log.e(TAG, "MONOTRIB QUOTATIN READY OK ......");
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


        mCantidadIntegrantesEditText.setText("");

        mConyuge = mQuotation.getConyuge();
        if (mConyuge == null)
            mConyuge = mQuotation.getConcubino();

        if (mConyuge != null) {
            mConyugeTextView.setText(getResources().getString(R.string.field_dato_conyuge, mConyuge.parentesco.title));
            mConyugeDniEditText.setText("" + ((mConyuge.dni != -1) ? mConyuge.dni : ""));
        }


        // Unification Data
        if (mQuotation.unificaAportes != null) {
            if (mQuotation.unificaAportes) {
                if (mQuotation.unificationType != -1) {

                    if (mQuotation.unificationType == 0) {  // Conyuge Nuevo
                        showConyugeData();

                    } else {  // Conyuge Asociado
                        hideConyugeSection();
                        fillConyugeQuotationData();
                    }
                }
            } else {
                hideConyugeSection();
            }
        }

        if (mQuotation.monotributo == null)
            return;

        mCantidadIntegrantesEditText.setText("" + mQuotation.monotributo.nroAportantes);
    }


    public void setQuotation(Quotation q) {
        mQuotation = q;
        initializeForm();
    }

    public MonotributoQuotation getMonotributoQuotation() {

        if (mQuotation.monotributo == null) {
            mQuotation.monotributo = new MonotributoQuotation();
        }

        if (!mCantidadIntegrantesEditText.getText().toString().isEmpty()) {
            mQuotation.monotributo.nroAportantes = Integer.parseInt(mCantidadIntegrantesEditText.getText().toString());
        }

        // UNIFICACION
        mQuotation.monotributo.unificaAportes = mQuotation.unificaAportes;
        mQuotation.monotributo.titularMainAffilliation = mQuotation.titularMainAffilliation;
        mQuotation.monotributo.unificationType = mQuotation.unificationType;

        if (mQuotation.monotributo.unificaAportes != null && mQuotation.monotributo.unificaAportes) {

            if (mQuotation.monotributo.conyuge == null)
                mQuotation.monotributo.conyuge = new ConyugeQuotation();

            // last chance to update conyuge/concubino DNI
            if (mConyugeDniEditText.getText().toString().isEmpty()) {
                mConyuge.dni = -1;
            } else {
                mConyuge.dni = Long.parseLong(mConyugeDniEditText.getText().toString());
            }
            mQuotation.monotributo.conyuge.dni = mConyuge.dni;

            //if (mSelectedConyugeTipoUni != -1) {
            if (mQuotation.monotributo.unificationType != -1) {

                if (mQuotation.monotributo.unificationType == 0) { // Conyuge nuevo
                    mQuotation.monotributo.conyuge.afiliado = false;

                    // Conyuge monotributo
                    mQuotation.monotributo.conyuge.aportaMonotributo = mConyugeAportaMonotributoRadioButton.isChecked();

                    if (mQuotation.monotributo.conyuge.aportaMonotributo) {
                        if (!mConyugeCantidadIntegrantesEditText.getText().toString().isEmpty()) {
                            mQuotation.monotributo.conyuge.nroAportantes = Integer.parseInt(mConyugeCantidadIntegrantesEditText.getText().toString());
                        }
                    }


                } else {
                    // Conyuge asociado
                    mQuotation.monotributo.conyuge.afiliado = true;
                }
                // Always Define conyuge segment
                mQuotation.monotributo.conyuge.segmento = new QuoteOption(ConstantsUtil.MONOTRIBUTO_SEGMENTO, null);

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

        return mQuotation.monotributo;
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

        // conyugue monotributo
        mConyugeMonotributoContainer.setVisibility(View.GONE);
        mConyugeCantidadIntegrantesTextInputLayout.setVisibility(View.GONE);
    }

    private void showConyugeData() {

        mConyugeTextView.setVisibility(View.VISIBLE);
        mConyugeDniEditText.setVisibility(View.VISIBLE);
        mConyugeDniContainer.setVisibility(View.VISIBLE);

        mConyugeMonotributoContainer.setVisibility(View.VISIBLE);
        mConyugeCantidadIntegrantesTextInputLayout.setVisibility(View.GONE);

        if (mQuotation.monotributo == null) {
            mQuotation.monotributo = new MonotributoQuotation();
        }

        if (mQuotation.monotributo.conyuge == null) {
            mQuotation.monotributo.conyuge = new ConyugeQuotation();
        }

        // Conyuge monotributo
        if (mQuotation.monotributo.conyuge.aportaMonotributo != null && mQuotation.monotributo.conyuge.aportaMonotributo) {
            mConyugeAportaMonotributoRadioButton.setChecked(true);
            mConyugeNoAportaMonotributoRadioButton.setChecked(false);

            showConyugeMonotributoExtraFields(true);
            mConyugeCantidadIntegrantesEditText.setText("" + mQuotation.monotributo.conyuge.nroAportantes);

        } else {
            mConyugeAportaMonotributoRadioButton.setChecked(false);
            mConyugeNoAportaMonotributoRadioButton.setChecked(true);
            showConyugeMonotributoExtraFields(false);
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

        isValid = isValid & validateCantAportantesField(mCantidadIntegrantesEditText, R.id.cantidad_integrante_wrapper);

        if (mQuotation.unificationType != -1) {

            if (mQuotation.unificationType == 0) { // Conyuge Nuevo
                // conyuge monotributo
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

            if (mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) {
                isValid = isValid & validateField(mManualPlanEditText, R.string.manual_input_plan_error, R.id.manual_plan_input_wrapper);
                isValid = isValid & validateField(mManualPlanPriceText, R.string.manual_input_plan_price_error, R.id.manual_plan_price_wrapper);
            }
        }

        return isValid;
    }

    private void resetError(int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        input.setErrorEnabled(false);

    }

    private void resetConyugeData() {
        if (mQuotation.monotributo == null) {
            mQuotation.monotributo = new MonotributoQuotation();
        }

        if (mQuotation.monotributo.conyuge == null) {
            mQuotation.monotributo.conyuge = new ConyugeQuotation();
        }

        mQuotation.monotributo.conyuge.osDeregulado = null;
        mQuotation.monotributo.conyuge.aportesLegales = new ArrayList<Aporte>();
        mQuotation.monotributo.conyuge.aportaMonotributo = null;
    }

    private void fillConyugeQuotationData() {

        if (mQuotation.monotributo == null) {
            mQuotation.monotributo = new MonotributoQuotation();
        }

        // reset conyuge data for actual quotation
        mQuotation.monotributo.conyuge = new ConyugeQuotation();

        if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.AUTONOMO_SEGMENTO)) {
            mQuotation.monotributo.conyuge.osDeregulado = null;
            mQuotation.monotributo.conyuge.aportesLegales = null;
            mQuotation.monotributo.conyuge.aportaMonotributo = false;

        } else if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.DESREGULADO_SEGMENTO)) {

            // transfer data to conyuge
            mQuotation.monotributo.conyuge.osDeregulado = mConyugeQuotation.desregulado.osDeregulado;
            mQuotation.monotributo.conyuge.aportesLegales = mConyugeQuotation.aportes;
            mQuotation.monotributo.conyuge.aportaMonotributo = mConyugeQuotation.desregulado.aportaMonotributo;
            mQuotation.monotributo.conyuge.nroAportantes = mConyugeQuotation.desregulado.nroAportantes;

            if (mConyuge != null) {
                mConyuge.familiaresACargoList = mConyugeQuotation.titular.familiaresACargoList;
            }
        } else if (mConyugeQuotation.segmento.id.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {

            mQuotation.monotributo.conyuge.osDeregulado = null;
            mQuotation.monotributo.conyuge.aportesLegales = null;
            mQuotation.monotributo.conyuge.aportaMonotributo = true;
            mQuotation.monotributo.conyuge.nroAportantes = mConyugeQuotation.monotributo.nroAportantes;

            if (mConyuge != null) {
                mConyuge.familiaresACargoList = mConyugeQuotation.titular.familiaresACargoList;
            }

        } else {

            mQuotation.monotributo.conyuge.osDeregulado = null;
            mQuotation.monotributo.conyuge.aportesLegales = null;
            mQuotation.monotributo.conyuge.aportaMonotributo = false;
        }
    }
}
