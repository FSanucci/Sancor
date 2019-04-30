package ar.com.sancorsalud.asociados.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ar.com.sancorsalud.asociados.manager.CalendarManager;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HRestEngine;
import ar.com.sancorsalud.asociados.rest.core.HRestSecurityEngine;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;


public class AppController extends Application {

    private static final String TAG = "APP";
    private static AppController mInstance;
    public Storage storage;

    // TODO NO CERT VERSION
    private HRestEngine mRestEngine;

    // TODO CERT VERSION
    //private HRestSecurityEngine mRestEngine;

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    private Handler handler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // TODO NO CERT VERSION
        mRestEngine = new HRestEngine(getApplicationContext());

        // TODO CERT VERSION
        //mRestEngine = new HRestSecurityEngine(getApplicationContext());

        Log.e(TAG, "ApplicationController onCreate..........................." );

        Storage.getInstance().init(this.getApplicationContext());
        this.storage = Storage.getInstance();
        CalendarManager.getInstance().init(this.getApplicationContext());

        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                // other setters
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);

        deleteFiles();
        freeMemory();
    }

    // TODO NO CERT VERSION

    public HRestEngine getRestEngine() {
        return mRestEngine;
    }

    //TODO CERT VERSION
    /*
    public HRestSecurityEngine getRestEngine() {
        return mRestEngine;
    }
    */

    public UserController getUserManager(){
        return UserController.getInstance();
    }

    public boolean isNetworkAvailable() {
        return mRestEngine.isNetworkAvailable();
    }

    //https://gist.github.com/steveliles/dd9035dbfaa776ee5a51
    public boolean isAppIsInBackground() {
        Context context = getApplicationContext();
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void updateSalesmanAssigmentData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                requestUpdateSalesmanAssigmentData();
            }
        }).start();
    }



    public void updateZoneLeaderAssigmentData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                requestUpdateZoneLeaderData();
            }
        }).start();
    }




    public void updateSalesmanBadgesCycle(){
        Log.e(TAG, "updateSalesmanBadgesCycle -------------------------------");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                updateSalesmanAssigmentData();

                // call recusive every BADGE_COUNT_REFRESH_DELAY
                updateSalesmanBadgesCycle();
            }
        }, ConstantsUtil.BADGE_COUNT_REFRESH_DELAY);
    }


    public void updateZoneLeadeBadgesCycle(){
        Log.e(TAG, "updateZoneLeadeBadgesCycle -------------------------------");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateZoneLeaderAssigmentData();

                // call recusive every BADGE_COUNT_REFRESH_DELAY
                updateZoneLeadeBadgesCycle();
            }
        }, ConstantsUtil.BADGE_COUNT_REFRESH_DELAY);
    }



    private void requestUpdateSalesmanAssigmentData(){
        HRequest request = RestApiServices.createGetSalesmanCountersRequest(new Response.Listener<Counter>() {
            @Override
            public void onResponse(Counter counter) {
                sendSalesmanBadgeAssigmentBroadCast(counter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        getRestEngine().addToRequestQueue(request,false);
    }

    private void requestUpdateZoneLeaderData(){
        HRequest request = RestApiServices.createGetZoneLeaderCountersRequest(new Response.Listener<Counter>() {
            @Override
            public void onResponse(Counter counter) {
                sendZoneLeaderAssigmentBroadCast(counter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        getRestEngine().addToRequestQueue(request,false);
    }


    private void sendSalesmanBadgeAssigmentBroadCast(Counter counter){
        Intent i = new Intent();
        i.setAction(ConstantsUtil.REFRESH_SALESMAN_ASSIGMENT_BADGE);
        i.putExtra(ConstantsUtil.BADGE_COUNT, counter);
        sendBroadcast(i);
    }

    private void sendZoneLeaderAssigmentBroadCast(Counter counter){
        Intent i = new Intent();
        i.setAction(ConstantsUtil.REFRESH_ZONE_LEADER_ASSIGMENT_BADGE);
        i.putExtra(ConstantsUtil.BADGE_COUNT, counter);
        sendBroadcast(i);
    }


    /*
    * Silence process to free memory
    */
    public void freeMemory() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG , "freeMemory-------------");
                Runtime.getRuntime().gc();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        freeMemory();
                    }
                }, ConstantsUtil.FREE_MEORY_DELAY);

            }
        }).start();
    }

    private void deleteFiles(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileHelper.deleteUserFiles(getApplicationContext());
            }
        }).start();
    }
}
