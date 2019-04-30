package ar.com.sancorsalud.asociados.fragments.salesman;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.AppointmentsAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseListWithSearchFragment;
import ar.com.sancorsalud.asociados.manager.ProspectiveClientController;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;


public class MyAppointmentsFragment extends BaseListWithSearchFragment {

    private static final String TAG = "MyAppsFRG";
    private ArrayList<ProspectiveClient> mList = new ArrayList<ProspectiveClient>();
    private ArrayList<ProspectiveClient> mFilterList = new ArrayList<ProspectiveClient>();
    private AppointmentsAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);

        setHasOptionsMenu(true);
        setupLayouts(view);
        setupListeners();

        showProgress(true);
        reloadData(false);
        return view;
    }

    protected RecyclerView.Adapter getAdapter(){
        if(mAdapter ==null)
            mAdapter = new AppointmentsAdapter(mList);
        return mAdapter;
    }

    @Override
    protected void reloadData(){
        hideAll();
        showProgress(true);
        reloadData(true);
    }

    @Override
    protected void onRefreshData(){
        onCancellAllRequets();
        showProgress(false);
        reloadData(true);
    }


    protected void reloadData(boolean force){

       ProspectiveClientController.getInstance().cancelRequest();

       // TODO NOT PAGGING
       ProspectiveClientController.getInstance().getMyAssignedProspectiveClients(force, ProspectiveClient.Filter.SCHEDULED, new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> var1) {
                mList = var1;
                Collections.sort(mList, new Comparator<ProspectiveClient>() {
                    public int compare(ProspectiveClient o1, ProspectiveClient o2) {

                        if(o1.appointment!=null && o1.appointment.date!=null && o2.appointment!=null && o2.appointment.date!=null)
                            return o1.appointment.date.compareTo(o2.appointment.date);
                        else if(o2.appointment==null || o2.appointment.date==null){
                            return 1;
                        }else return -1;
                    }
                });

                showProgress(false);
                if (mList.isEmpty()){
                    showEmptyListLayout();
                }
                else {
                    showDataListLayout();
                    mAdapter.onRefresh(mList);
                }
                onEndedRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                showProgress(false);
                showErrorListLayout();
                onEndedRefresh();
            }
        });


    }

    protected void didClickItem(int position){}
    protected void didLongClickItem(int position) {}

    protected void filterDataByText(String filterText) {
        mFilterList = new ArrayList<ProspectiveClient>();

        if (!filterText.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                ProspectiveClient assignment = mList.get(i);
                if (assignment.firstname.toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(assignment);
                }else if (assignment.lastname.toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(assignment);
                }
            }

            mAdapter.onRefresh(mFilterList);
        } else mAdapter.onRefresh(mList);
        onEndedRefresh();
    }

    protected void setupListeners(){
        super.setupListeners();


    }

}