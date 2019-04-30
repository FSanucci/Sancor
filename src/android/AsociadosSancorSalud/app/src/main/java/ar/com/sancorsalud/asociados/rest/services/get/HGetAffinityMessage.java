package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Response;
import org.json.JSONObject;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

public class HGetAffinityMessage extends HRequest<String> {

    private static final String TAG = "HPOST_ADD_AFFINITY";
    private static final String PATH = RestConstants.WEB_HOST + RestConstants.GET_AFFINITY_MESSAGE;

    private long affinityId;

    public HGetAffinityMessage(long affinityId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + affinityId, listener, errorListener);
        this.affinityId = affinityId;
    }

    @Override
    protected String parseObject(Object obj) {
        String response = "";
        if (obj instanceof JSONObject) {

            if (obj != null) {
                try {
                    response = ((JSONObject) obj).optString("documento_mensaje");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return response;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }
}
