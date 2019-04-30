package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/20/17.
 */

public class HGetZoneLeaderCounterRequest extends HRequest<Counter> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_ZONELEADER_COUNTERS;

    public HGetZoneLeaderCounterRequest(Response.Listener<Counter> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH , listener, errorListener);
    }

    @Override
    protected Counter parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {
                Counter counter = new Counter();
                counter.pendingAssignments = dic.optInt("cantidad_pendientes",0);
                counter.totalAssignments = dic.optInt("cantidad",0);
                return counter;
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
