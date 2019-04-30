package ar.com.sancorsalud.asociados.rest.services.post;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.SalesmanIndicator;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 4/10/17.
 */

public class HGetSalesmanIndicatorsRequest extends HRequest<SalesmanIndicator> {

    private static final String TAG = "HPOST_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_SALESMAN_INDICATOR;

    //  https://servicios.sancorsalud.com.ar/asociados/api/ServicioIndicadoresPromotor?fechaDesde=&fechaHasta=

    // https://servicios.sancorsalud.com.ar/asociados/api/ServicioIndicadoresReferente?idUsuario=&fechaDesde=&fechaHasta=



    private long mUserId;
    private String mFromDate;
    private String mToDate;

    public HGetSalesmanIndicatorsRequest(long userId, String fromDate, String toDate, Response.Listener<SalesmanIndicator> listener, Response.ErrorListener errorListener) {
        super(Method.GET, PATH + "fechaDesde=" + fromDate + "&fechaHasta=" + toDate, listener, errorListener);
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
    protected SalesmanIndicator parseObject(Object obj) {

        SalesmanIndicator indicador = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) obj;

            if (dic != null) {
                try {

                    indicador = new SalesmanIndicator();

                    indicador.cantidadFichas = dic.optInt("cantidadFichasAprobadas", -1);
                    indicador.produccion = ParserUtils.optString(dic, "produccion");
                    indicador.cantidadGrav = dic.optInt("cantidadCapitasGrav", -1);
                    indicador.cantidadNograv = dic.optInt("cantidadCapitasNoGrav", -1);

                } catch (Exception e) {
                    e.printStackTrace();
                    indicador = null;
                }
            }
        }


        return indicador;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return null;
    }

}

