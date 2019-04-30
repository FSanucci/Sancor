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


public class PriorityOptionsController extends BaseQuoteOptionsController<QuoteOption> {

    public String getObjectKey(){
        return "priority";
    }

    public String getLastUpdateKey(){
        return "priority_last_update";
    }

    public String getLocalJsonName(){
        return "priority.json";
    }

    public ArrayList<QuoteOption> parseData(String json){
        try {
            JSONObject jsonObj = new JSONObject(json);
            return ParserUtils.parsePriority(jsonObj);
        }catch (JSONException e) {
            return null;
        }
    }

    protected Type typeToken(){
        return new TypeToken<ArrayList<QuoteOption>>() {
        }.getType();
    }


    public HRequest createService(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener){
        return null;
    }


    protected boolean shouldUpdateList(){
        return false;
    }

}
