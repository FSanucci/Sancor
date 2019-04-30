package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.quotation.ConyugeQuotation;
import ar.com.sancorsalud.asociados.model.quotation.DesreguladoQuotation;
import ar.com.sancorsalud.asociados.model.FamiliarACargo;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.quotation.MonotributoQuotation;
import ar.com.sancorsalud.asociados.model.Pago;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class HPostQuotationParametersForExistentClientRequest extends HRequest<Quotation> {

    private static final String TAG = "HQUOT_PARAMS";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_LISTAR_INTEGRANTES;

    private long dni;
    private boolean checkUnification;

    public HPostQuotationParametersForExistentClientRequest(long dni, boolean checkUnification, Response.Listener<Quotation> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.dni = dni;
        this.checkUnification = checkUnification;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("numero_documento", dni);
            json.put("unificacion", checkUnification);

            Log.e(TAG, "JSON: " +  json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Quotation parseObject(Object obj) {

        Quotation quotation = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    quotation = new Quotation();
                    quotation.integrantes = new ArrayList<Member>();

                    Client client = new Client();
                    quotation.client = client;

                    // already client quoted data
                    JSONArray jCotizacionesArray = dic.optJSONArray("cotizaciones");
                    if (jCotizacionesArray != null) {

                        for (int i = 0; i < jCotizacionesArray.length(); i++) {
                            JSONObject jCotizacion = jCotizacionesArray.optJSONObject(i);
                            if (jCotizacion != null) {

                                QuotedClientData quotedClientData = new QuotedClientData();
                                quotedClientData.id =  jCotizacion.optLong("id", -1);

                                quotedClientData.planes = new ArrayList<Plan>();
                                JSONArray jPlanesArray = jCotizacion.optJSONArray("planesCotizados");
                                if (jPlanesArray != null) {
                                    for (int j = 0; j < jPlanesArray.length(); j++) {

                                        JSONObject jPlan = jPlanesArray.optJSONObject(j);
                                        if (jPlan != null) {

                                            Plan plan = new Plan();
                                            plan.idCotizacion = jPlan.optLong("idCotizacion", -1);
                                            plan.idProducto = jPlan.optLong("idProducto", -1);
                                            plan.idPlan = ParserUtils.optString(jPlan, "idPlan") != null ? ParserUtils.optString(jPlan, "idPlan").trim() : null;
                                            plan.descripcionPlan = ParserUtils.optString(jPlan, "descripcionPlan") != null ? ParserUtils.optString(jPlan, "descripcionPlan").trim() : null;
                                            plan.descripcionProducto = ParserUtils.optString(jPlan, "descripcionProducto") != null ? ParserUtils.optString(jPlan, "descripcionProducto").trim() : null;
                                            plan.descripcionConcepto = ParserUtils.optString(jPlan, "descripcionConcepto") != null ? ParserUtils.optString(jPlan, "descripcionConcepto").trim() : null;
                                            plan.valor = jPlan.optDouble("valor", -1f);
                                            plan.diferenciaAPagar = jPlan.optDouble("diferencia_a_pagar", 0f);

                                            if (plan.idPlan!= null && !plan.idPlan.isEmpty() ) {
                                                quotedClientData.planes.add(plan);
                                            }
                                        }
                                    }
                                }

                                // Capitas de las cotizaciones
                                quotedClientData.integrantes = new ArrayList<Member>();
                                JSONArray jCapitasArray = jCotizacion.optJSONArray("capitas");
                                if (jCapitasArray != null) {

                                    for (int l = 0; l < jCapitasArray.length(); l++) {
                                        JSONObject jMember = jCapitasArray.optJSONObject(l);
                                        Member m = new Member();

                                        m.age = jMember.optInt("edad", 0);
                                        m.cant = jMember.optInt("cantidad", -1);
                                        m.inputDate = ParserUtils.optString(jMember, "fecha_ingreso_salud") != null ? ParserUtils.optString(jMember, "fecha_ingreso_salud").trim() : null;
                                        m.dni = jMember.optLong("numero_documento", -1);

                                        String parentescoId = ParserUtils.optString(jMember, "codigo_parentesco") != null ? ParserUtils.optString(jMember, "codigo_parentesco").trim() : null;
                                        String descrParentesco = ParserUtils.optString(jMember, "descripcion_parentesco") != null ? ParserUtils.optString(jMember, "descripcion_parentesco").trim(): null;

                                        if (parentescoId != null && (descrParentesco == null ||  descrParentesco.isEmpty()) ){
                                            descrParentesco = QuoteOptionsController.getInstance().getParentescoName(parentescoId);
                                        }
                                        m.parentesco = new QuoteOption(parentescoId, descrParentesco);


                                        // Add member to client quoted data
                                        quotedClientData.integrantes.add(m);
                                    }
                                }

                                quotation.client.quotedDataList.add(quotedClientData);
                            }
                        }
                    }

                    // GET client quoted parameters
                    JSONObject jParams = dic.optJSONObject("parametros_cotizacion");
                    if (jParams != null) {

                        client.firstname = ParserUtils.optString(jParams, "nombre") != null ? ParserUtils.optString(jParams, "nombre").trim() : null;
                        client.lastname = ParserUtils.optString(jParams, "apellido") != null ? ParserUtils.optString(jParams, "apellido").trim() : null;
                        client.dni = jParams.optLong("numero_documento", -1);
                        client.zip = ParserUtils.optString(jParams, "cp") != null ? ParserUtils.optString(jParams, "cp").trim() : null;


                        // marca will be seted in caller
                        // product_id not used

                        Pago pago = new Pago();
                        String formaPagoId = ParserUtils.optString(jParams, "formadepago_id") != null ? ParserUtils.optString(jParams, "formadepago_id").trim() : null;
                        if (formaPagoId != null){
                            pago.formaPago = new QuoteOption(formaPagoId, QuoteOptionsController.getInstance().getFormaPagoName(formaPagoId));
                        }

                        String copagos = ParserUtils.optString(jParams, "copagos") != null ? ParserUtils.optString(jParams, "copagos").trim() : null;
                        if (copagos != null && !copagos.isEmpty()) {
                            if (copagos.equals("S")) {
                                pago.conCopago = true;
                            } else if (copagos.equals("N")) {
                                pago.conCopago = false;
                            }
                        }else{
                            pago.conCopago = false;
                        }

                        String bancoId = ParserUtils.optString(jParams, "banco_id") != null ? ParserUtils.optString(jParams, "banco_id").trim() : null;
                        if (bancoId != null){
                            pago.banco = new QuoteOption(bancoId, QuoteOptionsController.getInstance().getBancoName(bancoId));
                        }

                        String tarjetaId = ParserUtils.optString(jParams, "tarjeta_id") != null ? ParserUtils.optString(jParams, "tarjeta_id").trim() : null;
                        if (tarjetaId != null){
                            pago.tarjeta = new QuoteOption(tarjetaId, QuoteOptionsController.getInstance().getTarjetaName(tarjetaId));
                        }

                        quotation.pago = pago;

                        String formaIngresoId  = ParserUtils.optString(jParams, "formadeingreso_id") != null ? ParserUtils.optString(jParams, "formadeingreso_id").trim(): null;
                        if (formaIngresoId != null){
                            quotation.formaIngreso = new QuoteOption(formaIngresoId, QuoteOptionsController.getInstance().getFormaIngresoName(formaIngresoId));
                        }

                        quotation.regimenId = ParserUtils.optString(jParams, "regimen_id") != null ? ParserUtils.optString(jParams, "regimen_id").trim() : null;
                        quotation.aportantesMonotributo = ParserUtils.optString(jParams, "aportantes_monotributo") != null ? ParserUtils.optString(jParams, "aportantes_monotributo").trim() : null;

                        // region_id not used
                        //quotation.regionId = ParserUtils.optString(jParams, "region_id") != null ? ParserUtils.optString(jParams, "region_id").trim(): null;

                        quotation.nombreAfinidad = new QuoteOption(ParserUtils.optString(jParams, "afinidad_id") != null ? ParserUtils.optString(jParams, "afinidad_id").trim() : null, null);
                        //quotation.nombreAfinidad = new QuoteOption(ParserUtils.optString(jParams, "grupo_afinidad_id") != null ? ParserUtils.optString(jParams, "grupo_afinidad_id").trim() : null, null);
                        quotation.nombreEmpresa = new QuoteOption(ParserUtils.optString(jParams, "empresa_id") != null ? ParserUtils.optString(jParams, "empresa_id").trim() : null, null);

                        String provenienteId = ParserUtils.optString(jParams, "proveniente_id") != null ? ParserUtils.optString(jParams, "proveniente_id").trim() : null;
                        if (provenienteId != null){
                            quotation.coberturaProveniente = new QuoteOption(provenienteId, QuoteOptionsController.getInstance().getCoberturaName(provenienteId));
                        }

                        // Will be ovberride
                        quotation.planSalud = ParserUtils.optString(jParams, "plan_salud") != null ? ParserUtils.optString(jParams, "plan_salud").trim() : null;

                        //quotation.categoria = new QuoteOption(ParserUtils.optString(jParams, "categoria") != null ? ParserUtils.optString(jParams, "categoria").trim() : null, null);
                        String categoriaId = ParserUtils.optString(jParams, "categoria") != null ? ParserUtils.optString(jParams, "categoria").trim(): null;
                        if (categoriaId != null){
                            quotation.categoria = new QuoteOption(categoriaId, QuoteOptionsController.getInstance().getCategoriaName(categoriaId));
                        }

                        quotation.inputDate = ParserUtils.optString(jParams, "fecha_vigencia") != null ? ParserUtils.optString(jParams, "fecha_vigencia").trim() : null;

                        String condicionIvaId =  ParserUtils.optString(jParams, "condicion_iva") != null ? ParserUtils.optString(jParams, "condicion_iva").trim(): null;
                        if (condicionIvaId != null){
                            quotation.condicionIva = new QuoteOption(condicionIvaId, QuoteOptionsController.getInstance().getCondicionIvaName(condicionIvaId));
                        }

                        quotation.isEmpleadaDomestica = jParams.optBoolean("empleada_domestica", false);
                        quotation.accountNumber =   jParams.optLong("numero_cuenta", -1);
                        quotation.accountSubNumber =   jParams.optLong("numero_subcuenta", -1);

                        // Titular capitas
                        JSONArray jcapitasArray = jParams.optJSONArray("capitas");
                        Boolean titularMainAffiliation = false;

                        if (jcapitasArray != null) {
                            for (int i = 0; i < jcapitasArray.length(); i++) {

                                JSONObject jMember = jcapitasArray.optJSONObject(i);
                                Member m = new Member();

                                m.age = jMember.optInt("edad", 0);
                                m.cant = jMember.optInt("cantidad", -1);
                                m.inputDate = ParserUtils.optString(jMember, "fecha_ingreso_salud") != null ? ParserUtils.optString(jMember, "fecha_ingreso_salud").trim() : null;
                                m.dni = jMember.optLong("numero_documento", -1);
                                m.mainAffiliation = jMember.optBoolean("afiliacion_principal", false);
                                m.existent = jMember.optBoolean("existente", false);
                                m.active = jMember.optBoolean("activo", false);

                                String parentescoId = ParserUtils.optString(jMember, "codigo_parentesco") != null ? ParserUtils.optString(jMember, "codigo_parentesco").trim() : null;
                                String descrParentesco = ParserUtils.optString(jMember, "descripcion_parentesco") != null ? ParserUtils.optString(jMember, "descripcion_parentesco").trim() : null;

                                if (parentescoId != null) {
                                    if (descrParentesco == null || descrParentesco.isEmpty()) {
                                        descrParentesco = QuoteOptionsController.getInstance().getParentescoName(parentescoId);
                                    }

                                    if (parentescoId.equals(ConstantsUtil.TITULAR_MEMBER)) {
                                        titularMainAffiliation = m.mainAffiliation;
                                    }

                                    if (descrParentesco != null) {
                                        m.parentesco = new QuoteOption(parentescoId, descrParentesco);
                                    }
                                }

                                // familiares a cargo
                                JSONArray jFamiliaresArray = jMember.optJSONArray("familiares_a_cargo");
                                if (jFamiliaresArray != null) {
                                    for (int l = 0; l < jFamiliaresArray.length(); l++) {
                                        JSONObject jFamiliar = jFamiliaresArray.optJSONObject(l);

                                        FamiliarACargo fc = new FamiliarACargo();
                                        fc.cant = jFamiliar.optInt("cantidad", 0);
                                        fc.age = jFamiliar.optInt("edad", 0);

                                        String fcPparentescoId = ParserUtils.optString(jFamiliar, "codigo_parentesco") != null ? ParserUtils.optString(jFamiliar, "codigo_parentesco").trim() : null;
                                        fc.parentesco = new QuoteOption(fcPparentescoId,  QuoteOptionsController.getInstance().getParentescoName(fcPparentescoId));

                                        m.familiaresACargoList.add(fc);
                                    }
                                }

                                addMemberToQuotationIntegrantes(quotation, m);
                            }
                        }

                        String segmentoId = ParserUtils.optString(jParams, "segmento_id") != null ? ParserUtils.optString(jParams, "segmento_id").trim(): null;
                        if (segmentoId != null){
                            quotation.segmento = new QuoteOption(segmentoId, QuoteOptionsController.getInstance().getSegmentoName(segmentoId));
                        }

                        String codigoSubproductoId = ParserUtils.optString(jParams, "codigo_subproducto") != null ? ParserUtils.optString(jParams, "codigo_subproducto").trim() : null;

                        // check Segmento
                        if (segmentoId.equals(ConstantsUtil.AUTONOMO_SEGMENTO)){
                            Log.e(TAG, "AUTONOMO_SEGMENTO");
                            quotation.unificaAportes = false;

                        }else if (segmentoId.equals(ConstantsUtil.DESREGULADO_SEGMENTO)){
                            Log.e(TAG, "DESREGULADO_SEGMENTO");
                            quotation.desregulado = new DesreguladoQuotation();

                            if (codigoSubproductoId != null && Long.valueOf(codigoSubproductoId) != 0) {
                                quotation.desregulado.osDeregulado = new QuoteOption(codigoSubproductoId, QuoteOptionsController.getInstance().getOSDesreguladaName(codigoSubproductoId), QuoteOptionsController.getInstance().getOSDesregulaType(codigoSubproductoId));
                            }

                            quotation.desregulado.titularMainAffilliation = titularMainAffiliation;
                            quotation.titularMainAffilliation = titularMainAffiliation;

                            JSONArray jRemuneracionArray = jParams.optJSONArray("remuneracion");
                            if (jRemuneracionArray != null) {
                                quotation.aportes = buildAportes(jRemuneracionArray);
                            }

                            int cantidadMonotributo =  jParams.optInt("cantidad_monotributo", 0);
                            if (cantidadMonotributo > 0){
                                quotation.desregulado.aportaMonotributo = true;
                                quotation.desregulado.nroAportantes = cantidadMonotributo;

                                /*
                                if (cantidadMonotributo == quotation.integrantes.size() +1){
                                    quotation.desregulado.aportantes = new QuoteOption(ConstantsUtil.IGUAL_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.IGUAL_NRO_APORTANTES));

                                }else{
                                    quotation.desregulado.aportantes = new QuoteOption(ConstantsUtil.MENOR_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.MENOR_NRO_APORTANTES));
                                }
                                */

                            }else{
                                quotation.desregulado.aportaMonotributo = false;
                            }


                            // Conyuge data
                            JSONObject jUnifica = jParams.optJSONObject("unifica");
                            if (jUnifica != null) {
                                quotation.desregulado.conyuge = new ConyugeQuotation();
                                quotation.desregulado.unificaAportes = true;
                                quotation.unificaAportes = true;

                                String conyugeSegmentoId = ParserUtils.optString(jUnifica, "segmento_id") != null ? ParserUtils.optString(jUnifica, "segmento_id").trim(): null;
                                if (conyugeSegmentoId != null){
                                    quotation.desregulado.conyuge.segmento = new QuoteOption(conyugeSegmentoId, QuoteOptionsController.getInstance().getSegmentoName(conyugeSegmentoId));
                                }

                                String conyugeOSdesregulaId = ParserUtils.optString(jUnifica, "osdesregula_id") != null ? ParserUtils.optString(jUnifica, "osdesregula_id").trim(): null;
                                if (conyugeOSdesregulaId != null) {
                                    quotation.desregulado.conyuge.osDeregulado = new QuoteOption(conyugeOSdesregulaId, QuoteOptionsController.getInstance().getOSDesreguladaName(conyugeOSdesregulaId), QuoteOptionsController.getInstance().getOSDesregulaType(conyugeOSdesregulaId));
                                }

                                JSONArray jConyugeRemuneracionArray = jUnifica.optJSONArray("remuneracion");
                                if (jConyugeRemuneracionArray != null) {
                                    quotation.desregulado.conyuge.aportesLegales = buildAportes(jConyugeRemuneracionArray);
                                }

                                int conyugeCantidadMonotributo = jUnifica.optInt("cantidad_monotributo", 0);
                                quotation.desregulado.conyuge.nroAportantes = conyugeCantidadMonotributo;
                                if (conyugeCantidadMonotributo > 0){
                                    quotation.desregulado.conyuge.aportaMonotributo = true;

                                    /*
                                    if (conyugeCantidadMonotributo == quotation.integrantes.size() +1){
                                        quotation.desregulado.conyuge.aportantes = new QuoteOption(ConstantsUtil.IGUAL_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.IGUAL_NRO_APORTANTES));

                                    }else{
                                        quotation.desregulado.conyuge.aportantes = new QuoteOption(ConstantsUtil.MENOR_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.MENOR_NRO_APORTANTES));
                                    }
                                    */

                                }else{
                                    quotation.desregulado.conyuge.aportaMonotributo = false;
                                }

                                quotation.desregulado.conyuge.afiliado = jUnifica.optBoolean("afiliado", false);
                                quotation.desregulado.conyuge.dni = jUnifica.optLong("numero_documento", -1L);
                            }

                        }else if (segmentoId.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)){
                            Log.e(TAG, "MONOTRIBUTO_SEGMENTO");
                            quotation.monotributo = new MonotributoQuotation();

                            quotation.monotributo.titularMainAffilliation = titularMainAffiliation;
                            quotation.titularMainAffilliation = titularMainAffiliation;

                            int cantidadMonotributo =  jParams.optInt("cantidad_monotributo", 0);
                            if (cantidadMonotributo > 0){
                                quotation.monotributo.nroAportantes = cantidadMonotributo;

                                /*
                                if (cantidadMonotributo == quotation.integrantes.size() +1){
                                    quotation.monotributo.aportantes = new QuoteOption(ConstantsUtil.IGUAL_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.IGUAL_NRO_APORTANTES));
                                }else{
                                    quotation.monotributo.aportantes = new QuoteOption(ConstantsUtil.MENOR_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.MENOR_NRO_APORTANTES));
                                }
                                */
                            }

                            // Conyuge data
                            JSONObject jUnifica = jParams.optJSONObject("unifica");
                            if (jUnifica != null) {

                                quotation.monotributo.conyuge = new  ConyugeQuotation();
                                quotation.monotributo.unificaAportes = true;
                                quotation.unificaAportes = true;

                                String conyugeSegmentoId = ParserUtils.optString(jUnifica, "segmento_id") != null ? ParserUtils.optString(jUnifica, "segmento_id").trim(): null;
                                if (conyugeSegmentoId != null){
                                    quotation.monotributo.conyuge.segmento = new QuoteOption(conyugeSegmentoId, QuoteOptionsController.getInstance().getSegmentoName(conyugeSegmentoId));
                                }

                                int conyugeCantidadMonotributo = jUnifica.optInt("cantidad_monotributo", 0);
                                quotation.monotributo.conyuge.nroAportantes = conyugeCantidadMonotributo;
                                if (conyugeCantidadMonotributo > 0){
                                    quotation.monotributo.conyuge.aportaMonotributo = true;

                                    /*
                                    if (conyugeCantidadMonotributo == quotation.integrantes.size() +1){
                                        quotation.monotributo.conyuge.aportantes = new QuoteOption(ConstantsUtil.IGUAL_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.IGUAL_NRO_APORTANTES));

                                    }else{
                                        quotation.monotributo.conyuge.aportantes = new QuoteOption(ConstantsUtil.MENOR_NRO_APORTANTES, QuoteOptionsController.getInstance().getAportantesMonoGrupoName(ConstantsUtil.MENOR_NRO_APORTANTES));
                                    }
                                    */
                                }else{
                                    quotation.monotributo.conyuge.aportaMonotributo = false;
                                }

                                quotation.monotributo.conyuge.afiliado = jUnifica.optBoolean("afiliado", false);
                                quotation.monotributo.conyuge.dni = jUnifica.optLong("numero_documento", -1L);
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error Filling quotation ....");
                    Log.e(TAG, e.getMessage() != null ? e.getMessage() : "");
                    quotation = null;
                }
            }
        }
        return quotation;
    }



    private void  addMemberToQuotationIntegrantes(Quotation quotation, Member m){
        if (m.parentesco != null && m.parentesco.id.trim().equals(ConstantsUtil.TITULAR_MEMBER)) {
            if (quotation.titular == null) {
                // add just once
                quotation.titular = m;
            }
        } else {
            quotation.integrantes.add(m);
        }
    }


    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }


    private boolean checkMemberinList(List<Member> integrantes, Member inMember){
        boolean isAdded = false;
        for (Member m : integrantes){
            if (m.parentesco.id.equals(inMember.parentesco.id) && (m.dni == inMember.dni)){
                isAdded = true;
                break;
            }
        }
        return isAdded;
    }

    private  ArrayList<Aporte> buildAportes(JSONArray jRemuneracionArray){
        ArrayList<Aporte> aportes = new ArrayList<Aporte>();
        for (int i = 0; i < jRemuneracionArray.length(); i++) {
            double r = jRemuneracionArray.optDouble(i, -1f);
            if (r != -1) {
                Aporte aporte = new Aporte();
                aporte.tipoAporte = new QuoteOption(ConstantsUtil.APORTE_LEGAL_REM_BRUTA, QuoteOptionsController.getInstance().getAporteLegalName(ConstantsUtil.APORTE_LEGAL_REM_BRUTA));
                aporte.monto = r;

                aportes.add(aporte);
            }
        }

        return aportes;
    }

}
