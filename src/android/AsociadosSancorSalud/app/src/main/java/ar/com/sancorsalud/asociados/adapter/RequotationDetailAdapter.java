package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.affiliation.RecotizacionDetalle;
import ar.com.sancorsalud.asociados.model.user.Salesman;

/**
 * Created by sergiocirasa on 7/9/17.
 */

public class RequotationDetailAdapter extends RecyclerView.Adapter<RequotationDetailAdapter.ViewHolder> {

    private List<RecotizacionDetalle> mList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemValue;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.title);
            itemValue = (TextView) view.findViewById(R.id.value);
        }
    }

    public RecotizacionDetalle getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public RequotationDetailAdapter(List<RecotizacionDetalle> items) {
        mList = items;
    }

    @Override
    public RequotationDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_quotation_detail, parent, false);
        return new RequotationDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RequotationDetailAdapter.ViewHolder holder, final int position) {
        RecotizacionDetalle client = mList.get(position);
        setupView(holder, position, client);
    }

    private void setupView(final RequotationDetailAdapter.ViewHolder holder, final int position, RecotizacionDetalle detail) {
        holder.itemViewContainer.setTag(position);
        if(detail.descripcionConcepto!=null)
            holder.itemTitle.setText(detail.descripcionConcepto);
        else holder.itemTitle.setText("-");
        holder.itemValue.setText("$"+detail.valor);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<RecotizacionDetalle> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

}
