package ar.com.sancorsalud.asociados.activity.quotation;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.fragments.quote.additional.AdicionalesOptativosFragment;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class AdicionalesOptativosActivity extends BaseActivity {

    private static final String TAG = "ADD_OPT_PA";

    private AdicionalesOptativosFragment mAdicionalesOptativosFragment;

    private Quotation mQuotation;
    private AdicionalesOptativosData mOptativosData;
    private boolean mHidePlanValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionales_optativos_for_pa);

        Log.e(TAG, "AdicionalesOptativosActivity....");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_quote);

        mMainContainer = findViewById(R.id.main);

        if (getIntent().getExtras() != null) {
            mQuotation = (Quotation) getIntent().getSerializableExtra(ConstantsUtil.QUOTATION);
            mOptativosData = (AdicionalesOptativosData) getIntent().getSerializableExtra(ConstantsUtil.OPTATIVOS_DATA);
            mHidePlanValue = getIntent().getBooleanExtra(ConstantsUtil.OPTATIVOS_HIDE_PLAN_VALUE, false);
        }

        showFragment();
    }

    private void showFragment() {
        mAdicionalesOptativosFragment = AdicionalesOptativosFragment.newInstance(mQuotation, mOptativosData, mHidePlanValue);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, mAdicionalesOptativosFragment);
        transaction.commit();
    }
}
