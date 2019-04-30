package ar.com.sancorsalud.asociados.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.plan.PlanDetail;


public class PlanDetailAdapter extends BaseAdapter {

    List<PlanDetail> mData;
    Context mContext;
    LayoutInflater inflater;

    public PlanDetailAdapter(List<PlanDetail> data, Context context) {
        mData = data;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.adapter_plan_detail, null);
        }

        PlanDetail planDetail = mData.get(position);

        TextView itemLabel = (TextView) convertView.findViewById(R.id.item_label);
        EditText itemTxt = (EditText) convertView.findViewById(R.id.item_txt);

        itemLabel.setText(planDetail.descripcionConcepto != null ? planDetail.descripcionConcepto.trim() : "");
        itemTxt.setText("$" + planDetail.valor);


        return convertView;
    }
}