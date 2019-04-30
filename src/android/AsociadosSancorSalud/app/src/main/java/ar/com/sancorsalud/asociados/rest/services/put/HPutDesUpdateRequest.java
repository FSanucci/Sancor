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
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by francisco on 2/1/18.
 */


public class HPutDesUpdateRequest extends HRequest<DesResult> {

    private static final String TAG = "HPOST_SAVE_DES";
    private static final String PATH = RestConstants.HOST + RestConstants.PUT_DES_UPDATE;

    private Des des;

    public HPutDesUpdateRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, PATH, listener, errorListener);

        this.des = des;
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
            json.put("id", (des.id != -1L ? des.id : JSONObject.NULL));
            json.put("ficha_id", (des.cardId != -1L ? des.cardId : JSONObject.NULL));
            json.put("cotizacion_id", (des.cotizacionId != -1L ? des.cotizacionId : JSONObject.NULL));
            json.put("comentarios", (des.comments != null ? des.comments.trim() : JSONObject.NULL));
            json.put("numero_solicitud", (des.requestNumber != -1L ? des.requestNumber : JSONObject.NULL));
            json.put("medico_auditor_id", (des.doctorId != -1L ? des.doctorId : JSONObject.NULL));
            json.put("nro_documento_potencial", (des.clientDni != -1L ? des.clientDni : JSONObject.NULL));
            json.put("patologia", des.hasPatologia ? "S" : "N");

            // HEALTH FILE
            if (des.healthCertFiles.size() > 0) {
                json.put("buena_salud", des.healthCertFiles.get(0).id);
            } else {
                json.put("buena_salud", JSONObject.NULL);
            }

            // DES files
            JSONArray jDesFilesArray = new JSONArray();
            for (AttachFile desFile : des.desFiles) {
                jDesFilesArray.put(desFile.id);
            }
            json.put("archivos", jDesFilesArray);


            // ANEXO
            if (des.anexoFiles.size() > 0) {
                json.put("anexo3_id", des.anexoFiles.get(0).id);
            } else {
                json.put("anexo3_id", JSONObject.NULL);
            }

            // Attached Files
            JSONArray jDocumentsArray = new JSONArray();
            for (int i = 0; i < des.attachsFiles.size(); i++) {

                JSONObject jDocument = new JSONObject();
                jDocument.put("archivo_id", des.attachsFiles.get(i).id);
                jDocument.put("comentarios", "adjunto_" + i);
                jDocumentsArray.put(jDocument);
            }
            json.put("documentos", jDocumentsArray);


            // DETAILS
            JSONArray jDetailsArray = new JSONArray();
            for (DesDetail detail : des.details) {

                JSONObject jDetail = new JSONObject();

                jDetail.put("peso", detail.weight != -1f ? detail.weight : 0);
                jDetail.put("altura", detail.height != -1f ? detail.height : 0);
                jDetail.put("indiceMasaCorporal", (detail.imc != -1)? detail.imc : 0);
                jDetail.put("modulo", 0);
                jDetail.put("cantidad_cuotas", 1);
                jDetail.put("descripcion_patologia", detail.descPatologia != null ? detail.descPatologia.trim() : "");
                jDetail.put("parentesco_id", detail.parentesco.id != null ? Long.valueOf(detail.parentesco.id.trim()) : JSONObject.NULL);
                jDetail.put("edad", detail.age);


                // if  detail.cardPeopleId  == -1 will create one, this when loading DES form Quot. Parameters
                jDetail.put("ficha_persona_id", (detail.cardPeopleId != -1L ? detail.cardPeopleId : JSONObject.NULL));
                jDetail.put("nombre", detail.firstname != null ? detail.firstname.trim() : "");
                jDetail.put("apellido", detail.lastname != null ? detail.lastname.trim() : "");
                jDetail.put("tipo_documento_id", detail.docTypeID != -1L ? detail.docTypeID : JSONObject.NULL);

                jDetailsArray.put(jDetail);

            }
            json.put("detalles", jDetailsArray);
            Log.e(TAG, "JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected DesResult parseObject(Object obj) {

        DesResult desResult = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    desResult = new DesResult();

                    Log.e(TAG, "UPDATE DES RESP: " + dic.toString());
                    desResult.desId = dic.optLong("des_id", -1L);
                    desResult.cardId = dic.optLong("ficha_id", -1L);

                    // This generate desId, and cardId
                    // {"des_id":1776,"ficha_id":1962}

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return desResult;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}