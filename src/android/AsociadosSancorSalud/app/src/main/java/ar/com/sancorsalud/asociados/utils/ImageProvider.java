package ar.com.sancorsalud.asociados.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.yalantis.ucrop.UCrop;


import java.io.File;
import java.io.IOException;

import ar.com.sancorsalud.asociados.R;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sergiocirasa on 23/4/17.
 */

public class ImageProvider {

    private static final String TAG = "IMG_PROVIDER";

    public static final int REQUEST_IMAGE_CAPTURE = 51;
    public static final int REQUEST_IMAGE_GALLERY = 52;
    public static final int REQUEST_SCAN_CODE = 53;

    public static final String ACCION_DETECT_QRCODE = "DECTECT_QRCODE_FORM_FILE";
    public static final String ACCION_DETECT_CREDIT_CARD = "DECTECT_CREDIT_CARD";

    private Activity mActivity;
    private String mCurrentFilePath;
    private ImageProviderListener mListener;

    private String subDir = "";
    private String resourceName= "";
    private String action;

    public ImageProvider(Activity activity) {
        mActivity = activity;
    }

    public void setImageProviderListener(ImageProviderListener listener){
        mListener = listener;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionsHelper.REQUEST_CAMERA_PERMISSION  || requestCode == PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {

                if (action == null) {
                    showImagePicker(subDir, resourceName);
                }else if (action != null && action.equals(ACCION_DETECT_QRCODE)){
                    detectQRCodeFromFile(subDir, resourceName);
                }else if (action != null && action.equals(ACCION_DETECT_CREDIT_CARD)){
                    detectCreditCardNumber();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mListener != null)
                mListener.didSelectImage(mCurrentFilePath); // Ya me devuelve el path del archivo que se genero en el ambiente del provider

        }else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){  // De la Galeria viene una uri en el intent. De esa uri me guardo el file
            Uri selectedResource = data.getData();
            //String extension = FileHelper.getFileExtension(mActivity, selectedResource);
            //resourceName += "." + extension;


            resourceName = FileHelper.getFileNameAndExtension(mActivity, selectedResource);
            try {

                File file = createTempImageFile();
                if (file != null) {
                    FileHelper.saveFile(mActivity, selectedResource,file.getAbsolutePath());
                    mListener.didSelectImage(file.getAbsolutePath());

                    // ---- TODO for crop image
                    //UCrop uCrop = UCrop.of(selectedResource, Uri.fromFile(new File(mActivity.getCacheDir(), "dniQRCode.jpg")));
                    //UCrop uCrop = UCrop.of(selectedResource, Uri.fromFile(file));
                    //UCrop uCrop = UCrop.of(selectedResource, getImageUri());

                    //uCrop.withAspectRatio(16,9);
                    //uCrop.start(mActivity);
                    // --- TODO end crop image

                }
            } catch (IOException ex) {
                Log.e(TAG ,"File Attach error");
            }
        }

        else if(requestCode == REQUEST_SCAN_CODE ){
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                String cardNumber =  scanResult.getFormattedCardNumber();
                Log.e(TAG, "Card Number: " + cardNumber);

                String expireDate = null;
                if (scanResult.isExpiryValid()) {

                    if (scanResult.expiryYear != 0 && scanResult.expiryMonth!= 0){
                        expireDate = scanResult.expiryYear + "-" + scanResult.expiryMonth + "-01";
                    }
                    Log.e(TAG, "Expiration Date: " + expireDate );
                }

                mListener.didDetectCreditCardNumber(cardNumber, expireDate);
            }
        }


        else if (requestCode == UCrop.REQUEST_CROP ){
            Log.e(TAG, "handleCropResult...");
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Log.e(TAG, "resultUri ok...");
                try {
                    File file = createTempImageFile();

                    if (file != null) {
                        FileHelper.saveFile(mActivity, resultUri, file.getAbsolutePath());
                        mListener.didSelectImage(file.getAbsolutePath());
                    }

                } catch (Exception e){
                    Log.e(TAG, "File Attach error");
                }

            } else {
                Log.e(TAG ,"File Crop error");
            }
        }
    }


    public void showImagePicker(String subDir, String resourceName) {
        this.subDir = subDir;
        this.resourceName = resourceName;

        if (!PermissionsHelper.getInstance().checkPermissionForCamera(mActivity)) {
            PermissionsHelper.getInstance().requestPermissionForCamera(mActivity);
            return;
        }

        // TODO nuevo
        if (!PermissionsHelper.getInstance().checkPermissionFoReadStorage(mActivity)) {
            PermissionsHelper.getInstance().requestPermissionForReadStorage(mActivity);
            return;
        }


        final CharSequence[] options = new CharSequence[]{mActivity.getResources().getString(R.string.ip_picker_image_source_gallery_option), mActivity.getResources().getString(R.string.ip_picker_image_source_camera_option)};
        int title = R.string.ip_picker_image_source_title;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(title));
        builder.setItems(options,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                dispatchGalleryIntent();
                                break;
                            }
                            case 1: {
                                dispatchTakePictureIntent();
                                break;
                            }
                            default:
                                break;
                        }
                    }
                });

        builder.show();
    }

    public void detectQRCodeFromFile(String subDir, String resourceName) {
        action = ACCION_DETECT_QRCODE;

        if (!PermissionsHelper.getInstance().checkPermissionForCamera(mActivity)) {
            PermissionsHelper.getInstance().requestPermissionForCamera(mActivity);
            return;
        }

        if (!PermissionsHelper.getInstance().checkPermissionFoReadStorage(mActivity)) {
            PermissionsHelper.getInstance().requestPermissionForReadStorage(mActivity);
            return;
        }

        action = null;
        dispatchGalleryIntent();
    }


    public void detectCreditCardNumber() {
        action = ACCION_DETECT_CREDIT_CARD;

        if (!PermissionsHelper.getInstance().checkPermissionForCamera(mActivity)) {
            PermissionsHelper.getInstance().requestPermissionForCrediCardCameraDetect(mActivity);
            return;
        }

        Intent scanIntent = new Intent(mActivity, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        action = null;
        mActivity.startActivityForResult(scanIntent, REQUEST_SCAN_CODE);
    }

    public static void viewResource(Activity activity, Uri uri, String resourceName){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (resourceName!=null && (resourceName.endsWith(".png") || resourceName.endsWith(".PNG")|| resourceName.endsWith(".jpg") || resourceName.endsWith(".JPG") )) {
            intent.setDataAndType(uri, "image/*");
        }else {
            intent.setDataAndType(uri, "*/*");
        }

        activity.startActivity(intent);
    }

    public void viewResource(Uri uri, String resourceName){
        ImageProvider.viewResource(mActivity,uri,resourceName);
    }

    // --- helper methods ---------------------------------------------------------------- //

    private File createTempImageFile() throws IOException {

        File file = FileHelper.getImageFile(mActivity, subDir, resourceName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = file.getAbsolutePath();
        Log.e(TAG, "mCurrentFilePath: " + mCurrentFilePath);
        return file;
    }

    private Uri getImageUri(){
        File photoFile = null;
        try {
            photoFile = createTempImageFile();
        } catch (IOException ex) {
            Log.e("e","error");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(mActivity, "ar.com.sancorsalud.asociados.fileprovider", photoFile);
            return photoURI;
        }
        return null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {

            resourceName += ConstantsUtil.IMG_EXTENSION;
            Uri photoUri = getImageUri(); // saco la uri dentro del espacio de nombre del provider, y se le asocia ya a un archivo en ese ambiente  --> queda en mCurrentFilePath

            if(photoUri!=null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchGalleryIntent(){

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
        galleryIntent.setType("*/*");

        Intent chooser = Intent.createChooser(galleryIntent, mActivity.getResources().getString(R.string.image_source_title));
        mActivity.startActivityForResult(chooser, REQUEST_IMAGE_GALLERY);
    }
}
