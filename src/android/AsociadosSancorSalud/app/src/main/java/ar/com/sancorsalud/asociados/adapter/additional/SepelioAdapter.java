package ar.com.sancorsalud.asociados.adapter.additional;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergiocirasa on 4/4/17.
 */

public class SepelioAdapter extends RecyclerView.Adapter<SepelioAdapter.ViewHolder> {

    private ArrayList<QuoteOption> mList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle;
        public RadioButton itemCheckbox1;
        public RadioButton itemCheckbox2;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemCheckbox1 = (RadioButton) view.findViewById(R.id.opt1Button);
            itemCheckbox2 = (RadioButton) view.findViewById(R.id.opt2Button);
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

    public SepelioAdapter(List<QuoteOption> items) {
        mList.addAll(items);
    }

    public SepelioAdapter() {

    }

    @Override
    public SepelioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sepelio_item, parent, false);
        return new SepelioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SepelioAdapter.ViewHolder holder, final int position) {
        QuoteOption m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final SepelioAdapter.ViewHolder holder, final int position, QuoteOption quoteOption) {
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(StringHelper.uppercaseFirstCharacter(quoteOption.title));
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

}
