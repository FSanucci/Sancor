package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.model.affiliation.Document;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostDesToAuditoriaRequest extends HRequest<DesResult> {

    private static final String TAG = "HPOST_DES_AUDT";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_DES_AUDITORIA;

    private Des des;

    public HPostDesToAuditoriaRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.des = des;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("numero_des", (des.desNumber != -1L ? des.desNumber : JSONObject.NULL));
            json.put("mensaje", "Derivar Des a Auditoria");
            json.put("nro_documento_potencial", (des.clientDni != -1L ? des.clientDni : JSONObject.NULL));

            json.put("fichaId", (des.cardId != -1L ? des.cardId : JSONObject.NULL));
            json.put("patologia", des.hasPatologia ? "S" : "N");

            // DES files
            JSONArray jDesFilesArray = new JSONArray();
            for (AttachFile desFile : des.desFiles) {
                jDesFilesArray.put(desFile.id);
            }
            json.put("des_archivos", jDesFilesArray);


            // HEALTH FILE
            if (des.healthCertFiles.size() > 0) {
                json.put("buena_salud", des.healthCertFiles.get(0).id);
            } else {
                json.put("buena_salud", JSONObject.NULL);
            }


            // Attached Files
            JSONArray jDocumentsArray = new JSONArray();
            for (AttachFile attachFile : des.attachsFiles) {
                jDocumentsArray.put(attachFile.id);
            }
            json.put("adjuntos", jDocumentsArray);

            JSONArray jDetailsArray = new JSONArray();

            for (DesDetail detail : des.details) {

                if (detail.age < 18){
                    detail.imc = 0;
                }

                JSONObject jDetail = new JSONObject();

                jDetail.put("peso", (detail.weight != -1f  && detail.weight != 0f) ? detail.weight : JSONObject.NULL);
                jDetail.put("altura", (detail.height != -1f && detail.height != 0f) ? detail.height : JSONObject.NULL);
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

            json.put("capitas", jDetailsArray);

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
                desResult = new DesResult();
                try {

                    Log.e(TAG, "SEND AUDITORIA DES RESP: " + dic.toString());

                    // TODO NEW
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
