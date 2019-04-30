package ar.com.sancorsalud.asociados.activity.quotation;

import android.content.DialogInterface;
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
import ar.com.sancorsalud.asociados.fragments.quote.IQuoteMember;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteBaseFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteFormaPagoFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteMembersAutonomoFragment;
import ar.com.sancorsalud.asociados.fragments.quote.QuoteMembersNoAutonomoFragment;
import ar.com.sancorsalud.asociados.interfaces.QuoteListener;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.MembersAndUnificationData;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;

/*
 * Same as Quotaton  and add new adicionales optativos for users in this quotation scope
 */
public class QuoteNewAdicionalesOptativosActivity extends BaseActivity implements QuoteListener {

    private static final String TAG = "QUOT_NEW_OPT_ACT";

    private ImageView mDopPage1;
    private ImageView mDopPage2;
    private ImageView mDopPage3;

    private View mNextButton;
    private View mPrevButton;

    private QuoteBaseFragment mBaseFragment;
    private IQuoteMember mMembersFragment;
    private QuoteFormaPagoFragment mFormaPagoFragment;

    private Quotation mQuotation;
    private ProspectiveClient mProspectiveClient;
    private int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_optativos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_quote_adic_opt);

        mMainContainer = findViewById(R.id.main);

        if (getIntent().getExtras() != null) {
            mProspectiveClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
        }

        mQuotation = new Quotation();
        mQuotation.marca = ConstantsUtil.COTIZATION_CLIENT_MARCA;
        mQuotation.cotizacion = ConstantsUtil.COTIZATION_PARCIAL;
        mQuotation.planSalud = ConstantsUtil.COTIZATION_PLAN_SALUD;


        mQuotation.client = mProspectiveClient;
        mQuotation.titular = buildTitularMemberFromClient();

        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);
        mDopPage1 = (ImageView) findViewById(R.id.page1);
        mDopPage2 = (ImageView) findViewById(R.id.page2);
        mDopPage3 = (ImageView) findViewById(R.id.page3);

        setupListeners();
        showFirstPage();
    }


    private void setupListeners() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 0) {  // P1 Basic Data
                    if (mBaseFragment.isValidSection()) {
                        mQuotation = mBaseFragment.getQuotation();
                        if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.EMPRESA_FORMA_INGRESO)) {
                            mBaseFragment.showLoader(true);
                            checkEmpresa();
                            return;
                        } else if (mQuotation.formaIngreso.id.equalsIgnoreCase(ConstantsUtil.AFINIDAD_FORMA_INGRESO)) {
                            checkAfinidad();
                            return;
                        } else {
                            highlightDot(mDopPage2);
                            showSecondPage();
                        }
                    }

                } else if (mCurrentPage == 1) {  // P2 Members Data
                    highlightDot(mDopPage3);
                    //mQuotation.integrantes = mMembersFragment.getMembers();
                    fillMembersAndUnificationData(mMembersFragment.getMembersAndUnificationData());
                    showThirdPage();

                }
                updateButtonVisivility();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 1) {

                    fillMembersAndUnificationData(mMembersFragment.getMembersAndUnificationData());

                    highlightDot(mDopPage1);
                    showFirstPage();
                } else if (mCurrentPage == 2) {
                    highlightDot(mDopPage2);
                    mQuotation.pago = mFormaPagoFragment.getPago();
                    showSecondPage();
                }
                updateButtonVisivility();
            }
        });
    }


    private void checkEmpresa() {
        mBaseFragment.updateEmpresa(new Response.Listener<QuoteOption>() {
            @Override
            public void onResponse(QuoteOption response) {
                mBaseFragment.showLoader(false);
                mQuotation.nombreEmpresa = response;
                if (response.id == null) {

                    Log.e(TAG, "Empresa error ,,,,,,,,,,,");
                    new AlertDialog.Builder(QuoteNewAdicionalesOptativosActivity.this).setMessage(getString(R.string.quote_incorrect_empresa)).setPositiveButton(R.string.option_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                } else {
                    highlightDot(mDopPage2);
                    showSecondPage();
                    updateButtonVisivility();
                }
            }
        });
    }

    private void checkAfinidad() {
        mBaseFragment.updateAfinidad(new Response.Listener<QuoteOption>() {
            @Override
            public void onResponse(QuoteOption response) {
                mBaseFragment.showLoader(false);
                mQuotation.nombreAfinidad = response;
                if (response.id == null) {

                    Log.e(TAG, "Afinidad error ,,,,,,,,,,,");
                    new AlertDialog.Builder(QuoteNewAdicionalesOptativosActivity.this).setMessage(getString(R.string.quote_incorrect_afinidad)).setPositiveButton(R.string.option_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                } else {
                    highlightDot(mDopPage2);
                    showSecondPage();
                    updateButtonVisivility();
                }
            }
        });
    }


    private void highlightDot(ImageView dot) {
        mDopPage1.setColorFilter(ContextCompat.getColor(QuoteNewAdicionalesOptativosActivity.this, R.color.colorDarkGrey));
        mDopPage2.setColorFilter(ContextCompat.getColor(QuoteNewAdicionalesOptativosActivity.this, R.color.colorDarkGrey));
        mDopPage3.setColorFilter(ContextCompat.getColor(QuoteNewAdicionalesOptativosActivity.this, R.color.colorDarkGrey));

        dot.setColorFilter(ContextCompat.getColor(QuoteNewAdicionalesOptativosActivity.this, R.color.colorAccent));
    }

    private void fillMembersAndUnificationData(MembersAndUnificationData data){
        mQuotation.integrantes = data.integrantes;
        mQuotation.unificaAportes = data.unificaAportes;
        mQuotation.unificationType = data.unificationType;
        mQuotation.titularMainAffilliation = data.titularMainAffilliation;

        Quotation conyugeQuotation = data.conyugeQuotation;
        if (conyugeQuotation != null && (mQuotation.unificaAportes != null && mQuotation.unificaAportes)) {
            mQuotation.accountNumber = conyugeQuotation.accountNumber;
            mQuotation.accountSubNumber = conyugeQuotation.accountSubNumber;
        }

    }

    private MembersAndUnificationData getMembersAndUnificationData(){
        MembersAndUnificationData data = new MembersAndUnificationData();

        data.integrantes =   mQuotation.integrantes;
        data.unificaAportes = mQuotation.unificaAportes;
        data.unificationType = mQuotation.unificationType;
        data.titularMainAffilliation = mQuotation.titularMainAffilliation;

        return data;
    }
    private void updateButtonVisivility() {

        if (mCurrentPage == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.VISIBLE);

            if (mCurrentPage == 2) {
                mNextButton.setVisibility(View.INVISIBLE);
            }

        }
    }

    private void showFirstPage() {
        mBaseFragment = QuoteBaseFragment.newInstance(mQuotation, true);
        mCurrentPage = 0;
        switchToFragment(mBaseFragment);
    }

    private void showSecondPage() {

        ConstantsUtil.Segmento seg = ((mQuotation == null )? null : mQuotation.getSegmento());
        if (seg == null) {
            return;
        }
        if (seg == ConstantsUtil.Segmento.AUTONOMO) {
            mMembersFragment = QuoteMembersAutonomoFragment.newInstance(getMembersAndUnificationData());
        }else{
            mMembersFragment = QuoteMembersNoAutonomoFragment.newInstance(getMembersAndUnificationData());
        }

        mCurrentPage = 1;
        switchToFragment((BaseFragment) mMembersFragment);
    }

    private void showThirdPage() {
        mFormaPagoFragment = QuoteFormaPagoFragment.newInstance(mQuotation, false);
        mCurrentPage = 2;
        switchToFragment(mFormaPagoFragment);
    }

    private void switchToFragment(BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void showQuotation(final Quotation quotation) {
        Log.e(TAG, "fillAdicionalesOptativosData.....");
        fillAdicionalesOptativosData(quotation);
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
                        toAdditionalesOptativosForPA(quotation, adicionalesOptativos);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //dismissProgressDialog();
                    Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    DialogHelper.showMessage(QuoteNewAdicionalesOptativosActivity.this, getResources().getString(R.string.error_quote), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(QuoteNewAdicionalesOptativosActivity.this, null);
        }
    }


    private void toAdditionalesOptativosForPA(Quotation quotation, AdicionalesOptativosData optativosData) {
        IntentHelper.goToAdicionalesOptativosActivity(QuoteNewAdicionalesOptativosActivity.this, quotation, optativosData, true);
    }

    private Member buildTitularMemberFromClient() {
        Member titular = new Member();

        titular.age = mQuotation.client.age;
        titular.cant = 1;
        titular.parentesco = new QuoteOption(ConstantsUtil.TITULAR_MEMBER, ConstantsUtil.TITULAR_MEMBER_DESC);
        titular.inputDate = null;
        titular.dni = mQuotation.client.dni;

        return titular;
    }

}
