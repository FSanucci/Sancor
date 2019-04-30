package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.CloseReasons;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HGetCloseReasonsRequest extends HRequest<ArrayList<CloseReasons>> {
    private static final String PATH = RestConstants.HOST + RestConstants.GET_CLOSE_REASONS;

    public HGetCloseReasonsRequest(Response.Listener<ArrayList<CloseReasons>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<CloseReasons> parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                try {
                    JSONArray reasons = dic.getJSONArray("motivos");
                    if(reasons!=null) {
                        ArrayList<CloseReasons> list = new ArrayList<>();
                        for (int i = 0; i < reasons.length(); i++) {

                                JSONObject reason = reasons.optJSONObject(i);
                                CloseReasons r = new CloseReasons();
                                r.reasonId = reason.optLong("id",-1);
                                r.reasonDescription = ParserUtils.optString(reason,"motivo");
                                if(r.reasonId!=-1 && r.reasonDescription!=null)
                                    list.add(r);
                        }
                        return list;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
