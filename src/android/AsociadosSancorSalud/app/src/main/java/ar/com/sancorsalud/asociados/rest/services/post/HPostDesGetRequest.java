package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesDetail;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostDesGetRequest extends HRequest<Des> {

    private static final String TAG = "HPOST_DES";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_DES_GET;

    private long desId = -1L;
    private long cardId;

    public HPostDesGetRequest(long desId, long cardId, Response.Listener<Des> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);

        this.desId = desId;
        this.cardId = cardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", (cardId != -1L ? cardId : JSONObject.NULL));
            json.put("id", (desId != -1L ? desId : JSONObject.NULL));
            Log.e(TAG, "JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Des parseObject(Object obj) {

        Des des = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    Log.e(TAG, "DES GET RESP: " + dic.toString());

                    JSONObject jDes = dic.optJSONObject("des");
                    if (jDes != null) {

                        des = new Des();
                        des.id = jDes.optLong("id", -1L);
                        des.cardId = jDes.optLong("ficha_id", -1L);
                        des.requestNumber = jDes.optLong("numero_solicitud", -1L);
                        des.doctorId = jDes.optLong("medico_auditor_id", -1L);
                        des.comments = ParserUtils.optString(jDes, "comentarios") != null ? ParserUtils.optString(jDes, "comentarios").trim() : null;

                        // DES FILES
                        try {
                            JSONArray jDesFilesIdArray = jDes.getJSONArray("archivos");
                            if (jDesFilesIdArray != null && jDesFilesIdArray.length() > 0) {
                                for (int i = 0; i < jDesFilesIdArray.length(); i++) {

                                    AttachFile desFile = new AttachFile();
                                    desFile.id = jDesFilesIdArray.optLong(i);
                                    des.desFiles.add(desFile);
                                }
                            }
                        } catch (JSONException jEx) {
                        }

                        // HEALH FILES
                        AttachFile healhCertFile = new AttachFile();
                        healhCertFile.id = jDes.optLong("buena_salud", -1L);
                        if (healhCertFile.id != -1) {
                            des.healthCertFiles.add(healhCertFile);
                        }

                        // ANEXO FILES
                        AttachFile anexoFile = new AttachFile();
                        anexoFile.id = jDes.optLong("anexo3_id", -1L);
                        if (anexoFile.id != -1) {
                            des.anexoFiles.add(anexoFile);
                        }

                        //ATTACHS FILES
                        try {
                            JSONArray jDocumentsArray = jDes.getJSONArray("documentos");
                            if (jDocumentsArray != null && jDocumentsArray.length() > 0) {
                                for (int j = 0; j < jDocumentsArray.length(); j++) {

                                    JSONObject jDocument = jDocumentsArray.getJSONObject(j);
                                    if (jDocument != null) {

                                        AttachFile attachFile = new AttachFile();
                                        attachFile.id = jDocument.optLong("archivo_id", -1L);
                                        des.attachsFiles.add(attachFile);
                                    }
                                }
                            }
                        } catch (JSONException jEx) {
                        }

                        try {
                            JSONArray jDetailsArray = jDes.getJSONArray("detalles");
                            if (jDetailsArray != null && jDetailsArray.length() > 0) {

                                for (int j = 0; j < jDetailsArray.length(); j++) {
                                    JSONObject jDetail = jDetailsArray.optJSONObject(j);

                                    DesDetail detail = new DesDetail();
                                    detail.id = jDetail.optLong("id", -1L);
                                    detail.cardPeopleId = jDetail.optLong("ficha_persona_id", -1L);

                                    detail.weight = jDetail.optDouble("peso", -1f);
                                    detail.height = jDetail.optDouble("altura", -1f);

                                    // TODO PARCHE Calcular el imc si tengo peso y altura y setearlo deberia venir del serv
                                    if ((detail.weight != -1f && detail.height != -1f)  && (detail.weight != 0f && detail.height != 0f)) {
                                        double imc = (detail.weight / (detail.height * detail.height));
                                        String sImc = String.format("%.2f", imc).replace(",", ".");
                                        detail.imc = Double.valueOf(sImc.trim().replace(",", "."));
                                    }

                                    detail.module = jDetail.optLong("modulo", -1L);
                                    detail.cantCoutas = jDetail.optInt("cantidad_cuotas", 0);
                                    detail.age = jDetail.optInt("edad", 0);

                                    detail.firstname =  ParserUtils.optString(jDetail, "nombre") != null ? ParserUtils.optString(jDetail, "nombre").trim(): null;
                                    detail.lastname =  ParserUtils.optString(jDetail, "apellido") != null ? ParserUtils.optString(jDetail, "apellido").trim(): null;
                                    detail.docTypeID = jDetail.optLong("tipo_documento_id", -1L);
                                    detail.dni = jDetail.optLong("numero_documento", -1L);

                                    detail.existent = jDetail.optBoolean("existente", false);
                                    detail.active = jDetail.optBoolean("activo", false);

                                    String parentescoId = ParserUtils.optString(jDetail, "parentesco_id") != null ? ParserUtils.optString(jDetail, "parentesco_id").trim() : null;
                                    if (parentescoId != null) {
                                        detail.parentesco = new QuoteOption(parentescoId, QuoteOptionsController.getInstance().getParentescoName(parentescoId));
                                    }

                                    des.details.add(detail);
                                }
                            }
                        } catch (JSONException jEx) {
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return des;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
