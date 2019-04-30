package ar.com.sancorsalud.asociados.rest.services.put;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.Address;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationDataResult;
import ar.com.sancorsalud.asociados.model.affiliation.BeneficiarioSUF;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.Person;
import ar.com.sancorsalud.asociados.model.affiliation.TicketPago;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;

/**
 * Created by sergio on 1/6/17.
 */

public class HPutCardUpdateRequest extends HRequest<AffiliationDataResult> {

    private static final String TAG = "HPUT_UPDATECARD";
    private static final String PATH = RestConstants.HOST + RestConstants.PUT_CARD_UPDATE;

    private AffiliationCard affiliationCard;

    public HPutCardUpdateRequest(AffiliationCard affiliationCard, Response.Listener<AffiliationDataResult> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, PATH, listener, errorListener);
        this.affiliationCard = affiliationCard;
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
            json.put("ficha_id", affiliationCard.id != -1L ? affiliationCard.id : JSONObject.NULL);
            json.put("cotizacion_id", affiliationCard.idCotizacion != -1L ? affiliationCard.idCotizacion : JSONObject.NULL);

            json.put("requiere_auditoria", "N"); // TODO DES ??
            json.put("entidad_numero", (affiliationCard.titularData != null && affiliationCard.titularData.entity != null) ? Long.valueOf(affiliationCard.titularData.entity.id.trim()) : JSONObject.NULL);
            json.put("datero", (affiliationCard.titularData != null && affiliationCard.titularData.dateroNumber != null) ? Long.valueOf(affiliationCard.titularData.dateroNumber.extra.trim()) : JSONObject.NULL);

            json.put("documento_afinidad_id", (affiliationCard.titularData != null && affiliationCard.titularData.documentoAfinidadId != -1L) ? affiliationCard.titularData.documentoAfinidadId : JSONObject.NULL);

            json.put("documento_convenio_id", (affiliationCard.titularData != null && affiliationCard.titularData.documentoAgreementId != -1L) ? affiliationCard.titularData.documentoAgreementId : JSONObject.NULL);

            json.put("promotor_id", (affiliationCard.titularData != null && affiliationCard.titularData.promotorId != -1L) ? Long.valueOf(affiliationCard.titularData.promotorId) : JSONObject.NULL);

            json.put("afinidad_id", (affiliationCard.titularData != null && affiliationCard.titularData.nombreAfinidad != null && affiliationCard.titularData.nombreAfinidad.id != null) ? Long.valueOf(affiliationCard.titularData.nombreAfinidad.id.trim()) : JSONObject.NULL);
            json.put("empresa_factura_id", (affiliationCard.titularData != null && affiliationCard.titularData.nombreEmpresa != null && affiliationCard.titularData.nombreEmpresa.id != null) ? Long.valueOf(affiliationCard.titularData.nombreEmpresa.id.trim()) : JSONObject.NULL);

            json.put("segmento_id", (affiliationCard.titularData != null && affiliationCard.titularData.segmento != null && affiliationCard.titularData.segmento.id != null) ? Long.valueOf(affiliationCard.titularData.segmento.id.trim()) : JSONObject.NULL);
            json.put("segmento_conyuge_id", (affiliationCard.conyugeData != null && affiliationCard.conyugeData.segmento != null && affiliationCard.conyugeData.segmento.id != null) ? Long.valueOf(affiliationCard.conyugeData.segmento.id.trim()) : JSONObject.NULL);

            json.put("formadeingreso_id", (affiliationCard.titularData != null && affiliationCard.titularData.formaIngreso != null && affiliationCard.titularData.formaIngreso.id != null) ? Long.valueOf(affiliationCard.titularData.formaIngreso.id.trim()) : JSONObject.NULL);

            Log.e(TAG, "Save Fecha Carga: " + ParserUtils.parseDate(affiliationCard.titularData.fechaCarga, DATE_FORMAT));

            json.put("fecha_carga", affiliationCard.titularData != null && affiliationCard.titularData.fechaCarga != null ? (ParserUtils.parseDate(affiliationCard.titularData.fechaCarga, DATE_FORMAT)) : JSONObject.NULL);
            json.put("fecha_inicio_servicio", affiliationCard.titularData != null && affiliationCard.titularData.fechaInicioServicio != null ? (ParserUtils.parseDate(affiliationCard.titularData.fechaInicioServicio, DATE_FORMAT)) : JSONObject.NULL);

            json.put("obra_social_proveniente_id", (affiliationCard.titularData != null && affiliationCard.titularData.coberturaProveniente != null) ? Long.valueOf(affiliationCard.titularData.coberturaProveniente.id.trim()) : JSONObject.NULL);

            if ((affiliationCard.titularData != null && affiliationCard.titularData.categoria != null && affiliationCard.titularData.categoria.id != null)) {
                if (affiliationCard.titularData.categoria.id.equals(Long.valueOf(ConstantsUtil.ACTIVO_CATEGORIA).toString())) {
                    json.put("categoria_id", "A");
                } else if (affiliationCard.titularData.categoria.id.equals(Long.valueOf(ConstantsUtil.ADHERENTE_CATEGORIA).toString())) {
                    json.put("categoria_id", "D");
                }
            } else {
                json.put("categoria_id", JSONObject.NULL);
            }

            // TODO  inferir esto del tickert,nro de DES
            json.put("numero_solicitud", JSONObject.NULL); // nro DES ?


            // PERSONAS
            JSONArray jPersonasArray = new JSONArray();

            // TITULAR DATA
            JSONObject jTitular = new JSONObject();

            jTitular.put("ficha_persona_id", (affiliationCard.titularData != null && affiliationCard.titularData.personCardId != -1L) ? affiliationCard.titularData.personCardId : JSONObject.NULL);
            jTitular.put("parentesco_id", Long.valueOf(ConstantsUtil.TITULAR_MEMBER));
            jTitular.put("aporta_monotributo", affiliationCard.titularData.aportaMonotributo);

            // Special character in DB not send if null
            if (affiliationCard.titularData != null && affiliationCard.titularData.sex != null && affiliationCard.titularData.sex.id != null) {
                jTitular.put("sexo_id", affiliationCard.titularData.sex.id.trim());
            }

            jTitular.put("edad", (affiliationCard.titularData.age != -1) ? Integer.valueOf(affiliationCard.titularData.age) : JSONObject.NULL);

            jTitular.put("tipo_documento_id", (affiliationCard.titularData != null && affiliationCard.titularData.docType != null && affiliationCard.titularData.docType.id != null) ? Long.valueOf(affiliationCard.titularData.docType.id.trim()) : JSONObject.NULL);
            jTitular.put("numero_documento", (affiliationCard.titularData != null && affiliationCard.titularData.dni != -1L) ? affiliationCard.titularData.dni : JSONObject.NULL);

            // Special character in DB not send if null
            if (affiliationCard.titularData != null && affiliationCard.titularData.birthday != null) {
                jTitular.put("fecha_nacimiento", affiliationCard.titularData.getBirthday());
            }

            jTitular.put("estado_civil_id", (affiliationCard.titularData != null && affiliationCard.titularData.civilStatus != null && affiliationCard.titularData.civilStatus.id != null) ? affiliationCard.titularData.civilStatus.id.trim() : JSONObject.NULL);
            jTitular.put("apellido", (affiliationCard.titularData != null && affiliationCard.titularData.lastname != null && !affiliationCard.titularData.lastname.trim().isEmpty()) ? affiliationCard.titularData.lastname : "");
            jTitular.put("nombre", (affiliationCard.titularData != null && affiliationCard.titularData.firstname != null && !affiliationCard.titularData.firstname.trim().isEmpty()) ? affiliationCard.titularData.firstname : "");
            jTitular.put("condicion_iva_id", (affiliationCard.titularData != null && affiliationCard.titularData.condicionIva != null && affiliationCard.titularData.condicionIva.id != null) ? Long.valueOf(affiliationCard.titularData.condicionIva.id.trim()) : JSONObject.NULL);

            jTitular.put("discapacidad_id", 0); // no for titular
            jTitular.put("cuil", (affiliationCard.titularData != null && affiliationCard.titularData.cuil != null && !affiliationCard.titularData.cuil.trim().isEmpty()) ? Long.valueOf(affiliationCard.titularData.cuil.trim()) : JSONObject.NULL);

            if (affiliationCard.contactData != null && affiliationCard.contactData.addInvoice != null) {
                jTitular.put("adhiere_efactura", (affiliationCard.contactData.addInvoice ? "S" : "N"));
            }

            jTitular.put("nacionalidad_id", (affiliationCard.titularData != null && affiliationCard.titularData.nationality != null && affiliationCard.titularData.nationality.id != null) ? Long.valueOf(affiliationCard.titularData.nationality.id.trim()) : JSONObject.NULL);
            jTitular.put("existente", affiliationCard.titularData.existent);

            // TITULAR DOCUMENTOS
            JSONArray jDocumentsArray = new JSONArray();
            if (affiliationCard.document != null) {

                if (affiliationCard.document.dniFrontFiles.size() > 0) {
                    for (AttachFile dniFrontFile : affiliationCard.document.dniFrontFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", dniFrontFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_DNI_FILE);
                        jDocumentsArray.put(jDocument);
                    }
                }
                if (affiliationCard.document.dniBackFiles.size() > 0) {
                    for (AttachFile dniBackFile : affiliationCard.document.dniBackFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", dniBackFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_DNI_FILE);
                        jDocumentsArray.put(jDocument);
                    }
                }

                if (affiliationCard.document.cuilFiles.size() > 0) {
                    for (AttachFile cuilFile : affiliationCard.document.cuilFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", cuilFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_CUIL_FILE);
                        jDocumentsArray.put(jDocument);
                    }
                }
                if (affiliationCard.document.ivaFiles.size() > 0) {
                    for (AttachFile ivaFile : affiliationCard.document.ivaFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", ivaFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_IVA_FILE);
                        jDocumentsArray.put(jDocument);
                    }
                }
                if (affiliationCard.document.coverageFiles.size() > 0) {
                    for (AttachFile coverageFile : affiliationCard.document.coverageFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", coverageFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_COVERAGE);
                        jDocumentsArray.put(jDocument);
                    }
                }
                if (affiliationCard.document.planFiles.size() > 0) {
                    for (AttachFile planFile : affiliationCard.document.planFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", planFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_PLAN);
                        jDocumentsArray.put(jDocument);
                    }
                }

                // TITULAR MONOTRIBUTO FILES
                if (affiliationCard.document.form184Files.size() > 0) {
                    for (AttachFile formFile : affiliationCard.document.form184Files) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", formFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_FORM_184);
                        jDocumentsArray.put(jDocument);
                    }
                }
                if (affiliationCard.document.threeMonthFiles.size() > 0) {
                    for (AttachFile sixMonthFile : affiliationCard.document.threeMonthFiles) {
                        JSONObject jDocument = new JSONObject();

                        jDocument.put("documento_id", sixMonthFile.id);
                        jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_3_MONTH_TICKET);
                        jDocumentsArray.put(jDocument);
                    }
                }

            }
            jTitular.put("documentos", jDocumentsArray);

            // TITULAR ADDRESS
            JSONArray jTitularAddressArray = new JSONArray();
            JSONObject jTitularAddress = new JSONObject();

            jTitularAddress.put("tipo_domicilio_id", ConstantsUtil.ADDRESS_TITULAR_TYPE);
            jTitularAddress.put("calle", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.street != null && !affiliationCard.titularAddress.street.isEmpty()) ? affiliationCard.titularAddress.street : "");
            jTitularAddress.put("numero", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.number != -1) ? Long.valueOf(affiliationCard.titularAddress.number) : JSONObject.NULL);
            jTitularAddress.put("orientacion", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.orientation != null && affiliationCard.titularAddress.orientation.id != null) ? affiliationCard.titularAddress.orientation.id.trim() : JSONObject.NULL);
            jTitularAddress.put("piso", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.floor != -1) ? Long.valueOf(affiliationCard.titularAddress.floor) : JSONObject.NULL);
            jTitularAddress.put("departamento", (affiliationCard.titularData != null && affiliationCard.titularAddress.dpto != null && !affiliationCard.titularAddress.dpto.isEmpty()) ? affiliationCard.titularAddress.dpto.trim() : JSONObject.NULL);

            jTitularAddress.put("domicilio_atributo_codigo1", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.adressCode1 != null && affiliationCard.titularAddress.adressCode1.id != null) ? Long.valueOf(affiliationCard.titularAddress.adressCode1.id.trim()) : JSONObject.NULL);
            jTitularAddress.put("domicilio_atributo_codigo2", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.adressCode2 != null && affiliationCard.titularAddress.adressCode2.id != null) ? Long.valueOf(affiliationCard.titularAddress.adressCode2.id.trim()) : JSONObject.NULL);
            jTitularAddress.put("domicilio_atributo_descripcion1", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.adressCode1Description != null && !affiliationCard.titularAddress.adressCode1Description.trim().isEmpty()) ? affiliationCard.titularAddress.adressCode1Description.trim() : JSONObject.NULL);
            jTitularAddress.put("domicilio_atributo_descripcion2", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.adressCode2Description != null && !affiliationCard.titularAddress.adressCode2Description.trim().isEmpty()) ? affiliationCard.titularAddress.adressCode2Description.trim() : JSONObject.NULL);

            jTitularAddress.put("barrio", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.barrio != null && !affiliationCard.titularAddress.barrio.trim().isEmpty()) ? affiliationCard.titularAddress.barrio : JSONObject.NULL);
            jTitularAddress.put("cp", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.zipCode != null && !affiliationCard.titularAddress.zipCode.trim().isEmpty()) ? Long.valueOf(affiliationCard.titularAddress.zipCode.trim()) : JSONObject.NULL);

            // NOT IN USE CODE
            //jTitularAddress.put("sufijo_cpa", (affiliationCard.titularAddress != null && affiliationCard.titularAddress.code != null  && !affiliationCard.titularAddress.code.trim().isEmpty()) ?  affiliationCard.titularAddress.code.trim() : JSONObject.NULL);

            jTitularAddressArray.put(jTitularAddress);
            //jTitular.put("lista_domicilios", jTitularAddressArray);

            // ...//

            // TITULAR CONTACT_DATA
            JSONArray jTitularPhoneArray = new JSONArray();
            JSONArray jTitularMailsArray = new JSONArray();

            if (affiliationCard.contactData != null) {

                // TODO OLD

                if (affiliationCard.contactData.areaPhone != null && !affiliationCard.contactData.areaPhone.isEmpty() &&
                        affiliationCard.contactData.phone != null && !affiliationCard.contactData.phone.isEmpty()) {

                    JSONObject jTitularPhoneContact = new JSONObject();

                    jTitularPhoneContact.put("caracteristica", affiliationCard.contactData.areaPhone);
                    jTitularPhoneContact.put("numero", affiliationCard.contactData.phone);

                    jTitularPhoneContact.put("es_principal", "S");
                    jTitularPhoneContact.put("tipo_id", 1);
                    jTitularPhoneArray.put(jTitularPhoneContact);
                }


                if (affiliationCard.contactData.areaDevice != null && !affiliationCard.contactData.areaDevice.isEmpty() &&
                        affiliationCard.contactData.device != null && !affiliationCard.contactData.device.isEmpty()) {

                    JSONObject jTitularDeviceContact = new JSONObject();

                    jTitularDeviceContact.put("caracteristica", affiliationCard.contactData.areaDevice );
                    jTitularDeviceContact.put("numero", affiliationCard.contactData.device);

                    jTitularDeviceContact.put("es_principal", "S");
                    jTitularDeviceContact.put("tipo_id", 1);
                    jTitularPhoneArray.put(jTitularDeviceContact);
                }


                if (affiliationCard.contactData.email != null && !affiliationCard.contactData.email.isEmpty()) {
                    JSONObject jTitularMailContact = new JSONObject();
                    jTitularMailContact.put("tipo_id", 1);
                    jTitularMailContact.put("mail", affiliationCard.contactData.email.trim());
                    jTitularMailsArray.put(jTitularMailContact);
                }


                if (affiliationCard.contactData.alternativeAddress != null) {
                    Address alternativeAddress = affiliationCard.contactData.alternativeAddress;

                    JSONObject jAlternativeAddress = new JSONObject();

                    jAlternativeAddress.put("tipo_domicilio_id", ConstantsUtil.ADDRESS_ALTERNATIVE_TYPE);
                    jAlternativeAddress.put("calle", (alternativeAddress.street != null && !alternativeAddress.street.isEmpty()) ? alternativeAddress.street : "");
                    jAlternativeAddress.put("numero", (alternativeAddress.number != -1) ? Long.valueOf(alternativeAddress.number) : JSONObject.NULL);
                    jAlternativeAddress.put("orientacion", (alternativeAddress.orientation != null && alternativeAddress.orientation.id != null) ? alternativeAddress.orientation.id.trim() : JSONObject.NULL);
                    jAlternativeAddress.put("piso", (alternativeAddress.floor != -1) ? Long.valueOf(alternativeAddress.floor) : JSONObject.NULL);
                    jAlternativeAddress.put("departamento", (alternativeAddress.dpto != null && !alternativeAddress.dpto.isEmpty()) ? alternativeAddress.dpto.trim() : JSONObject.NULL);

                    jAlternativeAddress.put("domicilio_atributo_codigo1", (alternativeAddress.adressCode1 != null && alternativeAddress.adressCode1.id != null) ? Long.valueOf(alternativeAddress.adressCode1.id.trim()) : JSONObject.NULL);
                    jAlternativeAddress.put("domicilio_atributo_codigo2", (alternativeAddress.adressCode2 != null && alternativeAddress.adressCode2.id != null) ? Long.valueOf(alternativeAddress.adressCode2.id.trim()) : JSONObject.NULL);
                    jAlternativeAddress.put("domicilio_atributo_descripcion1", (alternativeAddress.adressCode1Description != null && alternativeAddress.adressCode1Description.trim().isEmpty()) ? alternativeAddress.adressCode1Description.trim() : JSONObject.NULL);
                    jAlternativeAddress.put("domicilio_atributo_descripcion2", (alternativeAddress.adressCode2Description != null && !alternativeAddress.adressCode2Description.trim().isEmpty()) ? alternativeAddress.adressCode2Description.trim() : JSONObject.NULL);

                    jAlternativeAddress.put("barrio", (alternativeAddress.barrio != null && !alternativeAddress.barrio.trim().isEmpty()) ? alternativeAddress.barrio : JSONObject.NULL);
                    jAlternativeAddress.put("cp", (alternativeAddress.zipCode != null && !alternativeAddress.zipCode.trim().isEmpty()) ? Long.valueOf(alternativeAddress.zipCode.trim()) : JSONObject.NULL);

                    // NOT IN USE CODE
                    //jTitularAddress.put("sufijo_cpa", ( alternativeAddress.code != null  && !alternativeAddress.code.trim().isEmpty()) ?  alternativeAddress.code.trim() : JSONObject.NULL);

                    jTitularAddressArray.put(jAlternativeAddress);
                }
            }

            jTitular.put("lista_domicilios", jTitularAddressArray);
            jTitular.put("lista_telefonos", jTitularPhoneArray);
            jTitular.put("lista_mails", jTitularMailsArray);


            // ADD TITULAR DATA
            jPersonasArray.put(jTitular);

            // REST OF MEMBERS //
            if (affiliationCard.members.size() > 0) {

                for (Member member : affiliationCard.members) {

                    // Pre settt DOC type if not have one --> DB restriction
                    if (member.docType == null) {
                        member.docType = new QuoteOption(ConstantsUtil.DOC_TYPE_IDENTITY, QuoteOptionsController.getInstance().getDocTypeName(ConstantsUtil.DOC_TYPE_IDENTITY));
                    }
                    // Add MEMBERS data
                    JSONObject jMember = new JSONObject();

                    jMember.put("ficha_persona_id", (member.personCardId != -1L) ? member.personCardId : JSONObject.NULL);
                    jMember.put("parentesco_id", (member.parentesco != null && member.parentesco.id != null) ? Long.valueOf(member.parentesco.id.trim()) : JSONObject.NULL);
                    jMember.put("aporta_monotributo", member.aportaMonotributo);

                    if (member.sex != null && member.sex.id != null) {
                        jMember.put("sexo_id", member.sex.id.trim());
                    }

                    jMember.put("edad", (member.age != -1) ? Integer.valueOf(member.age) : JSONObject.NULL);
                    jMember.put("tipo_documento_id", (member.docType != null && member.docType.id != null) ? Long.valueOf(member.docType.id.trim()) : JSONObject.NULL);
                    jMember.put("numero_documento", (member.dni != -1L) ? member.dni : JSONObject.NULL);

                    if (member.birthday != null) {
                        jMember.put("fecha_nacimiento", member.getBirthday());
                    }

                    jMember.put("estado_civil_id", ConstantsUtil.CIVIL_STATUS_SOLTERO);
                    jMember.put("condicion_iva_id", Long.valueOf(ConstantsUtil.CONDICION_IVA_CONSUMIDOR_FINAL));

                    jMember.put("apellido", (member.lastname != null && !member.lastname.isEmpty()) ? member.lastname.trim() : "");
                    jMember.put("nombre", (member.firstname != null && !member.firstname.isEmpty()) ? member.firstname.trim() : "");
                    jMember.put("discapacidad_id", member.hasDisability != null ? (member.hasDisability ? 1 : 0) : 0);
                    jMember.put("cuil", (member.cuil != null && !member.cuil.trim().isEmpty()) ? Long.valueOf(member.cuil.trim()) : JSONObject.NULL);
                    jMember.put("nacionalidad_id", (member.nationality != null && member.nationality.id != null) ? Long.valueOf(member.nationality.id.trim()) : JSONObject.NULL);
                    jMember.put("existente", member.existent);

                    /*
                    if (member.addInvoice != null) {
                        jMember.put("adhiere_efactura", (member.addInvoice ? "S" : "N"));
                    }
                    */

                    // TODO ADD FIELD
                    /*
                    if (affiliationCard.titularData.segmento != null && affiliationCard.titularData.segmento.id.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)){
                        jMember.put("xxx", member.titularAportaForMember);
                    }
                    */

                    // MEMBER DOCUMENTS
                    JSONArray jMembersDocumentsArray = new JSONArray();
                    if (!member.certDiscapacidadFiles.isEmpty()) {
                        for (AttachFile certDiscFile : member.certDiscapacidadFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", certDiscFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_CERT_DISC_FILE);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }

                    if (!member.dniFrontFiles.isEmpty()) {
                        for (AttachFile dniFrontFile : member.dniFrontFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", dniFrontFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_DNI_FILE);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }
                    if (!member.dniBackFiles.isEmpty()) {
                        for (AttachFile dniBackFile : member.dniBackFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", dniBackFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_DNI_FILE);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }
                    if (!member.cuilFiles.isEmpty()) {
                        for (AttachFile cuilFile : member.cuilFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", cuilFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_CUIL_FILE);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }
                    if (!member.actaMatrimonioFiles.isEmpty()) {
                        for (AttachFile actaFile : member.actaMatrimonioFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", actaFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_ACTA_MATRIMONIO);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }
                    if (!member.partidaNacimientoFiles.isEmpty()) {
                        for (AttachFile partNacFile : member.partidaNacimientoFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", partNacFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_PART_NACIMIENTO);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }

                    // MONOTRIBUTO CONYUGE  ONLY
                    if (!member.conyugeForm184Files.isEmpty()) {
                        for (AttachFile formFile : member.conyugeForm184Files) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", formFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_CONYUGE_FORM_184);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }

                    if (!member.conyugeThreeMonthFiles.isEmpty()) {
                        for (AttachFile monthFile : member.conyugeThreeMonthFiles) {
                            JSONObject jDocument = new JSONObject();

                            jDocument.put("documento_id", monthFile.id);
                            jDocument.put("tipo_documento_asociado_id", ConstantsUtil.DOC_ASSOCIATED_CONYUGE_3_MONTH_TICKET);
                            jMembersDocumentsArray.put(jDocument);
                        }
                    }

                    jMember.put("documentos", jMembersDocumentsArray);

                    jMember.put("lista_domicilios", new JSONArray());
                    jMember.put("lista_telefonos", new JSONArray());
                    jMember.put("lista_mails", new JSONArray());

                    jPersonasArray.put(jMember);
                }
            }

            json.put("personas", jPersonasArray);


            if (affiliationCard.ticketPago != null ) {
                JSONObject jsonTicketPago = getTicketPagoJSON(affiliationCard.ticketPago);
                json.put("pago_anticipado", jsonTicketPago);
            }

            if (affiliationCard.additionalData2 != null ) {

                if (affiliationCard.additionalData2.getSufTitular() != null) {
                    BeneficiarioSUF sufTitular = affiliationCard.additionalData2.getSufTitular();

                    if (sufTitular != null) {
                        JSONObject jsonSufTitular = getBeneficiarioSufJSON(sufTitular);
                        json.put("suf_titular", jsonSufTitular);
                    } else {
                        json.put("suf_titular", JSONObject.NULL);
                    }
                }

                if (affiliationCard.additionalData2.getSufConyuge() != null) {
                    BeneficiarioSUF sufConyuge = affiliationCard.additionalData2.getSufConyuge();

                    if (sufConyuge != null) {
                        JSONObject jsonSufConyuge = getBeneficiarioSufJSON(sufConyuge);
                        json.put("suf_conyuge", jsonSufConyuge);
                    } else {
                        json.put("suf_conyuge", JSONObject.NULL);
                    }

                }
            }

            Log.e(TAG, "SEND JSON: " + json.toString());
            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG, "Error UPDATING CARD ");
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getTicketPagoJSON(TicketPago ticket) throws Exception {
        JSONObject json = new JSONObject();

        // Check empty mandatory data for service
        if (ticket.ticketNumber == null || ticket.pagoDate == null) {
            return null;
        } else {

            if (ticket.formaAlta != null) {
                json.put("tipo_alta", ticket.formaAlta.id);

                Date today = new Date();
                if (ticket.formaAlta.id.equals(ConstantsUtil.ALTA_INMEDIATA)) {
                    json.put("fecha_alta", ParserUtils.parseDate(today, "yyyy-MM-dd"));
                } else {
                    // ALTA FIRST DAY FROM NEXT MONTH
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    c.add(Calendar.MONTH, 1);
                    c.set(Calendar.DATE, 1);

                    Date nextDate = c.getTime();
                    json.put("fecha_alta", ParserUtils.parseDate(nextDate, "yyyy-MM-dd"));
                }

            } else {
                json.put("tipo_alta", JSONObject.NULL);
                json.put("fecha_alta", JSONObject.NULL);
            }

            json.put("valor_plan", ticket.planValue);

            if (ticket.ticketNumber != null) {
                json.put("numero_ticket", ticket.ticketNumber);
            } else {
                json.put("numero_ticket", 0);
            }

            if (ticket.pagoDate != null) {
                json.put("fecha_pago", ParserUtils.parseDate(ticket.pagoDate, "yyyy-MM-dd"));
            }

            json.put("importe", ticket.importe);
            if (ticket.desNumber != null && !ticket.desNumber.isEmpty()) {
                json.put("numero_des", Long.valueOf(ticket.desNumber));
            }

            if (ticket.ticketPagoFiles != null && ticket.ticketPagoFiles.size() > 0) {
                JSONArray array = new JSONArray();
                for (AttachFile f : ticket.ticketPagoFiles) {
                    JSONObject obj = new JSONObject();
                    obj.put("archivo_id", f.id);
                    obj.put("comentario", "");
                    array.put(obj);
                }
                json.put("adjuntos", array);
            }
            return json;
        }
    }

    private JSONArray getBeneficiarioSufJSON(List<BeneficiarioSUF> array) throws Exception {
        if (array == null)
            return null;

        JSONArray jsonArray = new JSONArray();
        for (BeneficiarioSUF suf : array) {
            JSONObject json = new JSONObject();
            if (suf.docType != null)
                json.put("tipo_documento_id", suf.docType.id);

            if (suf.dni != 0)
                json.put("numero_documento", suf.dni);

            if (suf.birthday != null)
                json.put("fecha_nacimiento", ParserUtils.parseDate(suf.birthday, "yyyy-MM-dd"));

            if (suf.lastname != null)
                json.put("apellido", suf.lastname);

            if (suf.firstname != null)
                json.put("nombre", suf.firstname);

            // Check empty data
            if (json.length() != 0) {
                jsonArray.put(json);
            }
        }

        return jsonArray;
    }

    private JSONObject getBeneficiarioSufJSON(BeneficiarioSUF suf) throws Exception {
        JSONObject json = new JSONObject();

        if (suf.docType != null)
            json.put("tipo_documento_id", suf.docType.id);

        if (suf.dni != 0)
            json.put("numero_documento", suf.dni);

        if (suf.birthday != null)
            json.put("fecha_nacimiento", ParserUtils.parseDate(suf.birthday, "yyyy-MM-dd"));

        if (suf.lastname != null)
            json.put("apellido", suf.lastname);

        if (suf.firstname != null)
            json.put("nombre", suf.firstname);

        return json;
    }


    @Override
    protected AffiliationDataResult parseObject(Object obj) {

        AffiliationDataResult dataResult = null;

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e(TAG, "RESULT SAVED CARD!::: " + dic.toString());

                    dataResult = new AffiliationDataResult();
                    dataResult.cardId = dic.optLong("ficha_id", -1L);
                    dataResult.desId = dic.optLong("des_id", -1L);

                    try {
                        JSONArray jPersonasArray = dic.getJSONArray("personas");
                        for (int i = 0; i < jPersonasArray.length(); i++) {
                            JSONObject jPersona = jPersonasArray.optJSONObject(i);

                            Person person = new Person();
                            person.personCardId = jPersona.optLong("ficha_persona_id", -1L);
                            person.dni = jPersona.optLong("numero_documento", -1L);

                            dataResult.personList.add(person);
                        }
                    } catch (JSONException jEx) {
                    }

                    // Ej resultado Save
                    //{"des_id":141,"ficha_id":230,"personas":[{"ficha_persona_id":84,"numero_documento":25633007}]


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return dataResult;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
