package ar.com.sancorsalud.asociados.activity.affiliation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AffiliationMembersAdapter;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DateUtils;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.ImageProviderListener;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.LoadResourceUriCallback;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;
import ar.com.sancorsalud.asociados.utils.StringHelper;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class AddAffiliationMemberActivity extends BaseActivity {

    private static final String TAG = "AFFIL_MEMBER_ACT";
    private static final String INIT_BIRTH_DAY = "1999-01-01";
    private static final String DNI_QRCODE_FILE_PREFIX = "dniQRCode";

    private static final String CERT_DISC_FILE_PREFIX = "certDisc";
    private static final String DNI_FRONT_FILE_PREFIX = "dniFront";
    private static final String DNI_BACK_FILE_PREFIX = "dniBack";
    private static final String CUIL_FILE_PREFIX = "accountCuil";
    private static final String ACTA_MATRIM_FILE_PREFIX = "actaMatrim";
    private static final String PART_NAC_FILE_PREFIX = "partNac";

    public static final int QRCODE_REQUEST = 100;

    private SimpleDateFormat mDateFormat;
    private ScrollView mScrollView;

    private Button mQrCodeButton;
    private ImageView photoImg;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mSexEditText;
    private EditText mDocTypeEditText;
    private EditText mDocNumberEditText;
    private EditText mBirthdayEditText;
    private EditText mCuitEditText;
    private EditText mNationalityEditText;
    private EditText mParentescoEditText;

    private ArrayList<QuoteOption> mSexos;
    private ArrayList<QuoteOption> mDocTypes;
    private ArrayList<QuoteOption> mNationalities;
    private ArrayList<QuoteOption> mParentescos;

    private SpinnerDropDownAdapter mSexoAlertAdapter;
    private int mSelectedSexo = -1;
    private SpinnerDropDownAdapter mDocTypeAlertAdapter;
    private int mSelectedDocType = -1;
    private SpinnerDropDownAdapter mNationalityAlertAdapter;
    private int mSelectedNationality = -1;
    private int mSelectedParentesco = -1;


    private long titularDNI;
    private boolean titularAportaMonotributo;
    private Member mMember;
    private int mIndex = -1;

    private LinearLayout mDniFathersBox;
    private RadioButton mYesPadresOnDniRadioButton;
    private RadioButton mNoPadresOnDniRadioButton;
    private RelativeLayout mCuilBox;
    private TextView mCuilAnsesLink;

    private RadioButton mYesDisabilityRadioButton;
    private RadioButton mNoDisabilityRadioButton;

    // CERT DISCAPACIDAD  File
    private RecyclerView mCertificadoDiscapacidadRecyclerView;
    private AttachFilesAdapter mCertificadoDiscapacidadFileAdapter;
    private Button addCertificadoDiscapacidadButton;
    private View mCertificadoDiscapacidadContainerView;

    // DNI FRONT  File
    private RecyclerView mDNIiFrontRecyclerView;
    private AttachFilesAdapter mDNIFrontFileAdapter;
    private Button addDNIFrontButton;

    // DNI BACK  File
    private RecyclerView mDNIiBackRecyclerView;
    private AttachFilesAdapter mDNIBackFileAdapter;
    private Button addDNIBackButton;

    // CUIL FILES
    private RadioButton mYesCuilDorsoRadioButton;
    private RadioButton mNoCuilDorsoRadioButton;

    private RecyclerView mCuilRecyclerView;
    private AttachFilesAdapter mCuilFileAdapter;
    private Button addCuilButton;

    // PART NAC File
    private RelativeLayout mPartNacimBox;
    private RecyclerView mPartidaNacRecyclerView;
    private AttachFilesAdapter mPartidaNacFileAdapter;
    private Button addPartidaNacButton;

    // ACTA MATRIM
    private RelativeLayout mAltaMatrimBox;
    private RecyclerView mActaMatrimRecyclerView;
    private AttachFilesAdapter mActaMatrimFileAdapter;
    private Button addActaMatrimButton;

    private LinearLayout mAportesBox;
    private RadioButton mSiAportesRadioButton;
    private RadioButton mNoAportesRadioButton;

    public List<AttachFile> tmpCertDiscapacidadFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpDNIFrontFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpDNIBackFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpCuilFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpPartNacimientoiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpActaMatrimonioFiles = new ArrayList<AttachFile>();

    private  View editButton;
    private boolean editableCard = true;

    private int scan = 0;
    private int QR_SCAN = 1;

    private String ansesLink;

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "BirthDate: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                mBirthdayEditText.setText(mDateFormat.format(date));
                checkShowAgeAlert(date);
            } catch (Exception e) {
                mBirthdayEditText.setText(mDateFormat.format(today));
            }
            mBirthdayEditText.requestFocus();

        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_affiliation_member);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.add_member_title);

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mMainContainer = findViewById(R.id.content_add_member);

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mQrCodeButton = (Button) findViewById(R.id.qrcode_button);
        photoImg = (ImageView) findViewById(R.id.photo_button);

        mFirstNameEditText = (EditText) findViewById(R.id.firstname_input);
        setTypeTextNoSuggestions(mFirstNameEditText);

        mLastNameEditText = (EditText) findViewById(R.id.lastname_input);
        setTypeTextNoSuggestions(mLastNameEditText);

        mDocTypeEditText = (EditText) findViewById(R.id.doc_type_input);
        setTypeTextNoSuggestions(mDocTypeEditText);

        mDocNumberEditText = (EditText) findViewById(R.id.doc_number_input);

        mCuitEditText = (EditText) findViewById(R.id.cuit_input);

        mSexEditText = (EditText) findViewById(R.id.sex_input);
        setTypeTextNoSuggestions(mSexEditText);

        mBirthdayEditText = (EditText) findViewById(R.id.birthday_input);

        mNationalityEditText = (EditText) findViewById(R.id.nationality_input);
        setTypeTextNoSuggestions(mNationalityEditText);

        mParentescoEditText = (EditText) findViewById(R.id.parentesco_input);
        setTypeTextNoSuggestions(mParentescoEditText);

        mDniFathersBox = (LinearLayout) findViewById(R.id.dni_father_box);
        mYesPadresOnDniRadioButton = (RadioButton) findViewById(R.id.dni_father_yes_button);
        mNoPadresOnDniRadioButton = (RadioButton) findViewById(R.id.dni_father_no_button);

        mYesDisabilityRadioButton = (RadioButton) findViewById(R.id.yes_disability_button);
        mNoDisabilityRadioButton = (RadioButton) findViewById(R.id.no_disability_button);

        // CUIL LOGIC
        mCuilBox = (RelativeLayout) findViewById(R.id.cuil_box);
        mYesCuilDorsoRadioButton = (RadioButton) findViewById(R.id.yes_cuil_dorso_button);
        mNoCuilDorsoRadioButton = (RadioButton) findViewById(R.id.no_cuil_dorso_button);
        mCuilAnsesLink = (TextView) findViewById(R.id.cuil_anses_link);

        mAportesBox = (LinearLayout) mMainContainer.findViewById(R.id.aportes_box);
        mSiAportesRadioButton = (RadioButton) mMainContainer.findViewById(R.id.aportes_yes_button);
        mNoAportesRadioButton = (RadioButton) mMainContainer.findViewById(R.id.aportes_no_button);

        editButton = findViewById(R.id.edit_button);
        fillArraysData();

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        if (getIntent().getExtras() != null) {

            titularDNI = getIntent().getLongExtra(ConstantsUtil.AFFILIATION_TITULAR_DNI, -1L);
            titularAportaMonotributo = getIntent().getBooleanExtra(ConstantsUtil.AFFILIATION_TITULAR_APORTA_MONO, false);
            mMember = (Member) getIntent().getSerializableExtra(ConstantsUtil.AFFILIATION_MEMBER);
            mIndex = getIntent().getIntExtra(ConstantsUtil.AFFILIATION_MEMBER_INDEX, -1);

            if (mMember != null && titularDNI != -1L) {
                Log.e(TAG, "Ok gettin member.....");
                Log.e(TAG, "Titular Aporta mono: " + titularAportaMonotributo );

                attachFilesSubDir = titularDNI + "/member/" + mIndex + "/affiliation/docs";
                mImageProvider = new ImageProvider(this);

                fillAllPhysicalFiles();
            }
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsHelper.REQUEST_CAMERA_PERMISSION && scan == QR_SCAN) {
            // reset falg
            scan = 0;
            captureQRCode();
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == QRCODE_REQUEST) {
            String message = data.getStringExtra(ConstantsUtil.QRCODE_DATA);
            Log.e(TAG, "Result:" + message);
            if (message != null && !message.isEmpty()) {
                qrCodeCaptured(message);
            }
        }
    }

    @Override
    public void updateFileList(final AttachFile attachFile) {

        if (attachFileType.equals(DNI_QRCODE_FILE_PREFIX)) {
            showProgressDialog(R.string.decode_image_file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    decodeQRCode(attachFile);
                }
            }).start();
        }

        else if (attachFileType.equals(CERT_DISC_FILE_PREFIX)) {
            mCertificadoDiscapacidadFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(DNI_FRONT_FILE_PREFIX)) {
            mDNIFrontFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(DNI_BACK_FILE_PREFIX)) {
            mDNIBackFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CUIL_FILE_PREFIX)) {
            mCuilFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(PART_NAC_FILE_PREFIX)) {
            mPartidaNacFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(ACTA_MATRIM_FILE_PREFIX)) {
            mActaMatrimFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position) {

        if (attachFileType.equals(CERT_DISC_FILE_PREFIX)) {
            mCertificadoDiscapacidadFileAdapter.removeItem(position);
        } else if (attachFileType.equals(DNI_FRONT_FILE_PREFIX)) {
            mDNIFrontFileAdapter.removeItem(position);
        } else if (attachFileType.equals(DNI_BACK_FILE_PREFIX)) {
            mDNIBackFileAdapter.removeItem(position);
        } else if (attachFileType.equals(CUIL_FILE_PREFIX)) {
            mCuilFileAdapter.removeItem(position);
        } else if (attachFileType.equals(ACTA_MATRIM_FILE_PREFIX)) {
            mActaMatrimFileAdapter.removeItem(position);
        } else if (attachFileType.equals(PART_NAC_FILE_PREFIX)) {
            mPartidaNacFileAdapter.removeItem(position);
        }
    }

    @Override
    public void onDecodedFile(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                if (text != null){
                    qrCodeCaptured(text);
                }else{
                    SnackBarHelper.makeError(mMainContainer, R.string.error_decoding_file).show();
                }
            }
        });
    }

    private void fillArraysData() {

        mParentescos = QuoteOptionsController.getInstance().getParentescos();

        mSexos = new ArrayList<>();
        QuoteOption sexoSelection = new QuoteOption("-1", getResources().getString(R.string.field_sex));
        mSexos.add(sexoSelection);
        mSexos.addAll(QuoteOptionsController.getInstance().getSexos());

        mDocTypes = new ArrayList<>();
        QuoteOption docTypeSelection = new QuoteOption("-1", getResources().getString(R.string.field_docType));
        mDocTypes.add(docTypeSelection);
        mDocTypes.addAll(QuoteOptionsController.getInstance().getDocTypes());

        mNationalities = new ArrayList<>();
        QuoteOption nationSelection = new QuoteOption("-1", getResources().getString(R.string.field_nationality));
        mNationalities.add(nationSelection);
        mNationalities.addAll(QuoteOptionsController.getInstance().getNationalities());
    }

    private void fillAllPhysicalFiles() {
        showProgressDialog(R.string.affiliation_member_loading);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll Member PhysicalFiles ....");
                fillCertDiscapacidadFiles();
            }
        }).start();
    }

    private void fillCertDiscapacidadFiles() {

        if (!mMember.certDiscapacidadFiles.isEmpty()) {

            Log.e(TAG, "fillCERTDISCFiles !!....");

            final AttachFile certDiscFile = mMember.certDiscapacidadFiles.remove(0);
            if (certDiscFile.fileNameAndExtension == null || certDiscFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + CERT_DISC_FILE_PREFIX, certDiscFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Member certDisc File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCertDiscapacidadFiles.add(resultFile);
                            fillCertDiscapacidadFiles();

                        } else {
                            Log.e(TAG, "Null Member certDisc File ");
                            tmpCertDiscapacidadFiles.add(certDiscFile);
                            fillCertDiscapacidadFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpCertDiscapacidadFiles.add(certDiscFile);
                        fillCertDiscapacidadFiles();
                    }
                });
            } else {
                tmpCertDiscapacidadFiles.add(certDiscFile);
                fillCertDiscapacidadFiles();
            }

        } else {
            mMember.certDiscapacidadFiles.addAll(tmpCertDiscapacidadFiles);
            tmpCertDiscapacidadFiles = new ArrayList<AttachFile>();
            fillDNIFrontFiles();
        }
    }

    private void fillDNIFrontFiles() {

        if (!mMember.dniFrontFiles.isEmpty()) {

            Log.e(TAG, "fillDNIFRONTFiles !!....");

            final AttachFile dniFrontFile = mMember.dniFrontFiles.remove(0);
            if (dniFrontFile.fileNameAndExtension == null || dniFrontFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + DNI_FRONT_FILE_PREFIX, dniFrontFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Member DNI Front File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpDNIFrontFiles.add(resultFile);
                            fillDNIFrontFiles();

                        } else {
                            Log.e(TAG, "Null Member DNI Front File.");
                            tmpDNIFrontFiles.add(dniFrontFile);
                            fillDNIFrontFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpDNIFrontFiles.add(dniFrontFile);
                        fillDNIFrontFiles();
                    }
                });
            } else {
                tmpDNIFrontFiles.add(dniFrontFile);
                fillDNIFrontFiles();
            }

        } else {
            mMember.dniFrontFiles.addAll(tmpDNIFrontFiles);
            tmpDNIFrontFiles = new ArrayList<AttachFile>();
            fillDNIBackFiles();
        }
    }

    private void fillDNIBackFiles() {

        if (!mMember.dniBackFiles.isEmpty()) {

            Log.e(TAG, "fillBackFRONTFiles !!....");
            final AttachFile dniBackFile = mMember.dniBackFiles.remove(0);
            if (dniBackFile.fileNameAndExtension == null || dniBackFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + DNI_BACK_FILE_PREFIX, dniBackFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        dismissProgressDialog();

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Member Back File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpDNIBackFiles.add(resultFile);
                            fillDNIBackFiles();

                        } else {
                            Log.e(TAG, "NULL Member DNI Back File ....");
                            tmpDNIBackFiles.add(dniBackFile);
                            fillDNIBackFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpDNIBackFiles.add(dniBackFile);
                        fillDNIBackFiles();
                    }
                });
            } else {
                tmpDNIBackFiles.add(dniBackFile);
                fillDNIBackFiles();
            }
        } else {
            mMember.dniBackFiles.addAll(tmpDNIBackFiles);
            tmpDNIBackFiles = new ArrayList<AttachFile>();
            fillCuilFiles();
        }
    }


    private void fillCuilFiles() {

        if (!mMember.cuilFiles.isEmpty()) {

            Log.e(TAG, "fillCuilFiles !!....");
            final AttachFile cuilFile = mMember.cuilFiles.remove(0);
            if (cuilFile.fileNameAndExtension == null || cuilFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + CUIL_FILE_PREFIX, cuilFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Cuil File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCuilFiles.add(resultFile);
                            fillCuilFiles();

                        } else {
                            Log.e(TAG, "Null Cuil file ....");
                            tmpCuilFiles.add(cuilFile);
                            fillCuilFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpCuilFiles.add(cuilFile);
                        fillCuilFiles();
                    }
                });
            } else {
                tmpCuilFiles.add(cuilFile);
                fillCuilFiles();
            }
        } else {
            mMember.cuilFiles.addAll(tmpCuilFiles);
            tmpCuilFiles = new ArrayList<AttachFile>();
            fillPartNacimientoFiles();
        }
    }

    private void fillPartNacimientoFiles() {

        if (!mMember.partidaNacimientoFiles.isEmpty()) {
            Log.e(TAG, "partidaNacimientoFiles !!....");
            final AttachFile partNacFile = mMember.partidaNacimientoFiles.remove(0);
            if (partNacFile.fileNameAndExtension == null || partNacFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + PART_NAC_FILE_PREFIX, partNacFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        dismissProgressDialog();

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Member partNacFile File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpPartNacimientoiles.add(resultFile);
                            fillPartNacimientoFiles();

                        } else {
                            Log.e(TAG, "Null member partNac file ....");
                            tmpPartNacimientoiles.add(partNacFile);
                            fillPartNacimientoFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpPartNacimientoiles.add(partNacFile);
                        fillPartNacimientoFiles();
                    }
                });
            } else {
                tmpPartNacimientoiles.add(partNacFile);
                fillPartNacimientoFiles();
            }
        } else {
            mMember.partidaNacimientoFiles.addAll(tmpPartNacimientoiles);
            tmpPartNacimientoiles = new ArrayList<AttachFile>();
            fillActaMatrimonioFiles();
        }
    }


    private void fillActaMatrimonioFiles() {

        if (!mMember.actaMatrimonioFiles.isEmpty()) {

            Log.e(TAG, "actaMatrimonioFiles !!....");
            final AttachFile actaMatrimFile = mMember.actaMatrimonioFiles.remove(0);
            if (actaMatrimFile.fileNameAndExtension == null || actaMatrimFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getApplicationContext(), attachFilesSubDir + "/" + ACTA_MATRIM_FILE_PREFIX, actaMatrimFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        dismissProgressDialog();
                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Member actaMatrimFile File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpActaMatrimonioFiles.add(resultFile);
                            fillActaMatrimonioFiles();

                        } else {
                            Log.e(TAG, "Null Member actaMatrimFile File ....");
                            tmpActaMatrimonioFiles.add(actaMatrimFile);
                            fillActaMatrimonioFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpActaMatrimonioFiles.add(actaMatrimFile);
                        fillActaMatrimonioFiles();
                    }
                });
            } else {
                tmpActaMatrimonioFiles.add(actaMatrimFile);
                fillActaMatrimonioFiles();
            }
        } else {
            mMember.actaMatrimonioFiles.addAll(tmpActaMatrimonioFiles);
            tmpActaMatrimonioFiles = new ArrayList<AttachFile>();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initialize();
                }
            });
        }
    }

    private void initialize() {
        initializeForm();
        setupListeners();
    }

    private void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);

        initCertificadoDiscapacidadFile();
        initDNIFrontFile();
        initDNIBackFile();
        initCuilFile();
        initPartNacimientoFile();
        initActaMatrimonioFile();
        initCuilFile();

        if (mMember == null)
            return;

        if (mMember.firstname != null && !mMember.firstname.isEmpty())
            mFirstNameEditText.setText(mMember.firstname);

        if (mMember.lastname != null && !mMember.lastname.isEmpty())
            mLastNameEditText.setText(mMember.lastname);

        if (mMember.sex != null) {
            mSelectedSexo = mSexos.indexOf(mMember.sex);

            if (mSelectedSexo != -1)
                mSexEditText.setText(mMember.sex.title);
        }

        if (mMember.docType != null) {
            mSelectedDocType = mDocTypes.indexOf(mMember.docType);

            if (mSelectedDocType != -1)
                mDocTypeEditText.setText(mMember.docType.title);
        }

        if (mMember.dni != -1L)
            mDocNumberEditText.setText("" + mMember.dni);

        if (mMember.nationality != null) {
            mSelectedNationality = mNationalities.indexOf(mMember.nationality);

            if (mSelectedNationality != -1) {
                mNationalityEditText.setText(mMember.nationality.title);
            } else {
                // preload Argentino
                QuoteOption nationalityDefault = new QuoteOption(ConstantsUtil.ARG_NATIONALITY_ID, QuoteOptionsController.getInstance().getNationalityName(ConstantsUtil.ARG_NATIONALITY_ID));
                mNationalityEditText.setText(nationalityDefault.title);
            }
        } else {
            // preload Argentino
            QuoteOption nationalityDefault = new QuoteOption(ConstantsUtil.ARG_NATIONALITY_ID, QuoteOptionsController.getInstance().getNationalityName(ConstantsUtil.ARG_NATIONALITY_ID));
            mNationalityEditText.setText(nationalityDefault.title);
        }

        if (mMember.parentesco != null) {
            mSelectedParentesco = mParentescos.indexOf(mMember.parentesco);

            if (mSelectedParentesco != -1)
                mParentescoEditText.setText(mMember.parentesco.title);
        }

        if (mMember.birthday != null)
            mBirthdayEditText.setText(mMember.getBirthday());
        //else
        //  mBirthdayEditText.setText(INIT_BIRTH_DAY);


        if (mMember.cuil != null && !mMember.cuil.isEmpty())
            mCuitEditText.setText("" + mMember.cuil);

        if (mMember.hasDisability != null && mMember.hasDisability) {
            mYesDisabilityRadioButton.setChecked(true);
            mNoDisabilityRadioButton.setChecked(false);
            mCertificadoDiscapacidadContainerView.setVisibility(View.VISIBLE);
        } else {
            mNoDisabilityRadioButton.setChecked(true);
            mYesDisabilityRadioButton.setChecked(false);
            mCertificadoDiscapacidadContainerView.setVisibility(View.GONE);
        }

        if (mMember.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || mMember.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER)) {
            mDniFathersBox.setVisibility(View.GONE);
        } else {
            mDniFathersBox.setVisibility(View.VISIBLE);
        }

        if (mMember.cuilFiles.isEmpty()) {
            mYesCuilDorsoRadioButton.setChecked(true);
            mNoCuilDorsoRadioButton.setChecked(false);
            mCuilBox.setVisibility(View.GONE);
            mCuilAnsesLink.setVisibility(View.GONE);
        } else {
            mYesCuilDorsoRadioButton.setChecked(false);
            mNoCuilDorsoRadioButton.setChecked(true);
            mCuilBox.setVisibility(View.VISIBLE);
            mCuilAnsesLink.setVisibility(View.VISIBLE);
        }

        if (titularAportaMonotributo){
            mAportesBox.setVisibility(View.VISIBLE);

           if (mMember.aportaMonotributo){
               mSiAportesRadioButton.setChecked(true);
               mNoAportesRadioButton.setChecked(false);
           }else{
               mSiAportesRadioButton.setChecked(false);
               mNoAportesRadioButton.setChecked(true);
           }
        }else{
            mAportesBox.setVisibility(View.GONE);
        }

        checkEditableCardMode();
    }

    private void initCertificadoDiscapacidadFile() {
        mCertificadoDiscapacidadFileAdapter = new AttachFilesAdapter(mMember.certDiscapacidadFiles, true);

        mCertificadoDiscapacidadRecyclerView = (RecyclerView) findViewById(R.id.cert_discapacidad_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCertificadoDiscapacidadRecyclerView.getContext());
        mCertificadoDiscapacidadRecyclerView.setLayoutManager(attachLayoutManager);
        mCertificadoDiscapacidadRecyclerView.setAdapter(mCertificadoDiscapacidadFileAdapter);
        mCertificadoDiscapacidadRecyclerView.setHasFixedSize(true);
        addCertificadoDiscapacidadButton = (Button) findViewById(R.id.cert_discapacidad_button);
        mCertificadoDiscapacidadContainerView = findViewById(R.id.cert_discapacidad_box);
    }

    private void initDNIFrontFile() {
        mDNIFrontFileAdapter = new AttachFilesAdapter(mMember.dniFrontFiles, true);

        mDNIiFrontRecyclerView = (RecyclerView) findViewById(R.id.dni_front_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mDNIiFrontRecyclerView.getContext());
        mDNIiFrontRecyclerView.setLayoutManager(attachLayoutManager);
        mDNIiFrontRecyclerView.setAdapter(mDNIFrontFileAdapter);
        mDNIiFrontRecyclerView.setHasFixedSize(true);
        addDNIFrontButton = (Button) findViewById(R.id.dni_front_button);

    }

    private void initDNIBackFile() {
        mDNIBackFileAdapter = new AttachFilesAdapter(mMember.dniBackFiles, true);

        mDNIiBackRecyclerView = (RecyclerView) findViewById(R.id.dni_back_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mDNIiBackRecyclerView.getContext());
        mDNIiBackRecyclerView.setLayoutManager(attachLayoutManager);
        mDNIiBackRecyclerView.setAdapter(mDNIBackFileAdapter);
        mDNIiBackRecyclerView.setHasFixedSize(true);
        addDNIBackButton = (Button) findViewById(R.id.dni_back_button);
    }


    private void initCuilFile() {
        mCuilFileAdapter = new AttachFilesAdapter(mMember.cuilFiles, true);

        mCuilRecyclerView = (RecyclerView) findViewById(R.id.cuil_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCuilRecyclerView.getContext());
        mCuilRecyclerView.setLayoutManager(attachLayoutManager);
        mCuilRecyclerView.setAdapter(mCuilFileAdapter);
        mCuilRecyclerView.setHasFixedSize(true);
        addCuilButton = (Button) findViewById(R.id.cuil_button);
    }

    private void initPartNacimientoFile() {
        mPartidaNacFileAdapter = new AttachFilesAdapter(mMember.partidaNacimientoFiles, true);

        mPartidaNacRecyclerView = (RecyclerView) findViewById(R.id.part_nac_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mPartidaNacRecyclerView.getContext());
        mPartidaNacRecyclerView.setLayoutManager(attachLayoutManager);
        mPartidaNacRecyclerView.setAdapter(mPartidaNacFileAdapter);
        mPartidaNacRecyclerView.setHasFixedSize(true);
        addPartidaNacButton = (Button) findViewById(R.id.part_nac_button);


        mPartNacimBox = (RelativeLayout) findViewById(R.id.part_nac_box);
        if (mMember.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER)) {
            mPartNacimBox.setVisibility(View.GONE);
        } else {
            // NO preloading data
            mPartNacimBox.setVisibility(View.VISIBLE);
        }
    }

    private void initActaMatrimonioFile() {
        mActaMatrimFileAdapter = new AttachFilesAdapter(mMember.actaMatrimonioFiles, true);

        mActaMatrimRecyclerView = (RecyclerView) findViewById(R.id.acta_matrim_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mActaMatrimRecyclerView.getContext());
        mActaMatrimRecyclerView.setLayoutManager(attachLayoutManager);
        mActaMatrimRecyclerView.setAdapter(mActaMatrimFileAdapter);
        mActaMatrimRecyclerView.setHasFixedSize(true);
        addActaMatrimButton = (Button) findViewById(R.id.acta_matrim_button);

        mAltaMatrimBox = (RelativeLayout) findViewById(R.id.alta_matrim_box);
        if (mMember.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER)) {
            mAltaMatrimBox.setVisibility(View.VISIBLE);
        } else {
            mAltaMatrimBox.setVisibility(View.GONE);
        }
    }


    private void checkEditableCardMode() {

        if (editableCard){
            mQrCodeButton.setVisibility(View.VISIBLE);
            photoImg.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }else{
            mQrCodeButton.setVisibility(View.GONE);
            photoImg.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }

        mFirstNameEditText.setFocusable(editableCard);
        mLastNameEditText.setFocusable(editableCard);
        mDocNumberEditText.setFocusable(editableCard);
        mCuitEditText.setFocusable(editableCard);

        mYesDisabilityRadioButton.setEnabled(editableCard);
        mNoDisabilityRadioButton.setEnabled(editableCard);

        mYesCuilDorsoRadioButton.setEnabled(editableCard);
        mNoCuilDorsoRadioButton.setEnabled(editableCard);

        mYesPadresOnDniRadioButton.setEnabled(editableCard);
        mNoPadresOnDniRadioButton.setEnabled(editableCard);

        if (!editableCard) {
            disableView(addDNIFrontButton);
            disableView(addDNIBackButton);
            disableView(addCuilButton);
            disableView(addPartidaNacButton);
            disableView(addActaMatrimButton);
        }
    }


    private void updateMember() {

        if (!mFirstNameEditText.getText().toString().trim().isEmpty()) {
            mMember.firstname = mFirstNameEditText.getText().toString().trim();
        }

        if (!mLastNameEditText.getText().toString().trim().isEmpty()) {
            mMember.lastname = mLastNameEditText.getText().toString().trim();
        }

        if (mSelectedSexo != -1) {
            mMember.sex = mSexos.get(mSelectedSexo);
        } else {
            mMember.sex = null;
        }

        if (mSelectedDocType != -1) {
            mMember.docType = mDocTypes.get(mSelectedDocType);
        } else {
            mMember.docType = null;
        }

        if (mSelectedNationality != -1) {
            mMember.nationality = mNationalities.get(mSelectedNationality);
        } else {
            mMember.nationality = null;
        }

        if (mSelectedParentesco != -1) {
            mMember.parentesco = mParentescos.get(mSelectedParentesco);
        } else {
            mMember.parentesco = null;
        }

        try {
            mMember.birthday = ParserUtils.parseDate(mBirthdayEditText.getText().toString(), DATE_FORMAT);
        } catch (Exception e) {
        }

        if (!mCuitEditText.getText().toString().trim().isEmpty()) {
            mMember.cuil = mCuitEditText.getText().toString().trim();
        }

        if (!mDocNumberEditText.getText().toString().trim().isEmpty()) {
            mMember.dni = Long.parseLong(mDocNumberEditText.getText().toString().trim());
        } else {
            mMember.dni = -1;
        }

        if (mYesDisabilityRadioButton.isChecked()) {
            mMember.hasDisability = true;
            if (mCertificadoDiscapacidadFileAdapter != null) {
                mMember.certDiscapacidadFiles = mCertificadoDiscapacidadFileAdapter.getAttachFiles();
            }
        } else {
            mMember.hasDisability = false;
        }

        if (mDNIFrontFileAdapter != null) {
            mMember.dniFrontFiles = mDNIFrontFileAdapter.getAttachFiles();
        }
        if (mDNIBackFileAdapter != null) {
            mMember.dniBackFiles = mDNIBackFileAdapter.getAttachFiles();
        }
        if (mPartidaNacFileAdapter != null) {
            mMember.partidaNacimientoFiles = mPartidaNacFileAdapter.getAttachFiles();
        }
        if (mActaMatrimFileAdapter != null) {
            mMember.actaMatrimonioFiles = mActaMatrimFileAdapter.getAttachFiles();
        }

        if (mYesCuilDorsoRadioButton.isChecked() && mCuilFileAdapter != null ) {
            mMember.cuilFiles = mCuilFileAdapter.getAttachFiles();
        }

        if (titularAportaMonotributo) {
            if (mSiAportesRadioButton.isChecked()) {
                mMember.aportaMonotributo = true;
            }else{
                mMember.aportaMonotributo = false;
            }
        }
    }

    private void setupListeners() {

        setupImageProvider();

        if (editableCard) {
            mQrCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scan = QR_SCAN;
                    captureQRCode();
                }
            });


            photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "take photo....");
                    setHasToAdddFile(false);
                    attachFileType = DNI_QRCODE_FILE_PREFIX;
                    mImageProvider.detectQRCodeFromFile(attachFilesSubDir + "/" + DNI_QRCODE_FILE_PREFIX, DNI_QRCODE_FILE_PREFIX + FileHelper.getFilePrefix());

                }
            });

            View birhtDayButton = findViewById(R.id.birthday_button);
            birhtDayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar();
                }
            });


            View sexButton = findViewById(R.id.sex_button);
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


            View doctTypeButton = findViewById(R.id.doc_type_button);
            doctTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
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

            View nationalityButton = findViewById(R.id.nationality_button);
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

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateForm()) {
                        updateMember();

                        Intent i = new Intent();
                        i.putExtra(ConstantsUtil.RESULT_MEMBER, mMember);
                        setResult(Activity.RESULT_OK, i);
                        finish();
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

            // CUIL BOX
            mYesCuilDorsoRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCuilBox.setVisibility(View.GONE);
                    mCuilAnsesLink.setVisibility(View.GONE);
                }
            });

            mNoCuilDorsoRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCuilBox.setVisibility(View.VISIBLE);
                    mCuilAnsesLink.setVisibility(View.VISIBLE);
                }
            });

            mCuilAnsesLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "mCuilAnsesLink clik .....");
                    //IntentHelper.goToLinkDetailActivity(AddAffiliationMemberActivity.this, getResources().getString(R.string.activity_anses), ConstantsUtil.ANSES_CUIL_LINK);

                    if (ansesLink == null  || ansesLink.isEmpty()){
                        showProgressDialog(R.string.downloading_link);

                        CardController.getInstance().getLinkRequest(ConstantsUtil.LINK_ID_ANSES_CUIL, new Response.Listener<LinkData>() {
                            @Override
                            public void onResponse(LinkData responseLink) {
                                dismissProgressDialog();

                                if (responseLink != null) {
                                    ansesLink = responseLink.link;
                                    IntentHelper.goToLinkDetailActivity(AddAffiliationMemberActivity.this, getResources().getString(R.string.activity_anses), ansesLink);
                                } else {
                                    DialogHelper.showMessage(AddAffiliationMemberActivity.this, getResources().getString(R.string.error_downloading_link));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissProgressDialog();

                                Log.e(TAG, "Error donwloading link: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                DialogHelper.showMessage(AddAffiliationMemberActivity.this, getResources().getString(R.string.error_downloading_link), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                            }
                        });
                    }else{
                        IntentHelper.goToLinkDetailActivity(AddAffiliationMemberActivity.this, getResources().getString(R.string.activity_anses), ansesLink);
                    }
                }
            });

            // buttons
            addCertificadoDiscapacidadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHasToAdddFile(true);
                    attachFileType = CERT_DISC_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CERT_DISC_FILE_PREFIX, CERT_DISC_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addDNIFrontButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHasToAdddFile(true);
                    attachFileType = DNI_FRONT_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + DNI_FRONT_FILE_PREFIX, DNI_FRONT_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addDNIBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHasToAdddFile(true);
                    attachFileType = DNI_BACK_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + DNI_BACK_FILE_PREFIX, DNI_BACK_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addCuilButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CUIL_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CUIL_FILE_PREFIX, CUIL_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addActaMatrimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHasToAdddFile(true);
                    attachFileType = ACTA_MATRIM_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + ACTA_MATRIM_FILE_PREFIX, ACTA_MATRIM_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addPartidaNacButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHasToAdddFile(true);
                    attachFileType = PART_NAC_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + PART_NAC_FILE_PREFIX, PART_NAC_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });


            // ADAPTERS
            mCertificadoDiscapacidadFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "dniFrontFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CERT_DISC_FILE_PREFIX;
                    AttachFile certDiscFile = mMember.certDiscapacidadFiles.get(position);
                    if (certDiscFile.id != -1) {
                        removeFile(certDiscFile, position);
                    }
                }
            });
            mDNIFrontFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "dniFrontFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = DNI_FRONT_FILE_PREFIX;
                    AttachFile dniFrontFile = mMember.dniFrontFiles.get(position);
                    if (dniFrontFile.id != -1) {
                        removeFile(dniFrontFile, position);
                    }
                }
            });
            mDNIBackFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "dniBackFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = DNI_BACK_FILE_PREFIX;
                    AttachFile dniBackFile = mMember.dniBackFiles.get(position);
                    if (dniBackFile.id != -1) {
                        removeFile(dniBackFile, position);
                    }
                }
            });
            mCuilFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "cuilFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CUIL_FILE_PREFIX;
                    AttachFile cuilFile = mMember.cuilFiles.get(position);
                    if (cuilFile.id != -1) {
                        removeFile(cuilFile, position);
                    }
                }
            });
            mActaMatrimFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "actaMatrimFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = ACTA_MATRIM_FILE_PREFIX;
                    AttachFile actaMatrimFile = mMember.actaMatrimonioFiles.get(position);
                    if (actaMatrimFile.id != -1) {
                        removeFile(actaMatrimFile, position);
                    }
                }
            });
            mPartidaNacFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "partNacFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = PART_NAC_FILE_PREFIX;
                    AttachFile partNacFile = mMember.partidaNacimientoFiles.get(position);
                    if (partNacFile.id != -1) {
                        removeFile(partNacFile, position);
                    }
                }
            });

            mYesPadresOnDniRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPartNacimBox.setVisibility(View.GONE);
                    //mCuilBox.setVisibility(View.GONE);
                    //mCuilAnsesLink.setVisibility(View.GONE);
                }
            });

            mNoPadresOnDniRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPartNacimBox.setVisibility(View.VISIBLE);
                    //mCuilBox.setVisibility(View.VISIBLE);
                    //mCuilAnsesLink.setVisibility(View.VISIBLE);
                }
            });

            mNoDisabilityRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mCertificadoDiscapacidadContainerView.setVisibility(View.GONE);
                }
            });

            mYesDisabilityRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mCertificadoDiscapacidadContainerView.setVisibility(View.VISIBLE);
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
        }
        // ADAPTERS
        mCertificadoDiscapacidadFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile certFile = mMember.certDiscapacidadFiles.get(position);
                Log.e(TAG, "dniFrontFile path:" + certFile.filePath);
                loadFile(certFile);
            }
        });
        mDNIFrontFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile dniFrontFile = mMember.dniFrontFiles.get(position);
                Log.e(TAG, "dniFrontFile path:" + dniFrontFile.filePath);
                loadFile(dniFrontFile);
            }
        });
        mDNIBackFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile dniBackFile = mMember.dniBackFiles.get(position);
                Log.e(TAG, "dniBackFile path:" + dniBackFile.filePath);
                loadFile(dniBackFile);
            }
        });
        mCuilFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile cuilFile = mMember.cuilFiles.get(position);
                Log.e(TAG, "cuilFile path:" + cuilFile.filePath);
                loadFile(cuilFile);
            }
        });
        mActaMatrimFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile actaMatrimFile = mMember.actaMatrimonioFiles.get(position);
                Log.e(TAG, "actaMatrimFile path:" + actaMatrimFile.filePath);
                loadFile(actaMatrimFile);
            }
        });
        mPartidaNacFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile partNacFile = mMember.partidaNacimientoFiles.get(position);
                Log.e(TAG, "partNacFile path:" + partNacFile.filePath);
                loadFile(partNacFile);
            }
        });
    }

    private void setError(EditText editText, TextInputLayout input) {
        input.setErrorEnabled(true);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    public boolean validateForm() {
        return true;
    }

    private void showSexAlert() {
        ArrayList<String> sexStr = new ArrayList<String>();
        for (QuoteOption q : mSexos) {
            sexStr.add(q.optionName());
        }

        mSexoAlertAdapter = new SpinnerDropDownAdapter(this, sexStr, mSelectedSexo);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(mSexoAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedSexo = i;
                        if (i == 0){
                            mSelectedSexo = -1;
                            mSexEditText.setText("");
                        }else {
                            mSexEditText.setText(mSexos.get(i).optionName());
                            AddAffiliationMemberActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    mSexoAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDocTypesAlert() {
        ArrayList<String> docTypeStr = new ArrayList<String>();
        for (QuoteOption q : mDocTypes) {
            docTypeStr.add(q.optionName());
        }

        mDocTypeAlertAdapter = new SpinnerDropDownAdapter(this, docTypeStr, mSelectedDocType);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(mDocTypeAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedDocType = i;
                        if (i == 0){
                            mSelectedDocType = -1;
                            mDocTypeEditText.setText("");
                        }else {
                            mDocTypeEditText.setText(mDocTypes.get(i).optionName());
                            AddAffiliationMemberActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    mDocTypeAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showNationalitiesAlert() {
        ArrayList<String> nationStr = new ArrayList<String>();

        for (QuoteOption q : mNationalities) {
            nationStr.add(q.optionName());
        }

        mNationalityAlertAdapter = new SpinnerDropDownAdapter(this, nationStr, mSelectedNationality);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(mNationalityAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedNationality = i;
                        if (i == 0){
                            mSelectedNationality = -1;
                            mNationalityEditText.setText("");
                        }else {
                            mNationalityEditText.setText(mNationalities.get(i).optionName());
                            AddAffiliationMemberActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    mNationalityAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCalendar() {
        Date date = null;
        String data = mBirthdayEditText.getText().toString().trim();
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

        DatePickerDialog dateDialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
        dateDialog.show();
    }

    private void checkShowAgeAlert(Date inputDate) {

        int age1 = mMember.age;
        Log.e (TAG, "Age1: " + age1);

        int age2 =0;
        if(inputDate!=null){
            age2 =  DateUtils.getAge(inputDate);
        }
        Log.e (TAG, "Age2: " + age2);

        if (age1 != age2) {
            showMessageWithTitle(getResources().getString(R.string.warning_alert), getResources().getString(R.string.affiliation_member_error_age));
        }
    }

    private void captureQRCode() {
        if (!permHelper.checkPermissionForCamera(this)) {
            permHelper.requestPermissionForCamera(this);
        } else {
            IntentHelper.goToQrCodeScan(this, QRCODE_REQUEST);
        }
    }

    private void qrCodeCaptured(String data) {
        Log.e(TAG, "QRCODE: " + data);

        // parse data
        String[] dataArray = data.split("@");
        try {

            String birthDay = "";
            if (dataArray.length < 10) {
                mFirstNameEditText.setText(StringHelper.capitalize(dataArray[2]));
                mLastNameEditText.setText(StringHelper.capitalize(dataArray[1]));
                mDocNumberEditText.setText(dataArray[4]);
                birthDay = dataArray[6];
            } else {
                mFirstNameEditText.setText(StringHelper.capitalize(dataArray[5]));
                mLastNameEditText.setText(StringHelper.capitalize(dataArray[4]));
                mDocNumberEditText.setText(dataArray[1]);
                birthDay = dataArray[7];
            }

            Date date = ParserUtils.parseDate(birthDay, "dd/MM/yyyy");
            mBirthdayEditText.setText(ParserUtils.parseDate(date, "yyyy-MM-dd"));

        } catch (Exception e) {

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddAffiliationMember Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
