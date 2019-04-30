package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.affiliation.DesState;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostDesCheckStateRequest extends HRequest<DesState> {

    private static final String TAG = "HPOST_CHECK_DES_STATE";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_DES_CHECK_STATE;

    private long cardId;

    public HPostDesCheckStateRequest(long cardId , Response.Listener<DesState> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);

        this.cardId = cardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", (cardId != -1L ? cardId : JSONObject.NULL));
            Log.e(TAG, "JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected DesState parseObject(Object obj) {

        DesState desState = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                desState = new DesState();
                try {

                    Log.e(TAG, "CHECK DES RESP: " + dic.toString());

                    desState.stateId = dic.optInt("estado_id", -1);
                    desState.stateDesc = dic.optString("estado");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return desState;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
