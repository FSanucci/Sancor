package ar.com.sancorsalud.asociados.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import ar.com.sancorsalud.asociados.activity.AddEditPAActivity;
import ar.com.sancorsalud.asociados.activity.NotificationSelectionPromoterActivity;
import ar.com.sancorsalud.asociados.activity.NotificationsSendedActivity;
import ar.com.sancorsalud.asociados.activity.SplashActivity;
import ar.com.sancorsalud.asociados.activity.TraceCardActivity;
import ar.com.sancorsalud.asociados.activity.FilterActivity;
import ar.com.sancorsalud.asociados.activity.LoginActivity;
import ar.com.sancorsalud.asociados.activity.NotificationsDetailActivity;
import ar.com.sancorsalud.asociados.activity.PACardDetailActivity;
import ar.com.sancorsalud.asociados.activity.PADetailActivity;
import ar.com.sancorsalud.asociados.activity.QRCodeActivity;
import ar.com.sancorsalud.asociados.activity.SalesmanSelectionActivity;
import ar.com.sancorsalud.asociados.activity.WebViewDetailActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AddAffiliationMemberActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AddEntidadEmpleadoraActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.ConyugeDataActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.DesActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.InitLoadDataActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.LinkDetailActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.workflow.AuthorizationCobranzaAddActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.workflow.AuthorizationCobranzaUpdateActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.workflow.AuthorizationPromotionAddActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.workflow.AuthorizationPromotionUpdateActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.workflow.WorkflowCobranzaForPAActivity;
import ar.com.sancorsalud.asociados.activity.main.SalesmanMainActivity;
import ar.com.sancorsalud.asociados.activity.main.ZoneLeaderMainActivity;
import ar.com.sancorsalud.asociados.activity.quotation.AddAporteActivity;
import ar.com.sancorsalud.asociados.activity.quotation.AddMemberActivity;
import ar.com.sancorsalud.asociados.activity.quotation.AdicionalesOptativosActivity;
import ar.com.sancorsalud.asociados.activity.quotation.ManualQuoteActivity;
import ar.com.sancorsalud.asociados.activity.quotation.QuotationResultActivity;
import ar.com.sancorsalud.asociados.activity.quotation.QuoteActivity;
import ar.com.sancorsalud.asociados.activity.quotation.QuoteExistentAdicionalesOptativosActivity;
import ar.com.sancorsalud.asociados.activity.quotation.QuoteNewAdicionalesOptativosActivity;
import ar.com.sancorsalud.asociados.activity.quotation.RecotizacionActivity;
import ar.com.sancorsalud.asociados.activity.quotation.TransferenciaSegmentoActivity;
import ar.com.sancorsalud.asociados.activity.subte.SubteGravActivity;
import ar.com.sancorsalud.asociados.activity.subte.SubteNoGravActivity;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.Notification;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCard;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.Des;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;

/**
 * Created by sergio on 10/31/16.
 */

public class IntentHelper extends BaseIntentHelper {

    public static void goToSplashActivity(Activity activity) {
        launchIntentAndFinish(activity, SplashActivity.class);
    }

    public static void goToLoginActivity(Activity activity) {
        launchIntentAndFinish(activity, LoginActivity.class);
    }

    public static void goToQuoteActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, QuoteActivity.class, false, params, ConstantsUtil.CLIENT_CODE);
    }

    public static void goToRecotizacionActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, RecotizacionActivity.class,false, params);
    }

    public static void goToQuotationResultActivity(Activity activity, QuotationDataResult quotationResult) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.QUOTATION_RESULT, quotationResult);
        launchIntent(activity, QuotationResultActivity.class, false, params);
    }

    public static void goToQuoteNewAdicionalesOptativosActivity(Activity activity, ProspectiveClient client){
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, QuoteNewAdicionalesOptativosActivity.class, false, params, ConstantsUtil.CLIENT_CODE);
    }


    public static void goToQuoteExistentAdicionalesOptativosActivity(Activity activity) {
        launchIntent(activity, QuoteExistentAdicionalesOptativosActivity.class);
    }


    public static void goToAdicionalesOptativosActivity(Activity activity, Quotation quotation, AdicionalesOptativosData optativosData) {
        goToAdicionalesOptativosActivity(activity, quotation, optativosData, false);
    }

    public static void goToAdicionalesOptativosActivity(Activity activity, Quotation quotation, AdicionalesOptativosData optativosData, boolean hidePlanValue){
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.QUOTATION, quotation);
        params.putSerializable(ConstantsUtil.OPTATIVOS_DATA, optativosData);
        params.putBoolean(ConstantsUtil.OPTATIVOS_HIDE_PLAN_VALUE, hidePlanValue);

        launchIntent(activity, AdicionalesOptativosActivity.class, false, params);
    }

    public static void goToManualQuoteActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, ManualQuoteActivity.class, false, params, ConstantsUtil.CLIENT_CODE);
    }


    public static void goToZoneLeaderMainActivity(Activity activity) {
        launchIntentAndFinish(activity, ZoneLeaderMainActivity.class);
    }

    public static void goToSalesmanMainActivity(Activity activity) {
        launchIntentAndFinish(activity, SalesmanMainActivity.class);
    }

    public static void goToQrCodeScan(Activity activity, int requestCode) {
        launchIntent(activity, QRCodeActivity.class, null, requestCode);
    }

    public static void goToSalesmanSelectionActivity(Activity activity, Bundle params) {
        launchIntent(activity, SalesmanSelectionActivity.class, false, params,ConstantsUtil.VIEW_SALESMAN_SELECTION_REQUEST_CODE);
    }

    public static void goToAddPAActivity(Activity activity) {
        launchIntent(activity, AddEditPAActivity.class);
    }

    public static void gotoEditPAActivity(Activity activity, ProspectiveClient client, boolean readOnly) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        params.putSerializable(ConstantsUtil.CLIENT_READ_ONLY, readOnly);
        launchIntent(activity, AddEditPAActivity.class, false, params, ConstantsUtil.VIEW_EDIT_PROFILE_REQUEST_CODE);
    }

    public static void gotoFilterActivity(Activity activity) {
        launchIntent(activity, FilterActivity.class, false, null, ConstantsUtil.SELECT_FILTER_OR_STATE);
    }


    public static void gotoPADetailActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, PADetailActivity.class, false, params, ConstantsUtil.VIEW_DETAIL_REQUEST_CODE);
    }

    public static void gotoWorkFlowCobranzaForPAActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, WorkflowCobranzaForPAActivity.class, false, params, ConstantsUtil.VIEW_AUTH_COBRZ);
    }

    public static void gotoPACardDetailActivity(Activity activity, ProspectiveClient client) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        launchIntent(activity, PACardDetailActivity.class, false, params, ConstantsUtil.VIEW_DETAIL_REQUEST_CODE);
    }


    public static void gotoAddMemberActivity(Fragment fragment) {
        Bundle params = new Bundle();
        params.putBoolean(ConstantsUtil.FILTER_PARENTESCOS, true);
        launchIntent(fragment, AddMemberActivity.class, false, params, ConstantsUtil.VIEW_CODE);
    }

    public static void gotoAddMemberActivity(Fragment fragment, boolean unificaAportes) {
        Bundle params = new Bundle();
        params.putBoolean(ConstantsUtil.UNIFICA_APORTES, unificaAportes);
        params.putBoolean(ConstantsUtil.FILTER_PARENTESCOS, false);
        launchIntent(fragment, AddMemberActivity.class, false, params, ConstantsUtil.VIEW_CODE);
    }

    public static void gotoAddAporte(Fragment fragment) {
        launchIntent(fragment, AddAporteActivity.class, false, null, ConstantsUtil.VIEW_CODE);
    }


    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public static void gotoCalendarActivity(final Activity context, long eventId){
        Uri uriUpdate = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(uriUpdate);
        context.startActivityForResult(intent, ConstantsUtil.CALENDAR_REQUEST_CODE);
    }

    public static void goToNotificationDetailActivity(Activity activity, Notification notif, boolean isZoneLeaderRole) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.ARG, notif);
        params.putBoolean(ConstantsUtil.IS_ZONE_LEADER_ROLE, isZoneLeaderRole);
        launchIntent(activity, NotificationsDetailActivity.class, false, params, ConstantsUtil.VIEW_NOTIFICATION);
    }

    public static void goToNotificationsSendedActivity(Activity activity ) {
        launchIntent(activity, NotificationsSendedActivity.class, true, null);
    }

    public static void goToNotificationReadDetailActivity(Activity activity, Notification notif) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.ARG, notif);
        launchIntent(activity, NotificationsDetailActivity.class, false, params, ConstantsUtil.VIEW_NOTIFICATION);
    }

    public static void goToNotificationPromoterSelectionActivity(Activity activity, long notificationId, Zone zone) {
        Bundle params = new Bundle();
        params.putLong(ConstantsUtil.NOTIFICATION_ID, notificationId);
        params.putSerializable(ConstantsUtil.ARG, zone);
        launchIntent(activity, NotificationSelectionPromoterActivity.class, params,  ConstantsUtil.VIEW_NOTIFICATION);
    }


    public static void goToWebDetailActivity(Activity activity,String link) {
        Bundle params = new Bundle();
        params.putString(ConstantsUtil.ARG, link);
        launchIntent(activity, WebViewDetailActivity.class, false, params);
    }

    public static void goToTransferenciaSegmentoActivity(Activity activity) {
        launchIntent(activity, TransferenciaSegmentoActivity.class);
    }


    public static void goToInitLoadActivity(Activity activity, ProspectiveClient client, String sFechaCarga) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CLIENT_ARG, client);
        params.putString(ConstantsUtil.FECHA_CARGA, sFechaCarga);
        launchIntent(activity, InitLoadDataActivity.class,false, params);
    }

    // TODO OLD
    public static void goToDesActivity(Activity activity, Des des, boolean addMode) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.DES, des);
        params.putBoolean(ConstantsUtil.ADD_MODE, addMode);
        launchIntent(activity, DesActivity.class,false, params);
    }

    public static void goToDesActivity(Activity activity, Des des) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.DES, des);
        launchIntent(activity, DesActivity.class,false, params);
    }



    public static void goToAffiliationActivity(Activity activity,  AffiliationCard affiliationCard, long paId ) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.AFFILIATION, affiliationCard);
        params.putSerializable(ConstantsUtil.PA_ID, paId);
        launchIntent(activity, AffiliationActivity.class,false, params);
    }

    public static void goToAffiliationActivity(Activity activity,  AffiliationCard affiliationCard, long paId, boolean PALoadFromQR ) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.AFFILIATION, affiliationCard);
        params.putSerializable(ConstantsUtil.PA_ID, paId);
        params.putSerializable(ConstantsUtil.LOAD_FROM_QR, PALoadFromQR);
        launchIntent(activity, AffiliationActivity.class,false, params);
    }

    public static void goToAddAffiliationMemberActivity(Fragment fragment, long titularDNI, boolean titularAportaMono,  Member member, int index) {
        Bundle params = new Bundle();
        params.putLong(ConstantsUtil.AFFILIATION_TITULAR_DNI, titularDNI);
        params.putBoolean(ConstantsUtil.AFFILIATION_TITULAR_APORTA_MONO, titularAportaMono);
        params.putSerializable(ConstantsUtil.AFFILIATION_MEMBER, member);
        params.putInt(ConstantsUtil.AFFILIATION_MEMBER_INDEX, index);

        launchIntent(fragment, AddAffiliationMemberActivity.class, false, params, ConstantsUtil.VIEW_CODE);

    }

    public static void goToLinkDetailActivity(Activity activity, String title, String url) {
        Bundle params = new Bundle();
        params.putString(ConstantsUtil.LINK_TITLE, title);
        params.putString(ConstantsUtil.LINK_URL, url);

        launchIntent(activity, LinkDetailActivity.class, false, params);
    }

    public static void gotoEntidadEmpleadoraActivity(Fragment fragment, long titularDNI, int index, EntidadEmpleadora ee) {
        Bundle params = new Bundle();

        params.putLong(ConstantsUtil.AFFILIATION_TITULAR_DNI, titularDNI);
        params.putInt(ConstantsUtil.AFFILIATION_EE_INDEX, index);
        params.putSerializable(ConstantsUtil.AFFILIATION_ENTIDAD_EMPLEADORA, ee);

        launchIntent(fragment, AddEntidadEmpleadoraActivity.class, false, params, ConstantsUtil.VIEW_EE);
    }


    public static void goToTraceCardActivity(Activity activity, ProspectiveClient client, boolean showEditMode){
        Bundle params = new Bundle();
        params.putSerializable(TraceCardActivity.PA, client);
        params.putBoolean(TraceCardActivity.SHOW_EDIT_MODE, showEditMode);
        launchIntent(activity, TraceCardActivity.class, false, params, ConstantsUtil.VIEW_CARD_DETAIL);
    }

    public static void goToSubteGravActivity(Activity activity, long paId) {
        Bundle params = new Bundle();
        params.putLong(SubteGravActivity.PA, paId);
        launchIntent(activity, SubteGravActivity.class, false, params);
    }

    public static void goToSubteNoGravActivity(Activity activity, long paId) {
        Bundle params = new Bundle();
        params.putLong(SubteGravActivity.PA, paId);
        launchIntent(activity, SubteNoGravActivity.class, false, params);
    }

    public static void goToConyugeDataActivity(Activity activity, ConyugeData conyugeData){
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.CONYUGE_DATA, conyugeData);
        launchIntent(activity, ConyugeDataActivity.class, params,  ConstantsUtil.VIEW_CONYUGE_OS);
    }

    public static void goToAuthorizationPromotionAddActivity(Activity activity, AuthorizationPromotion auth) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.AUTH_PROMOTION, auth);
        launchIntent(activity, AuthorizationPromotionAddActivity.class, params,  ConstantsUtil.VIEW_AUTH_PROMO);

    }
    public static void goToAuthorizationPromotionUpdateActivity(Activity activity, long paId) {
        Bundle params = new Bundle();
        params.putLong(ConstantsUtil.PA_ID, paId);
        launchIntent(activity, AuthorizationPromotionUpdateActivity.class, params,  ConstantsUtil.VIEW_AUTH_PROMO);
    }

    public static void goToAuthorizationCobranzaAddActivity(Activity activity, AuthorizationCobranza auth) {
        Bundle params = new Bundle();
        params.putSerializable(ConstantsUtil.AUTH_COBRANZA, auth);
        launchIntent(activity, AuthorizationCobranzaAddActivity.class, params,  ConstantsUtil.VIEW_AUTH_COBRZ);

    }
    public static void goToAuthorizationCobranzaUpdateActivity(Activity activity, long paId) {
        Bundle params = new Bundle();
        params.putLong(ConstantsUtil.PA_ID, paId);
        launchIntent(activity, AuthorizationCobranzaUpdateActivity.class, params,  ConstantsUtil.VIEW_AUTH_COBRZ);
    }




}