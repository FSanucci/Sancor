package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergio on 2/3/17.
 */

public class EntidadEmpleadoraAdapter extends RecyclerView.Adapter<EntidadEmpleadoraAdapter.ViewHolder> {

    private static final String TAG = "EE_ADP";

    private List<EntidadEmpleadora> mList = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

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

    public EntidadEmpleadora getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<EntidadEmpleadora> getEntidadEmpledoraList(){
        ArrayList<EntidadEmpleadora> list =new ArrayList<EntidadEmpleadora>(mList);
        return list;
    }

    public EntidadEmpleadoraAdapter(List<EntidadEmpleadora> items) {
        mList.addAll(items);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public EntidadEmpleadoraAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_basic, parent, false);
        return new EntidadEmpleadoraAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EntidadEmpleadoraAdapter.ViewHolder holder, final int position) {
        EntidadEmpleadora ee = mList.get(position);
        setupView(holder,  position, ee);
    }

    private void setupView(final EntidadEmpleadoraAdapter.ViewHolder holder, final int position, EntidadEmpleadora ee){
        holder.itemViewContainer.setTag(position);

        holder.itemTitle.setText("EE: " + (ee.isTitular ? "Titular" : "Conyuge"));
        holder.itemSubtitle.setText(String.format("%.2f", ee.remuneracion).replace(",", "."));
        holder.removeButton.setTag(position);
        //holder.removeButton.setOnClickListener(clickListener);
        holder.removeButton.setVisibility(View.GONE);

        if (mOnItemClickListener != null) {
            holder.itemViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "position:" + position);
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<EntidadEmpleadora> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(EntidadEmpleadora item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(List<EntidadEmpleadora> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems(){
        mList.clear();
    }

    /*
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mList.remove(position);
            notifyDataSetChanged();
        }
    };
    */

}
