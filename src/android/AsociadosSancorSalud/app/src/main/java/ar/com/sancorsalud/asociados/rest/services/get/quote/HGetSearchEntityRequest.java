package ar.com.sancorsalud.asociados.rest.services.get.quote;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 2/23/17.
 */

public class HGetSearchEntityRequest extends HRequest<ArrayList<QuoteOption>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_SEARCH_ENTITY;

    public HGetSearchEntityRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH+query , listener, errorListener);
    }

    @Override
    protected ArrayList<QuoteOption> parseObject(Object obj) {
        JSONObject data = ParserUtils.parseResponse((JSONObject) obj);
        try {
            JSONArray jsonArray = data.getJSONArray("entidades");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
