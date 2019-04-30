package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.user.Salesman;

/**
 * Created by sergio on 1/20/17.
 */

public class SalesmanAdapter extends RecyclerView.Adapter<SalesmanAdapter.ViewHolder> {

    private List<Salesman> mList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public TextView rightBadge;
        public TextView leftBadge;
        public TextView itemSeparator;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            rightBadge = (TextView) view.findViewById(R.id.item_total);
            leftBadge = (TextView) view.findViewById(R.id.item_pendings);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
        }
    }

    public Salesman getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public SalesmanAdapter(List<Salesman> items) {
        mList = items;
    }

    @Override
    public SalesmanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_salesman_item2, parent, false);
        return new SalesmanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SalesmanAdapter.ViewHolder holder, final int position) {
        Salesman client = mList.get(position);
        setupView(holder,  position, client);
    }

    private void setupView(final SalesmanAdapter.ViewHolder holder, final int position, Salesman salesman){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(salesman.getFullName());
        holder.itemSubtitle.setText("Zona: "+salesman.zoneId);
        holder.rightBadge.setText(""+salesman.totalAssignments);
        holder.leftBadge.setText(""+salesman.pendingAssignments);
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
