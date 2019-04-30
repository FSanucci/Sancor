package ar.com.sancorsalud.asociados.fragments.quote;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.interfaces.QuoteListener;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Pago;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

public class QuoteFormaPagoFragment extends BaseFragment {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private final static String QUOTE_DATE_FORMAT = "yyyy-MM-dd";

    private static final String ARG_QUOTATION = "quotation";
    private static final String ARG_COPAGOS = "showCopagos";

    private View mMainContainer;

    private View mTarjetaContainerView;
    private RadioButton mConRadioButton;
    private RadioButton mSinRadioButton;
    private View mCopagoErrorView;

    private EditText mFormaPagoEditText;
    private SpinnerDropDownAdapter mFormasPagoAlertAdapter;
    private int mSelectedFormasPago = -1;
    private ArrayList<QuoteOption> mFormasPago;

    private TextInputLayout mTarjetaTextInputLayout;
    private EditText mTarjetaEditText;
    private SpinnerDropDownAdapter mTarjetasAlertAdapter;
    private int mSelectedTarjetas = -1;
    private ArrayList<QuoteOption> mTarjetas;


    //TODO: 01/03/2019 se quitan los bancos de la cotización
    //private View mBancoContainerView;
    //private TextInputLayout mBancoTextInputLayout;
    //private EditText mBancoEditText;
    //private SpinnerDropDownAdapter mBancosAlertAdapter;
    //private int mSelectedBanco = -1;
    //private ArrayList<QuoteOption> mBancos;

    private QuoteListener mCallback;
    protected Quotation mQuotation;

    private EditText mDateEditText;
    private boolean showCopagos = true;

    private ConstantsUtil.Segmento segmento;
    private EditText mManualPlanEditText;
    private EditText mManualPlanPriceText;


    public QuoteFormaPagoFragment() {
    }

    public static QuoteFormaPagoFragment newInstance(Quotation q) {
        QuoteFormaPagoFragment fragment = new QuoteFormaPagoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, q);
        args.putBoolean(ARG_COPAGOS, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static QuoteFormaPagoFragment newInstance(Quotation q, boolean showCopagos) {
        QuoteFormaPagoFragment fragment = new QuoteFormaPagoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, q);
        args.putBoolean(ARG_COPAGOS, showCopagos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
            showCopagos = getArguments().getBoolean(ARG_COPAGOS, true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRetainInstance(false);
        if (mMainContainer == null)
            mMainContainer = inflater.inflate(R.layout.fragment_quote_forma_pago, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mFormaPagoEditText = (EditText) mMainContainer.findViewById(R.id.forma_pago_input);
        setTypeTextNoSuggestions(mFormaPagoEditText);

        mTarjetaEditText = (EditText) mMainContainer.findViewById(R.id.tarjeta_input);
        setTypeTextNoSuggestions(mTarjetaEditText);

        //mBancoEditText = (EditText) mMainContainer.findViewById(R.id.banco_input);
        //setTypeTextNoSuggestions(mBancoEditText);

        mConRadioButton = (RadioButton) mMainContainer.findViewById(R.id.conButton);
        mSinRadioButton = (RadioButton) mMainContainer.findViewById(R.id.sinButton);
        mCopagoErrorView = mMainContainer.findViewById(R.id.co_pago_error);

        mTarjetaContainerView = mMainContainer.findViewById(R.id.tarjeta_container);
        mTarjetaTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.tarjeta_wrapper);
        //mBancoContainerView = mMainContainer.findViewById(R.id.banco_container);
        //mBancoTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.banco_wrapper);

        mDateEditText = (EditText) mMainContainer.findViewById(R.id.date_input);

        mManualPlanEditText = (EditText) mMainContainer.findViewById(R.id.manual_plan_input);
        mManualPlanPriceText = (EditText) mMainContainer.findViewById(R.id.manual_plan_price_input);

        fillArraysData();
        setupListener();
        initializeForm();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (QuoteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement QuoteListener");
        }
    }

    private void fillArraysData() {

        mFormasPago = new ArrayList<>();
        QuoteOption pagoSelection = new QuoteOption("-1", getResources().getString(R.string.field_forma_pago));
        mFormasPago.add(pagoSelection);
        mFormasPago.addAll(QuoteOptionsController.getInstance().getFormasPago());

        mTarjetas = new ArrayList<>();
        QuoteOption cardSelection = new QuoteOption("-1", getResources().getString(R.string.field_card));
        mTarjetas.add(cardSelection);
        mTarjetas.addAll(QuoteOptionsController.getInstance().getTarjetas());

/* //TODO: 01/03/2019 se quitan los bancos de la cotización
        mBancos = new ArrayList<>();
        QuoteOption bancoSelection = new QuoteOption("-1", getResources().getString(R.string.field_bank));
        mBancos.add(bancoSelection);
        mBancos.addAll(QuoteOptionsController.getInstance().getBancos());
*/
    }

    private void initializeForm() {

        if (mQuotation == null)
            return;

        segmento = mQuotation.getSegmento();
        if ((mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) && (segmento == ConstantsUtil.Segmento.AUTONOMO)) {
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

        mFormaPagoEditText.setText("");
        mTarjetaEditText.setText("");
        //mBancoEditText.setText("");
        mDateEditText.setText("");

        mSelectedFormasPago = -1;
        mSelectedTarjetas = -1;
        //mSelectedBanco = -1;

        mConRadioButton.setVisibility(showCopagos ? View.VISIBLE : View.GONE);
        mSinRadioButton.setVisibility(showCopagos ? View.VISIBLE : View.GONE);
        mCopagoErrorView.setVisibility(View.GONE);


        if (!isAutonomo()) {
            View v = mMainContainer.findViewById(R.id.see_quote_button);
            v.setVisibility(View.GONE);
        }

        if (isEmpresa()) {

            Log.e(TAG, "isEmpresa-------------");

            QuoteOption opt = new QuoteOption();
            opt.id = mQuotation.nombreEmpresa.extra;   //   normal case id = number, now is extra ej EF
            opt.title = mQuotation.nombreEmpresa.extra;  //  normal case title will be description

            mFormasPago = new ArrayList<QuoteOption>();
            mFormasPago.add(opt);

            mFormaPagoEditText.setText(opt.optionName());
            mTarjetaContainerView.setVisibility(View.GONE);
            //mBancoContainerView.setVisibility(View.GONE);
            mSelectedFormasPago = mFormasPago.size() - 1;
        }

        if (mQuotation.inputDate != null && !mQuotation.inputDate.isEmpty()) {
            Date date = ParserUtils.parseDate(mQuotation.inputDate, QUOTE_DATE_FORMAT);
            mDateEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));

        }

        if (mQuotation.pago == null)
            return;

        if (mQuotation.pago.formaPago != null) {
            mSelectedFormasPago = mFormasPago.indexOf(mQuotation.pago.formaPago);
            if (mSelectedFormasPago != -1)
                mFormaPagoEditText.setText(mQuotation.pago.formaPago.title);
        }

        if (mQuotation.pago.tarjeta != null) {
            mSelectedTarjetas = mTarjetas.indexOf(mQuotation.pago.tarjeta);
            if (mSelectedTarjetas != -1)
                mTarjetaEditText.setText(mQuotation.pago.tarjeta.title);
        }
/* //TODO: 01/03/2019 se quitan los bancos de la cotización
        if (mQuotation.pago.banco != null) {
            mSelectedBanco = mBancos.indexOf(mQuotation.pago.banco);
            if (mSelectedBanco != -1)
                mBancoEditText.setText(mQuotation.pago.banco.title);
        }

        if (mSelectedFormasPago != -1) {
            QuoteOption opt = mFormasPago.get(mSelectedFormasPago);
            if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                mTarjetaContainerView.setVisibility(View.VISIBLE);
<<<<<<< HEAD
<<<<<<< HEAD
                mBancoContainerView.setVisibility(View.GONE);
            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                mBancoContainerView.setVisibility(View.GONE);
=======
                // El combo 'Banco' ya no es visible, si lo piden descomentar la linea de abajo
                // mBancoContainerView.setVisibility(View.VISIBLE);
            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                // El combo 'Banco' ya no es visible, si lo piden descomentar la linea de abajo
                // mBancoContainerView.setVisibility(View.VISIBLE);
>>>>>>> master
=======

                // Banco ya no es visible
                //mBancoContainerView.setVisibility(View.VISIBLE);
                mBancoContainerView.setVisibility(View.GONE);
            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                // Banco ya no es visible
                //mBancoContainerView.setVisibility(View.VISIBLE);
                mBancoContainerView.setVisibility(View.GONE);
>>>>>>> origin/sprint-05-12-2018
            } else {
                mTarjetaContainerView.setVisibility(View.GONE);
                mBancoContainerView.setVisibility(View.GONE);
            }
        }
*/
        initializeCopago();
    }

    private void initializeCopago() {
        if (mQuotation.pago == null)
            return;

        if (mQuotation.pago.conCopago == null) {
            mConRadioButton.setChecked(false);
            mSinRadioButton.setChecked(false);
        } else if (mQuotation.pago.conCopago) {
            mConRadioButton.setChecked(true);
            mSinRadioButton.setChecked(false);
        } else {
            mConRadioButton.setChecked(false);
            mSinRadioButton.setChecked(true);
        }
    }

    private void setupListener() {

        View pago = mMainContainer.findViewById(R.id.forma_pago_button);
        pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (!isEmpresa())
                    showFormasPagoAlert();
            }
        });

        pago.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedFormasPago = -1;
                mFormaPagoEditText.setText("");
                return true;
            }
        });


        View tarjeta = mMainContainer.findViewById(R.id.tarjeta_button);
        tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showTarjetasAlert();
            }
        });

        tarjeta.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedTarjetas = -1;
                mTarjetaEditText.setText("");
                return true;
            }
        });

/* //TODO: 01/03/2019 se quitan los bancos de la cotización
        View banco = mMainContainer.findViewById(R.id.banco_button);
        banco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showBancosAlert();
            }
        });

        banco.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedBanco = -1;
                mBancoEditText.setText("");
                return true;
            }
        });
*/

        mConRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCopagoErrorView.setVisibility(View.GONE);
            }
        });

        mSinRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCopagoErrorView.setVisibility(View.GONE);
            }
        });


        View dateBtn = mMainContainer.findViewById(R.id.date_button);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showCalendar();
            }
        });


        View seeQuote = mMainContainer.findViewById(R.id.see_quote_button);
        seeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (isValidSection()) {
                    mQuotation.pago = getPago();
                    mCallback.showQuotation(mQuotation);
                }
            }
        });
    }

    private boolean isEmpresa() {
        if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.EMPRESA_FORMA_INGRESO))
            return true;
        return false;
    }

    private boolean isGroupAffinity() {
        if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.AFINIDAD_FORMA_INGRESO))
            return true;
        return false;
    }

    private boolean isAutonomo() {
        if (mQuotation.segmento.id.equalsIgnoreCase(ConstantsUtil.AUTONOMO_SEGMENTO))
            return true;
        return false;
    }

    public void setQuotation(Quotation q) {
        mQuotation = q;
        initializeForm();
    }

    public Pago getPago() {

        if (mQuotation.pago == null)
            mQuotation.pago = new Pago();

        if (showCopagos) {
            if (!mSinRadioButton.isChecked() && !mConRadioButton.isChecked()) {
                mQuotation.pago.conCopago = null;
            } else {
                mQuotation.pago.conCopago = mConRadioButton.isChecked();

            }
        } else {
            mQuotation.pago.conCopago = null;
        }

        if (mSelectedFormasPago != -1) {
            mQuotation.pago.formaPago = mFormasPago.get(mSelectedFormasPago);

            if (mQuotation.pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                if (mSelectedTarjetas != -1) {

                    mQuotation.pago.tarjeta = mTarjetas.get(mSelectedTarjetas);
                    //if (mSelectedBanco != -1) {
                    //    mQuotation.pago.banco = mBancos.get(mSelectedBanco);
                    //}
                }
            } else if (mQuotation.pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                //if (mSelectedBanco != -1) {
                //    mQuotation.pago.banco = mBancos.get(mSelectedBanco);
                //}
            } else {
                mQuotation.pago.tarjeta = null;
                mQuotation.pago.banco = null;
            }
        }

        if ((mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) && (segmento == ConstantsUtil.Segmento.AUTONOMO)) {
            // add manual quotation data
            mQuotation.manualPlanName = mManualPlanEditText.getText().toString().trim();

            if (!mManualPlanPriceText.getText().toString().trim().isEmpty()) {
                mQuotation.manualPlanPrice = Double.parseDouble(mManualPlanPriceText.getText().toString().trim());
            }
        }

        return mQuotation.pago;
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

    private boolean validateForm() {
        boolean isValid = true;

        isValid = isValid & validateField(mDateEditText, R.string.add_date_error, R.id.date_wrapper);
        isValid = isValid & validateField(mFormaPagoEditText, R.string.seleccione_forma_pago, R.id.forma_pago_wrapper);

        if (mSelectedFormasPago != -1) {
            QuoteOption opt = mFormasPago.get(mSelectedFormasPago);

            if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                isValid = isValid & validateField(mTarjetaEditText, R.string.seleccione_tarjeta, R.id.tarjeta_wrapper);
            }
            /*
            else if(opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)){
                isValid = isValid & validateField(mBancoEditText, R.string.seleccione_banco_error, R.id.banco_wrapper);
            }
            */
        }

        if (showCopagos && !mSinRadioButton.isChecked() && !mConRadioButton.isChecked()) {
            isValid = false;
            mCopagoErrorView.setVisibility(View.VISIBLE);
        } else
            mCopagoErrorView.setVisibility(View.GONE);

        if ((mQuotation.cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) && (segmento == ConstantsUtil.Segmento.AUTONOMO)) {
            isValid = isValid & validateField(mManualPlanEditText, R.string.manual_input_plan_error, R.id.manual_plan_input_wrapper);
            isValid = isValid & validateField(mManualPlanPriceText, R.string.manual_input_plan_price_error, R.id.manual_plan_price_wrapper);
        }

        return isValid;
    }

    public boolean isValidSection() {
        return validateForm();
    }

    public void showFormasPagoAlert() {

        String query = null;
        if (isGroupAffinity() && mQuotation.nombreAfinidad.id != null)
            query = "?afinidadId=" + mQuotation.nombreAfinidad.id;

        HRequest request = RestApiServices.createGetFormasPagoRequest(query, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                String title = "";

                if (response.size() > 0)
                    title = getResources().getString(R.string.field_forma_pago);
                else title = "No se encontraron formas de pago";

                mFormasPago.clear();
                QuoteOption pagoSelection = new QuoteOption("-1", title);
                mFormasPago.add(pagoSelection);
                mFormasPago.addAll(response);

                ArrayList<String> formaPagoStr = new ArrayList<String>();
                for (QuoteOption q : mFormasPago) {
                    formaPagoStr.add(q.optionName());
                }

                mFormasPagoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), formaPagoStr, mSelectedFormasPago);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setAdapter(mFormasPagoAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedFormasPago = i;
                        if (i == 0) {
                            mSelectedFormasPago = -1;
                            mFormaPagoEditText.setText("");
                        } else {

                            QuoteOption opt = mFormasPago.get(mSelectedFormasPago);
                            if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                                mSelectedTarjetas = -1;
                                //mSelectedBanco = -1;
                                mTarjetaEditText.setText("");
                                //mBancoEditText.setText("");
                                mTarjetaTextInputLayout.setErrorEnabled(false);
                                //mBancoTextInputLayout.setErrorEnabled(false);
                                mTarjetaContainerView.setVisibility(View.VISIBLE);
/************************ Start MERGE (sprint-05-12-2018 --> development)  ************************/
/*
                                // Banco ya no es visible
                                //mBancoContainerView.setVisibility(View.VISIBLE);
                                mBancoContainerView.setVisibility(View.GONE);
                            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                                mSelectedBanco = -1;
                                mBancoEditText.setText("");
                                // Banco ya no es visible
                                //mBancoContainerView.setVisibility(View.VISIBLE);
                                mBancoContainerView.setVisibility(View.GONE);
*/
                                //mBancoContainerView.setVisibility(View.GONE);
                            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                                //mSelectedBanco = -1;
                                //mBancoEditText.setText("");
                                //mBancoTextInputLayout.setErrorEnabled(false);
                                //mBancoContainerView.setVisibility(View.GONE);
/************************* End MERGE (sprint-05-12-2018 --> development)  *************************/
                                mTarjetaContainerView.setVisibility(View.GONE);
                            } else {
                                mTarjetaContainerView.setVisibility(View.GONE);
                                //mBancoContainerView.setVisibility(View.GONE);
                            }

                            mFormaPagoEditText.setText(opt.optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mFormasPagoAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);

    }

    public void showTarjetasAlert() {

        ArrayList<String> cardStr = new ArrayList<String>();
        for (QuoteOption q : mTarjetas) {
            cardStr.add(q.optionName());
        }

        mTarjetasAlertAdapter = new SpinnerDropDownAdapter(getActivity(), cardStr, mSelectedTarjetas);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mTarjetasAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedTarjetas = i;
                if (i == 0) {
                    mSelectedTarjetas = -1;
                    mTarjetaEditText.setText("");
                } else {
                    mTarjetaEditText.setText(mTarjetas.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mTarjetasAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
/* //TODO: 01/03/2019 se quitan los bancos de la cotización
    public void showBancosAlert() {

        ArrayList<String> bankStr = new ArrayList<String>();
        for (QuoteOption q : mBancos) {
            bankStr.add(q.optionName());
        }

        mBancosAlertAdapter = new SpinnerDropDownAdapter(getActivity(), bankStr, mSelectedBanco);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mBancosAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedBanco = i;
                if (i == 0) {
                    mSelectedBanco = -1;
                    mBancoEditText.setText("");
                } else {
                    mBancoEditText.setText(mBancos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mBancosAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
*/

    private void showCalendar() {
        Date date = null;
        String data = mDateEditText.getText().toString().trim();
        if (data.isEmpty()) {
            date = new Date();
        } else {
            try {
                date = ParserUtils.parseDate(data, DATE_FORMAT);
            } catch (Exception e) {
                date = new Date();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
        dateDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String sDate = new StringBuilder().append(String.format("%02d", dayOfMonth)).append("/").append(String.format("%02d", monthOfYear + 1)).append("/").append(year).toString();
            Date today = new Date();

            try {
                Date date = ParserUtils.parseDate(sDate, DATE_FORMAT);

                if (date.before(today)) {
                    mDateEditText.setText(ParserUtils.parseDate(today, DATE_FORMAT));
                    mQuotation.inputDate = ParserUtils.parseDate(today, QUOTE_DATE_FORMAT);

                } else {
                    mDateEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));
                    mQuotation.inputDate = ParserUtils.parseDate(date, QUOTE_DATE_FORMAT);
                }

            } catch (Exception e) {
                mDateEditText.setText("");
                mQuotation.inputDate = ParserUtils.parseDate(today, QUOTE_DATE_FORMAT);
            }

            mDateEditText.requestFocus();
        }
    };
}
