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
 * Created by sergiocirasa on 27/9/17.
 */

public class HPostDefaultCarRequest extends HRequest<Void> {

    private static final String TAG = "HPostDefaultCar";
    private static final String PATH = RestConstants.HOST_USER + RestConstants.POST_DEFAULT_CAR;
    private long mUserId;
    private long mCarId;

    public HPostDefaultCarRequest(long userId, long carId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.mUserId = userId;
        this.mCarId = carId;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("idUsuario", mUserId);
            json.put("carId", mCarId);
            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Void parseObject(Object obj) {
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }


}
