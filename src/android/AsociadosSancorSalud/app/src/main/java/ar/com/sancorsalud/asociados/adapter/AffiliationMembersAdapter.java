package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.affiliation.Member;


public class AffiliationMembersAdapter extends RecyclerView.Adapter<AffiliationMembersAdapter.ViewHolder> {

    private static final String TAG = "AFF_MEMBERS_ADPT";

    private OnMemberItemClickListener mOnItemClickListener;
    private List<Member> mList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public RelativeLayout itemBox = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;
        public View removeButton;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;

            itemBox =  (RelativeLayout) view.findViewById(R.id.item_box);
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

    public void setItemAtIndex(int position, Member member){
        mList.set(position, member);
        notifyDataSetChanged();
    }



    public List<Member> getMembers(){
        List<Member> list =new ArrayList<Member>(mList);
        return list;
    }

    public AffiliationMembersAdapter(List<Member> items) {
        mList.addAll(items);
    }

    public AffiliationMembersAdapter() {

    }

    public void setOnItemClickListener(OnMemberItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    @Override
    public AffiliationMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_basic, parent, false);
        return new AffiliationMembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AffiliationMembersAdapter.ViewHolder holder, final int position) {
        Member m = mList.get(position);
        setupView(holder,  position, m);
    }

    private void setupView(final AffiliationMembersAdapter.ViewHolder holder, final int position, final Member member){
        holder.itemViewContainer.setTag(position);

        // filter and not Show Existent members (Already Existent in sancor system)
        /*
        if (member.existent) {
            holder.itemBox.setVisibility(View.GONE);
            holder.itemBox.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else{
            holder.itemBox.setVisibility(View.VISIBLE);
        }
        */

        holder.itemTitle.setText("Parentesco");
        holder.itemSubtitle.setText( QuoteOptionsController.getInstance().getParentescoName(member.parentesco.id));
        holder.removeButton.setVisibility(View.GONE);

        if (mOnItemClickListener != null) {
            holder.itemViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "position:" + position);

                    // only navigate non existent users
                    if (!member.existent){
                        mOnItemClickListener.onItemClick(v, position);
                    }else{
                        mOnItemClickListener.onHideItemClick(v, position);
                    }
                }
            });
        }
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

    public void setItems(List<Member> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems(){
        mList.clear();
    }
}
