package ar.com.sancorsalud.asociados.manager;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationAffiliation;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.affiliation.BaseAuthorization;
import ar.com.sancorsalud.asociados.model.affiliation.EEMorosidadData;
import ar.com.sancorsalud.asociados.model.affiliation.WorkflowAuthorization;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;

/**
 * Created by francisco on 25/9/17.
 */

public class WorkflowController {

    private static final String TAG = "WORK_CONTR";

    private static WorkflowController mInstance = new WorkflowController();
    public static synchronized WorkflowController getInstance() {
        return mInstance;
    }


    // ---- PROMOTION ------------------------------------------------------------------------------------------------------------------------------ //

    public void verifiyAuthorizationPromotionRequest(final long paId, final Response.Listener<WorkflowAuthorization> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.verificationAutorizationPromotionRequest(paId, new Response.Listener<WorkflowAuthorization>() {
            @Override
            public void onResponse(WorkflowAuthorization response) {
                Log.e(TAG , "verificationAutorizationPromotion Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "verificationAutorizationPromotion  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void getAuthorizationPromotionRequest(final long paId, final Response.Listener<List<AuthorizationPromotion>> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.getAuthorizationPromotionRequest(paId, new Response.Listener<List<AuthorizationPromotion>>() {
            @Override
            public void onResponse(List<AuthorizationPromotion> response) {
                Log.e(TAG , "getAuthorizationPromotionRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "getAuthorizationPromotionRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void saveAuthorizationPromotionRequest(final AuthorizationPromotion auth, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.saveAuthorizationPromotionRequest(auth, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.e(TAG , "saveAuthorizationPromotionRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "saveAuthorizationPromotionRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void updateAuthorizationPromotionRequest(final AuthorizationPromotion auth, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.updateAuthorizationPromotionRequest(auth, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.e(TAG , "updateAuthorizationPromotionRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "updateAuthorizationPromotionRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    // ---- COBRANZA ------------------------------------------------------------------------------------------------------------------------------ //

    public void verifiyMorosidadRequest(long cuit, final Response.Listener<EEMorosidadData> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.verificationMorosidadRequest(cuit, new Response.Listener<EEMorosidadData>() {
            @Override
            public void onResponse(EEMorosidadData response) {
                Log.e(TAG , "verificationMorosidadRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "verificationMorosidadRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void verifiyAuthorizationCobranzaRequest(final long paId, boolean forCarga, final Response.Listener<WorkflowAuthorization> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.verificationAutorizationCobranzaRequest(paId, forCarga, new Response.Listener<WorkflowAuthorization>() {
            @Override
            public void onResponse(WorkflowAuthorization response) {
                Log.e(TAG , "verifiyAuthorizationCobranzaRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "verifiyAuthorizationCobranzaRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void getAuthorizationCobranzaRequest(final long paId, final Response.Listener<List<AuthorizationCobranza>> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.getAuthorizationCobranzaRequest(paId, new Response.Listener<List<AuthorizationCobranza>>() {
            @Override
            public void onResponse(List<AuthorizationCobranza> response) {
                Log.e(TAG , "getAuthorizationCobranzaRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "getAuthorizationCobranzaRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void saveAuthorizationCobranzaRequest(final AuthorizationCobranza auth, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.saveAuthorizationCobranzaRequest(auth, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.e(TAG , "saveAuthorizationPromotionRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "saveAuthorizationPromotionRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void updateAuthorizationCobranzaRequest(final AuthorizationCobranza auth, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.updateAuthorizationCobranzaRequest(auth, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.e(TAG , "updateAuthorizationCobranzaRequest Successs");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "updateAuthorizationCobranzaRequest  Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }
}
