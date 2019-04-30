package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class HPostCardObraSocialSaveRequest extends HRequest<Long> {

    private static final String TAG = "HPOST_OS";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_ADD_OBRA_SOCIAL;

    private ObraSocial obraSocial;

    public HPostCardObraSocialSaveRequest(ObraSocial obraSocial, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.obraSocial = obraSocial;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", (obraSocial.id != -1L ? obraSocial.id : JSONObject.NULL));
            json.put("ficha_id", (obraSocial.cardId != -1L ? obraSocial.cardId : JSONObject.NULL));
            json.put("ficha_persona_id", (obraSocial.personCardId != -1L ? obraSocial.personCardId : JSONObject.NULL));
            json.put("monotributo", obraSocial.isMonotributo);

            json.put("obra_social_desregula_id", (obraSocial.osQuotation != null && obraSocial.osQuotation.id != null) ? Long.valueOf(obraSocial.osQuotation.id.trim()) : 0);

            if (obraSocial.inputOSDate != null) {
                json.put("fecha_ingreso_obra_social", obraSocial.getOSInputDate());
            }

            // For DESREGULADO
            json.put("estado_id", (obraSocial.osState != null && obraSocial.osState.id != null) ? Long.valueOf(obraSocial.osState.id.trim()) : JSONObject.NULL);
            json.put("meses_impagos", obraSocial.mesesImpagos != -1 ? obraSocial.mesesImpagos : JSONObject.NULL);
            json.put("formulario_sss", obraSocial.osSSSFormNumber != -1L ? obraSocial.osSSSFormNumber : JSONObject.NULL);


            json.put("codigo_obra_social",  (obraSocial.osActual != null && obraSocial.osActual.id != null) ? Long.valueOf(obraSocial.osActual.id.trim()) : 0);

            if (obraSocial.empadronado != null){
                json.put("empadronado",  (obraSocial.empadronado) ? true: false);
            }

            /*
            if (obraSocial.hasMedicControl != null){
                json.put("cambio_centro_medico",  (obraSocial.hasMedicControl) ? true: false);
            }
            */

            // ADD FILES
            JSONArray jSSSFilesArray = new JSONArray();
            for (AttachFile sssFile : obraSocial.comprobantesSSSFiles) {
                jSSSFilesArray.put(sssFile.id);
            }
            json.put("comprobante_sss", jSSSFilesArray);


            JSONArray jAfipFilesArray = new JSONArray();
            for (AttachFile afipFile : obraSocial.comprobantesAfipFiles) {
                jAfipFilesArray.put(afipFile.id);
            }
            json.put("comprobante_afip", jAfipFilesArray);

            JSONArray jAttachsArray = new JSONArray();

            // ATACH FILES
            if (!obraSocial.osCodeFiles.isEmpty()) {
                addAttachFiles(jAttachsArray, obraSocial.osCodeFiles, ConstantsUtil.FILE_INDEX_OS_CODE);
            }

            // FOR OS check type
            if (!obraSocial.isMonotributo  && obraSocial.osQuotation != null) {
                String osType = obraSocial.osQuotation.extra;
                if (osType != null && !osType.isEmpty()) {
                    if (osType.equals(ConstantsUtil.OS_TYPE_SINDICAL)) {
                        fillSindicalFiles(jAttachsArray);
                    } else {
                        // OS DIRECCION
                        fillDirecctionFiles(jAttachsArray);
                    }
                }
            }
            else{ // FOR OS_MONOTRIBUTO Files are like sindical OS
                fillSindicalFiles(jAttachsArray);
            }

            json.put("adjuntos", jAttachsArray);

            Log.e(TAG, "JSON OS SAVE: " + json.toString());
            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            Log.e(TAG, "Error filling OS...");
            e.printStackTrace();
            return null;
        }
    }

    private void fillSindicalFiles(JSONArray jAttachsArray ){
        if (!obraSocial.optionChangeFiles.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.optionChangeFiles, ConstantsUtil.FILE_INDEX_OS_CHANGE_OPTION);
        }
        if (!obraSocial.formFiles.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.formFiles, ConstantsUtil.FILE_INDEX_OS_FORM);
        }
        if (!obraSocial.certChangeFiles.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.certChangeFiles, ConstantsUtil.FILE_INDEX_OS_CHANGE_CERT);
        }
    }

    private void fillDirecctionFiles(JSONArray jAttachsArray){
        if (!obraSocial.emailFiles.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.emailFiles, ConstantsUtil.FILE_INDEX_OS_EMAIL);
        }
        if (!obraSocial.form53Files.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.form53Files, ConstantsUtil.FILE_INDEX_OS_FORM_53);
        }
        if (!obraSocial.form59Files.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.form59Files, ConstantsUtil.FILE_INDEX_OS_FORM_53);
        }
        if (!obraSocial.modelNotesFiles.isEmpty()) {
            addAttachFiles(jAttachsArray, obraSocial.modelNotesFiles, ConstantsUtil.FILE_INDEX_OS_MODEL_NOTE);
        }
    }


    private void addAttachFiles(JSONArray jAttachsArray, List<AttachFile> files, String comment){
       try {
           for (AttachFile file : files) {
               JSONObject jAttach = new JSONObject();

               jAttach.put("archivo_id", file.id);
               jAttach.put("comentario", comment);

               jAttachsArray.put(jAttach);
           }
       }catch(Exception e){

       }
    }


    @Override
    protected Long parseObject(Object obj) {

        Long osId = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e(TAG, "SAVE OBRA SOCIAL RESPONSE: " + dic.toString());
                    osId = dic.optLong("obra_social", -1L);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return osId;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
