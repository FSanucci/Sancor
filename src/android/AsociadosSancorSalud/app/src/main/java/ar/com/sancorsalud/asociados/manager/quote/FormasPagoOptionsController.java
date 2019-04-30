package ar.com.sancorsalud.asociados.manager.quote;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;

/**
 * Created by sergio on 2/22/17.
 */

public class FormasPagoOptionsController extends BaseQuoteOptionsController<QuoteOption> {

    public String getObjectKey() {
        return "formas_pago";
    }

    public String getLastUpdateKey() {
        return "formas_pago_last_update";
    }

    public String getLocalJsonName() {
        return "formas_pago.json";
    }

    public ArrayList<QuoteOption> parseData(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);

            return ParserUtils.parseFormasPago(jsonObj);
        } catch (JSONException e) {
            return null;
        }
    }

    protected Type typeToken() {
        return new TypeToken<ArrayList<QuoteOption>>() {
        }.getType();
    }

    public HRequest createService(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return RestApiServices.createGetFormasPagoRequest(null, listener, errorListener);
    }

}
