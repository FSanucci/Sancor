package ar.com.sancorsalud.asociados.rest.services.put;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HPutAssignSalesmanRequest extends HRequest<Void> {
    private static final String PATH = RestConstants.HOST + RestConstants.ASSIGN_SALESMAN;
    private ArrayList<ProspectiveClient> mClients;
    private long mSalesmanId;

    public HPutAssignSalesmanRequest(long salesmanId, ArrayList<ProspectiveClient> clients, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, PATH, listener, errorListener);
        mSalesmanId = salesmanId;
        mClients = clients;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        //  headers.put("Content-Type", "application/json; charset=utf-8");

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
            json.put("promotor_id", mSalesmanId);
            JSONArray array = new JSONArray();
            for(int i=0; i< mClients.size();i++) {
                ProspectiveClient client = mClients.get(i);
                array.put(client.id);
            }
            json.put("potencial_id", array);
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
