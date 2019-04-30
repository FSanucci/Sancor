package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2Monotributo;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class AffiliationAdditionalData2MonotributoFragment extends BaseFragment implements IAffiliationAdditionalData2Fragment {

    private static final String TAG = "AF_MONO2_FRG";

    protected static final String ARG_ADD_DATA_2 = "additionalData2";
    protected static final String ARG_TITULAR_DNI = "titularDNI";
    protected static final String ARG_CARD_ID = "cardId";
    protected static final String ARG_TITULAR_CARD_ID = "titularCardId";
    protected static final String ARG_CONYUGE_CARD_ID = "conyugeCardId";
    protected static final String ARG_CONYUGE_DATA = "conyugeData";

    private static final String FORM_184_FILE_PREFIX = "form184";
    private static final String MONTH_FILE_PREFIX = "3meses";
    private static final String OS_CODE_FILE_PREFIX = "oscode";

    private static final String OPTION_CHANGE_FILE_PREFIX = "opcion";
    private static final String FORM_FILE_PREFIX = "cartilla";
    private static final String CERT_CHANGE_FILE_PREFIX = "certChange";

    private ScrollView mScrollView;
    private RadioButton mEmpadronadoRadioButton;
    private RadioButton mOpcionRadioButton;

    // CONDICION MONOTRIBUTO
    private LinearLayout mOptionBox;
    private EditText mOSEditText;
    private EditText mOSStateEditText;
    private RelativeLayout mOSDateBox;
    private EditText mOSDateInputEditText;
    private EditText mSSSFormEditText;

    private SpinnerDropDownAdapter mOSAlertAdapter;
    private int mSelectedOS = -1;
    private ArrayList<QuoteOption> mObrasSociales;

    private SpinnerDropDownAdapter mOSStateAlertAdapter;
    private int mSelectedOSState = -1;
    private ArrayList<QuoteOption> mOSStates;

    // FORM 184 FILES
    private RecyclerView m184RecyclerView;
    private AttachFilesAdapter m184FileAdapter;
    private Button add184Button;

    // 3 MONTH FILES
    private RecyclerView mThreeMonthRecyclerView;
    private AttachFilesAdapter mThreeMonthFileAdapter;
    private Button addThreeMonthButton;

    // OS CODE  FILES
    private RecyclerView mOSCodeRecyclerView;
    private AttachFilesAdapter mOSCodeFileAdapter;
    private Button addOSCodeButton;

    // SINDICAL OPTION CHANGE FILES
    private RelativeLayout mOptionChangeBox;
    private RecyclerView mOptionChangeRecyclerView;
    private AttachFilesAdapter mOptionChangeFileAdapter;
    private Button addOptionChangeButton;

    // SINDICAL FORM CHANGE FILES
    private RelativeLayout mFormBox;
    private RecyclerView mFormRecyclerView;
    private AttachFilesAdapter mFormFileAdapter;
    private Button addFormButton;

    // SINDICAL CERT CHANGE FILES
    private RelativeLayout mCertChangeBox;
    private RecyclerView mCertChangeRecyclerView;
    private AttachFilesAdapter mCertChangeFileAdapter;
    private Button addCertChangeButton;


    public List<AttachFile> tmpForm184Files = new ArrayList<AttachFile>();
    public List<AttachFile> tmpThreeMonthFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpOSCodeFiles = new ArrayList<AttachFile>();

    public List<AttachFile> tmpOptionChangeFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpFormFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpCertChangeFiles = new ArrayList<AttachFile>();

    private long cardId;
    private long titularDNI;
    private long titularCardId;
    private long conyugeCardId;

    private ConyugeData conyugeData;

    private View view;
    private SimpleDateFormat mDateFormat;
    private AdditionalData2Monotributo additionalData2;


    private Button mOsConyugeButton;

    private boolean editableCard = true;
    private String osCodeLink;

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "Validity Date: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                if (date.after(today)) {
                    mOSDateInputEditText.setText(mDateFormat.format(today));
                } else {
                    mOSDateInputEditText.setText(mDateFormat.format(date));
                    checkShowAlertMessage(date);
                }
            } catch (Exception e) {
                mOSDateInputEditText.setText(mDateFormat.format(today));
            }
            mOSDateInputEditText.requestFocus();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionalData2 = (AdditionalData2Monotributo) getArguments().getSerializable(ARG_ADD_DATA_2);
            titularDNI = getArguments().getLong(ARG_TITULAR_DNI);
            cardId = getArguments().getLong(ARG_CARD_ID);
            titularCardId = getArguments().getLong(ARG_TITULAR_CARD_ID);
            conyugeCardId = getArguments().getLong(ARG_CONYUGE_CARD_ID);
            conyugeData = (ConyugeData) getArguments().getSerializable(ARG_CONYUGE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_additional_data_mono2, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");
        attachFilesSubDir = titularDNI + "/affiliation/mono";
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mImageProvider = new ImageProvider(getActivity());

        mOSEditText = (EditText) mMainContainer.findViewById(R.id.os_input);
        setTypeTextNoSuggestions(mOSEditText);

        mOSDateInputEditText = (EditText) mMainContainer.findViewById(R.id.os_date_input);

        mEmpadronadoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.os_code_empadronado_button);
        mOpcionRadioButton = (RadioButton) mMainContainer.findViewById(R.id.os_code_opcion_button);
        mOptionBox = (LinearLayout) mMainContainer.findViewById(R.id.os_opcion_box);

        mOSStateEditText = (EditText) mMainContainer.findViewById(R.id.os_state_input);
        setTypeTextNoSuggestions(mOSStateEditText);

        mOSDateBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_date_box);
        mSSSFormEditText = (EditText) mMainContainer.findViewById(R.id.os_form_input);

        // OS SINDICAL
        mOptionChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_change_option_box);
        mFormBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_card_box);
        mCertChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_cert_box);

        mOsConyugeButton = (Button) mMainContainer.findViewById(R.id.os_conyuge_button);
        mScrollView = (ScrollView) mMainContainer.findViewById(R.id.scroll);

        this.view = view;

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        fillArraysData();
        initOSBaseForm();
        fillAllPhysicalFiles();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_CONYUGE_OS) {
            Log.e(TAG, "onActivityResult for conyuge DATA.....");
            conyugeData = (ConyugeData) data.getSerializableExtra(ConstantsUtil.RESULT_CONYUGE_DATA);

            boolean hasToUpdateConyugeMonoFiles = data.getBooleanExtra(ConstantsUtil.UPDATE_CONYUGE_MONO_FILES, false);
            if (conyugeData != null && hasToUpdateConyugeMonoFiles) {
                Log.e(TAG, "onActivityResult updating conyufe files... ");
                ((AffiliationActivity) getActivity()).updateConyugeMonotributoFiles(conyugeData.form184Files, conyugeData.threeMonthFiles);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void updateFileList(AttachFile attachFile) {

        if (attachFileType.equals(FORM_184_FILE_PREFIX)) {
            m184FileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(MONTH_FILE_PREFIX)) {
            mThreeMonthFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(OS_CODE_FILE_PREFIX)) {
            mOSCodeFileAdapter.addItem(attachFile);
        }

        // SINDICAL
        else if (attachFileType.equals(OPTION_CHANGE_FILE_PREFIX)) {
            mOptionChangeFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(FORM_FILE_PREFIX)) {
            mFormFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CERT_CHANGE_FILE_PREFIX)) {
            mCertChangeFileAdapter.addItem(attachFile);
        }

    }

    @Override
    public void onRemovedFile(int position) {
        try {

            if (attachFileType.equals(FORM_184_FILE_PREFIX)) {
                m184FileAdapter.removeItem(position);
            } else if (attachFileType.equals(MONTH_FILE_PREFIX)) {
                mThreeMonthFileAdapter.removeItem(position);
            } else if (attachFileType.equals(OS_CODE_FILE_PREFIX)) {
                mOSCodeFileAdapter.removeItem(position);
            }
            // SINDICAL
            else if (attachFileType.equals(OPTION_CHANGE_FILE_PREFIX)) {
                mOptionChangeFileAdapter.removeItem(position);
            } else if (attachFileType.equals(FORM_FILE_PREFIX)) {
                mFormFileAdapter.removeItem(position);
            } else if (attachFileType.equals(CERT_CHANGE_FILE_PREFIX)) {
                mCertChangeFileAdapter.removeItem(position);
            }

        } catch (Throwable e) {
        }
    }


    public IAdditionalData2 getAdditionalData2() {
        Log.e(TAG, "----- getAdditionalData2 -------");

        ObraSocial osMonotributo = additionalData2.obraSocial;
        osMonotributo.isMonotributo = true;
        osMonotributo.cardId = cardId;
        osMonotributo.personCardId = titularCardId;

        if (mSelectedOS != -1) {
            osMonotributo.osQuotation = mObrasSociales.get(mSelectedOS);
        }

        if (mEmpadronadoRadioButton.isChecked() || mOpcionRadioButton.isChecked()) {
            osMonotributo.empadronado = mEmpadronadoRadioButton.isChecked();
        } else {
            osMonotributo.empadronado = null;
        }

        if ( osMonotributo.empadronado != null &&  !osMonotributo.empadronado) {

            if (mSelectedOSState != -1) {
                osMonotributo.osState = mOSStates.get(mSelectedOSState);
                if (osMonotributo.osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION)) {
                    if (!mOSDateInputEditText.getText().toString().isEmpty()) {
                        try {
                            osMonotributo.inputOSDate = ParserUtils.parseDate(mOSDateInputEditText.getText().toString(), "yyyy-MM-dd");
                        } catch (Exception e) {
                        }
                    }
                } else {
                    osMonotributo.inputOSDate = null;
                }

            } else {
                osMonotributo.osState = null;
            }

            if (!mOSDateInputEditText.getText().toString().isEmpty()) {
                try {
                    osMonotributo.inputOSDate = ParserUtils.parseDate(mOSDateInputEditText.getText().toString(), "yyyy-MM-dd");
                } catch (Exception e) {
                }
            } else {
                osMonotributo.inputOSDate = null;
            }

            osMonotributo.inputOSDate = null;

            if (!mSSSFormEditText.getText().toString().isEmpty()) {
                osMonotributo.osSSSFormNumber = Integer.valueOf(mSSSFormEditText.getText().toString().trim());
            } else {
                osMonotributo.osSSSFormNumber = -1L;
            }

            if (mOptionChangeFileAdapter != null) {
                osMonotributo.optionChangeFiles = mOptionChangeFileAdapter.getAttachFiles();
            }
            if (mFormFileAdapter != null) {
                osMonotributo.formFiles = mFormFileAdapter.getAttachFiles();
            }
            if (mCertChangeFileAdapter != null) {
                osMonotributo.certChangeFiles = mCertChangeFileAdapter.getAttachFiles();
            }
        }
        else{

            osMonotributo.osState = null;
            osMonotributo.inputOSDate = null;
            osMonotributo.mesesImpagos = 0;
            osMonotributo.osSSSFormNumber = -1L;

            osMonotributo.osCodeFiles =new ArrayList<AttachFile>();
            osMonotributo.optionChangeFiles = new ArrayList<AttachFile>();
            osMonotributo.formFiles = new ArrayList<AttachFile>();
            osMonotributo.certChangeFiles = new ArrayList<AttachFile>();
        }

        // UPDATE FILES
        if (m184FileAdapter != null) {
            additionalData2.form184Files = m184FileAdapter.getAttachFiles();
        }
        if (mThreeMonthFileAdapter != null) {
            additionalData2.threeMonthFiles = mThreeMonthFileAdapter.getAttachFiles();
        }
        if (mOSCodeFileAdapter != null) {
            osMonotributo.osCodeFiles = mOSCodeFileAdapter.getAttachFiles();
        }

        return additionalData2;
    }

    @Override
    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private void fillArraysData(){

        //MONOTRIBUTO ALWAYS WORK WITH OS SINDICAL TYPE
        mObrasSociales = new ArrayList<>();
        QuoteOption osSelection = new QuoteOption("-1", getResources().getString(R.string.field_os));
        mObrasSociales.add(osSelection);
        //mObrasSociales.addAll(QuoteOptionsController.getInstance().getOSDesreguladas(ConstantsUtil.OS_TYPE_SINDICAL));
        mObrasSociales.addAll(QuoteOptionsController.getInstance().getOSCondMonotrinbuto(ConstantsUtil.OS_TYPE_SINDICAL));

        mOSStates = new ArrayList<>();
        QuoteOption osStateSelection = new QuoteOption("-1", getResources().getString(R.string.field_state));
        mOSStates.add(osStateSelection);
        mOSStates.addAll(QuoteOptionsController.getInstance().getOSStates(ConstantsUtil.OS_TYPE_SINDICAL));
    }

    private void fillAllPhysicalFiles() {
        ((AffiliationActivity) getActivity()).setActionButtons(false);

        // OS
        ObraSocial obraSocial = additionalData2.obraSocial;
        if (obraSocial == null) {
            obraSocial = new ObraSocial();
        }
        obraSocial.cardId = cardId;

        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll AddData1 Data PhysicalFiles ....");
                fillForm184Files();
            }
        }).start();
    }

    private void fillForm184Files() {

        if (!additionalData2.form184Files.isEmpty()) {

            Log.e(TAG, "fill 184 Form Files !!....");

            final AttachFile form184File = additionalData2.form184Files.remove(0);
            if (form184File.fileNameAndExtension == null || form184File.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_184_FILE_PREFIX, form184File.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting 184 Form File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpForm184Files.add(resultFile);
                            fillForm184Files();

                        } else {
                            Log.e(TAG, "Null 184 file ....");
                            tmpForm184Files.add(form184File);
                            fillForm184Files();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpForm184Files.add(form184File);
                        fillForm184Files();
                    }
                });
            } else {
                tmpForm184Files.add(form184File);
                fillForm184Files();
            }

        } else {
            additionalData2.form184Files.addAll(tmpForm184Files);
            tmpForm184Files = new ArrayList<AttachFile>();
            fillThreeMonthFiles();
        }
    }

    private void fillThreeMonthFiles() {

        if (!additionalData2.threeMonthFiles.isEmpty()) {

            Log.e(TAG, "fill conyugeThreeMonthFiles !!....");

            final AttachFile monthFile = additionalData2.threeMonthFiles.remove(0);
            if (monthFile.fileNameAndExtension == null || monthFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + MONTH_FILE_PREFIX, monthFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting sixMonth File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpThreeMonthFiles.add(resultFile);
                            fillThreeMonthFiles();

                        } else {
                            Log.e(TAG, "Null conyugeThreeMonth File ....");
                            tmpThreeMonthFiles.add(monthFile);
                            fillThreeMonthFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpThreeMonthFiles.add(monthFile);
                        fillThreeMonthFiles();
                    }
                });
            } else {
                tmpThreeMonthFiles.add(monthFile);
                fillThreeMonthFiles();
            }

        } else {
            additionalData2.threeMonthFiles.addAll(tmpThreeMonthFiles);
            tmpThreeMonthFiles = new ArrayList<AttachFile>();
            fillOSCOdeFiles();
        }
    }

    private void fillOSCOdeFiles() {

        if (!additionalData2.obraSocial.osCodeFiles.isEmpty()) {

            Log.e(TAG, "fillOSCOdeFiles !!....");

            final AttachFile osCodeFile = additionalData2.obraSocial.osCodeFiles.remove(0);
            if (osCodeFile.fileNameAndExtension == null || osCodeFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + OS_CODE_FILE_PREFIX, osCodeFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting OS CODE File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpOSCodeFiles.add(resultFile);
                            fillOSCOdeFiles();

                        } else {
                            Log.e(TAG, "Null OSCOde file ....");
                            tmpOSCodeFiles.add(osCodeFile);
                            fillOSCOdeFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpOSCodeFiles.add(osCodeFile);
                        fillOSCOdeFiles();
                    }
                });
            } else {
                tmpOSCodeFiles.add(osCodeFile);
                fillOSCOdeFiles();
            }

        } else {
            additionalData2.obraSocial.osCodeFiles.addAll(tmpOSCodeFiles);
            tmpOSCodeFiles = new ArrayList<AttachFile>();
            fillOptionChangeFiles();
        }
    }

    // --- SINDICAL FILES ------------------------------------------------ //

    private void fillOptionChangeFiles() {

        if (!additionalData2.obraSocial.optionChangeFiles.isEmpty()) {

            Log.e(TAG, "fillOptionChangeFiles !!....");

            final AttachFile optionFile = additionalData2.obraSocial.optionChangeFiles.remove(0);
            if (optionFile.fileNameAndExtension == null || optionFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + OPTION_CHANGE_FILE_PREFIX, optionFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Option Change File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpOptionChangeFiles.add(resultFile);
                            fillOptionChangeFiles();

                        } else {
                            Log.e(TAG, "Null Option Change File ....");
                            tmpOptionChangeFiles.add(optionFile);
                            fillOptionChangeFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpOptionChangeFiles.add(optionFile);
                        fillOptionChangeFiles();
                    }
                });
            } else {
                tmpOptionChangeFiles.add(optionFile);
                fillOptionChangeFiles();
            }

        } else {
            additionalData2.obraSocial.optionChangeFiles.addAll(tmpOptionChangeFiles);
            tmpOptionChangeFiles = new ArrayList<AttachFile>();
            fillFormFiles();
        }
    }

    private void fillFormFiles() {

        if (!additionalData2.obraSocial.formFiles.isEmpty()) {

            Log.e(TAG, "fill OS FormFiles !!....");

            final AttachFile formFile = additionalData2.obraSocial.formFiles.remove(0);
            if (formFile.fileNameAndExtension == null || formFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_FILE_PREFIX, formFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting OS Form File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpFormFiles.add(resultFile);
                            fillFormFiles();

                        } else {
                            Log.e(TAG, "Null OS Form file ....");
                            tmpFormFiles.add(formFile);
                            fillFormFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpFormFiles.add(formFile);
                        fillFormFiles();
                    }
                });
            } else {
                tmpFormFiles.add(formFile);
                fillFormFiles();
            }

        } else {
            additionalData2.obraSocial.formFiles.addAll(tmpFormFiles);
            tmpFormFiles = new ArrayList<AttachFile>();
            fillCertChangeFiles();
        }
    }

    private void fillCertChangeFiles() {

        if (!additionalData2.obraSocial.certChangeFiles.isEmpty()) {

            Log.e(TAG, "certChangeFiles !!....");

            final AttachFile certChangeFile = additionalData2.obraSocial.certChangeFiles.remove(0);
            if (certChangeFile.fileNameAndExtension == null || certChangeFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + CERT_CHANGE_FILE_PREFIX, certChangeFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting certChange File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCertChangeFiles.add(resultFile);
                            fillCertChangeFiles();

                        } else {
                            Log.e(TAG, "Null certChange file ....");
                            tmpCertChangeFiles.add(certChangeFile);
                            fillCertChangeFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpCertChangeFiles.add(certChangeFile);
                        fillCertChangeFiles();
                    }
                });
            } else {
                tmpFormFiles.add(certChangeFile);
                fillCertChangeFiles();
            }

        } else {
            additionalData2.obraSocial.certChangeFiles.addAll(tmpCertChangeFiles);
            tmpCertChangeFiles = new ArrayList<AttachFile>();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initialize();
                }
            });
        }
    }

    private void showSindicalFiles() {
        Log.e(TAG, "showSindicalFiles ...");
        mOptionChangeBox.setVisibility(View.VISIBLE);
        mFormBox.setVisibility(View.VISIBLE);
        mCertChangeBox.setVisibility(View.VISIBLE);
    }

    private void hideSindicalFiles() {
        mOptionChangeBox.setVisibility(View.GONE);
        mFormBox.setVisibility(View.GONE);
        mCertChangeBox.setVisibility(View.GONE);
    }


    private void checkShowDateBox(QuoteOption osState, boolean cleanData) {
        if (osState != null && (osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION))) {
            mOSDateBox.setVisibility(View.VISIBLE);
            if (cleanData) {
                mOSDateInputEditText.setText("");
            }
        } else {
            mOSDateBox.setVisibility(View.GONE);
        }
    }

    private void checkShowOSData(QuoteOption osState, boolean cleanData) {

        mSSSFormEditText.setVisibility(View.VISIBLE);
        checkShowDateBox(osState, cleanData);

        // default hide files
        hideSindicalFiles();

        if (osState != null && osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE_AT_INTPUT)) {
            mSSSFormEditText.setText("999999");
            mSSSFormEditText.setEnabled(false);
        } else {
            if (cleanData) {
                mSSSFormEditText.setText("");
            }
            mSSSFormEditText.setEnabled(true);
            showSindicalFiles();
        }
    }

    private void initialize() {
        initializeForm();
        setupListeners();
        ((AffiliationActivity) getActivity()).setActionButtons(true);
    }

    private void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);

        initForm184Files();
        initSixMonthFiles();
        initOSCodeFiles();

        checkShowOSData(additionalData2.obraSocial.osState, false);

        // SINDICAL FILES
        initOptionChangeFiles();
        initFormFiles();
        initCertChangeFiles();

        checkEditableCardMode();
    }


    private void initOSBaseForm() {

        Log.e(TAG, "initOSBaseForm ---------------------------------------" );


        if (conyugeData != null && (conyugeData.obraSocial != null || conyugeData.osMonotributo != null)) {
            mOsConyugeButton.setVisibility(View.VISIBLE);
        } else {
            mOsConyugeButton.setVisibility(View.GONE);
        }

        ObraSocial obraSocial = additionalData2.obraSocial;
        if (obraSocial != null) {

            Log.e(TAG, "SSSSS " + obraSocial.osSSSFormNumber + " *******************");
            obraSocial.cardId = cardId;

            if (obraSocial.osQuotation != null) {
                mSelectedOS = mObrasSociales.indexOf(obraSocial.osQuotation);
                if (mSelectedOS != -1)
                    mOSEditText.setText(obraSocial.osQuotation.title);
            }

            if (obraSocial.empadronado != null) {
                if (obraSocial.empadronado) {
                    mOptionBox.setVisibility(View.GONE);
                    mEmpadronadoRadioButton.setChecked(true);
                    mOpcionRadioButton.setChecked(false);

                } else {
                    mOptionBox.setVisibility(View.VISIBLE);
                    mEmpadronadoRadioButton.setChecked(false);
                    mOpcionRadioButton.setChecked(true);

                    if (obraSocial.osState != null) {
                        mSelectedOSState = mOSStates.indexOf(obraSocial.osState);
                        if (mSelectedOSState != -1) {
                            mOSStateEditText.setText(obraSocial.osState.title);
                            checkShowOSSDateBox(mOSStates.get(mSelectedOSState));
                        }
                    } else {
                        mOSDateBox.setVisibility(View.GONE);
                    }

                    if (obraSocial.inputOSDate != null) {
                        mOSDateInputEditText.setText(obraSocial.getOSInputDate());
                    }

                    if (obraSocial.osSSSFormNumber != -1) {
                        mSSSFormEditText.setText(Long.valueOf(obraSocial.osSSSFormNumber).toString());
                    }
                }
            } else {
                mEmpadronadoRadioButton.setChecked(true);
                mOpcionRadioButton.setChecked(false);
                mOptionBox.setVisibility(View.GONE);
            }
        }
    }


    private void initForm184Files() {

        m184FileAdapter = new AttachFilesAdapter(additionalData2.form184Files, true);

        m184RecyclerView = (RecyclerView) view.findViewById(R.id.form_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(m184RecyclerView.getContext());
        m184RecyclerView.setLayoutManager(attachLayoutManager);
        m184RecyclerView.setAdapter(m184FileAdapter);
        m184RecyclerView.setHasFixedSize(true);
        add184Button = (Button) view.findViewById(R.id.form_button);
    }

    private void initSixMonthFiles() {

        mThreeMonthFileAdapter = new AttachFilesAdapter(additionalData2.threeMonthFiles, true);

        mThreeMonthRecyclerView = (RecyclerView) view.findViewById(R.id.month_ticket_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mThreeMonthRecyclerView.getContext());
        mThreeMonthRecyclerView.setLayoutManager(attachLayoutManager);
        mThreeMonthRecyclerView.setAdapter(mThreeMonthFileAdapter);
        mThreeMonthRecyclerView.setHasFixedSize(true);
        addThreeMonthButton = (Button) view.findViewById(R.id.month_ticket_button);
    }

    private void initOSCodeFiles() {

        mOSCodeFileAdapter = new AttachFilesAdapter(additionalData2.obraSocial.osCodeFiles, true);

        mOSCodeRecyclerView = (RecyclerView) view.findViewById(R.id.os_code_verify_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mOSCodeRecyclerView.getContext());
        mOSCodeRecyclerView.setLayoutManager(attachLayoutManager);
        mOSCodeRecyclerView.setAdapter(mOSCodeFileAdapter);
        mOSCodeRecyclerView.setHasFixedSize(true);
        addOSCodeButton = (Button) view.findViewById(R.id.os_code_verify_button);
    }

    private void initOptionChangeFiles() {

        mOptionChangeFileAdapter = new AttachFilesAdapter(additionalData2.obraSocial.optionChangeFiles, true);

        mOptionChangeRecyclerView = (RecyclerView) view.findViewById(R.id.os_change_option_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mOptionChangeRecyclerView.getContext());
        mOptionChangeRecyclerView.setLayoutManager(attachLayoutManager);
        mOptionChangeRecyclerView.setAdapter(mOptionChangeFileAdapter);
        mOptionChangeRecyclerView.setHasFixedSize(true);
        addOptionChangeButton = (Button) view.findViewById(R.id.os_change_option_button);
    }

    private void initFormFiles() {

        mFormFileAdapter = new AttachFilesAdapter(additionalData2.obraSocial.formFiles, true);

        mFormRecyclerView = (RecyclerView) view.findViewById(R.id.os_card_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFormRecyclerView.getContext());
        mFormRecyclerView.setLayoutManager(attachLayoutManager);
        mFormRecyclerView.setAdapter(mFormFileAdapter);
        mFormRecyclerView.setHasFixedSize(true);
        addFormButton = (Button) view.findViewById(R.id.os_card_button);
    }

    private void initCertChangeFiles() {
        mCertChangeFileAdapter = new AttachFilesAdapter(additionalData2.obraSocial.certChangeFiles, true);

        mCertChangeRecyclerView = (RecyclerView) view.findViewById(R.id.os_cert_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCertChangeRecyclerView.getContext());
        mCertChangeRecyclerView.setLayoutManager(attachLayoutManager);
        mCertChangeRecyclerView.setAdapter(mCertChangeFileAdapter);
        mCertChangeRecyclerView.setHasFixedSize(true);
        addCertChangeButton = (Button) view.findViewById(R.id.os_cert_button);
    }


    private void checkEditableCardMode() {
        mSSSFormEditText.setFocusable(editableCard);
        mEmpadronadoRadioButton.setEnabled(editableCard);
        mOpcionRadioButton.setEnabled(editableCard);

        if (!editableCard){
            disableView(add184Button);
            disableView(addThreeMonthButton);
            disableView(addOSCodeButton);
            disableView(addCertChangeButton);
            disableView(addOptionChangeButton);
            disableView(addFormButton);
        }
    }


    private void setupListeners() {
        setupImageProvider();

        mOsConyugeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "toConyugeData.....");

                // TODO TEMP  put earlier this data
                conyugeData.titularDNI = titularDNI;
                conyugeData.cardId = cardId;
                conyugeData.conyugeCardId = conyugeCardId;

                IntentHelper.goToConyugeDataActivity(getActivity(), conyugeData);
            }
        });

        if (editableCard) {

            View osCodeVerificationLink = mMainContainer.findViewById(R.id.os_code_verify_link);
            osCodeVerificationLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "osCodeVerificationLink clik .....");
                    //IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_sss_code), ConstantsUtil.OS_CODE_VERIFICATION_LINK);

                    if (osCodeLink == null  || osCodeLink.isEmpty()){
                        showProgressDialog(R.string.downloading_link);

                        CardController.getInstance().getLinkRequest(ConstantsUtil.LINK_ID_OS_CODE_VERIFICATION, new Response.Listener<LinkData>() {
                            @Override
                            public void onResponse(LinkData responseLink) {
                                dismissProgressDialog();

                                if (responseLink != null) {
                                    osCodeLink = responseLink.link;
                                    IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_sss_code), osCodeLink);
                                } else {
                                    DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_downloading_link));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissProgressDialog();
                                Log.e(TAG, "Error donwloading link: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_downloading_link), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                            }
                        });
                    }else{
                        IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_sss_code), osCodeLink);
                    }
                }
            });

            View osButton = mMainContainer.findViewById(R.id.os_button);
            osButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showOsAlert();
                }
            });
            osButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedOS = -1;
                    mOSEditText.setText("");
                    return true;
                }
            });

            View osStateButton = mMainContainer.findViewById(R.id.os_state_button);
            osStateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showOsStateAlert();
                }
            });
            osStateButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedOSState = -1;
                    mOSStateEditText.setText("");
                    return true;
                }
            });

            View osDateButton = mMainContainer.findViewById(R.id.os_date_button);
            osDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar();
                }
            });

            mEmpadronadoRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOptionBox.setVisibility(View.GONE);

                    mSelectedOSState = -1;
                    mOSStateEditText.setText("");
                    mSSSFormEditText.setText("");

                    // reset Files
                    //mSSSFileAdapter.removeAllItems();
                    mOptionChangeFileAdapter.removeAllItems();
                    mFormFileAdapter.removeAllItems();
                }
            });

            mOpcionRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOptionBox.setVisibility(View.VISIBLE);
                }
            });


            // FILE BUTTONS
            add184Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = FORM_184_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + FORM_184_FILE_PREFIX, FORM_184_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addThreeMonthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = MONTH_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + MONTH_FILE_PREFIX, MONTH_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addOSCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = OS_CODE_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + OS_CODE_FILE_PREFIX, OS_CODE_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            // SINDICAL
            addOptionChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = OPTION_CHANGE_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + OPTION_CHANGE_FILE_PREFIX, OPTION_CHANGE_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addFormButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = FORM_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + FORM_FILE_PREFIX, FORM_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addCertChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CERT_CHANGE_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CERT_CHANGE_FILE_PREFIX, CERT_CHANGE_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            // ADAPTERS
            m184FileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "form184File onItemDeleteClick()!!!!!:" + position);
                    attachFileType = FORM_184_FILE_PREFIX;
                    AttachFile formFile = additionalData2.form184Files.get(position);
                    if (formFile.id != -1) {
                        removeFile(formFile, position);
                    }
                }
            });
            mThreeMonthFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "month file onItemDeleteClick()!!!!!:" + position);
                    attachFileType = MONTH_FILE_PREFIX;
                    AttachFile monthFile = additionalData2.threeMonthFiles.get(position);
                    if (monthFile.id != -1) {
                        removeFile(monthFile, position);
                    }
                }
            });
            mOSCodeFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "afipFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = OS_CODE_FILE_PREFIX;
                    AttachFile osCodeFile = additionalData2.obraSocial.osCodeFiles.get(position);
                    if (osCodeFile.id != -1) {
                        removeFile(osCodeFile, position);
                    }
                }
            });

            // SINDICAL
            mOptionChangeFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "optionFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = OPTION_CHANGE_FILE_PREFIX;
                    AttachFile optionFile = additionalData2.obraSocial.optionChangeFiles.get(position);
                    if (optionFile.id != -1) {
                        removeFile(optionFile, position);
                    }
                }
            });
            mFormFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "formFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = FORM_FILE_PREFIX;
                    AttachFile formFile = additionalData2.obraSocial.formFiles.get(position);
                    if (formFile.id != -1) {
                        removeFile(formFile, position);
                    }
                }
            });
            mCertChangeFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "certFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CERT_CHANGE_FILE_PREFIX;
                    AttachFile certFile = additionalData2.obraSocial.certChangeFiles.get(position);
                    if (certFile.id != -1) {
                        removeFile(certFile, position);
                    }
                }
            });
        }

        // ADAPTERS
        m184FileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile form184File = additionalData2.form184Files.get(position);
                Log.e(TAG, "form184File path:" + form184File.filePath);
                loadFile(form184File);
            }
        });
        mThreeMonthFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile monthFile = additionalData2.threeMonthFiles.get(position);
                Log.e(TAG, "monthFile path:" + monthFile.filePath);
                loadFile(monthFile);
            }
        });
        mOSCodeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile osCodeFile = additionalData2.obraSocial.osCodeFiles.get(position);
                Log.e(TAG, "osCodeFile path:" + osCodeFile.filePath);
                loadFile(osCodeFile);
            }
        });

        // SINDICAL
        mOptionChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile optionFile = additionalData2.obraSocial.optionChangeFiles.get(position);
                Log.e(TAG, "optionFile path:" + optionFile.filePath);
                loadFile(optionFile);
            }
        });
        mFormFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile formFile = additionalData2.obraSocial.formFiles.get(position);
                Log.e(TAG, "formFile path:" + formFile.filePath);
                loadFile(formFile);
            }
        });
        mCertChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile certFile = additionalData2.obraSocial.certChangeFiles.get(position);
                Log.e(TAG, "certFile path:" + certFile.filePath);
                loadFile(certFile);
            }
        });
    }

    private boolean validateForm() {
        return true;
    }


    private void showOsAlert() {

        ArrayList<String> osStr = new ArrayList<String>();
        for (QuoteOption q : mObrasSociales) {
            osStr.add(q.optionName());
        }

        mOSAlertAdapter = new SpinnerDropDownAdapter(getActivity(), osStr, mSelectedOS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mOSAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedOS = i;
                        if (i == 0){
                            mSelectedOS = -1;
                            mOSEditText.setText("");
                        }else {
                            mOSEditText.setText(mObrasSociales.get(i).optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mOSAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showOsStateAlert() {

        ArrayList<String> osStateStr = new ArrayList<String>();
        for (QuoteOption q : mOSStates) {
            osStateStr.add(q.optionName());
        }

        mOSStateAlertAdapter = new SpinnerDropDownAdapter(getActivity(), osStateStr, mSelectedOSState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mOSStateAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedOSState = i;
                        if (i == 0){
                            mSelectedOSState = -1;
                            mOSStateEditText.setText("");
                        }else {
                            mOSStateEditText.setText(mOSStates.get(i).optionName());
                            checkShowOSData(mOSStates.get(mSelectedOSState), true);

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mOSStateAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void checkShowOSSDateBox(QuoteOption osState) {
        if (osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION)) {
            mOSDateBox.setVisibility(View.VISIBLE);
        } else {
            mOSDateBox.setVisibility(View.GONE);
            mOSDateInputEditText.setText("");
        }
    }

    private void checkShowAlertMessage(Date date) {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.MONTH, -9);

        Date limitDate = c.getTime();
        if (date.after(limitDate) && date.before(today)) {
            DialogHelper.showMessage(getActivity(), getResources().getString(R.string.affiliation_os_state_alert));
        }
    }

    private void showCalendar() {
        Date date = null;
        String data = mOSDateInputEditText.getText().toString().trim();
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

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
        dateDialog.show();
    }
}
