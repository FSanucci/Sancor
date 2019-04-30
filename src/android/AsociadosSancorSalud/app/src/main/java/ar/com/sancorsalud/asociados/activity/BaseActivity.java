package ar.com.sancorsalud.asociados.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import javax.net.ssl.SSLHandshakeException;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.ImageProviderListener;
import ar.com.sancorsalud.asociados.utils.LoadResourceUriCallback;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BASEACT";

    protected Context ctx;
    protected Handler handler = new Handler();
    protected PermissionsHelper permHelper = PermissionsHelper.getInstance();
    protected ProgressDialog pDialog;

    protected View mMainContainer;
    private boolean hasCancel = false;

    protected ImageProvider mImageProvider;
    protected String attachFileType = "";
    protected String attachFilesSubDir;

    protected boolean hasToAdddFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = this;

        mMainContainer = findViewById(R.id.main);

        //permHelper.setActivity(this);
        Log.e(TAG, "onCreate ...checkPermissionFlag----------");

        // Check user has change permission: Storage is recreated  in settings app can break  close and restart again
        if (!checkPermissionFlag()) {
            Log.e(TAG, "User changes permissions ...");
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void setHasToAdddFile(boolean hasToAdddFile) {
        this.hasToAdddFile = hasToAdddFile;
    }

    public boolean checkPermissionFlag() {
        return Storage.getInstance().hasPermissionFlag();
    }

    // -- Template methods  ---------------------------------------------------------------------------//
    public void updateFileList(AttachFile attachFile) {

    }

    public void onRemovedFile(int position) {

    }

    public void onDecodedFile(String text) {
    }

    protected void onDecodeError(String msg){
    }

    protected void setTypeTextNoSuggestions(TextView textView){
        textView.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
    protected void setTypeTextNoSuggestions(EditText editText){
        editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    // -- End Template methods  ---------------------------------------------------------------------------//

    public void onCancellAllRequets(){
        Log.e(TAG, "onCancellAllRequets ...");
        AppController.getInstance().getRestEngine().cancelPendingRequests();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mImageProvider != null && (requestCode == PermissionsHelper.REQUEST_CAMERA_PERMISSION || requestCode == PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION)) {
            mImageProvider.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && mImageProvider != null && (requestCode == ImageProvider.REQUEST_IMAGE_CAPTURE || requestCode == ImageProvider.REQUEST_IMAGE_GALLERY)) {
            mImageProvider.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == UCrop.REQUEST_CROP) {
            mImageProvider.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed -----------------------------");
        // cancel actual requests
        AppController.getInstance().getRestEngine().cancelPendingRequests();
        super.onBackPressed();
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy------------------------");
        permHelper = null;
        Runtime.getRuntime().gc();
        super.onDestroy();
    }

    public void finishActivityWithCode(int code) {
        Intent resultIntent = new Intent();
        setResult(code, resultIntent);
        finish();
    }

    protected void enableView(View view) {
        if (view != null) {
            view.setAlpha(1f);
        }
    }

    protected void disableView(View view) {
        if (view != null) {
            view.setAlpha(0.5f);
        }
    }

    protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        setupToolbar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setupToolbar(Toolbar toolbar, int titleId) {
        setupToolbar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(titleId));
        }
    }

    protected void showProgressDialog(int message) {
        try {
            if (pDialog != null)
                pDialog.dismiss();

            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getResources().getString(message));
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Throwable e) {
        }
    }

    protected void dismissProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Throwable e) {
        }
    }

    public void showMessage(String message) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(getResources().getString(R.string.option_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    protected void showMessage(String message, DialogInterface.OnClickListener aListener) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(getResources().getString(R.string.option_ok), aListener).create().show();
    }

    public void showMessageWithTitle(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton(getResources().getString(R.string.option_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    protected void showMessageWithTitle(String title, String message, DialogInterface.OnClickListener aListener) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton(getResources().getString(R.string.option_ok), aListener).create().show();
    }


    public void showMessageWithOption(String message, DialogInterface.OnClickListener noListener, DialogInterface.OnClickListener yesListener) {
        new AlertDialog.Builder(this).setMessage(message).setNegativeButton(R.string.option_no, noListener).setPositiveButton(R.string.option_yes, yesListener).create().show();
    }

    protected void showMessageWithOptionAndLabels(String message, int noLabel, int yesLabel, DialogInterface.OnClickListener noListener, DialogInterface.OnClickListener yesListener) {
        new AlertDialog.Builder(this).setMessage(message).setNegativeButton(noLabel, noListener).setPositiveButton(yesLabel, yesListener).create().show();
    }

    protected void showMessageWithAcceptCancelOption(String message, DialogInterface.OnClickListener noListener, DialogInterface.OnClickListener yesListener) {
        new AlertDialog.Builder(this).setMessage(message).setNegativeButton(R.string.option_cancel, noListener).setPositiveButton(R.string.option_accept, yesListener).create().show();
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    protected void setupImageProvider() {

        mImageProvider.setImageProviderListener(new ImageProviderListener() {

            @Override
            public void didDetectCreditCardNumber(String creditCardNumber, String expireDate) {
            }

            @Override
            public void didSelectImage(final String path) {
                Log.e(TAG, "didSelectImage!!!: " + path);
                showProgressDialog(R.string.compress_file);

                hasCancel = false;
                pDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        CardController.getInstance().cancelRequest();
                        Log.e(TAG, "Cancell request ...");
                        hasCancel = true;
                        if (mMainContainer != null) {
                            SnackBarHelper.makeError(mMainContainer, R.string.cancel_request).show();
                        }
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = null;
                        if (hasToAdddFile) {
                            // hight compress to Add file and send to server
                            file = FileHelper.compressImageFile(path, 20, true);
                        } else {
                            // medium compress just to detect QRCODE
                            file = FileHelper.compressImageFile(path, 50, false);
                        }

                        if (file != null) {

                            try {
                                final AttachFile attachFile = new AttachFile();
                                attachFile.filePath = file.getPath();
                                attachFile.fileName = FileHelper.getResourceName(file.getPath());
                                attachFile.fileExtension = FileHelper.getFileExtension(file.getPath());
                                attachFile.fileNameAndExtension = attachFile.fileName + "." + attachFile.fileExtension;

                                Log.e(TAG, "filePath:" + attachFile.filePath);
                                Log.e(TAG, "fileName:" + attachFile.fileName);
                                Log.e(TAG, "fileExtension:" + attachFile.fileExtension);

                                if (!hasCancel) {

                                    if (hasToAdddFile) {

                                        final String encodedImage = FileHelper.encodeFileToBase64(attachFile.filePath);
                                        Log.e(TAG, "encodedImage:" + encodedImage);

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                dismissProgressDialog();

                                                if (AppController.getInstance().isNetworkAvailable()) {
                                                    showProgressDialog(R.string.sending_file);
                                                    pDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                                                        @Override
                                                        public void onCancel(DialogInterface dialog) {
                                                            CardController.getInstance().cancelRequest();
                                                            Log.e(TAG, "Cancell request ...");

                                                            if (mMainContainer != null) {
                                                                SnackBarHelper.makeError(mMainContainer, R.string.cancel_request).show();
                                                            }
                                                        }
                                                    });

                                                    CardController.getInstance().addFile(attachFile, encodedImage, new Response.Listener<Long>() {
                                                        @Override
                                                        public void onResponse(Long fileId) {
                                                            dismissProgressDialog();
                                                            Log.e(TAG, "ok send File !!!!");

                                                            if (fileId != -1L) {
                                                                attachFile.id = fileId;
                                                                updateFileList(attachFile);
                                                            } else {
                                                                DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_send_file));
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            dismissProgressDialog();
                                                            Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                                                            DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_send_file), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                                        }
                                                    });
                                                } else {
                                                    DialogHelper.showNoInternetErrorMessage(BaseActivity.this, null);
                                                }
                                            }
                                        });
                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // just return file  no added to server
                                                dismissProgressDialog();
                                                updateFileList(attachFile);
                                            }
                                        });
                                    }
                                }

                            } catch (Exception e) {
                                Log.e(TAG, "error enoding file .....");
                                e.printStackTrace();
                                showDelayMessage(getResources().getString(R.string.encode_file_error));
                            }
                        } else {
                            showDelayMessage(getResources().getString(R.string.compress_file_error));
                        }
                    }
                }).start();
            }
        });
    }

    protected void showDelayMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_send_file), message);
            }
        });
    }

    /*
    protected void loadFile(final AttachFile attachFile) {

        if (attachFile.filePath.isEmpty()) {

            showProgressDialog(R.string.download_image_file);

            CardController.getInstance().getDecodedFile(getApplicationContext(), attachFile, new Response.Listener<File>() {
                @Override
                public void onResponse(File resultFile) {
                    dismissProgressDialog();
                    Log.e(TAG, "ok getting File !!!!");

                    if (resultFile != null) {
                        Log.e(TAG, "File path:  " + resultFile.getPath());

                        // update filepath
                        attachFile.filePath = resultFile.getPath();
                        loadUriFormFile(getApplicationContext(), attachFile);

                    } else {
                        Log.e(TAG, "Error getting file ....");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });

        } else {
            loadUriFormFile(getApplicationContext(), attachFile);
        }
    }
    */

    // TODO NUEVO
    protected void loadFile(final AttachFile attachFile) {

        if (attachFile.filePath.isEmpty()) {

            showProgressDialog(R.string.download_image_file);

            final String linkUrl = RestConstants.HOST + RestConstants.POST_GET_FILE + "?id=" + attachFile.id + "&token=" + HRequest.authorizationHeaderValue;
            Log.e(TAG, "linkUrl: " + linkUrl);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = FileHelper.getFile(getApplicationContext(), attachFile.subDir, attachFile.fileNameAndExtension, linkUrl);
                    if (file != null) {
                        attachFile.filePath = file.getPath();
                    }
                    Log.e(TAG, "attachFile filePath: " + attachFile.filePath);
                    loadUriFormFile(getApplicationContext(), attachFile);
                }
            }).start();
        }else{
            loadUriFormFile(getApplicationContext(), attachFile);
        }
    }


    protected void loadUriFormFile(Context ctx, final AttachFile attachFile) {

        FileHelper.loadUriFromFile(getApplicationContext(), attachFile.filePath, new LoadResourceUriCallback() {
            @Override
            public void onSuccesLoadUri(final Uri uri) {
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //dismissProgressDialog();
                            mImageProvider.viewResource(uri, attachFile.fileNameAndExtension);
                        }
                    });
                } catch (Throwable e) {
                }
            }

            @Override
            public void onErrorLoadUri(String error) {
                try {
                    //dismissProgressDialog();
                    Log.e(TAG, "Error loading resource...");
                } catch (Throwable e) {
                }
            }
        });
    }


    protected void removeFile(final AttachFile attachFile, final int position) {
        if (AppController.getInstance().isNetworkAvailable()) {

            CardController.getInstance().removeFile(attachFile.id, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void result) {
                    dismissProgressDialog();

                    if (attachFile.filePath != null && !attachFile.filePath.isEmpty()) {
                        FileHelper.removeFile(attachFile.filePath);
                    }
                    onRemovedFile(position);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    // TODO Simulate desasociate call files list because service not real remove files ids only physically
                }
            });
        }
    }


    protected void decodeQRCode(final AttachFile attachFile) {

        if (attachFile.filePath != null) {
            File inputFile = FileHelper.getFile(attachFile.filePath);

            String text = FileHelper.decodeQRCode(inputFile);

            if (text != null) {
                Log.e(TAG, "Decoded Text: " + text);
                onDecodedFile(text);
            } else {
                Log.e(TAG, "No Text founded");
                onDecodedFile(null);
            }

        } else {

            CardController.getInstance().getDecodedFile(getApplicationContext(), attachFile, new Response.Listener<File>() {
                @Override
                public void onResponse(File resultFile) {
                    //dismissProgressDialog();
                    Log.e(TAG, "ok getting File !!!!");

                    if (resultFile != null) {
                        Log.e(TAG, "File path:  " + resultFile.getPath());

                        // update filepath
                        attachFile.filePath = resultFile.getPath();

                        //File compressFile  = FileHelper.compressImageFile(resultFile);

                        String text = FileHelper.decodeQRCode(resultFile);
                        if (text != null) {
                            Log.e(TAG, "Decoded Text: " + text);
                            onDecodedFile(text);
                        } else {
                            Log.e(TAG, "No Text founded");
                            onDecodedFile(null);
                        }

                    } else {
                        Log.e(TAG, "Error getting file ....");
                        onDecodedFile(null);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onDecodedFile(null);
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                }
            });
        }
    }


    protected void decodeError(VolleyError error) {
        if (error != null) {
            Log.e(TAG, "Decode Error: " + ( error.getMessage() != null ? error.getMessage() : ""));
            String msg = error.getMessage();
            if (msg != null) {
                if (msg.trim().contains("Handshake failed")) {
                    DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_invalid_certificate));
                }else if (msg.trim().contains("PKCS12 key store mac invalid")) {
                    DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_bad_certificate_keystore));
                } else{
                    onDecodeError(msg);
                }
            } else {
                DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_unexpected));
            }

        } else {
            DialogHelper.showMessage(BaseActivity.this, getResources().getString(R.string.error_unexpected));
        }
    }

    protected void openViewOnBrowser(String url){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
