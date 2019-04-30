package ar.com.sancorsalud.asociados.fragments.zoneleader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.PromoterAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseListWithSearchFragment;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sergio on 11/3/16.
 */

public class SalesmanSelectionFragment extends BaseListWithSearchFragment {

    private static final String TAG = "SalesmanSelection";
    private ArrayList<Salesman> mList = new ArrayList<Salesman>();
    private ArrayList<Salesman> mFilterList = new ArrayList<Salesman>();
    private PromoterAdapter mAdapter = null;
    private Button mAssignButton;
    private ArrayList<ProspectiveClient> mSelectedClients;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_salesman_selection, container, false);
        mAssignButton = (Button) view.findViewById(R.id.assign_button);

        setHasOptionsMenu(true);
        setupLayouts(view);
        setupListeners();

        showProgress(true);
        reloadData(true);

        if(mSelectedClients.size()==1)
            setupHeaderList(R.string.accion_assign_txt_singular);
        else setupHeaderList(AppController.getInstance().getResources().getString(R.string.accion_assign_txt_plural, mSelectedClients.size()));

        return view;
    }

    public void setSelectedClients(ArrayList<ProspectiveClient> selectedClients){
        mSelectedClients = selectedClients;
    }

    protected RecyclerView.Adapter getAdapter(){
        if(mAdapter ==null)
            mAdapter = new PromoterAdapter(mList);

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

        mAdapter.clearSelection();

        SalesmanController.getInstance().getSalesman(force, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> var1) {
                mList = var1;
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

    protected void filterDataByText(String filterText){
        mFilterList = new ArrayList<Salesman>();

        if(!filterText.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                Salesman salesman = mList.get(i);
                if (salesman.getFullName().toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(salesman);
                }
            }

            mAdapter.onRefresh(mFilterList);
        }else mAdapter.onRefresh(mList);
        onEndedRefresh();
    }

    protected void setupListeners(){
        super.setupListeners();

        mAssignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Salesman salesman = mAdapter.getSelectedItems();
                if (salesman!=null) {
                    String message = getResources().getString(R.string.assingment_confirmation, Integer.valueOf(mSelectedClients.size()).toString(), salesman.getFullName());
                    new AlertDialog.Builder(getActivity()).setMessage(message).setNegativeButton(R.string.option_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton(R.string.option_accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hideAll();
                            showProgress(true);
                            SalesmanController.getInstance().assignSalesman(salesman.id, mSelectedClients, new Response.Listener<Void>() {
                                @Override
                                public void onResponse(Void response) {
                                    showProgress(false);

                                    Intent intent=new Intent();
                                    getActivity().setResult(RESULT_OK,intent);
                                    getActivity().finish();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    showProgress(false);
                                    showDataListLayout();
                                    DialogHelper.showStandardErrorMessage(getActivity(),null);
                                }
                            });
                        }
                    }).create().show();

                } else {
                    new AlertDialog.Builder(getActivity()).setMessage(getResources().getString(R.string.error_promoter_selection_mandatory)).setPositiveButton(getResources().getString(R.string.option_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }
        });
    }

}