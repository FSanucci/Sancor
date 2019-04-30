package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

// Conyuge could be Desregulado or Montributista but for OS screen all os comes from segment 2
public class ConyugeOSFragment extends BaseFragment {

    private static final String ARG_CONYUGE_DATA = "conyugeData";

    private static final String COMPROBANTE_SSS_FILE_PREFIX = "sssCard";
    private static final String AFIP_FILE_PREFIX = "afip";

    private static final String OPTION_CHANGE_FILE_PREFIX = "opcion";
    private static final String FORM_FILE_PREFIX = "cartilla";
    private static final String CERT_CHANGE_FILE_PREFIX = "certChange";

    private static final String EMAIL_FILE_PREFIX = "email";
    private static final String FORM_53_FILE_PREFIX = "form53";
    private static final String FORM_59_FILE_PREFIX = "form59";
    private static final String MODEL_NOTE_FILE_PREFIX = "modelNote";

    private View view;
    private ScrollView mScrollView;

    // QUOTATION OBRA SOCIAL --> DESREGULARA
    private EditText mOSEditText;
    private EditText mOSStateEditText;
    private RelativeLayout mOSDateBox;
    private EditText mOSDateInputEditText;
    private EditText mSSSFormEditText;

    private String osType;

    // Radio buttons
    private RadioButton mDayAportesRadioButton;
    private RadioButton mMonthAportesRadioButton;
    private EditText mMesesImpagosEditText;
    private TextInputLayout mMesesImpagosWrapper;

    private SpinnerDropDownAdapter mOSAlertAdapter;
    private int mSelectedOS = -1;
    private int mSelectedActualOS = -1;
    private ArrayList<QuoteOption> mObrasSociales;

    // ACTUAL OS
    private RelativeLayout mOSActualBox;
    private SpinnerDropDownAdapter mOSActualAlertAdapter;
    private EditText mOSActualEditText;

    private SpinnerDropDownAdapter mOSStateAlertAdapter;
    private int mSelectedOSState = -1;
    private ArrayList<QuoteOption> mOSStates;

    // SSS Files
    private RecyclerView mSSSRecyclerView;
    private AttachFilesAdapter mSSSFileAdapter;
    private Button addSSSButton;

    // AFIP FILES
    private RecyclerView mAfipRecyclerView;
    private AttachFilesAdapter mAfipFileAdapter;
    private Button addAfipButton;

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

    // DIRECCION EMAIL FILES
    private RelativeLayout mEmailBox;
    private RecyclerView mEmailRecyclerView;
    private AttachFilesAdapter mEmailFileAdapter;
    private Button addEmailButton;

    private RelativeLayout mForm53Box;
    private RecyclerView mForm53RecyclerView;
    private AttachFilesAdapter mForm53FileAdapter;
    private Button addForm53Button;

    private RelativeLayout mForm59Box;
    private RecyclerView mForm59RecyclerView;
    private AttachFilesAdapter mForm59FileAdapter;
    private Button addForm59Button;

    private RelativeLayout mModelNoteBox;
    private RecyclerView mModelNoteRecyclerView;
    private AttachFilesAdapter mModelNoteFileAdapter;
    private Button addModelNoteButton;

    private List<AttachFile> tmpSSSFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpAfipFiles = new ArrayList<AttachFile>();

    // SINDICAL
    private List<AttachFile> tmpOptionChangeFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpFormFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpCertChangeFiles = new ArrayList<AttachFile>();

    // DIRECCION
    private List<AttachFile> tmpEmailFiles = new ArrayList<AttachFile>();
    private List<AttachFile> tmpForm53Files = new ArrayList<AttachFile>();
    private List<AttachFile> tmpForm59Files = new ArrayList<AttachFile>();
    private List<AttachFile> tmpModelNotesFiles = new ArrayList<AttachFile>();

    private ConyugeData mConyugeData;
    private SimpleDateFormat mDateFormat;
    private boolean editableCard = true;

    private String afipLink;
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

    public static ConyugeOSFragment newInstance(ConyugeData conyugeData) {
        ConyugeOSFragment fragment = new ConyugeOSFragment();
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
            //Log.e(TAG, "ConyugeObraSocial.cardId: " + mConyugeData.obraSocial.cardId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_conyuge_os, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");
        attachFilesSubDir = mConyugeData.titularDNI + "/affiliation/conyuge/osActual";
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mImageProvider = new ImageProvider(getActivity());

        mOSEditText = (EditText) mMainContainer.findViewById(R.id.os_input);
        setTypeTextNoSuggestions(mOSEditText);

        mOSStateEditText = (EditText) mMainContainer.findViewById(R.id.os_state_input);
        setTypeTextNoSuggestions(mOSStateEditText);

        mOSDateBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_date_box);
        mOSDateInputEditText = (EditText) mMainContainer.findViewById(R.id.os_date_input);

        mSSSFormEditText = (EditText) mMainContainer.findViewById(R.id.os_form_input);
        setTypeTextNoSuggestions(mSSSFormEditText);

        mDayAportesRadioButton = (RadioButton) mMainContainer.findViewById(R.id.afip_day_aportes_button);
        mMonthAportesRadioButton = (RadioButton) mMainContainer.findViewById(R.id.afip_month_aportes_button);
        mMesesImpagosEditText = (EditText) mMainContainer.findViewById(R.id.afip_cant_month_input);

        mMesesImpagosWrapper = (TextInputLayout) mMainContainer.findViewById(R.id.afip_cant_month_wrapper);

        mOSActualBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_actual_box);
        mOSActualEditText = (EditText) mMainContainer.findViewById(R.id.os_actual_input);
        setTypeTextNoSuggestions(mOSActualEditText);

        // SINDICAL
        mOptionChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_change_option_box);
        mFormBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_card_box);
        mCertChangeBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_cert_box);

        // DIRECCION
        mEmailBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_email_box);
        mForm53Box = (RelativeLayout) mMainContainer.findViewById(R.id.os_form_5_3_box);
        mForm59Box = (RelativeLayout) mMainContainer.findViewById(R.id.os_form_5_9_box);
        mModelNoteBox = (RelativeLayout) mMainContainer.findViewById(R.id.os_note_box);

        mObrasSociales = new ArrayList<>();
        QuoteOption osSelection = new QuoteOption("-1", getResources().getString(R.string.field_os));
        mObrasSociales.add(osSelection);
        mObrasSociales.addAll( QuoteOptionsController.getInstance().getOSDesreguladas());

        mOSActualBox.setVisibility(View.GONE);
        mScrollView = (ScrollView) mMainContainer.findViewById(R.id.scroll);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);


        if (mConyugeData != null) {

            osType = mConyugeData.obraSocial.osQuotation.extra;
            if (osType != null) {
                Log.e(TAG, "Conyuge OS: type: " + osType + " *******************");

                mOSStates = new ArrayList<>();
                QuoteOption osStateSelection = new QuoteOption("-1", getResources().getString(R.string.field_state));
                mOSStates.add(osStateSelection);
                if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                    mOSStates.addAll(QuoteOptionsController.getInstance().getOSStates(ConstantsUtil.OS_TYPE_SINDICAL));
                } else {
                    mOSStates.addAll(QuoteOptionsController.getInstance().getOSStates(ConstantsUtil.OS_TYPE_DIRECCION));
                }
            }

            initOSBaseForm();
            fillAllPhysicalFiles();
        }
        this.view = view;
    }

    @Override
    public void updateFileList(AttachFile attachFile) {

        // SINDICAL
        if (attachFileType.equals(COMPROBANTE_SSS_FILE_PREFIX)) {
            mSSSFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(AFIP_FILE_PREFIX)) {
            mAfipFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(OPTION_CHANGE_FILE_PREFIX)) {
            mOptionChangeFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(FORM_FILE_PREFIX)) {
            mFormFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CERT_CHANGE_FILE_PREFIX)) {
            mCertChangeFileAdapter.addItem(attachFile);
        }
        // DIRECCION
        else if (attachFileType.equals(EMAIL_FILE_PREFIX)) {
            mEmailFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(FORM_53_FILE_PREFIX)) {
            mForm53FileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(FORM_59_FILE_PREFIX)) {
            mForm59FileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(MODEL_NOTE_FILE_PREFIX)) {
            mModelNoteFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position) {

        try {
            // SINDICAL
            if (attachFileType.equals(COMPROBANTE_SSS_FILE_PREFIX)) {
                mSSSFileAdapter.removeItem(position);
            } else if (attachFileType.equals(AFIP_FILE_PREFIX)) {
                mAfipFileAdapter.removeItem(position);
            } else if (attachFileType.equals(OPTION_CHANGE_FILE_PREFIX)) {
                mOptionChangeFileAdapter.removeItem(position);
            } else if (attachFileType.equals(FORM_FILE_PREFIX)) {
                mFormFileAdapter.removeItem(position);
            } else if (attachFileType.equals(CERT_CHANGE_FILE_PREFIX)) {
                mCertChangeFileAdapter.removeItem(position);
            }

            // DIRECCION
            else if (attachFileType.equals(EMAIL_FILE_PREFIX)) {
                mEmailFileAdapter.removeItem(position);
            } else if (attachFileType.equals(FORM_53_FILE_PREFIX)) {
                mForm53FileAdapter.removeItem(position);
            } else if (attachFileType.equals(FORM_59_FILE_PREFIX)) {
                mForm59FileAdapter.removeItem(position);
            } else if (attachFileType.equals(MODEL_NOTE_FILE_PREFIX)) {
                mModelNoteFileAdapter.removeItem(position);
            }
        } catch (Throwable e) {
        }
    }


    public ObraSocial getConyugeOSData() {
        Log.e(TAG, "----- getConyugeOSData() -------");

        ObraSocial obraSocial = mConyugeData.obraSocial;
        obraSocial.isMonotributo = false;
        obraSocial.cardId = mConyugeData.cardId;
        obraSocial.personCardId = mConyugeData.conyugeCardId;

        if (mSelectedOS != -1) {
            obraSocial.osQuotation = mObrasSociales.get(mSelectedOS);
        }
        if (mSelectedActualOS != -1) {
            obraSocial.osActual = mObrasSociales.get(mSelectedActualOS);
        }

        if (mSelectedOSState != -1) {
            obraSocial.osState = mOSStates.get(mSelectedOSState);

            if (obraSocial.osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION) || obraSocial.osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE)) {
                if (!mOSDateInputEditText.getText().toString().isEmpty()) {
                    try {
                        obraSocial.inputOSDate = ParserUtils.parseDate(mOSDateInputEditText.getText().toString(), "yyyy-MM-dd");
                    } catch (Exception e) {
                    }
                }
            } else {
                obraSocial.inputOSDate = null;
            }
        } else {
            obraSocial.inputOSDate = null;
        }

        if (!mMesesImpagosEditText.getText().toString().isEmpty()) {
            obraSocial.mesesImpagos = Integer.valueOf(mMesesImpagosEditText.getText().toString().trim());
        } else {
            obraSocial.mesesImpagos = 0;
        }

        if (!mSSSFormEditText.getText().toString().isEmpty()) {
            obraSocial.osSSSFormNumber = Integer.valueOf(mSSSFormEditText.getText().toString().trim());
        } else {
            obraSocial.osSSSFormNumber = -1L;
        }


        // UPDATE FILES
        if (mSSSFileAdapter != null) {
            obraSocial.comprobantesSSSFiles = mSSSFileAdapter.getAttachFiles();
        }
        if (mAfipFileAdapter != null) {
            obraSocial.comprobantesAfipFiles = mAfipFileAdapter.getAttachFiles();
        }

        if (osType != null) {
            if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {

                if (mOptionChangeFileAdapter != null) {
                    obraSocial.optionChangeFiles = mOptionChangeFileAdapter.getAttachFiles();
                }
                if (mFormFileAdapter != null) {
                    obraSocial.formFiles = mFormFileAdapter.getAttachFiles();
                }
                if (mCertChangeFileAdapter != null) {
                    obraSocial.certChangeFiles = mCertChangeFileAdapter.getAttachFiles();
                }
            } else {
                // DIRECCION
                if (mSelectedActualOS != -1) {
                    obraSocial.osActual = mObrasSociales.get(mSelectedActualOS);
                } else {
                    obraSocial.osActual = null;
                }
                if (mEmailFileAdapter != null) {
                    obraSocial.emailFiles = mEmailFileAdapter.getAttachFiles();
                }
                if (mForm53FileAdapter != null) {
                    obraSocial.form53Files = mForm53FileAdapter.getAttachFiles();
                }
                if (mForm59FileAdapter != null) {
                    obraSocial.form59Files = mForm59FileAdapter.getAttachFiles();
                }
                if (mModelNoteFileAdapter != null) {
                    obraSocial.modelNotesFiles = mModelNoteFileAdapter.getAttachFiles();
                }
            }
        }

        return obraSocial;
    }


    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private void fillAllPhysicalFiles() {

        ((ConyugeDataActivity) getActivity()).setActionButtons(false);

        // OS
        ObraSocial obraSocial = mConyugeData.obraSocial;
        obraSocial.cardId =  mConyugeData.cardId;

        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll AddData1 Data PhysicalFiles ....");
                fillComprobantesSSSFiles();
            }
        }).start();
    }

    private void fillComprobantesSSSFiles() {

        if (!mConyugeData.obraSocial.comprobantesSSSFiles.isEmpty()) {

            Log.e(TAG, "fillComprobantesSSSFiles !!....");

            final AttachFile sssFile = mConyugeData.obraSocial.comprobantesSSSFiles.remove(0);
            if (sssFile.fileNameAndExtension == null || sssFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + COMPROBANTE_SSS_FILE_PREFIX, sssFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting SSS File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpSSSFiles.add(resultFile);
                            fillComprobantesSSSFiles();

                        } else {
                            Log.e(TAG, "Null SSS File  ....");
                            tmpSSSFiles.add(sssFile);
                            fillComprobantesSSSFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpSSSFiles.add(sssFile);
                        fillComprobantesSSSFiles();
                    }
                });
            } else {
                tmpSSSFiles.add(sssFile);
                fillComprobantesSSSFiles();
            }

        } else {
            mConyugeData.obraSocial.comprobantesSSSFiles.addAll(tmpSSSFiles);
            tmpSSSFiles = new ArrayList<AttachFile>();
            fillAfipFiles();
        }
    }

    private void fillAfipFiles() {

        if (! mConyugeData.obraSocial.comprobantesAfipFiles.isEmpty()) {

            Log.e(TAG, "fillAfipFiles !!....");

            final AttachFile afipFile =  mConyugeData.obraSocial.comprobantesAfipFiles.remove(0);
            if (afipFile.fileNameAndExtension == null || afipFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + AFIP_FILE_PREFIX, afipFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting AFIP File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpAfipFiles.add(resultFile);
                            fillAfipFiles();

                        } else {
                            Log.e(TAG, "Null AFIP File ....");
                            tmpAfipFiles.add(afipFile);
                            fillAfipFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpAfipFiles.add(afipFile);
                        fillAfipFiles();
                    }
                });
            } else {
                tmpAfipFiles.add(afipFile);
                fillAfipFiles();
            }

        } else {
            mConyugeData.obraSocial.comprobantesAfipFiles.addAll(tmpAfipFiles);
            tmpAfipFiles = new ArrayList<AttachFile>();

            if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                fillOptionChangeFiles();
            } else {
                fillEmailFiles();
            }
        }
    }


    // --- SINDICAL FILES ------------------------------------------------ //

    private void fillOptionChangeFiles() {

        if (! mConyugeData.obraSocial.optionChangeFiles.isEmpty()) {

            Log.e(TAG, "fillOptionChangeFiles !!....");

            final AttachFile optionFile =  mConyugeData.obraSocial.optionChangeFiles.remove(0);
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
                            Log.e(TAG, "Null Option Change File....");
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
            mConyugeData.obraSocial.optionChangeFiles.addAll(tmpOptionChangeFiles);
            tmpOptionChangeFiles = new ArrayList<AttachFile>();
            fillFormFiles();
        }
    }

    private void fillFormFiles() {

        if (! mConyugeData.obraSocial.formFiles.isEmpty()) {

            Log.e(TAG, "fill OS form Files !!....");

            final AttachFile formFile =  mConyugeData.obraSocial.formFiles.remove(0);
            if (formFile.fileNameAndExtension == null || formFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_FILE_PREFIX, formFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting OS form File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpFormFiles.add(resultFile);
                            fillFormFiles();

                        } else {
                            Log.e(TAG, "Null OS form file ....");
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
            mConyugeData.obraSocial.formFiles.addAll(tmpFormFiles);
            tmpFormFiles = new ArrayList<AttachFile>();
            fillCertChangeFiles();
        }
    }

    private void fillCertChangeFiles() {

        if (! mConyugeData.obraSocial.certChangeFiles.isEmpty()) {

            Log.e(TAG, "certChangeFiles !!....");

            final AttachFile certChangeFile =  mConyugeData.obraSocial.certChangeFiles.remove(0);
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
                            Log.e(TAG, "Null certChangeFiles....");
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
            mConyugeData.obraSocial.certChangeFiles.addAll(tmpCertChangeFiles);
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


    // --- DIRECCION FILES ------------------------------------------------ //


    private void fillEmailFiles() {

        if (! mConyugeData.obraSocial.emailFiles.isEmpty()) {

            Log.e(TAG, "emailFiles !!....");

            final AttachFile emailFile =  mConyugeData.obraSocial.emailFiles.remove(0);
            if (emailFile.fileNameAndExtension == null || emailFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + EMAIL_FILE_PREFIX, emailFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting email File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpEmailFiles.add(resultFile);
                            fillEmailFiles();

                        } else {
                            Log.e(TAG, "Null email file ....");
                            tmpEmailFiles.add(emailFile);
                            fillEmailFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpEmailFiles.add(emailFile);
                        fillEmailFiles();
                    }
                });
            } else {
                tmpEmailFiles.add(emailFile);
                fillEmailFiles();
            }

        } else {
            mConyugeData.obraSocial.emailFiles.addAll(tmpEmailFiles);
            tmpEmailFiles = new ArrayList<AttachFile>();
            fillForm53Files();
        }
    }


    private void fillForm53Files() {

        if (! mConyugeData.obraSocial.form53Files.isEmpty()) {

            Log.e(TAG, "form53Files !!....");

            final AttachFile form53File =  mConyugeData.obraSocial.form53Files.remove(0);
            if (form53File.fileNameAndExtension == null || form53File.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_53_FILE_PREFIX, form53File.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting form53File  !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpForm53Files.add(resultFile);
                            fillForm53Files();

                        } else {
                            Log.e(TAG, "Null form53File ....");
                            tmpForm53Files.add(form53File);
                            fillForm53Files();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpForm53Files.add(form53File);
                        fillForm53Files();
                    }
                });
            } else {
                tmpForm53Files.add(form53File);
                fillForm53Files();
            }

        } else {
            mConyugeData.obraSocial.form53Files.addAll(tmpForm53Files);
            tmpForm53Files = new ArrayList<AttachFile>();
            fillForm59Files();
        }
    }

    private void fillForm59Files() {

        if (! mConyugeData.obraSocial.form59Files.isEmpty()) {

            Log.e(TAG, "form59Files !!....");

            final AttachFile form59File =  mConyugeData.obraSocial.form59Files.remove(0);
            if (form59File.fileNameAndExtension == null || form59File.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + FORM_59_FILE_PREFIX, form59File.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting form59File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpForm59Files.add(resultFile);
                            fillForm59Files();

                        } else {
                            Log.e(TAG, "Null form59File ....");
                            tmpForm59Files.add(form59File);
                            fillForm59Files();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpForm59Files.add(form59File);
                        fillForm59Files();
                    }
                });
            } else {
                tmpForm59Files.add(form59File);
                fillForm59Files();
            }

        } else {
            mConyugeData.obraSocial.form59Files.addAll(tmpForm59Files);
            tmpForm59Files = new ArrayList<AttachFile>();
            fillModelNotesFiles();
        }
    }


    private void fillModelNotesFiles() {

        if (! mConyugeData.obraSocial.modelNotesFiles.isEmpty()) {

            Log.e(TAG, "fillModelNotesFiles !!....");

            final AttachFile noteFile =  mConyugeData.obraSocial.modelNotesFiles.remove(0);
            if (noteFile.fileNameAndExtension == null || noteFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + MODEL_NOTE_FILE_PREFIX, noteFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting ModelNotesFile !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpModelNotesFiles.add(resultFile);
                            fillModelNotesFiles();

                        } else {
                            Log.e(TAG, "Null ModelNotesFile ....");
                            tmpModelNotesFiles.add(noteFile);
                            fillModelNotesFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpModelNotesFiles.add(noteFile);
                        fillModelNotesFiles();
                    }
                });
            } else {
                tmpModelNotesFiles.add(noteFile);
                fillModelNotesFiles();
            }

        } else {
            mConyugeData.obraSocial.modelNotesFiles.addAll(tmpModelNotesFiles);
            tmpModelNotesFiles = new ArrayList<AttachFile>();
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

    private void showDireccionFiles() {
        Log.e(TAG, "showDireccionFiles ...");
        mEmailBox.setVisibility(View.VISIBLE);
        mForm53Box.setVisibility(View.VISIBLE);
        mForm59Box.setVisibility(View.VISIBLE);
        mModelNoteBox.setVisibility(View.VISIBLE);
    }

    private void hideDireccionFiles() {
        mEmailBox.setVisibility(View.GONE);
        mForm53Box.setVisibility(View.GONE);
        mForm59Box.setVisibility(View.GONE);
        mModelNoteBox.setVisibility(View.GONE);
    }

    private void showEmailFile() {
        mEmailBox.setVisibility(View.VISIBLE);
    }


    private void showOSDateBox(){
        mOSDateBox.setVisibility(View.VISIBLE);
    }

    private void hideOSDateBox(boolean cleanData){
        mOSDateBox.setVisibility(View.GONE);
        if (cleanData) {
            mOSDateInputEditText.setText("");
        }
    }

    private void checkShowDateBox(QuoteOption osState, boolean cleanData) {

        if (osState != null && (osState.id.equals(ConstantsUtil.OS_STATE_OTHER_OS_OPTION) || osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE))) {
            showOSDateBox();
        } else {
            hideOSDateBox(cleanData);
        }
    }

    private void checkShowOSFields(QuoteOption osState, boolean cleanData) {

        checkShowDateBox(osState, cleanData);

        if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {

            mSSSFormEditText.setVisibility(View.VISIBLE);
            // default hide files
            hideSindicalFiles();

            if (osState != null && (osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE_AT_INTPUT))) {
                mSSSFormEditText.setText("999999");
                mSSSFormEditText.setEnabled(false);
            } else {
                if (cleanData) {
                    mSSSFormEditText.setText("");
                }
                mSSSFormEditText.setEnabled(true);
                showSindicalFiles();
            }

        } else {
            //  DESREGULARA OS_DIRECCION: OSIM or ASE
            mSSSFormEditText.setVisibility(View.GONE);

            // default hide files
            hideDireccionFiles();

            if (osState != null && (osState.id.equals(ConstantsUtil.OS_STATE_ACTIVE))) {
                mOSActualBox.setVisibility(View.VISIBLE);
                if (mSelectedActualOS != -1) {
                    checkShowFieldsForActualOS(mObrasSociales.get(mSelectedActualOS), osState, cleanData);
                }
            } else {
                mOSActualBox.setVisibility(View.GONE);

                String osId =  mConyugeData.obraSocial.osQuotation.id;
                if (osId != null && osId.equals(ConstantsUtil.OS_OSIM_ID)) {
                    showEmailFile();
                }
            }
        }
    }

    private void checkShowFieldsForActualOS(QuoteOption oSActual, QuoteOption osState, boolean cleanData) {
        hideDireccionFiles();

        String osId =  mConyugeData.obraSocial.osQuotation.id;

        // if Desregulara is the same as actual
        if (oSActual.id.equals(osId)) {
            if (osId.equals(ConstantsUtil.OS_OSIM_ID)) {
                showEmailFile();
            }
            else if (osId.equals(ConstantsUtil.OS_ASE_ID)) {
                Log.e(TAG, "ASE vs ASE");
            }


        } else if (oSActual.extra != null && !oSActual.extra.isEmpty()) {

            // CHECK OS DSREGULARA TYPE
            if (oSActual.extra.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                if (osId.equals(ConstantsUtil.OS_OSIM_ID)) {
                    showEmailFile();
                    hideOSDateBox(cleanData);
                }
                // FOR OS OSIM and ASE
                mForm53Box.setVisibility(View.VISIBLE);
                mModelNoteBox.setVisibility(View.VISIBLE);
            } else {
                // DIRECCION
                showOSDateBox();
                if (osId.equals(ConstantsUtil.OS_OSIM_ID)) {
                    showEmailFile();
                } else if (osId.equals(ConstantsUtil.OS_ASE_ID)) {
                    Log.e(TAG, "ASE OS DESREGULARA");
                }
                // FOR OS OSIM and ASE
                mForm59Box.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initialize() {
        initializeForm();
        setupListeners();
        ((ConyugeDataActivity) getActivity()).setActionButtons(true);
    }

    private void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);


        initComprobanteSSSFiles();
        initComprobantesAfipFiles();

        checkShowOSFields( mConyugeData.obraSocial.osState, false);

        if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
            initOptionChangeFiles();
            initFormFiles();
            initCertChangeFiles();

        } else {
            // DIRECCION
            initEmailFiles();
            initForm53Files();
            initFomr59Files();
            initModelNoteFiles();
        }

        checkEditableCardMode();
    }

    private void checkEditableCardMode() {

        mSSSFormEditText.setFocusable(editableCard);
        mMesesImpagosEditText.setFocusable(editableCard);

        mDayAportesRadioButton.setEnabled(editableCard);
        mMonthAportesRadioButton.setEnabled(editableCard);

        if (!editableCard){
            disableView(addSSSButton);
            disableView(addAfipButton);
            disableView(addOptionChangeButton);
            disableView(addFormButton);
            disableView(addCertChangeButton);
            disableView(addEmailButton);
            disableView(addForm53Button);
            disableView(addForm59Button);
            disableView(addModelNoteButton);
        }
    }

    private void initOSBaseForm() {

            mConyugeData.obraSocial.cardId = mConyugeData.cardId;

            if ( mConyugeData.obraSocial.osQuotation != null) {
                mSelectedOS = mObrasSociales.indexOf( mConyugeData.obraSocial.osQuotation);
                if (mSelectedOS != -1) {
                    mOSEditText.setText( mConyugeData.obraSocial.osQuotation.title);
                }
            }

            if ( mConyugeData.obraSocial.inputOSDate != null) {
                mOSDateInputEditText.setText( mConyugeData.obraSocial.getOSInputDate());
            }

            if ( mConyugeData.obraSocial.osState != null) {
                mSelectedOSState = mOSStates.indexOf( mConyugeData.obraSocial.osState);
                if (mSelectedOSState != -1) {
                    mOSStateEditText.setText( mConyugeData.obraSocial.osState.title);
                }
            } else {
                mOSDateBox.setVisibility(View.GONE);
            }

            if ( mConyugeData.obraSocial.osSSSFormNumber != -1) {
                mSSSFormEditText.setText(Long.valueOf( mConyugeData.obraSocial.osSSSFormNumber).toString());
            }

            if ( mConyugeData.obraSocial.mesesImpagos != -1) {
                if ( mConyugeData.obraSocial.mesesImpagos == 0) {
                    mMesesImpagosWrapper.setVisibility(View.GONE);
                    mDayAportesRadioButton.setChecked(true);
                    mMonthAportesRadioButton.setChecked(false);
                } else {
                    mMesesImpagosWrapper.setVisibility(View.VISIBLE);
                    mMesesImpagosEditText.setText(Integer.valueOf( mConyugeData.obraSocial.mesesImpagos).toString());
                    mDayAportesRadioButton.setChecked(false);
                    mMonthAportesRadioButton.setChecked(true);

                    if (editableCard &&  mConyugeData.obraSocial.mesesImpagos > 2) {
                        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.affiliation_afip_cant_month_value_error_title), getResources().getString(R.string.affiliation_afip_cant_month_value_error));
                    }
                }
            }

            if (osType != null && osType.equals(ConstantsUtil.OS_TYPE_DIRECCION) &&  mConyugeData.obraSocial.osActual != null) {
                mSelectedActualOS = mObrasSociales.indexOf( mConyugeData.obraSocial.osActual);
                if (mSelectedActualOS != -1) {
                    mOSActualEditText.setText( mConyugeData.obraSocial.osActual.title);
                }
            }
    }


    // --- SINDICAL FILES ------------------------------------------------------------------ //

    private void initComprobanteSSSFiles() {
        Log.e(TAG, "initComprobanteSSSFiles");

        mSSSFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.comprobantesSSSFiles, true);

        mSSSRecyclerView = (RecyclerView) view.findViewById(R.id.os_verify_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mSSSRecyclerView.getContext());
        mSSSRecyclerView.setLayoutManager(attachLayoutManager);
        mSSSRecyclerView.setAdapter(mSSSFileAdapter);
        mSSSRecyclerView.setHasFixedSize(true);
        addSSSButton = (Button) view.findViewById(R.id.os_verify_button);
    }

    private void initComprobantesAfipFiles() {

        Log.e(TAG, "initComprobantesAfipFiles");

        mAfipFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.comprobantesAfipFiles, true);

        mAfipRecyclerView = (RecyclerView) view.findViewById(R.id.afip_verify_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mAfipRecyclerView.getContext());
        mAfipRecyclerView.setLayoutManager(attachLayoutManager);
        mAfipRecyclerView.setAdapter(mAfipFileAdapter);
        mAfipRecyclerView.setHasFixedSize(true);
        addAfipButton = (Button) view.findViewById(R.id.afip_verify_button);
    }


    private void initOptionChangeFiles() {
        mOptionChangeFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.optionChangeFiles, true);

        mOptionChangeRecyclerView = (RecyclerView) view.findViewById(R.id.os_change_option_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mOptionChangeRecyclerView.getContext());
        mOptionChangeRecyclerView.setLayoutManager(attachLayoutManager);
        mOptionChangeRecyclerView.setAdapter(mOptionChangeFileAdapter);
        mOptionChangeRecyclerView.setHasFixedSize(true);
        addOptionChangeButton = (Button) view.findViewById(R.id.os_change_option_button);
    }

    private void initFormFiles() {
        mFormFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.formFiles, true);

        mFormRecyclerView = (RecyclerView) view.findViewById(R.id.os_card_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mFormRecyclerView.getContext());
        mFormRecyclerView.setLayoutManager(attachLayoutManager);
        mFormRecyclerView.setAdapter(mFormFileAdapter);
        mFormRecyclerView.setHasFixedSize(true);
        addFormButton = (Button) view.findViewById(R.id.os_card_button);
    }

    private void initCertChangeFiles() {
        mCertChangeFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.certChangeFiles, true);

        mCertChangeRecyclerView = (RecyclerView) view.findViewById(R.id.os_cert_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCertChangeRecyclerView.getContext());
        mCertChangeRecyclerView.setLayoutManager(attachLayoutManager);
        mCertChangeRecyclerView.setAdapter(mCertChangeFileAdapter);
        mSSSRecyclerView.setHasFixedSize(true);
        addCertChangeButton = (Button) view.findViewById(R.id.os_cert_button);
    }


    // --- DIRECCION FILES ------------------------------------------------------------------ //

    private void initEmailFiles() {

        Log.e(TAG, "initEmailFiles");

        mEmailFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.emailFiles, true);

        mEmailRecyclerView = (RecyclerView) view.findViewById(R.id.os_email_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mEmailRecyclerView.getContext());
        mEmailRecyclerView.setLayoutManager(attachLayoutManager);
        mEmailRecyclerView.setAdapter(mEmailFileAdapter);
        mEmailRecyclerView.setHasFixedSize(true);
        addEmailButton = (Button) view.findViewById(R.id.os_email_button);
    }

    private void initForm53Files() {

        Log.e(TAG, "initForm53Files");
        mForm53FileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.form53Files, true);

        mForm53RecyclerView = (RecyclerView) view.findViewById(R.id.os_form_5_3_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mForm53RecyclerView.getContext());
        mForm53RecyclerView.setLayoutManager(attachLayoutManager);
        mForm53RecyclerView.setAdapter(mForm53FileAdapter);
        mForm53RecyclerView.setHasFixedSize(true);
        addForm53Button = (Button) view.findViewById(R.id.os_form_5_3_button);
    }

    private void initFomr59Files() {
        Log.e(TAG, "initFomr59Files");

        mForm59FileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.form59Files, true);

        mForm59RecyclerView = (RecyclerView) view.findViewById(R.id.os_form_5_9_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mForm59RecyclerView.getContext());
        mForm59RecyclerView.setLayoutManager(attachLayoutManager);
        mForm59RecyclerView.setAdapter(mForm59FileAdapter);
        mForm59RecyclerView.setHasFixedSize(true);
        addForm59Button = (Button) view.findViewById(R.id.os_form_5_9_button);
    }

    private void initModelNoteFiles() {
        Log.e(TAG, "initModelNoteFiles");

        mModelNoteFileAdapter = new AttachFilesAdapter( mConyugeData.obraSocial.modelNotesFiles, true);

        mModelNoteRecyclerView = (RecyclerView) view.findViewById(R.id.os_note_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mModelNoteRecyclerView.getContext());
        mModelNoteRecyclerView.setLayoutManager(attachLayoutManager);
        mModelNoteRecyclerView.setAdapter(mModelNoteFileAdapter);
        mModelNoteRecyclerView.setHasFixedSize(true);
        addModelNoteButton = (Button) view.findViewById(R.id.os_note_button);
    }


    private void setupListeners() {
        setupImageProvider();

        if (editableCard) {

            View osOriginVerificationLink = mMainContainer.findViewById(R.id.os_origin_verify_link);
            osOriginVerificationLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "osOriginVerificationLink clik .....");
                    //IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_origin_title), ConstantsUtil.OS_ORIGIN_VERIFICATION_LINK);

                    if (osCodeLink == null  || osCodeLink.isEmpty()){
                        showProgressDialog(R.string.downloading_link);

                        CardController.getInstance().getLinkRequest(ConstantsUtil.LINK_ID_OS_CODE_VERIFICATION, new Response.Listener<LinkData>() {
                            @Override
                            public void onResponse(LinkData responseLink) {
                                dismissProgressDialog();

                                if (responseLink != null) {
                                    osCodeLink = responseLink.link;
                                    IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_origin_title), osCodeLink);
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
                        IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_origin_title), osCodeLink);
                    }
                }
            });


            View osAfipVerificationLink = mMainContainer.findViewById(R.id.os_afip_verify_link);
            osAfipVerificationLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "osOriginVerificationLink clik .....");
                    //IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_situacion_prov), ConstantsUtil.OS_AFIP_VERIFICATION_LINK);

                    if (afipLink == null  || afipLink.isEmpty()){
                        showProgressDialog(R.string.downloading_link);

                        CardController.getInstance().getLinkRequest(ConstantsUtil.LINK_ID_OS_AFIP_VERIFICATION, new Response.Listener<LinkData>() {
                            @Override
                            public void onResponse(LinkData responseLink) {
                                dismissProgressDialog();

                                if (responseLink != null) {
                                    afipLink = responseLink.link;
                                    IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_situacion_prov), afipLink);
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
                        IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.title_os_situacion_prov), afipLink);
                    }


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

            mDayAportesRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMesesImpagosWrapper.setVisibility(View.GONE);
                }
            });

            mMonthAportesRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMesesImpagosWrapper.setVisibility(View.VISIBLE);
                }
            });


            if (osType.equals(ConstantsUtil.OS_TYPE_DIRECCION)) {
                View osActualButton = mMainContainer.findViewById(R.id.os_actual_button);
                osActualButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard(v);
                        showOsDesregAlert();
                    }
                });
                osActualButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mSelectedActualOS = -1;
                        mOSActualEditText.setText("");
                        return true;
                    }
                });
            }


            mMesesImpagosEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    Log.e(TAG, "mMesesImpagosEditText " + s.toString());
                    // WORKFLOW  OS COBRANZAS
                    if (!s.toString().isEmpty() && Integer.valueOf(s.toString()) > 2) {
                        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.affiliation_afip_cant_month_value_error_title), getResources().getString(R.string.affiliation_afip_cant_month_value_error));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });


            // FILE BUTTONS
            addSSSButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = COMPROBANTE_SSS_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + COMPROBANTE_SSS_FILE_PREFIX, COMPROBANTE_SSS_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addAfipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = AFIP_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + AFIP_FILE_PREFIX, AFIP_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });


            if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {

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
            } else {
                // DIRECCION
                addEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFileType = EMAIL_FILE_PREFIX;
                        mImageProvider.showImagePicker(attachFilesSubDir + "/" + EMAIL_FILE_PREFIX, EMAIL_FILE_PREFIX + FileHelper.getFilePrefix());
                    }
                });

                addForm53Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFileType = FORM_53_FILE_PREFIX;
                        mImageProvider.showImagePicker(attachFilesSubDir + "/" + FORM_53_FILE_PREFIX, FORM_53_FILE_PREFIX + FileHelper.getFilePrefix());
                    }
                });

                addForm59Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFileType = FORM_59_FILE_PREFIX;
                        mImageProvider.showImagePicker(attachFilesSubDir + "/" + FORM_59_FILE_PREFIX, FORM_59_FILE_PREFIX + FileHelper.getFilePrefix());
                    }
                });

                addModelNoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFileType = MODEL_NOTE_FILE_PREFIX;
                        mImageProvider.showImagePicker(attachFilesSubDir + "/" + MODEL_NOTE_FILE_PREFIX, MODEL_NOTE_FILE_PREFIX + FileHelper.getFilePrefix());
                    }
                });
            }

            // ADAPTERS
            mSSSFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "sssFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = COMPROBANTE_SSS_FILE_PREFIX;
                    AttachFile sssFile =  mConyugeData.obraSocial.comprobantesSSSFiles.get(position);
                    if (sssFile.id != -1) {
                        removeFile(sssFile, position);
                    }
                }
            });
            mAfipFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "afipFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = AFIP_FILE_PREFIX;
                    AttachFile afipFile =  mConyugeData.obraSocial.comprobantesAfipFiles.get(position);
                    if (afipFile.id != -1) {
                        removeFile(afipFile, position);
                    }
                }
            });

            if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {

                mOptionChangeFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.e(TAG, "optionFile onItemDeleteClick()!!!!!:" + position);
                        attachFileType = OPTION_CHANGE_FILE_PREFIX;
                        AttachFile optionFile =  mConyugeData.obraSocial.optionChangeFiles.get(position);
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
                        AttachFile formFile =  mConyugeData.obraSocial.formFiles.get(position);
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
                        AttachFile certFile =  mConyugeData.obraSocial.certChangeFiles.get(position);
                        if (certFile.id != -1) {
                            removeFile(certFile, position);
                        }
                    }
                });
            } else {
                // DIRECCION
                mEmailFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.e(TAG, "emailFile onItemDeleteClick()!!!!!:" + position);
                        attachFileType = EMAIL_FILE_PREFIX;
                        AttachFile emailFile =  mConyugeData.obraSocial.emailFiles.get(position);
                        if (emailFile.id != -1) {
                            removeFile(emailFile, position);
                        }
                    }
                });
                mForm53FileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.e(TAG, "form53File onItemDeleteClick()!!!!!:" + position);
                        attachFileType = FORM_53_FILE_PREFIX;
                        AttachFile form53File =  mConyugeData.obraSocial.form53Files.get(position);
                        if (form53File.id != -1) {
                            removeFile(form53File, position);
                        }
                    }
                });
                mForm59FileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.e(TAG, "form53File onItemDeleteClick()!!!!!:" + position);
                        attachFileType = FORM_59_FILE_PREFIX;
                        AttachFile form59File =  mConyugeData.obraSocial.form59Files.get(position);
                        if (form59File.id != -1) {
                            removeFile(form59File, position);
                        }
                    }
                });
                mModelNoteFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.e(TAG, "noteFile onItemDeleteClick()!!!!!:" + position);
                        attachFileType = MODEL_NOTE_FILE_PREFIX;
                        AttachFile noteFile =  mConyugeData.obraSocial.modelNotesFiles.get(position);
                        if (noteFile.id != -1) {
                            removeFile(noteFile, position);
                        }
                    }
                });
            }
        }

        // ADAPTERS
        mSSSFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile sssFile =  mConyugeData.obraSocial.comprobantesSSSFiles.get(position);
                Log.e(TAG, "sssFile path:" + sssFile.filePath);
                loadFile(sssFile);
            }
        });
        mAfipFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile afipFile =  mConyugeData.obraSocial.comprobantesAfipFiles.get(position);
                Log.e(TAG, "afipFile path:" + afipFile.filePath);
                loadFile(afipFile);
            }
        });

        if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
            mOptionChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile optionFile =  mConyugeData.obraSocial.optionChangeFiles.get(position);
                    Log.e(TAG, "optionFile path:" + optionFile.filePath);
                    loadFile(optionFile);
                }
            });
            mFormFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile formFile =  mConyugeData.obraSocial.formFiles.get(position);
                    Log.e(TAG, "formFile path:" + formFile.filePath);
                    loadFile(formFile);
                }
            });
            mCertChangeFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile certFile =  mConyugeData.obraSocial.certChangeFiles.get(position);
                    Log.e(TAG, "certFile path:" + certFile.filePath);
                    loadFile(certFile);
                }
            });
        }else{
            // DIRECCION
            mEmailFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile emailFile =  mConyugeData.obraSocial.emailFiles.get(position);
                    Log.e(TAG, "formFile path:" + emailFile.filePath);
                    loadFile(emailFile);
                }
            });
            mForm53FileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile form53File =  mConyugeData.obraSocial.form53Files.get(position);
                    Log.e(TAG, "formFile path:" + form53File.filePath);
                    loadFile(form53File);
                }
            });

            mForm59FileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile form59File =  mConyugeData.obraSocial.form59Files.get(position);
                    Log.e(TAG, "formFile path:" + form59File.filePath);
                    loadFile(form59File);
                }
            });
            mModelNoteFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    AttachFile noteFile =  mConyugeData.obraSocial.modelNotesFiles.get(position);
                    attachFileType = MODEL_NOTE_FILE_PREFIX;
                    Log.e(TAG, "noteFile path:" + noteFile.filePath);
                    loadFile(noteFile);
                }
            });
        }
    }

    private boolean validateForm() {
        return true;
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
                            checkShowOSFields(mOSStates.get(mSelectedOSState), true);

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


    private void showOsDesregAlert() {

        ArrayList<String> osStr = new ArrayList<String>();
        for (QuoteOption q : mObrasSociales) {
            osStr.add(q.optionName());
        }

        mOSActualAlertAdapter = new SpinnerDropDownAdapter(getActivity(), osStr, mSelectedActualOS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mOSActualAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedActualOS = i;

                        if (i == 0){
                            mSelectedActualOS = -1;
                            mOSActualEditText.setText("");
                        }else {
                            mOSActualEditText.setText(mObrasSociales.get(i).optionName());

                            QuoteOption osState = null;
                            if (mSelectedOSState != -1) {
                                osState = mOSStates.get(mSelectedOSState);
                            }
                            checkShowFieldsForActualOS(mObrasSociales.get(mSelectedActualOS), osState, true);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mOSActualAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
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
