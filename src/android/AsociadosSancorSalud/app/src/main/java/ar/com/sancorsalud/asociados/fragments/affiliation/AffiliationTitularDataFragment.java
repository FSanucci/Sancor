package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DateUtils;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AffiliationTitularDataFragment extends BaseFragment {

    private static final String TAG = "AF_TIT_FRG";

    private static final String ARG_TITULAR_DATA = "titularData";
    private static final String ARG_FICHA_ID = "fichaId";

    private static final String AFFINITY_FILE_PREFIX = "afinidad";
    private static final String AGREEMENT_FILE_PREFIX = "convenio"; //convenio

    private View view;

    private View mMainContainer;
    private ScrollView mScrollView;

    private EditText mSegmentoEditText;
    private EditText mFormaIngresoEditText;
    private EditText mCategoriaEditText;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mSexEditText;
    private EditText mDocTypeEditText;
    private EditText mDocNumberEditText;
    private EditText mBirthdayEditText;
    private EditText mCuitEditText;
    private EditText mCivilSatatusEditText;
    private EditText mNationalityEditText;
    private EditText mCondicionIvaEditText;
    private EditText mCoberturaEditText;

    private ArrayList<QuoteOption> mSegmentos;
    private ArrayList<QuoteOption> mFormasIngreso;
    private ArrayList<QuoteOption> mCategories;
    private ArrayList<QuoteOption> mSexos;
    private ArrayList<QuoteOption> mDocTypes;
    private ArrayList<QuoteOption> mCoberturas;
    private ArrayList<QuoteOption> mCondicionesIva;
    private ArrayList<QuoteOption> mCivilStatus;
    private ArrayList<QuoteOption> mNationalities;

    private int mSelectedSegmento = -1;
    private int mSelectedFormaIngreso = -1;
    private int mSelectedCategory = -1;
    private int mSelectedCondicionIva = -1;
    private int mSelectedCobertura = -1;

    private SpinnerDropDownAdapter mSexoAlertAdapter;
    private int mSelectedSexo = -1;
    private SpinnerDropDownAdapter mDocTypeAlertAdapter;
    private int mSelectedDocType = -1;
    private SpinnerDropDownAdapter mCivilStatusAdapter;
    private int mSelectedCivilStatus = -1;
    private SpinnerDropDownAdapter mNationalityAlertAdapter;
    private int mSelectedNationality = -1;

    private AutoCompleteTextView mEntityEditText;
    private AutoCompleteTextView mDateroNumberEditText;
    private QuoteOption mEntitySelected;
    private QuoteOption mDateroNumberSelected;


    //affinity
    private EditText mAffinityGroup;
    private TextView mAffinityMessage;
    private LinearLayout mAffinityContainer;
    private Button mAffinityAddButton;

    private RecyclerView mAffinityRecyclerView;
    private AttachFilesAdapter mAffinityFileAdapter;

    private List<AttachFile> affinityAttachFiles = new ArrayList<>();
    private AttachFile mAffinityAttachFile;

    // agreement
    private EditText mAgreementInput;
    private TextView mAgreementMessage;
    private LinearLayout mAgreementContainer;
    private Button mAgreementAddButton;

    private RecyclerView mAgreementRecyclerView;
    private AttachFilesAdapter mAgreementFileAdapter;

    private List<AttachFile> magreementAttachFiles = new ArrayList<>();
    private AttachFile mAgreementAttachFile;

    private boolean searching = false;
    private TitularData mTitularData;
    private long mFichaId;

    private boolean editableCard = true;


    private boolean loadFromQR = true;

    public static AffiliationTitularDataFragment newInstance(TitularData param1, long fichaId) {
        AffiliationTitularDataFragment fragment = new AffiliationTitularDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TITULAR_DATA, param1);
        args.putLong(ARG_FICHA_ID, fichaId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitularData = (TitularData) getArguments().getSerializable(ARG_TITULAR_DATA);
            mFichaId = getArguments().getLong(ARG_FICHA_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_titular_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mImageProvider = new ImageProvider(getActivity());

        mSegmentoEditText = (EditText) view.findViewById(R.id.segmento_input);
        setTypeTextNoSuggestions(mSegmentoEditText);

        mFormaIngresoEditText = (EditText) view.findViewById(R.id.forma_ingreso_input);
        setTypeTextNoSuggestions(mFormaIngresoEditText);

        mCategoriaEditText = (EditText) view.findViewById(R.id.categoria_input);
        setTypeTextNoSuggestions(mCategoriaEditText);

        mFirstNameEditText = (EditText) view.findViewById(R.id.firstname_input);
        setTypeTextNoSuggestions(mFirstNameEditText);

        mLastNameEditText = (EditText) view.findViewById(R.id.lastname_input);
        setTypeTextNoSuggestions(mLastNameEditText);

        mSexEditText = (EditText) view.findViewById(R.id.sex_input);
        setTypeTextNoSuggestions(mSexEditText);

        mDocTypeEditText = (EditText) view.findViewById(R.id.doc_type_input);
        setTypeTextNoSuggestions(mDocTypeEditText);

        mDocNumberEditText = (EditText) view.findViewById(R.id.doc_number_input);
        mBirthdayEditText = (EditText) view.findViewById(R.id.birthday_input);
        mCuitEditText = (EditText) view.findViewById(R.id.cuit_input);

        mCivilSatatusEditText = (EditText) view.findViewById(R.id.civil_status_input);
        setTypeTextNoSuggestions(mCivilSatatusEditText);

        mNationalityEditText = (EditText) view.findViewById(R.id.nationality_input);
        setTypeTextNoSuggestions(mNationalityEditText);

        mCondicionIvaEditText = (EditText) view.findViewById(R.id.condicion_iva_input);
        setTypeTextNoSuggestions(mCondicionIvaEditText);

        mCoberturaEditText = (EditText) view.findViewById(R.id.cobertura_input);
        setTypeTextNoSuggestions(mCoberturaEditText);

        mScrollView = (ScrollView) view.findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        mEntityEditText = (AutoCompleteTextView) view.findViewById(R.id.entity_input);
        setTypeTextNoSuggestions(mEntityEditText);
        mEntityEditText.setThreshold(1);

        mDateroNumberEditText = (AutoCompleteTextView) view.findViewById(R.id.datero_number_input);
        setTypeTextNoSuggestions(mDateroNumberEditText);
        mDateroNumberEditText.setThreshold(1);

        mAffinityGroup = (EditText) view.findViewById(R.id.group_afinity_input);
        setTypeTextNoSuggestions(mAffinityGroup);

        mAffinityMessage = (TextView) view.findViewById(R.id.affinity_message);

        mAffinityContainer = (LinearLayout) view.findViewById(R.id.affinity_constancy_container);
        mAffinityAddButton = (Button) view.findViewById(R.id.affinity_add_button);

        mAgreementInput = (EditText) view.findViewById(R.id.agreement_input);
        setTypeTextNoSuggestions(mAgreementInput);

        mAgreementMessage = (TextView) view.findViewById(R.id.agreement_message);

        mAgreementContainer = (LinearLayout) view.findViewById(R.id.agreement_certificate_container);
        mAgreementAddButton = (Button) view.findViewById((R.id.agreement_add_button));


        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);
        checkEditableCardMode();

        this.view = view;

        fillArraysData();
        initializeForm();
        setupListeners();
    }

    public void setLoadFromQR(boolean value){
        
        loadFromQR = value;
    }

    public TitularData getTitularData() {

        if (mTitularData == null) {
            mTitularData = new TitularData();
        }

        // UPDATE ORIGINAL DATA
        mTitularData.firstname = mFirstNameEditText.getText().toString().trim();
        mTitularData.lastname = mLastNameEditText.getText().toString().trim();

        if (!mDocNumberEditText.getText().toString().trim().isEmpty()) {
            mTitularData.dni = Long.parseLong(mDocNumberEditText.getText().toString().trim());
        } else {
            mTitularData.dni = -1;
        }

        mTitularData.cuil = mCuitEditText.getText().toString().trim();

        try {
            mTitularData.birthday = ParserUtils.parseDate(mBirthdayEditText.getText().toString(), "yyyy-MM-dd");
        } catch (Exception e) {
        }

        if (mSelectedSegmento != -1) {
            mTitularData.segmento = mSegmentos.get(mSelectedSegmento);
        } else {
            mTitularData.segmento = null;
        }

        if (mSelectedFormaIngreso != -1) {
            mTitularData.formaIngreso = mFormasIngreso.get(mSelectedFormaIngreso);
        } else {
            mTitularData.formaIngreso = null;
        }

        if (mSelectedCategory != -1) {
            mTitularData.categoria = mCategories.get(mSelectedCategory);
        } else {
            mTitularData.categoria = null;
        }

        if (mSelectedCobertura != -1) {
            mTitularData.coberturaProveniente = mCoberturas.get(mSelectedCobertura);
        } else {
            mTitularData.coberturaProveniente = null;
        }

        if (mSelectedCondicionIva != -1) {
            mTitularData.condicionIva = mCondicionesIva.get(mSelectedCondicionIva);
        } else {
            mTitularData.condicionIva = null;
        }

        if (mSelectedSexo != -1) {
            mTitularData.sex = mSexos.get(mSelectedSexo);
        } else {
            mTitularData.sex = null;
        }

        if (mSelectedCivilStatus != -1) {
            mTitularData.civilStatus = mCivilStatus.get(mSelectedCivilStatus);
        } else {
            mTitularData.civilStatus = null;
        }

        if (mSelectedDocType != -1) {
            mTitularData.docType = mDocTypes.get(mSelectedDocType);
        } else {
            mTitularData.docType = null;
        }

        if (mSelectedNationality != -1) {
            mTitularData.nationality = mNationalities.get(mSelectedNationality);
        } else {
            mTitularData.nationality = null;
        }


        mTitularData.entity = mEntitySelected;

        mTitularData.dateroNumber = mDateroNumberSelected;

        return mTitularData;
    }

    public boolean isValidSection() {
        return validateForm();
    }


    // --- helper methods ---------------------------------------------------- //

    private void checkEditableCardMode() {
        mEntityEditText.setFocusable(editableCard);
        mFirstNameEditText.setFocusable(editableCard);
        mLastNameEditText.setFocusable(editableCard);
        mDocNumberEditText.setFocusable(editableCard);
        mCuitEditText.setFocusable(editableCard);
        mDateroNumberEditText.setFocusable(editableCard);

        if (loadFromQR) {
            mFirstNameEditText.setEnabled(false);
            mLastNameEditText.setEnabled(false);
            mDocNumberEditText.setEnabled(false);
            mBirthdayEditText.setEnabled(false);
        }
    }

    private void fillArraysData() {

        mSegmentos = QuoteOptionsController.getInstance().getSegmentos();
        mCategories = QuoteOptionsController.getInstance().getCategorias();
        mFormasIngreso = QuoteOptionsController.getInstance().getFormasIngreso();
        mCondicionesIva = QuoteOptionsController.getInstance().getCondicionIva();
        mCoberturas = QuoteOptionsController.getInstance().getCoberturas();

        mSexos = new ArrayList<>();
        QuoteOption sexoSelection = new QuoteOption("-1", getResources().getString(R.string.field_sex));
        mSexos.add(sexoSelection);
        mSexos.addAll(QuoteOptionsController.getInstance().getSexos());

        mDocTypes = new ArrayList<>();
        QuoteOption docTypeSelection = new QuoteOption("-1", getResources().getString(R.string.field_docType));
        mDocTypes.add(docTypeSelection);
        mDocTypes.addAll(QuoteOptionsController.getInstance().getDocTypes());

        mCivilStatus = new ArrayList<>();
        QuoteOption civilStatusSelection = new QuoteOption("-1", getResources().getString(R.string.field_civil_status));
        mCivilStatus.add(civilStatusSelection);
        mCivilStatus.addAll(QuoteOptionsController.getInstance().getCivilStatus());

        mNationalities = new ArrayList<>();
        QuoteOption nationSelection = new QuoteOption("-1", getResources().getString(R.string.field_nationality));
        mNationalities.add(nationSelection);
        mNationalities.addAll(QuoteOptionsController.getInstance().getNationalities());
    }


    private void initializeForm() {
        if (mTitularData == null)
            return;

        if (mTitularData.firstname != null && !mTitularData.firstname.isEmpty())
            mFirstNameEditText.setText(mTitularData.firstname);

        if (mTitularData.lastname != null && !mTitularData.lastname.isEmpty())
            mLastNameEditText.setText(mTitularData.lastname);

        if (mTitularData.dni != -1L)
            mDocNumberEditText.setText("" + mTitularData.dni);

        if (mTitularData.birthday != null) {
            mBirthdayEditText.setText(mTitularData.getBirthday());

            // Not in use Titular has always correct age, is deduced form birthday
            if (mTitularData.age > 0) {
                checkShowAgeAlert(mTitularData.birthday);
            }
        }

        if (mTitularData.cuil != null && !mTitularData.cuil.isEmpty())
            mCuitEditText.setText("" + mTitularData.cuil);

        if (mTitularData.segmento != null) {
            mSelectedSegmento = mSegmentos.indexOf(mTitularData.segmento);
            if (mSelectedSegmento != -1)
                mSegmentoEditText.setText(mTitularData.segmento.title);
        }

        if (mTitularData.formaIngreso != null) {
            mSelectedFormaIngreso = mFormasIngreso.indexOf(mTitularData.formaIngreso);
            if (mSelectedFormaIngreso != -1)
                mFormaIngresoEditText.setText(mTitularData.formaIngreso.title);
        }

        if (mTitularData.categoria != null) {
            mSelectedCategory = mCategories.indexOf(mTitularData.categoria);
            if (mSelectedCategory != -1)
                mCategoriaEditText.setText(mTitularData.categoria.title);
        }

        if (mTitularData.coberturaProveniente != null) {
            mSelectedCobertura = mCoberturas.indexOf(mTitularData.coberturaProveniente);
            if (mSelectedCobertura != -1)
                mCoberturaEditText.setText(mTitularData.coberturaProveniente.title);
        }

        if (mTitularData.condicionIva != null) {
            mSelectedCondicionIva = mCondicionesIva.indexOf(mTitularData.condicionIva);

            if (mSelectedCondicionIva != -1)
                mCondicionIvaEditText.setText(mTitularData.condicionIva.title);
        }

        if (mTitularData.sex != null) {
            mSelectedSexo = mSexos.indexOf(mTitularData.sex);

            if (mSelectedSexo != -1)
                mSexEditText.setText(mTitularData.sex.title);
        }

        if (mTitularData.civilStatus != null) {
            mSelectedCivilStatus = mCivilStatus.indexOf(mTitularData.civilStatus);

            if (mSelectedCivilStatus != -1)
                mCivilSatatusEditText.setText(mTitularData.civilStatus.title);
        }

        if (mTitularData.docType != null) {
            mSelectedDocType = mDocTypes.indexOf(mTitularData.docType);

            if (mSelectedDocType != -1)
                mDocTypeEditText.setText(mTitularData.docType.title);
        }


        if (mTitularData.getFormaIngreso().equals(ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAffinityGroup.setVisibility(View.VISIBLE);
            if (mTitularData.nombreAfinidad != null)
                mAffinityGroup.setText(mTitularData.nombreAfinidad.title + " - " + mTitularData.nombreAfinidad.id);

            mAffinityContainer.setVisibility(View.VISIBLE);
            if (mTitularData.documentoAfinidadId == -1L) {
                mAffinityAttachFile = null;
                initAffinityList();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fillAffinityDocumentation();
                    }
                }).start();
            }
            if (mTitularData.nombreAfinidad.id != null && mTitularData.nombreAfinidad.id != "")
                getAffinityMessage(Long.valueOf(mTitularData.nombreAfinidad.id));
        }

        if (mTitularData.getFormaIngreso().equals(ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAgreementInput.setVisibility(View.VISIBLE);
            if (mTitularData.nombreEmpresa != null)
                mAgreementInput.setText(mTitularData.nombreEmpresa.title + " - " + mTitularData.nombreEmpresa.id);

            mAgreementContainer.setVisibility(View.VISIBLE);
            if (mTitularData.documentoAgreementId == -1L) {
                mAgreementAttachFile = null;
                initAgreementList();
            } else {
                new Thread((new Runnable() {
                    @Override
                    public void run() {
                        fillAgreementDocumentation();
                    }
                })).start();
            }
            if (mTitularData.nombreEmpresa.id != null && mTitularData.nombreEmpresa.id != "")
                getAgreementMessage(Long.valueOf(mTitularData.nombreEmpresa.id));
        }


        if (mTitularData.nationality != null) {
            mSelectedNationality = mNationalities.indexOf(mTitularData.nationality);

            if (mSelectedNationality != -1) {
                mNationalityEditText.setText(mTitularData.nationality.title);
            } else {
                // preload Argentino
                QuoteOption nationalityDefault = new QuoteOption(ConstantsUtil.ARG_NATIONALITY_ID, QuoteOptionsController.getInstance().getNationalityName(ConstantsUtil.ARG_NATIONALITY_ID));
                mNationalityEditText.setText(nationalityDefault.title);
                mSelectedNationality = mNationalities.indexOf(nationalityDefault);
            }
        } else {
            // preload Argentino
            QuoteOption nationalityDefault = new QuoteOption(ConstantsUtil.ARG_NATIONALITY_ID, QuoteOptionsController.getInstance().getNationalityName(ConstantsUtil.ARG_NATIONALITY_ID));
            mNationalityEditText.setText(nationalityDefault.title);
            mSelectedNationality = mNationalities.indexOf(nationalityDefault);
        }

        if (mTitularData.entity != null) {

            mEntitySelected = mTitularData.entity;
            mEntityEditText.setText(mTitularData.entity.id);
            //mEntityEditText.setFocusable(false);
            updateEntity(new Response.Listener<QuoteOption>() {
                @Override
                public void onResponse(QuoteOption response) {

                    Log.e(TAG, "mTitularDataFragment entity onResponse....");
                    if (response != null) {
                        mEntitySelected = response;
                        mEntityEditText.setText(mEntitySelected.title);
                        //mEntityEditText.setFocusable(true);
                    }
                }
            });
        }

        if (mTitularData.dateroNumber != null) {
            mDateroNumberSelected = mTitularData.dateroNumber;
            mDateroNumberEditText.setText(mTitularData.dateroNumber.id);
            //mDateroNumberEditText.setText(mTitularData.dateroNumber.extra + " - " + mTitularData.dateroNumber.title);
            updateDatero(new Response.Listener<QuoteOption>() {
                @Override
                public void onResponse(QuoteOption response) {

                    Log.e(TAG, "mTitularDataFragment entity onResponse....");
                    if (response != null) {
                        mDateroNumberSelected = response;
                        mDateroNumberEditText.setText(mDateroNumberSelected.extra + " - " + mDateroNumberSelected.title);
                        //mEntityEditText.setFocusable(true);
                    }
                }
            });
        }
    }

    private void setupListeners() {

        setupImageProvider();

        if (editableCard) {
            View sexButton = mMainContainer.findViewById(R.id.sex_button);
            sexButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showSexAlert();
                }
            });
            sexButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedSexo = -1;
                    mSexEditText.setText("");
                    return true;
                }
            });

            View civilStatusButton = mMainContainer.findViewById(R.id.civil_status_button);
            civilStatusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCivilStatusAlert();
                }
            });
            civilStatusButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedCivilStatus = -1;
                    mCivilSatatusEditText.setText("");
                    return true;
                }
            });


            View doctTypeButton = mMainContainer.findViewById(R.id.doc_type_button);
            doctTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    if (!loadFromQR)
                        showDocTypesAlert();
                }
            });
            doctTypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedSexo = -1;
                    mSexEditText.setText("");
                    return true;
                }
            });


            View nationalityButton = mMainContainer.findViewById(R.id.nationality_button);
            nationalityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showNationalitiesAlert();
                }
            });
            nationalityButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedNationality = -1;
                    mNationalityEditText.setText("");
                    return true;
                }
            });


            mEntityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mEntitySelected = null;
                    searchEntity(s.toString());
                }
            });

            mDateroNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mDateroNumberSelected = null;
                    searchDatero(s.toString());
                }
            });

            mEntityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        updateEntity(null);
                    }
                }
            });

            mDateroNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        updateDatero(null);
                    }
                }
            });

            mCuitEditText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (start == 1 && mCuitEditText.getText().length() == 2 && mSelectedDocType != -1) {

                        QuoteOption qo = mDocTypes.get(mSelectedDocType);
                        if (ConstantsUtil.DOC_TYPE_IDENTITY.equalsIgnoreCase(qo.id) && mDocNumberEditText.getText().length() > 0) {
                            mCuitEditText.setText(mCuitEditText.getText().toString() + mDocNumberEditText.getText().toString());
                            mCuitEditText.setSelection(mCuitEditText.getText().length());
                        }
                    }
                }
            });

            mAffinityAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachFileType = AFFINITY_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + AFFINITY_FILE_PREFIX, AFFINITY_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mAgreementAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachFileType = AGREEMENT_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + AGREEMENT_FILE_PREFIX, AGREEMENT_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });
        }
    }

    private void initAffinityList() {

        if (mAffinityAttachFile != null) {
            affinityAttachFiles.add(mAffinityAttachFile);
            mAffinityFileAdapter = new AttachFilesAdapter(affinityAttachFiles, true);
            mAffinityAddButton.setEnabled(false);
        } else {
            mAffinityFileAdapter = new AttachFilesAdapter();
            mAffinityAddButton.setEnabled(true);
        }

        mAffinityFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile affinityAttachFile = affinityAttachFiles.get(position);
                Log.e(TAG, "affinity path:" + affinityAttachFile.filePath);
                loadFile(affinityAttachFile);
            }
        });

        mAffinityFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "affinity onItemDeleteClick()!!!!!:" + position);
                attachFileType = AFFINITY_FILE_PREFIX;
                AttachFile affinityAttachFile = affinityAttachFiles.get(position);
                if (affinityAttachFile.id != -1) {
                    removeFile(affinityAttachFile, position);
                }
            }
        });

        mAffinityRecyclerView = (RecyclerView) view.findViewById(R.id.constancia_afinidad_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager((mAffinityRecyclerView.getContext()));
        mAffinityRecyclerView.setLayoutManager(attachLayoutManager);
        mAffinityRecyclerView.setAdapter(mAffinityFileAdapter);
        mAffinityRecyclerView.setHasFixedSize(true);

    }


    private void initAgreementList() {

        if (mAgreementAttachFile != null) {
            magreementAttachFiles.add(mAgreementAttachFile);
            mAgreementFileAdapter = new AttachFilesAdapter(magreementAttachFiles, true);
            mAgreementAddButton.setEnabled(false);
        } else {
            mAgreementFileAdapter = new AttachFilesAdapter();
            mAgreementAddButton.setEnabled(true);
        }

        mAgreementFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile agreementAttachFile = magreementAttachFiles.get(position);
                Log.e(TAG, "agreement path:" + agreementAttachFile.filePath);
                loadFile(agreementAttachFile);
            }
        });

        mAgreementFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "dniFrontFile onItemDeleteClick()!!!!!:" + position);
                attachFileType = AGREEMENT_FILE_PREFIX;
                AttachFile agreementAttachFile = magreementAttachFiles.get(position);
                if (agreementAttachFile.id != -1) {
                    removeFile(agreementAttachFile, position);
                }
            }
        });

        mAgreementRecyclerView = (RecyclerView) view.findViewById(R.id.agreement_certificate_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager((mAgreementRecyclerView.getContext()));
        mAgreementRecyclerView.setLayoutManager(attachLayoutManager);
        mAgreementRecyclerView.setAdapter(mAgreementFileAdapter);
        mAgreementRecyclerView.setHasFixedSize(true);

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
        //return true;
        boolean isValid = true;
        isValid = isValid & validateField(mDocTypeEditText, R.string.affiliation_doc_type_error, R.id.doc_type_wrapper);
        return isValid;
    }


    private void showSexAlert() {
        ArrayList<String> sexStr = new ArrayList<String>();
        for (QuoteOption q : mSexos) {
            sexStr.add(q.optionName());
        }

        mSexoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), sexStr, mSelectedSexo);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mSexoAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedSexo = i;
                if (i == 0) {
                    mSelectedSexo = -1;
                    mSexEditText.setText("");
                } else {
                    mSexEditText.setText(mSexos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mSexoAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDocTypesAlert() {
        ArrayList<String> docTypeStr = new ArrayList<String>();
        for (QuoteOption q : mDocTypes) {
            docTypeStr.add(q.optionName());
        }

        mDocTypeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), docTypeStr, mSelectedDocType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mDocTypeAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedDocType = i;
                if (i == 0) {
                    mSelectedDocType = -1;
                    mDocTypeEditText.setText("");
                } else {
                    mDocTypeEditText.setText(mDocTypes.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mDocTypeAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCivilStatusAlert() {
        ArrayList<String> civilStatusStr = new ArrayList<String>();
        for (QuoteOption q : mCivilStatus) {
            civilStatusStr.add(q.optionName());
        }

        mCivilStatusAdapter = new SpinnerDropDownAdapter(getActivity(), civilStatusStr, mSelectedCivilStatus);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCivilStatusAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedCivilStatus = i;
                if (i == 0) {
                    mSelectedCivilStatus = -1;
                    mCivilSatatusEditText.setText("");
                } else {
                    mCivilSatatusEditText.setText(mCivilStatus.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCivilStatusAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showNationalitiesAlert() {
        ArrayList<String> nationsStr = new ArrayList<String>();

        for (QuoteOption q : mNationalities) {
            nationsStr.add(q.optionName());
        }

        mNationalityAlertAdapter = new SpinnerDropDownAdapter(getActivity(), nationsStr, mSelectedNationality);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mNationalityAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedNationality = i;
                if (i == 0) {
                    mSelectedNationality = -1;
                    mNationalityEditText.setText("");
                } else {
                    mNationalityEditText.setText(mNationalities.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mNationalityAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void searchEntity(String query) {
        if (searching)
            return;

        Log.e(TAG, "searchEntity....");
        searching = true;
        HRequest request = RestApiServices.createGetSearchEntityRequest(query, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                ArrayList<String> options = new ArrayList<String>();
                for (QuoteOption q : response) {
                    options.add(q.title);
                }
                Log.e("SEARCHHHH", "FINISH: " + response.size());

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                AffiliationTitularDataFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mEntityEditText.setAdapter(adapter);
                        mEntityEditText.showDropDown();
                    }
                });

                searching = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                searching = false;
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateEntity(final Response.Listener<QuoteOption> listener) {

        final String data = mEntityEditText.getText().toString().trim();
        if (data != null && !data.isEmpty()) {

            HRequest request = RestApiServices.createGetSearchEntityRequest(data.split("-")[0].trim(), new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {
                    if (response != null && !response.isEmpty()) {
                        mEntitySelected = filterDataById(data.split("-")[0].trim(), response);
                    } else {
                        mEntitySelected = new QuoteOption();
                        mEntitySelected.title = data;
                        mEntitySelected.id = null;
                    }
                    if (listener != null)
                        listener.onResponse(mEntitySelected);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (listener != null)
                        listener.onResponse(null);
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);

        } else {
            if (listener != null)
                listener.onResponse(null);
        }
    }

    private void searchDatero(String query) {
        if (searching)
            return;

        Log.e(TAG, "searchEntity....");
        searching = true;
        HRequest request = RestApiServices.createGetSearchDateroRequest(query, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                ArrayList<String> options = new ArrayList<String>();
                for (QuoteOption q : response) {
                    options.add(q.extra + " - " + q.title);
                }
                Log.e("SEARCHHHH", "FINISH: " + response);

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                AffiliationTitularDataFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mDateroNumberEditText.setAdapter(adapter);
                        mDateroNumberEditText.showDropDown();
                    }
                });

                searching = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                searching = false;
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    public void updateDatero(final Response.Listener<QuoteOption> listener) {

        final String data = mDateroNumberEditText.getText().toString().trim();
        if (data != null && !data.isEmpty()) {

            HRequest request = RestApiServices.createGetSearchDateroRequest(data.split("-")[0].trim(), new Response.Listener<ArrayList<QuoteOption>>() {
                @Override
                public void onResponse(ArrayList<QuoteOption> response) {
                    if (response != null && !response.isEmpty()) {
                        mDateroNumberSelected = filterDataByNumberDatero(data.split("-")[0].trim(), response);
                    } else {
                        mDateroNumberSelected = new QuoteOption();
                        mDateroNumberSelected.title = data;
                        mDateroNumberSelected.id = null;
                    }
                    if (listener != null)
                        listener.onResponse(mDateroNumberSelected);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (listener != null)
                        listener.onResponse(null);
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);

        } else {
            if (listener != null)
                listener.onResponse(null);
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

    private QuoteOption filterDataByNumberDatero(String num, ArrayList<QuoteOption> responseList) {
        QuoteOption result = null;
        for (QuoteOption quoteOption : responseList) {
            if (quoteOption.extra.equals(num)) {
                result = quoteOption;
                break;
            }
        }
        return result;
    }

    // NOT IN USE FOR TITULAR
    private void checkShowAgeAlert(Date inputDate) {
        int age1 = mTitularData.age;
        Log.e(TAG, "Age1: " + age1);

        int age2 = 0;
        if (inputDate != null) {
            age2 = DateUtils.getAge(inputDate);
        }
        Log.e(TAG, "Age2: " + age2);

        if (age1 != age2) {
            ((BaseActivity) getActivity()).showMessageWithTitle(getActivity().getResources().getString(R.string.warning_alert), getActivity().getResources().getString(R.string.affiliation_titular_error_age));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // CHECK SCAN CODE
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            String scanCode = result.getContents();
            Log.e(TAG, "Result: " + scanCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void fillAffinityDocumentation() {
        CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + AFFINITY_FILE_PREFIX, mTitularData.documentoAfinidadId,
                new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile attachFile) {
                        Log.e(TAG, "Success to get affinity documentation");
                        mAffinityAttachFile = attachFile;
                        mTitularData.documentoAfinidadId = attachFile.id;
                        initAffinityList();
                    }
                }
                , new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error to get affinity documentation");
                        mAffinityAttachFile = null;
                        initAffinityList();
                    }
                });
    }

    public void fillAgreementDocumentation() {
        CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + AGREEMENT_FILE_PREFIX, mTitularData.documentoAgreementId,
                new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile attachFile) {
                        Log.e(TAG, "Success to get agreement documentation");
                        mAgreementAttachFile = attachFile;
                        mTitularData.documentoAgreementId = attachFile.id;
                        initAgreementList();
                    }
                }
                , new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error to get affinity documentation");
                        mAgreementAttachFile = null;
                        initAgreementList();
                    }
                });
    }

    @Override
    public void onRemovedFile(int position) {
        try {
            if (attachFileType.equals(AFFINITY_FILE_PREFIX)) {
                mAffinityFileAdapter.removeAllItems();
                mTitularData.documentoAfinidadId = -1L;
                mAffinityAttachFile = null;
                mAffinityAddButton.setEnabled(true);

            } else if (attachFileType.equals(AGREEMENT_FILE_PREFIX)) {
                mAgreementFileAdapter.removeAllItems();
                mTitularData.documentoAgreementId = -1L;
                mAgreementAttachFile = null;
                mAgreementAddButton.setEnabled(true);
            }

        } catch (Throwable e) {
        }
    }

    @Override
    public void updateFileList(final AttachFile attachFile) {
        if (attachFileType.equals(AFFINITY_FILE_PREFIX)) {
            mAffinityAttachFile = attachFile;
            Log.d(TAG, attachFile.filePath);

            // la ficha no existe
            if (mFichaId == -1L) {
                mTitularData.documentoAfinidadId = attachFile.id;
                mAffinityFileAdapter.addItem(attachFile);
                mAffinityAddButton.setEnabled(false);
            } else {
                addAffinityDocument(mFichaId, true);
            }
        } else if (attachFileType.equals(AGREEMENT_FILE_PREFIX)) {
            mAgreementAttachFile = attachFile;
            Log.d(TAG, attachFile.filePath);

            // la ficha no existe
            if (mFichaId == -1L) {
                mTitularData.documentoAgreementId = attachFile.id;
                mAgreementFileAdapter.addItem(attachFile);
                mAgreementAddButton.setEnabled(false);
            } else {
                addAgreementDocument(mFichaId, true);
            }
        }
    }

    public void addAffinityDocument(long fichaId, final boolean loadFileAdapter) {
        if (mAffinityAttachFile != null && mAffinityAttachFile.id != -1L) {
            HRequest request = RestApiServices.createAffinityDocument(fichaId, mAffinityAttachFile.id, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void response) {
                    Log.d(TAG, "Documento afinidad enviado correctamente");
                    if (loadFileAdapter) {
                        mTitularData.documentoAfinidadId = mAffinityAttachFile.id;
                        mAffinityFileAdapter.addItem(mAffinityAttachFile);
                        mAffinityAddButton.setEnabled(false);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error al enviar documento afinidad");
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
        }
    }

    public void addAgreementDocument(long fichaId, final boolean loadFileAdapter) {
        if (mAgreementAttachFile != null && mAgreementAttachFile.id != -1L) {
            HRequest request = RestApiServices.createAgreementDocument(fichaId, mAgreementAttachFile.id, new Response.Listener<Void>() {

                @Override
                public void onResponse(Void response) {
                    Log.d(TAG, "Documento empresa enviado correctamente");
                    if (loadFileAdapter) {
                        mTitularData.documentoAgreementId = mAgreementAttachFile.id;
                        mAgreementFileAdapter.addItem(mAgreementAttachFile);
                        mAgreementAddButton.setEnabled(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error al enviar documento empresa");
                }
            });
            AppController.getInstance().getRestEngine().addToRequestQueue(request);
        }

    }

    private void getAffinityMessage(long affinityId) {
        HRequest request = RestApiServices.getAffinityMessage(affinityId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, response);
                mAffinityMessage.setText(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Affinity Message Error");
                mAffinityMessage.setText("");
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    private void getAgreementMessage(long agreementId) {
        HRequest request = RestApiServices.getAgreementMessage(agreementId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mAgreementMessage.setText(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Agreement Message Error");
                mAgreementMessage.setText("");
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }
}
