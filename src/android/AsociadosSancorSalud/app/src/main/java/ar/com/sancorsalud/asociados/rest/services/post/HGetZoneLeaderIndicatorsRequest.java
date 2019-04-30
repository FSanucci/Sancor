package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.ZoneLeaderIndicator;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


/**
 * Created by sergiocirasa on 4/10/17.
 */

public class HGetZoneLeaderIndicatorsRequest extends HRequest<ZoneLeaderIndicator> {

    private static final String TAG = "HPOST_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_ZONELEADER_INDICATORS;

    private long mUserId;
    private String mFromDate;
    private String mToDate;

    public HGetZoneLeaderIndicatorsRequest(long userId, String fromDate, String toDate, Response.Listener<ZoneLeaderIndicator> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH + ((userId != -1L) ? "idUsuario=" + userId : "") + "&fechaDesde=" + fromDate + "&fechaHasta=" + toDate, listener, errorListener);
        this.mUserId = userId;
        this.mFromDate = fromDate;
        this.mToDate = toDate;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("usuario_id", mUserId);
            json.put("fecha_desde", mFromDate);
            json.put("fecha_hasta", mToDate);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ZoneLeaderIndicator parseObject(Object obj) {

        ZoneLeaderIndicator indicador = null;

        if (obj instanceof JSONArray) {
            JSONArray indicators = (JSONArray) obj;
            if (indicators.length() > 0) {
                JSONObject dic = indicators.optJSONObject(0);
                try {

                    indicador = new ZoneLeaderIndicator();

                    JSONObject tiempoPromedio = dic.getJSONObject("tiempoPromedio");

                    indicador.tiempo_promedio = tiempoPromedio.optInt("hour", -1);
                    indicador.pa_sin_agendar = dic.optInt("cantidadSinAgendar", -1);
                    indicador.pa_agendados = dic.optInt("cantidadAgendado", -1);
                    indicador.pa_cotizados = dic.optInt("cantidadCotizado", -1);
                    indicador.pa_cerrados = dic.optInt("cantidadCerrado", -1);

                    indicador.tasa_cotizaciones_manual = dic.optInt("tasa_cotizaciones_manual", -1);

                    indicador.carga_produccion = dic.optInt("carga_produccion", -1);
                    indicador.cantidad_capitas = dic.optInt("cantidadCapitas", -1);
                    indicador.carga_tasa_errores = dic.optInt("tasaErrores", -1);
                    indicador.carga_tiempo_promedio = dic.optInt("diasCargaPromedio", -1);
                    indicador.carga_grav = dic.optInt("cantidadGrav", -1);
                    indicador.carga_nograv = dic.optInt("cantidadNoGrav", -1);
                    indicador.carga_empresa = ParserUtils.optString(dic, "cantidadEmpresa");
                    indicador.carga_afinidad = ParserUtils.optString(dic, "cantidadAfinidad");
                    indicador.carga_individual = ParserUtils.optString(dic, "cantidadIndividual");
                    indicador.pago_tc = dic.optInt("cantidadTc", -1);
                    indicador.pago_cbu = dic.optInt("cantidadCbu", -1);
                    indicador.pago_pgf = dic.optInt("cantidadPagoFacil", -1);
                    indicador.pago_rce = dic.optInt("cantidadReciboCobroEntidad", -1);
                    indicador.pago_ef = dic.optInt("cantidadEfectivo", -1);

                    indicador.presentacion_fichas = dic.optInt("tasaPresentacion", -1);
                    indicador.presentacion_errores = dic.optInt("tasaDocumentosConErrores", -1);

                    //fichasEnControl, fichasEnCorreccion, fichasEnProceso y cantidadCotizado

                    indicador.fichas_en_control = dic.optInt("fichasEnControl",-1);
                    indicador.fichas_en_correccion = dic.optInt("fichasEnCorreccion",-1);
                    indicador.fichas_en_proceso = dic.optInt("fichasEnProceso",-1);
                    indicador.cantidad_cotizado = dic.optInt("cantidadCotizado",-1);


                } catch (Exception e) {
                    e.printStackTrace();
                    indicador = null;
                }

            }
        }


        Log.e(TAG, "PArseo de FIcha !!!! .................");
        return indicador;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }

}

