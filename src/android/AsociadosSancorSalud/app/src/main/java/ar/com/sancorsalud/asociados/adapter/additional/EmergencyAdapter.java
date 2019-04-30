package ar.com.sancorsalud.asociados.adapter.additional;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergiocirasa on 4/4/17.
 */

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {

    private ArrayList<QuoteOption> mList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public CheckBox itemCheckbox;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemCheckbox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

    public QuoteOption getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<QuoteOption> getMembers() {
        ArrayList<QuoteOption> list = new ArrayList<QuoteOption>(mList);
        return list;
    }

    public EmergencyAdapter(List<QuoteOption> items) {
        mList.addAll(items);
    }

    public EmergencyAdapter() {

    }

    @Override
    public EmergencyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_checkbox_item, parent, false);
        return new EmergencyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EmergencyAdapter.ViewHolder holder, final int position) {
        QuoteOption m = mList.get(position);
        setupView(holder, position, m);
    }

        private void setupView(final EmergencyAdapter.ViewHolder holder, final int position, QuoteOption quoteOption) {
        holder.itemViewContainer.setTag(position);
        holder.itemCheckbox.setText(StringHelper.uppercaseFirstCharacter(quoteOption.title));
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

    public void addItem(QuoteOption item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<QuoteOption> items) {
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
