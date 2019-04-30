package ar.com.sancorsalud.asociados.rest.services.get;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HCheckProspectiveClientRequest extends HRequest<ExistenceStatus> {

    private static final String PATH = RestConstants.HOST + RestConstants.CHECK_PROSPECTIVE_CLIENT;

    public HCheckProspectiveClientRequest(String dni, Response.Listener<ExistenceStatus> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH+dni, listener, errorListener);
    }

    @Override
    protected ExistenceStatus parseObject(Object obj) {
        ExistenceStatus response = null;

        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) obj;

            Log.e(TAG, "ExistenceStatus RESP: " + dic.toString());

            if(dic!=null){
                response = new ExistenceStatus();
                response.status = ParserUtils.optString(dic, "status");
                response.errorCode = dic.optInt("errorCode",-1);

                try {
                    JSONObject data = dic.getJSONObject("data");

                    response.guardable = data.optBoolean("guardable", false);
                    response.workflow = data.optBoolean("workflow", false);
                    response.message = ParserUtils.optString(data, "mensaje");

                }catch(Exception e){
                }
            }
        }
        return response;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}
