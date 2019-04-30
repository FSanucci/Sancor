package ar.com.sancorsalud.asociados.fragments.zoneleader;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SalesmanManualQuoteAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseListWithSearchFragment;
import ar.com.sancorsalud.asociados.manager.SalesmanController;
import ar.com.sancorsalud.asociados.model.user.Salesman;


public class ManualQuoteListFragment extends BaseListWithSearchFragment{

    private static final String TAG = "MANUAL_QUOTE_FRG";
    private ArrayList<Salesman> mList = new ArrayList<Salesman>();
    private ArrayList<Salesman> mFilterList = new ArrayList<Salesman>();
    private SalesmanManualQuoteAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manual_quote_list, container, false);

        setupLayouts(view);
        setupListeners();
        setHasOptionsMenu(true);

        showProgress(true);
        reloadData(false);
        return view;
    }

    protected RecyclerView.Adapter getAdapter(){
        if(mAdapter ==null)
            mAdapter = new SalesmanManualQuoteAdapter(mList);

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

        SalesmanController.getInstance().getSalesman(force, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> var1) {
                mList = new ArrayList<Salesman>(var1);
                showProgress(false);
                onEndedRefresh();
                if (mList.isEmpty()){
                    showEmptyListLayout();
                }else {
                    showDataListLayout();
                    mAdapter.onRefresh(mList);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                onEndedRefresh();
                showProgress(false);
                showErrorListLayout();
            }
        });

    }

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

    protected  void didClickItem(int position){}
    protected  void didLongClickItem(int position){}
}
