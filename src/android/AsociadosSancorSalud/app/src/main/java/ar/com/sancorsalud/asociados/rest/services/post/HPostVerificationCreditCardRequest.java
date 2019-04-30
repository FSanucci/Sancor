package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardComment;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by luciano on 8/29/17.
 */

public class HPostVerificationCreditCardRequest extends HRequest<Boolean> {

    private static final String TAG = "HPOST_VERIF_CREDIT_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFY_CREDIT_CARD;

    private long creditCardId;
    private String fechaCardVencimiento;
    private String fechaIniServ;

    public HPostVerificationCreditCardRequest(long creditCardId, String fechaCardVencimiento, String fechaIniServ, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.creditCardId = creditCardId;
        this.fechaCardVencimiento = fechaCardVencimiento;
        this.fechaIniServ = fechaIniServ;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("tarjeta_id", creditCardId);
            json.put("fecha_vencimiento_tarjeta", fechaCardVencimiento);
            json.put("fecha_ingreso_salud", fechaIniServ);

            Log.e(TAG, "HPOST_VERIF_CARD SEND JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Boolean parseObject(Object obj) {

       boolean response = true;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    Log.e(TAG, "RESULT VERIF_CREDIT_CARD::: " + dic.toString());
                    response = dic.optBoolean("tarjeta_valida");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "ERROR PARSING VERIF_CREDIT_CARD : " + e.getMessage());
                    response = false;
                }
            }
        }

        Log.e(TAG, "PArseo Verif Credit Card!!!! .................");
        return response;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

