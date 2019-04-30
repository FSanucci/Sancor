package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.affiliation.VerifyAuthorizationCardResponse;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by francisco on 15/11/17.
 */

public class HPostVerifyAltaInmediataRequest extends HRequest<Boolean> {

    private static final String TAG = "HPOST_VERIFY_ALTA_INMD";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFY_ALTA_INMEDIATA;
    private long mCardId;


    public HPostVerifyAltaInmediataRequest(long cardId, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mCardId = cardId;
    }


    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", mCardId  != -1L ?  mCardId : JSONObject.NULL);
            String body = json.toString();

            Log.e(TAG, "HPostVerifyAltaInmd: "  +  json.toString());
            return body.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected Boolean parseObject(Object obj) {

        Boolean control = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e (TAG, "HPostVerifyAltaInmd response: " + dic.toString());
                    control = dic.optBoolean("control");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return control;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
