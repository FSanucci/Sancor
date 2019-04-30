package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.FamiliarACargo;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.plan.PlanDetail;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class HPostQuotationRequest extends HRequest<QuotationDataResult> {

    private static final String TAG = "HPost";

    private static final String PATH = RestConstants.HOST + RestConstants.POST_QUOTATION;
    private Quotation mQuotation;


    public HPostQuotationRequest(Quotation quotation, Response.Listener<QuotationDataResult> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mQuotation = quotation;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {

            json.put("marca", mQuotation.marca != null ? mQuotation.marca : JSONObject.NULL);
            //json.put("region_id", mQuotation.regionId != null ?   Long.valueOf(mQuotation.regionId): JSONObject.NULL);
            json.put("copagos", (mQuotation.pago != null && mQuotation.pago.conCopago != null) ? (mQuotation.pago.conCopago ? "S" : "N") : JSONObject.NULL);

            if (mQuotation.pago != null) {
                json.put("formadepago_id", mQuotation.pago.formaPago.id != null ? mQuotation.pago.formaPago.id : JSONObject.NULL);

                if (mQuotation.pago.formaPago.id != null && !mQuotation.pago.formaPago.id.trim().isEmpty()) {
                    if (mQuotation.pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                        //json.put("banco_id", mQuotation.pago.banco != null ? (mQuotation.pago.banco.id != null ? Long.valueOf(mQuotation.pago.banco.id.trim()) : JSONObject.NULL) : JSONObject.NULL);
                        json.put("tarjeta_id", JSONObject.NULL);
                    } else if (mQuotation.pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                        //json.put("banco_id", mQuotation.pago.banco != null ? (mQuotation.pago.banco.id != null ? Long.valueOf(mQuotation.pago.banco.id.trim()) : JSONObject.NULL) : JSONObject.NULL);
                        json.put("tarjeta_id", mQuotation.pago.tarjeta != null ? (mQuotation.pago.tarjeta.id != null ? Long.valueOf(mQuotation.pago.tarjeta.id.trim()) : JSONObject.NULL) : JSONObject.NULL);
                    } else {
                        //json.put("banco_id", JSONObject.NULL);
                        json.put("tarjeta_id", JSONObject.NULL);
                    }
                }
            } else {
                json.put("formadepago_id", JSONObject.NULL);
            }

            json.put("formadeingreso_id", (mQuotation.formaIngreso != null && mQuotation.formaIngreso.id != null) ? Long.valueOf(mQuotation.formaIngreso.id.trim()) : JSONObject.NULL);
            json.put("segmento_id", (mQuotation.segmento != null && mQuotation.segmento.id != null) ? Long.valueOf(mQuotation.segmento.id.trim()) : JSONObject.NULL);
            json.put("condicion_iva", (mQuotation.condicionIva != null && mQuotation.condicionIva.id != null) ? Long.valueOf(mQuotation.condicionIva.id.trim()) : JSONObject.NULL);

            String regimenId = null;
            if (mQuotation.regimenId != null && !mQuotation.regimenId.trim().isEmpty()) {
                regimenId = mQuotation.regimenId;
            } else {

                if (mQuotation.segmento != null) {
                    if ((mQuotation.segmento.id.equals(ConstantsUtil.AUTONOMO_SEGMENTO)) || (mQuotation.segmento.id.equals(ConstantsUtil.DESREGULADO_SEGMENTO))) {
                        regimenId = ConstantsUtil.NO_MONOTRIBUTO_REGIMEN;
                    } else {
                        if (mQuotation.monotributo != null) {
                            // MONOTRIBUTO_SEGMENTO
                            /*
                            if (mQuotation.monotributo.aportantes.id.equalsIgnoreCase(ConstantsUtil.MENOR_NRO_APORTANTES)) {
                                regimenId = ConstantsUtil.MONOTRIBUTO_REGIMEN;
                            } else {
                                regimenId = ConstantsUtil.NO_MONOTRIBUTO_REGIMEN;
                            }
                            */

                            if (mQuotation.monotributo.nroAportantes < (mQuotation.integrantes.size() + 1)) {
                                regimenId = ConstantsUtil.MONOTRIBUTO_REGIMEN;
                            } else {
                                regimenId = ConstantsUtil.NO_MONOTRIBUTO_REGIMEN;
                            }


                        }
                    }
                }
            }
            json.put("regimen_id", regimenId != null ? Long.valueOf(regimenId) : JSONObject.NULL);

            if (mQuotation.aportantesMonotributo != null && !mQuotation.aportantesMonotributo.trim().isEmpty()) {
                json.put("aportantes_monotributo", mQuotation.aportantesMonotributo);
            } else if (regimenId != null && mQuotation.formaIngreso != null && mQuotation.formaIngreso.id != null) {
                json.put("aportantes_monotributo", (regimenId.equals(ConstantsUtil.MONOTRIBUTO_REGIMEN) && mQuotation.formaIngreso.id.equals(ConstantsUtil.INDIVIDUAL_FORMA_INGRESO)) ? "M" : "I");
            } else {
                json.put("aportantes_monotributo", JSONObject.NULL);
            }

            String cotizacion = mQuotation.cotizacion;
            if (cotizacion != null) {
                json.put("cotizacion", cotizacion.trim());
                // check manual quotation
                if (cotizacion.equals(ConstantsUtil.COTIZATION_MANUAL)) {
                    json.put("plan", mQuotation.manualPlanName != null ? mQuotation.manualPlanName : JSONObject.NULL);
                    json.put("importe", mQuotation.manualPlanPrice != null ? Double.valueOf(mQuotation.manualPlanPrice) : JSONObject.NULL);
                }

            } else {
                json.put("cotizacion", JSONObject.NULL);
            }

            json.put("proveniente_id", (mQuotation.coberturaProveniente != null && mQuotation.coberturaProveniente.id != null) ? Long.valueOf(mQuotation.coberturaProveniente.id.trim()) : JSONObject.NULL);

            if (mQuotation.formaIngreso != null && mQuotation.formaIngreso.id != null) {
                if (mQuotation.formaIngreso.id.trim().equals(ConstantsUtil.AFINIDAD_FORMA_INGRESO)) {
                    if (mQuotation.nombreAfinidad != null) {

                        //json.put("grupo_afinidad_id", mQuotation.nombreAfinidad.id != null ? Long.valueOf(mQuotation.nombreAfinidad.id.trim()) : JSONObject.NULL);
                        json.put("afinidad_id", mQuotation.nombreAfinidad.id != null ? Long.valueOf(mQuotation.nombreAfinidad.id.trim()) : JSONObject.NULL);
                    }
                } else if (mQuotation.formaIngreso.id.trim().equals(ConstantsUtil.EMPRESA_FORMA_INGRESO)) {
                    if (mQuotation.nombreEmpresa != null) {
                        json.put("empresa_id", mQuotation.nombreEmpresa.id != null ? Long.valueOf(mQuotation.nombreEmpresa.id.trim()) : JSONObject.NULL);
                    }
                }
            }

            json.put("cp", mQuotation.client.zip != null ? Long.valueOf(mQuotation.client.zip.trim()) : JSONObject.NULL);
            json.put("plan_salud", mQuotation.planSalud != null ? mQuotation.planSalud : JSONObject.NULL);

            json.put("categoria", (mQuotation.categoria != null && mQuotation.categoria.id != null) ? Long.valueOf(mQuotation.categoria.id.trim()) : JSONObject.NULL);
            json.put("fecha_vigencia", mQuotation.inputDate != null ? mQuotation.inputDate.trim() : JSONObject.NULL);
            json.put("empleada_domestica", mQuotation.isEmpleadaDomestica ? true : false);

            JSONArray jAportesArray = new JSONArray();

            if (mQuotation.segmento.id == null) {
                // default SEGMENT
                json.put("remuneracion", JSONObject.NULL);
                json.put("cantidad_monotributo", 0);
                json.put("codigo_subproducto", 0);
                json.put("unifica", JSONObject.NULL);


            } else if (mQuotation.segmento.id.equals(ConstantsUtil.AUTONOMO_SEGMENTO)) {

                json.put("remuneracion", JSONObject.NULL);
                json.put("cantidad_monotributo", 0);
                json.put("codigo_subproducto", 0);
                json.put("unifica", JSONObject.NULL);

            } else if (mQuotation.segmento.id.equals(ConstantsUtil.DESREGULADO_SEGMENTO)) {

                // Titular data
                addAportesLegales(jAportesArray, mQuotation.aportes);
                json.put("remuneracion", jAportesArray.length() > 0 ? jAportesArray : JSONObject.NULL);

                json.put("cantidad_monotributo", (mQuotation.desregulado != null && mQuotation.desregulado.aportaMonotributo) ? mQuotation.desregulado.nroAportantes : 0);
                json.put("codigo_subproducto", (mQuotation.desregulado != null && mQuotation.desregulado.osDeregulado != null && mQuotation.desregulado.osDeregulado.id != null) ? Long.valueOf(mQuotation.desregulado.osDeregulado.id) : JSONObject.NULL);


                // Conyuge Data
                if (mQuotation.desregulado != null && mQuotation.desregulado.conyuge != null && mQuotation.desregulado.unificaAportes != null && mQuotation.desregulado.unificaAportes) {

                    JSONObject jUnifica = new JSONObject();
                    jUnifica.put("segmento_id", mQuotation.desregulado.conyuge.segmento.id != null ? Long.valueOf(mQuotation.desregulado.conyuge.segmento.id.trim()) : JSONObject.NULL);

                    if (mQuotation.desregulado.conyuge.osDeregulado != null) {
                        jUnifica.put("osdesregula_id", mQuotation.desregulado.conyuge.osDeregulado.id != null ? Long.valueOf(mQuotation.desregulado.conyuge.osDeregulado.id.trim()) : JSONObject.NULL);

                        JSONArray jConyugeAportesArray = new JSONArray();
                        addAportesLegales(jConyugeAportesArray, mQuotation.desregulado.conyuge.aportesLegales);

                        if (jConyugeAportesArray.length() > 0) {
                            jUnifica.put("remuneracion", jConyugeAportesArray);
                        } else {
                            jUnifica.put("remuneracion", JSONObject.NULL);
                        }

                    } else {
                        jUnifica.put("osdesregula_id", JSONObject.NULL);
                        jUnifica.put("remuneracion", JSONObject.NULL);
                    }
                    jUnifica.put("cantidad_monotributo", Long.valueOf(mQuotation.desregulado.conyuge.nroAportantes));

                    jUnifica.put("afiliado", mQuotation.desregulado.conyuge.afiliado);
                    jUnifica.put("numero_documento", mQuotation.desregulado.conyuge.dni != -1L ? mQuotation.desregulado.conyuge.dni : JSONObject.NULL);

                    json.put("unifica", jUnifica);

                } else {
                    json.put("unifica", JSONObject.NULL);
                }

            } else if (mQuotation.segmento.id.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {

                // Titular data
                json.put("cantidad_monotributo", (mQuotation.monotributo != null) ? mQuotation.monotributo.nroAportantes : 0);
                json.put("codigo_subproducto", 0);

                // Conyuge Data
                if (mQuotation.monotributo != null && mQuotation.monotributo.conyuge != null && mQuotation.monotributo.unificaAportes != null && mQuotation.monotributo.unificaAportes) {

                    JSONObject jUnifica = new JSONObject();

                    jUnifica.put("segmento_id", mQuotation.monotributo.conyuge.segmento.id != null ? Long.valueOf(mQuotation.monotributo.conyuge.segmento.id.trim()) : JSONObject.NULL);
                    jUnifica.put("osdesregula_id", JSONObject.NULL);
                    jUnifica.put("remuneracion", JSONObject.NULL);
                    jUnifica.put("cantidad_monotributo", Long.valueOf(mQuotation.monotributo.conyuge.nroAportantes));

                    jUnifica.put("afiliado", mQuotation.monotributo.conyuge.afiliado);
                    jUnifica.put("numero_documento", mQuotation.monotributo.conyuge.dni != -1L ? mQuotation.monotributo.conyuge.dni : JSONObject.NULL);

                    json.put("unifica", jUnifica);
                } else {
                    json.put("unifica", JSONObject.NULL);
                }
            }

            // Capitas
            JSONArray jCapitasArray = new JSONArray();
            if (mQuotation.titular != null) {
                jCapitasArray.put(getTitularData(mQuotation.titular));
            }

            if (mQuotation.integrantes != null) {
                for (Member member : mQuotation.integrantes) {
                    JSONObject jCapita = new JSONObject();

                    jCapita.put("edad", member.age);
                    jCapita.put("cantidad", member.cant != -1 ? member.cant : 1);
                    jCapita.put("codigo_parentesco", member.parentesco.id != null ? Long.valueOf(member.parentesco.id.trim()) : JSONObject.NULL);
                    jCapita.put("fecha_ingreso_salud", member.inputDate != null ? (member.inputDate.trim()) : (mQuotation.inputDate != null ? mQuotation.inputDate.trim() : JSONObject.NULL));
                    jCapita.put("numero_documento", member.dni != -1L ? member.dni : JSONObject.NULL);

                    // default affilliation for others members than titular
                    Boolean mainAffiliation = false;
                    if (member.parentesco.id != null && (member.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || member.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))) {

                        if (mQuotation.desregulado != null && mQuotation.desregulado.conyuge != null && mQuotation.desregulado.conyuge.segmento != null && mQuotation.desregulado.conyuge.segmento.id.equals(ConstantsUtil.DESREGULADO_SEGMENTO)) {
                            if (mQuotation.desregulado.titularMainAffilliation != null) {
                                mainAffiliation = !mQuotation.desregulado.titularMainAffilliation;
                            }
                        } else if (mQuotation.monotributo != null && mQuotation.monotributo.conyuge != null && mQuotation.monotributo.conyuge.segmento != null && mQuotation.monotributo.conyuge.segmento.id.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
                            if (mQuotation.monotributo.titularMainAffilliation != null) {
                                mainAffiliation = !mQuotation.monotributo.titularMainAffilliation;
                            }
                        }
                    }
                    jCapita.put("afiliacion_principal", mainAffiliation);


                    JSONArray jFamiliaresArray = buldFamiliaresACargo(member);
                    if (jFamiliaresArray.length() > 0) {
                        jCapita.put("familiares_a_cargo", jFamiliaresArray);
                    }

                    jCapitasArray.put(jCapita);
                }
            }
            json.put("capitas", jCapitasArray);


            // OPCIONALES
            JSONArray jOpcionalesArray = new JSONArray();
            for (AdicionalesOptativosData.OpcionalData data : mQuotation.opcionales) {

                JSONObject jOpcional = new JSONObject();
                jOpcional.put("codigo_producto", (data.productoId != -1 ? data.productoId : JSONObject.NULL));
                jOpcional.put("codigo_plan", data.codigo);
                jOpcional.put("plan_id", (data.planId != -1 ? data.planId : JSONObject.NULL));

                JSONArray jOpcionalCapitasArray = new JSONArray();
                for (Member member : data.capitas) {

                    JSONObject jCap = new JSONObject();

                    jCap.put("edad", member.age);
                    jCap.put("cantidad", member.cant != -1 ? member.cant : 1);
                    jCap.put("codigo_parentesco", member.parentesco.id != null ? Long.valueOf(member.parentesco.id.trim()) : JSONObject.NULL);
                    jCap.put("fecha_ingreso_salud", member.inputDate != null ? (member.inputDate.trim()) : (mQuotation.inputDate != null ? mQuotation.inputDate.trim() : JSONObject.NULL));
                    jCap.put("numero_documento", member.dni != -1L ? member.dni : JSONObject.NULL);

                    jOpcionalCapitasArray.put(jCap);
                }
                jOpcional.put("capitas", jOpcionalCapitasArray);
                jOpcionalesArray.put(jOpcional);
            }
            json.put("opcionales", jOpcionalesArray);


            json.put("numero_documento", mQuotation.client.dni != -1 ? mQuotation.client.dni : JSONObject.NULL);
            json.put("apellido", mQuotation.client.lastname != null ? mQuotation.client.lastname : JSONObject.NULL);
            json.put("nombre", mQuotation.client.firstname != null ? mQuotation.client.firstname : JSONObject.NULL);

            json.put("numero_cuenta", mQuotation.accountNumber != -1 ? mQuotation.accountNumber : 0);
            json.put("numero_subcuenta", mQuotation.accountSubNumber != -1 ? mQuotation.accountSubNumber : 0);
            json.put("transferencia_segmento", mQuotation.isTransferSegment);

            Log.e("JSON Cotizar", json.toString());


            String body = json.toString();

            //Decomentar para testear
            // body = "{  \"marca\": \"C\",  \"copagos\": \"S\",  \"formadepago_id\": \"RCE\",  \"formadeingreso_id\": 1,  \"segmento_id\":2,  \"regimen_id\": 1,  \"aportantes_monotributo\": \"I\",  \"region_id\":3,  \"cp\": 2400010,  \"plan_salud\": \"S\",  \"categoria\": \"D\",  \"fecha_vigencia\": \"2017-05-01\",  \"remuneracion\": [25500.50,12000],  \"remuneracion_monotributo\": 0,  \"capitas\": [    {      \"edad\": 33,      \"cantidad\": 1,      \"codigo_parentesco\": 0,      \"fecha_ingreso_salud\": \"2013-01-01\",      \"numero_documento\":30023849    },    {      \"edad\": 31,      \"cantidad\": 1,      \"codigo_parentesco\": 1,      \"fecha_ingreso_salud\": \"2014-01-01\",      \"numero_documento\": 31543612    },    {      \"edad\": 6,      \"cantidad\": 2,      \"codigo_parentesco\": 3,      \"fecha_ingreso_salud\": \"2015-01-01\",      \"numero_documento\": 40543612    },    {      \"edad\": 3,      \"cantidad\": 1,      \"codigo_parentesco\": 3,      \"fecha_ingreso_salud\": \"2015-01-01\",      \"numero_documento\": 42543612    }  ],  \"codigo_subproducto\": 23,  \"opcionales\": [    {      \"codigo_producto\": 5,      \"codigo_plan\": \"SSAC\",      \"capitas\":[        {\"edad\":30,\"codigo_parentesco\":0,\"numero_documento\":32569034,\"cantidad\":1},        {\"edad\":28,\"codigo_parentesco\":1,\"numero_documento\":33569034,\"cantidad\":1}]    },    {      \"codigo_producto\": 8,      \"codigo_plan\": \"\"    },    {      \"codigo_producto\": 2,      \"codigo_plan\": \"\"    }  ],  \"numero_documento\": 30023849,  \"apellido\": \"PEREZ\",  \"nombre\": \"JUAN\"}";

            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            Log.e(TAG, "Error send Quotation check parameters of Quotation ...... " + (e.getMessage() != null ? e.getMessage() : ""));
            e.printStackTrace();
            return null;
        }
    }


    private void addAportesLegales(JSONArray jAportesArray, List<Aporte> aportes) {
        try {
            if (aportes != null && aportes.size() > 0) {
                for (Aporte aporte : aportes) {
                    if (aporte.isRemuneracionBruta()) {
                        jAportesArray.put(aporte.monto);
                    } else {
                        jAportesArray.put(aporte.remuneracionBruta());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void addAportesLegales(JSONArray jAportesArray, List<Double> remuneracionList, boolean flag) {
        for (Double r : remuneracionList) {
            jAportesArray.put(r);
        }
    }


    @Override
    protected QuotationDataResult parseObject(Object obj) {

        QuotationDataResult result = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    result = new QuotationDataResult();

                    JSONObject jCotizacion = dic.optJSONObject("cotizacion");
                    if (jCotizacion != null) {
                        Log.e("Result JSON jCotizacion", jCotizacion.toString());

                        result.id = jCotizacion.optLong("id", -1);
                        result.link = ParserUtils.optString(jCotizacion, "link") != null ? ParserUtils.optString(jCotizacion, "link").trim() : null;

                        try {
                            JSONArray jPlanesArray = jCotizacion.getJSONArray("planesCotizados");

                            if (jPlanesArray != null) {
                                for (int i = 0; i < jPlanesArray.length(); i++) {
                                    JSONObject jPlan = jPlanesArray.optJSONObject(i);

                                    if (jPlan != null) {
                                        Plan plan = new Plan();
                                        plan.idCotizacion = jPlan.optLong("idCotizacion", -1);
                                        plan.idProducto = jPlan.optLong("idProducto", -1);
                                        plan.idPlan = ParserUtils.optString(jPlan, "idPlan") != null ? ParserUtils.optString(jPlan, "idPlan").trim() : null;
                                        plan.descripcionPlan = ParserUtils.optString(jPlan, "descripcionPlan") != null ? ParserUtils.optString(jPlan, "descripcionPlan").trim() : null;
                                        plan.descripcionConcepto = ParserUtils.optString(jPlan, "descripcionConcepto") != null ? ParserUtils.optString(jPlan, "descripcionConcepto").trim() : null;
                                        plan.valor = jPlan.optDouble("valor", 0f);
                                        plan.diferenciaAPagar = jPlan.optDouble("diferencia_a_pagar", 0f);

                                        try {
                                            JSONArray jDetailArray = jPlan.getJSONArray("detalle");
                                            for (int j = 0; j < jDetailArray.length(); j++) {

                                                JSONObject jDetail = jDetailArray.optJSONObject(j);

                                                PlanDetail detail = new PlanDetail();
                                                detail.descripcionConcepto = ParserUtils.optString(jDetail, "descripcionConcepto") != null ? ParserUtils.optString(jDetail, "descripcionConcepto").trim() : null;
                                                detail.idPlan = ParserUtils.optString(jDetail, "idPlan") != null ? ParserUtils.optString(jDetail, "idPlan").trim() : null;
                                                detail.descripcionPlan = ParserUtils.optString(jDetail, "descripcionPlan") != null ? ParserUtils.optString(jDetail, "descripcionPlan").trim() : null;
                                                detail.valor = jDetail.optDouble("valor", 0f);

                                                if (detail.descripcionConcepto != null && !detail.descripcionConcepto.isEmpty()) {
                                                    plan.details.add(detail);
                                                }
                                            }
                                        } catch (JSONException jEx) {
                                        }

                                        result.planes.add(plan);
                                    }
                                }
                            }
                        } catch (JSONException jEx) {
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    result = null;
                }
            }
        }
        return result;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }


    private JSONObject getTitularData(Member member) {

        String segmentoId = mQuotation.segmento.id;

        JSONObject jTitular = null;
        try {
            jTitular = new JSONObject();

            jTitular.put("edad", member.age);
            jTitular.put("cantidad", member.cant != -1 ? member.cant : 1);
            jTitular.put("codigo_parentesco", member.parentesco.id != null ? Long.valueOf(member.parentesco.id.trim()) : JSONObject.NULL);
            jTitular.put("fecha_ingreso_salud", member.inputDate != null ? (member.inputDate.trim()) : (mQuotation.inputDate != null ? mQuotation.inputDate.trim() : JSONObject.NULL));
            jTitular.put("numero_documento", member.dni != -1L ? member.dni : JSONObject.NULL);

            Boolean mainAffiliation = null;
            if (segmentoId.equals(ConstantsUtil.AUTONOMO_SEGMENTO)) {
                mainAffiliation = true;
            } else if (segmentoId.equals(ConstantsUtil.DESREGULADO_SEGMENTO)) {
                mainAffiliation = mQuotation.desregulado.titularMainAffilliation;
            } else if (segmentoId.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
                mainAffiliation = mQuotation.monotributo.titularMainAffilliation;
            } else {
                mainAffiliation = null;
            }

            jTitular.put("afiliacion_principal", mainAffiliation != null ? mainAffiliation : JSONObject.NULL);

            JSONArray jFamiliaresArray = buldFamiliaresACargo(member);
            if (jFamiliaresArray.length() > 0) {
                jTitular.put("familiares_a_cargo", jFamiliaresArray);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return jTitular;
    }

    private JSONArray buldFamiliaresACargo(Member member) {

        JSONArray jFamiliaresArray = new JSONArray();
        try {

            if (!member.familiaresACargoList.isEmpty()) {
                for (FamiliarACargo fc : member.familiaresACargoList) {

                    JSONObject jFamiliar = new JSONObject();
                    jFamiliar.put("edad", fc.age);
                    jFamiliar.put("cantidad", fc.cant != -1 ? fc.cant : 1);
                    jFamiliar.put("codigo_parentesco", fc.parentesco.id != null ? Long.valueOf(fc.parentesco.id.trim()) : JSONObject.NULL);

                    jFamiliaresArray.put(jFamiliar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jFamiliaresArray;
    }
}
