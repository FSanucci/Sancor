package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class HPostCardEntidadEmpleadoraSaveRequest extends HRequest<Long> {

    private static final String TAG = "HPOST_EE";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_ADD_ENTIDAD_EMPLEADORA;

    private EntidadEmpleadora ee ;

    public HPostCardEntidadEmpleadoraSaveRequest(EntidadEmpleadora ee, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.ee = ee;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", (ee.id != -1L ? ee.id : JSONObject.NULL));
            json.put("ficha_id", (ee.cardId != -1L ? ee.cardId : JSONObject.NULL));
            json.put("empresa_id", (ee.empresaId != -1L ? ee.empresaId : JSONObject.NULL));
            json.put("ficha_persona_id", (ee.personCardId != -1L ? ee.personCardId : JSONObject.NULL));
            json.put("cuit", (ee.cuit != null && !ee.cuit.trim().isEmpty()) ? Long.valueOf(ee.cuit.trim()) : JSONObject.NULL);
            json.put("razon_social", (ee.razonSocial != null && !ee.razonSocial.isEmpty()) ? ee.razonSocial.trim() : "");

            if (ee.inputDate != null) {
                json.put("fecha_ingreso", ee.getInputDate());
            }

            json.put("remuneracion", ee.remuneracion);
            json.put("titular", ee.isTitular? true: false);

            // PHONES
            JSONArray jPhonesArray = new JSONArray();

            if (ee.areaPhone!= null && !ee.areaPhone.isEmpty() &&  ee.phone != null &&  ! ee.phone.isEmpty()) {
                JSONObject jPhone = new JSONObject();
                jPhone.put("caracteristica", ee.areaPhone);
                jPhone.put("numero", ee.phone);
                jPhonesArray.put(jPhone);
            }
            json.put("lista_telefonos", jPhonesArray);


            // ADD FILES
            JSONArray jRecibosArray = new JSONArray();
            int i = 0;
            if (ee.reciboSueldoFiles.size() > 0) {
                for (AttachFile reciboFile : ee.reciboSueldoFiles) {
                    JSONObject jRecibo = new JSONObject();

                    jRecibo.put("archivo_id", reciboFile.id);
                    jRecibo.put("comentario", "recibo_" + i++);
                    jRecibosArray.put(jRecibo);
                }
            }

            json.put("recibos_sueldo", jRecibosArray);

            Log.e(TAG, "JSON ENTIDAD EMP: "  +  json.toString());
            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG ,  "Error filling Entidad Empleadora...");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Long parseObject(Object obj) {
        Long empId = -1L;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    Log.e(TAG, "SAVE ENTIDAD EMPLEADORA RESPONSE: " + dic.toString());
                    empId = dic.optLong("empresa_id", -1L);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return empId;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
