package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.Car;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 27/9/17.
 */

public class HGetCarsRequest extends HRequest<ArrayList<Car>> {

    private static final String TAG = "HGetCars";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_CARS;

    public HGetCarsRequest(Response.Listener<ArrayList<Car>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
    }

    @Override
    public byte[] getBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll(super.getHeaders());
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }


    @Override
    protected ArrayList<Car> parseObject(Object obj) {

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if(dic!=null){
                JSONArray jsonArray = dic.optJSONArray("cars");
                if(jsonArray!=null && jsonArray.length()>0){
                    ArrayList<Car> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.optJSONObject(i);
                        long id = data.optLong("id",-1);
                        if(id != -1){
                            Car q = new Car();
                            q.id = id;
                            q.description = ParserUtils.optString(data,"descripcion");
                            list.add(q);
                        }
                    }
                    return list;
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
