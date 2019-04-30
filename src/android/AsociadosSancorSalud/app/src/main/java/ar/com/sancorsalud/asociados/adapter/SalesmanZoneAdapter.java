package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.Salesman;

public class SalesmanZoneAdapter extends RecyclerView.Adapter<SalesmanZoneAdapter.ViewHolder> {

    private static final String TAG = "SalesmanZoneAdpt";

    private ArrayList<Salesman> mList;
    private HashMap<Long,Boolean> mItemsSelected = new HashMap<>();


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public ImageView itemIcon;
        public TextView itemSeparator;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);
        }
    }

    public void clearSelections(){
        mItemsSelected = new HashMap<>();
    }

    public HashMap<Long,Boolean> getSelectedItems(){
        return mItemsSelected;
    }


    public SalesmanZoneAdapter(ArrayList<Salesman> items) {
        mList = items;
    }

    public void toggleSelectedItem(long objId){
        Boolean selected = mItemsSelected.get(objId);
        if(selected!=null && selected==true)
            mItemsSelected.remove(objId);
        else mItemsSelected.put(objId,true);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_salesman_zone_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Salesman salesman = mList.get(position);
        setupView(holder,  position, salesman);
    }

    private void setupView(final ViewHolder holder, final int position, Salesman salesman){

        holder.itemIcon.setTag(position);
        holder.itemTitle.setText(salesman.firstname+" "+salesman.lastname);
        //Log.e (TAG, "position: + "  + position + "::" + salesman.firstname+" "+salesman.lastname + " " + salesman.id);

        Boolean selected = mItemsSelected.get(salesman.id);
        if(selected!=null && selected==true){
            holder.itemIcon.setImageResource(R.drawable.ic_check);
        }else{
            holder.itemIcon.setImageResource(R.drawable.ic_no_checked);
        }

        holder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "holder.itemIcon click...");

                int position = (int) view.getTag();
                if(position< mList.size()){
                    Salesman salesman = mList.get(position);
                    toggleSelectedItem(salesman.id);
                }
            }
        });

    }

    public void onSelectAll(){
        for (Salesman salesman: mList) {
            mItemsSelected.put(salesman.id,true);
        }
        notifyDataSetChanged();
    }

    public void onUnSelectAll(){
        for (Salesman salesman: mList) {
            mItemsSelected.put(salesman.id, false);
        }
        notifyDataSetChanged();
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

    public void onRefresh(int position) {
        notifyItemChanged(position);
    }

}
