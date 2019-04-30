package ar.com.sancorsalud.asociados.adapter.additional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;


public class SpinnerAdicionalesOptativosDropDownAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private View mConvertView;
    private List<AdicionalesOptativosData.OpcionalData> mList;
    private int mSelectedIndex = -1;
    private HashMap<Integer,Boolean> mSelections;

    public SpinnerAdicionalesOptativosDropDownAdapter(Context ctx) {
        context = ctx;
    }

    public SpinnerAdicionalesOptativosDropDownAdapter(Context ctx, List<AdicionalesOptativosData.OpcionalData> list, int index) {
        context = ctx;
        this.mList = list;
        this.mSelectedIndex = index;
    }

    public SpinnerAdicionalesOptativosDropDownAdapter(Context ctx, List<AdicionalesOptativosData.OpcionalData> list, ArrayList<Integer> selections) {
        context = ctx;
        this.mList = list;

        mSelections = new HashMap<Integer,Boolean>();
        for(int i=0; i< mList.size();i++){
            mSelections.put(i,false);
        }

        if(selections!=null){
            for(int i : selections){
                mSelections.put(i,true);
            }
        }
    }

    private boolean multipleSelectionsEnabled(){
        return mSelections!=null;
    }

    public SpinnerAdicionalesOptativosDropDownAdapter(Context ctx, List<AdicionalesOptativosData.OpcionalData> list) {
        this(ctx,list,-1);
    }

    public void setSelectedIndex(int index){

        if(multipleSelectionsEnabled()){
            boolean est = !mSelections.get(index);
            mSelections.put(index,est);
        }else {
            mSelectedIndex = index;
        }
    }

    public int getSelectedIndex(){
        return  mSelectedIndex;
    }

    public ArrayList<Integer> getSelectedIndexes(){
        ArrayList<Integer> selections = new ArrayList<Integer>();

        for(int i=0; i< mList.size();i++){
            boolean est = mSelections.get(i);
            if(est)
                selections.add(i);
        }
        return  selections;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AdicionalesOptativosData.OpcionalData getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            this.mConvertView = LayoutInflater.from(context).inflate(R.layout.spinner_item,null);
        else this.mConvertView = convertView;

        Holder holder = (Holder) this.mConvertView.getTag();

        if(holder==null){
            holder = mapHolderUIControls();
            this.mConvertView.setTag(holder);
        }

        AdicionalesOptativosData.OpcionalData item = mList.get(position);
        this.fillHolderUIValues(item, holder, position);
        return this.mConvertView;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    protected Holder mapHolderUIControls() {
        Holder holder = new Holder();
        holder.title = (TextView) this.mConvertView.findViewById(R.id.textView);
        holder.image = (ImageView) this.mConvertView.findViewById(R.id.checkImage);
        return holder;
    }

    protected void fillHolderUIValues(AdicionalesOptativosData.OpcionalData item, Holder holder, int position) {
        holder.title.setText(item!= null ? item.descripcionPlan : "");

        if(!multipleSelectionsEnabled()) {
            holder.image.setVisibility((position == mSelectedIndex) ? View.VISIBLE : View.INVISIBLE);
        }else{
            holder.image.setVisibility(mSelections.get(position) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private class Holder {
        TextView title;
        ImageView image;
    }
}
