package ar.com.sancorsalud.asociados.fragments.quote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.AportesAdapter;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.quotation.DesreguladoQuotation;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class OSDesregulaFragment extends BaseFragment{

    private static final String ARG_QUOTATION = "quotation";

    private View mMainContainer;
    private Quotation mQuotation;

    private EditText mOsDesregulaEditText;
    private SpinnerDropDownAdapter mOsDesregulaAlertAdapter;
    private int mSelectedOsDesregula = -1;
    private ArrayList<QuoteOption> mOSDesreguladas;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private AportesAdapter mAdapter;
    private ArrayList<Aporte> mAportes;


    public OSDesregulaFragment() {
        // Required empty public constructor
    }

    public static OSDesregulaFragment newInstance(Quotation param1) {
        OSDesregulaFragment fragment = new OSDesregulaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Aporte aporte = (Aporte) data.getSerializableExtra(ConstantsUtil.RESULT_APORTE);
            if(aporte!=null)
                mAdapter.addItem(aporte);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_os_desregula, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mOsDesregulaEditText = (EditText) mMainContainer.findViewById(R.id.os_desregula_input);

        mOSDesreguladas = new ArrayList<>();
        QuoteOption osSelection = new QuoteOption("-1", getResources().getString(R.string.field_os_desregula));
        mOSDesreguladas.add(osSelection);
        mOSDesreguladas.addAll(QuoteOptionsController.getInstance().getOSDesreguladas());

        if (mAdapter == null) {
            mAdapter = new AportesAdapter();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        initializeForm();
        setupListeners();
    }


    // --- helper methods -------------------------------------------------//

    private void setupListeners() {

        View conOS = mMainContainer.findViewById(R.id.os_desregula_button);
        conOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showOsDesregulaAlert();
            }
        });

        conOS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedOsDesregula = -1;
                mOsDesregulaEditText.setText("");
                return true;
            }
        });


        View v = mMainContainer.findViewById(R.id.add_button);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoAddAporte(OSDesregulaFragment.this);
            }
        });
    }

    private void initializeForm() {

        mOsDesregulaEditText.setText("");
        mSelectedOsDesregula = -1;

        if(mQuotation.desregulado==null)
            return;

        if(mQuotation.desregulado.osDeregulado!=null){
            mSelectedOsDesregula = mOSDesreguladas.indexOf(mQuotation.desregulado.osDeregulado);

            if(mSelectedOsDesregula !=-1)
                mOsDesregulaEditText.setText(mQuotation.desregulado.osDeregulado.title);
        }

        mAportes = mQuotation.aportes;
        if(mAportes!=null)
            mAdapter.setItems(mAportes);
        else mAdapter.removeAllItems();


    }

    public void setQuotation(Quotation q){
        mQuotation = q;
        initializeForm();
    }

    public DesreguladoQuotation getDereguladoQuotation(){

        if (mQuotation.desregulado  == null) {
            mQuotation.desregulado = new DesreguladoQuotation();
        }

        if(mSelectedOsDesregula !=-1)
            mQuotation.desregulado.osDeregulado = mOSDesreguladas.get(mSelectedOsDesregula);

        mQuotation.aportes  = mAdapter.getAportes();

        return mQuotation.desregulado;
    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }


    public boolean validateAportes(){
        if(mAdapter.getAportes().size()>0)
            return true;

        SnackBarHelper.makeError(mMainContainer,R.string.agregar_aporte).show();
        return false;
    }

    public boolean isValidSection() {
        boolean isValid = true;

        isValid = isValid & validateField(mOsDesregulaEditText, R.string.select_option_error, R.id.os_desregula_wrapper);
        isValid = isValid & validateAportes();

        return isValid;
    }

    private void showOsDesregulaAlert(){

        ArrayList<String> desregStr = new ArrayList<String>();
        for (QuoteOption q : mOSDesreguladas) {
            desregStr.add(q.optionName());
        }

        mOsDesregulaAlertAdapter = new SpinnerDropDownAdapter(getActivity(), desregStr, mSelectedOsDesregula);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(mOsDesregulaAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedOsDesregula = i;

                        if (i == 0){
                            mSelectedOsDesregula = -1;
                            mOsDesregulaEditText.setText("");
                        }else {

                            mOsDesregulaEditText.setText(mOSDesreguladas.get(i).optionName());

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mOsDesregulaAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
