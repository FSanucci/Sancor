package ar.com.sancorsalud.asociados.adapter.additional;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergiocirasa on 4/4/17.
 */

public class SupraAdapter extends RecyclerView.Adapter<SupraAdapter.ViewHolder> {

    private ArrayList<QuoteOption> mList = new ArrayList<>();
    private int mSelectedItemIndex = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public RadioButton itemRadioButton;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemRadioButton = (RadioButton) view.findViewById(R.id.radiobutton);
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

    public SupraAdapter(List<QuoteOption> items) {
        mList.addAll(items);
    }

    public SupraAdapter() {

    }

    @Override
    public SupraAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_radiobutton_item, parent, false);
        return new SupraAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SupraAdapter.ViewHolder holder, final int position) {
        QuoteOption m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final SupraAdapter.ViewHolder holder, final int position, QuoteOption quoteOption) {
        holder.itemViewContainer.setTag(position);
        holder.itemRadioButton.setText(StringHelper.uppercaseFirstCharacter(quoteOption.title));
        holder.itemRadioButton.setOnClickListener(clickListener);
        holder.itemRadioButton.setTag(position);

        if(position==mSelectedItemIndex)
            holder.itemRadioButton.setChecked(true);
        else holder.itemRadioButton.setChecked(false);
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

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mSelectedItemIndex = position;
            notifyDataSetChanged();
        }
    };

}
