package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.model.affiliation.DesValidation;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_TIME_FORMAT;


public class HGetValidateDESRequest extends HRequest<DesValidation> {

    private static final String TAG = "DES_VALID_REQ";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_DES_VALIDATION;

    public HGetValidateDESRequest(long desId, Response.Listener<DesValidation> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + desId, listener, errorListener);
    }

    @Override
    protected DesValidation parseObject(Object obj) {

        DesValidation desValidation = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                desValidation = new DesValidation();
                try {

                    Log.e(TAG, "DES VALIDATION RESP: " + dic.toString());
                    desValidation.cardId = dic.optLong("fichaId", -1L);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return desValidation;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}