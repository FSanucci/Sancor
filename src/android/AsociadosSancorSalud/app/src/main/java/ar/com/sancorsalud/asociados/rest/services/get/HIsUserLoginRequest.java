package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

public class HIsUserLoginRequest extends HRequest<Boolean> {

    private static final String PATH = RestConstants.HOST + RestConstants.IS_LOGGED_IN;

    public HIsUserLoginRequest(Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected Boolean parseObject(Object obj) {

        try {
            return ((JSONObject) obj).getBoolean("status");
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
