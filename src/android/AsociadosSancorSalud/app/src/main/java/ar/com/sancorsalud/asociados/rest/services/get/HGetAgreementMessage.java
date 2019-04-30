package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

public class HGetAgreementMessage extends HRequest<String> {

    private static final String TAG = "HPOST_ADD_AGREEMENT";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_AGREEMENT_MESSAGE;

    private long agreementId;

    public HGetAgreementMessage(long agreementId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + agreementId, listener, errorListener);
        this.agreementId = agreementId;
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
