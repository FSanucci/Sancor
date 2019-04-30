package ar.com.sancorsalud.asociados.fragments.quote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.MembersAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.MembersAndUnificationData;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;


public class QuoteMembersAutonomoFragment extends BaseFragment implements IQuoteMember {

    private static final String TAG = "MEMBER_AUTO_FRG";

    private static final String ARG_MEMBERS = "membersData";

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private MembersAdapter mAdapter;
    private View mMainContainer;
    private ArrayList<Member> mMembers;

    private MembersAndUnificationData mMembersAndUnificationData;

    public QuoteMembersAutonomoFragment() {
        // Required empty public constructor
    }

    public static QuoteMembersAutonomoFragment newInstance(MembersAndUnificationData membersAndUnificationData) {
        QuoteMembersAutonomoFragment fragment = new QuoteMembersAutonomoFragment();
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
        mMainContainer = inflater.inflate(R.layout.fragment_member_autonomo_list, container, false);
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

        if (mMembersAndUnificationData == null) {
            mMembersAndUnificationData = new MembersAndUnificationData();
        }
        updateMembers(mMembersAndUnificationData.integrantes);

        setupListeners();
    }

    public void updateMembers(ArrayList<Member> list){
        mMembers = list;
        if(mMembers!=null)
            mAdapter.setItems(mMembers);
        else mAdapter.removeAllItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_CODE) {
            Member member = (Member) data.getSerializableExtra(ConstantsUtil.RESULT_MEMBER);
            if (getMembers() != null && !getMembers().isEmpty()) {
                if (isValidMember(member)) {
                    mAdapter.addItem(member);
                }else{
                    ((BaseActivity)getActivity()).showMessage(getResources().getString(R.string.conyugue_already_exist_error));
                }
            }else{
                mAdapter.addItem(member);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupListeners() {
        View v = mMainContainer.findViewById(R.id.add_button);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoAddMemberActivity(QuoteMembersAutonomoFragment.this);
            }
        });
    }


    public MembersAndUnificationData getMembersAndUnificationData(){
        mMembersAndUnificationData.integrantes = mAdapter.getMembers();
        return mMembersAndUnificationData;
    }

    private ArrayList<Member> getMembers() {
        return mAdapter.getMembers();
    }

    public boolean isValidSection() {
        return true;
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
