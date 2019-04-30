package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import ar.com.sancorsalud.asociados.activity.affiliation.ConyugeDataActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
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

// Condicionmonotributo always load CondicionMonotributoOs with segment 3
public class ConyugeOSMonotributoFragment extends BaseFragment {

    private static final String ARG_CONYUGE_DATA = "conyugeData";

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
    private ArrayList<QuoteOption> mOSCondicionMonotributo;

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

    private View view;
    private SimpleDateFormat mDateFormat;
    private ConyugeData mConyugeData;

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

    public static ConyugeOSMonotributoFragment newInstance(ConyugeData conyugeData) {
        ConyugeOSMonotributoFragment fragment = new ConyugeOSMonotributoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONYUGE_DATA, conyugeData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mConyugeData = (ConyugeData) getArguments().getSerializable(ARG_CONYUGE_DATA);
            //Log.e(TAG, "ConyugeOSMonotributo.cardId: " + mConyugeData.osMonotributo.cardId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_conyuge_os_monotributo, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");
        attachFilesSubDir = mConyugeData.titularDNI + "/affiliation/conyuge/mono";
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mImageProvider = new ImageProvider(getActivity());

        mOSEditText = (EditText) mMainContainer.findViewById(R.id.os_input);
        setTypeTextNoSuggestions(mOSEditText);

        mOSDateInputEditText = (EditText) mMainContainer.findViewById(R.id.os_date_input);
        setTypeTextNoSuggestions(mOSDateInputEditText);

        mEmpadronadoRadioButton = (RadioButton) mMainContainer.findViewById(R.id.os_code_empadronado_button);
        mOpcionRadioButton = (RadioButton) mMainContainer.findViewById(R.id.os_code_opcion_button);
        mOptionBox = (LinearLayout) mMainContainer.findViewById(R.id.os_opcion_box);

        mOSStateEditText = (EditText) mMainContainer.findViewById(R.id.os_state_input);
        setTypeTextNoSuggestions(mOSStateEditText);

        mOSDateBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_date_box);
        mSSSFormEditText = (EditText) mMainContainer.findViewById(R.id.os_form_input);
        setTypeTextNoSuggestions(mSSSFormEditText);

        // OS SINDICAL
        mOptionChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_change_option_box);
        mFormBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_card_box);
        mCertChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_cert_box);

        mScrollView = (ScrollView) mMainContainer.findViewById(R.id.scroll);

        this.view = view;

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        fillArraysData();
        initOSBaseForm();
        fillAllPhysicalFiles();
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
            //else if (attachFileType.equals(COMPROBANTE_SSS_FILE_PREFIX)) {
              //  mSSSFileAdapter.removeItem(position);
            //}
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

    public ObraSocial getConyugeOSMonotributoData() {
        Log.e(TAG, "----- getConyugeOSMonotributoData -------");

        ObraSocial osMonotributo = mConyugeData.osMonotributo;
        osMonotributo.isMonotributo = true;
        osMonotributo.cardId = mConyugeData.cardId;
        osMonotributo.personCardId = mConyugeData.conyugeCardId;

        if (mSelectedOS != -1) {
            osMonotributo.osQuotation = mOSCondicionMonotributo.get(mSelectedOS);
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

            // UPDATE FILES
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

            // UPDATE FILES
            osMonotributo.osCodeFiles =new ArrayList<AttachFile>();
            osMonotributo.optionChangeFiles = new ArrayList<AttachFile>();
            osMonotributo.formFiles = new ArrayList<AttachFile>();
            osMonotributo.certChangeFiles = new ArrayList<AttachFile>();
        }

        // UPDATE FILES

        if (m184FileAdapter != null) {
            mConyugeData.form184Files = m184FileAdapter.getAttachFiles();
        }
        if (mThreeMonthFileAdapter != null) {
            mConyugeData.threeMonthFiles = mThreeMonthFileAdapter.getAttachFiles();
        }
        if (mOSCodeFileAdapter != null) {
            osMonotributo.osCodeFiles = mOSCodeFileAdapter.getAttachFiles();
        }

        return osMonotributo;
    }


    public boolean isValidSection() {
        return validateForm();
    }
    // --- helper methods ---------------------------------------------------- //

    private void fillArraysData() {

        //MONOTRIBUTO ALWAYS WORK WITH OS SINDICAL TYPE
        mOSCondicionMonotributo = new ArrayList<>();
        QuoteOption osSelection = new QuoteOption("-1", getResources().getString(R.string.field_os));
        mOSCondicionMonotributo.add(osSelection);
        mOSCondicionMonotributo.addAll(QuoteOptionsController.getInstance().getOSCondMonotrinbuto(ConstantsUtil.OS_TYPE_SINDICAL));

        mOSStates = new ArrayList<>();
        QuoteOption osStateSelection = new QuoteOption("-1", getResources().getString(R.string.field_state));
        mOSStates.add(osStateSelection);
        mOSStates.addAll(QuoteOptionsController.getInstance().getOSStates(ConstantsUtil.OS_TYPE_SINDICAL));
    }


    private void fillAllPhysicalFiles() {
        ((ConyugeDataActivity) getActivity()).setActionButtons(false);

        // OS
        ObraSocial osMonotributo = mConyugeData.osMonotributo;
        if (osMonotributo == null) {
            osMonotributo = new ObraSocial();
        }
        osMonotributo.cardId = mConyugeData.cardId;

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

        if (!mConyugeData.form184Files.isEmpty()) {

            Log.e(TAG, "fill 184 Form Files !!....");

            final AttachFile form184File = mConyugeData.form184Files.remove(0);
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
                            Log.e(TAG, "Null 184 Form File ....");
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
            mConyugeData.form184Files.addAll(tmpForm184Files);
            tmpForm184Files = new ArrayList<AttachFile>();
            fillThreeMonthFiles();
        }
    }

    private void fillThreeMonthFiles() {

        if (!mConyugeData.threeMonthFiles.isEmpty()) {

            Log.e(TAG, "fill conyugeThreeMonthFiles !!....");

            final AttachFile monthFile = mConyugeData.threeMonthFiles.remove(0);
            if (monthFile.fileNameAndExtension == null || monthFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + MONTH_FILE_PREFIX, monthFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting threeMonth File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpThreeMonthFiles.add(resultFile);
                            fillThreeMonthFiles();

                        } else {
                            Log.e(TAG, "Null threeMonth file ....");
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
            mConyugeData.threeMonthFiles.addAll(tmpThreeMonthFiles);
            tmpThreeMonthFiles = new ArrayList<AttachFile>();
            fillOSCOdeFiles();
        }
    }

    private void fillOSCOdeFiles() {

        if (!mConyugeData.osMonotributo.osCodeFiles.isEmpty()) {

            Log.e(TAG, "fillOSCOdeFiles !!....");

            final AttachFile osCodeFile = mConyugeData.osMonotributo.osCodeFiles.remove(0);
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
                            Log.e(TAG, "Null OS CODE  file ....");
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
            mConyugeData.osMonotributo.osCodeFiles.addAll(tmpOSCodeFiles);
            tmpOSCodeFiles = new ArrayList<AttachFile>();
            fillOptionChangeFiles();
        }
    }

    // --- SINDICAL FILES ------------------------------------------------ //

    private void fillOptionChangeFiles() {

        if (!mConyugeData.osMonotributo.optionChangeFiles.isEmpty()) {

            Log.e(TAG, "fillOptionChangeFiles !!....");

            final AttachFile optionFile = mConyugeData.osMonotributo.optionChangeFiles.remove(0);
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
                            Log.e(TAG, "Null Option Change File file ....");
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
            mConyugeData.osMonotributo.optionChangeFiles.addAll(tmpOptionChangeFiles);
            tmpOptionChangeFiles = new ArrayList<AttachFile>();
            fillFormFiles();
        }
    }

    private void fillFormFiles() {

        if (!mConyugeData.osMonotributo.formFiles.isEmpty()) {

            Log.e(TAG, "fillFormFiles !!....");

            final AttachFile formFile = mConyugeData.osMonotributo.formFiles.remove(0);
            if (formFile.fileNameAndExtension == null || formFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_FILE_PREFIX, formFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Form File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpFormFiles.add(resultFile);
                            fillFormFiles();

                        } else {
                            Log.e(TAG, "Null From file ....");
                            tmpFormFiles.add(resultFile);
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
            mConyugeData.osMonotributo.formFiles.addAll(tmpFormFiles);
            tmpFormFiles = new ArrayList<AttachFile>();
            fillCertChangeFiles();
        }
    }

    private void fillCertChangeFiles() {

        if (!mConyugeData.osMonotributo.certChangeFiles.isEmpty()) {

            Log.e(TAG, "certChangeFiles !!....");

            final AttachFile certChangeFile = mConyugeData.osMonotributo.certChangeFiles.remove(0);
            if (certChangeFile.fileNameAndExtension == null || certChangeFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + CERT_CHANGE_FILE_PREFIX, certChangeFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting certChangeFile !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCertChangeFiles.add(resultFile);
                            fillCertChangeFiles();

                        } else {
                            Log.e(TAG, "Null certChangeFile ....");
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
            mConyugeData.osMonotributo.certChangeFiles.addAll(tmpCertChangeFiles);
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


    private void checkShowDateBox(QuoteOption osState) {
        if (osState != null && (osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION))) {
            mOSDateBox.setVisibility(View.VISIBLE);
            mOSDateInputEditText.setText("");
        } else {
            mOSDateBox.setVisibility(View.GONE);
        }
    }

    private void checkShowOSData(QuoteOption osState) {

        mSSSFormEditText.setVisibility(View.VISIBLE);
        checkShowDateBox(osState);

        // default hide files
        hideSindicalFiles();

        if (osState != null && osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE_AT_INTPUT)) {
            mSSSFormEditText.setText("999999");
            mSSSFormEditText.setEnabled(false);
        } else {
            mSSSFormEditText.setText("");
            mSSSFormEditText.setEnabled(true);
            showSindicalFiles();
        }
    }

    private void initialize() {
        initializeForm();
        setupListeners();
        ((ConyugeDataActivity) getActivity()).setActionButtons(true);
    }

    private void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);

        initForm184Files();
        initSixMonthFiles();
        initOSCodeFiles();

        checkShowOSData(mConyugeData.osMonotributo.osState);

        // SINDICAL FILES
        initOptionChangeFiles();
        initFormFiles();
        initCertChangeFiles();

        checkEditableCardMode();
    }

    private void initOSBaseForm() {


        ObraSocial osMonotributo = mConyugeData.osMonotributo;
        if (osMonotributo != null) {
            osMonotributo.cardId = mConyugeData.osMonotributo.cardId;

            if (osMonotributo.osQuotation != null) {
                mSelectedOS = mOSCondicionMonotributo.indexOf(osMonotributo.osQuotation);
                if (mSelectedOS != -1)
                    mOSEditText.setText(osMonotributo.osQuotation.title);
            }

            if (osMonotributo.empadronado != null) {
                if (osMonotributo.empadronado) {
                    mOptionBox.setVisibility(View.GONE);
                    mEmpadronadoRadioButton.setChecked(true);
                    mOpcionRadioButton.setChecked(false);

                } else {
                    mOptionBox.setVisibility(View.VISIBLE);
                    mEmpadronadoRadioButton.setChecked(false);
                    mOpcionRadioButton.setChecked(true);

                    if (osMonotributo.osState != null) {
                        mSelectedOSState = mOSStates.indexOf(osMonotributo.osState);
                        if (mSelectedOSState != -1) {
                            mOSStateEditText.setText(osMonotributo.osState.title);
                            checkShowOSSDateBox(mOSStates.get(mSelectedOSState), false);
                        }
                    } else {
                        mOSDateBox.setVisibility(View.GONE);
                    }

                    if (osMonotributo.inputOSDate != null) {
                        mOSDateInputEditText.setText(osMonotributo.getOSInputDate());
                    }

                    if (osMonotributo.osSSSFormNumber != -1) {
                        mSSSFormEditText.setText(Long.valueOf(osMonotributo.osSSSFormNumber).toString());
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

        m184FileAdapter = new AttachFilesAdapter(mConyugeData.form184Files, true);

        m184RecyclerView = (RecyclerView) view.findViewById(R.id.form_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(m184RecyclerView.getContext());
        m184RecyclerView.setLayoutManager(attachLayoutManager);
        m184RecyclerView.setAdapter(m184FileAdapter);
        m184RecyclerView.setHasFixedSize(true);
        add184Button = (Button) view.findViewById(R.id.form_button);
    }

    private void initSixMonthFiles() {

        mThreeMonthFileAdapter = new AttachFilesAdapter(mConyugeData.threeMonthFiles, true);

        mThreeMonthRecyclerView = (RecyclerView) view.findViewById(R.id.month_ticket_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mThreeMonthRecyclerView.getContext());
        mThreeMonthRecyclerView.setLayoutManager(attachLayoutManager);
        mThreeMonthRecyclerView.setAdapter(mThreeMonthFileAdapter);
        mThreeMonthRecyclerView.setHasFixedSize(true);
        addThreeMonthButton = (Button) view.findViewById(R.id.month_ticket_button);
    }

    private void initOSCodeFiles() {

        mOSCodeFileAdapter = new AttachFilesAdapter(mConyugeData.osMonotributo.osCodeFiles, true);

        mOSCodeRecyclerView = (RecyclerView) view.findViewById(R.id.os_code_verify_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mOSCodeRecyclerView.getContext());
        mOSCodeRecyclerView.setLayoutManager(attachLayoutManager);
        mOSCodeRecyclerView.setAdapter(mOSCodeFileAdapter);
        mOSCodeRecyclerView.setHasFixedSize(true);
        addOSCodeButton = (Button) view.findViewById(R.id.os_code_verify_button);
    }

    private void initOptionChangeFiles() {

        mOptionChangeFileAdapter = new AttachFilesAdapter(mConyugeData.osMonotributo.optionChangeFiles, true);

        mOptionChangeRecyclerView = (RecyclerView) view.findViewById(R.id.os_change_option_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mOptionChangeRecyclerView.getContext());
        mOptionChangeRecyclerView.setLayoutManager(attachLayoutManager);
        mOptionChangeRecyclerView.setAdapter(mOptionChangeFileAdapter);
        mOptionChangeRecyclerView.setHasFixedSize(true);
        addOptionChangeButton = (Button) view.findViewById(R.id.os_change_option_button);
    }

    private void initFormFiles() {

        mFormFileAdapter = new AttachFilesAdapter(mConyugeData.osMonotributo.formFiles, true);

        mFormRecyclerView = (RecyclerView) view.findViewById(R.id.os_card_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFormRecyclerView.getContext());
        mFormRecyclerView.setLayoutManager(attachLayoutManager);
        mFormRecyclerView.setAdapter(mFormFileAdapter);
        mFormRecyclerView.setHasFixedSize(true);
        addFormButton = (Button) view.findViewById(R.id.os_card_button);
    }

    private void initCertChangeFiles() {
        mCertChangeFileAdapter = new AttachFilesAdapter(mConyugeData.osMonotributo.certChangeFiles, true);

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
            //disableView(addSSSButton);
            disableView(addCertChangeButton);
            disableView(addOptionChangeButton);
            disableView(addFormButton);
            disableView(addCertChangeButton);
        }
    }


    private void setupListeners() {
        setupImageProvider();

        if (editableCard) {

            View osCodeVerificationLink = mMainContainer.findViewById(R.id.os_code_verify_link);
            osCodeVerificationLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "osCodeVerificationLink clik .....");

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
                    AttachFile formFile = mConyugeData.form184Files.get(position);
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
                    AttachFile monthFile = mConyugeData.threeMonthFiles.get(position);
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
                    AttachFile osCodeFile = mConyugeData.osMonotributo.osCodeFiles.get(position);
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
                    AttachFile optionFile = mConyugeData.osMonotributo.optionChangeFiles.get(position);
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
                    AttachFile formFile = mConyugeData.osMonotributo.formFiles.get(position);
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
                    AttachFile certFile = mConyugeData.osMonotributo.certChangeFiles.get(position);
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
                AttachFile form184File = mConyugeData.form184Files.get(position);
                Log.e(TAG, "form184File path:" + form184File.filePath);
                loadFile(form184File);
            }
        });
        mThreeMonthFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile monthFile = mConyugeData.threeMonthFiles.get(position);
                Log.e(TAG, "monthFile path:" + monthFile.filePath);
                loadFile(monthFile);
            }
        });
        mOSCodeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile osCodeFile = mConyugeData.osMonotributo.osCodeFiles.get(position);
                Log.e(TAG, "osCodeFile path:" + osCodeFile.filePath);
                loadFile(osCodeFile);
            }
        });

        // SINDICAL
        mOptionChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile optionFile = mConyugeData.osMonotributo.optionChangeFiles.get(position);
                Log.e(TAG, "optionFile path:" + optionFile.filePath);
                loadFile(optionFile);
            }
        });
        mFormFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile formFile = mConyugeData.osMonotributo.formFiles.get(position);
                Log.e(TAG, "formFile path:" + formFile.filePath);
                loadFile(formFile);
            }
        });
        mCertChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile certFile = mConyugeData.osMonotributo.certChangeFiles.get(position);
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
        for (QuoteOption q : mOSCondicionMonotributo) {
            osStr.add(q.optionName());
        }

        mOSAlertAdapter = new SpinnerDropDownAdapter(getActivity(), osStr, mSelectedOS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mOSAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedOS = i;
                        if (i == 0) {
                            mSelectedOS = -1;
                            mOSEditText.setText("");
                        } else {
                            mOSEditText.setText(mOSCondicionMonotributo.get(i).optionName());
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
                            checkShowOSData(mOSStates.get(mSelectedOSState));

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

    private void checkShowOSSDateBox(QuoteOption osState, boolean cleanData) {
        if (osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION)) {
            mOSDateBox.setVisibility(View.VISIBLE);
        } else {
            mOSDateBox.setVisibility(View.GONE);
            if (cleanData) {
                mOSDateInputEditText.setText("");
            }
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
