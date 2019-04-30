package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HPostSaveQuotationRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.POST_SAVE_QUOTATION;
    private long mCotizacionId;
    private List<Long> mPlanesCotizIdList;


    public HPostSaveQuotationRequest(long cotizacionId , List<Long> planesCotizIdList, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        mCotizacionId = cotizacionId;
        mPlanesCotizIdList = planesCotizIdList;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("cotizacion_id", mCotizacionId);

            JSONArray jplanesCotizIArray = new JSONArray();
            for (Long id : mPlanesCotizIdList){
                jplanesCotizIArray.put(id);
            }
            json.put("cotizaciones_elegidas", jplanesCotizIArray);

            String body = json.toString();

            Log.e(TAG, "Save quotation: " + body);
            return body.getBytes(Charset.forName("UTF-8"));

        }catch(Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    protected Void parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);
            if(dic!=null){

            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
