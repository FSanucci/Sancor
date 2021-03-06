package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_TIME_FORMAT;

/**
 * Created by sergiocirasa on 29/3/17.
 */

public class HGetNotificationSendedRequest extends HRequest<ArrayList<Notification>> {

    private static final String TAG = "NOTIF_REQ";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_NOTIFICATIONS_SENDED;

    public HGetNotificationSendedRequest(long userId, Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + userId, listener, errorListener);
    }

    @Override
    protected ArrayList<Notification> parseObject(Object obj) {
        ArrayList<Notification> list = new ArrayList<>();

        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                Log.e(TAG, "Sended: Notif:" + dic.toString());

                try {
                    JSONArray array = dic.getJSONArray("notificaciones");
                    if(array!=null) {

                        Log.e (TAG, "Size: " + array.length());

                        for (int i = 0; i < array.length(); i++) {

                            //Log.e (TAG, "index:" + i );
                            JSONObject pc = array.optJSONObject(i);

                            Notification notif = new Notification();
                            notif.id = pc.optLong("id",0);
                            notif.notificationId = pc.optLong("notificacion_id",0);
                            notif.title = ParserUtils.optString(pc, "titulo");
                            notif.date = ParserUtils.parseDate(pc, "fecha",DATE_TIME_FORMAT);
                            notif.isRead = ParserUtils.optString(pc, "leido")!= null ? (ParserUtils.optString(pc, "leido").equalsIgnoreCase("N")?false:true): false;
                            notif.description = ParserUtils.optString(pc,"texto");
                            notif.link = ParserUtils.optString(pc,"link");

                            String priority =  ParserUtils.optString(pc,"importancia");
                            notif.priority = Notification.getPriority(priority);
                            notif.owner =  new Long(pc.optLong("autor", -1L)).toString();

                            list.add(notif);
                        }
                        return list;
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Error parseando en las notificaciones" );
                    list = new ArrayList<>();
                }
            }

        }

        return list;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}