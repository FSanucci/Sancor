package ar.com.sancorsalud.asociados.fragments.base;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ar.com.sancorsalud.asociados.R;

/**
 * Created by sergio on 11/2/16.
 */

public abstract class BaseListWithSearchFragment extends BaseListFragment{

    protected SearchView mSearchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        setupSearch(searchItem);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void setupSearch(MenuItem searchItem){

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();

        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String filterText) {
                    BaseListWithSearchFragment.this.filterDataByText(filterText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    hideSoftKeyboard(mSearchView);
                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);
        }
    }

    protected abstract void filterDataByText(String query);
}