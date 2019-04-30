package ar.com.sancorsalud.asociados.rest.core;

import com.android.volley.VolleyError;

/**
 * Created by sergio on 10/24/15.
 */
public class HVolleyError extends VolleyError {

    private Integer mErrorCode;
    private String mErrorData;
    public Object errorObject;

    public HVolleyError(String exceptionMessage, Integer errorCode) {
        this(exceptionMessage, errorCode, null);
    }

    public HVolleyError(String exceptionMessage,Integer errorCode,  String data) {
        super(exceptionMessage);
        mErrorCode = errorCode;
        mErrorData = data;
    }

    public Integer getErrorCode(){
        return mErrorCode;
    }

    public String getErrorData(){
       return mErrorData;
    }

}
