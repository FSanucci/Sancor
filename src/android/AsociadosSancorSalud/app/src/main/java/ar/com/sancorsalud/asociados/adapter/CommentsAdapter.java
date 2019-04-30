package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;




public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private static final String TAG = "PLAN_ADPT";
    private List<String> mList = new ArrayList<String>();

    private int mSelectedItemIndex = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        private TextView itemCommnet;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemCommnet = (TextView) view.findViewById(R.id.comment_txt);
        }
    }

    public String getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public List<String> getCommnets() {
        List<String> list = new ArrayList<String>(mList);
        return list;
    }

    public CommentsAdapter(List<String> items) {
        mList.addAll(items);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String c = mList.get(position);
        setupView(holder, position, c);
    }

    private void setupView(final ViewHolder holder, final int position, String comment) {
        holder.itemViewContainer.setTag(position);

        holder.itemCommnet.setText(comment);
        holder.itemCommnet.setTag(comment);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<String> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(String item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<String> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }


    public void removeAllItems() {
        mList.clear();
    }


    public int getSelectedIndex() {
        return mSelectedItemIndex;
    }


    private View.OnClickListener costClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String commnet = (String) v.getTag();
        }
    };

}