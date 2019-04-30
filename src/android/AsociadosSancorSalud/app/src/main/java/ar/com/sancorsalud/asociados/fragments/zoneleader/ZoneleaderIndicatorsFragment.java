package ar.com.sancorsalud.asociados.fragments.zoneleader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.ZoneLeaderIndicator;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class ZoneleaderIndicatorsFragment extends BaseFragment {

    private View mMainContainer;
    private ZoneLeaderIndicator mIndicator;
    public EditText mFromDateEditText;
    public EditText mToDateEditText;
    public View mIndicatorContainer;
    public View mGetIndicatorsButton;
    public Date mFromDate;
    public Date mToDate;
    private SimpleDateFormat mDateFormat;

    /*private EditText mTempPromedioEditText;
    private EditText mPAScheduleEditText;
    private EditText mPAScheduledEditText;
    private EditText mPAQuotedEditText;
    private EditText mPACloseEditText;*/

    //private EditText mManualQuoteEditText;

    private EditText mProduccionEditText;
    private EditText mCantCapitasEditText;
    private EditText mTasaErrorEditText;
    private EditText mTiempoCargaEditText;
    private EditText mGravEditText;
    private EditText mNoGravEditText;
    private EditText mAfinidadEditText;
    private EditText mEmpresaEditText;
    private EditText mIndividualEditText;
    private EditText mTCEditText;
    private EditText mCBUEditText;
    private EditText mPFEditText;
    private EditText mRceEditText;
    private EditText mEfEditText;
    //private EditText mTrasabilidadTasaEditText;
    //private EditText mTrasabilidadDocErrorEditText;
    private EditText mSalesmanEditText;
    private EditText mPAQuoted;
    private EditText mInProcess;
    private EditText mRecordsToCorrect;
    private EditText mDerivedToControl;

    private SpinnerDropDownAdapter mSalesmanAlertAdapter;
    private int mSalesmanSelected = -1;

    private View mProgressView;
    private View mMainContentView;
    private ArrayList<Salesman> mSalesmans;

// Cuotas ya no es visible
    /*private View mPAContainer;
    private ImageView mBtnArrowMoreLessPA;
    private View mPADivider;*/

    // Cuotas ya no es visible
    /*private View mQuoteContainer;
    private ImageView mBtnArrowMoreLessQuote;
    private View mQuoteDivider;*/

    private View mChargeContainer;
    private ImageView mBtnArrowMoreLessCharge;
    private View mChargeDivider;

    /*private View mTrazabilidadContainer;
    private ImageView mBtnArrowMoreLessTrazabilidad;*/

    private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(String.format("%02d", dayOfMonth)).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(year).toString();

            Date today = new Date();
            try {
                mFromDate = mDateFormat.parse(out);
                mFromDateEditText.setText(mDateFormat.format(mFromDate));
            } catch (Exception e) {
                mFromDateEditText.setText(mDateFormat.format(today));
            }
            mFromDateEditText.requestFocus();
        }
    };

    private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(String.format("%02d", dayOfMonth)).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(year).toString();

            Date today = new Date();
            try {
                mToDate = mDateFormat.parse(out);
                mToDateEditText.setText(mDateFormat.format(mToDate));
            } catch (Exception e) {
                mToDateEditText.setText(mDateFormat.format(today));
            }
            mToDateEditText.requestFocus();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zoneleader_indicators, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.mMainContainer = view;
        this.mGetIndicatorsButton = view.findViewById(R.id.button);
        this.mFromDateEditText = (EditText) view.findViewById(R.id.from_date_input);
        this.mToDateEditText = (EditText) view.findViewById(R.id.to_date_input);
        this.mIndicatorContainer = view.findViewById(R.id.indicator_container);

        /*this.mTempPromedioEditText = (EditText) view.findViewById(R.id.indicators_temp);
        this.mPAScheduleEditText = (EditText) view.findViewById(R.id.indicators_pa_schedule);
        this.mPAScheduledEditText = (EditText) view.findViewById(R.id.indicators_pa_scheduled);
        this.mPAQuotedEditText = (EditText) view.findViewById(R.id.indicators_pa_quoted);
        this.mPACloseEditText = (EditText) view.findViewById(R.id.indicators_pa_close);*/

        //this.mManualQuoteEditText = (EditText) view.findViewById(R.id.indicators_quote_manual);

        this.mProduccionEditText = (EditText) view.findViewById(R.id.indicators_carga_prod);
        this.mCantCapitasEditText = (EditText) view.findViewById(R.id.indicators_carga_prod_in_capitas);
        this.mTasaErrorEditText = (EditText) view.findViewById(R.id.indicators_carga_tasa_error);
        this.mTiempoCargaEditText = (EditText) view.findViewById(R.id.indicators_carga_tiempo);
        this.mGravEditText = (EditText) view.findViewById(R.id.indicators_carga_grav);
        this.mNoGravEditText = (EditText) view.findViewById(R.id.indicators_carga_nograv);
        this.mAfinidadEditText = (EditText) view.findViewById(R.id.indicators_carga_afinidad);
        this.mEmpresaEditText = (EditText) view.findViewById(R.id.indicators_carga_empresa);
        this.mIndividualEditText = (EditText) view.findViewById(R.id.indicators_carga_ind);
        this.mTCEditText = (EditText) view.findViewById(R.id.indicators_carga_tc);
        this.mCBUEditText = (EditText) view.findViewById(R.id.indicators_carga_cbu);
        this.mPFEditText = (EditText) view.findViewById(R.id.indicators_carga_pf);
        this.mRceEditText = (EditText) view.findViewById(R.id.indicators_carga_rce);
        this.mEfEditText = (EditText) view.findViewById(R.id.indicators_carga_ef);

        /*this.mTrasabilidadTasaEditText = (EditText) view.findViewById(R.id.indicators_trazabilidad_tasa);
        this.mTrasabilidadDocErrorEditText = (EditText) view.findViewById(R.id.indicators_trazabilidad_doc_error);*/

        this.mSalesmanEditText = (EditText) view.findViewById(R.id.salesman_input);

        this.mPAQuoted = (EditText) view.findViewById(R.id.indicators_carga_pa_quoted);
        this.mInProcess = (EditText) view.findViewById(R.id.indicators_carga_in_proccess);
        this.mRecordsToCorrect = (EditText) view.findViewById(R.id.indicators_carga_records_to_correct);
        this.mDerivedToControl = (EditText) view.findViewById(R.id.indicators_carga_derived_to_control);

        this.mProgressView = view.findViewById(R.id.progress);
        this.mMainContentView = view.findViewById(R.id.main_content);

        this.mGetIndicatorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFromDate == null || mToDate == null) {
                    SnackBarHelper.makeError(mMainContainer, R.string.indicators_error1);
                } else if (mSalesmanSelected == -1) {
                    SnackBarHelper.makeError(mMainContainer, R.string.indicators_error3);
                } else {
                    getIndicators();
                }
            }
        });

        View v1 = view.findViewById(R.id.from_date_button);
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar(mFromDateEditText.getText().toString().trim(), fromDateSetListener);
            }
        });

        View v2 = view.findViewById(R.id.to_date_button);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar(mToDateEditText.getText().toString().trim(), toDateSetListener);
            }
        });

        View v3 = view.findViewById(R.id.salesman_button);
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSalesmanAlert();
            }
        });


        /*this.mBtnArrowMoreLessPA = (ImageView) view.findViewById(R.id.btn_arrow_more_less_PA);
        this.mPAContainer = view.findViewById(R.id.PA_container);
        this.mPADivider = view.findViewById(R.id.PA_divider);*/
        /*this.mBtnArrowMoreLessQuote = (ImageView) view.findViewById(R.id.btn_arrow_more_less_quote);
        this.mQuoteContainer = view.findViewById(R.id.quote_container);
        this.mQuoteDivider = view.findViewById(R.id.quote_divider);*/
        this.mBtnArrowMoreLessCharge = (ImageView) view.findViewById(R.id.btn_arrow_more_less_charge);
        this.mChargeContainer = view.findViewById(R.id.charge_container);
        this.mChargeDivider = view.findViewById(R.id.charge_divider);
        /*this.mBtnArrowMoreLessTrazabilidad = (ImageView) view.findViewById(R.id.btn_arrow_more_less_trazabilidad);
        this.mTrazabilidadContainer = view.findViewById(R.id.trazabilidad_container);*/

        setAccordionsListeners();

        retrieveSalesman();
    }

    private void setAccordionsListeners() {
        /*this.mBtnArrowMoreLessPA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int height = mPAContainer.getLayoutParams().height;
                if (height == 0)
                    showAccordionView(mPAContainer, mBtnArrowMoreLessPA, mPADivider);
                else if (height == ActionBar.LayoutParams.WRAP_CONTENT)
                    hideAccordionView(mPAContainer, mBtnArrowMoreLessPA, mPADivider);
            }
        });*/

        /*this.mBtnArrowMoreLessQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int height = mQuoteContainer.getLayoutParams().height;
                if (height == 0)
                    showAccordionView(mQuoteContainer, mBtnArrowMoreLessQuote, mQuoteDivider);
                else if (height == ActionBar.LayoutParams.WRAP_CONTENT)
                    hideAccordionView(mQuoteContainer, mBtnArrowMoreLessQuote, mQuoteDivider);
            }
        });*/

        this.mBtnArrowMoreLessCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int height = mChargeContainer.getLayoutParams().height;
                if (height == 0)
                    showAccordionView(mChargeContainer, mBtnArrowMoreLessCharge, mChargeDivider);
                else if (height == ActionBar.LayoutParams.WRAP_CONTENT)
                    hideAccordionView(mChargeContainer, mBtnArrowMoreLessCharge, mChargeDivider);
            }
        });

        /*this.mBtnArrowMoreLessTrazabilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int height = mTrazabilidadContainer.getLayoutParams().height;
                if (height == 0)
                    showAccordionView(mTrazabilidadContainer, mBtnArrowMoreLessTrazabilidad, null);
                else if (height == ActionBar.LayoutParams.WRAP_CONTENT)
                    hideAccordionView(mTrazabilidadContainer, mBtnArrowMoreLessTrazabilidad, null);
            }
        });*/
    }

    private void retrieveSalesman() {
        showProgress(true);
        SalesmanController.getInstance().getSalesman(true, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> var1) {
                mSalesmans = new ArrayList<Salesman>(var1);
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                showProgress(false);
            }
        });
    }

    public void getIndicators() {
        showProgressDialog(R.string.indicators_loading);
        mIndicatorContainer.setVisibility(View.GONE);
        hideAllAccordionView();

        Long id = mSalesmans.get(mSalesmanSelected).id;

        /*if (mSalesmanSelected == 0) {
            id = -1L;
        } else {
            id = mSalesmans.get(mSalesmanSelected - 1).id;
        }*/

        HRequest request = RestApiServices.createGetZoneLeaderIndicatorsRequest(id, ParserUtils.parseDate(mFromDate, "yyyy-MM-dd"), ParserUtils.parseDate(mToDate, "yyyy-MM-dd"), new Response.Listener<ZoneLeaderIndicator>() {
            @Override
            public void onResponse(ZoneLeaderIndicator ind) {
                dismissProgressDialog();
                mIndicator = ind;
                if (mIndicator != null) {
                    showIndicator();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissProgressDialog();
                SnackBarHelper.makeError(mMainContainer, R.string.indicators_error2);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void showIndicator() {

/*
        if (mIndicator.tiempo_promedio >= 0)
            this.mTempPromedioEditText.setText("" + mIndicator.tiempo_promedio + " Hs.");
        else mTempPromedioEditText.setText("-");

        if (mIndicator.pa_sin_agendar >= 0)
            this.mPAScheduleEditText.setText("" + mIndicator.pa_sin_agendar);
        else mPAScheduleEditText.setText("-");

        if (mIndicator.pa_agendados >= 0)
            this.mPAScheduledEditText.setText("" + mIndicator.pa_agendados);
        else mPAScheduledEditText.setText("-");

        if (mIndicator.pa_cotizados >= 0)
            this.mPAQuotedEditText.setText("" + mIndicator.pa_cotizados);
        else mPAQuotedEditText.setText("-");

        if (mIndicator.pa_cerrados >= 0)
            this.mPACloseEditText.setText("" + mIndicator.pa_cerrados);
        else mPACloseEditText.setText("-");
*/
        /*if (mIndicator.tasa_cotizaciones_manual >= 0)
            this.mManualQuoteEditText.setText("" + mIndicator.tasa_cotizaciones_manual);
        else mManualQuoteEditText.setText("-");*/

        // producción TOTAL
        int cantGrav = (mIndicator.carga_grav >= 0) ? mIndicator.carga_grav : 0;
        int cantNoGrav = (mIndicator.carga_nograv >= 0) ? mIndicator.carga_nograv : 0;
        mIndicator.carga_produccion = cantGrav + cantNoGrav;
        this.mProduccionEditText.setText("" + mIndicator.carga_produccion + " ventas");

        if (mIndicator.cantidad_capitas >= 0)
            this.mCantCapitasEditText.setText("" + mIndicator.cantidad_capitas);
        else this.mCantCapitasEditText.setText("-");

        // produccion TOTAL viejo
        /*if (mIndicator.carga_produccion >= 0)
            this.mProduccionEditText.setText("" + mIndicator.carga_produccion);
        else mProduccionEditText.setText("-");*/

        if (mIndicator.carga_tasa_errores >= 0)
            this.mTasaErrorEditText.setText("" + mIndicator.carga_tasa_errores + "%");
        else mTasaErrorEditText.setText("-");

        if (mIndicator.carga_tiempo_promedio >= 0)
            this.mTiempoCargaEditText.setText("" + mIndicator.carga_tiempo_promedio + " días hábiles");
        else mTiempoCargaEditText.setText("-");

        if (mIndicator.carga_grav >= 0)
            this.mGravEditText.setText("" + mIndicator.carga_grav + " ventas");
        else mGravEditText.setText("-");

        if (mIndicator.carga_nograv >= 0)
            this.mNoGravEditText.setText("" + mIndicator.carga_nograv + " ventas");
        else mNoGravEditText.setText("-");

        if (mIndicator.carga_afinidad != null)
            this.mAfinidadEditText.setText(mIndicator.carga_afinidad);
        else mAfinidadEditText.setText("-");

        if (mIndicator.carga_empresa != null)
            this.mEmpresaEditText.setText(mIndicator.carga_empresa);
        else mEmpresaEditText.setText("-");

        if (mIndicator.carga_individual != null)
            this.mIndividualEditText.setText(mIndicator.carga_individual);
        else mIndividualEditText.setText("-");

        if (mIndicator.pago_tc >= 0)
            this.mTCEditText.setText("" + mIndicator.pago_tc);
        else mTCEditText.setText("-");

        if (mIndicator.pago_cbu >= 0)
            this.mCBUEditText.setText("" + mIndicator.pago_cbu);
        else mCBUEditText.setText("-");

        if (mIndicator.pago_pgf >= 0)
            this.mPFEditText.setText("" + mIndicator.pago_pgf);
        else mPFEditText.setText("-");

        if(mIndicator.pago_rce >= 0)
            this.mRceEditText.setText("" + mIndicator.pago_rce);
        else this.mRceEditText.setText("-");

        if(mIndicator.pago_ef >= 0)
            this.mEfEditText.setText("" + mIndicator.pago_ef);
        else this.mEfEditText.setText("-");

/*
        if (mIndicator.presentacion_fichas >= 0)
            this.mTrasabilidadTasaEditText.setText("" + mIndicator.presentacion_fichas + "%");
        else mTrasabilidadTasaEditText.setText("-");

        if (mIndicator.presentacion_errores >= 0)
            this.mTrasabilidadDocErrorEditText.setText("" + mIndicator.presentacion_errores + "%");
        else mTrasabilidadDocErrorEditText.setText("-");
        */

        if (mIndicator.cantidad_cotizado >= 0)
            this.mPAQuoted.setText("" + mIndicator.cantidad_cotizado);
        else this.mPAQuoted.setText("-");

        if (mIndicator.fichas_en_proceso >= 0)
            this.mInProcess.setText("" + mIndicator.fichas_en_proceso);
        else this.mInProcess.setText("-");

        if (mIndicator.fichas_en_correccion >= 0)
            this.mRecordsToCorrect.setText("" + mIndicator.fichas_en_correccion);
        else this.mRecordsToCorrect.setText("-");

        if (mIndicator.fichas_en_control >= 0)
            this.mDerivedToControl.setText("" + mIndicator.fichas_en_control);
        else this.mDerivedToControl.setText("-");

        mIndicatorContainer.setVisibility(View.VISIBLE);
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
        dateDialog.getDatePicker().setMaxDate(today.getTime());
        dateDialog.show();
    }

    public void showSalesmanAlert() {

        if (mSalesmans == null)
            return;

        final ArrayList<String> reasonsStr = new ArrayList<String>();

        /*String allAssesorsOption = getString(R.string.indicators_all_asesor_option).toUpperCase();
        reasonsStr.add(allAssesorsOption);*/

        for (Salesman q : mSalesmans) {
            reasonsStr.add(q.getFullName());
        }

        mSalesmanAlertAdapter = new SpinnerDropDownAdapter(getActivity(), reasonsStr, mSalesmanSelected);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccione_salesman))
                .setAdapter(mSalesmanAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        mSalesmanSelected = position;
                        mSalesmanEditText.setText(reasonsStr.get(position));
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mSalesmanAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
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


    private void showAccordionView(View view, ImageView img, View divider) {
        view.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        view.requestLayout();
        img.setImageResource(R.drawable.ic_expand_less);
        if (divider != null)
            divider.setVisibility(View.GONE);
    }

    private void hideAccordionView(View view, ImageView img, View divider) {
        view.getLayoutParams().height = 0;
        view.requestLayout();
        img.setImageResource(R.drawable.ic_expand);
        if (divider != null)
            divider.setVisibility(View.VISIBLE);
    }

    private void hideAllAccordionView() {
        //hideAccordionView(mPAContainer, mBtnArrowMoreLessPA, mPADivider);
        //hideAccordionView(mQuoteContainer, mBtnArrowMoreLessQuote, mQuoteDivider);
        hideAccordionView(mChargeContainer, mBtnArrowMoreLessCharge, mChargeDivider);
        //hideAccordionView(mTrazabilidadContainer, mBtnArrowMoreLessTrazabilidad, null);
    }


}

