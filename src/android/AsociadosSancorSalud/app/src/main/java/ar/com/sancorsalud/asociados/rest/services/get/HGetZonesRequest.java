package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/29/17.
 */

public class HGetZonesRequest  extends HRequest<ArrayList<Zone>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_ZONES;

    public HGetZonesRequest(Response.Listener<ArrayList<Zone>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH , listener, errorListener);
    }

    @Override
    protected ArrayList<Zone> parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {
                try {
                    JSONArray jsonList = dic.getJSONArray("zonas");
                    if(jsonList!=null) {
                        ArrayList<Zone> list = new ArrayList<>();
                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject zoneDic = jsonList.optJSONObject(i);
                            Zone zone = new Zone();
                            zone.id = zoneDic.optInt("zona_id",-1);
                            zone.name = ParserUtils.optString(zoneDic,"descripcion_zona");
                            if(zone.id!=-1)
                                list.add(zone);
                        }
                        return list;
                    }
                } catch (Exception e) {
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
