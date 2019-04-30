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

public class HGetSearchDateroRequest extends HRequest<ArrayList<QuoteOption>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_SEARCH_DATERO;

    public HGetSearchDateroRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + "texto=" + query + "&maxResults=10" , listener, errorListener);
    }

    @Override
    protected ArrayList<QuoteOption> parseObject(Object obj) {
        JSONArray jsonArray = (JSONArray) obj;
        try {
            return ParserUtils.parseQuestionOptions(jsonArray,"id","nombre","numero");

        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }
}
