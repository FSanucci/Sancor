package ar.com.sancorsalud.asociados.fragments.zoneleader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.main.MainActivity;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SelectableProspectiveClientsAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseListWithSearchFragment;
import ar.com.sancorsalud.asociados.manager.ProspectiveClientController;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static android.app.Activity.RESULT_OK;


public class PASelectionFragment extends BaseListWithSearchFragment {

    private static final String TAG = "ProspectiveClients";
    private ArrayList<ProspectiveClient> mList = new ArrayList<ProspectiveClient>();
    private ArrayList<ProspectiveClient> mFilterList = new ArrayList<ProspectiveClient>();

    private SelectableProspectiveClientsAdapter mAdapter = null;
    private Button mAssignButton;
    private View mMainContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prospective_clients, container, false);
        mAssignButton = (Button) view.findViewById(R.id.assign_button);
        mMainContainer = view.findViewById(R.id.main_container);

        setHasOptionsMenu(true);
        setupLayouts(view);
        setupListeners();

        reloadData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (Storage.getInstance().hasChangePAQuantityList()){
            Storage.getInstance().setHasChangePAQuantityList(false);

            Log.e(TAG, "onResume-----------------------------");
            AppController.getInstance().updateZoneLeaderAssigmentData();

            showProgress(true);
            reloadData(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsUtil.VIEW_SALESMAN_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            reloadData();
            SnackBarHelper.makeSucessful(mMainContainer, R.string.assing_success);
        }
        else if (requestCode == ConstantsUtil.VIEW_DETAIL_REQUEST_CODE) {
            if (resultCode == ConstantsUtil.RESULT_DATA_UPDATED) {
                reloadData();
            } else {
                reloadData(false);
            }
        }


    }

    protected RecyclerView.Adapter getAdapter() {
        if (mAdapter == null)
            mAdapter = new SelectableProspectiveClientsAdapter(mList);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "Celda Click !  -----------");

                ProspectiveClient client = mList.get(position);
                loadProfile(client);
            }
        });


        return mAdapter;
    }

    @Override
    protected void reloadData() {
        //hideAll();

        showProgress(true);
        reloadData(true);
    }

    @Override
    protected void onRefreshData() {
        onCancellAllRequets();
        showProgress(false);
        reloadData(true);
    }

    public void refreshDataList() {
        reloadData();
    }


    protected void reloadData(boolean force) {
        mAdapter.clearSelections();

        Log.e (TAG, "getAllProspectiveClients *****");

        ProspectiveClientController.getInstance().getAllProspectiveClients(force, new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> var1) {

                mList = new ArrayList<ProspectiveClient>();
                for (int i = var1.size() - 1; i >= 0; i--) {
                    mList.add(var1.get(i));
                }

                 if (mList.isEmpty()) {
                    showProgress(false);
                    showEmptyListLayout();
                } else {
                    int count = countOfUnAssignedProspectiveClients();
                    if (count > 1)
                        setupHeaderList(AppController.getInstance().getResources().getString(R.string.quantity_to_assign_txt_plural, UserController.getInstance().getSignedUser().firstname, count));
                    else
                        setupHeaderList(AppController.getInstance().getResources().getString(R.string.quantity_to_assign_txt_singular, UserController.getInstance().getSignedUser().firstname));
                }
                showProgress(false);
                showDataListLayout();
                mAdapter.onRefresh(mList);
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

    @Override
    protected void didClickItem(int position) {
    }

    @Override
    protected void didLongClickItem(int position) {
    }

    private int countOfUnAssignedProspectiveClients() {
        int count = 0;
        for (ProspectiveClient client : mList) {
            if (!client.isAssigned())
                count++;
        }
        return count;
    }

    protected void filterDataByText(String filterText) {
        mFilterList = new ArrayList<ProspectiveClient>();

        if (!filterText.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                ProspectiveClient assignment = mList.get(i);
                if (assignment.getFullName().toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(assignment);
                }
            }

            mAdapter.onRefresh(mFilterList);
        } else mAdapter.onRefresh(mList);
        onEndedRefresh();
    }


    protected void setupListeners() {
        super.setupListeners();

        mAssignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Long, Boolean> itemsSelected = mAdapter.getSelectedItems();
                ArrayList<ProspectiveClient> list = new ArrayList<>();
                for (ProspectiveClient item : mList) {
                    Boolean selected = itemsSelected.get(item.id);
                    if (selected != null && selected == true) {
                        list.add(item);
                    }
                }

                if (!list.isEmpty()) {
                    Bundle param = new Bundle();
                    param.putSerializable(ConstantsUtil.SELECTED_CLIENTS, list);
                    IntentHelper.goToSalesmanSelectionActivity(getActivity(), param);
                } else {
                    ((MainActivity) getActivity()).showMessage(getResources().getString(R.string.error_assignment_mandatory));
                }
            }
        });
    }


    private void loadProfile(final ProspectiveClient client){
        showProgress(true);

        final long time = System.currentTimeMillis();
        ProspectiveClientController.getInstance().getProspectiveClientProfile(client, new Response.Listener<ProspectiveClient>() {
            @Override
            public void onResponse(ProspectiveClient response) {
                showProgress(false);
                IntentHelper.gotoEditPAActivity(getActivity(), client, client.isAssigned());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

}
