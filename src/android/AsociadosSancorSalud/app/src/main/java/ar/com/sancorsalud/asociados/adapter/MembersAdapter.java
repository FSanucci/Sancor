package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergio on 2/3/17.
 */

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private ArrayList<Member> mList = new ArrayList<>();

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

    public Member getItemAtIndex(int position){
        if(position< mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<Member> getMembers(){
        ArrayList<Member> list =new ArrayList<Member>(mList);
        return list;
    }

    public MembersAdapter(List<Member> items) {
        mList.addAll(items);
    }

    public MembersAdapter() {

    }

    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_basic, parent, false);
        return new MembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MembersAdapter.ViewHolder holder, final int position) {
        Member m = mList.get(position);
        setupView(holder,  position, m);
    }

    private void setupView(final MembersAdapter.ViewHolder holder, final int position, Member member){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(StringHelper.uppercaseFirstCharacter(member.parentesco.optionName()));

        String subTititle = "";
        if (member.age >= 0){
            subTititle +=  member.age +" años";
        }
        if (member.dni > 0){
            subTititle +=  " - " + member.dni;
        }

        if (member.existent){
            subTititle +=  " (Existente)";
        }

        holder.itemSubtitle.setText(subTititle);
        holder.removeButton.setTag(position);
        holder.removeButton.setOnClickListener(clickListener);
        holder.removeButton.setVisibility(member.readOnly ? View.GONE: View.VISIBLE);

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

    public void removeAllItems(){
        mList.clear();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            mList.remove(position);
            removeAllReadOnlyMembers();
            notifyDataSetChanged();
        }
    };

    private void removeAllReadOnlyMembers(){

        boolean hasReadOnly = false;

        for(int i= 0 ; i < mList.size(); i++){
            if (mList.get(i).readOnly){
                hasReadOnly = true;
                mList.remove(i);
                break;
            }
        }

        if (hasReadOnly){
            removeAllReadOnlyMembers();
        }
    }

}
