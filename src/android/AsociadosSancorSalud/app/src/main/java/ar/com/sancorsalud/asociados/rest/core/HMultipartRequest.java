package ar.com.sancorsalud.asociados.rest.core;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by sergio on 9/21/15.
 */
public abstract class HMultipartRequest<T> extends HRequest<T> {

    private static final String BOUNDARY = "----WebKitFormBoundarylu0mESBhpufhnTAd";
    protected MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    private HttpEntity mHttpentity;
    private Map<String, String> mFileUploads = null;

    public HMultipartRequest(int httpMethod, String path, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(httpMethod, path, listener, errorListener);

        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setCharset(Charset.forName("UTF-8"));
        entity.setBoundary(BOUNDARY);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        headers.put("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return null;
    }

    //Override to avoid add invalid headers
    protected void buildMultipartEntity() {

    }

    protected void didFinishToBuildMultipartEntity() {
        mHttpentity = entity.build();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpentity.writeTo(new FilterOutputStream(bos));
            return bos.toByteArray();

        } catch (Throwable e) {
            //VolleyLog.e("" + e.toString());
            return null;
        }
        //return bos.toByteArray();
    }

}
