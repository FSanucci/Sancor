package ar.com.sancorsalud.asociados.activity.affiliation;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.activity.main.SalesmanMainActivity;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1AutonomoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1AutonomoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1DesreguladoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1DesreguladoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1MonotributoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData1MonotributoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2AutonomoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2AutonomoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2DesreguladoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2DesreguladoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2MonotributoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData2MonotributoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData3DesreguladoEmpresaFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationAdditionalData3DesreguladoIndividualFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationTitularAddressDataFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationTitularContactDataFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationMembersDataFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationPlanDataFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationTitularDataFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationTitularDocFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.AffiliationWorkflowFragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.IAffiliationAdditionalData1Fragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.IAffiliationAdditionalData2Fragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.IAffiliationAdditionalData3Fragment;
import ar.com.sancorsalud.asociados.fragments.affiliation.UpdateServiceCallback;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2Monotributo;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3Desregulado;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationDataResult;
import ar.com.sancorsalud.asociados.model.affiliation.BeneficiarioSUF;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData3;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.MembersData;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.affiliation.Person;
import ar.com.sancorsalud.asociados.model.affiliation.Recotizacion;
import ar.com.sancorsalud.asociados.model.affiliation.TicketPago;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;

/*
 * Load all data when user has been quotated.
 * Fill many forms to affiliate user to System
 */
public class AffiliationActivity extends BaseActivity {

    private static final String TAG = "AFFIL_ACT";

    private ImageView mDopPage1;
    private ImageView mDopPage2;
    private ImageView mDopPage3;
    private ImageView mDopPage4;
    private ImageView mDopPage5;
    private ImageView mDopPage6;
    private ImageView mDopPage7;
    private ImageView mDopPage8;
    private ImageView mDopPage9;
    private ImageView mDopPage10;

    private View mNextButton;
    private View mPrevButton;
    private TextView prevText;
    private int mCurrentPage = 0;

    private AffiliationTitularDataFragment mTitularDataFragment;
    private AffiliationTitularAddressDataFragment mAddressDataFragment;
    private AffiliationTitularContactDataFragment mContactDataFragment;
    private AffiliationTitularDocFragment mTitularDocFragment;
    private AffiliationMembersDataFragment mMemberDataFragment;

    private IAffiliationAdditionalData1Fragment mAdditionalData1Fragment;
    private IAffiliationAdditionalData2Fragment mAdditionalData2Fragment;
    private IAffiliationAdditionalData3Fragment mAdditionalData3Fragment;

    private AffiliationPlanDataFragment mPlanDataFragment;
    private AffiliationWorkflowFragment mWorkflowFragment;

    private AffiliationCard mAffiliationCard;
    private long mPAId;
    private View mProgressView;
    //private RelativeLayout affiliationBox;

    //private SimpleDateFormat mDateFormat;
    private boolean editableCard = true;
    private ConstantsUtil.Segmento mSegmento;
    private ConstantsUtil.FormaIngreso mIngreso;

    private BaseFragment mCurrentFragment;
    private boolean hasLoadEntidadesEmpleadoras = false;

    private boolean loadFromQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiliation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_affiliation);
        //mDateFormat = new SimpleDateFormat(DATE_FORMAT);

        if (getIntent().getExtras() != null) {
            mAffiliationCard = (AffiliationCard) getIntent().getSerializableExtra(ConstantsUtil.AFFILIATION);
            mPAId = getIntent().getExtras().getLong(ConstantsUtil.PA_ID, -1);
            loadFromQR = getIntent().getExtras().getBoolean(ConstantsUtil.LOAD_FROM_QR);
            Log.e(TAG, "PA_ID: " + mPAId + "  ************************");

            //affiliationBox = (RelativeLayout) findViewById(R.id.affiliation_box);
            mMainContainer = findViewById(R.id.affiliation_box);


            mNextButton = findViewById(R.id.next_button);
            mPrevButton = findViewById(R.id.prev_button);
            prevText = (TextView) findViewById(R.id.prev_title);

            mDopPage1 = (ImageView) findViewById(R.id.page1);
            mDopPage2 = (ImageView) findViewById(R.id.page2);
            mDopPage3 = (ImageView) findViewById(R.id.page3);
            mDopPage4 = (ImageView) findViewById(R.id.page4);
            mDopPage5 = (ImageView) findViewById(R.id.page5);
            mDopPage6 = (ImageView) findViewById(R.id.page6);
            mDopPage7 = (ImageView) findViewById(R.id.page7);
            mDopPage8 = (ImageView) findViewById(R.id.page8);
            mDopPage9 = (ImageView) findViewById(R.id.page9);
            mDopPage10 = (ImageView) findViewById(R.id.page10);

            if (mAffiliationCard.members.size() > 0) {
                mDopPage5.setVisibility(View.VISIBLE);
            } else {
                mDopPage5.setVisibility(View.GONE);
            }

            mSegmento = ((mAffiliationCard == null) ? null : mAffiliationCard.titularData.getSegmento());
            if (mSegmento == null) {
                return;
            }

            mIngreso = ((mAffiliationCard == null) ? null : mAffiliationCard.titularData.getFormaIngreso());
            if (mIngreso == null) {
                return;
            }

            if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && mAffiliationCard.additionalData3 != null && mAffiliationCard.additionalData3.getOsMonotributo() != null) {
                mDopPage8.setVisibility(View.VISIBLE);
            } else {
                mDopPage8.setVisibility(View.GONE);
            }

            mProgressView = findViewById(R.id.progress);
            editableCard = Storage.getInstance().isCardEditableMode();
            Log.e(TAG, "editableCard: " + editableCard);

            setupListeners();
            showFirstPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void toMain() {
        Intent intent = new Intent(this, SalesmanMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle params = new Bundle();
        params.putBoolean(ConstantsUtil.RELOAD_DATA, true);
        params.putBoolean(ConstantsUtil.CARDS_IN_PROCESS, true);
        intent.putExtras(params);

        startActivity(intent);
        finish();
    }

    private void setupListeners() {

        // On next we check valid form and persist data
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Cancel Previous requests
                AppController.getInstance().getRestEngine().cancelPendingRequests();

                if (mCurrentPage == 0) { // P1
                    if (mTitularDataFragment.isValidSection()) {
                        mAffiliationCard.titularData = mTitularDataFragment.getTitularData();

                        mTitularDataFragment.updateEntity(new Response.Listener<QuoteOption>() {
                            @Override
                            public void onResponse(QuoteOption response) {
                                Log.e(TAG, "mTitularDataFragment entity onResponse....");
                                if (response != null) {
                                    mAffiliationCard.titularData.entity = response;
                                }
                                mTitularDataFragment.updateDatero(new Response.Listener<QuoteOption>() {
                                    @Override
                                    public void onResponse(QuoteOption responseDatero) {
                                        if (responseDatero != null) {
                                            mAffiliationCard.titularData.dateroNumber = responseDatero;
                                        }
                                        updateAffilliationData(true);
                                    }
                                });
                                //updateAffilliationData(true);
                            }
                        });
                    }

                } else if (mCurrentPage == 1) { // P2
                    if (mAddressDataFragment.isValidSection()) {
                        mAffiliationCard.titularAddress = mAddressDataFragment.getAddressData();
                        updateAffilliationData(true);
                    }

                } else if (mCurrentPage == 2) { // P3
                    if (mContactDataFragment.isValidSection()) {
                        mAffiliationCard.contactData = mContactDataFragment.getContactData();
                        updateAffilliationData(true);
                    }
                } else if (mCurrentPage == 3) { // P4
                    if (mTitularDocFragment.isValidSection()) {
                        mAffiliationCard.document = mTitularDocFragment.getDocument();
                        updateAffilliationData(true);
                    }
                } else if (mCurrentPage == 4) { // P5
                    if (mMemberDataFragment.isValidSection()) {
                        mAffiliationCard.members = mMemberDataFragment.getMemebersData().members;
                        updateAffilliationData(true);
                    }
                } else if (mCurrentPage == 5) { // P6
                    if (mAdditionalData1Fragment.isValidSection()) {
                        mAffiliationCard.additionalData1 = mAdditionalData1Fragment.getAdditionalData1();
                        if (mAdditionalData1Fragment.isValidSection()) {
                            updateAdditionalData1(mAffiliationCard.additionalData1, true);
                        }
                    }
                } else if (mCurrentPage == 6) { // P7
                    if (mAdditionalData2Fragment.isValidSection()) {
                        mAffiliationCard.additionalData2 = mAdditionalData2Fragment.getAdditionalData2();
                        updateAdditionalData2(mAffiliationCard.additionalData2, true);

                    }
                } else if (mCurrentPage == 7) { // P8
                    if (mAdditionalData3Fragment.isValidSection()) {
                        mAffiliationCard.additionalData3 = mAdditionalData3Fragment.getAdditionalData3();
                        updateAdditionalData3(mAffiliationCard.additionalData3, true);
                    }
                } else if (mCurrentPage == 8) { // P9
                    if (mWorkflowFragment.isValidSection()) {
                        onMove(true);
                    }
                }

            }
        });

        // onprev just go to prev screen with no validation and update data
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Cancel Previous requests
                AppController.getInstance().getRestEngine().cancelPendingRequests();

                if (mCurrentPage == 0) {
                    finish();
                } else if (mCurrentPage == 1) {  // P2
                    mAffiliationCard.titularAddress = mAddressDataFragment.getAddressData();
                    updateAffilliationData(false);
                } else if (mCurrentPage == 2) { // P3
                    mAffiliationCard.contactData = mContactDataFragment.getContactData();
                    updateAffilliationData(false);

                } else if (mCurrentPage == 3) { // P4
                    mAffiliationCard.document = mTitularDocFragment.getDocument();
                    updateAffilliationData(false);

                } else if (mCurrentPage == 4) { // P5
                    mAffiliationCard.members = mMemberDataFragment.getMemebersData().members;
                    updateAffilliationData(false);

                } else if (mCurrentPage == 5) { // P6
                    mAffiliationCard.additionalData1 = mAdditionalData1Fragment.getAdditionalData1();
                    updateAdditionalData1(mAffiliationCard.additionalData1, false);

                } else if (mCurrentPage == 6) { // P7
                    mAffiliationCard.additionalData2 = mAdditionalData2Fragment.getAdditionalData2();
                    updateAdditionalData2(mAffiliationCard.additionalData2, false);

                } else if (mCurrentPage == 7) { // P8
                    Log.e(TAG, "mAdditionalData3Fragment!!!!!: ");
                    mAffiliationCard.additionalData3 = mAdditionalData3Fragment.getAdditionalData3();
                    updateAdditionalData3(mAffiliationCard.additionalData3, false);

                } else if (mCurrentPage == 8) { // P9
                    // workflow screen
                    onMove(false);
                } else if (mCurrentPage == 9) { // P9
                    // just print plan no need to update
                    onMove(false);
                }
            }
        });
    }


    private void highlightDot(ImageView dot) {

        mDopPage1.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage2.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage3.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage4.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage5.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage6.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage7.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage8.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage9.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));
        mDopPage10.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorDarkGrey));

        dot.setColorFilter(ContextCompat.getColor(AffiliationActivity.this, R.color.colorAccent));
    }

    private void showFirstPage() {
        mTitularDataFragment = AffiliationTitularDataFragment.newInstance(mAffiliationCard.titularData, mAffiliationCard.id);
        mTitularDataFragment.setLoadFromQR(loadFromQR);
        mCurrentPage = 0;
        switchToFragment(mTitularDataFragment);
    }

    private void showSecondPage() {
        mAddressDataFragment = AffiliationTitularAddressDataFragment.newInstance(mAffiliationCard.titularAddress, mAffiliationCard.id);
        mCurrentPage = 1;
        switchToFragment(mAddressDataFragment);
    }

    private void showThirdPage() {
        mContactDataFragment = AffiliationTitularContactDataFragment.newInstance(mAffiliationCard.contactData);
        mCurrentPage = 2;
        switchToFragment(mContactDataFragment);
    }

    private void showFourPage() {
        mTitularDocFragment = AffiliationTitularDocFragment.newInstance(mAffiliationCard.document, mAffiliationCard.titularData.dni, mAffiliationCard.titularData.condicionIva, mAffiliationCard.titularData.coberturaProveniente);
        mCurrentPage = 3;
        switchToFragment(mTitularDocFragment);
    }

    private void showFivePage() {
        MembersData membersData = getMembersData();
        mMemberDataFragment = AffiliationMembersDataFragment.newInstance(membersData, mAffiliationCard.titularData.dni, mAffiliationCard.titularData.aportaMonotributo);
        mCurrentPage = 4;
        switchToFragment(mMemberDataFragment);
    }

    private void showSixPage() {
        String sFechaInicioServicio = ParserUtils.parseDate(mAffiliationCard.titularData.fechaInicioServicio, "yyyy-MM-dd");

        if ((mSegmento == ConstantsUtil.Segmento.AUTONOMO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            Log.e(TAG, "PLAN ID:" + mAffiliationCard.plan.idPlan + "Valor " + mAffiliationCard.plan.valor);
            mAdditionalData1Fragment = AffiliationAdditionalData1AutonomoIndividualFragment.newInstance((AdditionalData1AutonomoIndividual) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, sFechaInicioServicio);

        } else if ((mSegmento == ConstantsUtil.Segmento.AUTONOMO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData1Fragment = AffiliationAdditionalData1AutonomoEmpresaFragment.newInstance((AdditionalData1AutonomoEmpresa) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, mAffiliationCard.titularData.nombreEmpresa, sFechaInicioServicio);

        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData1Fragment = AffiliationAdditionalData1DesreguladoIndividualFragment.newInstance((AdditionalData1DesreguladoIndividual) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, sFechaInicioServicio);

        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData1Fragment = AffiliationAdditionalData1DesreguladoEmpresaFragment.newInstance((AdditionalData1DesreguladoEmpresa) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, mAffiliationCard.titularData.nombreEmpresa, sFechaInicioServicio);

        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData1Fragment = AffiliationAdditionalData1MonotributoIndividualFragment.newInstance((AdditionalData1MonotributoIndividual) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, sFechaInicioServicio);

        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData1Fragment = AffiliationAdditionalData1MonotributoEmpresaFragment.newInstance((AdditionalData1MonotributoEmpresa) mAffiliationCard.additionalData1, mAffiliationCard.titularData.dni, mAffiliationCard.id, mAffiliationCard.titularData.cuil, mAffiliationCard.titularData.nombreEmpresa, sFechaInicioServicio);
        }

        mCurrentPage = 5;
        switchToFragment((BaseFragment) mAdditionalData1Fragment);
    }


    private void showSevenPage() {
        long titularCardId = mAffiliationCard.titularData.personCardId;
        long conyugeCardId = -1L;

        Member conyugeMember = mAffiliationCard.getConyuge();
        if (conyugeMember == null)
            conyugeMember = mAffiliationCard.getConcubino();

        if (conyugeMember != null) {
            conyugeCardId = conyugeMember.personCardId;
        }

        if ((mSegmento == ConstantsUtil.Segmento.AUTONOMO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2AutonomoIndividualFragment.newInstance((AdditionalData2AutonomoIndividual) mAffiliationCard.additionalData2, mAffiliationCard.titularData, conyugeMember);

        } else if ((mSegmento == ConstantsUtil.Segmento.AUTONOMO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2AutonomoEmpresaFragment.newInstance((AdditionalData2AutonomoEmpresa) mAffiliationCard.additionalData2, mAffiliationCard.titularData, conyugeMember);

        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2DesreguladoIndividualFragment.newInstance((AdditionalData2DesreguladoIndividual) mAffiliationCard.additionalData2, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId, conyugeCardId, mAffiliationCard.conyugeData);

        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2DesreguladoEmpresaFragment.newInstance((AdditionalData2DesreguladoEmpresa) mAffiliationCard.additionalData2, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId, conyugeCardId, mAffiliationCard.conyugeData);

        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2MonotributoIndividualFragment.newInstance((AdditionalData2MonotributoIndividual) mAffiliationCard.additionalData2, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId, conyugeCardId, mAffiliationCard.conyugeData);

        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData2Fragment = AffiliationAdditionalData2MonotributoEmpresaFragment.newInstance((AdditionalData2MonotributoEmpresa) mAffiliationCard.additionalData2, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId, conyugeCardId, mAffiliationCard.conyugeData);
        }

        mCurrentPage = 6;
        switchToFragment((BaseFragment) mAdditionalData2Fragment);
    }

    private void showEightPage() {
        long titularCardId = mAffiliationCard.titularData.personCardId;

        if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            mAdditionalData3Fragment = AffiliationAdditionalData3DesreguladoIndividualFragment.newInstance((AdditionalData3DesreguladoIndividual) mAffiliationCard.additionalData3, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId);
        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            mAdditionalData3Fragment = AffiliationAdditionalData3DesreguladoEmpresaFragment.newInstance((AdditionalData3DesreguladoEmpresa) mAffiliationCard.additionalData3, mAffiliationCard.titularData.dni, mAffiliationCard.id, titularCardId);
        }

        mCurrentPage = 7;
        switchToFragment((BaseFragment) mAdditionalData3Fragment);
    }

    private void showNinePage() {
        mWorkflowFragment = AffiliationWorkflowFragment.newInstance(mPAId);
        mCurrentPage = 8;
        switchToFragment(mWorkflowFragment);
    }

    private void showTenPage() {
        String fechaInicioServicio = ParserUtils.parseDate(mAffiliationCard.titularData.fechaInicioServicio, DATE_FORMAT);
        Log.e(TAG, "Fecha Ini serv: " + fechaInicioServicio);

        mPlanDataFragment = AffiliationPlanDataFragment.newInstance(mAffiliationCard.id, fechaInicioServicio, mAffiliationCard.ticketPago, mAffiliationCard.plan);
        mCurrentPage = 9;
        switchToFragment(mPlanDataFragment);
    }

    private void switchToFragment(BaseFragment fragment) {
        mCurrentFragment = fragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }

    private void updateButtonVisivility() {
        if (mCurrentPage == 0) {
            mPrevButton.setVisibility(View.VISIBLE);
            prevText.setText(getResources().getString(R.string.quote_back));
            mNextButton.setVisibility(View.VISIBLE);
        } else if (mCurrentPage == 9) {
            mPrevButton.setVisibility(View.VISIBLE);
            prevText.setText(getResources().getString(R.string.affiliation_prev));

            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            prevText.setText(getResources().getString(R.string.affiliation_prev));
            mPrevButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    private MembersData getMembersData() {
        MembersData membersData = new MembersData();
        membersData.members = mAffiliationCard.members;

        return membersData;
    }

    public void silenceUpdateAffiliationData(Recotizacion mRecotizacion, Date fechaInicioServicio, final UpdateServiceCallback callback) {
        if (mRecotizacion != null) {
            mAffiliationCard.idCotizacion = mRecotizacion.idCotizacion;
        }
        mAffiliationCard.titularData.fechaInicioServicio = fechaInicioServicio;
        silenceUpdateAffiliationData(callback);
    }

    public void silenceUpdateAffiliationData(Recotizacion mRecotizacion, Date fechaInicioServicio, TicketPago ticketPago, final UpdateServiceCallback callback) {
        if (mRecotizacion != null) {
            mAffiliationCard.idCotizacion = mRecotizacion.idCotizacion;
        }
        mAffiliationCard.titularData.fechaInicioServicio = fechaInicioServicio;
        mAffiliationCard.ticketPago = ticketPago;
        silenceUpdateAffiliationData(callback);
    }

    public void silenceUpdateAffiliationData(final UpdateServiceCallback callback) {

        CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
            @Override
            public void onResponse(AffiliationDataResult dataResult) {
                if (dataResult != null) {
                    Log.e(TAG, "updating affiliationCard data ok .....ID: " + dataResult.cardId);
                } else {
                    Log.e(TAG, "error updating Aff Card" + dataResult.cardId);
                }
                callback.onSuccesUpdatedData();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "updating affiliationCard data error .....");
                Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                callback.onErrorUpdatedData();
            }
        });
    }

    private void updateAffilliationData(final Boolean toNext) {
        setActionButtons(false);

        if (editableCard && toNext) {
            if (AppController.getInstance().isNetworkAvailable()) {
                showProgress(true);
                CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
                    @Override
                    public void onResponse(AffiliationDataResult dataResult) {
                        showProgress(false);
                        mNextButton.setEnabled(true);
                        mPrevButton.setEnabled(true);

                        if (dataResult != null) {
                            Log.e(TAG, "affiliationCard data ok .....ID: " + dataResult.cardId);
                            updateAffiliationCard(dataResult);

                            if ((mSegmento != ConstantsUtil.Segmento.AUTONOMO) && (!hasLoadEntidadesEmpleadoras)) {
                                loadEntidadesEmpleadoras(toNext);
                            } else {
                                onMove(toNext);
                            }

                        } else {
                            Log.e(TAG, "error Aff Card" + dataResult.cardId);
                            SnackBarHelper.makeError(mMainContainer, R.string.error_affiliation_request).show();
                            //onMove(toNext); // bloq next
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        setActionButtons(true);
                        Log.e(TAG, "updating affiliationCard data error!!! .....");
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        SnackBarHelper.makeError(mMainContainer, R.string.error_affiliation_request).show();
                        //onMove(toNext);  // bloq next
                    }
                });
            } else {
                Log.e(TAG, "Error No internet cnx ...");
                setActionButtons(true);
                SnackBarHelper.makeError(mMainContainer, R.string.no_internet_coneccion).show();
                //onMove(toNext); // bloq next
            }
        } else {
            setActionButtons(true);
            onMove(toNext);
        }
    }

    private void updateAdditionalData1(IAdditionalData1 additionalData1, Boolean toNext) {
        setActionButtons(false);
        if (editableCard) {
            addFormaPago(additionalData1, toNext);
        } else {
            onMove(toNext);
        }
    }


    // ---- LAST PAGE 6 --------------------------------------------------------------------- //
    private void addFormaPago(final IAdditionalData1 additionalData1, final Boolean toNext) {

        Log.e(TAG, "updateFormaPago PAGO ID : " + mAffiliationCard.additionalData1.getPago().id);

        if (AppController.getInstance().isNetworkAvailable()) {
/************************************ UpdateFormaPago (OLD) ****************************************
            showProgress(true);
            CardController.getInstance().updateFormaPago(additionalData1.getPago(), new Response.Listener<Pago>() {
                @Override
                public void onResponse(Pago pagoResult) {
                    showProgress(false);
                    onMove(toNext);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Log.e(TAG, "FormaDePago  error: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    // NO Need to persist EE , is already persisted in detail screen in its own table/service
                    // UpdateFicha not save EE data only reads it

                    showMessageWithTitle(getResources().getString(R.string.app_name), error.getMessage(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    //onMove(toNext);
                                    showProgress(false);
                                    setActionButtons(true);

                                    if ((toNext != null) && !toNext) {
                                        toBackPage();
                                    }
                                }
                            });
                }
            });
***************************************************************************************************/

            sendFormaPago(additionalData1,toNext);

        } else {
            Log.e(TAG, "Error No internet cnx ...");
            SnackBarHelper.makeError(mMainContainer, R.string.no_internet_coneccion).show();
            onMove(toNext);
        }
    }

    //TODO ACOMODAR PARA QUE MANDE 2 VECES SI HAY COPAGOS
    private void sendFormaPago(final IAdditionalData1 additionalData1, final Boolean toNext){
        Log.e(TAG, "sendFormaPago() *********************");

        showProgress(true);
        CardController.getInstance().updateFormaPago(additionalData1.getPago(), new Response.Listener<Pago>() {
            @Override
            public void onResponse(Pago pagoResult) {
                //showProgress(false);
                //onMove(toNext);

                //additionalData1.getPago().id = pagoResult.id;
                //additionalData1.getPago().cardId = pagoResult.cardId;

                if (additionalData1.getPago().formaPago.id.equals(ConstantsUtil.EF_FORMA_PAGO)
                        && additionalData1.getCopagos() != null
                        && additionalData1.getCopagos().equals(ConstantsUtil.AFFILIACION_COPAGO_ASOCIADO)){

                    //final QuoteOption formaPagoAux = additionalData1.getPago().formaPago;
                    //additionalData1.getPago().formaPago = additionalData1.getPago().formaCopago;

                    additionalData1.getPago().type = "C";

                    CardController.getInstance().updateFormaPago(additionalData1.getPago(), new Response.Listener<Pago>() {
                        @Override
                        public void onResponse(Pago pagoResult) {

                            //additionalData1.getPago().idCopago = pagoResult.idCopago;
                            //additionalData1.getPago().cardIdCopago = pagoResult.cardId;

                            //additionalData1.getPago().formaPago = formaPagoAux;
                            showProgress(false);
                            onMove(toNext);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Log.e(TAG, "FormaDePago  error: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                            // NO Need to persist EE , is already persisted in detail screen in its own table/service
                            // UpdateFicha not save EE data only reads it
                            //additionalData1.getPago().formaPago = formaPagoAux;

                            showMessageWithTitle(getResources().getString(R.string.app_name), error.getMessage(),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            //onMove(toNext);
                                            showProgress(false);
                                            setActionButtons(true);

                                            if ((toNext != null) && !toNext) {
                                                toBackPage();
                                            }
                                        }
                                    });
                        }
                    });



                } else {
                    showProgress(false);
                    onMove(toNext);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "FormaDePago  error: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                // NO Need to persist EE , is already persisted in detail screen in its own table/service
                // UpdateFicha not save EE data only reads it

                showMessageWithTitle(getResources().getString(R.string.app_name), error.getMessage(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                //onMove(toNext);
                                showProgress(false);
                                setActionButtons(true);

                                if ((toNext != null) && !toNext) {
                                    toBackPage();
                                }
                            }
                        });
            }
        });

    }

    // ---- LAST PAGE 7 --------------------------------------------------------------------- //
    private void updateAdditionalData2(final IAdditionalData2 additionalData2, Boolean toNext) {
        Log.e(TAG, "updateAdditionalData2 ....");
        setActionButtons(false);

        if (editableCard) {
            showProgress(true);
            initializeBeneficiariosSufs(additionalData2);
            checkUpdateBeneficiarioSUF(additionalData2, toNext);
        } else {
            onMove(toNext);
        }
    }

    private void initializeBeneficiariosSufs(final IAdditionalData2 additionalData2) {
        mAffiliationCard.titularData.beneficiarioSUF = false;
        for (Member m : mAffiliationCard.members) {
            m.beneficiarioSUF = false;
        }

        List<BeneficiarioSUF> list = new ArrayList<BeneficiarioSUF>();
        if (additionalData2.getSufTitular() != null) {
            list.add(additionalData2.getSufTitular());
        }
        if (additionalData2.getSufConyuge() != null) {
            list.add(additionalData2.getSufConyuge());
        }

        if (list.size() > 0) {
            for (BeneficiarioSUF suf : list) {
                if (mAffiliationCard.titularData.dni == suf.dni) {
                    mAffiliationCard.titularData.beneficiarioSUF = true;
                } else {
                    for (Member m : mAffiliationCard.members) {
                        if (m.dni == suf.dni) {
                            m.beneficiarioSUF = true;
                        }
                    }
                }
            }
        }
    }

    private void checkUpdateBeneficiarioSUF(final IAdditionalData2 additionalData2, final Boolean toNext) {
        Log.e(TAG, "checkUpdateBeneficiarioSUF");

        if (additionalData2.hasToShowBeneficiarioSUF()) {
            if (AppController.getInstance().isNetworkAvailable()) {

                CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
                    @Override
                    public void onResponse(AffiliationDataResult dataResult) {
                        if (dataResult != null) {
                            Log.e(TAG, "updating affiliationCard data ok .....ID: " + dataResult.cardId);
                            updateAffiliationCard(dataResult);
                            checkUpdateObraSocial(additionalData2, toNext);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "updating affiliationCard data error .....");
                        checkUpdateObraSocial(additionalData2, toNext);
                    }
                });
            } else {
                checkUpdateObraSocial(additionalData2, toNext);
            }
        } else {
            checkUpdateObraSocial(additionalData2, toNext);
        }
    }

    private void checkUpdateObraSocial(final IAdditionalData2 additionalData2, final Boolean toNext) {
        Log.e(TAG, "checkUpdateObraSocial...");

        if (additionalData2.hasToShowObraSocial()) {

            final ObraSocial os = additionalData2.getObraSocial();


            if (os != null && AppController.getInstance().isNetworkAvailable()) {
                CardController.getInstance().updateObraSocialData(os, new Response.Listener<Long>() {
                    @Override
                    public void onResponse(Long osId) {
                        Log.e(TAG, "updating OS ok  ....");

                        if (osId != null) {
                            additionalData2.setObraSocialId(osId);
                        }
                        checkUpdateMonotributoFiles(additionalData2, toNext);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "updating OS data error .....");
                        SnackBarHelper.makeError(mMainContainer, R.string.error_os_request).show();

                        checkUpdateMonotributoFiles(additionalData2, toNext);
                    }
                });
            } else {
                checkUpdateMonotributoFiles(additionalData2, toNext);
            }
        } else {
            checkUpdateMonotributoFiles(additionalData2, toNext);
        }
    }

    private void checkUpdateMonotributoFiles(final IAdditionalData2 additionalData2, final Boolean toNext) {
        Log.e(TAG, "checkUpdateMonotributoFiles");

        if (additionalData2.hasToUpdateMonotributoFiles()) {

            AdditionalData2Monotributo m = (AdditionalData2Monotributo) additionalData2;
            mAffiliationCard.document.form184Files = m.form184Files;
            mAffiliationCard.document.threeMonthFiles = m.threeMonthFiles;

            if (AppController.getInstance().isNetworkAvailable()) {

                CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
                    @Override
                    public void onResponse(AffiliationDataResult dataResult) {
                        if (dataResult != null) {
                            Log.e(TAG, "updating affiliationCard for monotributo ok .....ID: " + dataResult.cardId);
                            onMove(toNext);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "updating affiliationCard for monotributo error .....");
                        onMove(toNext);
                    }
                });
            } else {
                onMove(toNext);
            }
        } else {
            onMove(toNext);
        }
    }

    // ---- LAST PAGE 8 --------------------------------------------------------------------- //
    private void updateAdditionalData3(final IAdditionalData3 additionalData3, Boolean toNext) {

        Log.e(TAG, "updateAdditionalData3 ....");
        setActionButtons(false);

        if (editableCard) {
            showProgress(true);
            updateOSMonotributo(additionalData3, toNext);
        } else {
            onMove(toNext);
        }
    }

    private void updateOSMonotributo(final IAdditionalData3 additionalData3, final Boolean toNext) {
        Log.e(TAG, "updateAdditionalData3...");

        final ObraSocial osMonotributo = additionalData3.getOsMonotributo();
        if (osMonotributo != null && AppController.getInstance().isNetworkAvailable()) {
            CardController.getInstance().updateObraSocialData(osMonotributo, new Response.Listener<Long>() {
                @Override
                public void onResponse(Long osId) {
                    Log.e(TAG, "updating osMonotributo ok  ....");

                    if (osId != null) {
                        additionalData3.setOSMonotributo(osId);
                    }
                    checkUpdateOsMonotributoFiles(additionalData3, toNext);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "updating osMonotributo data error .....");
                    SnackBarHelper.makeError(mMainContainer, R.string.error_monotributo_request).show();
                    checkUpdateOsMonotributoFiles(additionalData3, toNext);
                }
            });
        } else {
            checkUpdateOsMonotributoFiles(additionalData3, toNext);
        }
    }

    private void checkUpdateOsMonotributoFiles(final IAdditionalData3 additionalData3, final Boolean toNext) {
        Log.e(TAG, "checkUpdateMonotributoFiles");

        AdditionalData3Desregulado dr = (AdditionalData3Desregulado) additionalData3;
        mAffiliationCard.document.form184Files = dr.form184Files;
        mAffiliationCard.document.threeMonthFiles = dr.threeMonthFiles;

        if (AppController.getInstance().isNetworkAvailable()) {

            CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
                @Override
                public void onResponse(AffiliationDataResult dataResult) {
                    if (dataResult != null) {
                        Log.e(TAG, "updating affiliationCard for monotributo files ok .....ID: " + dataResult.cardId);
                        onMove(toNext);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "updating affiliationCard for monotributo files error .....");
                    onMove(toNext);
                }
            });
        } else {
            onMove(toNext);
        }
    }


    // --- CONYUGE DATA --------------------------------------------------------------------------------------//
    public void updateConyugeMonotributoFiles(List<AttachFile> conyugeForm184Files, List<AttachFile> conyugeThreeMonthFiles) {

        if (!conyugeForm184Files.isEmpty() || !conyugeThreeMonthFiles.isEmpty()) {
            Member conyuge = getConyugeMember();
            conyuge.conyugeForm184Files = conyugeForm184Files;
            conyuge.conyugeThreeMonthFiles = conyugeThreeMonthFiles;

            showProgress(true);
            if (AppController.getInstance().isNetworkAvailable()) {
                CardController.getInstance().updateAffilliationData(mAffiliationCard, new Response.Listener<AffiliationDataResult>() {
                    @Override
                    public void onResponse(AffiliationDataResult dataResult) {
                        showProgress(false);
                        if (dataResult != null) {
                            Log.e(TAG, "updating affiliationCard data ok  for conyuge mono files .....ID: " + dataResult.cardId);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.e(TAG, "updating affiliationCard data error  for conyuge mono files.....");
                    }
                });
            } else {
                showProgress(false);
            }
        }
    }

    public void setActionButtons(boolean val) {
        mNextButton.setEnabled(val);
        mPrevButton.setEnabled(val);
    }

    private void onMove(Boolean toNext) {
        showProgress(false);
        setActionButtons(true);

        if (toNext != null) {
            if (toNext) {
                toNextPage();
            } else {
                toBackPage();
            }
        }
    }

    private void toNextPage() {

        if (mCurrentPage == 0) { // P1
            highlightDot(mDopPage2);
            showSecondPage();
        } else if (mCurrentPage == 1) { // P2
            highlightDot(mDopPage3);
            showThirdPage();
        } else if (mCurrentPage == 2) { // P3
            highlightDot(mDopPage4);
            showFourPage();
        } else if (mCurrentPage == 3) { // P4
            // Bypass members if empty
            if (mAffiliationCard.members.size() > 0) {
                highlightDot(mDopPage5);
                showFivePage();
            } else {
                highlightDot(mDopPage6);
                showSixPage();
            }
        } else if (mCurrentPage == 4) { // P5
            highlightDot(mDopPage6);
            showSixPage();
        } else if (mCurrentPage == 5) { // P6
            if(AppController.getInstance().storage.getEntidadEmpleadoraAvailable()){
                highlightDot(mDopPage7);
                showSevenPage();
            }

        } else if (mCurrentPage == 6) { // P7
            if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && mAffiliationCard.additionalData3 != null && mAffiliationCard.additionalData3.getOsMonotributo() != null) {
                highlightDot(mDopPage8);
                showEightPage();
            } else {
                highlightDot(mDopPage9);
                showNinePage();
            }
        } else if (mCurrentPage == 7) { // P8
            highlightDot(mDopPage9);
            showNinePage();
        } else if (mCurrentPage == 8) { // P9
            highlightDot(mDopPage10);
            showTenPage();
        }

        updateButtonVisivility();
    }

    private void toBackPage() {

        if (mCurrentPage == 1) {  // P2
            highlightDot(mDopPage1);
            showFirstPage();
        } else if (mCurrentPage == 2) { // P3
            highlightDot(mDopPage2);
            showSecondPage();
        } else if (mCurrentPage == 3) { // P4
            highlightDot(mDopPage3);
            showThirdPage();
        } else if (mCurrentPage == 4) { // P5
            highlightDot(mDopPage4);
            showFourPage();
        } else if (mCurrentPage == 5) { // P6
            // Bypass members if empty
            if (mAffiliationCard.members.size() > 0) {
                highlightDot(mDopPage5);
                showFivePage();
            } else {
                highlightDot(mDopPage4);
                showFourPage();
            }
        } else if (mCurrentPage == 6) { // P7
            highlightDot(mDopPage6);
            showSixPage();
        } else if (mCurrentPage == 7) { // P8
            highlightDot(mDopPage7);
            showSevenPage();
        } else if (mCurrentPage == 8) { // P9
            if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && mAffiliationCard.additionalData3 != null && mAffiliationCard.additionalData3.getOsMonotributo() != null) {
                highlightDot(mDopPage8);
                showEightPage();
            } else {
                highlightDot(mDopPage7);
                showSevenPage();
            }
        } else if (mCurrentPage == 9) { // 10
            highlightDot(mDopPage9);
            showNinePage();
        }

        updateButtonVisivility();
    }

    private void updateAffiliationCard(AffiliationDataResult dataResult) {
        // update persons ids  only once

        if (mAffiliationCard.id == -1L) {
            Log.e(TAG, "updateAffiliationCard *********");
            mAffiliationCard.id = dataResult.cardId;

            // como la primera vez que se ingresa a la afiliacion del PA la ficha no existe,
            // cargo los certificados en el success del ficha guardar
            if (mTitularDataFragment != null && mCurrentPage == 0) {
                if (mAffiliationCard.titularData.getFormaIngreso().equals(ConstantsUtil.FormaIngreso.AFINIDAD)) {
                    mTitularDataFragment.addAffinityDocument(mAffiliationCard.id, false);
                }

                if (mAffiliationCard.titularData.getFormaIngreso().equals(ConstantsUtil.FormaIngreso.EMPRESA)) {
                    mTitularDataFragment.addAgreementDocument(mAffiliationCard.id, false);
                }
            }

            searchAndUpdateTitular(dataResult.personList, mAffiliationCard.titularData);

            // could be all nuleables
            for (Person person : dataResult.personList) {
                searchAndUpdateMember(mAffiliationCard.members, person);
            }
        }
    }

    private void searchAndUpdateTitular(List<Person> personList, TitularData titularData) {
        for (Person person : personList) {
            if (person.dni != -1 && titularData.dni == person.dni) {
                titularData.personCardId = person.personCardId;
                Log.e(TAG, "Founded !!! titularData.personCardId: " + titularData.personCardId + "-----------------------------");
                break;
            }
        }
    }

    private void searchAndUpdateMember(List<Member> memberList, Person person) {
        for (Member member : memberList) {
            if (!member.hasPersonCardId && (member.dni == person.dni)) { // could fill nulls when person have not dni, dni = -1 , titular will always has dni so not match in this for
                member.personCardId = person.personCardId;
                member.hasPersonCardId = true;
                Log.e(TAG, "Founded !!! member.personCardId: " + member.personCardId + " ***********************************");
                break;
            }
        }
    }

    private Member getConyugeMember() {
        Member conyuge = null;

        for (Member m : mAffiliationCard.members) {
            if (m.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || m.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER)) {
                conyuge = m;
                break;
            }
        }
        return conyuge;
    }

    private void loadEntidadesEmpleadoras(final Boolean toNext) {

        Log.e(TAG, "loadEntidadesEmpleadoras !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        CardController.getInstance().getEmpresaLaboralListData(mAffiliationCard.id, new Response.Listener<List<EntidadEmpleadora>>() {
            @Override
            public void onResponse(List<EntidadEmpleadora> entidadEmpleadoraList) {
                loadEntidadesEmpleadoras(entidadEmpleadoraList);
                hasLoadEntidadesEmpleadoras = true;
                onMove(toNext);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressView.setVisibility(View.GONE);
                hasLoadEntidadesEmpleadoras = false;
                Log.e(TAG, ((error != null && (error.getMessage() != null) ? error.getMessage() : "")));
                SnackBarHelper.makeError(mMainContainer, R.string.error_loading_ee).show();
                //onMove(toNext); // bloq

            }
        });
    }

    private void loadEntidadesEmpleadoras(List<EntidadEmpleadora> entidadEmpleadoraList) {

        if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            AdditionalData1DesreguladoIndividual additionalData1 = (AdditionalData1DesreguladoIndividual) mAffiliationCard.additionalData1;
            additionalData1.entidadEmpleadoraArray = entidadEmpleadoraList;
        } else if ((mSegmento == ConstantsUtil.Segmento.DESREGULADO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            AdditionalData1DesreguladoEmpresa additionalData1 = (AdditionalData1DesreguladoEmpresa) mAffiliationCard.additionalData1;
            additionalData1.entidadEmpleadoraArray = entidadEmpleadoraList;
        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || mIngreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
            AdditionalData1MonotributoIndividual additionalData1 = (AdditionalData1MonotributoIndividual) mAffiliationCard.additionalData1;
            additionalData1.entidadEmpleadoraArray = entidadEmpleadoraList;
        } else if ((mSegmento == ConstantsUtil.Segmento.MONOTRIBUTO) && (mIngreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
            AdditionalData1MonotributoEmpresa additionalData1 = (AdditionalData1MonotributoEmpresa) mAffiliationCard.additionalData1;
            additionalData1.entidadEmpleadoraArray = entidadEmpleadoraList;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
