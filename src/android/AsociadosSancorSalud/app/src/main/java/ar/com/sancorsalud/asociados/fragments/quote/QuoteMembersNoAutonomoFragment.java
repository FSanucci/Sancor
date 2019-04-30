package ar.com.sancorsalud.asociados.fragments.quote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.activity.quotation.AddAporteActivity;
import ar.com.sancorsalud.asociados.adapter.MembersAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.MembersAndUnificationData;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class QuoteMembersNoAutonomoFragment extends BaseFragment implements IQuoteMember {

    private static final String TAG = "MEMBER_NO_AUTO_FRG";

    private static final String ARG_MEMBERS = "membersData";

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private MembersAdapter mAdapter;
    private View mMainContainer;

    private RadioGroup mUnificaRadioGroup;
    private RadioButton mUnificaRadioButton;
    private RadioButton mNoUnificaRadioButton;
    private View mUnificaErrorView;

    private LinearLayout  mMainAffiliationBox;
    private RadioGroup mMainAffiliationRadioGroup;
    private RadioButton mTitularSiMainAffiliationRadioButton;
    private RadioButton mTitularNoMainAffiliationRadioButton;
    private View mTitularMainAffiliationErrorView;

    private RelativeLayout conyugeAsociadoBox;
    private TextView mConyugeDataView;
    private TextView mConyugePlanesView;

    private MembersAndUnificationData mMembersAndUnificationData;

    public QuoteMembersNoAutonomoFragment() {
        // Required empty public constructor
    }

    public static QuoteMembersNoAutonomoFragment newInstance(MembersAndUnificationData membersAndUnificationData) {
        QuoteMembersNoAutonomoFragment fragment = new QuoteMembersNoAutonomoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMBERS, membersAndUnificationData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMembersAndUnificationData = (MembersAndUnificationData) getArguments().getSerializable(ARG_MEMBERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainContainer = inflater.inflate(R.layout.fragment_member_no_autonomo_list, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter == null) {
            mAdapter = new MembersAdapter();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        mUnificaRadioGroup = (RadioGroup) view.findViewById(R.id.aporte_unificados);
        mUnificaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.si_aporte_unificados);
        mNoUnificaRadioButton = (RadioButton) mMainContainer.findViewById(R.id.no_aporte_unificados);
        mUnificaErrorView = mMainContainer.findViewById(R.id.unifica_error);

        mMainAffiliationBox = (LinearLayout) mMainContainer.findViewById(R.id.main_affiliation_box);
        mMainAffiliationRadioGroup = (RadioGroup) view.findViewById(R.id.main_affiliation_group);
        mTitularSiMainAffiliationRadioButton = (RadioButton) mMainContainer.findViewById(R.id.si_main_affiliation);
        mTitularNoMainAffiliationRadioButton = (RadioButton) mMainContainer.findViewById(R.id.no_main_affiliation);
        mTitularMainAffiliationErrorView = mMainContainer.findViewById(R.id.main_affiliation_error);

        conyugeAsociadoBox = (RelativeLayout) mMainContainer.findViewById(R.id.conyuge_asociado_box);
        mConyugeDataView = (TextView)mMainContainer.findViewById(R.id.conyuge_user_data_txt);
        mConyugePlanesView = (TextView)mMainContainer.findViewById(R.id.conyuge_planes_txt);

        initializeForm();
        setupListeners();
    }

    private void initializeForm() {

        if (mMembersAndUnificationData == null) {
            mMembersAndUnificationData = new MembersAndUnificationData();
        }
        updateMembers( mMembersAndUnificationData.integrantes);

        if (mMembersAndUnificationData.unificaAportes != null) {

            if (mMembersAndUnificationData.unificaAportes) {
                mUnificaRadioButton.setChecked(true);
                mNoUnificaRadioButton.setChecked(false);

                mMainAffiliationBox.setVisibility(View.VISIBLE);
            }else{
                mUnificaRadioButton.setChecked(false);
                mNoUnificaRadioButton.setChecked(true);

                mMainAffiliationBox.setVisibility(View.GONE);
                mMembersAndUnificationData.titularMainAffilliation = true;
            }
        }else{
            // default options
            mUnificaRadioButton.setChecked(false);
            mNoUnificaRadioButton.setChecked(true);
            mMembersAndUnificationData.unificaAportes = false;

            mMainAffiliationBox.setVisibility(View.GONE);
            mMembersAndUnificationData.titularMainAffilliation = true;
        }

        //if (mMembersAndUnificationData.unificationType == 1){
            if (mMembersAndUnificationData.conyugeQuotation != null) {
              showConyugeData(mMembersAndUnificationData.conyugeQuotation);
            }else{
                conyugeAsociadoBox.setVisibility(View.GONE);
            }
        //}

        if (mMembersAndUnificationData.titularMainAffilliation != null) {
            if (mMembersAndUnificationData.titularMainAffilliation) {
                mTitularSiMainAffiliationRadioButton.setChecked(true);
                mTitularNoMainAffiliationRadioButton.setChecked(false);
            }else{
                mTitularSiMainAffiliationRadioButton.setChecked(false);
                mTitularNoMainAffiliationRadioButton.setChecked(true);
            }
        }


    }

    public void updateMembers(ArrayList<Member> list){
        mMembersAndUnificationData.integrantes = list;
        if(mMembersAndUnificationData.integrantes!=null) {
            mAdapter.setItems(mMembersAndUnificationData.integrantes);
        }

        else mAdapter.removeAllItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Member member = (Member) data.getSerializableExtra(ConstantsUtil.RESULT_MEMBER);
            if (getMembers() != null && !getMembers().isEmpty()) {
                if (isValidMember(member)) {
                    checkMemberUnificationType(member);
                }else{
                    ((BaseActivity)getActivity()).showMessage(getResources().getString(R.string.conyugue_already_exist_error));
                }
            }else{
                checkMemberUnificationType(member);
            }
        }
    }


    public MembersAndUnificationData getMembersAndUnificationData(){
         mMembersAndUnificationData.integrantes = mAdapter.getMembers();

        if (!mMembersAndUnificationData.unificaAportes){
            mMembersAndUnificationData.conyugeQuotation = null;
        }

        return mMembersAndUnificationData;
    }

    private ArrayList<Member> getMembers() {
        return mAdapter.getMembers();
    }

    private void setupListeners() {

        View v = mMainContainer.findViewById(R.id.add_button);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCheckUnifica()) {

                    if (!mUnificaRadioButton.isChecked() && mMembersAndUnificationData.isEmpleadaDomestica){
                        SnackBarHelper.makeError(mMainContainer,R.string.unifica_empleada_domestica_error).show();
                    }else {
                        IntentHelper.gotoAddMemberActivity(QuoteMembersNoAutonomoFragment.this, mMembersAndUnificationData.unificaAportes);
                    }
                }
            }
        });

        mUnificaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.si_aporte_unificados){
                    mMembersAndUnificationData.unificaAportes = true;
                    conyugeAsociadoBox.setVisibility(View.VISIBLE);
                    mMainAffiliationBox.setVisibility(View.VISIBLE);
                } else {
                    mMembersAndUnificationData.unificaAportes = false;
                    conyugeAsociadoBox.setVisibility(View.GONE);
                    mMainAffiliationBox.setVisibility(View.GONE);
                }
            }
        });

        mMainAffiliationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.si_main_affiliation){
                    mMembersAndUnificationData.titularMainAffilliation = true;
                } else {
                    mMembersAndUnificationData.titularMainAffilliation = false;
                }
            }
        });

    }

    public boolean isValidSection() {
        boolean isValid = true;

        isValid = isValid & hasCheckUnifica();

        if (isValid) {
            if (mUnificaRadioButton.isChecked()) {
                if (!hasConyuge()) {
                    SnackBarHelper.makeError(mMainContainer, R.string.unifica_conyuge_error).show();
                    isValid = false;
                }
            }else{
                if (mMembersAndUnificationData.isEmpleadaDomestica){
                    SnackBarHelper.makeError(mMainContainer,R.string.unifica_empleada_domestica_error).show();
                    isValid = false;
                }
            }
            if (mMainAffiliationBox.getVisibility() == View.VISIBLE) {
                if (!mTitularSiMainAffiliationRadioButton.isChecked() && !mTitularNoMainAffiliationRadioButton.isChecked()) {
                    mTitularMainAffiliationErrorView.setVisibility(View.VISIBLE);
                    isValid = false;
                } else mTitularMainAffiliationErrorView.setVisibility(View.GONE);
            }
        }
        return isValid;
    }

    private boolean hasCheckUnifica(){

        boolean hasCheckUnifica = true;

        if (!mUnificaRadioButton.isChecked() && !mNoUnificaRadioButton.isChecked()) {
            mUnificaErrorView.setVisibility(View.VISIBLE);
            hasCheckUnifica = false;
        } else mUnificaErrorView.setVisibility(View.GONE);

        return hasCheckUnifica;
    }

    private boolean hasConyuge(){

        boolean hasConyuge = false;
        for(Member member: getMembers()){
            if ((member.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || member.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER)) ){
                hasConyuge = true;
                break;
            }
        }
        return hasConyuge;
    }


    private boolean isValidMember(Member aMember){
        boolean isValid = true;
        for(Member member: getMembers()){
            if ((aMember.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || aMember.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER) ) && ( (member.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER)  || member.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER)))){
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    /*
     * When added memeber we need to check unification type N/N o N/E
     * N/N  search conyuge by dni not have data
     * N/E  search conyuge by dni  gives result data so its existent conyuge for System
     */
    private void checkMemberUnificationType(Member member){
        if ((member.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || member.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))){
            //final String conyugeDni = "29900116";
            checkConyugeUnificationType(member);
        }else{
            mAdapter.addItem(member);
        }
    }

    private void checkConyugeUnificationType(final Member member) {

        if (AppController.getInstance().isNetworkAvailable()) {
            //progressBar.setVisibility(View.VISIBLE);

            QuotationController.getInstance().quotationParametesForExistentClient(Long.valueOf(member.dni), true, new Response.Listener<Quotation>() {
                @Override
                public void onResponse(Quotation conyugeQuotation) {
                    Log.e(TAG, "Conyuge quotation parameters  ok .....");
                    //progressBar.setVisibility(View.GONE);

                    if (conyugeQuotation != null &&  conyugeQuotation.client.dni != 0){

                        // UNIFICATION N/E
                        mMembersAndUnificationData.unificationType = 1;
                        mMembersAndUnificationData.conyugeQuotation = conyugeQuotation;

                        if (conyugeQuotation.titular != null && conyugeQuotation.titular.age > 0) {
                            // correct age if conyuge exist
                            member.age = conyugeQuotation.titular.age;
                        }
                        mAdapter.addItem(member);

                        showConyugeData(conyugeQuotation);

                        if (conyugeQuotation.integrantes != null && !conyugeQuotation.integrantes.isEmpty());
                            for (Member innerMember : conyugeQuotation.integrantes) {
                                innerMember.existent = true;

                                if ((!innerMember.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) && !innerMember.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))) {
                                    innerMember.readOnly = true;
                                    mAdapter.addItem(innerMember);
                                }
                                //mAdapter.addItem(innerMember);
                            }
                    }else{

                        // UNIFICATION N/N
                        Log.e(TAG, "No Conyuge found !!! .....");
                        mAdapter.addItem(member);

                        mMembersAndUnificationData.unificationType = 0;
                        mMembersAndUnificationData.conyugeQuotation = null;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //progressBar.setVisibility(View.GONE);
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    Log.e(TAG, "Error No Conyuge found !!! .....");

                    if (error != null && error.getMessage() != null && !error.getMessage().trim().isEmpty()) {
                        DialogHelper.showMessage(getActivity(), error.getMessage());
                    }

                    mMembersAndUnificationData.unificationType = 0;
                    mMembersAndUnificationData.conyugeQuotation = null;
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
        }
    }


    private void showConyugeData(Quotation conyugeQuotation){

        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.help_unification_new_existent_pago));

        conyugeAsociadoBox.setVisibility(View.VISIBLE);
        mMainAffiliationBox.setVisibility(View.VISIBLE);

        Client client = conyugeQuotation.client;

        Log.e(TAG, "conyuge numero_cuenta: " +  conyugeQuotation.accountNumber);
        Log.e(TAG, "conyuge numero_subcuenta: " +  conyugeQuotation.accountSubNumber);

        StringBuffer txt = new StringBuffer();
        txt.append("Nombre y Apellido:<br>");
        txt.append("<b>" + client.getFullName() + "</b><br>");
        txt.append("DNI: " + (client.dni != -1 ? Long.valueOf(client.dni) : "") + "<br>");

        if (conyugeQuotation.titular != null && conyugeQuotation.titular.age > 0) {
            txt.append("Edad: " + conyugeQuotation.titular.age + " años<br>");
        }

        txt.append("Segmento: " +((conyugeQuotation.segmento != null &&  conyugeQuotation.segmento.title != null) ? conyugeQuotation.segmento.title : "") + "<br>");
        txt.append("Forma de Ingreso: " +((conyugeQuotation.formaIngreso != null &&  conyugeQuotation.formaIngreso.title != null) ? conyugeQuotation.formaIngreso.title : "") + "<br>");
        txt.append("Fecha de Ingreso: " +((conyugeQuotation.titular != null && conyugeQuotation.titular.inputDate != null)? conyugeQuotation.titular.inputDate : "") + "<br>");

        mConyugeDataView.setText(Html.fromHtml(txt.toString()));

        List<QuotedClientData> quotedClientDataList = client.quotedDataList;
        StringBuffer sb = new StringBuffer();
        for (QuotedClientData data : quotedClientDataList) {
            //sb.append("<b>ID: " + ((data.id != -1) ? data.id: "") + "</b><br>");
            sb.append("<b>Planes Cotizados</b><br>");
            for (Plan plan : data.planes) {
                if (plan.descripcionPlan != null) {
                    sb.append("&nbsp;" + plan.descripcionPlan + "<br>");
                }
            }
        }
        mConyugePlanesView.setText(Html.fromHtml(sb.toString()));
    }
}
