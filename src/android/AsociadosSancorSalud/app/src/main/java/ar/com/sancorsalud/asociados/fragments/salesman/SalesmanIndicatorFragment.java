package ar.com.sancorsalud.asociados.fragments.salesman;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.SalesmanIndicator;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class SalesmanIndicatorFragment extends BaseFragment {

    private View mMainContainer;
    private SalesmanIndicator mIndicator;
    public EditText mCantidadFichasEditText;
    public TextView mProduccionTextView;
    public EditText mCantidadGravEditText;
    public EditText mCantidadNogravEditText;
    public EditText mFromDateEditText;
    public EditText mToDateEditText;
    public View mIndicatorContainer;
    public View mGetIndicatorsButton;
    public Date mFromDate;
    public Date mToDate;
    private SimpleDateFormat mDateFormat;

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
        return inflater.inflate(R.layout.fragment_salesman_indicator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.mMainContainer = view;
        this.mCantidadFichasEditText = (EditText) view.findViewById(R.id.cantidad_fichas);
        this.mProduccionTextView = (TextView) view.findViewById(R.id.produccion);
        this.mCantidadGravEditText = (EditText) view.findViewById(R.id.cantidad_grav);
        this.mCantidadNogravEditText = (EditText) view.findViewById(R.id.cantidad_no_grav);
        this.mGetIndicatorsButton = view.findViewById(R.id.button);
        this.mFromDateEditText = (EditText) view.findViewById(R.id.from_date_input);
        this.mToDateEditText = (EditText) view.findViewById(R.id.to_date_input);
        this.mIndicatorContainer = view.findViewById(R.id.indicator_container);

        this.mGetIndicatorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFromDate == null || mToDate == null) {
                    SnackBarHelper.makeError(mMainContainer, R.string.indicators_error1);
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

    }

    public void getIndicators() {
        showProgressDialog(R.string.indicators_loading);
        HRequest request = RestApiServices.createGetSalesmanIndicatorsRequest(UserController.getInstance().getSignedUser().id, ParserUtils.parseDate(mFromDate, "yyyy-MM-dd"), ParserUtils.parseDate(mToDate, "yyyy-MM-dd"), new Response.Listener<SalesmanIndicator>() {
            @Override
            public void onResponse(SalesmanIndicator ind) {
                dismissProgressDialog();
                mIndicator = ind;
                showIndicator();
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

        if (mIndicator.cantidadFichas >= 0)
            mCantidadFichasEditText.setText("" + mIndicator.cantidadFichas);
        else mCantidadFichasEditText.setText("-");

        mProduccionTextView.setText(getActivity().getString(R.string.indicators_title2) + ((mIndicator.produccion != null) ? " (" + mIndicator.produccion + ")" : ""));

        if (mIndicator.cantidadGrav >= 0)
            mCantidadGravEditText.setText("" + mIndicator.cantidadGrav);
        else mCantidadGravEditText.setText("-");

        if (mIndicator.cantidadNograv >= 0)
            mCantidadNogravEditText.setText("" + mIndicator.cantidadNograv);
        else mCantidadNogravEditText.setText("-");

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

}
