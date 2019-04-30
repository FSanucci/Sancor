package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;

/**
 * Created by sergiocirasa on 4/10/17.
 */

public class HPostAddNotificationRequest extends HRequest<Long> {

    private static final String TAG = "HPOST_ADD_NOTIF";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_ADD_NOTIFICATION;

    private Notification mNotification;

    public HPostAddNotificationRequest(Notification notification, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mNotification = notification;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("autor", mNotification.owner);
            json.put("titulo", mNotification.title);
            json.put("texto", mNotification.description);
            json.put("vigencia", ParserUtils.parseDate(mNotification.date, DATE_FORMAT));
            json.put("importancia", Notification.getPriority(mNotification.priority));

            // ADD files
            if (mNotification.files.size()> 0) {
                JSONArray jFilesArray = new JSONArray();
                for (AttachFile attachFile : mNotification.files) {
                    jFilesArray.put(attachFile.id);
                }
                json.put("adjuntos", jFilesArray);
            }

            String body = json.toString();
            Log.e(TAG, "ADD NOTIFICATIO BODY : "  + body);


            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Long parseObject(Object obj) {

        Long notifId = -1L;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    Log.e(TAG, "ADD NOTIFICATION RESP: " + dic.toString());
                    notifId = dic.optLong("mensaje_id", -1L);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return notifId;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

