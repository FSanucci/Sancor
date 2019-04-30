package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardComment;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;

/**
 * Created by sergiocirasa on 28/9/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAX_ITEMS = 4;
    private List<AffiliationCardComment> mList;
    private boolean showMore;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public TextView itemSubtitle;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemSubtitle = (TextView) view.findViewById(R.id.item_subtitle);
            View v = view.findViewById(R.id.remove_button);
            v.setVisibility(View.GONE);
        }
    }

    public static class ShowMoreViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;

        public ShowMoreViewHolder(View view) {
            super(view);
            itemViewContainer = view;
        }
    }


    public CommentAdapter(List<AffiliationCardComment> items) {
        mList = items;
        if(mList.size()>MAX_ITEMS*2-1){
            showMore = true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_basic, parent, false);
            return new CommentAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_more, parent, false);
            return new CommentAdapter.ShowMoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(showMore && position==MAX_ITEMS) {
            ShowMoreViewHolder h = (ShowMoreViewHolder) holder;
            h.itemViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMore = false;
                    notifyDataSetChanged();
                }
            });
        }else {
            AffiliationCardComment comment = mList.get(position);
            setupView((CommentAdapter.ViewHolder)holder, position, comment);
        }
    }

    private void setupView(final CommentAdapter.ViewHolder holder, final int position, AffiliationCardComment comment){
        holder.itemViewContainer.setTag(position);
        holder.itemTitle.setText(comment.description);
        if(comment.postDate!=null)
            holder.itemSubtitle.setText(ParserUtils.parseDate(comment.postDate,"dd-MM-yyyy"));
    }

    @Override
    public int getItemViewType(int position) {
        if(showMore && position==MAX_ITEMS) {
            return 1;
        }else return 0;
    }

    @Override
    public int getItemCount() {
        if(showMore) {
            return MAX_ITEMS+1;
        }else return mList.size();
    }

}
