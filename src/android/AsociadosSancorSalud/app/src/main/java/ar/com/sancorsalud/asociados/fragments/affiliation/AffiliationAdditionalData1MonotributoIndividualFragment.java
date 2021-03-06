package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.EntidadEmpleadoraAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class AffiliationAdditionalData1MonotributoIndividualFragment extends AffiliationAdditionalData1FormaPagoIndividualFragment {

    private static final String TAG = "AF_MONO_INDV1_FRG";

    private int actualIndex = -1;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mEERecyclerView;
    private EntidadEmpleadoraAdapter mEEAdapter;


    public static AffiliationAdditionalData1MonotributoIndividualFragment newInstance(AdditionalData1MonotributoIndividual param1, long dni, long cardId, String cuil, String fechaInicioServicio) {
        AffiliationAdditionalData1MonotributoIndividualFragment fragment = new AffiliationAdditionalData1MonotributoIndividualFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_1, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putString(ARG_TITULAR_CUIL, cuil);
        args.putString(ARG_FECHA_INI_SERV, fechaInicioServicio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionalData1 = (AdditionalData1MonotributoIndividual) getArguments().getSerializable(ARG_ADD_DATA_1);
            titularDNI = getArguments().getLong(ARG_TITULAR_DNI);
            cardId = getArguments().getLong(ARG_CARD_ID);
            titularCuil = getArguments().getString(ARG_TITULAR_CUIL);
            mFechaIniServ =  getArguments().getString(ARG_FECHA_INI_SERV);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated....");
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_additional_data_mono_ind1, container, false);
        return mMainContainer;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_EE) {
            Log.e(TAG, "onActivityResult **********");
            EntidadEmpleadora returnEE = (EntidadEmpleadora) data.getSerializableExtra(ConstantsUtil.RESULT_EE);

            EntidadEmpleadora ee = mEEAdapter.getItemAtIndex(actualIndex);
            ee.update(returnEE);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public IAdditionalData1 getAdditionalData1() {
        Log.e(TAG, "----- getAdditionalData1 -------");
        AdditionalData1MonotributoIndividual additionalData = (AdditionalData1MonotributoIndividual) super.getAdditionalData1();

        // EE
        if (mEEAdapter != null) {
            additionalData.entidadEmpleadoraArray = mEEAdapter.getEntidadEmpledoraList();
        }
        return additionalData;
    }

    @Override
    protected void initializeForm() {

        View eeTitle = mMainContainer.findViewById(R.id.ee_title);
        AdditionalData1MonotributoIndividual additionalData = (AdditionalData1MonotributoIndividual)additionalData1;

        if (!additionalData.entidadEmpleadoraArray.isEmpty()) {
            eeTitle.setVisibility(View.VISIBLE);
            for (EntidadEmpleadora ee : additionalData.entidadEmpleadoraArray) {
                ee.cardId = cardId;
            }
        } else {
            eeTitle.setVisibility(View.GONE);
        }


        mEEAdapter = new EntidadEmpleadoraAdapter(additionalData.entidadEmpleadoraArray);
        mEERecyclerView = (RecyclerView) view.findViewById(R.id.ee_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mEERecyclerView.getContext());
        mEERecyclerView.setLayoutManager(mLinearLayoutManager);
        mEERecyclerView.setAdapter(mEEAdapter);
        mEERecyclerView.setHasFixedSize(true);


        super.initializeForm();
    }

    @Override
    protected void setupListeners() {

        // EE
        mEEAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "onItemClick():" + position);
                actualIndex = position;
                EntidadEmpleadora ee = mEEAdapter.getItemAtIndex(position);
                IntentHelper.gotoEntidadEmpleadoraActivity(AffiliationAdditionalData1MonotributoIndividualFragment.this, titularDNI, position, ee);
            }
        });

        super.setupListeners();
    }
}
