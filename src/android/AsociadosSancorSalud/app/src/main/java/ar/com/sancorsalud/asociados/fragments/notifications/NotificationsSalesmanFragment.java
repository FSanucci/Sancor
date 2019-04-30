package ar.com.sancorsalud.asociados.fragments.notifications;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.NotificationAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.fragments.base.BaseListFragment;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.utils.IntentHelper;

public class NotificationsSalesmanFragment extends BaseListFragment {

    private static final String TAG = "NOTIF_SALESMAN_FRG";
    private ArrayList<Notification> mList = new ArrayList<Notification>();
    private NotificationAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setupLayouts(view);

        View mAllItemsImg = view.findViewById(R.id.all_items);
        mAllItemsImg.setVisibility(View.GONE);

        View mAllTexts =  view.findViewById(R.id.all_txt);
        mAllTexts.setVisibility(View.GONE);


        setupListeners();
        setHasOptionsMenu(true);

        reloadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        NotificationController.getInstance().updateNotificationsCounter(true,null);
        super.onDestroy();
    }


    protected RecyclerView.Adapter getAdapter() {
        if (mAdapter == null)
            mAdapter = new NotificationAdapter(mList, false);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "Celda Click !  -----------");
                Notification notif = mList.get(position);
                notif.isRead = true;
                IntentHelper.goToNotificationDetailActivity(getActivity(),notif, false);
            }
        });

        return mAdapter;
    }

    @Override
    protected void reloadData(){
        hideAll();
        showProgress(true);
        reloadData(true);
    }


    @Override
    protected void onRefreshData(){
        onCancellAllRequets();
        showProgress(false);
        reloadData(true);
    }

    protected void reloadData(boolean force) {
        showProgress(true);
        hideAll();

        NotificationController.getInstance().getNotifications(new Response.Listener<ArrayList<Notification>>() {
            @Override
            public void onResponse(ArrayList<Notification> v) {
                mList = new ArrayList<Notification>(v);
                showProgress(false);
                onEndedRefresh();
                if (mList.isEmpty()) {
                    showEmptyListLayout();
                } else {
                    showDataListLayout();
                    mAdapter.onRefresh(mList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onEndedRefresh();
                showProgress(false);
                showErrorListLayout();
            }
        });
    }

    /*
    protected void didClickItem(int position) {
        Notification notif = mList.get(position);
        notif.isRead = true;
        IntentHelper.goToNotificationDetailActivity(getActivity(),notif, false);
    }

    protected void didLongClickItem(int position) {
    }
    */

    @Override
    protected void didClickItem(int position) {
    }

    @Override
    protected void didLongClickItem(int position) {
    }
}
