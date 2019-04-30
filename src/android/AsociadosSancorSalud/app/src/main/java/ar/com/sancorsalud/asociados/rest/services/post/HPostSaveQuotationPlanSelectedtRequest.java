package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostSaveQuotationPlanSelectedtRequest extends HRequest<Void> {

    private static final String TAG = "HPOST_SELEC_PLAN";
    private static final String PATH = RestConstants.HOST + RestConstants.SAVE_SELECTED_PLAN;
    private long quotedId;
    private Plan plan;

    public HPostSaveQuotationPlanSelectedtRequest(long quotedId , Plan plan, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.quotedId = quotedId;
        this.plan = plan;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("cotizacion_id", quotedId);
            json.put("cotizacion_elegida_id", plan.idCotizacion);

            Log.e(TAG, "JSON: " + json.toString());

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

            if(dic!=null){
                //TODO Ver que devuelve el Web Service
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }


}
