package ar.com.sancorsalud.asociados.activity.affiliation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.activity.main.SalesmanMainActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.ImcAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.interfaces.ClickListener;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.model.affiliation.DesNumberVerification;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.model.affiliation.DesState;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.RecyclerTouchListener;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

public class DesActivity extends BaseActivity {

    private static final String TAG = "DES_ACT";

    private static final String DES_FILE_PREFIX = "des";
    private static final String HEALTH_CERT_FILE_PREFIX = "certSalud";
    private static final String ATTACH_FILE_PREFIX = "adjunto";
    private static final String ANEXO_FILE_PREFIX = "anexo";
    private static final String FILE_EXTENSION = ".png";

    private ScrollView mScrollView;

    // IMC
    private RecyclerView recyclerView;
    private ImcAdapter imcAdapter;
    int imcResult = -1;

    // DES files
    private RecyclerView mDesFilesRecyclerView;
    private AttachFilesAdapter mDesFilesAdapter;
    private Button addDesFileButton;
    private String desFilesSubDir;

    private EditText mDesNotesEditTxt;
    private EditText mDesRequestNumberEditTxt;

    // HealthCert  files
    private RelativeLayout healthCertBox;
    private TextView healthCertTitle;

    private RecyclerView mHealthCertFilesRecyclerView;
    private AttachFilesAdapter mHealthCertFilesAdapter;
    private Button addHealthCertFileButton;
    private String healthCertFilesSubDir;

    // Attachs Files
    private RecyclerView mAttachsFilesRecyclerView;
    private AttachFilesAdapter mAttachsFilesAdapter;
    private Button addAttachFileButton;

    // Anexo Files
    private RecyclerView mAnexoFilesRecyclerView;
    private AttachFilesAdapter mAnexoFilesAdapter;
    private Button addAnexoFileButton;
    private String anexoFilesSubDir;

    private LinearLayout mDesButton;
    private LinearLayout mDesAuditoriaButton;

    private Button mDesNumberVerificationButton;
    private Button mBarCodeButton;

    // patologia
    private RadioButton mYesPPRadioButton;
    private RadioButton mNoPPRaRadioButton;
    boolean hasPatologia = false;

    private Des mDes;

    private boolean editableCard = true;

    private boolean editableIMC = false;
    private boolean editableHealthCert = false;
    private boolean editableAuxInfo = false;
    private boolean editableAnexo = false;

    private boolean hasToShowHealthCertFiles = false;


    private DesState mDesState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.init_load_des);

        mMainContainer = findViewById(R.id.main);

        mScrollView = (ScrollView) findViewById(R.id.scroll);

        mDesRequestNumberEditTxt = (EditText) findViewById(R.id.des_request_number_input);

        mDesNotesEditTxt = (EditText) findViewById(R.id.des_notes_input);
        setTypeTextNoSuggestions(mDesNotesEditTxt);

        mYesPPRadioButton = (RadioButton) findViewById(R.id.des_pp_yes_button);
        mNoPPRaRadioButton = (RadioButton) findViewById(R.id.des_pp_no_button);

        mDesNumberVerificationButton = (Button) findViewById(R.id.des_num_verif_button);
        mBarCodeButton = (Button) findViewById(R.id.barcode_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mDesButton = (LinearLayout) findViewById(R.id.des_button);
        mDesAuditoriaButton = (LinearLayout) findViewById(R.id.des_auditoria_button);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        healthCertTitle = (TextView) findViewById(R.id.healthCert_title);
        healthCertBox = (RelativeLayout) findViewById(R.id.healthCert_file_box);

        addDesFileButton = (Button) findViewById(R.id.des_add_button);

        addHealthCertFileButton = (Button) findViewById(R.id.healthCert_add_button);

        addAttachFileButton = (Button) findViewById(R.id.attach_add_button);
        addAnexoFileButton = (Button) findViewById(R.id.anexo_add_button);

        if (getIntent().getExtras() != null) {
            mDes = (Des) getIntent().getSerializableExtra(ConstantsUtil.DES);

            if (mDes != null) {

                desFilesSubDir = mDes.clientDni + "/des/des_files";
                healthCertFilesSubDir = mDes.clientDni + "/des/health_files";
                attachFilesSubDir = mDes.clientDni + "/des/attach_files";
                anexoFilesSubDir = mDes.clientDni + "/des/anexo_files";

                mImageProvider = new ImageProvider(this);
                disableAddDesButton();

                hasToShowHealthCertFiles = hasToShowHealthCertFiles();
                Log.e(TAG, "hasToShowHealthCertFiles: " + hasToShowHealthCertFiles + " ************************");

                if (editableCard) {
                    if (mDes.cardId != -1L) {
                        checkDESState();
                    } else {

                        // no card yet must enable all
                        editableIMC = true;
                        editableAuxInfo = true;
                        editableAnexo = true;

                        disableView(addDesFileButton); // must scan code first

                        if (hasToShowHealthCertFiles) {
                            showHealthCertSection(true);
                            editableHealthCert = true;
                            enableView(addHealthCertFileButton);
                        } else {
                            showHealthCertSection(false);
                            editableHealthCert = false;
                        }

                        enableView(addAttachFileButton);
                        enableView(addAnexoFileButton);
                        enableView(mDesAuditoriaButton);

                        mDesNotesEditTxt.setEnabled(true);
                        mDesNotesEditTxt.setFocusable(true);
                        mYesPPRadioButton.setEnabled(true);
                        mNoPPRaRadioButton.setEnabled(true);

                        // initial buttons states
                        mDesButton.setVisibility(View.VISIBLE);
                        mDesAuditoriaButton.setVisibility(View.GONE); // Initial visibility but can send to auditoria

                        onCheckDesState();
                        checkDerivarToAuditoria();
                    }

                } else {
                    // read only
                    editableIMC = false;
                    editableHealthCert = false;
                    editableAuxInfo = false;
                    editableAnexo = false;

                    disableView(addDesFileButton); // must scan code first
                    enableView(mBarCodeButton);

                    if (hasToShowHealthCertFiles) {
                        showHealthCertSection(true);
                        disableView(addHealthCertFileButton);
                    } else {
                        showHealthCertSection(false);
                    }

                    disableView(addAttachFileButton);
                    disableView(addAnexoFileButton);
                    disableView(mDesAuditoriaButton);

                    mDesNotesEditTxt.setEnabled(false);
                    mDesNotesEditTxt.setFocusable(false);
                    mYesPPRadioButton.setEnabled(false);
                    mNoPPRaRadioButton.setEnabled(false);

                    mDesButton.setVisibility(View.GONE);
                    mDesAuditoriaButton.setVisibility(View.GONE);

                    onCheckDesState();
                    checkDerivarToAuditoria();
                }
            }
        }
    }

    private void onCheckDesState() {
        fillDesData();

        fillDesFiles();
        fillHealthCertFiles();
        fillAttachsFiles();
        fillAnexoFiles();

        setupListeners();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check SCAN MODE
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String code = mDesRequestNumberEditTxt.getText().toString().trim();
            String scanCode = result.getContents();
            Log.e(TAG, "Result: " + scanCode);

            if (scanCode != null && code.equals(scanCode.trim())) {
                enableAddDesButton();
                SnackBarHelper.makeSucessful(mMainContainer, R.string.error_ok_scan_code).show();
            } else {
                SnackBarHelper.makeError(mMainContainer, R.string.error_incompatible_scan_code).show();
                if (mDesFilesAdapter != null)
                    mDesFilesAdapter.removeAllItems();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void updateFileList(final AttachFile attachFile) {

        if (attachFileType.equals(DES_FILE_PREFIX)) {
            mDesFilesAdapter.addItem(attachFile);

        } else if (attachFileType.equals(HEALTH_CERT_FILE_PREFIX)) {
            mHealthCertFilesAdapter.removeAllItems();
            mHealthCertFilesAdapter.addItem(attachFile);

        } else if (attachFileType.equals(ATTACH_FILE_PREFIX)) {
            if (mDes.id != -1L) {
                Log.e(TAG, "Attaching in edit mode!");

                // this also add file to adapter
                associateAttchedFileToDes(attachFile);
            } else {
                mAttachsFilesAdapter.addItem(attachFile);
            }

        } else if (attachFileType.equals(ANEXO_FILE_PREFIX)) {
            // only one anexo
            mAnexoFilesAdapter.removeAllItems();
            mAnexoFilesAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position) {
        if (attachFileType.equals(DES_FILE_PREFIX)) {
            mDesFilesAdapter.removeItem(position);
        } else if (attachFileType.equals(HEALTH_CERT_FILE_PREFIX)) {
            mHealthCertFilesAdapter.removeItem(position);
        } else if (attachFileType.equals(ATTACH_FILE_PREFIX)) {
            mAttachsFilesAdapter.removeItem(position);
        } else if (attachFileType.equals(ANEXO_FILE_PREFIX)) {
            mAnexoFilesAdapter.removeItem(position);
        }
    }


    protected void didClickItem(int position) {
        Log.e(TAG, "didClickItem.....");
        DesDetail detail = imcAdapter.getItemAtIndex(position);

        if (!detail.active && detail.age >= 18) {
            showImcDialog(detail);
        } else {
            SnackBarHelper.makeError(recyclerView, R.string.bad_capita_error).show();
        }
    }

    private void fillDesData() {
        mScrollView.smoothScrollTo(0, 0);

        if (mDes.requestNumber != -1) {
            mDesRequestNumberEditTxt.setText(Long.valueOf(mDes.requestNumber).toString());
        }

        if (mDes.comments != null) {
            mDesNotesEditTxt.setText(mDes.comments);
        }

        // default
        mYesPPRadioButton.setChecked(false);
        mNoPPRaRadioButton.setChecked(true);


        for (DesDetail detail : mDes.details) {
            silenceCalculateImc(detail);
        }

        /*
        if (groupNeedAuditoria()) {
            mYesPPRadioButton.setChecked(true);
            mNoPPRaRadioButton.setChecked(false);
        }
        */


        imcAdapter = new ImcAdapter(getApplicationContext(), mDes.details);

        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imcAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                didClickItem(position);
            }

            @Override
            public void onLongClick(View view, int position, float xx, float yy) {
            }
        }));

    }

    private void fillDesFiles() {
        Log.e(TAG, "buildDesFiles Adapter ...");
        for (AttachFile f : mDes.desFiles) {
            Log.e(TAG, "File: " + f.id + " :: " + f.fileNameAndExtension);
        }

        mDesFilesAdapter = new AttachFilesAdapter(mDes.desFiles, editableIMC);

        mDesFilesRecyclerView = (RecyclerView) findViewById(R.id.des_recycler_view);
        LinearLayoutManager desLayoutManager = new LinearLayoutManager(mDesFilesRecyclerView.getContext());
        mDesFilesRecyclerView.setLayoutManager(desLayoutManager);
        mDesFilesRecyclerView.setAdapter(mDesFilesAdapter);
        mDesFilesRecyclerView.setHasFixedSize(true);
    }


    private void fillHealthCertFiles() {
        for (AttachFile f : mDes.healthCertFiles) {
            Log.e(TAG, "File: " + f.id + " :: " + f.fileNameAndExtension);
        }
        mHealthCertFilesAdapter = new AttachFilesAdapter(mDes.healthCertFiles, editableHealthCert);

        mHealthCertFilesRecyclerView = (RecyclerView) findViewById(R.id.healthCert_recycler_view);
        LinearLayoutManager desLayoutManager = new LinearLayoutManager(mHealthCertFilesRecyclerView.getContext());
        mHealthCertFilesRecyclerView.setLayoutManager(desLayoutManager);
        mHealthCertFilesRecyclerView.setAdapter(mHealthCertFilesAdapter);
        mHealthCertFilesRecyclerView.setHasFixedSize(true);
    }

    private void fillAttachsFiles() {
        mAttachsFilesAdapter = new AttachFilesAdapter(mDes.attachsFiles, editableAuxInfo);

        mAttachsFilesRecyclerView = (RecyclerView) findViewById(R.id.attachs_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mAttachsFilesRecyclerView.getContext());
        mAttachsFilesRecyclerView.setLayoutManager(attachLayoutManager);
        mAttachsFilesRecyclerView.setAdapter(mAttachsFilesAdapter);
        mAttachsFilesRecyclerView.setHasFixedSize(true);
    }

    private void fillAnexoFiles() {
        mAnexoFilesAdapter = new AttachFilesAdapter(mDes.anexoFiles, editableAnexo);

        mAnexoFilesRecyclerView = (RecyclerView) findViewById(R.id.anexo_recycler_view);
        LinearLayoutManager anexoLayoutManager = new LinearLayoutManager(mAnexoFilesRecyclerView.getContext());
        mAnexoFilesRecyclerView.setLayoutManager(anexoLayoutManager);
        mAnexoFilesRecyclerView.setAdapter(mAnexoFilesAdapter);
        mAnexoFilesRecyclerView.setHasFixedSize(true);
    }


    private void checkDESState() {

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.des_loading_state);

            CardController.getInstance().checkDesState(mDes.cardId, new Response.Listener<DesState>() {
                @Override
                public void onResponse(DesState desState) {
                    dismissProgressDialog();

                    mDesState = desState;
                    if (mDesState != null) {

                        Log.e(TAG, "desState: " + mDesState.stateId + " ++++++++++++++++++");
                        switch (mDesState.stateId) {

                            case ConstantsUtil.DES_STATE_PENDING_CHARGE:
                                editableIMC = true;
                                editableAuxInfo = true;
                                editableAnexo = true;

                                disableView(addDesFileButton); // must scan code first and  editableIMC is flag to enable or not  codeButton
                                enableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    editableHealthCert = true;
                                    enableView(addHealthCertFileButton);
                                } else {
                                    editableHealthCert = false;
                                    showHealthCertSection(false);
                                }

                                enableView(addAttachFileButton);
                                enableView(addAnexoFileButton);
                                enableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(true);
                                mDesNotesEditTxt.setFocusable(true);
                                mYesPPRadioButton.setEnabled(true);
                                mNoPPRaRadioButton.setEnabled(true);


                                mDesButton.setVisibility(View.VISIBLE);
                                mDesAuditoriaButton.setVisibility(View.GONE);
                                checkDerivarToAuditoria();
                                break;

                            case ConstantsUtil.DES_STATE_PENDING:
                                editableIMC = true;
                                editableAuxInfo = true;
                                editableAnexo = true;

                                disableView(addDesFileButton); // must scan code first
                                enableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    editableHealthCert = true;
                                    enableView(addHealthCertFileButton);
                                } else {
                                    editableHealthCert = false;
                                    showHealthCertSection(false);
                                }
                                enableView(addAttachFileButton);
                                enableView(addAnexoFileButton);
                                enableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(true);
                                mDesNotesEditTxt.setFocusable(true);
                                mYesPPRadioButton.setEnabled(true);
                                mNoPPRaRadioButton.setEnabled(true);

                                mDesButton.setVisibility(View.VISIBLE);
                                mDesAuditoriaButton.setVisibility(View.GONE);
                                break;

                            case ConstantsUtil.DES_STATE_APROVED:
                                editableIMC = false;
                                editableHealthCert = false;
                                editableAuxInfo = false;
                                editableAnexo = false;

                                disableView(addDesFileButton);
                                disableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    disableView(addHealthCertFileButton);
                                } else {
                                    showHealthCertSection(false);
                                }

                                disableView(addAttachFileButton);
                                disableView(addAnexoFileButton);
                                disableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(false);
                                mDesNotesEditTxt.setFocusable(false);
                                mYesPPRadioButton.setEnabled(false);
                                mNoPPRaRadioButton.setEnabled(false);

                                mDesButton.setVisibility(View.VISIBLE);
                                mDesAuditoriaButton.setVisibility(View.GONE);
                                break;

                            case ConstantsUtil.DES_STATE_REJECTED:
                                editableIMC = false;
                                editableHealthCert = false;
                                editableAuxInfo = false;
                                editableAnexo = false;

                                disableView(addDesFileButton);
                                disableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    disableView(addHealthCertFileButton);
                                } else {
                                    showHealthCertSection(false);
                                }

                                disableView(addAttachFileButton);
                                disableView(addAnexoFileButton);
                                disableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(false);
                                mDesNotesEditTxt.setFocusable(false);
                                mYesPPRadioButton.setEnabled(false);
                                mNoPPRaRadioButton.setEnabled(false);

                                mDesButton.setVisibility(View.GONE);
                                mDesAuditoriaButton.setVisibility(View.GONE);

                                DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_rejected));
                                break;

                            case ConstantsUtil.DES_STATE_IN_CORRECTION:

                                //editableIMC = false;
                                editableIMC = true;
                                editableAuxInfo = true;
                                editableAnexo = true;
                                enableView(addDesFileButton);
                                enableAddDesButton();
                                enableView(mBarCodeButton);

                                //disableView(addDesFileButton); // must scan code first
                                //disableView(mBarCodeButton);



                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    editableHealthCert = true;
                                    enableView(addHealthCertFileButton);
                                } else {
                                    editableHealthCert = false;
                                    showHealthCertSection(false);
                                }
                                enableView(addAttachFileButton);
                                enableView(addAnexoFileButton);
                                enableView(mDesAuditoriaButton);

                                //mDesNotesEditTxt.setEnabled(false);
                                //mDesNotesEditTxt.setFocusable(false);
                                //mYesPPRadioButton.setEnabled(false);
                                //mNoPPRaRadioButton.setEnabled(false);

                                mDesNotesEditTxt.setEnabled(true);
                                mDesNotesEditTxt.setFocusable(true);
                                mYesPPRadioButton.setEnabled(true);
                                mNoPPRaRadioButton.setEnabled(true);

                                mDesButton.setVisibility(View.GONE);
                                mDesAuditoriaButton.setVisibility(View.VISIBLE);
                                break;

                            case ConstantsUtil.DES_STATE_APROVED_WITH_MODULE:
                                editableIMC = false;
                                editableHealthCert = false;
                                editableAuxInfo = true;
                                editableAnexo = true;

                                disableView(addDesFileButton);
                                disableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    enableView(addHealthCertFileButton);
                                } else {
                                    showHealthCertSection(false);
                                }

                                enableView(addAttachFileButton);
                                enableView(addAnexoFileButton);
                                disableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(false);
                                mDesNotesEditTxt.setFocusable(false);
                                mYesPPRadioButton.setEnabled(false);
                                mNoPPRaRadioButton.setEnabled(false);

                                mDesButton.setVisibility(View.VISIBLE);
                                mDesAuditoriaButton.setVisibility(View.GONE);
                                break;

                            default:
                                editableIMC = false;
                                editableHealthCert = false;
                                editableAuxInfo = false;
                                editableAnexo = false;

                                disableView(addDesFileButton);
                                disableView(mBarCodeButton);

                                if (hasToShowHealthCertFiles) {
                                    showHealthCertSection(true);
                                    disableView(addHealthCertFileButton);
                                } else {
                                    showHealthCertSection(false);
                                }

                                disableView(addAttachFileButton);
                                disableView(addAnexoFileButton);
                                disableView(mDesAuditoriaButton);

                                mDesNotesEditTxt.setEnabled(false);
                                mDesNotesEditTxt.setFocusable(false);
                                mYesPPRadioButton.setEnabled(false);
                                mNoPPRaRadioButton.setEnabled(false);

                                mDesButton.setVisibility(View.GONE);
                                mDesAuditoriaButton.setVisibility(View.GONE);
                                break;

                        }

                        onCheckDesState();

                    } else {
                        DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_state));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_save), (error.getMessage() != null ? error.getMessage() : ""));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(DesActivity.this, null);
        }
    }


    private void setupListeners() {

        setupImageProvider();
        mDesRequestNumberEditTxt.setFocusable(editableIMC);

        if (editableIMC) {

            mBarCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new IntentIntegrator((Activity) ctx).initiateScan();
                }
            });

            mYesPPRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkDerivarToAuditoria();
                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_to_auditoria));
                }
            });

            mNoPPRaRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkDerivarToAuditoria();
                }
            });

            // addDesFileButton: must scan file first

            mDesFilesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "onItemClick():" + position);

                    AttachFile desFile = mDes.desFiles.get(position);
                    Log.e(TAG, "DES path:" + desFile.filePath);
                    Log.e(TAG, "DES fileNameAndExtension:" + desFile.fileNameAndExtension);
                    loadFile(desFile);
                }
            });

            mDesFilesAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "DES onItemDeleteClick()!!!!!:" + position);
                    attachFileType = DES_FILE_PREFIX;
                    AttachFile desFile = mDes.desFiles.get(position);
                    removeFile(desFile, position);
                }
            });
        }

        if (editableHealthCert) {

            addHealthCertFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = HEALTH_CERT_FILE_PREFIX;
                    mImageProvider.showImagePicker(healthCertFilesSubDir, HEALTH_CERT_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mHealthCertFilesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "onItemClick():" + position);

                    AttachFile healthCertFile = mDes.healthCertFiles.get(position);
                    Log.e(TAG, "healthCertFile path:" + healthCertFile.filePath);
                    loadFile(healthCertFile);
                }
            });

            mHealthCertFilesAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "healthCert onItemDeleteClick()!!!!!:" + position);
                    attachFileType = HEALTH_CERT_FILE_PREFIX;
                    AttachFile attachFile = mDes.healthCertFiles.get(position);
                    removeFile(attachFile, position);
                }
            });
        }

        if (editableAuxInfo) {

            // Buttons
            addAttachFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = ATTACH_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir, ATTACH_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mAttachsFilesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "onItemClick():" + position);

                    AttachFile attachFile = mDes.attachsFiles.get(position);
                    Log.e(TAG, "Attach path:" + attachFile.filePath);
                    loadFile(attachFile);
                }
            });

            mAttachsFilesAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "Attach onItemDeleteClick()!!!!!:" + position);
                    attachFileType = ATTACH_FILE_PREFIX;
                    AttachFile attachFile = mDes.attachsFiles.get(position);
                    removeFile(attachFile, position);
                }
            });
        }


        if (editableAnexo) {

            addAnexoFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = ANEXO_FILE_PREFIX;
                    mImageProvider.showImagePicker(anexoFilesSubDir, ANEXO_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mAnexoFilesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "onItemClick():" + position);

                    AttachFile anexoFile = mDes.anexoFiles.get(position);
                    Log.e(TAG, "Anexo path:" + anexoFile.filePath);
                    loadFile(anexoFile);
                }
            });

            mAnexoFilesAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "Anexo onItemDeleteClick()!!!!!:" + position);
                    attachFileType = ANEXO_FILE_PREFIX;
                    AttachFile anexoFile = mDes.anexoFiles.get(position);
                    removeFile(anexoFile, position);
                }
            });
        }


        // ACTION BUTTON

        mDesNumberVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, " DES Number Verification !!!! ---------------");

                String desNumber = mDesRequestNumberEditTxt.getText().toString().trim();
                if (!desNumber.isEmpty()) {

                    mDesNumberVerificationButton.setEnabled(false);

                    CardController.getInstance().verifyDesNumber(Long.valueOf(desNumber), new Response.Listener<DesNumberVerification>() {
                        @Override
                        public void onResponse(DesNumberVerification desNumberVerification) {
                            dismissProgressDialog();

                            if (desNumberVerification != null) {
                                mDesNumberVerificationButton.setEnabled(true);
                                if (desNumberVerification.existentDes) {

                                    /*
                                    String fullData = getFullData(mDes.details);
                                    if (fullData != null){
                                        DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_verification_number_pa_error, fullData));
                                    }else {
                                        DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_verification_number_error));
                                    }
                                    */

                                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_verification_number_error));

                                } else {
                                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_verification_number_ok));
                                    enableAddDesButton();
                                }

                            } else {
                                mDesNumberVerificationButton.setEnabled(true);
                                DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_verification));
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();
                            mDesNumberVerificationButton.setEnabled(true);
                            Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_verification), (error.getMessage() != null ? error.getMessage() : ""));
                        }
                    });
                } else {
                    SnackBarHelper.makeError(mMainContainer, R.string.error_des_number_empty).show();
                }
            }
        });


        mDesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "mDesButton !!!");

                if (isValidDesForm()) {

                    mDes.hasPatologia = hasPatologia;
                    mDes.comments = mDesNotesEditTxt.getText().toString();
                    mDes.details = imcAdapter.getMembers();

                    mDes.desFiles = mDesFilesAdapter.getAttachFiles();
                    mDes.healthCertFiles = mHealthCertFilesAdapter.getAttachFiles();
                    mDes.attachsFiles = mAttachsFilesAdapter.getAttachFiles();
                    mDes.anexoFiles = mAnexoFilesAdapter.getAttachFiles();

                    mDes.requestNumber = Long.valueOf((mDesRequestNumberEditTxt.getText().toString().trim()));
                    mDes.doctorId = 0L;

                    if (AppController.getInstance().isNetworkAvailable()) {
                        showProgressDialog(R.string.des_saving);
                        mDesButton.setEnabled(false);

                        CardController.getInstance().saveDesData(mDes, new Response.Listener<DesResult>() {
                            @Override
                            public void onResponse(DesResult desResult) {
                                dismissProgressDialog();

                                if (desResult != null) {
                                    mDesButton.setEnabled(true);

                                    if (desResult == null || desResult.desId == -1L || desResult.cardId == -1L) {
                                        DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_save));
                                    } else {
                                        Log.e(TAG, "save desData ok desId: " + desResult.desId + "cardID: " + desResult.cardId);

                                        // This create desid, and generate cardId
                                        mDes.id = desResult.desId;
                                        Storage.getInstance().setAffiliationCardId(desResult.cardId);

                                        SnackBarHelper.makeSucessful(mDesFilesRecyclerView, R.string.des_saved_ok).show();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1500L);
                                    }
                                } else {
                                    mDesButton.setEnabled(true);
                                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_save));
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissProgressDialog();
                                mDesButton.setEnabled(true);
                                Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                                DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_des_save), (error.getMessage() != null ? error.getMessage() : ""));
                            }
                        });

                    } else {
                        mDesButton.setEnabled(true);
                        DialogHelper.showNoInternetErrorMessage(DesActivity.this, null);
                    }
                }
            }
        });

        mDesAuditoriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mDesAuditoriaButton on Click...");

                if (isValidAuditoriaForm()) {

                    Log.e(TAG, "TO medico Auditor .....");
                    mDes.desNumber = Long.valueOf(mDesRequestNumberEditTxt.getText().toString().trim());
                    mDes.hasPatologia = hasPatologia;

                    mDes.comments = mDesNotesEditTxt.getText().toString();
                    mDes.details = imcAdapter.getMembers();

                    mDes.desFiles = mDesFilesAdapter.getAttachFiles();

                    mDes.healthCertFiles = mHealthCertFilesAdapter.getAttachFiles();

                    mDes.attachsFiles = mAttachsFilesAdapter.getAttachFiles();
                    mDes.anexoFiles = mAnexoFilesAdapter.getAttachFiles();

                    mDes.requestNumber = Long.valueOf((mDesRequestNumberEditTxt.getText().toString().trim()));
                    mDes.doctorId = 0L;

                    sendDataToAuditoria();
                }
            }
        });
    }


    private String getFullData(List<DesDetail> detailList) {
        String fullData = null;
        for (DesDetail detail : detailList) {
            if (detail.parentesco.id.equals(ConstantsUtil.TITULAR_MEMBER)) {
                fullData = detail.getFullName() + " " + mDes.clientDni;
                break;
            }
        }
        return fullData;
    }


    private void enableAddDesButton() {
        enableView(addDesFileButton);
        addDesFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFileType = DES_FILE_PREFIX;
                mImageProvider.showImagePicker(desFilesSubDir, DES_FILE_PREFIX + FileHelper.getFilePrefix());
            }
        });
    }

    private void disableAddDesButton() {
        disableView(addDesFileButton);
        addDesFileButton.setOnClickListener(null);
    }


    private boolean groupNeedAuditoria() {
        boolean result = false;
        for (DesDetail detail : mDes.details) {
            if (detail.needAuditoria) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean groupUncompleteIMCData() {
        boolean result = false;
        for (DesDetail detail : mDes.details) {

            // No need to check if details are loaded for existent sancor system user --> active = true
            // Need to check if details has comes form quotation parameters  --> active = false
            if (!detail.active && detail.imc == -1 && detail.age >= 18) {
                result = true;
                break;
            }
        }
        return result;
    }


    private boolean hasToShowHealthCertFiles() {
        boolean result = false;
        for (DesDetail detail : mDes.details) {
            if (detail.parentesco.id.equals(ConstantsUtil.HIJO_SOLT_MENOR_21_MEMBER) || detail.parentesco.id.equals(ConstantsUtil.HIJO_SOLT_21_25_MEMBER)
                    || detail.parentesco.id.equals(ConstantsUtil.HIJO_CONY_SOLT_MENOR_21_MEMBER) || detail.parentesco.id.equals(ConstantsUtil.PARENTESCO_CODE_6)) {
                if (detail.age < 1) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }


    private void showHealthCertSection(boolean show) {
        if (show) {
            healthCertTitle.setVisibility(View.VISIBLE);
            healthCertBox.setVisibility(View.VISIBLE);
        } else {
            healthCertTitle.setVisibility(View.GONE);
            healthCertBox.setVisibility(View.GONE);
        }
    }

    private boolean validDataToDes() {
        hasPatologia = mYesPPRadioButton.isChecked();

        if (groupUncompleteIMCData()) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_uncomplete_imc));
            return false;
        } else if (groupNeedAuditoria() || hasPatologia) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_to_auditoria));
            return false;
        } else if (mDes.desFiles.size() == 0) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_add_des_files));
            return false;
        } else {
            return true;
        }
    }


    private boolean validDataToAuditoria() {
        hasPatologia = mYesPPRadioButton.isChecked();

        if (groupUncompleteIMCData()) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_uncomplete_imc));
            return false;
        } else if (mDes.desFiles.size() == 0) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_add_des_files));
            return false;
        } else if (hasToShowHealthCertFiles() && mDes.healthCertFiles.size() == 0) {
            DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_add_health_cert_files));
            return false;
        } else {
            return true;
        }
    }

    private void checkDerivarToAuditoria() {
        resetValidationField(R.id.des_request_number_wrapper);

        if (hasToDerivarToAuditoria()) {
            mDesButton.setVisibility(View.GONE);
            mDesAuditoriaButton.setVisibility(View.VISIBLE);
        } else {
            mDesButton.setVisibility(View.VISIBLE);
            mDesAuditoriaButton.setVisibility(View.GONE);
        }
    }

    private boolean hasToDerivarToAuditoria() {
        boolean hasPatologia = mYesPPRadioButton.isChecked();
        if (groupNeedAuditoria() || hasPatologia || hasToShowHealthCertFiles()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidDesForm() {

        boolean isValid = true;

        isValid = isValid & validDataToDes();
        isValid = isValid & validateField(mDesRequestNumberEditTxt, R.string.add_des_request_number_error, R.id.des_request_number_wrapper);
        return isValid;
    }

    private boolean isValidAuditoriaForm() {

        boolean isValid = true;

        isValid = isValid & validDataToAuditoria();
        isValid = isValid & validateField(mDesRequestNumberEditTxt, R.string.add_des_request_number_error, R.id.des_request_number_wrapper);
        return isValid;
    }


    // ----- IMC -------------------------------------------------------- //

    private void showImcDialog(final DesDetail detail) {

        Log.e(TAG, "Member: " + detail.firstname);

        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.activity_imc_input, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        inflator.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        inflator.setMinimumHeight((int) (displayRectangle.height() * 0.5f));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(inflator);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();

        final EditText mWeight = (EditText) inflator.findViewById(R.id.imc_weight_input);
        final EditText mHeight = (EditText) inflator.findViewById(R.id.imc_height_input);
        final EditText mImcText = (EditText) inflator.findViewById(R.id.imc_result);

        Button mButton = (Button) inflator.findViewById(R.id.imc_button);
        ImageView closeButton = (ImageView) inflator.findViewById(R.id.close_button);

        if (detail.weight != -1f) {
            mWeight.setText(String.format("%.2f", detail.weight).replace(",", "."));
        }
        if (detail.height != -1f) {
            mHeight.setText(String.format("%.2f", detail.height).replace(",", "."));
        }
        if (detail.imc != -1) {
            printImc(detail, mImcText);
        }

        if (editableCard && editableIMC) {
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftKeyboard(view);

                    boolean isValid = true;
                    isValid = isValid & validateField(mWeight, R.string.des_imc_weight_error, R.id.imc_weight_wrapper, inflator);
                    isValid = isValid & validateField(mHeight, R.string.des_imc_height_error, R.id.imc_height_wrapper, inflator);

                    if (isValid) {

                        double weight = Double.valueOf(mWeight.getText().toString().trim().replace(",", "."));
                        double height = Double.valueOf(mHeight.getText().toString().trim().replace(",", "."));
                        double imc = (weight / (height * height));

                        String sImc = String.format("%.2f", imc).replace(",", ".");
                        imc = Double.valueOf(sImc.trim().replace(",", "."));

                        Log.e(TAG, "weight: " + weight);
                        Log.e(TAG, "height: " + height);
                        Log.e(TAG, "IMC: " + imc);

                        // update detail data
                        detail.weight = weight;
                        detail.height = height;
                        detail.imc = imc;

                        calculateImc(detail, mImcText);

                    } else {
                        mImcText.setText("");
                    }
                }
            });
        } else {
            disableView(mButton);
            mWeight.setFocusable(false);
            mHeight.setFocusable(false);
            mImcText.setFocusable(false);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void printImc(DesDetail detail, EditText mImc) {
        if (detail.imc <= 17) {
            imcResult = R.string.des_imc_delgadez;
            detail.needAuditoria = true;
        } else if (detail.imc >= 34) {
            imcResult = R.string.des_imc_sobrepeso;
            detail.needAuditoria = true;
        } else {
            imcResult = R.string.des_imc_normal;
            detail.needAuditoria = false;
        }
        mImc.setText(getResources().getString(imcResult));
        detail.descPatologia = getResources().getString(imcResult);

        checkDerivarToAuditoria();
    }


    private void calculateImc(DesDetail detail, EditText mImc) {
        if (detail.imc <= 17) {
            imcResult = R.string.des_imc_delgadez;
            detail.needAuditoria = true;

            showMessageWithTitle(getResources().getString(imcResult), getResources().getString(R.string.des_imc_alert),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else if (detail.imc > 33) {
            imcResult = R.string.des_imc_sobrepeso;
            detail.needAuditoria = true;

            showMessageWithTitle(getResources().getString(imcResult), getResources().getString(R.string.des_imc_alert),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            detail.needAuditoria = false;
            imcResult = R.string.des_imc_normal;

        }
        mImc.setText(getResources().getString(imcResult));
        detail.descPatologia = getResources().getString(imcResult);

        checkDerivarToAuditoria();
    }

    private void silenceCalculateImc(DesDetail detail) {
        // Check if in service we can load the detail imc
        if (detail.imc != -1f) {
            if (detail.imc <= 17) {
                imcResult = R.string.des_imc_delgadez;
                detail.needAuditoria = true;
            } else if (detail.imc > 33) {
                imcResult = R.string.des_imc_sobrepeso;
                detail.needAuditoria = true;
            } else {
                detail.needAuditoria = false;
                imcResult = R.string.des_imc_normal;
            }
        }
    }

    private boolean validateField(EditText editText, int error, int inputId, View inflator) {
        TextInputLayout input = (TextInputLayout) inflator.findViewById(inputId);
        if (editText.getText().length() == 0 || editText.getText().toString().trim().equals(".")) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);

            return false;
        } else {
            input.setErrorEnabled(false);
            return true;
        }
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


    private void resetValidationField(int inputId) {
        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        input.setErrorEnabled(false);
    }


    private void sendDataToAuditoria() {
        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.des_send_auditoria_data);

            CardController.getInstance().sendDesToAuditoria(mDes, mDesState, new Response.Listener<DesResult>() {
                @Override
                public void onResponse(DesResult desResult) {
                    dismissProgressDialog();

                    Log.e(TAG, "des to auditoria OK desID: " + desResult.desId + "cardID: " + desResult.cardId);
                    SnackBarHelper.makeSucessful(mDesFilesRecyclerView, R.string.des_auditoria_ok).show();

                    // This create desid, and generate cardId
                    mDes.id = desResult.desId;

                    // if send to auditoria  Affiliation can not continue because card state will not be in card in proceess, so reload PA with the actual state
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toMain();
                        }
                    }, 1500L);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.des_auditoria_error), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(DesActivity.this, null);
        }
    }

    // ------- FILES ------------------------------------------------------------------- //

    private void toMain() {
        Intent intent = new Intent(this, SalesmanMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle params = new Bundle();
        params.putBoolean(ConstantsUtil.RELOAD_DATA, true);
        params.putBoolean(ConstantsUtil.CARDS_IN_PROCESS, true);
        intent.putExtras(params);

        startActivity(intent);
        finish();
    }


    private void associateAttchedFileToDes(final AttachFile attachFile) {

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.attaching_file);

            CardController.getInstance().attachFile(mDes.id, attachFile.id, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void result) {
                    dismissProgressDialog();
                    mAttachsFilesAdapter.addItem(attachFile);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.error_attaching_file), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(DesActivity.this, null);
        }
    }


    private void removeAttachFileFormDes(final AttachFile attachFile, final int position) {

        if (AppController.getInstance().isNetworkAvailable()) {
            showProgressDialog(R.string.removing_attach_file);

            CardController.getInstance().removeAttachFile(attachFile.id, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void result) {
                    dismissProgressDialog();

                    // remove physically
                    removeFile(attachFile, position);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    DialogHelper.showMessage(DesActivity.this, getResources().getString(R.string.removing_attach_file), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            DialogHelper.showNoInternetErrorMessage(DesActivity.this, null);
        }

    }
}