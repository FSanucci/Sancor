package ar.com.sancorsalud.asociados.manager;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.post.HPostDesSaveRequest;

/**
 * Created by sergio on 1/29/17.
 */

public class ZoneController extends BaseListController<Zone> {
    private static final String ZONE_KEY = "zone_v1";
    private static final String LAST_UPDATE_KEY = "zone_last_update";
    private static final long UPDATE_EACH_SECONDS = 3600 * 72;
    private static ZoneController instance = new ZoneController();

    public static synchronized ZoneController getInstance() {
        return instance;
    }

    public String getObjectKey() {
        return ZONE_KEY;
    }

    public String getLastUpdateKey() {
        return LAST_UPDATE_KEY;
    }

    public long getSecondsToUpdate() {
        return UPDATE_EACH_SECONDS;
    }

    public String getLocalJsonName() {
        return null;
    }

    public ArrayList<Zone> parseData(String json) {
        return null;
    }

    protected Type typeToken() {
        return new TypeToken<ArrayList<Zone>>() {
        }.getType();
    }

    public HRequest createService(Response.Listener<ArrayList<Zone>> listener, Response.ErrorListener errorListener) {
        return RestApiServices.createGetZonesRequest(listener, errorListener);
    }

    /*
    public static HRequest<?> salesmanByZoneRequest(Des des, Response.Listener<List<Salesman>> listener, Response.ErrorListener errorListener) {
        return new HPostSalesmanByZoneRequest(des, listener, errorListener);
    }
    */



    public Zone zoneAtId(long zoneId){
        for(Zone zone : mList){
            if(zone.id == zoneId)
                return zone;
        }
        return null;
    }
}
