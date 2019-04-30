package ar.com.sancorsalud.asociados.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.StringUtils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import ar.com.sancorsalud.asociados.rest.core.SSLSocketFactoryExtended;


/**
 * Created by sergio on 2/3/17.
 */
public class FileHelper {

    private static final String TAG = "FILE_HELPER";

    private static final String CERTIFICATE_PREFIX = "AMSS_";


    //private static final int MAX_FILE_LENGTH = 65000;  // 65kB
    private static final int MAX_FILE_LENGTH = 500000;  // 500KB

    public static String loadJSONFromAsset(String asset) {
        String json = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream stream = AppController.getInstance().getApplicationContext().getAssets().open(asset);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((json = in.readLine()) != null) {
                buf.append(json);
            }
            in.close();
            json = buf.toString();
        } catch (Exception e) {

        }
        return json;
    }

    public static File getImageFile(Context ctx, String subDir, String resourceName) {
        String dir = ConstantsUtil.USER + "/" + subDir + "/" + ConstantsUtil.IMG_DIR;
        Log.e(TAG, "File dir:" + dir);

        return getResouceFile(ctx, dir, resourceName);
    }

    // --- TODO CERTIFICADOS -------------------------------------------------

    public static File getAppCertFile(Context ctx, String resourceName) {
        String dir = ConstantsUtil.USER + "/" + ConstantsUtil.DOWNLOAD_DIR;
        Log.e(TAG, "File dir:" + dir);
        return getResouceFile(ctx, dir, resourceName);
    }

    public static File findAppCertificateFile(Context ctx) {
        String dir = ConstantsUtil.USER + "/" + ConstantsUtil.DOWNLOAD_DIR;
        Log.e(TAG, "App File dir:" + dir);

        File mediaDir = new File(ctx.getExternalFilesDir(null), dir);
        File certFile = null;

        if (mediaDir.isDirectory()) {
            for (File child : mediaDir.listFiles()) {
                if (child.getName().startsWith(CERTIFICATE_PREFIX) && child.getName().endsWith(".p12")){
                    certFile = child;
                    break;
                }
            }
        }
        return certFile;
    }

    public static boolean tryToLoadCertFileFromDownloads(Context ctx) {
       Log.e (TAG, "tryToLoadCertFileFromDownloads ....");

        boolean hasCertificate = false;

        int len;
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Log.e(TAG, "downloadPath: " +  downloadPath);
        try {

            boolean exists = (new File(downloadPath)).exists();
            if (exists) {

                File downloadDir = new File(downloadPath);
                File downloadedCertFile = null;

                for (File child : downloadDir.listFiles()) {
                    if (child.getName().endsWith(".p12")){
                        downloadedCertFile = child;
                        break;
                    }
                }

                if (downloadedCertFile != null) {

                    // Remove previous embedded  certificate
                    File actualAppCertFile = FileHelper.findAppCertificateFile(ctx);
                    if (actualAppCertFile != null) {
                        deleteFile(actualAppCertFile);
                    }

                    // Reset request Queue
                    AppController.getInstance().getRestEngine().resetRequestQueue();

                    // prepare for load the new one
                    actualAppCertFile = FileHelper.getAppCertFile(ctx, downloadedCertFile.getName());
                    Log.e(TAG, "appFile: " +  actualAppCertFile.getPath());

                    // original file
                    FileInputStream in = new FileInputStream(downloadPath + "/" + downloadedCertFile.getName());
                    // copy original to actual certFile
                    FileOutputStream out = new FileOutputStream(actualAppCertFile.getPath(), false);

                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();

                    hasCertificate = true;

                    // Remove original FILE
                    deleteFile(downloadedCertFile);
                    Log.e(TAG, "ok removing original file: " + downloadedCertFile.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "tryToLoadCertFileFromDownloads error: ");
            e.printStackTrace();
        }

        return hasCertificate;
    }
    // -- TODO end CERTIFICADOS -------------------------------------------------


    public static File getResouceFile(Context ctx, String dir, String fileName) {
        File mediaDir = new File(ctx.getExternalFilesDir(null), dir);

        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                Log.e(TAG, "Failed to create dir: " + dir);
                return null;
            }
        }

        File file = new File(mediaDir.getPath() + File.separator + fileName);
        Log.e (TAG, "APP file: " + file.getAbsolutePath());

        return file;
    }


    public static void deleteUserFiles(Context ctx) {
        File rootDir = new File(ctx.getExternalFilesDir(null), "");
        Log.e(TAG, "rootDir to delete: " + rootDir);

        deleteRecusrsive(rootDir);
        Log.e(TAG, "rootDir deleted: ok !");
    }

    private static void deleteRecusrsive(File file) {
        Log.e(TAG, "deleteRecusrsive:");

        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    // AVOID delete download ctx file --> will sotre certificate data
                    if (!child.getAbsolutePath().endsWith(ConstantsUtil.DOWNLOAD_DIR)) {
                        Log.e(TAG, "child file: " + child.getAbsolutePath());
                        deleteRecusrsive(child);
                    }
                }
            }
            Log.e(TAG, "file path: " + file.getAbsolutePath());
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(File file){
        try {
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void saveFile(Context ctx, Uri sourceuri, String outputPath) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            InputStream imageStream = ctx.getContentResolver().openInputStream(sourceuri);
            bis = new BufferedInputStream(imageStream);
            bos = new BufferedOutputStream(new FileOutputStream(outputPath, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getFileExtension(Context ctx, Uri sourceUri) {

        String extension = null;
        // get the file extension
        String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = ctx.getContentResolver().query(sourceUri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {

            int fileNameIndex = cursor.getColumnIndex(filePathColumn[1]);
            String fileName = cursor.getString(fileNameIndex);
            Log.e(TAG, fileName);
            extension = fileName.replaceAll("^.*\\.", "");
        }
        cursor.close();
        return extension;
    }

    public static String getFileNameAndExtension(Context ctx, Uri sourceUri) {

        String fileName = null;
        // get the file extension
        String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = ctx.getContentResolver().query(sourceUri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int fileNameIndex = cursor.getColumnIndex(filePathColumn[1]);
            fileName = cursor.getString(fileNameIndex);
        }
        cursor.close();
        return fileName;
    }

    public static void loadUriFromFile(Context ctx, String filePath, final LoadResourceUriCallback callback) {
        try {
            File file = new File(filePath);
            Uri uri = FileProvider.getUriForFile(ctx, ConstantsUtil.FILE_PROVIDER, file);

            callback.onSuccesLoadUri(uri);
        } catch (Exception e) {
            callback.onErrorLoadUri("Error loading resource");
        }
    }

    public static File getFile(String filePath){
        File file = null;
        try {
            file = new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  file;
    }


    public static void removeFile(String filePath) {
        try {
            File file = new File(filePath);
            file.delete();
            Log.e(TAG, "remove File:" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmapFormFile(File file, int quality) {

        Log.e(TAG , "getBitmapFormFile::: " + file.getAbsolutePath());

        FileInputStream fis = null;
        Bitmap bm = null;
        try {
            fis = new FileInputStream(file);

            bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath().endsWith(".PNG") ) {
                bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
            }else if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".JPG") ) {
                bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bm;
    }

    private static File getFileFormBitmap(Bitmap thumbnail, File inFile, int quality) {
        Log.e(TAG , "getFileFormBitmap::: " + inFile.getAbsolutePath());

        File file = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (inFile.getAbsolutePath().endsWith(".png") || inFile.getAbsolutePath().endsWith(".PNG") ) {
                thumbnail.compress(Bitmap.CompressFormat.PNG, quality, bytes);
            }else if (inFile.getAbsolutePath().endsWith(".jpg") || inFile.getAbsolutePath().endsWith(".JPG") ){
                thumbnail.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
            }

            FileOutputStream fo;

            inFile.createNewFile();
            fo = new FileOutputStream(inFile);
            fo.write(bytes.toByteArray());
            fo.close();

            file = inFile;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static File getFileFormBitmap(File inFile, int sampleSize, int quality) {
        Log.e(TAG, "getFileFormBitmap......"  + inFile.getAbsolutePath());

        File file = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            FileInputStream inputStream = new FileInputStream(inFile);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            inFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(inFile);

            if (inFile.getAbsolutePath().endsWith(".png") || inFile.getAbsolutePath().endsWith(".PNG") ) {
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            }else if (inFile.getAbsolutePath().endsWith(".jpg") || inFile.getAbsolutePath().endsWith(".JPG") ){
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            outputStream.close();
            file = inFile;

            selectedBitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }




    public static File compressImageFile(String filePath, int quality, boolean resize) {
        try {
            File file = new File(filePath);

            if (filePath.endsWith(".png") || filePath.endsWith(".jpg")) {
                // TODO TEMPORARY comeented
                //return compressImageFile(file, quality,  resize);

                return file;
            } else {
                return file;
            }

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public static File compressImageFile(File inFile, int quality, boolean resize) {
        File file = null;
        Bitmap bm = getBitmapFormFile(inFile, 100);
        if (bm != null) {

            // First maintain scale and decrease quality
            file = getFileFormBitmap(bm, inFile, quality);

            //Second Lopp: Maintain quality but download widht/height  and scale it

            if (file != null) {
                Log.e(TAG, "file.length() " + file.length());

                if (resize) {
                    int sampleSize = 1;
                    while (file != null && file.length() > MAX_FILE_LENGTH) {
                        sampleSize = sampleSize * 2;  // scale 1/2, ..1/4  and so each time
                        file = getFileFormBitmap(file, sampleSize, quality);
                    }
                    if (file != null) {
                        Log.e(TAG, "file.length() desps: " + file.length());
                    }
                }
            }

        }
        return file;
    }


    public static String encodeFileToBase64(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(is);
        return encodeData(bytes);
    }

    public static String encodeData(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    public static Double calcBase64SizeInKBytes(String base64String) {
        Double result = -1.0;
        if(!base64String.isEmpty()) {
            Integer padding = 0;
            if(base64String.endsWith("==")) {
                padding = 2;
            }
            else {
                if (base64String.endsWith("=")) padding = 1;
            }
            result = (Math.ceil(base64String.length() / 4) * 3 ) - padding;
        }
        return result / 1000;
    }

    // TODO GET FILE FORM URL NOT FORM B64
    public static File getFile(Context ctx, String subDir, String resourceName, String linkUrl) {
        File file = getImageFile(ctx, subDir, resourceName);

        try {

            URL url = new URL(linkUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(AppController.getInstance().getRestEngine().getFactory());

            FileOutputStream fos = new FileOutputStream(file);
            InputStream in = connection.getInputStream();

            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }
            in.close();
            fos.flush();
            fos.close();
        }catch (Exception e){
            //javax.net.ssl.SSLHandshakeException: Connection closed by peer
            e.printStackTrace();
        }
        return file;
    }

    public static File decodeAttachFile(Context ctx, String subDir, String resourceName, final String fileData) {
        Uri uri = null;
        File file = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            final byte[] imgBytesData = Base64.decode(fileData, Base64.DEFAULT);
            file = getImageFile(ctx, subDir, resourceName);


            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(imgBytesData);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static String getResourceName(String filePath) {
        int index = filePath.lastIndexOf("/");
        String data = filePath.substring(index + 1, filePath.length());
        return data.split("\\.")[0];
    }

    public static String getFileExtension(String filePath) {
        int index = filePath.lastIndexOf("/");
        String data = filePath.substring(index + 1, filePath.length());
        return data.split("\\.")[1];
    }


    public static String getFilePrefix() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


    public static String decodeQRCode(File inputFile) {
        String contents = null;

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            if (bitmap != null) {

                //Log.e(TAG, "Dim antes : w, h" + bitmap.getWidth()  + ","  + bitmap.getHeight());
                Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);

                if (resizeBitmap.getHeight() > resizeBitmap.getWidth() ) {
                    //Log.e(TAG, "rotate bitmap...");
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-90);
                    resizeBitmap = Bitmap.createBitmap(resizeBitmap, 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight(), matrix, true);
                }

                //Log.e(TAG, "Dim desps : w, h" + resizeBitmap.getWidth()  + ","  + resizeBitmap.getHeight());
                if (resizeBitmap != null) {
                    Map<DecodeHintType, Object> tmpHintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
                    tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                    tmpHintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
                    tmpHintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
                    tmpHintsMap.put(DecodeHintType.CHARACTER_SET, "utf-8");

                    int[] intArray = new int[resizeBitmap.getWidth() * resizeBitmap.getHeight()];
                    //copy pixel data from the Bitmap into the 'intArray' array
                    resizeBitmap.getPixels(intArray, 0, resizeBitmap.getWidth(), 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(resizeBitmap.getWidth(), resizeBitmap.getHeight(), intArray);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();
                    Result result = reader.decode(binaryBitmap, tmpHintsMap);

                    if (result != null) {
                        contents = result.getText();
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error decoding barcode", e);
        }
        return contents;
    }

}
