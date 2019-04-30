package ar.com.sancorsalud.asociados.rest.services.put;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 2/7/17.
 */

public class HPutQuoteProspectiveClientRequest extends HRequest<Void> {
    private static final String PATH = RestConstants.HOST + RestConstants.PUT_QUOTE;
    private long mClientId;
    private String mQuote;

    public HPutQuoteProspectiveClientRequest(long clientId,String quote, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, PATH, listener, errorListener);
        mClientId = clientId;
        mQuote = quote;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        if (mHttpMethod == Method.PATCH) {
            headers.put("X-HTTP-Method-Override", "PATCH");
        }

        headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (HRequest.authorizationHeaderValue != null)
            headers.put("token", HRequest.authorizationHeaderValue);
        return headers;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mClientId);
            json.put("cotizacion_temp", mQuote);
        }catch(Exception e){

        }

        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    protected Void parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                //TODO Ver que devuelve el Web Service
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
