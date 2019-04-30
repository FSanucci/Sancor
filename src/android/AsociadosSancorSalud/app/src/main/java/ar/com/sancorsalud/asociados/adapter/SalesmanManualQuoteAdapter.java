package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.user.Salesman;

public class SalesmanManualQuoteAdapter extends RecyclerView.Adapter<SalesmanManualQuoteAdapter.ViewHolder> {

    private List<Salesman> mList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;

        public TextView badge_total_manual_quote;
        public TextView itemSeparator;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);

            badge_total_manual_quote = (TextView) view.findViewById(R.id.badge_total_manual_quote);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
        }
    }

    public Salesman getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public SalesmanManualQuoteAdapter(List<Salesman> items) {
        mList = items;
    }

    @Override
    public SalesmanManualQuoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_salesman_item3, parent, false);
        return new SalesmanManualQuoteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SalesmanManualQuoteAdapter.ViewHolder holder, final int position) {
        Salesman salesman = mList.get(position);
        setupView(holder,  position, salesman);
    }

    private void setupView(final SalesmanManualQuoteAdapter.ViewHolder holder, final int position, Salesman salesman){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(salesman.getFullName());

        holder.itemSubtitle.setText("Zona: "+ salesman.zoneId);
        holder.badge_total_manual_quote.setText(""+salesman.totalManualQuotes);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<Salesman> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
