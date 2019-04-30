package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.affiliation.EEMorosidadData;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_TIME_FORMAT;

/**
 * Created by sergiocirasa on 29/3/17.
 */

public class HGetVerificationMorosidadRequest extends HRequest<EEMorosidadData> {

    private static final String TAG = "VERIF_MORO";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_VERIFICATION_MOROSIDAD;

    public HGetVerificationMorosidadRequest(long cuit, Response.Listener<EEMorosidadData> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + cuit, listener, errorListener);
    }


    @Override
    protected EEMorosidadData parseObject(Object obj) {

        EEMorosidadData result = null;

        Boolean hasMorosidad = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e(TAG, "VERIF_MOROSIDAD RESPONSE: " + dic.toString());

                    result = new EEMorosidadData();
                    boolean exist = dic.optBoolean("existe", false);
                    boolean morosa = dic.optBoolean("morosa", false);

                    result.hasMorosidad = exist;

                    JSONObject jEE = dic.optJSONObject("empresa");
                    if (jEE != null) {
                        result.eeId = jEE.optLong("empresa_id", -1L);
                        result.eeAreaPrefix = ParserUtils.optString(jEE, "empresa_prefijo") != null ? ParserUtils.optString(jEE, "empresa_prefijo").trim() : null;
                        result.eePhone = ParserUtils.optString(jEE, "empresa_telefono") != null ? ParserUtils.optString(jEE, "empresa_telefono").trim() : null;
                        result.eeDescription = ParserUtils.optString(jEE, "empresa_descripcion") != null ? ParserUtils.optString(jEE, "empresa_descripcion").trim() : null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());

                }
            }
        }
        return result;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}