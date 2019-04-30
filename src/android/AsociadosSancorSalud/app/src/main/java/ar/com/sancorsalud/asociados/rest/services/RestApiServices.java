package ar.com.sancorsalud.asociados.rest.services;


import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.Car;
import ar.com.sancorsalud.asociados.model.CloseReasons;
import ar.com.sancorsalud.asociados.model.Counter;
import ar.com.sancorsalud.asociados.model.DecodedFile;
import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.model.LinkData;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.SalesmanIndicator;
import ar.com.sancorsalud.asociados.model.Station;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.ZoneLeaderIndicator;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationDataResult;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.DesNumberVerification;
import ar.com.sancorsalud.asociados.model.affiliation.DesResult;
import ar.com.sancorsalud.asociados.model.affiliation.DesState;
import ar.com.sancorsalud.asociados.model.affiliation.DesValidation;
import ar.com.sancorsalud.asociados.model.affiliation.EEMorosidadData;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.affiliation.Recotizacion;
import ar.com.sancorsalud.asociados.model.affiliation.VerifyAuthorizationCardResponse;
import ar.com.sancorsalud.asociados.model.affiliation.WorkflowAuthorization;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.delete.HDeleteRemoveFileAmazonRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HCheckProspectiveClientRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetAffinityMessage;
import ar.com.sancorsalud.asociados.rest.services.get.HGetAgreementMessage;
import ar.com.sancorsalud.asociados.rest.services.get.HGetAllProspectiveClientsRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetCloseReasonsRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetDownloadFileRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetLinkRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetMyProspectiveClientsRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetNotificationCounterRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetNotificationRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetNotificationSendedRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetPAListRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetProspectiveClientProfileRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetProspectiveClientsByStateRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetSalesmanCountersRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetSalesmanListRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetSearchAfinidadRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetSearchEmpresaRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetVerifyDESNumberRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetValidateDESRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetVerificationMorosidadRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetZoneLeaderCounterRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HGetZonesRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HIsUserLoginRequest;
import ar.com.sancorsalud.asociados.rest.services.get.HLoginRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetAddressAttributeRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetAdicionalesOptativosRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetBancosRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetCategoriasRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetCivilStatusRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetCoberturasRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetCondicionIvaRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetDocTypesRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetFormasIngresoRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetFormasPagoQueryRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetFormasPagoRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetNationalitiesRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetOSDesregulaListRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetOSDesregulaRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetOSStateRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetOrientationRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetParentescosRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetSearchDateroRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetSearchEntityRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetSegmentosRequest;
import ar.com.sancorsalud.asociados.rest.services.get.quote.HGetTarjetasRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HChangeCardStateRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HGetGravRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HGetNoGravRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostAfinityDocumentRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostAgreementDocumentRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardFormaPagoConCopagoSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostGetFileImageRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostRecotizacionRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HGetSalesmanIndicatorsRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HGetZoneLeaderIndicatorsRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostAddNotificationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostAttachFileRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardEntidadEmpleadoraSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardFormaPagoSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardInfoRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardObraSocialSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCardSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostCloseProspectiveClientRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostDesCheckStateRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostDesGetRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostDesSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostDesToAuditoriaRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostEmpresaLaborlaListRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostFileRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostGetFileRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostProspectiveClientRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostQuotationParametersForExistentClientRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostQuotationParametersForQuotedClientRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostQuotationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostRemoveAttachFileRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostRemoveFileRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostSalesmansByZoneRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostSaveQuotationPlanSelectedtRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostSaveQuotationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostSearchLocationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostSendNotificationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostVerificationCreditCardRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostVerifyAltaInmediataRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostVerifyAuthorizationCardRequest;
import ar.com.sancorsalud.asociados.rest.services.post.HPostVerifyTicketPagoAnticipadoRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationCobranzaGetRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationCobranzaSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationCobranzaUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationCobranzaVerificationRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationPromotionGetRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationPromotionSaveRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationPromotionUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.post.workflow.HPostAuthorizationPromotionVerificationRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HMarkAsReadNotificationRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPuTDesToAuditoriaRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutAssignSalesmanRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutCardFormaPagoConCopagoUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutCardFormaPagoUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutCardUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutDesUpdateRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutEditProspectiveClientRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutQuoteProspectiveClientRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutRemoveNotificationRequest;
import ar.com.sancorsalud.asociados.rest.services.put.HPutScheduleAppointmentRequest;

import ar.com.sancorsalud.asociados.utils.AppController;

public class RestApiServices {

    public static HRequest<?> createLoginRequest(String email, String password, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        return new HLoginRequest(email, password, listener, errorListener);
    }

    public static HRequest<?> isUserLoginRequest(Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        return new HIsUserLoginRequest(listener, errorListener);
    }

    public static HRequest<?> createAddScheduleAppointmentRequest(ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPutScheduleAppointmentRequest(client, listener, errorListener);
    }

    public static HRequest<?> createCloseProspectiveClientRequest(long prospectiveClientId, long reasonId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostCloseProspectiveClientRequest(prospectiveClientId, reasonId, listener, errorListener);
    }

    public static HRequest<?> createGetProspectiveClientProfileRequest(ProspectiveClient client, Response.Listener<ProspectiveClient> listener, Response.ErrorListener errorListener) {
        return new HGetProspectiveClientProfileRequest(client, listener, errorListener);
    }

    public static HRequest<?> createGetProspectiveClientProfileRequest(long potencialId, Response.Listener<ProspectiveClient> listener, Response.ErrorListener errorListener) {
        return new HGetProspectiveClientProfileRequest(potencialId, listener, errorListener);
    }

    public static HRequest<?> createGetOSDesregulaListRequest(int segmento, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener){
        return new HGetOSDesregulaListRequest(segmento, listener, errorListener);
    }

    public static HRequest<?> createGetCloseReasonsRequest(Response.Listener<ArrayList<CloseReasons>> listener, Response.ErrorListener errorListener) {
        return new HGetCloseReasonsRequest(listener, errorListener);
    }

    public static HRequest<?> createGetSalesmanRequest(Response.Listener<ArrayList<Salesman>> listener, Response.ErrorListener errorListener) {
        return new HGetSalesmanListRequest(listener, errorListener);
    }

    public static HRequest<?> createGetSalesmanByZoneRequest(long zoneId, Response.Listener<ArrayList<Salesman>> listener, Response.ErrorListener errorListener) {
        return new HPostSalesmansByZoneRequest(zoneId, listener, errorListener);
    }

    public static HRequest<?> createGetAllProspectiveClientsRequest(Response.Listener<ArrayList<ProspectiveClient>> listener, Response.ErrorListener errorListener) {
        return new HGetAllProspectiveClientsRequest(listener, errorListener);
    }

    public static HRequest<?> createGetProspectiveClientsByStateRequest(ProspectiveClient.State state, Response.Listener<ArrayList<ProspectiveClient>> listener, Response.ErrorListener errorListener) {
        return new HGetProspectiveClientsByStateRequest(state, listener, errorListener);
    }

    public static HRequest<?> createAssingSalesmanRequest(long salesmanId, ArrayList<ProspectiveClient> clients, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPutAssignSalesmanRequest(salesmanId, clients, listener, errorListener);
    }

    public static HRequest<?> createGetMyProspectiveClientsRequest(Response.Listener<ArrayList<ProspectiveClient>> listener, Response.ErrorListener errorListener) {
        return new HGetMyProspectiveClientsRequest(listener, errorListener);
    }

    // TODO NEW ------- //
    public static HRequest<?> createPAListRequest(String condition, Response.Listener<ArrayList<ProspectiveClient>> listener, Response.ErrorListener errorListener) {
        return new HGetPAListRequest(condition, listener, errorListener);
    }

    public static HRequest<?> createGetSalesmanCountersRequest(Response.Listener<Counter> listener, Response.ErrorListener errorListener) {
        return new HGetSalesmanCountersRequest(listener, errorListener);
    }

    public static HRequest<?> createGetZoneLeaderCountersRequest(Response.Listener<Counter> listener, Response.ErrorListener errorListener) {
        return new HGetZoneLeaderCounterRequest(listener, errorListener);
    }

    public static HRequest<?> createAddProspectiveClientRequest(long userId, boolean isSalesman, String isCompany, boolean loadFromQr, ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostProspectiveClientRequest(userId, isSalesman, isCompany, loadFromQr, client, listener, errorListener);
    }

    public static HRequest<?> createEditProspectiveClientRequest(boolean isSalesman, String isCompany, boolean loadFromQr, ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPutEditProspectiveClientRequest(isSalesman, isCompany, loadFromQr, client, listener, errorListener);
    }

    public static HRequest<?> createCheckDniRequest(String dni, Response.Listener<ExistenceStatus> listener, Response.ErrorListener errorListener) {
        return new HCheckProspectiveClientRequest(dni, listener, errorListener);
    }

    public static HRequest<?> createGetZonesRequest(Response.Listener<ArrayList<Zone>> listener, Response.ErrorListener errorListener) {
        return new HGetZonesRequest(listener, errorListener);
    }

    public static HRequest<?> createQuoteProspectiveClientRequest(long clientId, String quote, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPutQuoteProspectiveClientRequest(clientId, quote, listener, errorListener);
    }

    public static HRequest<?> createGetBancosRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetBancosRequest(listener, errorListener);
    }

    public static HRequest<?> createGetCategoriasRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetCategoriasRequest(listener, errorListener);
    }

    public static HRequest<?> createGetCoberturasRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetCoberturasRequest(listener, errorListener);
    }

    public static HRequest<?> createGetOSDesregulaRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetOSDesregulaRequest(listener, errorListener);
    }

    public static HRequest<?> createGetOSStateRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetOSStateRequest(listener, errorListener);
    }

    public static HRequest<?> createGetCivilStatusRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetCivilStatusRequest(listener, errorListener);
    }

    public static HRequest<?> createGetDocTypesRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetDocTypesRequest(listener, errorListener);
    }

    public static HRequest<?> createGetNationalitiesRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetNationalitiesRequest(listener, errorListener);
    }

    public static HRequest<?> createGetOrientationRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetOrientationRequest(listener, errorListener);
    }

    public static HRequest<?> createGetAddressAtributeRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetAddressAttributeRequest(listener, errorListener);
    }


    public static HRequest<?> createGetCondicionIvaRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetCondicionIvaRequest(listener, errorListener);
    }

    public static HRequest<?> createGetFormasIngresoRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetFormasIngresoRequest(listener, errorListener);
    }

    public static HRequest<?> createGetFormasPagoRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        if (query == null)
            return new HGetFormasPagoRequest(listener, errorListener);
        else
            return new HGetFormasPagoQueryRequest(query, listener, errorListener);
    }

    public static HRequest<?> createGetParentescosRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetParentescosRequest(listener, errorListener);
    }

    public static HRequest<?> createGetSegmentosRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetSegmentosRequest(listener, errorListener);
    }

    public static HRequest<?> createGetTarjetasRequest(Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        return new HGetTarjetasRequest(listener, errorListener);
    }

    public static HRequest<?> createGetSearchEmpresaRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        String encodedurl = query;
        try {
            encodedurl = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            //encodedurl = URLEncoder.encode(query, "UTF-8").replace("+"," ");
        } catch (UnsupportedEncodingException e) {
        }
        return new HGetSearchEmpresaRequest(encodedurl, listener, errorListener);
    }

    public static HRequest<?> createGetSearchAfinidadRequest(String query, String zipCode, int segmento, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        String fullQuery = query + "?codigoPostal=" + zipCode;

        if (segmento > 0){
            fullQuery = fullQuery + "&segmento=" + segmento;
        }

        return new HGetSearchAfinidadRequest(fullQuery, listener, errorListener);
    }

    public static HRequest<?> createGetSearchEntityRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        String encodedurl = query;
        try {
            encodedurl = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            //encodedurl = URLEncoder.encode(query, "UTF-8").replace("+"," ");
        } catch (UnsupportedEncodingException e) {
        }
        return new HGetSearchEntityRequest(encodedurl, listener, errorListener);
    }

    public static HRequest<?> createGetSearchDateroRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        String encodedurl = query;
        try {
            encodedurl = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            //encodedurl = URLEncoder.encode(query, "UTF-8").replace("+"," ");
        } catch (UnsupportedEncodingException e) {
        }
        return new HGetSearchDateroRequest(encodedurl, listener, errorListener);
    }

    public static HRequest<?> createAffinityDocument(long fichaId, long documentId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAfinityDocumentRequest(fichaId, documentId, listener, errorListener);
    }

    public static HRequest<?> getAffinityMessage(long affinityId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return new HGetAffinityMessage(affinityId, listener, errorListener);
    }

    public static HRequest<?> createAgreementDocument(long fichaId, long documentId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAgreementDocumentRequest(fichaId, documentId, listener, errorListener);
    }

    public static HRequest<?> getAgreementMessage(long agreementId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return new HGetAgreementMessage(agreementId, listener, errorListener);
    }

    public static HRequest<?> createSearchLocationRequest(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        /*
        String encodedurl = query;

        try {
            encodedurl = URLEncoder.encode(query, "UTF-8").replace("+"," ");
        } catch (UnsupportedEncodingException e) {}
        */

        return new HPostSearchLocationRequest(query, listener, errorListener);
    }

    public static HRequest<?> createGetNotificationsRequest(long userId, Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        return new HGetNotificationRequest(userId, listener, errorListener);
    }

    public static HRequest<?> createGetNotificationsSendedRequest(long userId, Response.Listener<ArrayList<Notification>> listener, Response.ErrorListener errorListener) {
        return new HGetNotificationSendedRequest(userId, listener, errorListener);
    }

    public static HRequest<?> createGetNotificationsCounterRequest(Response.Listener<Integer> listener, Response.ErrorListener errorListener) {
        return new HGetNotificationCounterRequest(listener, errorListener);
    }

    public static HRequest<?> removeNotificationRequest(long notificationId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPutRemoveNotificationRequest(notificationId, listener, errorListener);
    }

    public static HRequest<?> sendNotificationRequest(long notificationId, long zoneId, long salesmanId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostSendNotificationRequest(notificationId, zoneId, salesmanId, listener, errorListener);
    }

    public static HRequest<?> addNotificationRequest(Notification notification, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        return new HPostAddNotificationRequest(notification, listener, errorListener);
    }

    public static HRequest<?> createMatkAsReadNotificationRequest(long notificationId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HMarkAsReadNotificationRequest(notificationId, listener, errorListener);
    }

    public static HRequest<?> createQuotationRequest(Quotation quotation, Response.Listener<QuotationDataResult> listener, Response.ErrorListener errorListener) {
        return new HPostQuotationRequest(quotation, listener, errorListener);
    }

    public static HRequest<?> createGetAdicionalesOptativosRequest(QuoteOption segmento, Response.Listener<AdicionalesOptativosData> listener, Response.ErrorListener errorListener) {
        return new HGetAdicionalesOptativosRequest(segmento, listener, errorListener);
    }

    public static HRequest<?> createSaveQuotationRequest(long cotizacionId, List<Long> planesCotizIdList, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostSaveQuotationRequest(cotizacionId, planesCotizIdList, listener, errorListener);
    }

    public static HRequest<?> createQuotationParametersForQuotedClientRequest(long dni, Response.Listener<Quotation> listener, Response.ErrorListener errorListener) {
        return new HPostQuotationParametersForQuotedClientRequest(dni, false, listener, errorListener);
    }

    public static HRequest<?> createQuotationParametersForQuotedClientRequest(long dni, boolean fillCotizaciones, Response.Listener<Quotation> listener, Response.ErrorListener errorListener) {
        return new HPostQuotationParametersForQuotedClientRequest(dni, fillCotizaciones, listener, errorListener);
    }

    public static HRequest<?> createQuotationParametersForQuotedClientRequest(long dni, boolean fillCotizaciones, String filter, Response.Listener<Quotation> listener, Response.ErrorListener errorListener) {
        return new HPostQuotationParametersForQuotedClientRequest(dni, fillCotizaciones, filter, listener, errorListener);
    }

    public static HRequest<?> createQuotationParametersForExistentClientRequest(long dni, boolean checkUnification, Response.Listener<Quotation> listener, Response.ErrorListener errorListener) {
        return new HPostQuotationParametersForExistentClientRequest(dni, checkUnification, listener, errorListener);
    }

    public static HRequest<?> createGetDownloadFileRequest(String link, File file, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        return new HGetDownloadFileRequest(link, file, listener, errorListener);
    }

    public static HRequest<?> createSaveQuotationPlanSelectedtRequest(long quotedId, Plan plan, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostSaveQuotationPlanSelectedtRequest(quotedId, plan, listener, errorListener);
    }

    // DES
    public static HRequest<?> createGetDesNumberVerificationRequest(long desNumber, Response.Listener<DesNumberVerification> listener, Response.ErrorListener errorListener) {
        return new HGetVerifyDESNumberRequest(desNumber, listener, errorListener);
    }

    public static HRequest<?> createGetDesRequest(long id, long cardId, Response.Listener<Des> listener, Response.ErrorListener errorListener) {
        return new HPostDesGetRequest(id, cardId, listener, errorListener);
    }

    public static HRequest<?> createGetDesRequest(long cardId, Response.Listener<Des> listener, Response.ErrorListener errorListener) {
        return new HPostDesGetRequest(-1, cardId, listener, errorListener);
    }

    public static HRequest<?> createSaveDesRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        return new HPostDesSaveRequest(des, listener, errorListener);
    }

    public static HRequest<?> createUpdateDesRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        return new HPutDesUpdateRequest(des, listener, errorListener);
    }

    public static HRequest<?> checkDesStateRequest(long cardId, Response.Listener<DesState> listener, Response.ErrorListener errorListener) {
        return new HPostDesCheckStateRequest(cardId, listener, errorListener);
    }

    public static HRequest<?> createSendDesToAuditoriaRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        return new HPostDesToAuditoriaRequest(des, listener, errorListener);
    }

    public static HRequest<?> updateSendDesToAuditoriaRequest(Des des, Response.Listener<DesResult> listener, Response.ErrorListener errorListener) {
        return new HPuTDesToAuditoriaRequest(des, listener, errorListener);
    }

    // EE
    public static HRequest<?> createGetEmpresaLaboralList(long affiliationCardId, Response.Listener<List<EntidadEmpleadora>> listener, Response.ErrorListener errorListener) {
        return new HPostEmpresaLaborlaListRequest(affiliationCardId, listener, errorListener);
    }

    // CARDS
    public static HRequest<?> createGetCardInfoRequest(long affiliationCardId, Response.Listener<AffiliationCardInfo> listener, Response.ErrorListener errorListener) {
        return new HPostCardInfoRequest(affiliationCardId, listener, errorListener);
    }

    public static HRequest<?> createGetCardRequest(long affiliationCardId, Response.Listener<AffiliationCard> listener, Response.ErrorListener errorListener) {
        return new HPostCardRequest(affiliationCardId, listener, errorListener);
    }

    public static HRequest<?> createCardRequest(AffiliationCard affiliationCard, Response.Listener<AffiliationDataResult> listener, Response.ErrorListener errorListener) {
        return new HPostCardSaveRequest(affiliationCard, listener, errorListener);
    }

    public static HRequest<?> updateCardRequest(AffiliationCard affiliationCard, Response.Listener<AffiliationDataResult> listener, Response.ErrorListener errorListener) {
        return new HPutCardUpdateRequest(affiliationCard, listener, errorListener);
    }

    public static HRequest<?> verifyTicketPagoAnticipadoRequest(long affiliationCardId, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        return new HPostVerifyTicketPagoAnticipadoRequest(affiliationCardId, listener, errorListener);
    }

    // FILES
    public static HRequest<?> createAddFiledRequest(AttachFile inputFile, String encodedImage, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        return new HPostFileRequest(inputFile, encodedImage, listener, errorListener);
    }

    public static HRequest<?> createGetFileRequest(long resourceId, Response.Listener<DecodedFile> listener, Response.ErrorListener errorListener) {
        return new HPostGetFileRequest(resourceId, listener, errorListener);
    }

    public static HRequest<?> createGetFileImageRequest(long resourceId, Response.Listener<DecodedFile> listener, Response.ErrorListener errorListener) {
        return new HPostGetFileImageRequest(resourceId, listener, errorListener);
    }

    public static HRequest<?> createRemoveFileRequest(long attachFileId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostRemoveFileRequest(attachFileId, listener, errorListener);
        //return new HDeleteRemoveFileAmazonRequest(attachFileId, listener, errorListener);
    }

    // ATTACH FILE
    public static HRequest<?> createAttachFileRequest(long desId, long attachFileId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAttachFileRequest(desId, attachFileId, listener, errorListener);
    }

    public static HRequest<?> createRemoveAttachFileRequest(long attachFileId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostRemoveAttachFileRequest(attachFileId, listener, errorListener);
    }

    // FORMA PAGO
    public static HRequest<?> createCardFormaPagoRequest(Pago pago, Response.Listener<Pago> listener, Response.ErrorListener errorListener) {
        return new HPostCardFormaPagoSaveRequest(pago, listener, errorListener);
    }

    public static HRequest<?> updateCardFormaPagoRequest(Pago pago, Response.Listener<Pago> listener, Response.ErrorListener errorListener) {
        return new HPutCardFormaPagoUpdateRequest(pago, listener, errorListener);
    }

    //FORMA PAGO CON COPAGO //TODO
    public static HRequest<?> createCardFormaPagoConCopagoRequest(Pago pago, Response.Listener<Pago> listener, Response.ErrorListener errorListener) {
        return new HPostCardFormaPagoConCopagoSaveRequest(pago, listener, errorListener);
    }

    public static HRequest<?> updateCardFormaPagoConCopagoRequest(Pago pago, Response.Listener<Pago> listener, Response.ErrorListener errorListener) {
        return new HPutCardFormaPagoConCopagoUpdateRequest(pago, listener, errorListener);
    }

    // CREDIT CARD
    public static HRequest<?> verifyCreditCardRequest(long cardId, String fechaCardVencimiento, String fechaIniServ, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        return new HPostVerificationCreditCardRequest(cardId, fechaCardVencimiento, fechaIniServ, listener, errorListener);
    }

    // ENTIDAD EMPLEADORA
    public static HRequest<?> createCardEntidadEmpleadoraRequest(EntidadEmpleadora ee, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        return new HPostCardEntidadEmpleadoraSaveRequest(ee, listener, errorListener);
    }

    public static HRequest<?> createCardOSRequest(ObraSocial os, Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        return new HPostCardObraSocialSaveRequest(os, listener, errorListener);
    }

    public static HRequest<?> createRecotizacionRequest(long fichaId, Date date, Response.Listener<Recotizacion> listener, Response.ErrorListener errorListener) {
        return new HPostRecotizacionRequest(fichaId, date, listener, errorListener);
    }

    // PROMOTION WORKFLOW
    public static HRequest<?> verificationAutorizationPromotionRequest(long paId, Response.Listener<WorkflowAuthorization> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationPromotionVerificationRequest(paId, listener, errorListener);
    }

    public static HRequest<?> getAuthorizationPromotionRequest(long paId, Response.Listener<List<AuthorizationPromotion>> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationPromotionGetRequest(paId, listener, errorListener);
    }

    public static HRequest<?> saveAuthorizationPromotionRequest(AuthorizationPromotion auth, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationPromotionSaveRequest(auth, listener, errorListener);
    }

    public static HRequest<?> updateAuthorizationPromotionRequest(AuthorizationPromotion auth, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationPromotionUpdateRequest(auth, listener, errorListener);
    }

    // COBRANZA WORKFLOW
    public static HRequest<?> verificationMorosidadRequest(long cuit, Response.Listener<EEMorosidadData> listener, Response.ErrorListener errorListener) {
        return new HGetVerificationMorosidadRequest(cuit, listener, errorListener);
    }

    public static HRequest<?> verificationAutorizationCobranzaRequest(long paId, boolean forCarga, Response.Listener<WorkflowAuthorization> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationCobranzaVerificationRequest(paId, forCarga, listener, errorListener);
    }

    public static HRequest<?> getAuthorizationCobranzaRequest(long paId, Response.Listener<List<AuthorizationCobranza>> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationCobranzaGetRequest(paId, listener, errorListener);
    }

    public static HRequest<?> saveAuthorizationCobranzaRequest(AuthorizationCobranza auth, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationCobranzaSaveRequest(auth, listener, errorListener);
    }

    public static HRequest<?> updateAuthorizationCobranzaRequest(AuthorizationCobranza auth, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostAuthorizationCobranzaUpdateRequest(auth, listener, errorListener);
    }

/*
    // OLD CAR methods
    public static HRequest<?> createGetDefaultCarRequest(long userId, Response.Listener<Car> listener, Response.ErrorListener errorListener) {
        return new HGetDefaultCarRequest(userId, listener, errorListener);
    }

    public static HRequest<?> createGetCarsRequest(Response.Listener<ArrayList<Car>> listener, Response.ErrorListener errorListener) {
        return new HGetCarsRequest(listener, errorListener);
    }

    public static HRequest<?> createPostDefaultCarRequest(long userId, long carId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        return new HPostDefaultCarRequest(userId,carId, listener, errorListener);
    }
    // END OLD CAR methods

*/

    public static HRequest<?> verifyAltaInmediataRequest(long cardId, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        return new HPostVerifyAltaInmediataRequest(cardId, listener, errorListener);
    }

    public static HRequest<?> verifyAuthorizationCardRequest(long cardId, Response.Listener<VerifyAuthorizationCardResponse> listener, Response.ErrorListener errorListener) {
        return new HPostVerifyAuthorizationCardRequest(cardId, listener, errorListener);
    }

    public static HRequest<?> validateDESRequest(long desId, Response.Listener<DesValidation> listener, Response.ErrorListener errorListener) {
        return new HGetValidateDESRequest(desId, listener, errorListener);
    }

    public static HRequest<?> createChangeCardStateRequest(long cardId, ProspectiveClient.State state, Car car, String comments, Response.Listener<ExistenceStatus> listener, Response.ErrorListener errorListener) {
        return new HChangeCardStateRequest(cardId, state, car, comments, listener, errorListener);
    }

    public static HRequest<?> createGetSalesmanIndicatorsRequest(long userId, String fromDate, String toDate, Response.Listener<SalesmanIndicator> listener, Response.ErrorListener errorListener) {
        return new HGetSalesmanIndicatorsRequest(userId, fromDate, toDate, listener, errorListener);
    }

    public static HRequest<?> createGetZoneLeaderIndicatorsRequest(long userId, String fromDate, String toDate, Response.Listener<ZoneLeaderIndicator> listener, Response.ErrorListener errorListener) {
        return new HGetZoneLeaderIndicatorsRequest(userId, fromDate, toDate, listener, errorListener);
    }

    public static HRequest<?> createGetNoGravRequest(long userId, Response.Listener<HashMap<String, Station>> listener, Response.ErrorListener errorListener) {
        return new HGetNoGravRequest(userId, listener, errorListener);
    }

    public static HRequest<?> createGetGravRequest(long userId, Response.Listener<HashMap<String, Station>> listener, Response.ErrorListener errorListener) {
        return new HGetGravRequest(userId, listener, errorListener);
    }

    // LINKS
    public static HRequest<?> linkRequest(String linkId, Response.Listener<LinkData> listener, Response.ErrorListener errorListener) {
        return new HGetLinkRequest(linkId, listener, errorListener);
    }

}
