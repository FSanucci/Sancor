package ar.com.sancorsalud.asociados.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DateUtils;

/**
 * Created by sergiocirasa on 29/3/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";

    private ArrayList<Notification> mList;
    private Notification mItemSelected;

    private boolean mIsZoneLeaderRole = false;
    private HashMap<Long,Boolean> mItemsSelected = new HashMap<>();

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public View  itemBox = null;
        public ImageView itemIcon;

        public TextView itemTitle = null;
        public TextView itemDate;
        public ImageView itemPriority;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemBox =  view.findViewById(R.id.item_box);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);

            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemDate = (TextView) view.findViewById(R.id.item_date);
            itemPriority =  (ImageView) view.findViewById(R.id.item_priority);
        }
    }

    public NotificationAdapter(ArrayList<Notification> items, boolean isZoneLeaderRole) {
        mList = items;
        mIsZoneLeaderRole = isZoneLeaderRole;
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
        else
            mItemsSelected.put(objId,true);

        notifyDataSetChanged();
    }


    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, final int position) {
        Notification notification = mList.get(position);
        setupView(holder, position, notification);
    }

    private void setupView(final NotificationAdapter.ViewHolder holder, final int position, Notification notification) {

        if (mIsZoneLeaderRole){
            holder.itemIcon.setVisibility(View.VISIBLE);
            holder.itemIcon.setTag(position);

            Boolean selected = mItemsSelected.get(notification.id);
            if(selected!=null && selected==true){
                holder.itemIcon.setImageResource(R.drawable.ic_check);
            }else{
                holder.itemIcon.setImageResource(R.drawable.ic_no_checked);
            }

        }else{
            holder.itemIcon.setVisibility(View.GONE);
        }


        holder.itemTitle.setText(notification.title != null ? notification.title: "");
        if (notification.date != null) {
            if (mIsZoneLeaderRole) {
                holder.itemDate.setText(ParserUtils.parseDate(notification.date, "dd") + " " + DateUtils.shortMonthText(notification.date));
            }else {
                holder.itemDate.setText(ParserUtils.parseDate(notification.date, "yyyy-MM-dd"));
            }
        }

        if(!notification.isRead){
            holder.itemTitle.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorPrimaryText));
            holder.itemDate.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorAccent));
        }else{
            holder.itemTitle.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorSecondaryText));
            holder.itemDate.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorSecondaryText));
        }


        if (mIsZoneLeaderRole){
            holder.itemPriority.setVisibility(View.VISIBLE);

            if (notification.priority == Notification.Priority.HIGHT) {
                PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                holder.itemPriority.setColorFilter(porterDuffColorFilter);
            }else if (notification.priority == Notification.Priority.MEDIUM) {
                PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                holder.itemPriority.setColorFilter(porterDuffColorFilter);
            }  else if (notification.priority == Notification.Priority.LOW){
               PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
               holder.itemPriority.setColorFilter(porterDuffColorFilter);
            }

        }else{
            holder.itemPriority.setVisibility(View.GONE);
        }


        /*
        holder.itemViewContainer.setTag(notification);
        holder.itemViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemSelected = (Notification) view.getTag();
                notifyDataSetChanged();
            }
        });
        */


        holder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "holder.itemIcon click...");

                int position = (int) view.getTag();
                if(position<mList.size()){
                    Notification notification = mList.get(position);
                    toggleSelectedItem(notification.id);

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

    public void onSelectAll(){
        for (Notification notif: mList) {
            mItemsSelected.put(notif.id,true);
        }
        notifyDataSetChanged();
    }

    public void onUnSelectAll(){
        for (Notification notif: mList) {
            mItemsSelected.put(notif.id, false);
        }
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<Notification> list) {
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