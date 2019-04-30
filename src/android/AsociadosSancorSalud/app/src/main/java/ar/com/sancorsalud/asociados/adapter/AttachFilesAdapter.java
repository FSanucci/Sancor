package ar.com.sancorsalud.asociados.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.affiliation.DesActivity;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;


public class AttachFilesAdapter extends RecyclerView.Adapter<AttachFilesAdapter.ViewHolder> {

    private static final String TAG = "ATTACH_ADPT";
    private List<AttachFile> mList = new ArrayList<AttachFile>();

    private OnItemClickListener mOnItemClickListener;
    private OnItemClickListener mOnItemDeleteClickListener;

    private boolean editMode = true;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;

        public RelativeLayout attachBox;
        public TextView attachTxt;
        public View removeButton;


        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;

            attachBox = (RelativeLayout) view.findViewById(R.id.attach_box);
            attachTxt = (TextView) view.findViewById(R.id.attach_txt);
            removeButton = view.findViewById(R.id.remove_button);
        }
    }

    public AttachFile getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public ArrayList<AttachFile> getAttachFiles() {
        ArrayList<AttachFile> list = new ArrayList<AttachFile>(mList);
        return list;
    }

    public AttachFilesAdapter(List<AttachFile> items, boolean editMode) {
        mList = items;
        this.editMode = editMode;
        //mList.addAll(items);
    }


    public AttachFilesAdapter() {

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemDeleteClickListener(OnItemClickListener onItemDeleteClickListener) {
        this.mOnItemDeleteClickListener = onItemDeleteClickListener;
    }


    @Override
    public AttachFilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_des_file, parent, false);
        return new AttachFilesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AttachFilesAdapter.ViewHolder holder, final int position) {
        AttachFile m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final AttachFilesAdapter.ViewHolder holder, final int position, final AttachFile attachFile) {
        holder.itemViewContainer.setTag(position);

        // for files that could not be loaded form service we hide it
        if (attachFile.fileNameAndExtension == null || attachFile.fileNameAndExtension.isEmpty()){
            holder.itemViewContainer.setVisibility(View.GONE);
            holder.itemViewContainer.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        else{

            holder.itemViewContainer.setVisibility(View.VISIBLE);

            Log.e(TAG, "ATTACH ID: " + Long.valueOf(attachFile.id));
            Log.e(TAG, "ATTACH PATH: " + attachFile.filePath);
            Log.e(TAG, "ATTACH fileNameAndExtension: " + attachFile.fileNameAndExtension);

            holder.attachTxt.setText("" + attachFile.fileNameAndExtension);


        if (mOnItemClickListener!= null) {
                holder.attachBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "position:" + position);
                        mOnItemClickListener.onItemClick(v, position);
                    }
                });
            }

            if (editMode && mOnItemDeleteClickListener != null ) {
                holder.removeButton.setVisibility(View.VISIBLE);
                holder.removeButton.setTag(position);
                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        mOnItemDeleteClickListener.onItemClick(v, position);
                    }
                });

            } else {
                holder.removeButton.setVisibility(View.GONE);
            }
        }
    }

    // TODO AUX
    /*
    private void setupView(final AttachFilesAdapter.ViewHolder holder, final int position, final AttachFile attachFile) {
        holder.itemViewContainer.setTag(position);

        // for files that could not be loaded form service we hide it
        holder.itemViewContainer.setVisibility(View.VISIBLE);

        Log.e(TAG, "ATTACH ID: " + Long.valueOf(attachFile.id));
        Log.e(TAG, "ATTACH PATH: " + attachFile.filePath);
        Log.e(TAG, "ATTACH fileNameAndExtension: " + attachFile.fileNameAndExtension);

        holder.attachTxt.setText("" + attachFile.id);

        if (mOnItemClickListener!= null) {
            holder.attachBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "position:" + position);
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }

        if (editMode && mOnItemDeleteClickListener != null ) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setTag(position);
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    mOnItemDeleteClickListener.onItemClick(v, position);
                }
            });

        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }
    */


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onRefresh(List<AttachFile> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(AttachFile item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(List<AttachFile> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    private String getResourceName(String filePath) {
        Log.e(TAG, "Adapter PATH: " + filePath);

        int index = filePath.lastIndexOf("/");
        String data = filePath.substring(index + 1, filePath.length());
        return data;
    }
}
