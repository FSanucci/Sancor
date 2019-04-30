package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DateUtils;

/**
 * Created by sergio on 11/10/16.
 */

public class ProspectiveClientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProspectiveClient> mList;
    private boolean showCardId = false;

    public static class StateViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;

        public StateViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public TextView itemInfo;
        public TextView itemStatus;
        public ImageView itemLink;
        public ImageView itemIcon;
        public TextView itemSeparator;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            itemInfo = (TextView) view.findViewById(R.id.item_info);
            itemStatus = (TextView) view.findViewById(R.id.item_status);
            itemLink = (ImageView) view.findViewById(R.id.item_link_icon);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
        }
    }



    public ProspectiveClient getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public ProspectiveClientsAdapter(List<ProspectiveClient> items) {
        mList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_prospective_client, parent, false);
            return new ProspectiveClientsAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_prospective_client_state, parent, false);
            return new ProspectiveClientsAdapter.StateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ProspectiveClient client = mList.get(position);
        if (client.state != null) {
            setupViewForState((StateViewHolder) holder, position, client);
        } else if(client.filter != null)
            setupViewForFilter((ProspectiveClientsAdapter.ViewHolder) holder, position, client);
    }

    private void setupViewForState(final ProspectiveClientsAdapter.StateViewHolder holder, final int position, final ProspectiveClient client) {
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(client.getFullName());

        if (client.state == ProspectiveClient.State.CARDS_IN_PROCESS || client.state == ProspectiveClient.State.INCORRECT_CARD || client.state == ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT) {

            String str = "";
            if (client.getAge().length() > 0)
                str = client.getAge() + " años";
            if (client.getBirthday().length() > 0)
                str += " (" + client.getBirthday() + ")";
            if (client.zone != null)
                str += " - " + client.zone.name;
            holder.itemSubtitle.setText(str);
        } else {
            holder.itemSubtitle.setText("Solicitud " + client.cardId);
        }
    }

    private void setupViewForFilter(final ProspectiveClientsAdapter.ViewHolder holder, final int position, final ProspectiveClient client) {
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(client.getFullName());

        String str = "";
        if (client.getAge().length() > 0)
            str = client.getAge() + " años";
        if (client.getBirthday().length() > 0)
            str += " (" + client.getBirthday() + ")";
        if (client.zone != null)
            str += " - " + client.zone.name;
        if (str != null && str.length() > 0) {
            holder.itemSubtitle.setVisibility(View.VISIBLE);
            holder.itemSubtitle.setText(str);
        } else {
            holder.itemSubtitle.setVisibility(View.GONE);
        }

        holder.itemStatus.setText("");
        holder.itemInfo.setText("");
        holder.itemStatus.setVisibility(View.VISIBLE);
        holder.itemInfo.setVisibility(View.VISIBLE);

        if (client.id == 161)
            Log.e("", "");

        holder.itemLink.setVisibility(View.GONE);

        if (client.filter == ProspectiveClient.Filter.NO_SCHEDULED) {
            holder.itemIcon.setVisibility(View.VISIBLE);
            long daysToExpire = client.getDaysToExpire();
            if (daysToExpire <= 1) {
                holder.itemStatus.setVisibility(View.VISIBLE);
                holder.itemStatus.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorRed));
                if (daysToExpire == 1)
                    holder.itemStatus.setText(AppController.getInstance().getResources().getString(R.string.expired_tomorrow));
                else if (daysToExpire == 0)
                    holder.itemStatus.setText(AppController.getInstance().getResources().getString(R.string.expired_today));
                else
                    holder.itemStatus.setText(AppController.getInstance().getResources().getString(R.string.defeated));

            } else if (DateUtils.isToday(client.assignedDate)) {
                holder.itemInfo.setText(" - " + daysToExpire + " dias para contactar");
                holder.itemStatus.setVisibility(View.VISIBLE);
                holder.itemStatus.setText(AppController.getInstance().getResources().getString(R.string.new_item));
                holder.itemStatus.setTextColor(AppController.getInstance().getResources().getColor(R.color.colorGreen));
            } else {
                holder.itemInfo.setText(daysToExpire + " dias para contactar");
                holder.itemStatus.setText("");
                holder.itemStatus.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.itemStatus.setVisibility(View.INVISIBLE);
            holder.itemInfo.setText(client.statusComment);
        }

        if (client.filter == ProspectiveClient.Filter.NO_SCHEDULED) {
            holder.itemIcon.setImageResource(R.drawable.ic_event_not_scheduled);
            holder.itemIcon.setVisibility(View.VISIBLE);
        } else if (client.filter == ProspectiveClient.Filter.SCHEDULED) {
            holder.itemIcon.setImageResource(R.drawable.ic_event_scheduled);
            holder.itemIcon.setVisibility(View.VISIBLE);
            holder.itemStatus.setVisibility(View.GONE);
            holder.itemInfo.setVisibility(View.GONE);

            if (client.appointment != null && client.appointment.date != null) {
                holder.itemInfo.setText(ParserUtils.printableDate(client.appointment.date));
            }

        } else if (client.filter == ProspectiveClient.Filter.QUOTED) {
            holder.itemIcon.setImageResource(R.drawable.ic_quoted);
            holder.itemIcon.setVisibility(View.VISIBLE);
            holder.itemStatus.setVisibility(View.GONE);
            holder.itemInfo.setVisibility(View.GONE);

            if (client.quotatedLink != null && !client.quotatedLink.isEmpty()) {
                holder.itemLink.setImageResource(R.drawable.ic_file);
                holder.itemLink.setVisibility(View.VISIBLE);
            }
        } else {
            holder.itemIcon.setVisibility(View.GONE);
        }

        /*if (holder.itemStatus.getText().length() == 0 && holder.itemInfo.getText().length() == 0) {
            holder.itemStatus.setVisibility(View.GONE);
            holder.itemInfo.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ProspectiveClient client = mList.get(position);
        if(client.filter != null)
            return 0;
        else if(client.state != null)
            return  1;
        else return 1;
        /*if (this.showCardId)
            return 1;
        else return 0;*/
    }

    public void onRefresh(List<ProspectiveClient> list) {
        this.showCardId = false;
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void onRefresh(List<ProspectiveClient> list, boolean showCardId) {
        this.showCardId = showCardId;
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
