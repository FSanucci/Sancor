package ar.com.sancorsalud.asociados.fragments.quote;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.quotation.QuoteActivity;
import ar.com.sancorsalud.asociados.adapter.EmpresaArrayAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class QuoteBaseFragment extends BaseFragment {

    private static final String TAG = "QUOTE_BASEFRG";

    private static final String ARG_QUOTATION = "quotation";
    private static final String ARG_FIXED_COMBOS = "fixedCombos";

    private ScrollView mScrollView;
    private ProgressBar mProgressBar;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mDniEditText;
    private EditText mBirthdayEditText;
    private AutoCompleteTextView mZipEditText;
    private EditText mCoberturaEditText;
    private EditText mCategoriaEditText;
    private EditText mCondicionIvaEditText;
    private EditText mSegmentoEditText;
    private EditText mFormaIngresoEditText;
    private AutoCompleteTextView mEmpresaEditText;
    private AutoCompleteTextView mAfinidadEditText;

    private LinearLayout mEmpleadaBox;
    private RadioButton mSiEmpleadaRadioButton;
    private RadioButton mNoEmpleadaRadioButton;

    private TextInputLayout mAfinidadTextInputLayout;
    private TextInputLayout mEmpresaTextInputLayout;

    private TextView mEmpresaLeyendaText;
    private View segmentoButton;
    private View formaIngresoButton;

    private SimpleDateFormat mDateFormat;
    private SpinnerDropDownAdapter mCoberturaAlertAdapter;
    private int mSelectedCobertura = -1;
    private SpinnerDropDownAdapter mCategoriaAlertAdapter;
    private int mSelectedCategory = -1;
    private SpinnerDropDownAdapter mCondicionIvaAlertAdapter;
    private int mSelectedCondicionIva = -1;
    private SpinnerDropDownAdapter mSegmentoAlertAdapter;
    private int mSelectedSegmento = -1;
    private SpinnerDropDownAdapter mFormasIngresoAlertAdapter;
    private int mSelectedFormaIngreso = -1;

    private Quotation mQuotation;
    private ArrayList<QuoteOption> mCoberturas;
    private ArrayList<QuoteOption> mCategories;
    private ArrayList<QuoteOption> mCondicionesIva;
    private ArrayList<QuoteOption> mSegmentos;
    private ArrayList<QuoteOption> mFormasIngreso;

    private QuoteOption mEmpresaSelected;
    private QuoteOption mAfinidadSelected;

    private String mZip;
    private String mBirthday;

    private boolean fixedCombos = false;

    private QuoteOption mLocationDaTaSelected;
    private ArrayList<QuoteOption> locationDataArray = new ArrayList<QuoteOption>();

    private boolean hasToLoadZipCode = false;

    public QuoteBaseFragment() {
        // Required empty public constructor
    }


    public static QuoteBaseFragment newInstance(Quotation param1) {
        QuoteBaseFragment fragment = new QuoteBaseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, param1);
        args.putBoolean(ARG_FIXED_COMBOS, false);

        fragment.setArguments(args);
        return fragment;
    }

    public static QuoteBaseFragment newInstance(Quotation param1, boolean fixedCombos) {
        QuoteBaseFragment fragment = new QuoteBaseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, param1);
        args.putBoolean(ARG_FIXED_COMBOS, fixedCombos);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
            fixedCombos = getArguments().getBoolean(ARG_FIXED_COMBOS, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_quote_base, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll);

        mFirstNameEditText = (EditText) view.findViewById(R.id.firstname_input);
        setTypeTextNoSuggestions(mFirstNameEditText);

        mLastNameEditText = (EditText) view.findViewById(R.id.lastname_input);
        setTypeTextNoSuggestions(mLastNameEditText);

        mDniEditText = (EditText) view.findViewById(R.id.dni_input);
        setTypeTextNoSuggestions(mDniEditText);

        mBirthdayEditText = (EditText) view.findViewById(R.id.birthday_input);
        setTypeTextNoSuggestions(mBirthdayEditText);

        mZipEditText = (AutoCompleteTextView) view.findViewById(R.id.zip_input);
        setTypeTextNoSuggestions(mZipEditText);

        mCoberturaEditText = (EditText) view.findViewById(R.id.cobertura_input);
        setTypeTextNoSuggestions(mCoberturaEditText);

        mCategoriaEditText = (EditText) view.findViewById(R.id.categoria_input);
        setTypeTextNoSuggestions(mCategoriaEditText);

        mCondicionIvaEditText = (EditText) view.findViewById(R.id.condicion_iva_input);
        setTypeTextNoSuggestions(mCondicionIvaEditText);

        mSegmentoEditText = (EditText) view.findViewById(R.id.segmento_input);
        setTypeTextNoSuggestions(mSegmentoEditText);

        mFormaIngresoEditText = (EditText) view.findViewById(R.id.forma_ingreso_input);
        setTypeTextNoSuggestions(mFormaIngresoEditText);

        mEmpresaEditText = (AutoCompleteTextView) mMainContainer.findViewById(R.id.empresa_input);
        setTypeTextNoSuggestions(mEmpresaEditText);
        mEmpresaEditText.setThreshold(1);

        mAfinidadEditText = (AutoCompleteTextView) mMainContainer.findViewById(R.id.afinidad_input);
        mAfinidadEditText.setThreshold(1);

        mEmpresaLeyendaText = (TextView) mMainContainer.findViewById(R.id.empresa_leyenda);
        setTypeTextNoSuggestions(mEmpresaLeyendaText);

        mAfinidadTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.afinidad_wrapper);
        mEmpresaTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.empresa_wrapper);

        segmentoButton = mMainContainer.findViewById(R.id.segmento_button);
        formaIngresoButton = mMainContainer.findViewById(R.id.forma_ingreso_button);

        mEmpleadaBox = (LinearLayout) view.findViewById(R.id.empleada_box);
        mSiEmpleadaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.si_empleada);
        mNoEmpleadaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.no_empleada);

        mProgressBar = (ProgressBar) mMainContainer.findViewById(R.id.progress);

        //((QuoteActivity)getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        fillArraysData();
        initializeForm();
        setupListeners();
    }


    private void fillArraysData() {

        mCoberturas = new ArrayList<>();
        QuoteOption coberturaSelection = new QuoteOption("-1", getResources().getString(R.string.field_cobertura));
        mCoberturas.add(coberturaSelection);
        mCoberturas.addAll(QuoteOptionsController.getInstance().getCoberturas());

        mCategories = new ArrayList<>();
        QuoteOption categoriaSelection = new QuoteOption("-1", getResources().getString(R.string.field_categoria));
        mCategories.add(categoriaSelection);
        mCategories.addAll(QuoteOptionsController.getInstance().getCategorias());

        mCondicionesIva = new ArrayList<>();
        QuoteOption ivaSelection = new QuoteOption("-1", getResources().getString(R.string.field_condicion_iva));
        mCondicionesIva.add(ivaSelection);
        mCondicionesIva.addAll(QuoteOptionsController.getInstance().getCondicionIva());

        mSegmentos = new ArrayList<>();
        QuoteOption segmentoSelection = new QuoteOption("-1", getResources().getString(R.string.field_segmento));
        mSegmentos.add(segmentoSelection);
        mSegmentos.addAll(QuoteOptionsController.getInstance().getSegmentos());

        mFormasIngreso = new ArrayList<>();
        QuoteOption formaIngresoSelection = new QuoteOption("-1", getResources().getString(R.string.field_forma_ingreso));
        mFormasIngreso.add(formaIngresoSelection);
        mFormasIngreso.addAll(QuoteOptionsController.getInstance().getFormasIngreso());
    }

    private void setupListeners() {

        View coberturaHelp = mMainContainer.findViewById(R.id.cobertura_help);
        coberturaHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                DialogHelper.showMessage(getActivity(), getResources().getString(R.string.help_cobertura_list));
            }
        });


        View coberturaButton = mMainContainer.findViewById(R.id.cobertura_button);
        coberturaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showCoberturaAlert();
            }
        });

        coberturaButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedCobertura = -1;
                mCoberturaEditText.setText("");
                return true;
            }
        });


        View categoriaButton = mMainContainer.findViewById(R.id.categoria_button);
        categoriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showCategoriasAlert();
            }
        });

        categoriaButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedCategory = -1;
                mCategoriaEditText.setText("");
                return true;
            }
        });


        View condicionIvaButton = mMainContainer.findViewById(R.id.condicion_iva_button);
        condicionIvaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showCondicionIvaAlert();
            }
        });

        condicionIvaButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedCondicionIva = -1;
                mCondicionIvaEditText.setText("");
                return true;
            }
        });


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
                if(!hasFocus){
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

        // Default combos
        QuoteOption categoriaDefault = new QuoteOption(ConstantsUtil.ADHERENTE_CATEGORIA, QuoteOptionsController.getInstance().getCategoriaName(ConstantsUtil.ADHERENTE_CATEGORIA));
        QuoteOption condicionIvaDefault = new QuoteOption(ConstantsUtil.CONDICION_IVA_CONSUMIDOR_FINAL, QuoteOptionsController.getInstance().getCondicionIvaName(ConstantsUtil.CONDICION_IVA_CONSUMIDOR_FINAL));

        if (mQuotation == null)
            return;

        if (mQuotation.client.firstname != null)
            mFirstNameEditText.setText(mQuotation.client.firstname);

        if (mQuotation.client.lastname != null)
            mLastNameEditText.setText(mQuotation.client.lastname);

        if (mQuotation.client.dni > 0)
            mDniEditText.setText("" + mQuotation.client.dni);

        if (mQuotation.client.birthday != null) {
            mBirthday = mQuotation.client.getBirthday();
            if (mQuotation.client.age != 0) {
                mBirthdayEditText.setText(mBirthday + " (" + mQuotation.client.age + " " + getActivity().getResources().getString(R.string.option_years) + ")");
            } else {
                mBirthdayEditText.setText(mBirthday);
            }
        }

        if (mQuotation.client.zip != null && !mQuotation.client.zip.isEmpty()) {
            hasToLoadZipCode = false;

            mZipEditText.setFocusable(false);

            mZip = mQuotation.client.zip;
            if (mQuotation.client.description != null && !mQuotation.client.description.isEmpty()) {
                mZipEditText.setText(mZip + " (" + mQuotation.client.description + ")");
            } else {
                mZipEditText.setText(mZip);
            }
        } else {
            Log.e(TAG, "No zipppp ------------");
            hasToLoadZipCode = true;

            mZipEditText.setFocusable(true);
            mZipEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.e(TAG, "onTextChanged-----: " + s.toString() + "-----");
                    mLocationDaTaSelected = null;

                    searchLocation(s.toString(), new Response.Listener<ArrayList<QuoteOption>>() {
                        @Override
                        public void onResponse(ArrayList<QuoteOption> response) {

                            if (response != null && response.size() > 0) {
                                locationDataArray = response;

                                ArrayList<String> options = new ArrayList<String>();
                                for (QuoteOption q : response) {
                                    options.add(q.title);
                                }

                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        mZipEditText.setAdapter(adapter);
                                        mZipEditText.showDropDown();
                                    }
                                });
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error on search loacations");
                        }
                    });
                }
            });

            mZipEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        updateLocation(null);
                    }
                }
            });
        }

        if (mQuotation.coberturaProveniente != null) {
            mSelectedCobertura = mCoberturas.indexOf(mQuotation.coberturaProveniente);

            if (mSelectedCobertura != -1)
                mCoberturaEditText.setText(mQuotation.coberturaProveniente.title);

        } else mSelectedCobertura = -1;

        if (mQuotation.categoria != null) {
            mSelectedCategory = mCategories.indexOf(mQuotation.categoria);

            if (mSelectedCategory != -1)
                mCategoriaEditText.setText(mQuotation.categoria.title);

        } else {
            // Category Default
            mSelectedCategory = mCategories.indexOf(categoriaDefault);
            mCategoriaEditText.setText(categoriaDefault.title);
        }

        if (mQuotation.condicionIva != null) {
            mSelectedCondicionIva = mCondicionesIva.indexOf(mQuotation.condicionIva);

            if (mSelectedCondicionIva != -1)
                mCondicionIvaEditText.setText(mQuotation.condicionIva.title);

        } else {
            // Condicion IVA Default
            mSelectedCondicionIva = mCondicionesIva.indexOf(condicionIvaDefault);
            mCondicionIvaEditText.setText(condicionIvaDefault.title);
        }

        if (fixedCombos) {

            segmentoButton.setEnabled(false);
            formaIngresoButton.setEnabled(false);

            QuoteOption fixedSegment = new QuoteOption(ConstantsUtil.AUTONOMO_SEGMENTO, QuoteOptionsController.getInstance().getSegmentoName(ConstantsUtil.AUTONOMO_SEGMENTO));
            mSelectedSegmento = mSegmentos.indexOf(fixedSegment);
            mSegmentoEditText.setText(fixedSegment.title);

            QuoteOption fixedFormaIngreso = new QuoteOption(ConstantsUtil.INDIVIDUAL_FORMA_INGRESO, QuoteOptionsController.getInstance().getFormaIngresoName(ConstantsUtil.INDIVIDUAL_FORMA_INGRESO));
            mSelectedFormaIngreso = mFormasIngreso.indexOf(fixedSegment);
            mFormaIngresoEditText.setText(fixedFormaIngreso.title);

        } else {

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

            //updateEmpleadaDomesticaChoice();
        }


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

        mQuotation.client.firstname = mFirstNameEditText.getText().toString();
        mQuotation.client.lastname = mLastNameEditText.getText().toString();
        mQuotation.client.dni = Long.parseLong(mDniEditText.getText().toString());

        try {
            mQuotation.client.birthday = ParserUtils.parseDate(mBirthday, "dd-MM-yyyy");
        } catch (Exception e) {
        }

        mQuotation.client.zip = mZip;

        if (mSelectedCobertura != -1) {
            mQuotation.coberturaProveniente = mCoberturas.get(mSelectedCobertura);
        } else {
            mQuotation.coberturaProveniente = null;
        }
        mQuotation.categoria = mCategories.get(mSelectedCategory);
        mQuotation.condicionIva = mCondicionesIva.get(mSelectedCondicionIva);
        mQuotation.segmento = mSegmentos.get(mSelectedSegmento);
        mQuotation.formaIngreso = mFormasIngreso.get(mSelectedFormaIngreso);

        mQuotation.nombreEmpresa = null;
        mQuotation.nombreAfinidad = null;
        if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            mQuotation.nombreEmpresa = mEmpresaSelected;
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            mQuotation.nombreAfinidad = mAfinidadSelected;
        }

        mQuotation.isEmpleadaDomestica = mSiEmpleadaRadioButton.isChecked();

        return mQuotation;
    }

    public boolean hasToLoadZipCode() {
        return hasToLoadZipCode;
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
        } else {
            editText.setPaintFlags(View.INVISIBLE);
            input.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateForm() {

        boolean isValid = true;
        isValid = isValid & validateField(mFirstNameEditText, R.string.add_first_name_error, R.id.first_name_wrapper);
        isValid = isValid & validateField(mLastNameEditText, R.string.add_last_name_error, R.id.last_name_wrapper);
        isValid = isValid & validateField(mDniEditText, R.string.add_dni_error, R.id.dni_wrapper);
        isValid = isValid & validateField(mBirthdayEditText, R.string.add_birth_day_error, R.id.birth_day_wrapper);

        // cobertura is not mandatory

        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(R.id.categoria_wrapper);
        if (mSelectedCategory == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_categoria));
            mCategoriaEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else {
            input.setErrorEnabled(false);
        }

        input = (TextInputLayout) mMainContainer.findViewById(R.id.condicion_iva_wrapper);
        if (mSelectedCondicionIva == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_condicion_iva));
            mCondicionIvaEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else {
            input.setErrorEnabled(false);
        }

        input = (TextInputLayout) mMainContainer.findViewById(R.id.segmento_wrapper);
        if (mSelectedSegmento == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_segmento));
            mSegmentoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else {
            input.setErrorEnabled(false);
        }

        input = (TextInputLayout) mMainContainer.findViewById(R.id.forma_ingreso_wrapper);
        if (mSelectedFormaIngreso == -1) {
            isValid = false;

            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_ingreso));
            mFormaIngresoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        } else {
            input.setErrorEnabled(false);
        }

        if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.EMPRESA) {
            isValid = isValid & validateField(mEmpresaEditText, R.string.seleccione_empresa_error, R.id.empresa_wrapper);
        } else if (getFormaIngresoSelected() == ConstantsUtil.FormaIngreso.AFINIDAD) {
            isValid = isValid & validateField(mAfinidadEditText, R.string.seleccione_afinidad_error, R.id.afinidad_wrapper);
        }

        return isValid;
    }

    public void showCoberturaAlert() {


        ArrayList<String> cobStr = new ArrayList<String>();
        for (QuoteOption q : mCoberturas) {
            cobStr.add(q.optionName());
        }


        mCoberturaAlertAdapter = new SpinnerDropDownAdapter(getActivity(), cobStr, mSelectedCobertura);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCoberturaAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mSelectedCobertura = i;
                if (i == 0) {
                    mSelectedCobertura = -1;
                    mCoberturaEditText.setText("");
                } else {
                    mCoberturaEditText.setText(mCoberturas.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCoberturaAlertAdapter.notifyDataSetChanged();

                            if (!fixedCombos && mSelectedCobertura != -1) {
                                QuoteOption ingresoDefault = new QuoteOption(ConstantsUtil.AFINIDAD_FORMA_INGRESO, QuoteOptionsController.getInstance().getFormaIngresoName(ConstantsUtil.AFINIDAD_FORMA_INGRESO));
                                mSelectedFormaIngreso = mFormasIngreso.indexOf(ingresoDefault);
                                mFormaIngresoEditText.setText(ingresoDefault.title);
                                mAfinidadTextInputLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void showCategoriasAlert() {

        ArrayList<String> categStr = new ArrayList<String>();
        for (QuoteOption q : mCategories) {
            categStr.add(q.optionName());
        }

        mCategoriaAlertAdapter = new SpinnerDropDownAdapter(getActivity(), categStr, mSelectedCategory);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCategoriaAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedCategory = i;
                if (i == 0) {
                    mSelectedCategory = -1;
                    mCategoriaEditText.setText("");
                } else {
                    mCategoriaEditText.setText(mCategories.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCategoriaAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void showCondicionIvaAlert() {
        // si el segmento es monotributo, este campo se setea automaticamente en Monotributista y no se podrá editar.
        if (!mSegmentoEditText.getText().toString().equals("Monotributo")) {
            ArrayList<String> condIvaStr = new ArrayList<String>();
            for (QuoteOption q : mCondicionesIva) {
                condIvaStr.add(q.optionName());
            }

            mCondicionIvaAlertAdapter = new SpinnerDropDownAdapter(getActivity(), condIvaStr, mSelectedCondicionIva);
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setAdapter(mCondicionIvaAlertAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSelectedCondicionIva = i;
                    if (i == 0) {
                        mSelectedCondicionIva = -1;
                        mCondicionIvaEditText.setText("");
                    } else {
                        mCondicionIvaEditText.setText(mCondicionesIva.get(i).optionName());
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mCondicionIvaAlertAdapter.notifyDataSetChanged();
                            }
                        });
                        // si la condicion de iva es 'monotributista' seteo por default el segmento en 'monotributo'
                        if (mCondicionesIva.get(i).optionName().equals("Monotributista")) {
                            for (int index = 0; index < mSegmentos.size(); index++) {
                                if (mSegmentos.get(index).optionName().equals("Monotributo")) {
                                    mSegmentoEditText.setText("Monotributo");
                                    mSelectedSegmento = index;
                                    Log.e(TAG, "IVA: " + mSelectedCondicionIva + "  -------------------------------------");
                                    Log.e(TAG, "SEGMENTO: " + mSelectedSegmento + "  -------------------------------------");
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            updateEmpleadaDomesticaChoice();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });

            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void showSegmentoAlert() {

        ArrayList<String> segmStr = new ArrayList<String>();
        for (QuoteOption q : mSegmentos) {
            segmStr.add(q.optionName());
        }

        mSegmentoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), segmStr, mSelectedSegmento);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mSegmentoAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedSegmento = i;
                if (i == 0) {
                    mSelectedSegmento = -1;
                    mSegmentoEditText.setText("");
                } else {
                    mSegmentoEditText.setText(mSegmentos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mSegmentoAlertAdapter.notifyDataSetChanged();
                            updateEmpleadaDomesticaChoice();
                        }
                    });
                    // si el segmento es 'monotributo' seteo por default la condicion de iva en 'monotributista'
                    if (mSegmentos.get(i).optionName().equals("Monotributo")) {
                        for (int index = 0; index < mCondicionesIva.size(); index++) {
                            if (mCondicionesIva.get(index).optionName().equals("Monotributista")) {
                                mCondicionIvaEditText.setText("Monotributista");
                                mSelectedCondicionIva = index;
                                Log.e(TAG, "Segmento: " + mSelectedSegmento + "  -------------------------------------");
                                Log.e(TAG, "IVA: " + mSelectedCondicionIva + "  -------------------------------------");
                            }
                        }
                    }
                }

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

        ArrayList<String> formaIngStr = new ArrayList<String>();
        for (QuoteOption q : mFormasIngreso) {
            formaIngStr.add(q.optionName());
        }


        mFormasIngresoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), formaIngStr, mSelectedFormaIngreso);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mFormasIngresoAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mEmpresaSelected = null;
                mAfinidadSelected = null;
                mEmpresaEditText.setText("");
                mAfinidadEditText.setText("");

                mEmpresaLeyendaText.setText("");
                mEmpresaLeyendaText.setVisibility(View.GONE);

                mSelectedFormaIngreso = i;
                if (i == 0) {
                    mSelectedFormaIngreso = -1;
                    mSegmentoEditText.setText("");
                } else {
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

                        QuoteBaseFragment.this.getActivity().runOnUiThread(new Runnable() {
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
                    Log.e(TAG, "Error updatig empresa ...");
                    if (listener != null) {
                        listener.onResponse(null);
                    }
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
        }
    }

    private void searchAfinidad(String query) {
        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        if (query != null && !query.isEmpty()){

            String urlQuery = URLEncoder.encode(query);

            String zipCode = mQuotation.client.zip;
            //HRequest request = RestApiServices.createGetSearchAfinidadRequest(query, zipCode, new Response.Listener<ArrayList<QuoteOption>>() {
            HRequest request = RestApiServices.createGetSearchAfinidadRequest(urlQuery, zipCode, mSelectedSegmento, new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {

                    if (response != null && !response.isEmpty()) {
                        ArrayList<String> options = new ArrayList<String>();
                        for (QuoteOption q : response) {
                            options.add(q.title);
                        }

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                        QuoteBaseFragment.this.getActivity().runOnUiThread(new Runnable() {
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
                    Log.e(TAG, "searchAfinidad(): error on search afinidad");
                }
            });

/************************ Start MERGE (prod-para-14-12-18 -> development)  ************************/
/*
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error on search afinidad");
            }
        });
*/
            AppController.getInstance().getRestEngine().addToRequestQueue(request);

        } else {
            Log.e(TAG, "searchAfinidad(): Cannot search afinidad with an empty or null value");
        }
/************************* End MERGE (prod-para-14-12-18 -> development)  *************************/

    }

    public void updateAfinidad(final Response.Listener<QuoteOption> listener) {

        final String data = mAfinidadEditText.getText().toString().trim();
        if (data != null && !data.isEmpty()) {
/*
            String zipCode = mQuotation.client.zip;
            HRequest request = RestApiServices.createGetSearchAfinidadRequest(data.split("-")[0].trim(), zipCode, new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {
                    if (response != null && !response.isEmpty()) {
                        mAfinidadSelected = filterDataById(data.split("-")[0].trim(), response);
                    } else {
                        mAfinidadSelected = new QuoteOption();
                        mAfinidadSelected.title = "";
                        mAfinidadSelected.id = null;
                    }
                    if (listener != null)
                        listener.onResponse(mAfinidadSelected);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error updatig afinidad ...");
                    if (listener != null) {
                        listener.onResponse(null);
                    }
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
*/

            String query = data.split("-")[0].trim();
            if (query != null && !query.isEmpty()){

                String urlQuery = URLEncoder.encode(query);

                String zipCode = mQuotation.client.zip;
                HRequest request = RestApiServices.createGetSearchAfinidadRequest(urlQuery, zipCode, mSelectedSegmento, new Response.Listener<ArrayList<QuoteOption>>() {
                    @Override
                    public void onResponse(ArrayList<QuoteOption> response) {
                        if (response != null && !response.isEmpty()) {
                            mAfinidadSelected = filterDataById(data.split("-")[0].trim(), response);
                        } else {
                            mAfinidadSelected = new QuoteOption();
                            mAfinidadSelected.title = "";
                            mAfinidadSelected.id = null;
                        }
                        if (listener != null)
                            listener.onResponse(mAfinidadSelected);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error updatig afinidad ...");
                        if (listener != null) {
                            listener.onResponse(null);
                        }
                    }
                });
                AppController.getInstance().getRestEngine().addToRequestQueue(request);

            } else {
                Log.e(TAG, "updateAfinidad(): Cannot search afinidad with an empty or null value");
            }

        } else {
            Log.e(TAG, "updateAfinidad(): Cannot update afinidad with an empty or null data");
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


    private void searchLocation(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {

        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        HRequest request = RestApiServices.createSearchLocationRequest(query, listener, errorListener);
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateLocation(final Response.Listener<QuoteOption> listener) {

        final String detail = mZipEditText.getText().toString().trim();
        if (detail != null && !detail.isEmpty()) {
            QuoteOption option = findLocation(detail);

            if (option != null) {
                mLocationDaTaSelected = option;
            } else {
                mLocationDaTaSelected = new QuoteOption();
                mLocationDaTaSelected.title = detail; // loacation desc
                mLocationDaTaSelected.id = null;  // zipCode
            }
            if (listener != null)
                listener.onResponse(mLocationDaTaSelected);
        } else {
            if (listener != null)
                listener.onResponse(null);
        }
    }

    private QuoteOption findLocation(String detail) {
        QuoteOption result = null;
        for (QuoteOption option : locationDataArray) {
            if (option.title.equals(detail)) {
                result = option;
                break;
            }
        }
        return result;
    }

}
