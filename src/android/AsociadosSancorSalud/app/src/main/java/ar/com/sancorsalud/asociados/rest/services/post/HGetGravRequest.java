package ar.com.sancorsalud.asociados.rest.services.post;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import ar.com.sancorsalud.asociados.model.Station;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 11/10/17.
 */

public class HGetGravRequest extends HRequest<HashMap<String,Station>> {

    private static final String TAG = "GET_GRAV";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_GRAV;
    private long mProspectiveClienteId;

    public HGetGravRequest(long paId, Response.Listener<HashMap<String,Station>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mProspectiveClienteId = paId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mProspectiveClienteId);
            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected HashMap<String,Station> parseObject(Object obj) {

        if (obj instanceof JSONObject) {
            try {
                JSONArray jsonArray = (JSONArray) ((JSONObject) obj).getJSONArray("data");

                if (jsonArray != null && jsonArray.length() > 0) {
                    HashMap<String,Station> list = new HashMap<String,Station>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.optJSONObject(i);
                        Station s = new Station();
                        s.nodo = ParserUtils.optString(data, "nodo");
                        s.state = data.optBoolean("estado", false);
                        s.date = ParserUtils.parseDate(data, "fecha", ParserUtils.DATE_TIME_FORMAT);
                        s.username = ParserUtils.optString(data, "nombre_usuario");
                        s.shortComment = ParserUtils.optString(data, "comentario");
                        s.largeComment = ParserUtils.optString(data, "comentario_largo");
                        list.put(s.nodo,s);
                    }
                    return list;
                }


            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
