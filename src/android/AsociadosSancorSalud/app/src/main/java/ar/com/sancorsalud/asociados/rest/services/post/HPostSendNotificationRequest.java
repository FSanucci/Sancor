package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by sergiocirasa on 4/10/17.
 */

public class HPostSendNotificationRequest extends HRequest<Void> {

    private static final String TAG = "HPOST_SEND_NOTIF";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_SEND_NOTIFICATION;

    private static final  int ZONE_DESTIN = 5;

    private long mNotificationId;
    private long mZoneId;
    private long mSalesmanId;

    public HPostSendNotificationRequest(long notificationId, long zoneId, long salesmanId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mNotificationId = notificationId;
        this.mZoneId = zoneId;
        this.mSalesmanId = salesmanId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("mensaje_id", mNotificationId);
            json.put("zona_id", mZoneId);
            json.put("destinatario", mSalesmanId);
            json.put("destino", ZONE_DESTIN);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Void parseObject(Object obj) {

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e (TAG, dic.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

