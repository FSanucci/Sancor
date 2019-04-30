package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 30/3/17.
 */

public class HGetNotificationCounterRequest extends HRequest<Integer> {


    private static final String PATH = RestConstants.HOST + RestConstants.GET_NOTIFICATIONS_COUNTERS;

    public HGetNotificationCounterRequest(Response.Listener<Integer> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH , listener, errorListener);
    }

    @Override
    protected Integer parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {

                Log.e(TAG, "HGetNotificationCounterRequest count: " + dic.optInt("notificaciones",0) );

                return dic.optInt("notificaciones",0);
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
