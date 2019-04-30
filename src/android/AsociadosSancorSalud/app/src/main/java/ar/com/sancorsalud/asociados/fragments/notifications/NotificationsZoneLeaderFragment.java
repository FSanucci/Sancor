package ar.com.sancorsalud.asociados.fragments.notifications;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.NotificationsDetailActivity;
import ar.com.sancorsalud.asociados.activity.main.MainActivity;
import ar.com.sancorsalud.asociados.activity.main.ZoneLeaderMainActivity;
import ar.com.sancorsalud.asociados.adapter.NotificationAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.fragments.base.BaseListFragment;
import ar.com.sancorsalud.asociados.manager.NotificationController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

public class NotificationsZoneLeaderFragment extends BaseListFragment {

    private static final String TAG = "NOTIF_LEADER_FRG";

    private ImageView mAllItemsImg;
    private boolean allSelected = false;

    private ArrayList<Notification> mList = new ArrayList<Notification>();
    private NotificationAdapter mAdapter = null;
    private ArrayList<Notification> notificationRemoveList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setupLayouts(view);
        mAllItemsImg =  (ImageView)view.findViewById(R.id.all_items);

        setupListeners();
        setHasOptionsMenu(true);

        reloadData();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ConstantsUtil.VIEW_NOTIFICATION) {
            Log.e(TAG, "onActivityResult **********");
            reloadData();
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notif_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_remove: {
                Log.e(TAG, "option remove");
                onSelectToRemoveNotificationList();
                return true;
            }
        }
        return super.onOptionsItemSelected(item); // important line
    }

    protected void setupListeners(){
        super.setupListeners();

        mAllItemsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelected = !allSelected;

                if (allSelected){
                    mAllItemsImg.setImageResource(R.drawable.ic_check);
                    mAdapter.onSelectAll();

                }else{
                    mAllItemsImg.setImageResource(R.drawable.ic_no_checked);
                    mAdapter.onUnSelectAll();
                }
            }
        });
    }

    protected RecyclerView.Adapter getAdapter() {
        if (mAdapter == null)
            mAdapter = new NotificationAdapter(mList, true);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e(TAG, "Celda Click !  -----------");
                Notification notif = mList.get(position);
                notif.isRead = true;
                IntentHelper.goToNotificationDetailActivity(getActivity(),notif, true);
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
        mAllItemsImg.setImageResource(R.drawable.ic_no_checked);

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

    @Override
    protected void didClickItem(int position) {
    }

    @Override
    protected void didLongClickItem(int position) {
    }

    private void onSelectToRemoveNotificationList(){
        Log.e(TAG, "onSelectToRemoveNotificationList ...");

        HashMap<Long, Boolean> itemsSelected = mAdapter.getSelectedItems();
        if (itemsSelected.size() != 0) {

            notificationRemoveList = new ArrayList<>();
            for (Notification item : mList) {
                Boolean selected = itemsSelected.get(item.id);
                if (selected != null && selected == true) {
                    notificationRemoveList.add(item);
                }
            }

            if (!notificationRemoveList.isEmpty()) {
                ((MainActivity) getActivity()).showMessageWithOption(getResources().getString(R.string.notification_list_remove_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // NO option
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // YES option
                        dialog.dismiss();
                        showProgressDialog(R.string.notifications_removing_data);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "removing selection notifications ....");
                                removeNotificationList();
                            }
                        }).start();
                    }
                });

            } else {
                ((MainActivity) getActivity()).showMessage(getResources().getString(R.string.error_notif_selection_mandatory));
            }
        }else{
            ((MainActivity) getActivity()).showMessage(getResources().getString(R.string.error_notif_selection_mandatory));
        }

    }

    private void  removeNotificationList(){
        Log.e(TAG, "removeNotificationList ...");

        if (!notificationRemoveList.isEmpty()) {

            final Notification notification = notificationRemoveList.remove(0);
            if (notification != null ) {

                Log.e (TAG, "To remove: " + notification.notificationId);
                NotificationController.getInstance().removeNotification(notification.notificationId, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        Log.e(TAG , "ok remove Notification: "  + notification.notificationId);
                        removeNotificationList();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG , "Error Remove Notification: "  + notification.notificationId);
                        removeNotificationList();
                    }
                });
            }
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    reloadData();
                }
            });
        }
    }
}
