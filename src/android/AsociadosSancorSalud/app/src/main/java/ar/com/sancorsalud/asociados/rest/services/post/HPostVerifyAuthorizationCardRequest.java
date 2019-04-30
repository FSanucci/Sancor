package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Request;
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

public class HPostVerifyAuthorizationCardRequest extends HRequest<VerifyAuthorizationCardResponse> {

    private static final String TAG = "HPOST_VERIFY_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFY_AUTHORIZATION_CARD;
    private long mCardId;

    public HPostVerifyAuthorizationCardRequest(long cardId, Response.Listener<VerifyAuthorizationCardResponse> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.mCardId = cardId;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", mCardId != -1L ? mCardId : JSONObject.NULL);
            String body = json.toString();

            Log.e(TAG, "HPostVerifyCard: " + json.toString());
            return body.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected VerifyAuthorizationCardResponse parseObject(Object obj) {

        VerifyAuthorizationCardResponse response = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e (TAG, "Change HPostVerifyCard response: " + dic.toString());
                    response = new VerifyAuthorizationCardResponse();

                    boolean control = dic.optBoolean("control"); // Not in use
                    response.control = control;

                    try {
                        JSONArray jMessagesArray = dic.getJSONArray("mensajes");
                        if (jMessagesArray != null && jMessagesArray.length() > 0 ){
                            for (int i = 0; i < jMessagesArray.length(); i++) {
                                String data = jMessagesArray.getString(i);
                                response.messages.add(data);
                            }
                        }

                    } catch (JSONException jEx) {
                    }

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
        return ParserUtils.parseError((JSONObject) obj);
    }
}
