package ar.com.sancorsalud.asociados.rest.services.put;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPutRemoveNotificationRequest extends HRequest<Void> {
    private static final String PATH = RestConstants.HOST + RestConstants.PUT_REMOVE_NOTIFICATION;
    private long mNotificationId;

    public HPutRemoveNotificationRequest(long notificationId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, PATH, listener, errorListener);
        mNotificationId = notificationId;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", mNotificationId);
        }catch(Exception e){

        }

        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();


        headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (HRequest.authorizationHeaderValue != null)
            headers.put("token", HRequest.authorizationHeaderValue);
        return headers;
    }

    @Override
    protected Void parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                //TODO Ver que devuelve el Web Service, no devuelve el contentType correctamente
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}
