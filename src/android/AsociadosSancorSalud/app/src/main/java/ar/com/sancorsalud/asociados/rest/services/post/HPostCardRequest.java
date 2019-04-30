package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.ComprobanteCBUItem;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.BeneficiarioSUF;
import ar.com.sancorsalud.asociados.model.affiliation.ContactData;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.TicketPago;
import ar.com.sancorsalud.asociados.model.affiliation.Address;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.affiliation.TitularDoc;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

import static ar.com.sancorsalud.asociados.utils.ConstantsUtil.EF_FORMA_PAGO;


public class HPostCardRequest extends HRequest<AffiliationCard> {

    private static final String TAG = "HPOST_CARD";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_CARD_GET;

    private long affiliationCardId;

    public HPostCardRequest(long affiliationCardId, Response.Listener<AffiliationCard> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.affiliationCardId = affiliationCardId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", affiliationCardId);
            Log.e(TAG, "HPostCardRequest SEND JSON: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected AffiliationCard parseObject(Object obj) {

        AffiliationCard card = null;

        List<AttachFile> conyugeForm184Files = new ArrayList<AttachFile>();
        List<AttachFile> conyugeThreeMonthFiles = new ArrayList<AttachFile>();

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {

                    //  grupo_afinidad_id
                    Log.e(TAG, "RESULT GET CARD!::: " + dic.toString());
                    card = new AffiliationCard();
                    card.members = new ArrayList<Member>();

                    JSONObject jCard = dic.optJSONObject("ficha");
                    card.id = jCard.optLong("id", -1);
                    card.idCotizacion = jCard.optLong("cotizacion_id", -1);

                    card.titularData = new TitularData();
                    card.titularData.needAuditoria = ParserUtils.optString(jCard, "requiere_auditoria") != null ? ParserUtils.optString(jCard, "requiere_auditoria").trim() : null;

                    card.titularData.documentoAfinidadId = jCard.optLong("documento_afinidad_id", -1L);

                    card.titularData.documentoAgreementId = jCard.optLong("documento_convenio_id", -1L);

                    String fechaCarga = ParserUtils.optString(jCard, "fecha_carga") != null ? ParserUtils.optString(jCard, "fecha_carga").trim() : null;
                    if (fechaCarga != null) {
                        card.titularData.fechaCarga = ParserUtils.parseDate(fechaCarga, "yyyy-MM-dd");
                    }

                    card.titularData.promotorId = jCard.optLong("promotor_id", -1L);

                    String segmentoId = ParserUtils.optString(jCard, "segmento_id") != null ? ParserUtils.optString(jCard, "segmento_id").trim() : null;
                    if (segmentoId != null) {
                        card.titularData.segmento = new QuoteOption(segmentoId, QuoteOptionsController.getInstance().getSegmentoName(segmentoId));
                    }

                    // String afinidadId = ParserUtils.optString(jCard, "afinidad_id") != null ? ParserUtils.optString(jCard, "afinidad_id").trim() : null;
                    String afinidadId = ParserUtils.optString(jCard, "grupo_afinidad_id") != null ? ParserUtils.optString(jCard, "grupo_afinidad_id").trim() : null;
                    String afinidadDescription = ParserUtils.optString(jCard, "afinidad_descripcion") != null ? ParserUtils.optString(jCard, "afinidad_descripcion").trim() : null;
                    if (afinidadId != null) {
                        card.titularData.nombreAfinidad = new QuoteOption(afinidadId, afinidadDescription);
                    }
                    String formaIngresoId = ParserUtils.optString(jCard, "formadeingreso_id") != null ? ParserUtils.optString(jCard, "formadeingreso_id").trim() : null;
                    if (formaIngresoId != null) {
                        card.titularData.formaIngreso = new QuoteOption(formaIngresoId, QuoteOptionsController.getInstance().getFormaIngresoName(formaIngresoId));
                    }
                    String fechaInicioServicio = ParserUtils.optString(jCard, "fecha_inicio_servicio") != null ? ParserUtils.optString(jCard, "fecha_inicio_servicio").trim() : null;
                    if (fechaInicioServicio != null) {
                        card.titularData.fechaInicioServicio = ParserUtils.parseDate(fechaInicioServicio, "yyyy-MM-dd");
                    }
                    String coberturaProvenienteId = ParserUtils.optString(jCard, "obra_social_proveniente_id") != null ? ParserUtils.optString(jCard, "obra_social_proveniente_id").trim() : null;
                    String coberturaProvenienteDescription = ParserUtils.optString(jCard, "obra_social_proveniente_descripcion") != null ? ParserUtils.optString(jCard, "obra_social_proveniente_descripcion").trim() : null;
                    if (coberturaProvenienteId != null) {
                        card.titularData.coberturaProveniente = new QuoteOption(coberturaProvenienteId, coberturaProvenienteDescription);
                    }

                    String empresaId = ParserUtils.optString(jCard, "empresa_factura_id") != null ? ParserUtils.optString(jCard, "empresa_factura_id").trim() : null;
                    String entidadDescription = ParserUtils.optString(jCard, "afinidad_descripcion") != null ? ParserUtils.optString(jCard, "afinidad_descripcion").trim() : null;
                    if (empresaId != null) {
                        card.titularData.nombreEmpresa = new QuoteOption(empresaId, entidadDescription);
                    }
                    String entityId = ParserUtils.optString(jCard, "entidad_numero") != null ? ParserUtils.optString(jCard, "entidad_numero").trim() : null;
                    if (entityId != null) {
                        card.titularData.entity = new QuoteOption(entityId, null);
                    }

                    String datero = ParserUtils.optString(jCard, "datero") != null ? ParserUtils.optString(jCard, "datero").trim() : null;
                    if (datero != null) {
                        card.titularData.dateroNumber = new QuoteOption(datero, null);
                    }

                    String categoria = ParserUtils.optString(jCard, "categoria_id") != null ? ParserUtils.optString(jCard, "categoria_id").trim() : null;
                    String categoriaId = "";
                    if (categoria != null) {
                        if (categoria.equals("A")) {
                            categoriaId = ConstantsUtil.ACTIVO_CATEGORIA;
                        } else if (categoria.equals("D")) {
                            categoriaId = ConstantsUtil.ADHERENTE_CATEGORIA;
                        }

                        card.titularData.categoria = new QuoteOption(categoriaId, QuoteOptionsController.getInstance().getCategoriaName(categoriaId));
                    }

                    //card.stateId =  jCard.optInt("estado_id", -1);

                    // PERSONAS
                    try {
                        JSONArray jPersonasArray = jCard.getJSONArray("personas");
                        if (jPersonasArray != null) {
                            card.cantMembers = jPersonasArray.length();

                            for (int i = 0; i < jPersonasArray.length(); i++) {
                                JSONObject jPersona = jPersonasArray.optJSONObject(i);

                                Long personCardId = jPersona.optLong("ficha_persona_id", -1L);

                                String parentescoId = ParserUtils.optString(jPersona, "parentesco_id") != null ? ParserUtils.optString(jPersona, "parentesco_id").trim() : null;
                                QuoteOption parentesco = null;
                                if (parentescoId != null) {
                                    parentesco = new QuoteOption(parentescoId, QuoteOptionsController.getInstance().getParentescoName(parentescoId));
                                }
                                String sexoId = ParserUtils.optString(jPersona, "sexo_id") != null ? ParserUtils.optString(jPersona, "sexo_id").trim() : null;
                                QuoteOption sex = null;
                                if (sexoId != null) {
                                    sex = new QuoteOption(sexoId, QuoteOptionsController.getInstance().getSexoName(sexoId));
                                }

                                Integer age = jPersona.optInt("edad", -1);

                                String docTypeId = ParserUtils.optString(jPersona, "tipo_documento_id") != null ? ParserUtils.optString(jPersona, "tipo_documento_id").trim() : null;
                                QuoteOption docType = null;
                                if (docTypeId != null) {
                                    docType = new QuoteOption(docTypeId, QuoteOptionsController.getInstance().getDocTypeName(docTypeId));
                                }

                                Long dni = jPersona.optLong("numero_documento", -1L);
                                String birthday = ParserUtils.optString(jPersona, "fecha_nacimiento") != null ? ParserUtils.optString(jPersona, "fecha_nacimiento").trim() : null;

                                String civilStatusId = ParserUtils.optString(jPersona, "estado_civil_id") != null ? ParserUtils.optString(jPersona, "estado_civil_id").trim() : null;
                                QuoteOption civilStatus = null;
                                if (civilStatusId != null) {
                                    civilStatus = new QuoteOption(civilStatusId, QuoteOptionsController.getInstance().getCivilStatusName(civilStatusId));
                                }
                                String firstname = ParserUtils.optString(jPersona, "nombre") != null ? ParserUtils.optString(jPersona, "nombre").trim() : null;
                                String lastname = ParserUtils.optString(jPersona, "apellido") != null ? ParserUtils.optString(jPersona, "apellido").trim() : null;

                                int discapacidadId = jPersona.optInt("discapacidad_id", 0);

                                String condicionIvaId = ParserUtils.optString(jPersona, "condicion_iva_id") != null ? ParserUtils.optString(jPersona, "condicion_iva_id").trim() : null;
                                QuoteOption condicionIva = null;
                                if (condicionIvaId != null) {
                                    condicionIva = new QuoteOption(condicionIvaId, QuoteOptionsController.getInstance().getCondicionIvaName(condicionIvaId));
                                }

                                String cuil = ParserUtils.optString(jPersona, "cuil") != null ? ParserUtils.optString(jPersona, "cuil").trim() : null;

                                String nationalityId = ParserUtils.optString(jPersona, "nacionalidad_id") != null ? ParserUtils.optString(jPersona, "nacionalidad_id").trim() : null;
                                QuoteOption nationality = null;
                                if (nationalityId != null) {
                                    nationality = new QuoteOption(nationalityId, QuoteOptionsController.getInstance().getNationalityName(nationalityId));
                                }

                                boolean existent = jPersona.optBoolean("existente", false);
                                boolean isBeneficiarioSUF = jPersona.optBoolean("beneficiario_suf", false);
                                boolean aportaMonotributo = jPersona.optBoolean("aportaMonotributo", false);

                                // TITULAR MEMBER
                                if (parentescoId.equals(ConstantsUtil.TITULAR_MEMBER)) {

                                    card.titularData.personCardId = personCardId;
                                    card.titularData.parentesco = parentesco;
                                    card.titularData.sex = sex;
                                    card.titularData.age = age;
                                    card.titularData.docType = docType;
                                    card.titularData.dni = dni;
                                    card.titularData.birthday = ParserUtils.parseDate(birthday, "yyyy-MM-dd");
                                    card.titularData.civilStatus = civilStatus;
                                    card.titularData.firstname = firstname;
                                    card.titularData.lastname = lastname;
                                    card.titularData.condicionIva = condicionIva;
                                    //card.titularData.hasDisability = (discapacidadId == 0) ? false : true;
                                    card.titularData.cuil = cuil;
                                    card.titularData.nationality = nationality;
                                    card.titularData.beneficiarioSUF = isBeneficiarioSUF;
                                    card.titularData.aportaMonotributo = aportaMonotributo;
                                    card.titularData.existent = existent;


                                    // Contact DATA
                                    card.contactData = new ContactData();
                                    card.contactData.addInvoice = ParserUtils.optString(jPersona, "adhiere_efactura") != null ? (ParserUtils.optString(jPersona, "adhiere_efactura").trim().equals("S") ? true : false) : null;

                                    // LISTA  TELEFONOS  //
                                    try {
                                        JSONArray jTitularContactArray = jPersona.getJSONArray("lista_telefonos");
                                        if (jTitularContactArray.length() > 0) {
                                            for (int l = 0; l < jTitularContactArray.length(); l++) {

                                                JSONObject jContact = jTitularContactArray.optJSONObject(l);
                                                if (l == 0) {

                                                    card.contactData.areaPhone = ParserUtils.optString(jContact, "caracteristica") != null ? ParserUtils.optString(jContact, "caracteristica").trim() : null;
                                                    card.contactData.phone = ParserUtils.optString(jContact, "numero") != null ? ParserUtils.optString(jContact, "numero").trim() : null;
                                                    // tipo_id: 1
                                                    //es_principal": "S",
                                                }
                                                if (l == 1) {
                                                    card.contactData.areaDevice = ParserUtils.optString(jContact, "caracteristica") != null ? ParserUtils.optString(jContact, "caracteristica").trim() : null;
                                                    card.contactData.device = ParserUtils.optString(jContact, "numero") != null ? ParserUtils.optString(jContact, "numero").trim() : null;
                                                    // tipo_id: 1
                                                    //es_principal": "S",
                                                }
                                            }
                                        }
                                    } catch (JSONException jEx) {
                                    }

                                    // LISTA  MAILS  //
                                    try {
                                        JSONArray jTitularMailsArray = jPersona.getJSONArray("lista_mails");
                                        if (jTitularMailsArray.length() > 0) {
                                            // ONE MAIL
                                            JSONObject jMail = jTitularMailsArray.optJSONObject(0);
                                            // tipo_id: 1
                                            card.contactData.email = ParserUtils.optString(jMail, "mail") != null ? ParserUtils.optString(jMail, "mail").trim() : null;
                                        }
                                    } catch (JSONException jEx) {
                                    }

                                    // LISTA  DOMICILIOS  //
                                    try {
                                        JSONArray jAddressArray = jPersona.getJSONArray("lista_domicilios");
                                        if (jAddressArray.length() > 0) {

                                            for (int s = 0; s < jAddressArray.length(); s++) {
                                                JSONObject jAddress = jAddressArray.optJSONObject(s);
                                                if (jAddress != null) {
                                                    int tipoDomicilio = jAddress.optInt("tipo_domicilio_id", -1);

                                                    if (tipoDomicilio == 1) {
                                                        // TITULAR ADDRESS
                                                        card.titularAddress = new Address();
                                                        card.titularAddress.street = ParserUtils.optString(jAddress, "calle") != null ? ParserUtils.optString(jAddress, "calle").trim() : null;
                                                        card.titularAddress.number = jAddress.optInt("numero", -1);

                                                        String orientationId = ParserUtils.optString(jAddress, "orientacion") != null ? ParserUtils.optString(jAddress, "orientacion").trim() : null;
                                                        if (orientationId != null) {
                                                            card.titularAddress.orientation = new QuoteOption(orientationId, QuoteOptionsController.getInstance().getOrientationName(orientationId));
                                                        }

                                                        card.titularAddress.floor = jAddress.optInt("piso", -1);
                                                        card.titularAddress.dpto = ParserUtils.optString(jAddress, "departamento") != null ? ParserUtils.optString(jAddress, "departamento").trim() : null;

                                                        String adressCode1Id = ParserUtils.optString(jAddress, "domicilio_atributo_codigo1") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_codigo1").trim() : null;
                                                        if (adressCode1Id != null) {
                                                            card.titularAddress.adressCode1 = new QuoteOption(adressCode1Id, QuoteOptionsController.getInstance().getAddressAtributeName(adressCode1Id));
                                                        }
                                                        card.titularAddress.adressCode1Description = ParserUtils.optString(jAddress, "domicilio_atributo_descripcion1") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_descripcion1").trim() : null;

                                                        String adressCode2Id = ParserUtils.optString(jAddress, "domicilio_atributo_codigo2") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_codigo2").trim() : null;
                                                        if (adressCode2Id != null) {
                                                            card.titularAddress.adressCode2 = new QuoteOption(adressCode2Id, QuoteOptionsController.getInstance().getAddressAtributeName(adressCode2Id));
                                                        }
                                                        card.titularAddress.adressCode2Description = ParserUtils.optString(jAddress, "domicilio_atributo_descripcion2") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_descripcion2").trim() : null;

                                                        card.titularAddress.barrio = ParserUtils.optString(jAddress, "barrio") != null ? ParserUtils.optString(jAddress, "barrio").trim() : null;
                                                        card.titularAddress.zipCode = ParserUtils.optString(jAddress, "cp") != null ? ParserUtils.optString(jAddress, "cp").trim() : null;
                                                        // Not in use CODE
                                                        //card.titularAddress.code = ParserUtils.optString(jAddress, "sufijo_cpa") != null ? ParserUtils.optString(jAddress, "sufijo_cpa").trim() : null;
                                                    } else if (tipoDomicilio == 2) {
                                                        // ALTERNATIVE ADDRESS
                                                        card.contactData.alternativeAddress = new Address();
                                                        card.contactData.alternativeAddress.street = ParserUtils.optString(jAddress, "calle") != null ? ParserUtils.optString(jAddress, "calle").trim() : null;
                                                        card.contactData.alternativeAddress.number = jAddress.optInt("numero", -1);

                                                        String orientationId = ParserUtils.optString(jAddress, "orientacion") != null ? ParserUtils.optString(jAddress, "orientacion").trim() : null;
                                                        if (orientationId != null) {
                                                            card.contactData.alternativeAddress.orientation = new QuoteOption(orientationId, QuoteOptionsController.getInstance().getOrientationName(orientationId));
                                                        }

                                                        card.contactData.alternativeAddress.floor = jAddress.optInt("piso", -1);
                                                        card.contactData.alternativeAddress.dpto = ParserUtils.optString(jAddress, "departamento") != null ? ParserUtils.optString(jAddress, "departamento").trim() : null;

                                                        String adressCode1Id = ParserUtils.optString(jAddress, "domicilio_atributo_codigo1") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_codigo1").trim() : null;
                                                        if (adressCode1Id != null) {
                                                            card.contactData.alternativeAddress.adressCode1 = new QuoteOption(adressCode1Id, QuoteOptionsController.getInstance().getAddressAtributeName(adressCode1Id));
                                                        }
                                                        card.contactData.alternativeAddress.adressCode1Description = ParserUtils.optString(jAddress, "domicilio_atributo_descripcion1") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_descripcion1").trim() : null;

                                                        String adressCode2Id = ParserUtils.optString(jAddress, "domicilio_atributo_codigo2") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_codigo2").trim() : null;
                                                        if (adressCode2Id != null) {
                                                            card.contactData.alternativeAddress.adressCode2 = new QuoteOption(adressCode2Id, QuoteOptionsController.getInstance().getAddressAtributeName(adressCode2Id));
                                                        }
                                                        card.contactData.alternativeAddress.adressCode2Description = ParserUtils.optString(jAddress, "domicilio_atributo_descripcion2") != null ? ParserUtils.optString(jAddress, "domicilio_atributo_descripcion2").trim() : null;

                                                        card.contactData.alternativeAddress.barrio = ParserUtils.optString(jAddress, "barrio") != null ? ParserUtils.optString(jAddress, "barrio").trim() : null;
                                                        card.contactData.alternativeAddress.zipCode = ParserUtils.optString(jAddress, "cp") != null ? ParserUtils.optString(jAddress, "cp").trim() : null;
                                                        // Not in use CODE
                                                        //card.contactData.alternativeAddress.code = ParserUtils.optString(jAddress, "sufijo_cpa") != null ? ParserUtils.optString(jAddress, "sufijo_cpa").trim() : null;
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException jEx) {
                                    }

                                    try {
                                        // Lista DOCUMENTOS
                                        JSONArray jDocumentosArray = jPersona.getJSONArray("documentos");
                                        if (jDocumentosArray != null && jDocumentosArray.length() > 0) {
                                            card.document = new TitularDoc();

                                            for (int j = 0; j < jDocumentosArray.length(); j++) {
                                                JSONObject jDocument = jDocumentosArray.optJSONObject(j);

                                                AttachFile attachFile = new AttachFile();
                                                attachFile.id = jDocument.optLong("documento_id");
                                                int type = jDocument.optInt("tipo_documento_asociado_id", -1);

                                                switch (type) {
                                                    case ConstantsUtil.DOC_ASSOCIATED_DNI_FILE:
                                                        card.document.dniFrontFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_CUIL_FILE:
                                                        card.document.cuilFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_IVA_FILE:
                                                        card.document.ivaFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_COVERAGE:
                                                        card.document.coverageFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_PLAN:
                                                        card.document.planFiles.add(attachFile);
                                                        break;

                                                    // Pass this information to Monotributo section
                                                    case ConstantsUtil.DOC_ASSOCIATED_FORM_184:
                                                        card.document.form184Files.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_3_MONTH_TICKET:
                                                        card.document.threeMonthFiles.add(attachFile);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    } catch (JSONException jEx) {
                                    }

                                } else {

                                    //  OTHERS MEMBERS
                                    Member member = new Member();

                                    member.personCardId = personCardId;
                                    member.hasPersonCardId = true;
                                    member.parentesco = parentesco;
                                    member.sex = sex;
                                    member.age = age;
                                    member.docType = docType;
                                    member.dni = dni;
                                    member.birthday = ParserUtils.parseDate(birthday, "yyyy-MM-dd");
                                    member.firstname = firstname;
                                    member.lastname = lastname;
                                    member.cuil = cuil;
                                    member.hasDisability = (discapacidadId == 0) ? false : true;
                                    member.nationality = nationality;
                                    //member.addInvoice = false;
                                    member.beneficiarioSUF = isBeneficiarioSUF;
                                    member.aportaMonotributo = aportaMonotributo;
                                    member.existent = existent;


                                    // MEMBER DOCUMENTOS
                                    try {
                                        JSONArray jDocumentosArray = jPersona.getJSONArray("documentos");
                                        if (jDocumentosArray != null && jDocumentosArray.length() > 0) {
                                            for (int k = 0; k < jDocumentosArray.length(); k++) {
                                                JSONObject jDocument = jDocumentosArray.optJSONObject(k);

                                                AttachFile attachFile = new AttachFile();
                                                attachFile.id = jDocument.optLong("documento_id");
                                                int type = jDocument.optInt("tipo_documento_asociado_id", -1);

                                                switch (type) {
                                                    case ConstantsUtil.DOC_ASSOCIATED_DNI_FILE:
                                                        member.dniFrontFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_CUIL_FILE:
                                                        member.cuilFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_PART_NACIMIENTO:
                                                        member.partidaNacimientoFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_ACTA_MATRIMONIO:
                                                        member.actaMatrimonioFiles.add(attachFile);
                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_CERT_DISC_FILE:
                                                        member.certDiscapacidadFiles.add(attachFile);
                                                        break;
                                                    // Pass this information to Conyuge Monotributo section
                                                    case ConstantsUtil.DOC_ASSOCIATED_CONYUGE_FORM_184:
                                                        conyugeForm184Files.add(attachFile);
                                                        member.conyugeForm184Files = conyugeForm184Files;

                                                        break;
                                                    case ConstantsUtil.DOC_ASSOCIATED_CONYUGE_3_MONTH_TICKET:
                                                        conyugeThreeMonthFiles.add(attachFile);
                                                        member.conyugeThreeMonthFiles = conyugeThreeMonthFiles;
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    } catch (JSONException jEx) {
                                    }
                                    card.members.add(member);
                                }
                            }
                        } else {
                            card.cantMembers = 0;
                        }
                    } catch (JSONException jEx) {
                        card.cantMembers = 0;
                    }


                    // FORMA DE PAGO
                    Pago pago = parsePago(jCard.optJSONObject("forma_pago"), card.id);

                    pago = parseCopago(pago, jCard.optJSONObject("forma_pago_copago"), card.id);

                    // COPAGO
                    String copagos = ParserUtils.optString(jCard, "copagos") != null ? ParserUtils.optString(jCard, "copagos").trim() : null;

                    // TICKET DE PAGO
                    TicketPago ticketPago = parseTicketPago(jCard.optJSONObject("pago_anticipado"));
                    if (ticketPago != null) {
                        card.ticketPago = ticketPago;
                    }

                    // BSUF
                    BeneficiarioSUF sufTitular = parseBeneficiarioSUF(jCard.optJSONObject("suf_titular"));
                    BeneficiarioSUF sufConyuge = parseBeneficiarioSUF(jCard.optJSONObject("suf_conyuge"));

                    Date inicioServicio = ParserUtils.parseDate(jCard, "fecha_inicio_servicio", "yyyy-MM-dd");
                    // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity

                    // TITULAR OBRA SOCIAL
                    ObraSocial obraSocial = parseObraSocial(ConstantsUtil.DESREGULADO_SEGMENTO ,jCard.optJSONObject("obra_social"), card.id);
                    // TITULAR APORTA MONOTRIBUTO
                    ObraSocial osMonotributo = parseObraSocial(ConstantsUtil.MONOTRIBUTO_SEGMENTO, jCard.optJSONObject("os_monotributo"), card.id);

                    String conyugeSegmentoId = ParserUtils.optString(jCard, "segmento_conyuge_id") != null ? ParserUtils.optString(jCard, "segmento_conyuge_id").trim() : null;
                    QuoteOption conyugeSegmento = null;
                    if (conyugeSegmentoId != null) {
                        conyugeSegmento = new QuoteOption(conyugeSegmentoId, QuoteOptionsController.getInstance().getSegmentoName(conyugeSegmentoId));
                    }

                    // CONYUGE OBRA SOCIAL
                    ObraSocial conyugeObraSocial = parseObraSocial(ConstantsUtil.DESREGULADO_SEGMENTO , jCard.optJSONObject("obra_social_conyuge"), card.id);
                    // CONYUGE APORTA MONOTRIBUTO
                    ObraSocial conyugeOSMonotributo = parseObraSocial(ConstantsUtil.MONOTRIBUTO_SEGMENTO, jCard.optJSONObject("os_monotributo_conyuge"), card.id);


                    // LOGIC: CHECK SEGMENTO AND INGRESO TO CREATE ADDITIONAL DATA
                    ConstantsUtil.Segmento seg = card.titularData.getSegmento();
                    ConstantsUtil.FormaIngreso ingreso = card.titularData.getFormaIngreso();

                    if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
                        AdditionalData1AutonomoIndividual additionalData1 = new AdditionalData1AutonomoIndividual();
                        additionalData1.pago = pago;

                        card.additionalData1 = additionalData1;

                        AdditionalData2AutonomoIndividual additionalData2 = new AdditionalData2AutonomoIndividual();
                        additionalData2.sufTitular = sufTitular;
                        additionalData2.sufConyuge = sufConyuge;
                        additionalData2.inicioServicioDate = inicioServicio;

                        card.additionalData2 = additionalData2;

                    } else if ((seg == ConstantsUtil.Segmento.AUTONOMO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
                        AdditionalData1AutonomoEmpresa additionalData1 = new AdditionalData1AutonomoEmpresa();
                        additionalData1.pago = pago;
                        additionalData1.copagos = copagos;

                        card.additionalData1 = additionalData1;

                        AdditionalData2AutonomoEmpresa additionalData2 = new AdditionalData2AutonomoEmpresa();
                        additionalData2.sufTitular = sufTitular;
                        additionalData2.sufConyuge = sufConyuge;
                        additionalData2.inicioServicioDate = inicioServicio;

                        card.additionalData2 = additionalData2;

                    } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
                        AdditionalData1DesreguladoIndividual additionalData1 = new AdditionalData1DesreguladoIndividual();
                        additionalData1.pago = pago;

                        // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
                        card.additionalData1 = additionalData1;

                        AdditionalData2DesreguladoIndividual additionalData2 = new AdditionalData2DesreguladoIndividual();
                        additionalData2.obraSocial = obraSocial;
                        card.additionalData2 = additionalData2;

                        // Titular cond monotributo
                        AdditionalData3DesreguladoIndividual additionalData3 = new AdditionalData3DesreguladoIndividual();
                        additionalData3.osMonotributo = osMonotributo;
                        card.additionalData3 = additionalData3;

                        // Conyuge DATA
                        card.conyugeData = fillConyugeData(conyugeSegmento, conyugeObraSocial, conyugeOSMonotributo);


                    } else if ((seg == ConstantsUtil.Segmento.DESREGULADO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {
                        AdditionalData1DesreguladoEmpresa additionalData1 = new AdditionalData1DesreguladoEmpresa();
                        additionalData1.pago = pago;
                        additionalData1.copagos = copagos;

                        // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
                        card.additionalData1 = additionalData1;

                        AdditionalData2DesreguladoEmpresa additionalData2 = new AdditionalData2DesreguladoEmpresa();
                        additionalData2.obraSocial = obraSocial;
                        card.additionalData2 = additionalData2;

                        // check Titular cond monotributo
                        AdditionalData3DesreguladoEmpresa additionalData3 = new AdditionalData3DesreguladoEmpresa();
                        additionalData3.osMonotributo = osMonotributo;
                        card.additionalData3 = additionalData3;

                        // Conyuge DATA
                        card.conyugeData = fillConyugeData(conyugeSegmento, conyugeObraSocial, conyugeOSMonotributo);


                    } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.INDIVIDUAL || ingreso == ConstantsUtil.FormaIngreso.AFINIDAD)) {
                        AdditionalData1MonotributoIndividual additionalData1 = new AdditionalData1MonotributoIndividual();
                        additionalData1.pago = pago;

                        // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
                        card.additionalData1 = additionalData1;

                        AdditionalData2MonotributoIndividual additionalData2 = new AdditionalData2MonotributoIndividual();
                        // add monotributo files
                        if (card.document != null) {
                            // titular monotributo files
                            additionalData2.form184Files = card.document.form184Files;
                            additionalData2.threeMonthFiles = card.document.threeMonthFiles;
                        }

                        // osMonotributo
                        additionalData2.obraSocial = osMonotributo;
                        card.additionalData2 = additionalData2;

                        // Conyuge DATA
                        card.conyugeData = fillConyugeData(conyugeSegmento, conyugeObraSocial, conyugeOSMonotributo);

                    } else if ((seg == ConstantsUtil.Segmento.MONOTRIBUTO) && (ingreso == ConstantsUtil.FormaIngreso.EMPRESA)) {

                        AdditionalData1MonotributoEmpresa additionalData1 = new AdditionalData1MonotributoEmpresa();
                        additionalData1.pago = pago;
                        additionalData1.copagos = copagos;

                        // EE array will be load after saving the first card screen, it will be loaded in Affiliation Activity
                        card.additionalData1 = additionalData1;

                        AdditionalData2MonotributoEmpresa additionalData2 = new AdditionalData2MonotributoEmpresa();
                        // add monotributo files
                        if (card.document != null) {
                            // titular monotributo files
                            additionalData2.form184Files = card.document.form184Files;
                            additionalData2.threeMonthFiles = card.document.threeMonthFiles;
                        }

                        additionalData2.obraSocial = osMonotributo;
                        card.additionalData2 = additionalData2;

                        // Conyuge DATA
                        card.conyugeData = fillConyugeData(conyugeSegmento, conyugeObraSocial, conyugeOSMonotributo);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "ERROR PARSING CARD : " + e.getMessage());
                    card = null;
                }
            }
        }

        return card;
    }

    private ConyugeData fillConyugeData(QuoteOption segmento, ObraSocial conyugeObraSocial, ObraSocial conyugeOSMonotributo) {
        ConyugeData conyugeData = new ConyugeData();
        conyugeData.segmento = segmento;

        if (conyugeObraSocial != null) {
            conyugeData.obraSocial = conyugeObraSocial;
        }
        if (conyugeOSMonotributo != null) {
            conyugeData.osMonotributo = conyugeOSMonotributo;
        }

        return conyugeData;
    }


    private Pago parsePago(JSONObject jFormaPago, long cardId) {
        // FORMA DE PAGO

        Pago pago = null;
        if (jFormaPago != null) {

            pago = new Pago();
            pago.id = jFormaPago.optLong("id", -1);
            pago.cardId = cardId;

            // TODO nuevo C or S
            //String typeId = ParserUtils.optString(jFormaPago, "tipo") != null ? ParserUtils.optString(jFormaPago, "tipo").trim() : null;

            String formaPagoId = ParserUtils.optString(jFormaPago, "forma_pago_id") != null ? ParserUtils.optString(jFormaPago, "forma_pago_id").trim() : null;
            if (formaPagoId != null) {
                // for EMP  this will be replace by new QuoteOption(formaPagoId,formaPagoId); because its from extra data
                // so  getFormaPagoNAme will return empty data and will not be used
                if (formaPagoId.equals(EF_FORMA_PAGO)){
                    pago.formaPago = new QuoteOption(formaPagoId, formaPagoId);
                } else {
                    pago.formaPago = new QuoteOption(formaPagoId, QuoteOptionsController.getInstance().getFormaPagoName(formaPagoId));
                }
            }

            // TC : CREDIT CARD
            String cardTypeId = ParserUtils.optString(jFormaPago, "tarjeta_id") != null ? ParserUtils.optString(jFormaPago, "tarjeta_id").trim() : null;
            if (cardTypeId != null) {
                pago.cardType = new QuoteOption(cardTypeId, QuoteOptionsController.getInstance().getTarjetaName(cardTypeId));
            }

            pago.cardPagoNumber = ParserUtils.optString(jFormaPago, "numero_tarjeta") != null ? ParserUtils.optString(jFormaPago, "numero_tarjeta").trim() : null;

            String bankEmiterkId = ParserUtils.optString(jFormaPago, "banco_emisor_id") != null ? ParserUtils.optString(jFormaPago, "banco_emisor_id").trim() : null;
            if (bankEmiterkId != null) {
                pago.bankEmiter = new QuoteOption(bankEmiterkId, QuoteOptionsController.getInstance().getBancoName(bankEmiterkId));
            }

            String vencimientoDate = ParserUtils.optString(jFormaPago, "fecha_vencimiento") != null ? ParserUtils.optString(jFormaPago, "fecha_vencimiento").trim() : null;
            if (vencimientoDate != null) {
                pago.validityDate = ParserUtils.parseDate(vencimientoDate, "yyyy-MM-dd");
            }

            // CBU / REINTEGROS  Number
            pago.cbu = ParserUtils.optString(jFormaPago, "cbu") != null ? ParserUtils.optString(jFormaPago, "cbu").trim() : null;

            String bankCBUId = ParserUtils.optString(jFormaPago, "banco_id") != null ? ParserUtils.optString(jFormaPago, "banco_id").trim() : null;
            if (bankCBUId != null) {
                pago.bankCBU = new QuoteOption(bankCBUId, QuoteOptionsController.getInstance().getBancoName(bankCBUId));
            }

            String accountTypeId = ParserUtils.optString(jFormaPago, "cuenta_tipo_id") != null ? ParserUtils.optString(jFormaPago, "cuenta_tipo_id").trim() : null;
            if (accountTypeId != null) {
                pago.accountType = new QuoteOption(accountTypeId, QuoteOptionsController.getInstance().getAccountTypeName(accountTypeId));
            }

            //pago.accountNumber = ParserUtils.optString(jFormaPago, "cuenta_numero") != null ? ParserUtils.optString(jFormaPago, "cuenta_numero").trim() : null;
            pago.accountCuil = ParserUtils.optString(jFormaPago, "cuenta_cuil_titular") != null ? ParserUtils.optString(jFormaPago, "cuenta_cuil_titular").trim() : null;
            pago.accountFirstName = ParserUtils.optString(jFormaPago, "cuenta_apellido_titular") != null ? ParserUtils.optString(jFormaPago, "cuenta_apellido_titular").trim() : null;
            pago.accountLastName = ParserUtils.optString(jFormaPago, "cuenta_nombre_titular") != null ? ParserUtils.optString(jFormaPago, "cuenta_nombre_titular").trim() : null;

            pago.titularMainCbuAsAffiliation = jFormaPago.optBoolean("es_titular", true);
            pago.titularCardAsAffiliation = jFormaPago.optBoolean("titular_tarjeta_afiliacion", true);

            // CREDIT FILL FILES
            try {
                // CREDIT CARD  FILES
                try {
                    JSONArray jCreditFilesIdArray = jFormaPago.getJSONArray("archivosTarjetaId");
                    if (jCreditFilesIdArray != null) {
                        for (int i = 0; i < jCreditFilesIdArray.length(); i++) {
                            AttachFile creditCardFile = new AttachFile();
                            creditCardFile.id = jCreditFilesIdArray.optLong(i);
                            pago.creditCardFiles.add(creditCardFile);
                        }
                    }
                } catch (JSONException jEx) {
                }

                // CONST CREDiT CARD
                try {
                    JSONArray jConstanciaFilesIdArray = jFormaPago.getJSONArray("archivosConstanciaId");
                    if (jConstanciaFilesIdArray != null) {
                        for (int i = 0; i < jConstanciaFilesIdArray.length(); i++) {
                            AttachFile constanciaFile = new AttachFile();
                            constanciaFile.id = jConstanciaFilesIdArray.optLong(i);
                            pago.constanciaCardFiles.add(constanciaFile);
                        }
                    }
                } catch (JSONException jEx) {
                }

                // CBU CONST REINTEGROS
                try {
                    JSONArray JConstanciaCBUFilesIdArray = jFormaPago.getJSONArray("archivosReintegroId");
                    if (JConstanciaCBUFilesIdArray != null) {
                        for (int i = 0; i < JConstanciaCBUFilesIdArray.length(); i++) {
                            AttachFile constanciaCBUFile = new AttachFile();
                            constanciaCBUFile.id = JConstanciaCBUFilesIdArray.optLong(i);
                            pago.constanciaCBUFiles.add(constanciaCBUFile);
                        }
                    }
                } catch (JSONException jEx) {
                }

                // CONST CBU
                try {
                    JSONArray JComprobanteCBUFilesIdArray = jFormaPago.getJSONArray("archivos");
                    if (JComprobanteCBUFilesIdArray != null) {
                        for (int i = 0; i < JComprobanteCBUFilesIdArray.length(); i++) {
                            AttachFile comprobanteCBUFile = new AttachFile();
                            JSONObject ciJSON = JComprobanteCBUFilesIdArray.optJSONObject(i);
                            Gson gson = new Gson();
                            ComprobanteCBUItem ci = gson.fromJson(ciJSON.toString(), ComprobanteCBUItem.class);
                            comprobanteCBUFile.id = ci.archivo_id;
                            pago.comprobanteCBUFiles.add(comprobanteCBUFile);
                        }
                    }
                } catch (JSONException jEx) {
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

        return pago;
    }

    private Pago parseCopago(Pago pago, JSONObject jFormaPago, long cardId) {
        // FORMA DE PAGO

        if (jFormaPago != null && pago != null) {

            pago.idCopago = jFormaPago.optLong("id", -1);
            pago.cardIdCopago = cardId;

            // TODO nuevo C or S
            pago.typeCopago = ParserUtils.optString(jFormaPago, "salud_o_copago") != null ? ParserUtils.optString(jFormaPago, "salud_o_copago").trim() : null;

            String formaPagoId = ParserUtils.optString(jFormaPago, "forma_pago_id") != null ? ParserUtils.optString(jFormaPago, "forma_pago_id").trim() : null;
            if (formaPagoId != null) {
                // for EMP  this will be replace by new QuoteOption(formaPagoId,formaPagoId); because its from extra data
                // so  getFormaPagoNAme will return empty data and will not be used
                if (formaPagoId.equals(EF_FORMA_PAGO)){
                    pago.formaCopago = new QuoteOption(formaPagoId, formaPagoId);
                } else {
                    pago.formaCopago = new QuoteOption(formaPagoId, QuoteOptionsController.getInstance().getFormaPagoName(formaPagoId));
                }
            }

            if (formaPagoId != null && formaPagoId.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)){
                // TC : CREDIT CARD
                String cardTypeId = ParserUtils.optString(jFormaPago, "tarjeta_id") != null ? ParserUtils.optString(jFormaPago, "tarjeta_id").trim() : null;
                if (cardTypeId != null) {
                    pago.cardTypeCopago = new QuoteOption(cardTypeId, QuoteOptionsController.getInstance().getTarjetaName(cardTypeId));
                }

                pago.cardPagoNumberCopago = ParserUtils.optString(jFormaPago, "numero_tarjeta") != null ? ParserUtils.optString(jFormaPago, "numero_tarjeta").trim() : null;

                String bankEmiterkId = ParserUtils.optString(jFormaPago, "banco_emisor_id") != null ? ParserUtils.optString(jFormaPago, "banco_emisor_id").trim() : null;
                if (bankEmiterkId != null) {
                    pago.bankEmiterCopago = new QuoteOption(bankEmiterkId, QuoteOptionsController.getInstance().getBancoName(bankEmiterkId));
                }

                String vencimientoDate = ParserUtils.optString(jFormaPago, "fecha_vencimiento") != null ? ParserUtils.optString(jFormaPago, "fecha_vencimiento").trim() : null;
                if (vencimientoDate != null) {
                    pago.validityDateCopago = ParserUtils.parseDate(vencimientoDate, "yyyy-MM-dd");
                }
            } else if (formaPagoId != null && formaPagoId.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)){
                // CBU / REINTEGROS  Number
                pago.cbuCopago = ParserUtils.optString(jFormaPago, "cbu") != null ? ParserUtils.optString(jFormaPago, "cbu").trim() : null;

                String bankCBUId = ParserUtils.optString(jFormaPago, "banco_id") != null ? ParserUtils.optString(jFormaPago, "banco_id").trim() : null;
                if (bankCBUId != null) {
                    pago.bankCBUCopago = new QuoteOption(bankCBUId, QuoteOptionsController.getInstance().getBancoName(bankCBUId));
                }

                String accountTypeId = ParserUtils.optString(jFormaPago, "cuenta_tipo_id") != null ? ParserUtils.optString(jFormaPago, "cuenta_tipo_id").trim() : null;
                if (accountTypeId != null) {
                    pago.accountTypeCopago = new QuoteOption(accountTypeId, QuoteOptionsController.getInstance().getAccountTypeName(accountTypeId));
                }
            }
        }

        return pago;
    }

    private TicketPago parseTicketPago(JSONObject jTicketPago) {
        TicketPago pago = null;
        if (jTicketPago != null) {
            pago = new TicketPago();

            String id = ParserUtils.optString(jTicketPago, "tipo_alta");
            if (id != null) {
                pago.formaAlta = QuoteOptionsController.getInstance().getAltaType(id);
            }

            pago.planValue = jTicketPago.optDouble("valor_plan");

            int nro = jTicketPago.optInt("numero_ticket", 0);
            pago.ticketNumber = "" + nro;

            pago.pagoDate = ParserUtils.parseDate(jTicketPago, "fecha_pago", "yyyy-MM-dd");

            pago.importe = jTicketPago.optDouble("importe");

            long des = jTicketPago.optLong("numero_des", -1L);
            if (des != -1) {
                pago.desNumber = "" + des;
            }

            JSONArray array = jTicketPago.optJSONArray("adjuntos");
            if (array != null)
                for (int r = 0; r < array.length(); r++) {
                    JSONObject jobj = array.optJSONObject(r);

                    AttachFile attachFile = new AttachFile();
                    attachFile.id = jobj.optLong("archivo_id");
                    pago.ticketPagoFiles.add(attachFile);
                }
        }
        return pago;
    }

    private BeneficiarioSUF parseBeneficiarioSUF(JSONObject jSuf) throws Exception {
        BeneficiarioSUF suf = null;
        if (jSuf != null) {
            suf = new BeneficiarioSUF();

            String id = ParserUtils.optString(jSuf, "tipo_documento_id");
            if (id != null)
                suf.docType = QuoteOptionsController.getInstance().getDocType(id);

            suf.dni = jSuf.optInt("numero_documento", 0);
            suf.firstname = ParserUtils.optString(jSuf, "nombre");
            suf.lastname = ParserUtils.optString(jSuf, "apellido");
            suf.birthday = ParserUtils.parseDate(jSuf, "fecha_nacimiento", "yyyy-MM-dd");

            return suf;
        } else {
            return null;
        }
    }


    private List<EntidadEmpleadora> parseEntidadEmpleadora(JSONArray jEmpresasArray) throws Exception {
        // LISTA  EMPRESAS:  ENTIDADES EMPLEADORAS  //
        List<EntidadEmpleadora> entidadEmpleadoraArray = new ArrayList<EntidadEmpleadora>();
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
        return entidadEmpleadoraArray;
    }

    // This method is either used for OS  and Condicion Monotributo
    // For OS : segment always be 2
    // For Condicion monotributo always be 3
    private ObraSocial parseObraSocial(String segmentId, JSONObject jObraSocial, long cardID) throws Exception {
        // OBRA SOCIAL
        ObraSocial obraSocial = null;
        if (jObraSocial != null) {
            obraSocial = new ObraSocial();
            obraSocial.id = jObraSocial.optLong("id", -1);
            obraSocial.cardId = cardID;
            obraSocial.personCardId = jObraSocial.optLong("ficha_persona_id", -1);

            String osDesregId = ParserUtils.optString(jObraSocial, "obra_social_desregula_id") != null ? ParserUtils.optString(jObraSocial, "obra_social_desregula_id").trim() : null;
            if (osDesregId != null && segmentId != null) {
                if (segmentId.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO)){
                    obraSocial.osQuotation = new QuoteOption(osDesregId, QuoteOptionsController.getInstance().getOSCondMonotributoName(osDesregId), QuoteOptionsController.getInstance().getOSCondMonotributoType(osDesregId));
                }else{
                    obraSocial.osQuotation = new QuoteOption(osDesregId, QuoteOptionsController.getInstance().getOSDesreguladaName(osDesregId), QuoteOptionsController.getInstance().getOSDesregulaType(osDesregId));
                }
            }


            String inputDate = ParserUtils.optString(jObraSocial, "fecha_ingreso_obra_social") != null ? ParserUtils.optString(jObraSocial, "fecha_ingreso_obra_social").trim() : null;
            if (inputDate != null) {
                obraSocial.inputOSDate = ParserUtils.parseDate(inputDate, "yyyy-MM-dd");
            }

            String stateId = ParserUtils.optString(jObraSocial, "estado_id") != null ? ParserUtils.optString(jObraSocial, "estado_id").trim() : null;
            if (stateId != null) {
                obraSocial.osState = new QuoteOption(stateId, QuoteOptionsController.getInstance().getOSStateName(stateId));
            }

            obraSocial.osSSSFormNumber = jObraSocial.optLong("formulario_sss", -1);
            obraSocial.mesesImpagos = jObraSocial.optInt("meses_impagos", 0);

            String osId = ParserUtils.optString(jObraSocial, "codigo_obra_social") != null ? ParserUtils.optString(jObraSocial, "codigo_obra_social").trim() : null;
            if (osId != null && segmentId != null) {
                if (segmentId.equals(ConstantsUtil.MONOTRIBUTO_SEGMENTO) ){
                    obraSocial.osActual = new QuoteOption(osDesregId, QuoteOptionsController.getInstance().getOSCondMonotributoName(osDesregId), QuoteOptionsController.getInstance().getOSCondMonotributoType(osDesregId));
                }else{
                    obraSocial.osActual = new QuoteOption(osId, QuoteOptionsController.getInstance().getOSDesreguladaName(osId), QuoteOptionsController.getInstance().getOSDesregulaType(osId));
                }
            }

            obraSocial.empadronado = jObraSocial.optBoolean("empadronado", false);

            // FILE LIST
            // BOTH : DESREGULADO AND MONOTRIBUTO
            try {
                JSONArray jsssArray = jObraSocial.getJSONArray("comprobante_sss");
                if (jsssArray != null && jsssArray.length() > 0) {
                    for (int s = 0; s < jsssArray.length(); s++) {

                        AttachFile attachFile = new AttachFile();
                        attachFile.id = jsssArray.optLong(s);
                        obraSocial.comprobantesSSSFiles.add(attachFile);
                    }
                }
            } catch (JSONException jEx) {
            }

            try {
                JSONArray jAfipArray = jObraSocial.getJSONArray("comprobante_afip");
                if (jAfipArray != null && jAfipArray.length() > 0) {
                    for (int t = 0; t < jAfipArray.length(); t++) {
                        AttachFile attachFile = new AttachFile();
                        attachFile.id = jAfipArray.optLong(t);
                        obraSocial.comprobantesAfipFiles.add(attachFile);
                    }
                }
            } catch (JSONException jEx) {
            }

            try {
                JSONArray jAttachArray = jObraSocial.getJSONArray("adjuntos");
                if (jAttachArray != null && jAttachArray.length() > 0) {
                    for (int u = 0; u < jAttachArray.length(); u++) {
                        JSONObject jAttach = jAttachArray.optJSONObject(u);
                        AttachFile attachFile = new AttachFile();
                        attachFile.id = jAttach.optLong("archivo_id");

                        String comment = ParserUtils.optString(jAttach, "comentario") != null ? ParserUtils.optString(jAttach, "comentario").trim() : null;
                        if (comment != null && !comment.isEmpty()) {

                            // OS SINDICAL
                            if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_CHANGE_OPTION)) {
                                obraSocial.optionChangeFiles.add(attachFile);
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_FORM)) {
                                obraSocial.formFiles.add(attachFile);
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_CHANGE_CERT)) {
                                obraSocial.certChangeFiles.add(attachFile);

                                // OS DIRECCION
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_EMAIL)) {
                                obraSocial.emailFiles.add(attachFile);
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_FORM_53)) {
                                obraSocial.form53Files.add(attachFile);
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_FORM_59)) {
                                obraSocial.form59Files.add(attachFile);
                            } else if (comment.startsWith(ConstantsUtil.FILE_INDEX_OS_MODEL_NOTE)) {
                                obraSocial.modelNotesFiles.add(attachFile);
                            }

                            // MONOTRIBUTO
                            else if (comment.startsWith("osCodigo")) {
                                obraSocial.osCodeFiles.add(attachFile);
                            }
                        }
                    }
                }
            } catch (JSONException jEx) {
            }
        }
        return obraSocial;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
