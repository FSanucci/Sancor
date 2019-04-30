package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.affiliation.AddAffiliationMemberActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.affiliation.TitularDoc;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AffiliationTitularDocFragment extends BaseFragment {

    private static final String TAG = "AFILL_DOC_FRG";

    private static final String DNI_FRONT_FILE_PREFIX = "dniFront";
    private static final String DNI_BACK_FILE_PREFIX = "dniBack";
    private static final String CUIL_FILE_PREFIX = "accountCuil";
    private static final String COVERAGE_FILE_PREFIX = "coverage";
    private static final String IVA_FILE_PREFIX = "iva";
    private static final String PLAN_FILE_PREFIX = "plan";

    private static final String ARG_TITULAR_DOC = "titularDoc";
    private static final String ARG_TITULAR_DNI = "titularDNI";
    private static final String ARG_COND_IVA = "condIva";
    private static final String ARG_COBERTURA = "cobertura";

    private ScrollView mScrollView;

    // DNI FRONT  File
    private RecyclerView mDNIiFrontRecyclerView;
    private AttachFilesAdapter mDNIFrontFileAdapter;
    private Button addDNIFrontButton;

    // DNI BACK  File
    private RecyclerView mDNIiBackRecyclerView;
    private AttachFilesAdapter mDNIBackFileAdapter;
    private Button addDNIBackButton;

    // CUIL  File
    private RecyclerView mCuilRecyclerView;
    private AttachFilesAdapter mCuilFileAdapter;
    private Button addCuilButton;

    // IVA  File
    private RelativeLayout mIvaBox;
    private RecyclerView mIvaRecyclerView;
    private AttachFilesAdapter mIvaFileAdapter;
    private Button addIvaButton;

    // COVERAGE  File
    private RelativeLayout mCoverageBox;
    private RecyclerView mCoverageRecyclerView;
    private AttachFilesAdapter mCoverageFileAdapter;
    private Button addCoverageButton;

    // PLAN  File
    private RecyclerView mPlanRecyclerView;
    private AttachFilesAdapter mPlanFileAdapter;
    private Button addPlanButton;

    private RelativeLayout mCuilBox;
    private RadioButton mYesCuilDorsoRadioButton;
    private RadioButton mNoCuilDorsoRadioButton;
    private TextView mCuilAnsesLink;

    private TitularDoc titularDoc;
    private long titularDNI;
    private QuoteOption titularCondIva;
    private QuoteOption titularCobertura;

    public List<AttachFile> tmpDNIFrontFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpDNIBackFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpCuilFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpCoverageFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpIvaFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpPlanFiles = new ArrayList<AttachFile>();

    private EditText mPlanCodeFormEditTxt;
    private Button mBarCodeButton;

    private View view;
    private boolean editableCard = true;

    private String ansesLink;

    public static AffiliationTitularDocFragment newInstance(TitularDoc titularDoc, long dni, QuoteOption condicionIva, QuoteOption coberturaProveniente) {
        AffiliationTitularDocFragment fragment = new AffiliationTitularDocFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TITULAR_DOC, titularDoc);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putSerializable(ARG_COND_IVA, condicionIva);
        args.putSerializable(ARG_COBERTURA, coberturaProveniente);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titularDoc = (TitularDoc) getArguments().getSerializable(ARG_TITULAR_DOC);
            titularDNI =  getArguments().getLong(ARG_TITULAR_DNI);
            titularCondIva =  (QuoteOption) getArguments().getSerializable(ARG_COND_IVA);
            titularCobertura = (QuoteOption) getArguments().getSerializable(ARG_COBERTURA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_titular_doc, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        attachFilesSubDir = titularDNI + "/affiliation/docs";
        mImageProvider = new ImageProvider(getActivity());
        mScrollView = (ScrollView) view.findViewById(R.id.scroll);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        this.view = view;
        fillAllPhysicalFiles();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // CHECK SCAN CODE
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String code = mPlanCodeFormEditTxt.getText().toString().trim();
            String scanCode = result.getContents();
            Log.e(TAG, "Result: " + scanCode);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void updateFileList(AttachFile attachFile){

        if ( attachFileType.equals(DNI_FRONT_FILE_PREFIX)) {
            mDNIFrontFileAdapter.addItem(attachFile);
        } else if ( attachFileType.equals(DNI_BACK_FILE_PREFIX)) {
            mDNIBackFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CUIL_FILE_PREFIX)) {
            mCuilFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(IVA_FILE_PREFIX)) {
            mIvaFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(COVERAGE_FILE_PREFIX)) {
            mCoverageFileAdapter.addItem(attachFile);
        } else if ( attachFileType.equals(PLAN_FILE_PREFIX)) {
            mPlanFileAdapter.addItem(attachFile);
        }

    }

    @Override
    public void onRemovedFile(int position){
        try {
            if (attachFileType.equals(DNI_FRONT_FILE_PREFIX)) {
                mDNIFrontFileAdapter.removeItem(position);
            } else if (attachFileType.equals(DNI_BACK_FILE_PREFIX)) {
                mDNIBackFileAdapter.removeItem(position);
            } else if (attachFileType.equals(CUIL_FILE_PREFIX)) {
                mCuilFileAdapter.removeItem(position);
            } else if (attachFileType.equals(COVERAGE_FILE_PREFIX)) {
                mCoverageFileAdapter.removeItem(position);
            } else if (attachFileType.equals(IVA_FILE_PREFIX)) {
                mIvaFileAdapter.removeItem(position);
            } else if (attachFileType.equals(PLAN_FILE_PREFIX)) {
                mPlanFileAdapter.removeItem(position);
            }
        }catch (Throwable e){}
    }

    public TitularDoc getDocument(){

        // UPDATE ORIGINAL DATA
        if (mDNIFrontFileAdapter != null) {
            titularDoc.dniFrontFiles = mDNIFrontFileAdapter.getAttachFiles();
        }

        if (mDNIBackFileAdapter != null) {
            titularDoc.dniBackFiles = mDNIBackFileAdapter.getAttachFiles();
        }

        if (mIvaFileAdapter != null) {
            titularDoc.ivaFiles = mIvaFileAdapter.getAttachFiles();
        }

        if (mCoverageFileAdapter != null) {
            titularDoc.coverageFiles = mCoverageFileAdapter.getAttachFiles();
        }

        if (mPlanFileAdapter != null) {
            titularDoc.planFiles = mPlanFileAdapter.getAttachFiles();
        }

        if (mYesCuilDorsoRadioButton != null && mYesCuilDorsoRadioButton.isChecked() && mCuilFileAdapter != null) {
            titularDoc.cuilFiles = mCuilFileAdapter.getAttachFiles();
        }
        return titularDoc;
    }

    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private void fillAllPhysicalFiles() {
        ((AffiliationActivity) getActivity()).setActionButtons(false);

        if (titularDoc == null) {
            titularDoc =  new TitularDoc();
        }

        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll Titular Data PhysicalFiles ....");
                fillDNIFrontFiles();
            }
        }).start();
    }

    private void fillDNIFrontFiles() {

        if (!titularDoc.dniFrontFiles.isEmpty()) {

            Log.e(TAG, "fillDNIFRONTFiles !!....");
            final AttachFile dniFrontFile = titularDoc.dniFrontFiles.remove(0);

            if (dniFrontFile.fileNameAndExtension == null  || dniFrontFile.fileNameAndExtension .isEmpty()) {

                Log.e (TAG, "Bajando File id: " + dniFrontFile.id );
                final Date d1 = new Date();


                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + DNI_FRONT_FILE_PREFIX, dniFrontFile.id, new Response.Listener<AttachFile>() {

                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting DNI Front File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            final Date d2 = new Date();
                            long attachTime = (d2.getTime() - d1.getTime())/ 1000;
                            Log.e(TAG, "Bajada Arch Time: " + attachTime + " *********************************");

                            tmpDNIFrontFiles.add(resultFile);
                            fillDNIFrontFiles();

                        } else {
                            Log.e(TAG, "Null DNI Front  file ....");
                            // if error mantain original file  because on next will be lost if we dont keepp it , because sistem saved it on next
                            tmpDNIFrontFiles.add(dniFrontFile);
                            fillDNIFrontFiles();
                        }
                    }
                },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpDNIFrontFiles.add(dniFrontFile);
                        fillDNIFrontFiles();
                    }
                });


            }else{
                tmpDNIFrontFiles.add(dniFrontFile);
                fillDNIFrontFiles();
            }

        }else {
            titularDoc.dniFrontFiles .addAll(tmpDNIFrontFiles);
            tmpDNIFrontFiles = new ArrayList<AttachFile>();
            fillDNIBackFiles();
        }
    }

    private void fillDNIBackFiles() {

        if (!titularDoc.dniBackFiles.isEmpty()) {

            Log.e(TAG, "fillBackFRONTFiles !!....");

            final AttachFile dniBackFile = titularDoc.dniBackFiles.remove(0);
            if (dniBackFile.fileNameAndExtension == null  || dniBackFile.fileNameAndExtension  .isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + DNI_BACK_FILE_PREFIX , dniBackFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting DNI Back File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpDNIBackFiles.add(resultFile);
                            fillDNIBackFiles();

                        } else {
                            Log.e(TAG, "Null DNI Back file ....");
                            tmpDNIBackFiles.add(dniBackFile);
                            fillDNIBackFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpDNIBackFiles.add(dniBackFile);
                        fillDNIBackFiles();
                    }
                });
            }else{
                tmpDNIBackFiles.add(dniBackFile);
                fillDNIBackFiles();
            }
        }else {
            titularDoc.dniBackFiles.addAll(tmpDNIBackFiles);
            tmpDNIBackFiles = new ArrayList<AttachFile>();
            fillCuilFiles();
        }
    }

    private void fillCuilFiles() {

        if (!titularDoc.cuilFiles.isEmpty()) {

            Log.e(TAG, "fillCuilFiles !!....");

            final AttachFile cuilFile = titularDoc.cuilFiles.remove(0);
            if (cuilFile.fileNameAndExtension == null  || cuilFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir  + "/" + CUIL_FILE_PREFIX, cuilFile.id, new Response.Listener<AttachFile>() {
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
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpCuilFiles.add(cuilFile);
                        fillCuilFiles();
                    }
                });
            }else{
                tmpCuilFiles.add(cuilFile);
                fillCuilFiles();
            }
        }else {
            titularDoc.cuilFiles.addAll(tmpCuilFiles);
            tmpCuilFiles = new ArrayList<AttachFile>();

            if (titularDoc.cuilFiles.size() > 0){
                titularDoc.cuilInFront = false;
            }else{
                titularDoc.cuilInFront = true;
            }
            fillCoverageFiles();
        }
    }

    private void fillCoverageFiles() {

        if (!titularDoc.coverageFiles.isEmpty()) {

            Log.e(TAG, "fillCoverageFiles !!....");

            final AttachFile coverageFile = titularDoc.coverageFiles.remove(0);
            if (coverageFile.fileNameAndExtension == null  || coverageFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + COVERAGE_FILE_PREFIX , coverageFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting Coverage File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCoverageFiles.add(resultFile);
                            fillCoverageFiles();

                        } else {
                            Log.e(TAG, "Null Coverage file ....");
                            tmpCoverageFiles.add(coverageFile);
                            fillCoverageFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpCoverageFiles.add(coverageFile);
                        fillCoverageFiles();
                    }
                });
            }else{
                tmpCoverageFiles.add(coverageFile);
                fillCoverageFiles();
            }
        }else {
            titularDoc.coverageFiles.addAll(tmpCoverageFiles);
            tmpCoverageFiles = new ArrayList<AttachFile>();
            fillIvaFiles();
        }
    }

    private void fillIvaFiles() {

        if (!titularDoc.ivaFiles.isEmpty()) {

            Log.e(TAG, "fillIvaFiles !!....");

            final AttachFile ivaFile = titularDoc.ivaFiles.remove(0);
            if (ivaFile.fileNameAndExtension == null  || ivaFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + IVA_FILE_PREFIX,  ivaFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting iva File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpIvaFiles.add(resultFile);
                            fillIvaFiles();

                        } else {
                            Log.e(TAG, "Null iva file ....");
                            tmpIvaFiles.add(ivaFile);
                            fillIvaFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpIvaFiles.add(ivaFile);
                        fillIvaFiles();
                    }
                });
            }else{
                tmpIvaFiles.add(ivaFile);
                fillIvaFiles();
            }
        }else {
            titularDoc.ivaFiles.addAll(tmpIvaFiles);
            tmpIvaFiles = new ArrayList<AttachFile>();
            fillPlanFiles();
        }
    }

    private void fillPlanFiles() {

        if (!titularDoc.planFiles.isEmpty()) {

            Log.e(TAG, "planFiles !!....");

            final AttachFile planFile = titularDoc.planFiles.remove(0);
            if (planFile.fileNameAndExtension == null  || planFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + PLAN_FILE_PREFIX, planFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting plan File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpPlanFiles.add(resultFile);
                            fillPlanFiles();

                        } else {
                            Log.e(TAG, "Null plan file ....");
                            tmpPlanFiles.add(planFile);
                            fillPlanFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        tmpPlanFiles.add(planFile);
                        fillPlanFiles();
                    }
                });
            }else{
                tmpPlanFiles.add(planFile);
                fillPlanFiles();
            }
        }else {
            titularDoc.planFiles.addAll(tmpPlanFiles);
            tmpPlanFiles = new ArrayList<AttachFile>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initialize();
                }
            });
        }
    }

    private void initialize(){
        initializeForm();
        setupListeners();
        ((AffiliationActivity) getActivity()).setActionButtons(true);
    }

    private void initializeForm(){

        Log.e(TAG, "initializeForm!!!!!:");
        mScrollView.smoothScrollTo(0, 0);

        initDNIFrontFile();
        initDNIBackFile();
        initCuilFile();
        initIvaFile();
        initCoverageFile();
        initPlanFile();

        // CUIL LOGIC
        mCuilBox =  (RelativeLayout) view.findViewById(R.id.cuil_box);
        mYesCuilDorsoRadioButton = (RadioButton) view.findViewById(R.id.yes_cuil_dorso_button);
        mNoCuilDorsoRadioButton = (RadioButton) view.findViewById(R.id.no_cuil_dorso_button);
        mCuilAnsesLink = (TextView) view.findViewById(R.id.cuil_anses_link);

        mPlanCodeFormEditTxt = (EditText)view.findViewById(R.id.plan_form_code_input);
        setTypeTextNoSuggestions(mPlanCodeFormEditTxt);

        mBarCodeButton = (Button) view.findViewById(R.id.barcode_button);

        if(titularDoc.cuilInFront) {
            mYesCuilDorsoRadioButton.setChecked(true);
            mNoCuilDorsoRadioButton.setChecked(false);
            mCuilBox.setVisibility(View.GONE);
            mCuilAnsesLink.setVisibility(View.GONE);

        }else {
            mYesCuilDorsoRadioButton.setChecked(false);
            mNoCuilDorsoRadioButton.setChecked(true);
            mCuilBox.setVisibility(View.VISIBLE);
            mCuilAnsesLink.setVisibility(View.VISIBLE);
        }

        // IVA_LOGIC
        mIvaBox =  (RelativeLayout) view.findViewById(R.id.iva_box);
        if (titularCondIva != null &&  !titularCondIva.id.equals(ConstantsUtil.CONDICION_IVA_CONSUMIDOR_FINAL)){
            mIvaBox.setVisibility(View.VISIBLE);
        }else{
            mIvaBox.setVisibility(View.GONE);
        }

        checkEditableCardMode();

    }

    private void initDNIFrontFile(){
        mDNIFrontFileAdapter = new AttachFilesAdapter(titularDoc.dniFrontFiles, true);

        mDNIiFrontRecyclerView = (RecyclerView) view.findViewById(R.id.dni_front_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mDNIiFrontRecyclerView.getContext());
        mDNIiFrontRecyclerView.setLayoutManager(attachLayoutManager);
        mDNIiFrontRecyclerView.setAdapter(mDNIFrontFileAdapter);
        mDNIiFrontRecyclerView.setHasFixedSize(true);
        addDNIFrontButton = (Button) view.findViewById(R.id.dni_front_button);

    }

    private void initDNIBackFile(){
        mDNIBackFileAdapter = new AttachFilesAdapter(titularDoc.dniBackFiles, true);

        mDNIiBackRecyclerView = (RecyclerView) view.findViewById(R.id.dni_back_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mDNIiBackRecyclerView.getContext());
        mDNIiBackRecyclerView.setLayoutManager(attachLayoutManager);
        mDNIiBackRecyclerView.setAdapter(mDNIBackFileAdapter);
        mDNIiBackRecyclerView.setHasFixedSize(true);
        addDNIBackButton = (Button) view.findViewById(R.id.dni_back_button);
    }

    private void initCuilFile(){
        mCuilFileAdapter = new AttachFilesAdapter(titularDoc.cuilFiles, true);

        mCuilRecyclerView = (RecyclerView) view.findViewById(R.id.cuil_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCuilRecyclerView.getContext());
        mCuilRecyclerView.setLayoutManager(attachLayoutManager);
        mCuilRecyclerView.setAdapter(mCuilFileAdapter);
        mCuilRecyclerView.setHasFixedSize(true);
        addCuilButton = (Button) view.findViewById(R.id.cuil_button);
    }

    private void initIvaFile(){
        mIvaFileAdapter = new AttachFilesAdapter(titularDoc.ivaFiles, true);

        mIvaRecyclerView = (RecyclerView) view.findViewById(R.id.iva_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mIvaRecyclerView.getContext());
        mIvaRecyclerView.setLayoutManager(attachLayoutManager);
        mIvaRecyclerView.setAdapter(mIvaFileAdapter);
        mIvaRecyclerView.setHasFixedSize(true);
        addIvaButton = (Button) view.findViewById(R.id.iva_button);
    }


    private void initCoverageFile(){

        mCoverageBox = (RelativeLayout)view.findViewById(R.id.coverage_box);
        mCoverageFileAdapter = new AttachFilesAdapter(titularDoc.coverageFiles, true);

        mCoverageRecyclerView = (RecyclerView) view.findViewById(R.id.coverage_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCoverageRecyclerView.getContext());
        mCoverageRecyclerView.setLayoutManager(attachLayoutManager);
        mCoverageRecyclerView.setAdapter(mCoverageFileAdapter);
        mCoverageRecyclerView.setHasFixedSize(true);
        addCoverageButton = (Button) view.findViewById(R.id.coverage_button);

        if (titularCobertura == null ){
            mCoverageBox.setVisibility(View.GONE);
            mCoverageFileAdapter.removeAllItems();
        }else{
            mCoverageBox.setVisibility(View.VISIBLE);
        }
    }

    private void initPlanFile(){
        mPlanFileAdapter = new AttachFilesAdapter(titularDoc.planFiles, true);

        mPlanRecyclerView = (RecyclerView) view.findViewById(R.id.plan_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mPlanRecyclerView.getContext());
        mPlanRecyclerView.setLayoutManager(attachLayoutManager);
        mPlanRecyclerView.setAdapter(mPlanFileAdapter);
        mPlanRecyclerView.setHasFixedSize(true);
        addPlanButton = (Button) view.findViewById(R.id.plan_button);
    }

    private void checkEditableCardMode() {

        mYesCuilDorsoRadioButton.setEnabled(editableCard);
        mNoCuilDorsoRadioButton.setEnabled(editableCard);
        mPlanCodeFormEditTxt.setEnabled(editableCard);

        if (!editableCard){
            disableView(addDNIFrontButton);
            disableView(addDNIBackButton);
            disableView(addCuilButton);
            disableView(addIvaButton);
            disableView(addCoverageButton);
            disableView(addPlanButton);

            mBarCodeButton.setVisibility(View.GONE);
        }
    }


    private boolean validateForm(){
        return  true;
    }


    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) view.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private boolean validatePlan() {
        boolean isValid = true;

        isValid = isValid & validateField(mPlanCodeFormEditTxt, R.string.affiliation_titular_plan_form_code_error, R.id.plan_form_code_wrapper);
        return isValid;
    }



    private void setupListeners() {

        setupImageProvider();


        // TODO TEMP COMENTED
        //disableAddPlanButton();

        if (editableCard) {

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
                    //IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.activity_anses), ConstantsUtil.ANSES_CUIL_LINK);

                    if (ansesLink == null  || ansesLink.isEmpty()){
                        showProgressDialog(R.string.downloading_link);

                        CardController.getInstance().getLinkRequest(ConstantsUtil.LINK_ID_ANSES_CUIL, new Response.Listener<LinkData>() {
                            @Override
                            public void onResponse(LinkData responseLink) {
                                dismissProgressDialog();

                                if (responseLink != null) {
                                    ansesLink = responseLink.link;
                                    IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.activity_anses), ansesLink);
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
                        IntentHelper.goToLinkDetailActivity(getActivity(), getResources().getString(R.string.activity_anses), ansesLink);
                    }
                }
            });


            // buttons
            addDNIFrontButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = DNI_FRONT_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + DNI_FRONT_FILE_PREFIX, DNI_FRONT_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addDNIBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            addIvaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = IVA_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + IVA_FILE_PREFIX, IVA_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addCoverageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = COVERAGE_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + COVERAGE_FILE_PREFIX, COVERAGE_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            // TODO
            //addPlanButton  need to verify code to operate

            // TODO TEMP
            addPlanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = PLAN_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + PLAN_FILE_PREFIX, PLAN_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });


            // ADAPTERS
            mDNIFrontFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "dniFrontFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = DNI_FRONT_FILE_PREFIX;
                    AttachFile dniFrontFile = titularDoc.dniFrontFiles.get(position);
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
                    AttachFile dniBackFile = titularDoc.dniBackFiles.get(position);
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
                    AttachFile cuilFile = titularDoc.cuilFiles.get(position);
                    if (cuilFile.id != -1) {
                        removeFile(cuilFile, position);
                    }
                }
            });
            mIvaFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "ivaFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = IVA_FILE_PREFIX;
                    AttachFile ivaFile = titularDoc.ivaFiles.get(position);
                    if (ivaFile.id != -1) {
                        removeFile(ivaFile, position);
                    }
                }
            });
            mCoverageFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "coverageFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = COVERAGE_FILE_PREFIX;
                    AttachFile coverageFile = titularDoc.coverageFiles.get(position);
                    if (coverageFile.id != -1) {
                        removeFile(coverageFile, position);
                    }
                }
            });
            mPlanFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "planFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = PLAN_FILE_PREFIX;
                    AttachFile planFile = titularDoc.planFiles.get(position);
                    if (planFile.id != -1) {
                        removeFile(planFile, position);
                    }
                }
            });


            mBarCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new IntentIntegrator(getActivity()).initiateScan();

                    // TODO TEMP COMMENT
                    /*
                    if (validatePlan()){
                        new IntentIntegrator(getActivity()).initiateScan();
                    }else{
                        disableAddPlanButton();
                    }
                    */
                }
            });
        }

        // ADAPTERS
        mDNIFrontFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile dniFrontFile = titularDoc.dniFrontFiles.get(position);
                Log.e(TAG, "dniFrontFile path:" + dniFrontFile.filePath);
                loadFile(dniFrontFile);
            }
        });
        mDNIBackFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile dniBackFile = titularDoc.dniBackFiles.get(position);
                Log.e(TAG, "dniBackFile path:" + dniBackFile.filePath);
                loadFile(dniBackFile);
            }
        });
        mCuilFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile cuilFile = titularDoc.cuilFiles.get(position);
                Log.e(TAG, "cuilFile path:" + cuilFile.filePath);
                loadFile(cuilFile);
            }
        });
        mIvaFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile ivaFile = titularDoc.ivaFiles.get(position);
                Log.e(TAG, "ivaFile path:" + ivaFile.filePath);
                loadFile(ivaFile);
            }
        });
        mCoverageFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile coverageFile = titularDoc.coverageFiles.get(position);
                Log.e(TAG, "coverageFile path:" + coverageFile.filePath);
                loadFile(coverageFile);
            }
        });
        mPlanFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile planFile = titularDoc.planFiles.get(position);
                Log.e(TAG, "planFile path:" + planFile.filePath);
                loadFile(planFile);
            }
        });
    }

    private void enableAddPlanButton(){
        enableView(addPlanButton);
        addPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFileType = PLAN_FILE_PREFIX;
                mImageProvider.showImagePicker(attachFilesSubDir + "/" + PLAN_FILE_PREFIX, PLAN_FILE_PREFIX + FileHelper.getFilePrefix());
            }
        });
    }
    private void disableAddPlanButton(){
        disableView(addPlanButton);
        addPlanButton.setOnClickListener(null);
    }
}
