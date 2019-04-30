package ar.com.sancorsalud.asociados.fragments.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.interfaces.IFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
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

/**
 * Created by sergio on 11/2/16.
 */

public class BaseFragment extends Fragment implements IFragment {

    public static final String TAG = "BASE_FRG";

    protected View mMainContainer;
    protected ImageProvider mImageProvider;
    protected String attachFileType = "";
    protected String attachFilesSubDir;

    protected ProgressDialog pDialog;
    protected Interpolator mInterpolator;

    protected Handler handler = new Handler();

    private boolean hasCancel = false;
    protected boolean hasToAdddFile = true;

    protected void showProgressDialog(int message) {
        try {
            pDialog = new ProgressDialog(getActivity());
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

    protected void setTypeTextNoSuggestions(TextView textView) {
        textView.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    protected void setTypeTextNoSuggestions(EditText editText) {
        editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setHasToAdddFile(boolean hasToAdddFile) {
        this.hasToAdddFile = hasToAdddFile;
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
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

        // for scan result code is not ok is 12345678
        if (requestCode == ImageProvider.REQUEST_SCAN_CODE) {
            mImageProvider.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == UCrop.REQUEST_CROP) {
            mImageProvider.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onCancellAllRequets() {
        Log.e(TAG, "onCancellAllRequets ...");
        AppController.getInstance().getRestEngine().cancelPendingRequests();
    }

    protected void setupImageProvider() {

        mImageProvider.setImageProviderListener(new ImageProviderListener() {

            @Override
            public void didDetectCreditCardNumber(String creditCardNumber, String expireDate) {
                updateCreditCardNumber(creditCardNumber, expireDate);
            }

            @Override
            public void didSelectImage(final String path) {
                Log.e(TAG, "didSelectImage: " + path);
                showProgressDialog(R.string.compress_file);

                hasCancel = false;
                pDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        CardController.getInstance().cancelRequest();
                        Log.e(TAG, "Cancell request ...");
                        hasCancel = true;
                        SnackBarHelper.makeError(mMainContainer, R.string.cancel_request).show();
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e(TAG, "Comprimeindo Archivo: --------------------------------------------\n");
                        Date d1 = new Date(System.currentTimeMillis());
                        long d1s = d1.getTime() / 1000;

                        // compress image
                        //File file = FileHelper.compressImageFile(path);
                        File file = null;
                        if (hasToAdddFile) {
                            // hight compress to Add file and send to server
                            file = FileHelper.compressImageFile(path, 20, true);
                        } else {
                            // medium compress just to detect QRCODE
                            file = FileHelper.compressImageFile(path, 50, false);
                        }

                        Date d2 = new Date(System.currentTimeMillis());
                        long d2s = d2.getTime() / 1000;
                        Log.e(TAG, "Delta Time compresion: " + (d2s - d1s) + "  **********************");


                        if (file != null) {

                            try {
                                final AttachFile attachFile = new AttachFile();
                                attachFile.filePath = file.getPath();
                                attachFile.fileName = FileHelper.getResourceName(file.getPath());
                                attachFile.fileExtension = FileHelper.getFileExtension(file.getPath());
                                attachFile.fileNameAndExtension = attachFile.fileName + "." + attachFile.fileExtension;
                                attachFile.subDir = attachFilesSubDir;

                                Log.e(TAG, "filePath:" + attachFile.filePath);
                                Log.e(TAG, "fileName:" + attachFile.fileName);
                                Log.e(TAG, "fileExtension:" + attachFile.fileExtension);

                                if (!hasCancel) {

                                    if (hasToAdddFile) {


                                        Log.e(TAG, "Codificando Archivo a B64: --------------------------------------------\n");

                                        Date d3 = new Date(System.currentTimeMillis());
                                        long d3s = d3.getTime() / 1000;


                                        final String encodedImage = FileHelper.encodeFileToBase64(attachFile.filePath);
                                        Log.e(TAG, "encodedImage:" + encodedImage);

                                        Date d4 = new Date(System.currentTimeMillis());
                                        long d4s = d4.getTime() / 1000;

                                        Log.e(TAG, "Delta Time encodificacion image: " + (d4s - d3s) + "  **********************");
                                        Log.e(TAG, "encodedImg size: " + FileHelper.calcBase64SizeInKBytes(encodedImage) + " *********************************");


                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                dismissProgressDialog();

                                                if (AppController.getInstance().isNetworkAvailable()) {
                                                    showProgressDialog(R.string.sending_file);

                                                    Log.e(TAG, "Adjuntando B64: --------------------------------------------\n");

                                                    final Date d5 = new Date(System.currentTimeMillis());
                                                    final long d5s = d5.getTime() / 1000;


                                                    pDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                                                        @Override
                                                        public void onCancel(DialogInterface dialog) {
                                                            CardController.getInstance().cancelRequest();
                                                            Log.e(TAG, "Cancell request ...");
                                                            SnackBarHelper.makeError(mMainContainer, R.string.cancel_request).show();
                                                        }
                                                    });

                                                    CardController.getInstance().addFile(attachFile, encodedImage, new Response.Listener<Long>() {
                                                        @Override
                                                        public void onResponse(Long fileId) {
                                                            dismissProgressDialog();
                                                            Log.e(TAG, "ok send File !!!!");

                                                            Date d6 = new Date(System.currentTimeMillis());
                                                            long d6s = d6.getTime() / 1000;
                                                            Log.e(TAG, "Delta Subida file: " + (d6s - d5s) + "  **********************");

                                                            if (fileId != -1L) {
                                                                attachFile.id = fileId;
                                                                updateFileList(attachFile);


                                                            } else {
                                                                DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_send_file));
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            dismissProgressDialog();
                                                            Log.e(TAG, (error.getMessage() != null ? error.getMessage() : ""));
                                                            DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_send_file), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                                        }
                                                    });
                                                } else {
                                                    DialogHelper.showNoInternetErrorMessage(getActivity(), null);
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
                                showMessage(getResources().getString(R.string.encode_file_error));
                            }
                        } else {
                            showMessage(getResources().getString(R.string.compress_file_error));
                        }
                    }
                }).start();
            }
        });
    }

    private void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_send_file), message);
            }
        });
    }


    // -- Template methods  ---------------------------------------------------------------------------//
    public void updateFileList(AttachFile attachFile) {
    }

    public void updateCreditCardNumber(String creditCardNumber, String expireDate) {
    }

    public void onRemovedFile(int position) {

    }
    public void onRemovedFile() {

    }

    // -- IFragment ---------------------------------------------------------------------------//
    @Override
    public void onActiveFragment() {

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

    protected void showViewAnimated(View view) {
        if (getActivity() == null) {
            view.setAlpha(1);
            return;
        }

        if (mInterpolator == null)
            mInterpolator = AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear_out_slow_in);

        view.setVisibility(View.VISIBLE);
        view.clearAnimation();
        view.animate()
                .setStartDelay(0)
                .setDuration(200)
                .setInterpolator(mInterpolator)
                .alpha(1);
    }

    protected void hideViewAnimated(View view) {
        if (getActivity() == null) {
            view.setAlpha(0);
            return;
        }

        if (mInterpolator == null)
            mInterpolator = AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear_out_slow_in);

        view.clearAnimation();
        view.animate()
                .setStartDelay(0)
                .setDuration(200)
                .setInterpolator(mInterpolator)
                .alpha(0);
    }


    /*
    protected void loadFile(final AttachFile attachFile) {

        if (attachFile.filePath.isEmpty()) {

            showProgressDialog(R.string.download_image_file);

            CardController.getInstance().getDecodedFile(getActivity().getApplicationContext(), attachFile, new Response.Listener<File>() {
                @Override
                public void onResponse(File resultFile) {
                    dismissProgressDialog();
                    Log.e(TAG, "ok getting File !!!!");

                    if (resultFile != null) {
                        Log.e(TAG, "File path:  " + resultFile.getPath());

                        // update filepath
                        attachFile.filePath = resultFile.getPath();
                        loadUriFormFile(getActivity(), attachFile);

                    } else {
                        Log.e(TAG, "Error getting file ....");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });

        }else{
            loadUriFormFile(getActivity(), attachFile);
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
                    File file = FileHelper.getFile(getActivity(), attachFile.subDir, attachFile.fileNameAndExtension, linkUrl);
                    if (file != null) {
                        attachFile.filePath = file.getPath();

                        Log.e(TAG, "attachFile filePath: " + attachFile.filePath);
                        loadUriFormFile(getActivity(), attachFile);
                    }
                }
            }).start();
        }else{
            loadUriFormFile(getActivity(), attachFile);
        }
    }


    private void loadUriFormFile(Context ctx, final AttachFile attachFile) {

        FileHelper.loadUriFromFile(getActivity(), attachFile.filePath, new LoadResourceUriCallback() {
            @Override
            public void onSuccesLoadUri(final Uri uri) {
                Log.e(TAG, "onSuccesLoadUri ...");

                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //dismissProgressDialog();
                            dismissProgressDialog();
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                        }
                    });

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

    protected void removeFile(final AttachFile attachFile) {
        if (AppController.getInstance().isNetworkAvailable()) {

            CardController.getInstance().removeFile(attachFile.id, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void result) {
                    dismissProgressDialog();

                    if (attachFile.filePath != null && !attachFile.filePath.isEmpty()) {
                        FileHelper.removeFile(attachFile.filePath);
                    }
                    onRemovedFile();

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
}