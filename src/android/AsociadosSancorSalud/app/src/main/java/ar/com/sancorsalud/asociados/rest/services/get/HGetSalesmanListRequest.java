package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HGetSalesmanListRequest extends HRequest<ArrayList<Salesman>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_SALESMANS;

    public HGetSalesmanListRequest(Response.Listener<ArrayList<Salesman>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<Salesman> parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                try {
                    JSONArray jsonList = dic.getJSONArray("promotores");
                    if(jsonList!=null) {
                        ArrayList<Salesman> list = new ArrayList<>();
                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject salesman = jsonList.optJSONObject(i);
                            Salesman s = new Salesman();
                            s.id = salesman.optInt("promotor_id",-1);
                            s.firstname = ParserUtils.optString(salesman,"nombre");
                            s.lastname = ParserUtils.optString(salesman,"apellido");
                            s.pendingAssignments = salesman.optInt("pot_asignados",0);
                            s.totalAssignments = salesman.optInt("pot_historica",0);
                            s.description = ParserUtils.optString(salesman,"descripcion");
                            s.zoneId = salesman.optInt("nro_zona",-1);

                            JSONArray jManualQuotationArray = salesman.getJSONArray("cotizaciones_manuales");
                            if (jManualQuotationArray != null )
                                s.totalManualQuotes = jManualQuotationArray.length();

                            list.add(s);
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
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
