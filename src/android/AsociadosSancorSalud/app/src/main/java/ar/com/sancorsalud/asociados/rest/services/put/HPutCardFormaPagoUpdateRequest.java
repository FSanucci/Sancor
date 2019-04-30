package ar.com.sancorsalud.asociados.rest.services.put;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class HPutCardFormaPagoUpdateRequest extends HRequest<Pago> {
    private static final String TAG = "HPUT_FORMA_PAGO";
    private static final String PATH = RestConstants.HOST + RestConstants.PUT_FORMA_PAGO_UPDATE;

    private Pago pago;

    public HPutCardFormaPagoUpdateRequest(Pago pago, Response.Listener<Pago> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, PATH, listener, errorListener);
        this.pago = pago;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (HRequest.authorizationHeaderValue != null)
            headers.put("token", HRequest.authorizationHeaderValue);
        return headers;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", (pago.id != -1L ? pago.id : JSONObject.NULL));
            json.put("ficha_id", (pago.cardId != -1L ? pago.cardId : JSONObject.NULL));
            json.put("forma_pago_id", (pago.formaPago != null && pago.formaPago.id != null && !pago.formaPago.id.isEmpty()) ? pago.formaPago.id.trim() : JSONObject.NULL);

            // TODO nuevo  C(Copago) or S (Salud)
            //json.put("tipo", (pago.type != null && !pago.type.isEmpty()) ? pago.type.trim() : JSONObject.NULL);
            json.put("salud_o_copago", (pago.type != null && !pago.type.isEmpty()) ? pago.type.trim() : JSONObject.NULL);

            if (pago.formaPago != null && pago.formaPago.id != null && !pago.formaPago.id.isEmpty()) {

                if (pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {

                    json.put("tarjeta_id", (pago.cardType != null && pago.cardType.id != null) ? Long.valueOf(pago.cardType.id.trim()) : JSONObject.NULL);
                    json.put("numero_tarjeta", (pago.cardPagoNumber != null && !pago.cardPagoNumber.isEmpty()) ? Long.valueOf(pago.cardPagoNumber.trim()) : JSONObject.NULL);
                    json.put("banco_emisor_id", (pago.bankEmiter != null && pago.bankEmiter.id != null) ? Long.valueOf(pago.bankEmiter.id.trim()) : JSONObject.NULL);
                    json.put("fecha_vencimiento", pago.validityDate != null ? ParserUtils.parseDate(pago.validityDate, DATE_FORMAT) : JSONObject.NULL);
                    json.put("titular_tarjeta_afiliacion", pago.titularCardAsAffiliation);

                    if (pago.creditCardFiles.size() > 0) {
                        JSONArray jCreditFilesArray = new JSONArray();
                        for (AttachFile creditFile : pago.creditCardFiles) {
                            jCreditFilesArray.put(creditFile.id);
                        }
                        json.put("archivosTarjetaId", jCreditFilesArray);
                    }

                    if (pago.constanciaCardFiles.size() > 0) {
                        JSONArray jConstanciaFilesArray = new JSONArray();
                        for (AttachFile constanciaFile : pago.constanciaCardFiles) {
                            jConstanciaFilesArray.put(constanciaFile.id);
                        }
                        json.put("archivosConstanciaId", jConstanciaFilesArray);
                    }

                    if (pago.comprobanteCBUFiles.size() > 0){
                        JSONArray jComprobanteCBUFilesArray = new JSONArray();
                        for (AttachFile comprobanteCBUFile : pago.comprobanteCBUFiles) {
                            JSONObject object = new JSONObject();
                            object.put("archivo_id",comprobanteCBUFile.id);
                            object.put("descripcion","CONSTANCIA_CBU");
                            jComprobanteCBUFilesArray.put(comprobanteCBUFile.id);
                        }
                        json.put("archivos", jComprobanteCBUFilesArray);
                    }
                }

                // CBU / REINTEGROS
                // When Forma pago is CBU then cbu is the number of 22 digit, otherwise  cbu filed load reintegros that act as cbu number
                json.put("cbu", (pago.cbu != null && !pago.cbu.isEmpty()) ? pago.cbu.trim() : JSONObject.NULL);
                json.put("banco_id", (pago.bankCBU != null && pago.bankCBU.id != null) ? Long.valueOf(pago.bankCBU.id.trim()) : JSONObject.NULL);
                json.put("cuenta_tipo_id", (pago.accountType != null && pago.accountType.id != null) ? Long.valueOf(pago.accountType.id.trim()) : JSONObject.NULL);


                if (pago.constanciaCBUFiles.size() > 0) {
                    JSONArray jConstanciaCBUFilesArray = new JSONArray();
                    for (AttachFile constanciaCBUFile : pago.constanciaCBUFiles) {
                        jConstanciaCBUFilesArray.put(constanciaCBUFile.id);
                    }
                    json.put("archivosReintegroId", jConstanciaCBUFilesArray);
                }

                if (pago.comprobanteCBUFiles.size() > 0){
                    JSONArray jComprobanteCBUFilesArray = new JSONArray();
                    for (AttachFile comprobanteCBUFile : pago.comprobanteCBUFiles) {
                        JSONObject object = new JSONObject();
                        object.put("archivo_id",comprobanteCBUFile.id);
                        object.put("descripcion","CONSTANCIA_CBU");
                        jComprobanteCBUFilesArray.put(object);
                    }
                    json.put("archivos", jComprobanteCBUFilesArray);
                }


                json.put("es_titular", pago.titularMainCbuAsAffiliation);
                json.put("cuenta_cuil_titular", (pago.accountCuil != null && !pago.accountCuil.isEmpty()) ? pago.accountCuil.trim() : JSONObject.NULL);

                if (!pago.titularMainCbuAsAffiliation){
                    json.put("cuenta_nombre_titular", (pago.accountFirstName != null && !pago.accountFirstName.isEmpty()) ? pago.accountFirstName.trim() : JSONObject.NULL);
                    json.put("cuenta_apellido_titular", (pago.accountLastName != null && !pago.accountLastName.isEmpty()) ? pago.accountLastName.trim() : JSONObject.NULL);
                }
            }

            // TODO  not in use
            //json.put("cuenta_numero", (pago.accountNumber != null && !pago.accountNumber.isEmpty()) ? pago.accountNumber.trim() : JSONObject.NULL);

            Log.e(TAG, "JSON UPDATE FORMA PAGO: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG, "Error filling forma pago...");

            e.printStackTrace();
            return null;
        }
    }




    @Override
    protected Pago parseObject(Object obj) {

        Pago pago = new Pago();

        Long fileId = -1L;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e(TAG, "UPDATE FORMA PAGO RESPONSE: " + dic.toString());

                    pago.id =  dic.optLong("id", -1L);
                    // TODO parse other data ...

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return pago;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}
