package ar.com.sancorsalud.asociados.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.AppController;

public class SelectableProspectiveClientsAdapter extends RecyclerView.Adapter<SelectableProspectiveClientsAdapter.ViewHolder> {

    private static final String TAG  = "SELECT_ADPT";

    private List<ProspectiveClient> mList;
    private HashMap<Long,Boolean> mItemsSelected = new HashMap<>();

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public View  itemBox = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public TextView itemInfo;
        public ImageView itemIcon;
        public ImageView itemRightIcon;
        public TextView itemSeparator;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemBox =  view.findViewById(R.id.item_box);

            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            itemInfo = (TextView) view.findViewById(R.id.item_info);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);
            itemRightIcon = (ImageView) view.findViewById(R.id.item_right_icon);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
        }
    }

    public void clearSelections(){
        mItemsSelected = new HashMap<>();
    }

    public HashMap<Long,Boolean> getSelectedItems(){
        return mItemsSelected;
    }

    public void toggleSelectedItem(long objId){
        Boolean selected = mItemsSelected.get(objId);
        if(selected!=null && selected==true)
            mItemsSelected.remove(objId);
        else mItemsSelected.put(objId,true);
        notifyDataSetChanged();
    }

    public SelectableProspectiveClientsAdapter(List<ProspectiveClient> items) {
        mList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_assignment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ProspectiveClient client = mList.get(position);
        setupView(holder,  position, client);
    }

    private String getDayString(int days){
        if (days == 1){
            return AppController.getInstance().getResources().getString(R.string.expiration_day_singular);
        }else{
            return AppController.getInstance().getResources().getString(R.string.expiration_day_plural, Long.valueOf(days).toString());
        }
    }

    private void setupView(final ViewHolder holder, final int position, ProspectiveClient client){
        holder.itemIcon.setTag(position);
        holder.itemTitle.setText(client.getFullName());

        String str = "";
        if(client.getAge().length()>0)
            str = client.getAge()+ " años";

        if(client.getBirthday().length()>0)
            str += " ("+client.getBirthday()+")";

        if(client.description !=null)
            str += " - "+client.description;

        if(str != null && str.length()>0) {
            holder.itemSubtitle.setVisibility(View.VISIBLE);
            holder.itemSubtitle.setText(str);
        }else{
            holder.itemSubtitle.setVisibility(View.GONE);
        }

        if(client.isAssigned()){
            holder.itemInfo.setText((client.salesmanName != null ? client.salesmanName : "") +" - "+getDayString(client.countOfDaysAssignedToSalesman));
            holder.itemIcon.setImageResource(R.drawable.ic_checked);
            holder.itemRightIcon.setVisibility(View.GONE);
            holder.itemTitle.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorDisabledText));
            holder.itemSubtitle.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorDisabledText));
            holder.itemInfo.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorDisabledText));
        }else{
            holder.itemInfo.setText(getDayString(client.countOfDaysAssignedToLeader));
            holder.itemTitle.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorPrimaryText));
            holder.itemSubtitle.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorSecondaryText));
            holder.itemInfo.setTextColor(ContextCompat.getColor(AppController.getInstance(),R.color.colorSecondaryText));
            Boolean selected = mItemsSelected.get(client.id);
            if(selected!=null && selected==true){
                holder.itemIcon.setImageResource(R.drawable.ic_check);
            }else{
                holder.itemIcon.setImageResource(R.drawable.ic_no_checked);
                holder.itemRightIcon.setVisibility(View.VISIBLE);
            }
        }

        holder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "holder.itemIcon click...");


                int position = (int) view.getTag();
                if(position<mList.size()){
                    ProspectiveClient client = mList.get(position);
                    if(!client.isAssigned())
                        toggleSelectedItem(client.id);
                }
            }
        });

        if (mOnItemClickListener != null) {
            holder.itemBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "holder.itemBox click...");
                    mOnItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<ProspectiveClient> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
