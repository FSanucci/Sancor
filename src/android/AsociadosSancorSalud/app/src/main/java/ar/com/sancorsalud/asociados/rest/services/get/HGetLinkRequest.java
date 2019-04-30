package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Response;

import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HGetLinkRequest extends HRequest<LinkData> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_LINK_DATA;

    public HGetLinkRequest(String linkId, Response.Listener<LinkData> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + linkId , listener, errorListener);
    }

    @Override
    protected LinkData parseObject(Object obj) {
        LinkData linkData = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {
                linkData = new LinkData();
                linkData.description = ParserUtils.optString(dic, "descripcion");
                linkData.link = ParserUtils.optString(dic, "link");
            }
        }
        return linkData;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}
