package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by luciano on 8/29/17.
 */

public class HPostVerifyTicketPagoAnticipadoRequest extends HRequest<Boolean> {

    private static final String TAG = "HPOST_VERIFY_TICK_PAGO";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFY_TICKET_PAGO_ANTICIPADO;

    private long affiliationCardId;

    public HPostVerifyTicketPagoAnticipadoRequest(long affiliationCardId, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.affiliationCardId = affiliationCardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", affiliationCardId);
            Log.e(TAG, "SEND JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Boolean parseObject(Object obj) {

        Boolean hastoVerify = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e(TAG, "RESULT GET VERIFY TICKET PAGO!::: " + dic.toString());
                    hastoVerify = dic.optBoolean("control",false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "ERROR GET VERIFY TICKET PAGO! : " + e.getMessage());
                }
            }
        }

        return hastoVerify;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

