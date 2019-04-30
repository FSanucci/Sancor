package ar.com.sancorsalud.asociados.adapter;

import android.provider.SyncStateContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by sergio on 2/16/17.
 */

public class AportesAdapter extends RecyclerView.Adapter<AportesAdapter.ViewHolder> {

    private ArrayList<Aporte> mList = new ArrayList<>();
    public boolean readOnly;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public View removeButton;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            removeButton = view.findViewById(R.id.remove_button);
        }
    }

    public Aporte getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<Aporte> getAportes(){
        ArrayList<Aporte> list =new ArrayList<Aporte>(mList);
        return list;
    }

    public AportesAdapter(List<Aporte> items) {
        this(items, false);
    }

    public AportesAdapter(List<Aporte> items, boolean readOnly) {
        this.readOnly = readOnly;
        mList.addAll(items);
    }


    public AportesAdapter() {
        this(false);
    }

    public AportesAdapter( boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public AportesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_basic, parent, false);
        return new AportesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AportesAdapter.ViewHolder holder, final int position) {
        Aporte m = mList.get(position);
        setupView(holder,  position, m);
    }

    private void setupView(final AportesAdapter.ViewHolder holder, final int position, Aporte aporte){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(QuoteOptionsController.getInstance().getAporteLegalName(ConstantsUtil.APORTE_LEGAL_REM_BRUTA));

        if (aporte.tipoAporte.id.equals(ConstantsUtil.APORTE_LEGAL_OBRA_SOCIAL)) {
            holder.itemSubtitle.setText(String.format("%.2f", Aporte.remuneracionBruta(aporte.monto)).replace(",", ".") +"$" );
        }else {
            // APORTE_LEGAL_REM_BRUTA
            holder.itemSubtitle.setText(aporte.monto + "$");
        }

        if (readOnly){
            holder.removeButton.setVisibility(View.GONE);
        }else {

            holder.removeButton.setTag(position);
            holder.removeButton.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<Aporte> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(Aporte item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Aporte> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems(){
        mList.clear();
        notifyDataSetChanged();
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
