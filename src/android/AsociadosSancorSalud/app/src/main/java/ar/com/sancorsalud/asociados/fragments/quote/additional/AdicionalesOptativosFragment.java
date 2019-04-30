package ar.com.sancorsalud.asociados.fragments.quote.additional;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.additional.AdicionalesOptativosMembersAdapter;
import ar.com.sancorsalud.asociados.adapter.additional.SpinnerAdicionalesOptativosDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;


public class AdicionalesOptativosFragment extends BaseFragment {

    private static final String TAG = "ADD_SERV_FRG";

    private static final String ARG_PARAM1 = "user";
    private static final String ARG_QUOTATION = "quotation";
    private static final String ARG_OPTATIVOS_DATA = "optativosData";
    private static final String ARG_OPTATIVOS_HIDE_PLAN_VALUE = "hidePlanValue";


    private LinearLayout optionalsBox;
    private View mQuotationButton;

    private Quotation mQuotation;
    private AdicionalesOptativosData mOptativosData;
    private boolean mHidePlanValue;

    //private List<AdicionalesOptativosData.OpcionalData> opcionalesListType = new ArrayList<AdicionalesOptativosData.OpcionalData>();

    // Maps to get combo data
    private Map<String, AdicionalesOptativosData.OpcionalData> opcionalesComboMap = new HashMap<String, AdicionalesOptativosData.OpcionalData>();
    private Map<String, Integer> opcionalesComboIndex = new HashMap<String, Integer>();

    private Map<String, AdicionalesOptativosMembersAdapter> integrantesIndexesMap = new HashMap<String, AdicionalesOptativosMembersAdapter>();

    public AdicionalesOptativosFragment() {
        // Required empty public constructor
    }

    public static AdicionalesOptativosFragment newInstance() {
        AdicionalesOptativosFragment fragment = new AdicionalesOptativosFragment();
        return fragment;
    }


    public static AdicionalesOptativosFragment newInstance(Quotation quotation, AdicionalesOptativosData mOptativosData, boolean hidePlanValue) {
        AdicionalesOptativosFragment fragment = newInstance();

        Bundle args = new Bundle();
        args.putSerializable(ARG_QUOTATION, quotation);
        args.putSerializable(ARG_OPTATIVOS_DATA, mOptativosData);
        args.putBoolean(ARG_OPTATIVOS_HIDE_PLAN_VALUE, hidePlanValue);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mQuotation = (Quotation) getArguments().getSerializable(ARG_QUOTATION);
            mOptativosData = (AdicionalesOptativosData) getArguments().getSerializable(ARG_OPTATIVOS_DATA);
            mHidePlanValue = getArguments().getBoolean(ARG_OPTATIVOS_HIDE_PLAN_VALUE, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_additional_services, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        optionalsBox = (LinearLayout) view.findViewById(R.id.optionals_box);

        // Dinamically build the view from json response
        for (final AdicionalesOptativosData.TipoOpcion to : mOptativosData.tipoOpcionList) {

            // NOT SHOW ANY MORE OBLIGATORIOS
            /*
            if (to.tipo.equals(ConstantsUtil.OPCIONAL_TIPO_LISTA)) {

                // title
                TextView t = new TextView(getContext());
                t.setText(to.titulo);
                t.setTextColor(getResources().getColor(R.color.colorAccent));

                LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(getPixelsFromDPs(10), getPixelsFromDPs(10), 0, 0);
                t.setLayoutParams(params);
                optionalsBox.addView(t);

                // tipo list
                RecyclerView rv = new RecyclerView(getContext());
                AdicionalesOptativosAdapter listAdapter = new AdicionalesOptativosAdapter(to.opciones);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(listAdapter);
                rv.setHasFixedSize(true);

                optionalsBox.addView(rv);

                // save list data
                opcionalesListType.addAll(to.opciones);
            }
            */

            if (to.tipo.equals(ConstantsUtil.OPCIONAL_TIPO_COMBO)) {

                if (!to.opciones.isEmpty()) {
                    // title
                    TextView t = new TextView(getContext());
                    t.setText(to.titulo);
                    t.setTextColor(getResources().getColor(R.color.colorAccent));

                    LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(getPixelsFromDPs(10), getPixelsFromDPs(15), 0, 0);
                    t.setLayoutParams(params);
                    optionalsBox.addView(t);

                    // tipo combo: spinner
                    opcionalesComboIndex.put(ConstantsUtil.OPCIONAL_TIPO_COMBO + "_" + to.titulo, -1);

                    RelativeLayout rl = new RelativeLayout(getActivity());
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rl.setLayoutParams(rlp);

                    RelativeLayout.LayoutParams elp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    elp.setMargins(getPixelsFromDPs(10), getPixelsFromDPs(10), 0, 0);

                    final EditText et = new EditText(getContext());
                    et.setHint(to.titulo);
                    et.setFocusable(false);

                    et.setLayoutParams(elp);
                    rl.addView(et);

                    ImageView img = new ImageView(getActivity());
                    RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    img.setImageResource(R.drawable.ic_expand);

                    ilp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ilp.setMargins(0, getPixelsFromDPs(15), getPixelsFromDPs(10),0);

                    img.setLayoutParams(ilp);
                    rl.addView(img);

                    optionalsBox.addView(rl);

                    // combo selection
                    et.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideSoftKeyboard(v);

                            Log.e(TAG, "Clickkkkk!!");

                            final SpinnerAdicionalesOptativosDropDownAdapter alertAdapter = new SpinnerAdicionalesOptativosDropDownAdapter(getActivity(), to.opciones, opcionalesComboIndex.get(ConstantsUtil.OPCIONAL_TIPO_COMBO + "_" + to.titulo));
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                            builder.setTitle(getResources().getString(R.string.seleccione_opcion))
                                    .setAdapter(alertAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            String key = ConstantsUtil.OPCIONAL_TIPO_COMBO + "_" + to.titulo;
                                            opcionalesComboIndex.put(key, i);

                                            // save combo data
                                            opcionalesComboMap.put(key, to.opciones.get(i)); //  map: key , OcionalData
                                            et.setText(to.opciones.get(i).descripcionPlan);

                                            Log.e(TAG, "PROD ID: " + to.opciones.get(i).productoId);
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    alertAdapter.notifyDataSetChanged();
                                                }
                                            });

                                            // Preselect check only if allmembers contains the titular member when select the combo
                                            AdicionalesOptativosMembersAdapter membersAdapter = integrantesIndexesMap.get(key);
                                            if (membersAdapter.getItemCount() == 1){
                                                // only one member is added --> titular
                                                membersAdapter.setSelectedIndex(0);
                                            }
                                        }
                                    });

                            android.support.v7.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });


                    // list quotation members with multiselection for the combo selected
                    List<Member> allMembers = new ArrayList<Member>();
                    if (mQuotation.titular != null) {
                        allMembers.add(mQuotation.titular);
                    }

                    if (mQuotation.integrantes != null && !mQuotation.integrantes.isEmpty()) {
                        allMembers.addAll(mQuotation.integrantes);
                    }

                    RecyclerView crv = new RecyclerView(getContext());

                    ArrayList<Integer> selections = new ArrayList<Integer>();
                    AdicionalesOptativosMembersAdapter membersAdapter = new AdicionalesOptativosMembersAdapter(allMembers, selections);

                    LinearLayoutManager lManager = new LinearLayoutManager(getContext());

                    crv.setLayoutManager(lManager);
                    crv.setAdapter(membersAdapter);
                    crv.setHasFixedSize(true);

                    integrantesIndexesMap.put(ConstantsUtil.OPCIONAL_TIPO_COMBO + "_" + to.titulo, membersAdapter);
                    optionalsBox.addView(crv);
                }
            }
        }

        mQuotationButton = view.findViewById(R.id.see_quote_button);

        initializeForm();
        setupListeners();
    }

    // Method for converting DP/DIP value to pixels
    public int getPixelsFromDPs(int dps) {
        Resources r = getActivity().getResources();
        int px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    public boolean isValidSection() {
        return true;
    }

    private void initializeForm() {

    }

    private void setupListeners() {
        mQuotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);

                mQuotation.opcionales = new ArrayList<AdicionalesOptativosData.OpcionalData>();

                /*  NOT SEND ANY MORE OBLIGATORIOS
                // get all List data
                for (AdicionalesOptativosData.OpcionalData opcionalData : opcionalesListType){

                    Log.e(TAG, "Opcional Data:" + opcionalData.descripcionPlan);

                    // add to all capitas
                    opcionalData.capitas = new ArrayList<Member>();

                    if (mQuotation.titular != null) {
                        opcionalData.capitas.add(mQuotation.titular);
                    }
                    if ((mQuotation.integrantes != null && !mQuotation.integrantes.isEmpty())) {
                        opcionalData.capitas.addAll(mQuotation.integrantes);
                    }

                    mQuotation.opcionales.add(opcionalData);
                }
                */


                // get selected Combos data
                Set<String> opcionalesComboKeySet = opcionalesComboMap.keySet();
                for (String key : opcionalesComboKeySet) {
                    Log.e(TAG, "Opcional Key:" + key);

                    AdicionalesOptativosData.OpcionalData opcionalData = opcionalesComboMap.get(key);
                    Log.e(TAG, "Opcional Data:" + opcionalData.descripcionPlan);

                    if (!opcionalesComboKeySet.isEmpty()) {

                        // get slected indexes for each membersAdapter for that combo
                        ArrayList<Integer> membersIndex = integrantesIndexesMap.get(key).getSelectedIndexes();

                        opcionalData.capitas = new ArrayList<Member>();

                        for (int index : membersIndex) {
                            Log.e(TAG, "member index:" + index);

                            if (index == 0){
                                opcionalData.capitas.add(mQuotation.titular);
                            }else {
                                // mQuotation.integrantes does not have titular
                                opcionalData.capitas.add(mQuotation.integrantes.get(index -1));
                            }
                        }

                        mQuotation.opcionales.add(opcionalData);
                    }
                }

                quoteData();
            }
        });
    }

    private void quoteData() {
        if (AppController.getInstance().isNetworkAvailable()) {

            showProgressDialog(R.string.quoting_data);

            QuotationController.getInstance().quoteData(mQuotation, new Response.Listener<QuotationDataResult>() {
                @Override
                public void onResponse(QuotationDataResult quotationResponse) {
                    Log.e(TAG, "cotizacion ok .....");
                    dismissProgressDialog();
                    toViewQuotationResult(quotationResponse);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));

                    String errorData = ((HVolleyError)error).getErrorData();
                    if (errorData != null){
                        Log.e(TAG, "Error data: " + errorData);
                    }

                    DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_quote), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
        }
    }

    private void toViewQuotationResult(QuotationDataResult quotationResult) {
        quotationResult.clientFullName =  mQuotation.client.getFullName();
        quotationResult.clientId = mQuotation.client.id;
        quotationResult.quoteType =  ConstantsUtil.QUOTE_TYPE_GENERAL;
        quotationResult.hidePlanValue = mHidePlanValue;

        IntentHelper.goToQuotationResultActivity(getActivity(), quotationResult);
    }

}
