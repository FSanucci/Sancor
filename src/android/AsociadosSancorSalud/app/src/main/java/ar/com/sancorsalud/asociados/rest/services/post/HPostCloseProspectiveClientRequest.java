package ar.com.sancorsalud.asociados.rest.services.post;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HPostCloseProspectiveClientRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.CLOSE_PROSPECTIVE_CLIENT;
    private long mProspectiveClientId;
    private long mReasonId;

    public HPostCloseProspectiveClientRequest(long prospectiveClientId, long reasonId , Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mProspectiveClientId = prospectiveClientId;
        mReasonId = reasonId;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mProspectiveClientId);
            json.put("motivo_id", mReasonId);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Void parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}
