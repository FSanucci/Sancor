package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.plan.Plan;



public class PlanSelectAdapter extends RecyclerView.Adapter<PlanSelectAdapter.ViewHolder> {

    private static final String TAG = "PLAN_SELECT_ADPT";
    private List<Plan> mList = new ArrayList<Plan>();

    private int mSelectedItemIndex = -1;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public CheckBox itemCheckbox;
        private EditText itemQuote;

        private RelativeLayout item_button;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemCheckbox = (CheckBox) view.findViewById(R.id.checkBox);
            itemQuote = (EditText) view.findViewById(R.id.quote_input);
            item_button = (RelativeLayout) view.findViewById(R.id.quote_button);
        }
    }

    public Plan getItemAtIndex(int position) {
        if (position < mList.size())
            return mList.get(position);
        else return null;
    }

    public List<Plan> getPlans() {
        List<Plan> list = new ArrayList<Plan>(mList);
        return list;
    }

    public PlanSelectAdapter(Context ctx, List<Plan> items) {
        this.ctx = ctx;
        mList.addAll(items);
    }

    public PlanSelectAdapter(Context ctx, List<Plan> items, ArrayList<Integer> selections) {
        this.ctx = ctx;
        this.mList = items;
    }

    @Override
    public PlanSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_plan_checkbox_item, parent, false);
        return new PlanSelectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlanSelectAdapter.ViewHolder holder, final int position) {
        Plan m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final PlanSelectAdapter.ViewHolder holder, final int position, Plan plan) {
        holder.itemViewContainer.setTag(position);
        holder.item_button.setVisibility(View.GONE);

        if (plan.descripcionPlan != null && !plan.descripcionPlan.isEmpty()) {
            holder.itemCheckbox.setText(plan.descripcionPlan);
        }

        holder.itemCheckbox.setOnClickListener(clickListener);
        holder.itemCheckbox.setTag(position);
        holder.itemQuote.setText("$" + plan.diferenciaAPagar);

        if (position == mSelectedItemIndex)
            holder.itemCheckbox.setChecked(true);
        else
            holder.itemCheckbox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getSelectedIndex(){
        return  mSelectedItemIndex;
    }

    public void onRefresh(List<Plan> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addItem(Plan item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Plan> items) {
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
            int position = (int) v.getTag();

            mSelectedItemIndex = position;
            Log.e(TAG, "index:" + mSelectedItemIndex);
            notifyDataSetChanged();
        }
    };
}