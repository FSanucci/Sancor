package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AffiliationMembersAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.OnMemberItemClickListener;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.MembersData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;


public class AffiliationMembersDataFragment extends BaseFragment {

    private static final String TAG = "AF_MEMBERS_FRG";
    private static final String ARG_MEMBERS_DATA = "membersData";
    private static final String ARG_TITULAR_DNI = "titularDNI";
    private static final String ARG_TITULAR_APORTA_MONO = "titularAportaMono";

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private AffiliationMembersAdapter mAdapter;
    private View mMainContainer;
    private List<Member> mMembers;

    private MembersData membersData;
    private long titularDNI;
    private int actualMemberPosition = 0;
    private boolean titularAportaMono;


    public static AffiliationMembersDataFragment newInstance(MembersData param1, long dni, boolean titularAportaMonotributo) {
        AffiliationMembersDataFragment fragment = new AffiliationMembersDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMBERS_DATA, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putBoolean(ARG_TITULAR_APORTA_MONO, titularAportaMonotributo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            membersData =  (MembersData) getArguments().getSerializable(ARG_MEMBERS_DATA);
            titularDNI =  getArguments().getLong(ARG_TITULAR_DNI);
            titularAportaMono = getArguments().getBoolean(ARG_TITULAR_APORTA_MONO, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_member_data, container, false);
        return mMainContainer;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter == null) {
            mAdapter = new AffiliationMembersAdapter();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        ScrollView mScrollView = (ScrollView) view.findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        setupListeners();
        initializeForm();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e (TAG,"MEMBER onActivityResult!!!!" );

        if (resultCode == Activity.RESULT_OK) {
            Member member = (Member) data.getSerializableExtra(ConstantsUtil.RESULT_MEMBER);
            Log.e(TAG, "updating member: " + member.firstname + "::" + member.lastname);

            if (getMembers() != null ) {
                //mAdapter.addItem(member);
                mAdapter.setItemAtIndex(actualMemberPosition, member);
            }
        }
    }

    public boolean isValidSection(){
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private void initializeForm() {

        if (membersData == null){
            membersData = new MembersData();
        }

        mMembers = membersData.members;
        if(mMembers!=null){
            mAdapter.setItems(mMembers);
        }
        else {
            mAdapter.removeAllItems();
        }
    }

    private void setupListeners() {

        mAdapter.setOnItemClickListener(new OnMemberItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "onItemClick():" + position);
                actualMemberPosition = position;

                Member member = mAdapter.getItemAtIndex(position);
                Log.e(TAG, "member parentesco: " + member.parentesco.id);
                IntentHelper.goToAddAffiliationMemberActivity(AffiliationMembersDataFragment.this, titularDNI, titularAportaMono, member, position);
            }

            @Override
            public void onHideItemClick(View v, int position) {
                SnackBarHelper.makeError(v, R.string.affiliation_no_editable_member).show() ;
            }
        });
    }

    public  MembersData getMemebersData(){
        membersData = new MembersData();
        membersData.members = mAdapter.getMembers();


        Log.e(TAG, "getMemebersData !!!! .................");
        for(Member aMember: membersData.members){
            Log.e(TAG, "Member Person CARD ID:"  + aMember.personCardId  + " Nombre : " + ((aMember.firstname != null) ? aMember.firstname: "" ) +  " Parentesco ID  : "  + aMember.parentesco.id  + " **************************");
        }

        return membersData;
    }


    private boolean validateForm() {
        return true;
    }

    public List<Member> getMembers() {
        return mAdapter.getMembers();
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

}
