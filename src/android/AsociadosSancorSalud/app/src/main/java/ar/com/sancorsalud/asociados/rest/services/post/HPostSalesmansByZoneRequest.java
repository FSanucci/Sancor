package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.SalesmanIndicator;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 4/10/17.
 */

public class HPostSalesmansByZoneRequest extends  HRequest<ArrayList<Salesman>>  {

    private static final String TAG = "HPOST_SALESMAN_ZONE";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_SALESMANS_BY_ZONE;

    private long mZoneId;

    public HPostSalesmansByZoneRequest(long zoneId, Response.Listener<ArrayList<Salesman>> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mZoneId = zoneId;

    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("zona_id", mZoneId);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ArrayList<Salesman> parseObject(Object obj) {
        ArrayList<Salesman> list = new ArrayList<>();

        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){


                try {
                    JSONArray jsonList = dic.getJSONArray("promotores");

                    if(jsonList!=null) {

                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject jSalesman = jsonList.optJSONObject(i);
                            Salesman s = new Salesman();
                            s.id = jSalesman.optInt("promotor_id",-1);
                            s.firstname = ParserUtils.optString(jSalesman,"nombre");
                            s.lastname = ParserUtils.optString(jSalesman,"apellido");
                            s.docNumber = ParserUtils.optString(jSalesman,"numero_documento");
                            s.zone = ParserUtils.optString(jSalesman,"zona");
                            s.zoneId = jSalesman.optInt("nro_zona",-1);

                            list.add(s);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

