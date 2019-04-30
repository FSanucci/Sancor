package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardComment;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by luciano on 8/29/17.
 */

public class HPostCardInfoRequest extends HRequest<AffiliationCardInfo> {

    private static final String TAG = "HPOST_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_CARD_GET;

    private long affiliationCardId;

    public HPostCardInfoRequest(long affiliationCardId, Response.Listener<AffiliationCardInfo> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.affiliationCardId = affiliationCardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", affiliationCardId);
            Log.e(TAG, "SEND JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected AffiliationCardInfo parseObject(Object obj) {

        AffiliationCardInfo card = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    Log.e(TAG, "RESULT GET CARD INFO!::: " + dic.toString());
                    card = new AffiliationCardInfo();

                    JSONObject jCard = dic.optJSONObject("ficha");
                    card.id = jCard.optLong("id", -1);
                    card.idCotizacion = jCard.optLong("cotizacion_id", -1);

                    String segmentoId = ParserUtils.optString(jCard, "segmento_id") != null ? ParserUtils.optString(jCard, "segmento_id").trim() : null;
                    if (segmentoId != null) {
                        card.segmento = new QuoteOption(segmentoId, QuoteOptionsController.getInstance().getSegmentoName(segmentoId));
                    }

                    String formaIngresoId = ParserUtils.optString(jCard, "formadeingreso_id") != null ? ParserUtils.optString(jCard, "formadeingreso_id").trim() : null;
                    if (formaIngresoId != null) {
                        card.formaIngreso = new QuoteOption(formaIngresoId, QuoteOptionsController.getInstance().getFormaIngresoName(formaIngresoId));
                    }

                    String fechaCarga = ParserUtils.optString(jCard, "fecha_carga") != null ? ParserUtils.optString(jCard, "fecha_carga").trim() : null;
                    if (fechaCarga != null) {
                        card.fechaCarga = ParserUtils.parseDate(fechaCarga, "yyyy-MM-dd");
                    }

                    String fechaInicioServicio = ParserUtils.optString(jCard, "fecha_inicio_servicio") != null ? ParserUtils.optString(jCard, "fecha_inicio_servicio").trim() : null;
                    if (fechaInicioServicio != null) {
                        card.fechaInicioServicio = ParserUtils.parseDate(fechaInicioServicio, "yyyy-MM-dd");
                    }


                    try {
                        JSONArray jsonArray = jCard.optJSONArray("comentarios");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            ArrayList<AffiliationCardComment> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                AffiliationCardComment comment = new AffiliationCardComment();
                                comment.id = data.optLong("id", -1);
                                comment.description = ParserUtils.optString(data, "comentarios");
                                comment.userId = data.optLong("id_usuario", -1);
                                comment.postDate = ParserUtils.parseDate(data, "fecha_carga", "yyyy-MM-dd");
                                list.add(comment);
                            }
                            card.comments = list;
                        }
                    } catch (JSONException jEx) {
                    }

                    // PERSONAS
                    try {
                        JSONArray jPersonasArray = jCard.getJSONArray("personas");
                        if (jPersonasArray != null) {
                            card.cantMembers = jPersonasArray.length();
                        }
                    } catch (JSONException jEx) {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "ERROR PARSING CARD INFO : " + e.getMessage());
                    card = null;
                }
            }
        }


        Log.e(TAG, "PArseo de FIcha !!!! .................");
        return card;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

