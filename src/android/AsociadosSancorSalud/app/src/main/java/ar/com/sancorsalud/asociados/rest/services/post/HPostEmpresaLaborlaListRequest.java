package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.adapter.EmpresaArrayAdapter;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardComment;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by luciano on 8/29/17.
 */

public class HPostEmpresaLaborlaListRequest extends HRequest<List<EntidadEmpleadora>> {

    private static final String TAG = "HPOST_EE_LIST";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_EE_LIST;

    private long affiliationCardId;

    public HPostEmpresaLaborlaListRequest(long affiliationCardId, Response.Listener<List<EntidadEmpleadora>> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.affiliationCardId = affiliationCardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", affiliationCardId);
            Log.e(TAG, "SEND JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<EntidadEmpleadora> parseObject(Object obj) {

        List<EntidadEmpleadora> entidadEmpleadoraArray = new ArrayList<EntidadEmpleadora>();

        try {
            JSONArray jEmpresasArray = ((JSONObject) obj).getJSONArray("data");

            if (jEmpresasArray != null && jEmpresasArray.length() > 0) {

                for (int p = 0; p < jEmpresasArray.length(); p++) {

                    JSONObject jEntidadEmp = jEmpresasArray.optJSONObject(p);

                    EntidadEmpleadora ee = new EntidadEmpleadora();
                    ee.id = jEntidadEmp.optLong("id", -1);
                    ee.cardId = jEntidadEmp.optLong("ficha_id", -1);
                    ee.empresaId = jEntidadEmp.optLong("empresa_id", -1);
                    ee.personCardId = jEntidadEmp.optLong("ficha_persona_id", -1);

                    ee.cuit = ParserUtils.optString(jEntidadEmp, "cuit") != null ? ParserUtils.optString(jEntidadEmp, "cuit").trim() : null;
                    ee.razonSocial = ParserUtils.optString(jEntidadEmp, "razon_social") != null ? ParserUtils.optString(jEntidadEmp, "razon_social").trim() : null;

                    String inputDate = ParserUtils.optString(jEntidadEmp, "fecha_ingreso") != null ? ParserUtils.optString(jEntidadEmp, "fecha_ingreso").trim() : null;
                    if (inputDate != null) {
                        ee.inputDate = ParserUtils.parseDate(inputDate, "yyyy-MM-dd");
                    }
                    ee.remuneracion = jEntidadEmp.optDouble("remuneracion", 0f);
                    ee.isTitular = jEntidadEmp.optBoolean("titular", true);

                    try {
                        JSONArray jPhonesArray = jEntidadEmp.getJSONArray("lista_telefonos");
                        if (jPhonesArray != null && jPhonesArray.length() > 0) {
                            JSONObject jPhone = jPhonesArray.optJSONObject(0);
                            ee.areaPhone = ParserUtils.optString(jPhone, "caracteristica") != null ? ParserUtils.optString(jPhone, "caracteristica").trim() : null;
                            ee.phone = ParserUtils.optString(jPhone, "numero") != null ? ParserUtils.optString(jPhone, "numero").trim() : null;
                        }
                    } catch (JSONException jEx) {
                    }

                    // Recibos Sueldo
                    try {
                        JSONArray jRecibosSueldoArray = jEntidadEmp.getJSONArray("recibos_sueldo");
                        if (jRecibosSueldoArray != null && jRecibosSueldoArray.length() > 0) {

                            for (int r = 0; r < jRecibosSueldoArray.length(); r++) {
                                JSONObject jRecibo = jRecibosSueldoArray.optJSONObject(r);

                                AttachFile attachFile = new AttachFile();
                                attachFile.id = jRecibo.optLong("archivo_id");
                                String comment = ParserUtils.optString(jRecibo, "comentario") != null ? ParserUtils.optString(jRecibo, "comentario").trim() : null;

                                ee.reciboSueldoFiles.add(attachFile);
                            }
                        }
                    } catch (JSONException jEx) {
                    }

                    entidadEmpleadoraArray.add(ee);
                }
            }

        } catch (Exception e) {

        }
        return entidadEmpleadoraArray;
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }

}

