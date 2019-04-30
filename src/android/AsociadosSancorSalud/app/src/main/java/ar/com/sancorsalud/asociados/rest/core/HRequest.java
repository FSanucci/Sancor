package ar.com.sancorsalud.asociados.rest.core;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 10/24/15.
 */
public abstract class HRequest<T> extends JsonRequest<T> {

    public static final String TAG = "HRequest";

    public static final int HTTP_REQUEST_CODE_200_OK = 200;
    public static final int HTTP_REQUEST_CODE_201_CREATED = 201;
    public static final int HTTP_REQUEST_CODE_202_ACCEPTED = 202;
    public static final int HTTP_REQUEST_CODE_204_NO_CONTENT = 204;
    public static final int HTTP_REQUEST_CODE_400_BAD_REQUEST = 400;
    public static final int HTTP_REQUEST_CODE_401_UNAUTHORIZED = 401;
    public static final int HTTP_REQUEST_CODE_403_BAD_FORBIDDEN = 403;
    public static final int HTTP_REQUEST_CODE_403_NOT_FOUND = 404;
    public static final int HTTP_REQUEST_CODE_INVALID_TOKEN = -1;

    public static String authorizationHeaderValue = null;

    protected String mPath;
    protected int mHttpMethod;
    public LoginErrorListener<T> loginErrorListener;
    protected Response.Listener<T> mSuccessfulListener;
    protected Response.ErrorListener mErrorListener;
    public String jsonResponse;

    public HRequest(int httpMethod, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super((httpMethod == Method.PATCH) ? Method.POST : httpMethod, url, null, listener, errorListener);
        mPath = url;
        mHttpMethod = httpMethod;
        mErrorListener = errorListener;
        mSuccessfulListener = listener;
        setTag(this.getClass().getName());
    }

    public String getUrl() {
        return mPath;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        if (mHttpMethod != Method.POST && mHttpMethod != Method.PUT) {
            headers.put("Content-Type", "application/json; charset=utf-8");
        }

        if (mHttpMethod == Method.DELETE)
            headers.put("Authorization", "Basic " + "121212121221");
        else
            headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (mHttpMethod == Method.PATCH) {
            headers.put("X-HTTP-Method-Override", "PATCH");
        }

        if (HRequest.authorizationHeaderValue != null)
            headers.put("token", HRequest.authorizationHeaderValue);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() {
        return null;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {


        Log.e(TAG, "parseNetworkResponse!...." + toString());

        if (responseHaveRequestError(response))
            return Response.error(new VolleyError(response.statusCode + " | Request failed"));

        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, HTTP.UTF_8));
            if (jsonString == null || jsonString.equals(""))
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));


            this.jsonResponse = jsonString;
            Object jsonObj = null;
            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject)
                jsonObj = new JSONObject(jsonString);
            else if (json instanceof JSONArray)
                jsonObj = new JSONArray(jsonString);

            // check for special case response is 200 ok but status is error
            VolleyError error = parseError(jsonObj);
            if (error != null)
                return Response.error(error);

            T obj = null;
            obj = parseObject(jsonObj);

            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {


        Log.e(TAG, "parseNetworkError....");

        if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 403 && volleyError.toString().contains("AuthFailureError")) {
            HVolleyError error = new HVolleyError("com.android.volley.AuthFailureError", HTTP_REQUEST_CODE_INVALID_TOKEN);
            return error;
        }

        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            try {
                if (volleyError.getCause() != null) {
                    Log.e("", "" + volleyError.getCause().toString());
                }
                String str = new String(volleyError.networkResponse.data);
                Log.e("ERROR:: ", str);

                JSONObject jsonObj = new JSONObject(str);
                HVolleyError error = parseError(jsonObj);
                if (error != null)
                    return error;

            } catch (JSONException e) {
            }
            return new HVolleyError(new String(volleyError.networkResponse.data), 0);
        }
        return volleyError;
    }

    protected boolean responseHaveRequestError(NetworkResponse response) {
        return response.statusCode != HTTP_REQUEST_CODE_200_OK && response.statusCode != HTTP_REQUEST_CODE_201_CREATED && response.statusCode != HTTP_REQUEST_CODE_202_ACCEPTED && response.statusCode != HTTP_REQUEST_CODE_204_NO_CONTENT;
    }

    @Override
    public String toString() {

        String hMethod = "";
        switch (mHttpMethod) {
            case 0:
                hMethod = "GET";
                break;
            case 1:
                hMethod = "POST";
                break;
            case 2:
                hMethod = "PUT";
                break;
            case 7:
                hMethod = "PATH";
                break;
            case 3:
                hMethod = "DELETE";
                break;
        }

        String str = "\n" + getClass().getName() + "\n" + "HTTP Method= " + hMethod + "\n" + "HTTP URL= " + mPath + "\n" + "HTTP Header Token= " + authorizationHeaderValue + "\n --------------";

        byte[] body = getBody();
        if (body != null)
            str += "\nHTTP Body= " + new String(body, Charset.forName("UTF-8"));

        return str;
    }

    protected abstract T parseObject(Object obj);

    protected abstract HVolleyError parseError(Object obj);

    @Override
    public void deliverError(VolleyError error) {
        if (this.loginErrorListener != null && (error instanceof HVolleyError) && ((HVolleyError) error).getErrorCode() == HTTP_REQUEST_CODE_INVALID_TOKEN) {
            this.loginErrorListener.onErrorResponse(this);
        } else if (this.mErrorListener != null) {
            this.mErrorListener.onErrorResponse(error);
        }
    }

    public void notifyError(VolleyError error) {
        if (this.mErrorListener != null) {
            this.mErrorListener.onErrorResponse(error);
        }
    }

}