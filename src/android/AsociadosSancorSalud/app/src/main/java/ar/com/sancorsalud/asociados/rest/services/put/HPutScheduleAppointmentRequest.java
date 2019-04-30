package ar.com.sancorsalud.asociados.rest.services.put;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.nio.charset.Charset;
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

public class HPutScheduleAppointmentRequest extends HRequest<Void>{
    private static final String PATH = RestConstants.HOST + RestConstants.CREATE_APPOINTMENT;
    private ProspectiveClient mClient;

    public HPutScheduleAppointmentRequest(ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, PATH, listener, errorListener);
        mClient = client;

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
            json.put("potencial_id", mClient.appointment.prospectiveClientId);
            if(mClient.appointment.notes!=null && !TextUtils.isEmpty(mClient.appointment.notes))
                json.put("notas", mClient.appointment.notes);

            json.put("fecha_agenda", ParserUtils.parseDateTime(mClient.appointment.date));
            json.put("calendario_id", mClient.appointment.scheduleId);
            json.put("direccion_encuentro", mClient.appointment.address);

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
