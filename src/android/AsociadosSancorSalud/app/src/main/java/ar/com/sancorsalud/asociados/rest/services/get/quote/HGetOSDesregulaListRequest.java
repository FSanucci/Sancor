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


public class HGetOSDesregulaListRequest extends HRequest<ArrayList<QuoteOption>> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_OSDESREGULA + "?segmento=";

    public HGetOSDesregulaListRequest(int segmento, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener){
        super(Method.GET, PATH + segmento, listener, errorListener);
    }

    @Override
    protected ArrayList<QuoteOption> parseObject(Object obj) {
        ArrayList<QuoteOption> quotationOptionsList = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    JSONArray jsonArray = dic.getJSONArray("os_desregula");
                    quotationOptionsList = new ArrayList<>();
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject element = jsonArray.optJSONObject(i);

                        int id = Integer.parseInt(ParserUtils.optString(element, "id"));
                        String descripcion = ParserUtils.optString(element, "descripcion");
                        String tipo = ParserUtils.optString(element, "tipo");

                        QuoteOption quote = new QuoteOption(String.valueOf(id), descripcion, tipo);
                        quotationOptionsList.add(quote);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return quotationOptionsList;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
