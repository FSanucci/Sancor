package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.DateUtils;

/**
 * Created by sergio on 11/17/16.
 */

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<ProspectiveClient> mList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public TextView itemInfo;
        public TextView itemDate;
        public TextView itemMonth;
        public View itemDateContainer;
        public TextView itemSeparator;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            itemInfo = (TextView) view.findViewById(R.id.item_info);
            itemDate = (TextView) view.findViewById(R.id.day_number);
            itemMonth = (TextView) view.findViewById(R.id.month);
            itemDateContainer = view.findViewById(R.id.date_content);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
        }
    }

    public ProspectiveClient getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public AppointmentsAdapter(List<ProspectiveClient> items) {
        mList = items;
    }

    @Override
    public AppointmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_appointment_item, parent, false);
        return new AppointmentsAdapter.ViewHolder(view);
    }

    private boolean shouldShowDate(int position, Date date) {
        if (position == 0)
            return true;

        for (int i = 0; i < position; i++) {
            ProspectiveClient client = mList.get(i);
            if (DateUtils.isSameDay(client.appointment.date, date))
                return false;
        }
        return true;
    }

    @Override
    public void onBindViewHolder(final AppointmentsAdapter.ViewHolder holder, final int position) {
        ProspectiveClient client = mList.get(position);
        if (client.appointment != null)
            setupView(holder, position, client, shouldShowDate(position, client.appointment.date));
    }

    private void setupView(final AppointmentsAdapter.ViewHolder holder, final int position, ProspectiveClient client, boolean showDate) {
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(client.firstname + " " + client.lastname);
        holder.itemSubtitle.setText(ParserUtils.parseDate(client.appointment.date, "HH:mm") + " Hs");

        if (client.appointment.address != null) {
            holder.itemInfo.setVisibility(View.VISIBLE);
            holder.itemInfo.setText(client.appointment.address);
        } else {
            if (client.appointment.notes != null) {
                holder.itemInfo.setVisibility(View.VISIBLE);
                holder.itemInfo.setText(client.appointment.notes);
            } else holder.itemInfo.setVisibility(View.GONE);
        }


        if (showDate) {
            holder.itemDateContainer.setVisibility(View.VISIBLE);
            holder.itemMonth.setText(DateUtils.shortMonthText(client.appointment.date));
            holder.itemDate.setText(ParserUtils.parseDate(client.appointment.date, "dd"));
        } else {
            holder.itemDateContainer.setVisibility(View.INVISIBLE);
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