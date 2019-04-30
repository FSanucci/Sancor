package ar.com.sancorsalud.asociados.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import ar.com.sancorsalud.asociados.R;


public class PermissionsHelper {

    public static final int REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static final int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 3;

    public static final int REQUEST_READ_CALENDAR_PERMISSION = 4;
    public static final int REQUEST_WRITE_CALENDAR_PERMISSION = 5;

    public static final int REQUEST_CREDIT_CARD_CAMERA_DETECT_PERMISSION = 6;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 7;


    private static PermissionsHelper instance = new PermissionsHelper() ;

    public static synchronized PermissionsHelper getInstance() {
        return instance;
    }


    public boolean checkPermissionForCamera(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteCalendar(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionFoReadStorage(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }



    public void requestPermissionForCamera(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            showPermissionError(activity, R.string.permision_error_no_camara);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
        }
    }


    public void requestPermissionForReadStorage(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
            showPermissionError(activity, R.string.permision_error_no_write_data);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }


    public void  requestPermissioForWriteCalendar(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALENDAR)){
            showPermissionError(activity, R.string.permision_error_no_write_calendar);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_CALENDAR},REQUEST_WRITE_CALENDAR_PERMISSION);
        }
    }

    public void requestPermissionForCrediCardCameraDetect(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            showPermissionError(activity, R.string.permision_error_no_camara);
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA}, REQUEST_CREDIT_CARD_CAMERA_DETECT_PERMISSION);
        }
    }

    public void showPermissionError(final Activity activity, int error){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        SnackBarHelper.makeError(viewGroup, error)
                .setActionTextColor(activity.getResources().getColor(R.color.colorGreen))
                .setAction("Ir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IntentHelper.startInstalledAppDetailsActivity(activity);
                    }
                }).show();
    }
}
