package ar.com.sancorsalud.asociados.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SearchAndSelectListAdapter;
import ar.com.sancorsalud.asociados.interfaces.ClickListener;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.RecyclerTouchListener;

public class SearchActivity extends BaseActivity {

    protected SearchView searchView;
    protected RecyclerView recyclerView;
    protected SearchAndSelectListAdapter adapter;
    protected LinearLayoutManager mLinearLayoutManager;
    private boolean searching = false;
    private QuoteOption quoteOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupToolbar(toolbar,R.string.search_empresa);

        mMainContainer = findViewById(R.id.main);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchAndSelectListAdapter();
        mLinearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        setupListener();
    }

    private void setupListener(){

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                quoteOption = adapter.getItemAtIndex(position);
                adapter.setSelection(quoteOption);
                searchView.setQuery(quoteOption.title,true);
            }

            @Override
            public void onLongClick(View view, int position, float xx, float yy) {

            }
        }));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String filterText) {
                filterDataByText(filterText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyboard(searchView);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
    }

    private void filterDataByText(String query){
        if(searching)
            return;

        searching=true;
        HRequest request = RestApiServices.createGetSearchEmpresaRequest(query, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {
                adapter.setItems(response);
                searching = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                searching = false;
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }
}
