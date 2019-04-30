package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 2/23/17.
 */

public class HPostSearchLocationRequest extends HRequest<ArrayList<QuoteOption>> {

    private static final String PATH = RestConstants.WEB_HOST + RestConstants.POST_SEARCH_LOCALIDAD;
    private String mQuery;


    public HPostSearchLocationRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH , listener, errorListener);
        mQuery = query;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {

            Log.e ("TAG", "mQuery: " + mQuery);
            json.put("texto", mQuery);

            String body = json.toString();
            Log.e ("TAG", "body: " + body);
            return body.getBytes(Charset.forName("UTF-8"));

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected ArrayList<QuoteOption> parseObject(Object obj) {
        ArrayList<QuoteOption> array = null;

        if(obj instanceof JSONObject) {

            JSONObject dic = (JSONObject) obj;
            if(dic!=null){

               array = new ArrayList<>();
                try {

                    JSONArray jLocalizationArray = dic.getJSONArray("listaResultado");

                    Log.e ("TAG", "jLocalizationArray: " + jLocalizationArray.length());

                    for (int i = 0; i < jLocalizationArray.length(); i++) {
                        JSONObject jLocation = jLocalizationArray.optJSONObject(i);

                        QuoteOption q = new QuoteOption();
                        q.id = ParserUtils.optString(jLocation, "codigoPostal") != null ? ParserUtils.optString(jLocation, "codigoPostal").trim() : null;

                        String detalle =  ParserUtils.optString(jLocation, "detalle") != null ? ParserUtils.optString(jLocation, "detalle").trim() : "";
                        String codigoPostalReal =  ParserUtils.optString(jLocation, "codigoPostalReal") != null ? ParserUtils.optString(jLocation, "codigoPostalReal").trim() : "";
                        String provincia = "";

                        JSONObject jProvincia =   jLocation.getJSONObject("provincia");
                        if (jProvincia != null ){
                            provincia = ParserUtils.optString(jProvincia, "detalle") != null ? ParserUtils.optString(jProvincia, "detalle").trim() : "";
                        }

                        q.title = detalle + " - " + provincia + " - " + codigoPostalReal;
                        //q.title = ParserUtils.optString(jLocation, "detalle") != null ? ParserUtils.optString(jLocation, "detalle").trim() : null;

                        array.add(q);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e ("TAG", "error: " + e.getMessage());
                }
            }
        }
        return array;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
       // return ParserUtils.parseError((JSONObject) obj);
        return null;
    }

}
