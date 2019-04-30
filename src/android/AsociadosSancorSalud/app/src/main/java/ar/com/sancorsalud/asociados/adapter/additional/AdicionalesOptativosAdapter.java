package ar.com.sancorsalud.asociados.adapter.additional;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;


public class AdicionalesOptativosAdapter extends RecyclerView.Adapter<AdicionalesOptativosAdapter.ViewHolder> {

    private ArrayList<AdicionalesOptativosData.OpcionalData> mList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTxt;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTxt = (TextView) view.findViewById(R.id.item_txt);
        }
    }

    public AdicionalesOptativosData.OpcionalData getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<AdicionalesOptativosData.OpcionalData> getItems() {
        ArrayList<AdicionalesOptativosData.OpcionalData> list = new ArrayList<AdicionalesOptativosData.OpcionalData>(mList);
        return list;
    }

    public AdicionalesOptativosAdapter(List<AdicionalesOptativosData.OpcionalData> items) {
        mList.addAll(items);
    }

    public AdicionalesOptativosAdapter() {

    }

    @Override
    public AdicionalesOptativosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_opcionales_item, parent, false);
        return new AdicionalesOptativosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdicionalesOptativosAdapter.ViewHolder holder, final int position) {
        AdicionalesOptativosData.OpcionalData m = mList.get(position);
        setupView(holder, position, m);
    }

        private void setupView(final AdicionalesOptativosAdapter.ViewHolder holder, final int position, AdicionalesOptativosData.OpcionalData opcionalData) {
        holder.itemViewContainer.setTag(position);
        holder.itemTxt.setText(opcionalData.descripcionPlan);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<AdicionalesOptativosData.OpcionalData> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(AdicionalesOptativosData.OpcionalData item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<AdicionalesOptativosData.OpcionalData> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        mList.clear();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mList.remove(position);
            notifyDataSetChanged();
        }
    };

}
