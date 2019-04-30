package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.user.Salesman;

public class PromoterAdapter extends RecyclerView.Adapter<PromoterAdapter.ViewHolder> {

    private static final String TAG = "PromoterAdapter";

    private ArrayList<Salesman> mList;
    private Salesman mItemSelected ;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public TextView itemPendings;
        public TextView itemTotal;
        public ImageView itemIcon;
        public TextView itemSeparator;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            itemPendings = (TextView) view.findViewById(R.id.item_pendings);
            itemTotal = (TextView) view.findViewById(R.id.item_total);
            itemSeparator = (TextView) view.findViewById(R.id.item_separator);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);
        }
    }

    public void clearSelection(){
        mItemSelected = null;
    }

    public Salesman getSelectedItems(){
        return mItemSelected;
    }

    public PromoterAdapter(ArrayList<Salesman> items) {
        mList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_salesman_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Salesman promoter = mList.get(position);
        setupView(holder,  position, promoter);
    }

    private void setupView(final ViewHolder holder, final int position, Salesman salesman){

        holder.itemTitle.setText(salesman.firstname+" "+salesman.lastname);
        //holder.itemSubtitle.setText("ARREGLAR");
        //AppController.getInstance().getResources().getString(R.string.entity_age_zone, Integer.valueOf(salesman.age).toString(), salesman.zone)
        holder.itemSubtitle.setText(salesman.description);
        holder.itemPendings.setText(Integer.valueOf(salesman.pendingAssignments).toString());
        holder.itemTotal.setText(Integer.valueOf(salesman.totalAssignments).toString());

        if(salesman==mItemSelected){
            holder.itemIcon.setImageResource(R.drawable.ic_check);
        }else{
            holder.itemIcon.setImageResource(R.drawable.ic_no_checked);
        }

        holder.itemViewContainer.setTag(salesman);
        holder.itemViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemSelected = (Salesman) view.getTag();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<Salesman> list) {
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
