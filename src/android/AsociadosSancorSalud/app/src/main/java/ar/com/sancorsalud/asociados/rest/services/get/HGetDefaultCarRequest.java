package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.Car;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 27/9/17.
 */

public class HGetDefaultCarRequest extends HRequest<Car> {

    private static final String PATH = RestConstants.HOST_USER + RestConstants.GET_DEFAULT_CAR;

    public HGetDefaultCarRequest(long userId, Response.Listener<Car> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH + userId, listener, errorListener);
    }

    @Override
    protected Car parseObject(Object obj) {

        if (obj == null)
            return null;

        JSONObject jsonObj = (JSONObject) obj;
        long id = jsonObj.optLong("id", -1);

        if (id != -1) {
            Car q = new Car();
            q.id = id;
            q.description = ParserUtils.optString(jsonObj, "descripcion");
            return q;
        }

        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }

}
