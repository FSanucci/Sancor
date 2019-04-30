package ar.com.sancorsalud.asociados.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.zoneleader.SalesmanSelectionFragment;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

public class SalesmanSelectionActivity extends BaseActivity{

    private static final String TAG = "SalesmanSelectionActivity";
    private SalesmanSelectionFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar,R.string.menu_prospective_client);

        ArrayList<ProspectiveClient> list = (ArrayList<ProspectiveClient>) getIntent().getSerializableExtra(ConstantsUtil.SELECTED_CLIENTS);
        mFragment = new SalesmanSelectionFragment();
        mFragment.setSelectedClients(list);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, mFragment, "SalesmanSelectionFragment");
        ft.commit();
    }

}
