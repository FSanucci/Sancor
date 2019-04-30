package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.utils.StringHelper;


public class ImcAdapter extends RecyclerView.Adapter<ImcAdapter.ViewHolder> {

    private ArrayList<DesDetail> mList = new ArrayList<DesDetail>();
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public ImageView itemImg = null;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);

            itemImg =  (ImageView) view.findViewById(R.id.item_img);
        }
    }

    public DesDetail getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<DesDetail> getMembers(){
        ArrayList<DesDetail> list =new ArrayList<DesDetail>(mList);
        return list;
    }

    public ImcAdapter(Context ctx, List<DesDetail> items) {
        this.ctx = ctx;
        mList.addAll(items);
    }

    public ImcAdapter() {

    }

    @Override
    public ImcAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_imc, parent, false);
        return new ImcAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImcAdapter.ViewHolder holder, final int position) {
        DesDetail detail = mList.get(position);
        setupView(holder,  position, detail);
    }

    private void setupView(final ImcAdapter.ViewHolder holder, final int position, DesDetail  detail){
        holder.itemViewContainer.setTag(position);

        String fullName = !detail.getFullName().isEmpty() ? (detail.getFullName() + ", ") : "";
        String parentesco = detail.parentesco != null ? StringHelper.uppercaseFirstCharacter(detail.parentesco.optionName()): "";
        String age = (detail.age != 0)  ? detail.age +" años": "";

        holder.itemTitle.setText(fullName + " " + parentesco + " " + age);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<DesDetail > list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(DesDetail  item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<DesDetail > items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems(){
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
