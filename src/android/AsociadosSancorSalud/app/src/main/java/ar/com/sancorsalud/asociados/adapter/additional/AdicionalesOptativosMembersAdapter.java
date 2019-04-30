package ar.com.sancorsalud.asociados.adapter.additional;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.utils.StringHelper;


public class AdicionalesOptativosMembersAdapter extends RecyclerView.Adapter<AdicionalesOptativosMembersAdapter.ViewHolder> {

    private ArrayList<Member> mList = new ArrayList<>();
    private HashMap<Integer,Boolean> mSelections;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public CheckBox itemCheckbox;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemCheckbox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

    public Member getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<Member> getMembers() {
        ArrayList<Member> list = new ArrayList<Member>(mList);
        return list;
    }

    public AdicionalesOptativosMembersAdapter(List<Member> items) {
        mList.addAll(items);
    }

    public AdicionalesOptativosMembersAdapter(List<Member> items, ArrayList<Integer> selections) {

        mList.addAll(items);

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
    public AdicionalesOptativosMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_checkbox_item, parent, false);
        return new AdicionalesOptativosMembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdicionalesOptativosMembersAdapter.ViewHolder holder, final int position) {
        Member m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final AdicionalesOptativosMembersAdapter.ViewHolder holder, final int position, Member member) {

        holder.itemViewContainer.setTag(position);
        holder.itemCheckbox.setText(StringHelper.uppercaseFirstCharacter(member.parentesco.title));
        holder.itemCheckbox.setOnClickListener(clickListener);
        holder.itemCheckbox.setTag(position);


        boolean est = mSelections.get(position);
        if (est){
            holder.itemCheckbox.setChecked(true);
        }else{
            holder.itemCheckbox.setChecked(false);
        }

        /*
        if(position==mSelectedItemIndex)
            holder.itemRadioButton.setChecked(true);
        else holder.itemRadioButton.setChecked(false);
        */

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<Member> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(Member item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Member> items) {
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
            int index = (int) v.getTag();

            boolean est = !mSelections.get(index);
            mSelections.put(index,est);


            //mList.remove(position);
            notifyDataSetChanged();
        }
    };


    public void setSelectedIndex(int index){;
        mSelections.put(index,true);
        notifyDataSetChanged();
    }

}
