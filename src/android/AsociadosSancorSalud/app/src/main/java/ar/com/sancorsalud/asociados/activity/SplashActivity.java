package ar.com.sancorsalud.asociados.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by francisco on 13/9/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG  = "SPLASH_ACT";

    protected PermissionsHelper permHelper = PermissionsHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.e (TAG, "SPLASH DEVELOPMENT---------------------------");

        // mark no manipulated permissions
        Storage.getInstance().setPermissionFlag(true);

        // TODO NO CERTIFICATE VERSION
        toLogin();

        // TODO CERTIFICATE VERSION: check permission to load certificate
        /*
        if (!permHelper.checkPermissionFoReadStorage(this)) {
            permHelper.requestPermissionForReadStorage(this);
        } else {
            boolean hasNewCertificate = FileHelper.tryToLoadCertFileFromDownloads(getApplicationContext());
            onTryToLoadCertificate(hasNewCertificate);
        }
        */
        // TODO END CERTIFICATE VERSION
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {

                // try to load certificate form downloads dir,  if exists push and replace the embedded app certificate
                boolean hasNewCertificate = FileHelper.tryToLoadCertFileFromDownloads(getApplicationContext());
                onTryToLoadCertificate(hasNewCertificate);
            }
        }
    }

    private void onTryToLoadCertificate(boolean hasNewCertificate ){
        if (hasNewCertificate) {
            Log.e(TAG, "NEW CERTIFICATE FROM DOWNLOAD REPLACE THE OLD ONE ++++++++++++++++++++++++++");
            toLogin();
        }else if (checkEmbeddedAppCertificateExistence()) {
            Log.e(TAG, "KEEPING EMBEDDED CERTIFICATE APP ***********************");
            toLogin();
        }else{
            Log.e(TAG, "No certificate exists : missign certificate!!!");
            DialogHelper.showMessage(SplashActivity.this,R.string.error,R.string.error_missing_certificate);
        }
    }

    /*
    * Check if app already have an embedded certificate
    */
    private boolean checkEmbeddedAppCertificateExistence(){

        File certFile = FileHelper.findAppCertificateFile(getApplicationContext());
        if (certFile != null && certFile.length() > 0){
            Log.e (TAG, "check cert file: " + certFile.getAbsolutePath());
            return true;
        }else{
            return false;
        }
    }

    private void toLogin(){
        IntentHelper.goToLoginActivity(SplashActivity.this);
    }

}
