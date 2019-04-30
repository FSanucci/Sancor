package ar.com.sancorsalud.asociados.manager;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by sergiocirasa on 12/9/17.
 */

public class NotificationController {

    private static final long UPDATE_EACH_SECONDS = 180;
    private int mUnreadNotificationsCount=0;
    private long mLastUpdate=0;

    private static NotificationController mInstance = new NotificationController();
    public static synchronized NotificationController getInstance() {
        return mInstance;
    }

    public void clear(){
        mUnreadNotificationsCount=0;
        mLastUpdate=0;
    }

    public void markAsRead(Notification notification){

        notification.isRead = true;
        mUnreadNotificationsCount--;
        if(mUnreadNotificationsCount<0)
            mUnreadNotificationsCount=0;

        HRequest request = RestApiServices.createMatkAsReadNotificationRequest(notification.id,new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                Log.e("NOTIF","OK");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("NOTIF","BAD");
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void getNotifications(final Response.Listener<ArrayList<Notification>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetNotificationsRequest(UserController.getInstance().getSignedUser().id,new Response.Listener<ArrayList<Notification>>() {
            @Override
            public void onResponse(ArrayList<Notification> v) {
                listener.onResponse(v);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, true);
    }

    public void getNotificationsSended(final Response.Listener<ArrayList<Notification>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetNotificationsSendedRequest(UserController.getInstance().getSignedUser().id,new Response.Listener<ArrayList<Notification>>() {
            @Override
            public void onResponse(ArrayList<Notification> v) {
                listener.onResponse(v);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, true);
    }

    public void updateNotificationsCounter(boolean forced, final Response.Listener<Integer> listener){

        Date date = new Date(System.currentTimeMillis());
        long seconds = date.getTime() / 1000;

        if(!forced && seconds-mLastUpdate < UPDATE_EACH_SECONDS){
            if(listener!=null)
                listener.onResponse(mUnreadNotificationsCount);
            return;
        }

        mLastUpdate = seconds;
        HRequest request = RestApiServices.createGetNotificationsCounterRequest(new Response.Listener<Integer>() {
            @Override
            public void onResponse(Integer counter) {
                mUnreadNotificationsCount = counter;
                sendBadgeNotificationBroadCast(counter);
                if(listener!=null)
                    listener.onResponse(mUnreadNotificationsCount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(listener!=null)
                    listener.onResponse(mUnreadNotificationsCount);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void removeNotification(long notificationId , final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.removeNotificationRequest(notificationId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void sendNotification(long notificationId , long zoneId, long salesmanId, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.sendNotificationRequest(notificationId, zoneId, salesmanId, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void addNotification(Notification notification , final Response.Listener<Long> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.addNotificationRequest(notification, new Response.Listener<Long>() {
            @Override
            public void onResponse(Long notifId) {
                listener.onResponse(notifId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }



    private void sendBadgeNotificationBroadCast(int counter){
        Intent i = new Intent();
        i.setAction(ConstantsUtil.REFRESH_NOTIFICATION_BADGE);
        i.putExtra(ConstantsUtil.BADGE_COUNT, counter);
        AppController.getInstance().sendBroadcast(i);
    }

    public void downloadPDF(){

    }

}
