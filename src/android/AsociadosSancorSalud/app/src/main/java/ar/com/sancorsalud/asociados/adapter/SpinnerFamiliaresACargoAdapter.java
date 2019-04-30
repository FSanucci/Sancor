package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.sancorsalud.asociados.R;

/**
 * Created by sergio on 7/18/16.
 */
public class SpinnerFamiliaresACargoAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private View mConvertView;
    private int mSelectedIndex = -1;
    private ArrayList<String> mList;
    private HashMap<Integer,Boolean> mSelections;

    public SpinnerFamiliaresACargoAdapter(Context ctx) {
        context = ctx;
    }

    public SpinnerFamiliaresACargoAdapter(Context ctx, ArrayList<String> items, int index) {
        context = ctx;
        this.mList = items;
        this.mSelectedIndex = index;
    }

    public SpinnerFamiliaresACargoAdapter(Context ctx, ArrayList<String> items, ArrayList<Integer> selections) {
        context = ctx;
        this.mList = items;

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
    public String getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            this.mConvertView = LayoutInflater.from(context).inflate(R.layout.adapter_checkbox_item,null);
        else this.mConvertView = convertView;

        Holder holder = (Holder) this.mConvertView.getTag();

        if(holder==null){
            holder = mapHolderUIControls();
            this.mConvertView.setTag(holder);
        }

        String item = mList.get(position);
        this.fillHolderUIValues(item, holder, position);
        return this.mConvertView;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    protected Holder mapHolderUIControls() {
        Holder holder = new Holder();
        //holder.title = (TextView) this.mConvertView.findViewById(R.id.textView);
        //holder.image = (ImageView) this.mConvertView.findViewById(R.id.checkImage);
        holder.itemCheckbox = (CheckBox) this.mConvertView.findViewById(R.id.checkBox);
        holder.itemContainer = (RelativeLayout)this.mConvertView.findViewById(R.id.item_container);

        return holder;
    }

    protected void fillHolderUIValues(String str, Holder holder, int position) {

        holder.itemContainer.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        holder.itemCheckbox.setText(str!= null ? str : "");
        holder.itemCheckbox.setOnClickListener(clickListener);
        holder.itemCheckbox.setTag(position);

        boolean est = mSelections.get(position);
        if (est){
            holder.itemCheckbox.setChecked(true);
        }else{
            holder.itemCheckbox.setChecked(false);
        }
    }

    private class Holder {
        CheckBox itemCheckbox;
        RelativeLayout itemContainer;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();

            boolean est = !mSelections.get(index);
            mSelections.put(index,est);

            //mList.remove(position);
            notifyDataSetChanged();
        }
    };
}
