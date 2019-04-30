package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergio on 2/23/17.
 */

public class SearchAndSelectListAdapter extends RecyclerView.Adapter<SearchAndSelectListAdapter.ViewHolder> {

    private ArrayList<QuoteOption> mList = new ArrayList<>();
    private QuoteOption selectedOption;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public View checkIcon;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.textView);
            checkIcon = view.findViewById(R.id.checkImage);
        }
    }

    public QuoteOption getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public SearchAndSelectListAdapter(List<QuoteOption> items) {
        mList.addAll(items);
    }

    public SearchAndSelectListAdapter() {

    }

    public void setSelection(QuoteOption opt){
        selectedOption = opt;
    }

    @Override
    public SearchAndSelectListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
        return new SearchAndSelectListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchAndSelectListAdapter.ViewHolder holder, final int position) {
        QuoteOption m = mList.get(position);
        setupView(holder,  position, m);
    }

    private void setupView(final SearchAndSelectListAdapter.ViewHolder holder, final int position, QuoteOption opt){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(StringHelper.uppercaseFirstCharacter(opt.title));

        if(selectedOption!=null && opt.id.equalsIgnoreCase(selectedOption.id))
            holder.checkIcon.setVisibility(View.VISIBLE);
        else holder.checkIcon.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<QuoteOption> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setItems(ArrayList<QuoteOption> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

}
