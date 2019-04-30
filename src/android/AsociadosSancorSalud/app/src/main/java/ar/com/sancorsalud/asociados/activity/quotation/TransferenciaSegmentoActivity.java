package ar.com.sancorsalud.asociados.activity.quotation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.fragments.quote.DesreguladoAdditionalDataFragment;
import ar.com.sancorsalud.asociados.fragments.quote.IQuoteMember;
import ar.com.sancorsalud.asociados.fragments.quote.MonotributistaAdditionalDataFragment;
import ar.com.sancorsalud.asociados.fragments.quote.OSDesregulaFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteFormaPagoFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteMembersAutonomoFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteMembersNoAutonomoFragment;
import ar.com.sancorsalud.asociados.fragments.quote.client.ExistentClientDataFragment;
import ar.com.sancorsalud.asociados.fragments.quote.transfer.SelectTransferSegmentFragment;
import ar.com.sancorsalud.asociados.interfaces.QuoteListener;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.MembersAndUnificationData;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;

/*
 * Same as Quotation Activity  but change to new  segment
 *  With ExistentClientDataFragment we load actual quotation
 *  When user change segment we need to reset old data
 */
public class TransferenciaSegmentoActivity extends BaseActivity implements QuoteListener {

    private  static final String TAG = "TRANSFER_ACT";

    private ImageView mDopPage1;
    private ImageView mDopPage2;
    private ImageView mDopPage3;
    private ImageView mDopPage4;
    private ImageView mDopPage5;
    private ImageView mDopPage6;

    private View mNextButton;
    private View mPrevButton;

    private ExistentClientDataFragment mUserDataFragment;

    private SelectTransferSegmentFragment mSelectTransferSegmentFragment;
    private IQuoteMember mMembersFragment;
    private QuoteFormaPagoFragment mFormaPagoFragment;
    private OSDesregulaFragment mOSDesregulaFragment;

    private DesreguladoAdditionalDataFragment mDesreguladoFragment;
    private MonotributistaAdditionalDataFragment mMonitributistaFragment;

    private Quotation mQuotation;
    private Quotation mConyugeQuotation;

    private String mDni;

    private int mCurrentPage = 0;

    private BaseFragment mCurrentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_segmento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_quote);

        mMainContainer = findViewById(R.id.main);

        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);

        mDopPage1 = (ImageView) findViewById(R.id.page1);
        mDopPage2 = (ImageView) findViewById(R.id.page2);
        mDopPage3 = (ImageView) findViewById(R.id.page3);
        mDopPage4 = (ImageView) findViewById(R.id.page4);
        mDopPage5 = (ImageView) findViewById(R.id.page5);
        mDopPage6 = (ImageView) findViewById(R.id.page6);

        mQuotation = new Quotation();

        setupListeners();
        showFirstPage();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);
    }


    private void setupListeners() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 0) {
                    if (mUserDataFragment.isValidSection()) {

                        mQuotation = mUserDataFragment.getActualQuotation();
                        mQuotation.marca = ConstantsUtil.COTIZATION_CLIENT_MARCA;
                        mQuotation.cotizacion = ConstantsUtil.COTIZATION_TOTAL;
                        mQuotation.planSalud = ConstantsUtil.COTIZATION_PLAN_SALUD;
                        mQuotation.isTransferSegment = true;

                        mDni = mUserDataFragment.getDNI();
                        // reset all information will not be used for next Steps
                        restetQuotationDataForNextSteps();

                        highlightDot(mDopPage2);
                        showSecondPage();
                    }

                }else if (mCurrentPage == 1) {
                    if (mSelectTransferSegmentFragment.isValidSection()) {

                        mQuotation = mSelectTransferSegmentFragment.getQuotation();

                        if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.EMPRESA_FORMA_INGRESO)) {
                            //mBaseFragment.showLoader(true);
                            checkEmpresa();
                            return;
                        } else if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.AFINIDAD_FORMA_INGRESO)) {
                            checkAfinidad();
                            return;
                        } else {
                            highlightDot(mDopPage3);
                            showThirdPage();
                        }
                    }

                } else if (mCurrentPage == 2) { // Members
                    highlightDot(mDopPage4);
                    fillMembersAndUnificationData(mMembersFragment.getMembersAndUnificationData());
                    showFourPage();

                } else if (mCurrentPage == 3) {  // Forma de pago
                    if (mFormaPagoFragment.isValidSection()){
                        mQuotation.pago = mFormaPagoFragment.getPago();
                        highlightDot(mDopPage5);
                        showFivePage();
                    }
                } else if (mCurrentPage == 4) { // P5

                    ConstantsUtil.Segmento seg = mQuotation==null?null:mQuotation.getSegmento();
                    if(seg==null){
                        return;
                    }else if(seg==ConstantsUtil.Segmento.DESREGULADO){
                        if (mOSDesregulaFragment.isValidSection()) {
                            mQuotation.desregulado = mOSDesregulaFragment.getDereguladoQuotation();
                            fillDesreguladoUnificationData();

                            highlightDot(mDopPage6);
                            showSixPage();
                        }

                    }else if(seg==ConstantsUtil.Segmento.MONOTRIBUTO) {
                        if (!mMonitributistaFragment.isValidSection()) {
                            return;
                        }
                        mQuotation.monotributo = mMonitributistaFragment.getMonotributoQuotation();
                        fillMonotributoUnificationData();
                    }
                }

                enableFiveSectionIfNeeded();
                updateButtonVisivility();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 1) {
                    highlightDot(mDopPage1);
                    showFirstPage();
                }else if (mCurrentPage == 2) {
                    highlightDot(mDopPage2);
                    fillMembersAndUnificationData(mMembersFragment.getMembersAndUnificationData());
                    showSecondPage();
                }else if(mCurrentPage == 3){
                    highlightDot(mDopPage3);
                    mQuotation.pago = mFormaPagoFragment.getPago();
                    showThirdPage();
                }else if(mCurrentPage == 4){
                    highlightDot(mDopPage4);

                    ConstantsUtil.Segmento seg = mQuotation == null ? null : mQuotation.getSegmento();
                    if (seg == ConstantsUtil.Segmento.DESREGULADO) {
                        mQuotation.desregulado = mOSDesregulaFragment.getDereguladoQuotation();
                        fillDesreguladoUnificationData();
                    }
                    if (seg == ConstantsUtil.Segmento.MONOTRIBUTO) {
                        mQuotation.monotributo = mMonitributistaFragment.getMonotributoQuotation();
                        fillMonotributoUnificationData();
                    }

                    showFourPage();
                }else if(mCurrentPage == 5){
                    highlightDot(mDopPage5);

                    ConstantsUtil.Segmento seg = mQuotation == null ? null : mQuotation.getSegmento();
                    if (seg == ConstantsUtil.Segmento.DESREGULADO) {
                        mQuotation.desregulado = mDesreguladoFragment.getDereguladoQuotation();
                    }
                    showFivePage();
                }

                enableFiveSectionIfNeeded();
                updateButtonVisivility();

            }
        });
    }

    private void checkEmpresa() {

        mSelectTransferSegmentFragment.updateEmpresa(new Response.Listener<QuoteOption>() {
            @Override
            public void onResponse(QuoteOption response) {
                mSelectTransferSegmentFragment.showLoader(false);
                mQuotation.nombreEmpresa = response;

                if (response == null || response.id == null) {

                    Log.e(TAG, "Empresa error ,,,,,,,,,,,");
                    new AlertDialog.Builder(TransferenciaSegmentoActivity.this).setMessage(getString(R.string.quote_incorrect_empresa)).setPositiveButton(R.string.option_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                } else {
                    highlightDot(mDopPage3);
                    showThirdPage();
                    enableFiveSectionIfNeeded();
                    updateButtonVisivility();
                }
            }
        });
    }

    private void checkAfinidad() {
        mSelectTransferSegmentFragment.updateAfinidad(new Response.Listener<QuoteOption>() {
            @Override
            public void onResponse(QuoteOption response) {
                mSelectTransferSegmentFragment.showLoader(false);
                mQuotation.nombreAfinidad = response;
                if (response == null || response.id == null) {

                    Log.e(TAG, "Afinidad error ,,,,,,,,,,,");
                    new AlertDialog.Builder(TransferenciaSegmentoActivity.this).setMessage(getString(R.string.quote_incorrect_afinidad)).setPositiveButton(R.string.option_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                } else {
                    highlightDot(mDopPage3);
                    showThirdPage();
                    enableFiveSectionIfNeeded();
                    updateButtonVisivility();
                }
            }
        });
    }

    private void enableFiveSectionIfNeeded(){
        ConstantsUtil.Segmento seg = mQuotation==null?null:mQuotation.getSegmento();

        if(seg==null || seg==ConstantsUtil.Segmento.AUTONOMO){
            mDopPage5.setVisibility(View.GONE);
            mDopPage6.setVisibility(View.GONE);
        }else if(seg==ConstantsUtil.Segmento.DESREGULADO){
            mDopPage5.setVisibility(View.VISIBLE);
            mDopPage6.setVisibility(View.VISIBLE);
        }else if(seg==ConstantsUtil.Segmento.MONOTRIBUTO){
            mDopPage5.setVisibility(View.VISIBLE);
            mDopPage6.setVisibility(View.GONE);
        }
    }

    private void highlightDot(ImageView dot){
        mDopPage1.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));
        mDopPage2.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));
        mDopPage3.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));
        mDopPage4.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));
        mDopPage5.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));
        mDopPage6.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorDarkGrey));

        dot.setColorFilter(ContextCompat.getColor(TransferenciaSegmentoActivity.this, R.color.colorAccent));
    }

    private void fillMembersAndUnificationData(MembersAndUnificationData data){
        mQuotation.integrantes = data.integrantes;
        mQuotation.unificaAportes = data.unificaAportes;
        mQuotation.unificationType = data.unificationType;
        mQuotation.titularMainAffilliation = data.titularMainAffilliation;
        mQuotation.isEmpleadaDomestica = data.isEmpleadaDomestica;

        mConyugeQuotation = data.conyugeQuotation;

        // When unifica titular and conyuge share account
        if (mConyugeQuotation != null && (mQuotation.unificaAportes != null && mQuotation.unificaAportes)) {
            mQuotation.accountNumber = mConyugeQuotation.accountNumber;
            mQuotation.accountSubNumber = mConyugeQuotation.accountSubNumber;
        }
    }

    private MembersAndUnificationData getMembersAndUnificationData(){
        MembersAndUnificationData data = new MembersAndUnificationData();

        data.integrantes =   mQuotation.integrantes;
        data.unificaAportes = mQuotation.unificaAportes;
        data.unificationType = mQuotation.unificationType;
        data.titularMainAffilliation = mQuotation.titularMainAffilliation;
        data.isEmpleadaDomestica = mQuotation.isEmpleadaDomestica;

        data.conyugeQuotation = mConyugeQuotation;
        return data;
    }

    public void fillDesreguladoUnificationData(){
        mQuotation.desregulado.unificaAportes = mQuotation.unificaAportes;
        mQuotation.desregulado.titularMainAffilliation = mQuotation.titularMainAffilliation;
        mQuotation.desregulado.unificationType = mQuotation.unificationType;
    }
    public void fillMonotributoUnificationData(){
        mQuotation.monotributo.unificaAportes = mQuotation.unificaAportes;
        mQuotation.monotributo.titularMainAffilliation = mQuotation.titularMainAffilliation;
        mQuotation.monotributo.unificationType = mQuotation.unificationType;
    }


    private void updateButtonVisivility(){

        if(mCurrentPage==0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.INVISIBLE);
        }else{
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.VISIBLE);

            ConstantsUtil.Segmento seg = mQuotation==null?null:mQuotation.getSegmento();
            if(seg==null || seg==ConstantsUtil.Segmento.AUTONOMO){
                if(mCurrentPage==3){
                    mNextButton.setVisibility(View.INVISIBLE);
                }
            }else if(seg==ConstantsUtil.Segmento.MONOTRIBUTO && mCurrentPage==4){
                mNextButton.setVisibility(View.INVISIBLE);
            }else if(seg==ConstantsUtil.Segmento.DESREGULADO && mCurrentPage==5){
                mNextButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void showFirstPage() {
        mUserDataFragment = ExistentClientDataFragment.newInstance(mQuotation, mDni);
        mCurrentPage = 0;
        switchToFragment(mUserDataFragment);
    }

    private void showSecondPage() {
        mSelectTransferSegmentFragment = SelectTransferSegmentFragment.newInstance(mQuotation);
        mCurrentPage = 1;
        switchToFragment(mSelectTransferSegmentFragment);
    }

    private void showThirdPage() {
        ConstantsUtil.Segmento seg = ((mQuotation == null )? null : mQuotation.getSegmento());
        if (seg == null) {
            return;
        }
        if (seg == ConstantsUtil.Segmento.AUTONOMO) {
            mMembersFragment = QuoteMembersAutonomoFragment.newInstance(getMembersAndUnificationData());
        }else{
            mMembersFragment = QuoteMembersNoAutonomoFragment.newInstance(getMembersAndUnificationData());
        }
        mCurrentPage = 2;
        switchToFragment((BaseFragment) mMembersFragment);
    }

    private void showFourPage() {
        mFormaPagoFragment = QuoteFormaPagoFragment.newInstance(mQuotation);
        mCurrentPage = 3;
        switchToFragment(mFormaPagoFragment);
    }

    private void showFivePage() {
        ConstantsUtil.Segmento seg = mQuotation==null?null:mQuotation.getSegmento();
        if(seg==null){
            return;
        }
        if(seg==ConstantsUtil.Segmento.DESREGULADO){
            mOSDesregulaFragment = OSDesregulaFragment.newInstance(mQuotation);
            mCurrentPage = 4;
            switchToFragment(mOSDesregulaFragment);

        }else if(seg==ConstantsUtil.Segmento.MONOTRIBUTO) {
            mMonitributistaFragment = MonotributistaAdditionalDataFragment.newInstance(mQuotation, mConyugeQuotation);
            mCurrentPage = 4;
            switchToFragment(mMonitributistaFragment);
        }
    }

    private void showSixPage() {
        ConstantsUtil.Segmento seg = mQuotation == null ? null : mQuotation.getSegmento();
        if (seg == null) {
            return;
        }

        if (seg == ConstantsUtil.Segmento.DESREGULADO) {
            mDesreguladoFragment = DesreguladoAdditionalDataFragment.newInstance(mQuotation, mConyugeQuotation);
            mCurrentPage = 5;
            switchToFragment(mDesreguladoFragment);
        }
    }


    private void switchToFragment(BaseFragment fragment) {

        mCurrentFragment = fragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }


    @Override
    public void showQuotation(final Quotation quotation) {

        Log.e(TAG, "showQuotation.....");
        if (mQuotation.formaIngreso.id.equals(ConstantsUtil.EMPRESA_FORMA_INGRESO)) {
            quoteData(quotation);
        }else {
            showMessageWithOptionAndLabels(getResources().getString(R.string.quote_optionals_message), R.string.option_no_thanks_upper, R.string.option_yes_upper, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // NO option
                    dialog.dismiss();
                    quoteData(quotation);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // yes option
                    dialog.dismiss();

                    // With new segment transfer get adicionales optativos
                    fillAdicionalesOptativosData(quotation);
                }
            });
        }
    }

    private void quoteData(Quotation quotation) {

        if (AppController.getInstance().isNetworkAvailable()) {

            showProgressDialog(R.string.quoting_data);

            QuotationController.getInstance().quoteData(quotation, new Response.Listener<QuotationDataResult>() {
                @Override
                public void onResponse(QuotationDataResult quotationResult) {
                    dismissProgressDialog();
                    Log.e(TAG, "cotizacion ok .....");

                    if (quotationResult != null) {
                        toQuotationResult(quotationResult);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dismissProgressDialog();
                    Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    DialogHelper.showMessage(TransferenciaSegmentoActivity.this, getResources().getString(R.string.error_quote), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(TransferenciaSegmentoActivity.this, null);
        }

    }

    private void fillAdicionalesOptativosData(final Quotation quotation) {

        if (AppController.getInstance().isNetworkAvailable()) {

            //showProgressDialog(R.string.quoting_data);
            QuotationController.getInstance().getAdicionalesOptativos(quotation.segmento, new Response.Listener<AdicionalesOptativosData>() {
                @Override
                public void onResponse(AdicionalesOptativosData adicionalesOptativos) {
                    Log.e(TAG, "optativos ok .....");
                    //dismissProgressDialog();

                    if (adicionalesOptativos != null && !adicionalesOptativos.tipoOpcionList.isEmpty()) {
                        toAdditionalesOptativosForProspectiveClient(quotation, adicionalesOptativos);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //dismissProgressDialog();
                    Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    DialogHelper.showMessage(TransferenciaSegmentoActivity.this, getResources().getString(R.string.error_quote), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(TransferenciaSegmentoActivity.this, null);
        }
    }


    private void toQuotationResult(QuotationDataResult quotationResult) {

        quotationResult.clientFullName =  mQuotation.client.getFullName();
        quotationResult.clientId = mQuotation.client.id;
        quotationResult.quoteType =  ConstantsUtil.QUOTE_TYPE_GENERAL;

        IntentHelper.goToQuotationResultActivity(TransferenciaSegmentoActivity.this, quotationResult);
    }


    private void toAdditionalesOptativosForProspectiveClient(Quotation quotation, AdicionalesOptativosData optativosData) {
        IntentHelper.goToAdicionalesOptativosActivity(TransferenciaSegmentoActivity.this, quotation, optativosData);
    }


    private void restetQuotationDataForNextSteps(){

        if (mQuotation.previousSegmento == null) {
            // only the firs time we call service to get user data
            mQuotation.previousSegmento = mQuotation.segmento;
        }

        mQuotation.coberturaProveniente = null;
        //mQuotation.provenienteId = null;
        //mQuotation.inputDate = null;
        //mQuotation.pago = null;
        //mQuotation.regimenId = null;
        mQuotation.aportantesMonotributo = null;

        mQuotation.nombreAfinidad = null;
        mQuotation.nombreEmpresa  = null;

        //mQuotation.categoria = null;
        mQuotation.desregulado = null;
        mQuotation.monotributo = null;
    }
}

