package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.affiliation.DesNumberVerification;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HGetVerifyDESNumberRequest extends HRequest<DesNumberVerification> {

    private static final String TAG = "DES_VERIFY_NUMBER_REQ";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_DES_NUMBER_VERIFICATION;

    public HGetVerifyDESNumberRequest(long desNumber, Response.Listener<DesNumberVerification> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + desNumber, listener, errorListener);
    }

    /*
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        if (mHttpMethod != Method.POST && mHttpMethod != Method.PUT) {
            headers.put("Content-Type", "application/json; charset=utf-8");
        }

        if (mHttpMethod == Method.PATCH) {
            headers.put("X-HTTP-Method-Override", "PATCH");
        }

        headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (HRequest.authorizationHeaderValue != null)
            headers.put("Token", HRequest.authorizationHeaderValue);
        return headers;
    }
    */



    @Override
    protected DesNumberVerification parseObject(Object obj) {

        DesNumberVerification desNumberVerification = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                desNumberVerification = new DesNumberVerification();
                try {

                    Log.e(TAG, "DES NUMBER VERIFICATION RESP: " + dic.toString());
                    desNumberVerification.existentDes = dic.optBoolean("existe_des", false);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return desNumberVerification;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}