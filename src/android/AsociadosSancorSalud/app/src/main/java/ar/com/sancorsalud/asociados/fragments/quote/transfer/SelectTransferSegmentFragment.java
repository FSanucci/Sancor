package ar.com.sancorsalud.asociados.fragments.quote.transfer;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.EmpresaArrayAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteBaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class SelectTransferSegmentFragment extends BaseFragment {
    private static final String ARG_QUOTATION = "quotation";


    private ScrollView mScrollView;
    private ProgressBar mProgressBar;

    private EditText mSegmentoEditText;
    private EditText mFormaIngresoEditText;
    private AutoCompleteTextView mEmpresaEditText;
    private AutoCompleteTextView mAfinidadEditText;

    private TextView mEmpresaLeyendaText;
    private TextInputLayout mAfinidadTextInputLayout;
    private TextInputLayout mEmpresaTextInputLayout;

    private SpinnerDropDownAdapter mSegmentoAlertAdapter;
    private int mSelectedSegmento = -1;

    private SpinnerDropDownAdapter mFormasIngresoAlertAdapter;
    private int mSelectedFormaIngreso = -1;

    private LinearLayout mEmpleadaBox;
    private RadioButton mSiEmpleadaRadioButton;
    private RadioButton mNoEmpleadaRadioButton;

    private Quotation mQuotation;

    private ArrayList<QuoteOption> mSegmentos;
    private ArrayList<QuoteOption> mFormasIngreso;

    private QuoteOption mEmpresaSelected;
    private QuoteOption mAfinidadSelected;

    public SelectTransferSegmentFragment() {
        // Required empty public constructor
    }

    public static SelectTransferSegmentFragment newInstance(Quotation param1) {
        SelectTransferSegmentFragment fragment = new SelectTransferSegmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_select_transfer_segment, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mScrollView = (ScrollView) view.findViewById(R.id.scroll);

        mFormaIngresoEditText = (EditText) view.findViewById(R.id.forma_ingreso_input);
        mSegmentoEditText = (EditText) view.findViewById(R.id.segmento_input);

        mEmpresaEditText = (AutoCompleteTextView) mMainContainer.findViewById(R.id.empresa_input);
        mEmpresaEditText.setThreshold(1);

        mAfinidadEditText = (AutoCompleteTextView) mMainContainer.findViewById(R.id.afinidad_input);
        mAfinidadEditText.setThreshold(1);

        mEmpresaLeyendaText = (TextView) mMainContainer.findViewById(R.id.empresa_leyenda);

        mAfinidadTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.afinidad_wrapper);
        mEmpresaTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.empresa_wrapper);

        mSegmentos = QuoteOptionsController.getInstance().getSegmentos();
        mFormasIngreso = QuoteOptionsController.getInstance().getFormasIngreso();

        mEmpleadaBox = (LinearLayout) view.findViewById(R.id.empleada_box);
        mSiEmpleadaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.si_empleada);
        mNoEmpleadaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.no_empleada);

        mProgressBar = (ProgressBar) mMainContainer.findViewById(R.id.progress);

        initializeForm();
        setupListeners();
    }

    private void setupListeners() {

        View segmentoButton = mMainContainer.findViewById(R.id.segmento_button);
        segmentoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showSegmentoAlert();
            }
        });

        segmentoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedSegmento = -1;
                mSegmentoEditText.setText("");
                return true;
            }
        });

        View formaIngresoButton = mMainContainer.findViewById(R.id.forma_ingreso_button);
        formaIngresoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showFormaIngresoAlert();
            }
        });

        formaIngresoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedFormaIngreso = -1;
                mFormaIngresoEditText.setText("");
                return true;
            }
        });

        mEmpresaEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmpresaSelected = null;
                if (s.toString().isEmpty()) {
                    mEmpresaLeyendaText.setVisibility(View.GONE);
                }
                searchEmpresa(s.toString());
            }
        });

        mEmpresaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateEmpresa(null);
                }
            }
        });

        mEmpresaEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "POSITION" + position);

                final QuoteOption quoteOption = ((EmpresaArrayAdapter) mEmpresaEditText.getAdapter()).quoteOptions.get(position);
                if (quoteOption.extra2 != null && !quoteOption.extra2.isEmpty()) {
                    mEmpresaLeyendaText.setVisibility(View.VISIBLE);
                    Log.e(TAG, "LEYENDA " + quoteOption.extra2);
                    mEmpresaLeyendaText.setText(quoteOption.extra2 != null ? quoteOption.extra2 : "");

                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        });

        mAfinidadEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAfinidadSelected = null;
                searchAfinidad(s.toString());
            }
        });

        mAfinidadEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateAfinidad(null);
                }
            }
        });

    }

    private void initializeForm() {
        if (mQuotation == null)
            return;

        if (mQuotation.segmento != null) {

            mSelectedSegmento = mSegmentos.indexOf(mQuotation.segmento);

            if (mSelectedSegmento != -1)
                mSegmentoEditText.setText(mQuotation.segmento.title);

        } else mSelectedSegmento = -1;


        if (mQuotation.formaIngreso != null) {
            mSelectedFormaIngreso = mFormasIngreso.indexOf(mQuotation.formaIngreso);

            if (mSelectedFormaIngreso != -1)
                mFormaIngresoEditText.setText(mQuotation.formaIngreso.title);

        } else mSelectedFormaIngreso = -1;


        if (mQuotation.nombreEmpresa != null && getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            mEmpresaSelected = mQuotation.nombreEmpresa;
            mEmpresaEditText.setText(mQuotation.nombreEmpresa.title);

            if (mQuotation.nombreEmpresa.extra2 != null && !mQuotation.nombreEmpresa.extra2.isEmpty()) {
                mEmpresaLeyendaText.setText(mQuotation.nombreEmpresa.extra2);
                mEmpresaLeyendaText.setVisibility(View.VISIBLE);
            } else {
                mEmpresaLeyendaText.setVisibility(View.GONE);
            }


        } else if (mQuotation.nombreAfinidad != null && getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            mAfinidadSelected = mQuotation.nombreAfinidad;
            mAfinidadEditText.setText(mQuotation.nombreAfinidad.title);
        }

        updateFormaIngreso();
        updateEmpleadaDomesticaChoice();
    }

    public void setQuotation(Quotation q) {
        mQuotation = q;
        initializeForm();
    }

    public Quotation getQuotation() {

        mQuotation.segmento = mSegmentos.get(mSelectedSegmento);
        mQuotation.formaIngreso = mFormasIngreso.get(mSelectedFormaIngreso);

        mQuotation.nombreEmpresa = null;
        mQuotation.nombreAfinidad = null;
        /*
        if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            mQuotation.nombreEmpresa = mEmpresaSelected;
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            mQuotation.nombreAfinidad = mAfinidadSelected;
        }
        */

        mQuotation.isEmpleadaDomestica = mSiEmpleadaRadioButton.isChecked();

        return mQuotation;
    }

    private void updateFormaIngreso() {

        Log.e(TAG, "updateFormaIngreso-------------");

        if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.INDIVIDUAL) {
            mAfinidadTextInputLayout.setVisibility(View.GONE);
            mEmpresaTextInputLayout.setVisibility(View.GONE);
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            mAfinidadTextInputLayout.setVisibility(View.GONE);
            mEmpresaTextInputLayout.setVisibility(View.VISIBLE);
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            mAfinidadTextInputLayout.setVisibility(View.VISIBLE);
            mEmpresaTextInputLayout.setVisibility(View.GONE);
        }
    }

    private void updateEmpleadaDomesticaChoice() {

        if (mSelectedSegmento != -1 && mSegmentos.get(mSelectedSegmento).id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
            mEmpleadaBox.setVisibility(View.VISIBLE);

            if (mQuotation.isEmpleadaDomestica) {
                mSiEmpleadaRadioButton.setChecked(true);
                mNoEmpleadaRadioButton.setChecked(false);
            } else {
                mSiEmpleadaRadioButton.setChecked(false);
                mNoEmpleadaRadioButton.setChecked(true);
            }

        } else {
            mEmpleadaBox.setVisibility(View.GONE);
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

    private boolean validateForm() {

        boolean isValid = true;

        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(R.id.segmento_wrapper);
        if (mSelectedSegmento == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_segmento));
            mSegmentoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else input.setErrorEnabled(false);

        if (mSelectedSegmento != -1) {
            mQuotation.segmento = mSegmentos.get(mSelectedSegmento);
            if (mQuotation.segmento.id.equals(mQuotation.previousSegmento.id)) {

                isValid = false;
                input.setErrorEnabled(true);
                input.setError(getString(R.string.seleccione_segmento_distinto));
                mSegmentoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            }
        }

        input = (TextInputLayout) mMainContainer.findViewById(R.id.forma_ingreso_wrapper);
        if (mSelectedFormaIngreso == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_ingreso));
            mFormaIngresoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else input.setErrorEnabled(false);

        if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            isValid = isValid & validateField(mEmpresaEditText, R.string.seleccione_empresa_error, R.id.empresa_wrapper);
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            isValid = isValid & validateField(mAfinidadEditText, R.string.seleccione_afinidad_error, R.id.afinidad_wrapper);
        }

        return isValid;
    }

    public void showSegmentoAlert() {

        ArrayList<String> reasonsStr = new ArrayList<String>();

        for (QuoteOption q : mSegmentos) {
            reasonsStr.add(q.optionName());
        }

        mSegmentoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), reasonsStr, mSelectedSegmento);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccione_segmento))
                .setAdapter(mSegmentoAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedSegmento = i;
                        mSegmentoEditText.setText(mSegmentos.get(i).optionName());
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mSegmentoAlertAdapter.notifyDataSetChanged();
                                resetError(R.id.segmento_wrapper);
                                resetError(R.id.forma_ingreso_wrapper);
                                updateEmpleadaDomesticaChoice();
                            }
                        });
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private ConstantsUtil.FormaIngreso getFormaIngresoSelected() {
        if (mSelectedFormaIngreso == -1)
            return null;

        if (mFormasIngreso.get(mSelectedFormaIngreso).id.equalsIgnoreCase(ConstantsUtil.INDIVIDUAL_FORMA_INGRESO))
            return ConstantsUtil.FormaIngreso.INDIVIDUAL;
        else if (mFormasIngreso.get(mSelectedFormaIngreso).id.equalsIgnoreCase(ConstantsUtil.AFINIDAD_FORMA_INGRESO))
            return ConstantsUtil.FormaIngreso.AFINIDAD;
        else if (mFormasIngreso.get(mSelectedFormaIngreso).id.equalsIgnoreCase(ConstantsUtil.EMPRESA_FORMA_INGRESO))
            return ConstantsUtil.FormaIngreso.EMPRESA;
        else
            return null;
    }

    public void showFormaIngresoAlert() {

        ArrayList<String> reasonsStr = new ArrayList<String>();

        for (QuoteOption q : mFormasIngreso) {
            reasonsStr.add(q.optionName());
        }

        mFormasIngresoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), reasonsStr, mSelectedFormaIngreso);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccione_ingreso))
                .setAdapter(mFormasIngresoAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mEmpresaSelected = null;
                        mAfinidadSelected = null;
                        mEmpresaEditText.setText("");
                        mAfinidadEditText.setText("");

                        mEmpresaLeyendaText.setText("");
                        mEmpresaLeyendaText.setVisibility(View.GONE);

                        mSelectedFormaIngreso = i;
                        updateFormaIngreso();
                        mFormaIngresoEditText.setText(mFormasIngreso.get(i).optionName());
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mFormasIngresoAlertAdapter.notifyDataSetChanged();
                            }
                        });

                        mScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean isValidSection() {
        return validateForm();
    }


    private void searchEmpresa(String query) {

        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        HRequest request = RestApiServices.createGetSearchEmpresaRequest(query, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                if (response != null && !response.isEmpty()) {
                    ArrayList<String> options = new ArrayList<String>();
                    for (QuoteOption q : response) {
                        options.add(q.title);
                    }
                    Log.e(TAG, "SEARCHHHH FINISH: " + response.size());

                    if (options.size() > 0) {
                        final EmpresaArrayAdapter adapter = new EmpresaArrayAdapter(getActivity(), android.R.layout.select_dialog_item, options, response);

                        SelectTransferSegmentFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mEmpresaEditText.setAdapter(adapter);
                                mEmpresaEditText.showDropDown();
                            }
                        });
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error on search empresas");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateEmpresa(final Response.Listener<QuoteOption> listener) {

        final String data = mEmpresaEditText.getText().toString().trim();
        if (data != null && !data.isEmpty()) {

            HRequest request = RestApiServices.createGetSearchEmpresaRequest(data.split("-")[0].trim(), new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {
                    if (response != null && !response.isEmpty()) {
                        mEmpresaSelected = filterDataById(data.split("-")[0].trim(), response);
                    } else {
                        mEmpresaSelected = new QuoteOption();
                        mEmpresaSelected.title = data;
                        mEmpresaSelected.id = null;
                        mEmpresaLeyendaText.setVisibility(View.INVISIBLE);
                    }
                    if (listener != null)
                        listener.onResponse(mEmpresaSelected);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mEmpresaLeyendaText.setVisibility(View.INVISIBLE);
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
        }
    }

    private void searchAfinidad(String query) {
        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        String zipCode = mQuotation.client.zip;
        HRequest request = RestApiServices.createGetSearchAfinidadRequest(query, zipCode, mSelectedSegmento, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                if (response != null && !response.isEmpty()) {
                    ArrayList<String> options = new ArrayList<String>();
                    for (QuoteOption q : response) {
                        options.add(q.title);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                    SelectTransferSegmentFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mAfinidadEditText.setAdapter(adapter);
                            mAfinidadEditText.showDropDown();
                        }
                    });
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error on search afinidad");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateAfinidad(final Response.Listener<QuoteOption> listener) {

        final String data = mAfinidadEditText.getText().toString().trim();
        if (data != null && !data.isEmpty()) {

            String zipCode = mQuotation.client.zip;
            HRequest request = RestApiServices.createGetSearchAfinidadRequest(data.split("-")[0].trim(), zipCode, mSelectedSegmento, new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {
                    if (response != null && !response.isEmpty()) {
                        mAfinidadSelected = filterDataById(data.split("-")[0].trim(), response);
                    } else {
                        mAfinidadSelected = new QuoteOption();
                        mAfinidadSelected.title = mAfinidadEditText.getText().toString();
                        mAfinidadSelected.id = null;
                    }
                    if (listener != null)
                        listener.onResponse(mAfinidadSelected);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
        }
    }

    private QuoteOption filterDataById(String id, ArrayList<QuoteOption> responseList) {
        QuoteOption result = null;
        for (QuoteOption quoteOption : responseList) {
            if (quoteOption.id.equals(id)) {
                result = quoteOption;
                break;
            }
        }
        return result;
    }

    public void showLoader(boolean est) {
        mScrollView.setVisibility(est ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(est ? View.VISIBLE : View.GONE);
    }

    private void resetError(int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        input.setErrorEnabled(false);
    }

}
