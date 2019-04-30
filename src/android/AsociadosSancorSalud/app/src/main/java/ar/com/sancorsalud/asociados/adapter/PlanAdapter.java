package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.plan.Plan;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private static final String TAG = "PLAN_ADPT";
    private List<Plan> mList = new ArrayList<Plan>();

    private int mSelectedItemIndex = -1;
    private HashMap<Integer,Boolean> mSelections;
    private Context ctx;
    private boolean hidePlanValue;

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

    public PlanAdapter(Context ctx, List<Plan> items,  boolean hidePlanValue) {
        this.ctx = ctx;
        this.hidePlanValue = hidePlanValue;
        mList.addAll(items);
    }

    public PlanAdapter(Context ctx, List<Plan> items, ArrayList<Integer> selections, boolean hidePlanValue) {
        this.ctx = ctx;
        this.mList = items;
        this.hidePlanValue = hidePlanValue;


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


    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_plan_checkbox_item, parent, false);
        return new PlanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlanAdapter.ViewHolder holder, final int position) {
        Plan m = mList.get(position);
        setupView(holder, position, m);
    }

    private void setupView(final PlanAdapter.ViewHolder holder, final int position, Plan plan) {
        holder.itemViewContainer.setTag(position);

        /*
        if (plan.descripcionConcepto != null && !plan.descripcionConcepto.isEmpty()) {
            holder.itemCheckbox.setText(StringHelper.uppercaseFirstCharacter(plan.descripcionConcepto));
        }else if (plan.descripcionProducto != null && !plan.descripcionProducto.isEmpty()){
            holder.itemCheckbox.setText(StringHelper.uppercaseFirstCharacter(plan.descripcionProducto));
        }
        */

        if (plan.descripcionPlan != null && !plan.descripcionPlan.isEmpty()) {
            holder.itemCheckbox.setText(plan.descripcionPlan);
        }

        holder.itemCheckbox.setOnClickListener(clickListener);
        holder.itemCheckbox.setTag(position);

        //holder.itemQuote.setText("$" + plan.valor);
        holder.itemQuote.setText("$" + plan.diferenciaAPagar);
        holder.itemQuote.setOnClickListener(costClickListener);
        holder.itemQuote.setTag(plan);

        holder.item_button.setOnClickListener(costClickListener);
        holder.item_button.setTag(plan);

        /*
        if (position == mSelectedItemIndex)
            holder.itemCheckbox.setChecked(true);
        else
            holder.itemCheckbox.setChecked(false);
        */


        boolean est = mSelections.get(position);
        if (est){
            holder.itemCheckbox.setChecked(true);
        }else{
            holder.itemCheckbox.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
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

            /*
            mSelectedItemIndex = position;
            Log.e(TAG, "index:" + mSelectedItemIndex);
            notifyDataSetChanged();
            */

            boolean est = !mSelections.get(position);
            mSelections.put(position,est);

            //mList.remove(position);
            notifyDataSetChanged();
        }
    };


    public void setSelectedIndex(int index){

        if(multipleSelectionsEnabled()){
            boolean est = !mSelections.get(index);
            mSelections.put(index,est);
        }else {
            mSelectedItemIndex = index;
        }
    }

    public int getSelectedIndex(){
        return  mSelectedItemIndex;
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


    private View.OnClickListener costClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Plan plan = (Plan) v.getTag();
            //Log.e(TAG, plan.descripcionConcepto);
            showQuoteAlert(plan);
        }
    };

    private void showQuoteAlert(Plan plan) {
        LayoutInflater linf = LayoutInflater.from(ctx);
        final View inflator = linf.inflate(R.layout.alert_plan_detail, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        alert.setTitle(plan.descripcionConcepto != null ? plan.descripcionConcepto : (plan.descripcionProducto != null ? plan.descripcionProducto: ""));
        final RelativeLayout planValueBox =   (RelativeLayout) inflator.findViewById(R.id.plan_value_box);

        final EditText planValue = (EditText) inflator.findViewById(R.id.plan_value_input);
        planValue.setText("$" + plan.valor );

        final TextView planDiffLabel =  (TextView) inflator.findViewById(R.id.plan_diff_label);
        final EditText planDiff = (EditText) inflator.findViewById(R.id.plan_diff_input);
        planDiff.setText("$" + plan.diferenciaAPagar );


        if (hidePlanValue){
            planValueBox.setVisibility(View.INVISIBLE);
            planDiffLabel.setText(ctx.getResources().getString(R.string.plan_total));
        }else{
            planValueBox.setVisibility(View.VISIBLE);
            planDiffLabel.setText(ctx.getResources().getString(R.string.plan_diff));
        }


        ListView listView = (ListView) inflator.findViewById(R.id.plan_details_list) ;
        PlanDetailAdapter planDetailAdapter = new PlanDetailAdapter(plan.details, ctx);
        listView.setAdapter(planDetailAdapter);
        listView.setDivider(null);


        alert.setPositiveButton(ctx.getResources().getString(R.string.option_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.setView(inflator);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();
    }
}
