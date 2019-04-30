package ar.com.sancorsalud.asociados.activity.quotation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.fragments.quote.additional.AdicionalesOptativosFragment;
import ar.com.sancorsalud.asociados.fragments.quote.client.ExistentClientDataFragment;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/*
 * For users already existent in Sancor Salud System
 * we can add adicionales optativos for them and quota that part
 * This logic is loaded form left user menu
 */
public class QuoteExistentAdicionalesOptativosActivity extends BaseActivity {

    private static final String TAG = "AD_OPT_CLIENT";

    private ImageView mDopPage1;
    private ImageView mDopPage2;
    private View mNextButton;
    private View mPrevButton;

    private ExistentClientDataFragment mUserDataFragment;
    private AdicionalesOptativosFragment mAdicionalesOptativosFragment;

    private int mCurrentPage = 0;

    private Quotation mQuotation;
    private String mDni;
    private AdicionalesOptativosData mOptativosData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionales_optativos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_quote);

        mMainContainer = findViewById(R.id.main);

        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);
        mDopPage1 = (ImageView) findViewById(R.id.page1);
        mDopPage2 = (ImageView) findViewById(R.id.page2);

        mQuotation = new Quotation();


        setupListeners();
        showFirstPage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentPage == 0) {
            mUserDataFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupListeners() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 0) {
                    if (mUserDataFragment.isValidSection()) {

                        mQuotation = mUserDataFragment.getActualQuotation();

                        mQuotation.marca = ConstantsUtil.COTIZATION_CLIENT_MARCA;
                        mQuotation.cotizacion = ConstantsUtil.COTIZATION_PARCIAL;
                        mQuotation.planSalud = ConstantsUtil.COTIZATION_PLAN_OPCIONALES;

                        mDni = mUserDataFragment.getDNI();
                        mOptativosData = mUserDataFragment.getAdicionalesOptativos();

                        if (mOptativosData != null){
                            highlightDot(mDopPage2);
                            showSecondPage();
                        }
                    }
                }
                updateButtonVisivility();
            }
        }
    );

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if (mCurrentPage == 1) {
                    highlightDot(mDopPage1);
                    showFirstPage();
                }
                updateButtonVisivility();
            }
        });
    }

    private void highlightDot(ImageView dot) {
        mDopPage1.setColorFilter(ContextCompat.getColor(QuoteExistentAdicionalesOptativosActivity.this, R.color.colorDarkGrey));
        mDopPage2.setColorFilter(ContextCompat.getColor(QuoteExistentAdicionalesOptativosActivity.this, R.color.colorDarkGrey));
        dot.setColorFilter(ContextCompat.getColor(QuoteExistentAdicionalesOptativosActivity.this, R.color.colorAccent));
    }

    private void updateButtonVisivility() {

        if (mCurrentPage == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.INVISIBLE);
            mPrevButton.setVisibility(View.VISIBLE);
        }
    }

    private void showFirstPage() {

        mUserDataFragment = ExistentClientDataFragment.newInstance(mQuotation, mDni, mOptativosData);
        mCurrentPage = 0;
        switchToFragment(mUserDataFragment);
    }

    private void showSecondPage() {

        mAdicionalesOptativosFragment = AdicionalesOptativosFragment.newInstance(mQuotation, mOptativosData, true);
        mCurrentPage = 1;
        switchToFragment(mAdicionalesOptativosFragment);
    }

    private void switchToFragment(BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCurrentPage == 0) {
            mUserDataFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
