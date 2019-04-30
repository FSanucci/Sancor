package ar.com.sancorsalud.asociados.rest.services.get.quote;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 2/22/17.
 */

public class HGetBancosRequest extends HRequest<ArrayList<QuoteOption>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_BANCOS;

    public HGetBancosRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH , listener, errorListener);
    }

    @Override
    protected ArrayList<QuoteOption> parseObject(Object obj) {
        return ParserUtils.parseBancos((JSONObject) obj);
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
