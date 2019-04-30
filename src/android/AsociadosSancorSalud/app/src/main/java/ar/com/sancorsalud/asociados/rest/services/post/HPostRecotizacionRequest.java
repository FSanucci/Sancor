package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.affiliation.Recotizacion;
import ar.com.sancorsalud.asociados.model.affiliation.RecotizacionDetalle;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 7/9/17.
 */

public class HPostRecotizacionRequest extends HRequest<Recotizacion> {

    private static final String TAG = "HGetRecotizacion";

    private static final String PATH = RestConstants.HOST + RestConstants.POST_REQUOTATION;
    private long mFichaId;
    private Date mDate;


    public HPostRecotizacionRequest(long fichaId, Date date, Response.Listener<Recotizacion> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mFichaId = fichaId;
        this.mDate = date;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {

            json.put("ficha_id", mFichaId);
            json.put("fecha_alta", ParserUtils.parseDate(mDate,"yyyy-MM-dd"));
            String body = json.toString();

            Log.e (TAG, "RECOTIZACION: "  + body);

            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            Log.e(TAG, "Error send Quotation check parameters of Quotation ...... " + (e.getMessage() != null ? e.getMessage() : ""));
            e.printStackTrace();
            return null;
        }
    }



    @Override
    protected Recotizacion parseObject(Object obj) {

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                dic = dic.optJSONObject("cotizacion");

                if(dic !=null){

                    Log.e(TAG, "RECOTIZACION RESP: "  + dic.toString());


                    JSONArray array = dic.optJSONArray("planesCotizados");
                    if(array!=null && array.length() > 0){
                        dic = array.optJSONObject(0);

                        Recotizacion coti = new Recotizacion();
                        coti.idCotizacion = dic.optInt("idCotizacion");
                        coti.idGrilla = dic.optInt("idGrilla");
                        coti.idPlan = ParserUtils.optString(dic,"idPlan");
                        coti.descripcionPlan = ParserUtils.optString(dic,"descripcionPlan");
                        coti.idProducto = dic.optInt("idProducto");
                        coti.descripcionProducto = ParserUtils.optString(dic,"descripcionProducto");
                        coti.idConcepto = dic.optInt("idConcepto");
                        coti.descripcionConcepto = ParserUtils.optString(dic,"descripcionConcepto");
                        coti.valor = dic.optInt("valor");
                        coti.diferencia_a_pagar = dic.optDouble("diferencia_a_pagar");

                        array = dic.optJSONArray("detalle");
                        if(array != null){
                            coti.detalle = new ArrayList<>();

                            for(int i=0; i<array.length();i++){
                                JSONObject obj1 = array.optJSONObject(i);
                                RecotizacionDetalle detail = new RecotizacionDetalle();

                                detail.idCotizacion = obj1.optInt("idCotizacion");
                                detail.idGrilla = obj1.optInt("idGrilla");
                                detail.idPlan = ParserUtils.optString(obj1,"idPlan");
                                detail.descripcionPlan = ParserUtils.optString(obj1,"descripcionPlan");
                                detail.idProducto = obj1.optInt("idProducto");
                                detail.descripcionProducto = ParserUtils.optString(obj1,"descripcionProducto");
                                detail.idConcepto = obj1.optInt("idConcepto");
                                detail.descripcionConcepto = ParserUtils.optString(obj1,"descripcionConcepto");
                                detail.valor = obj1.optInt("valor");
                                detail.diferencia_a_pagar = obj1.optDouble("diferencia_a_pagar");
                                coti.detalle.add(detail);
                            }
                        }

                        return coti;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }



}
