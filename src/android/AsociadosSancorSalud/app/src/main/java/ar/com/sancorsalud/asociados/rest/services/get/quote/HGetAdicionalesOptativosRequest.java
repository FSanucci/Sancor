package ar.com.sancorsalud.asociados.rest.services.get.quote;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HGetAdicionalesOptativosRequest extends HRequest<AdicionalesOptativosData> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_ADICIONALES_OPTATIVOS;
    private QuoteOption mSegmento;


    public HGetAdicionalesOptativosRequest(QuoteOption segmento,  Response.Listener<AdicionalesOptativosData> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH, listener, errorListener);
        this.mSegmento = segmento;
    }

    @Override
    protected AdicionalesOptativosData parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);

            if(dic!=null){
                try {
                    AdicionalesOptativosData  optativosData = null;
                    JSONArray opcionales = dic.getJSONArray("opcionales");

                    if(opcionales!=null) {
                        optativosData =  new AdicionalesOptativosData();

                        for (int i = 0; i < opcionales.length(); i++) {
                            JSONObject tipoOpcion = opcionales.optJSONObject(i);

                            AdicionalesOptativosData.TipoOpcion to = optativosData.new TipoOpcion();
                            to.tipo = ParserUtils.optString(tipoOpcion, "tipo");
                            to.titulo = ParserUtils.optString(tipoOpcion, "titulo");

                            try {
                                JSONArray opciones = tipoOpcion.getJSONArray("opciones");
                                for (int j = 0; j < opciones.length(); j++) {
                                    JSONObject opcional = opciones.optJSONObject(j);

                                    // filter data by incoming segment id
                                    long segmentoId = opcional.optLong("segmento_id", -1L);
                                    if (segmentoId == Long.valueOf(mSegmento.id)) {

                                        AdicionalesOptativosData.OpcionalData od = optativosData.new OpcionalData();

                                        od.segmentoId = segmentoId;
                                        od.codigo = ParserUtils.optString(opcional, "codigo");
                                        od.descripcionPlan = ParserUtils.optString(opcional, "descripcion_plan");
                                        ;
                                        od.valor = opcional.optLong("valor", -1L);
                                        od.productoId = opcional.optLong("producto_id", -1L);
                                        od.obligatorio = ParserUtils.optString(opcional, "obligatorio");
                                        od.planId = opcional.optLong("plan_id", -1L);

                                        to.opciones.add(od);
                                    }
                                }
                            } catch (JSONException jEx) {
                            }

                            optativosData.tipoOpcionList.add(to);
                        }
                    }
                    return optativosData;

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
