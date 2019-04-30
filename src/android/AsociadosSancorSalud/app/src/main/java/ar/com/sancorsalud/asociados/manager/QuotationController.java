package ar.com.sancorsalud.asociados.manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.plan.PlanDetail;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class QuotationController {


    private static final String TAG = "QUOT_CONTR";

    private static QuotationController mInstance = new QuotationController();
    public static synchronized QuotationController getInstance() {
        return mInstance;
    }

    private QuotationDataResult mQuotationResponse;
    private AdicionalesOptativosData mAdicionalesOptativos;

    public void quoteData(final Quotation quotation, final Response.Listener<QuotationDataResult> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createQuotationRequest(quotation, new Response.Listener<QuotationDataResult>() {
            @Override
            public void onResponse(QuotationDataResult quotationResponse) {
                Log.e(TAG , "quotation  Successs");
                mQuotationResponse = quotationResponse;


                // update badges
                Storage.getInstance().setHasChangePAQuantityList(true);

                // update filter for PA
                if (quotation.client instanceof ProspectiveClient){
                    ((ProspectiveClient) quotation.client).setQuoted();
                }

                printQuotationResult(mQuotationResponse);
                listener.onResponse(mQuotationResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Quote Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.QUOTATION_TIMEOUT_MS);

    }

    public void getAdicionalesOptativos(QuoteOption segmento, final Response.Listener<AdicionalesOptativosData> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createGetAdicionalesOptativosRequest(segmento, new Response.Listener<AdicionalesOptativosData>() {
            @Override
            public void onResponse(AdicionalesOptativosData adicionalesOptativos) {
                Log.e(TAG, "adicionales optativos Successs");
                mAdicionalesOptativos = adicionalesOptativos;

                printAdicionalesOptativos(adicionalesOptativos);
                listener.onResponse(mAdicionalesOptativos);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Adicionales Optativos Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void saveQuotationData(long cotizacionId , List<Long> planesCotizIdList, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createSaveQuotationRequest( cotizacionId ,planesCotizIdList, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {
                Log.e(TAG, "Quotation saved Successs");
                listener.onResponse(v);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Quote Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void quotationParametesForQuotedClient(long dni, final Response.Listener<Quotation> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createQuotationParametersForQuotedClientRequest(dni, new Response.Listener<Quotation>() {
            @Override
            public void onResponse(Quotation quotation) {
                Log.e(TAG, "get quotation for quoted client  Successs");
                listener.onResponse(quotation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Quoted client parameters Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    public void quotationParametesForQuotedClient(long dni, boolean fillCotizaciones, final Response.Listener<Quotation> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createQuotationParametersForQuotedClientRequest(dni, fillCotizaciones, new Response.Listener<Quotation>() {
            @Override
            public void onResponse(Quotation quotation) {
                Log.e(TAG, "get quotation for quoted client  Successs");
                listener.onResponse(quotation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Quoted client parameters Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void quotationParametesForQuotedClient(long dni, boolean fillCotizaciones, String filter, final Response.Listener<Quotation> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createQuotationParametersForQuotedClientRequest(dni, fillCotizaciones, filter, new Response.Listener<Quotation>() {
            @Override
            public void onResponse(Quotation quotation) {
                Log.e(TAG, "get quotation for quoted client  Successs");
                listener.onResponse(quotation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Quoted client parameters Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void saveQuotationPlanSelected(final long quotedId, Plan plan, final Response.Listener<Void> listener, final Response.ErrorListener errorListener){

        HRequest request = RestApiServices.createSaveQuotationPlanSelectedtRequest(quotedId, plan, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void result) {
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request, true);
    }

    public void quotationParametesForExistentClient(long dni, boolean checkUnification, final Response.Listener<Quotation> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createQuotationParametersForExistentClientRequest(dni, checkUnification, new Response.Listener<Quotation>() {
            @Override
            public void onResponse(Quotation quotation) {
                Log.e(TAG, "get quotation for assigned client  Successs");
                listener.onResponse(quotation);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Assigned client parameters Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    public void getDownloadFile(Context ctx, String subdir, String link, final Response.Listener<File> listener, final Response.ErrorListener errorListener){

        String  pathFile = RestConstants.HOST + link;
        File file = FileHelper.getImageFile(ctx, subdir, "cotizacion.pdf");

        HRequest request = RestApiServices.createGetDownloadFileRequest(pathFile, file, new Response.Listener<File>() {
            @Override
            public void onResponse(File file) {
                Log.e(TAG, "download resource");
                listener.onResponse(file);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "download resource Failed...");
                errorListener.onErrorResponse(volleyError);
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }


    private void printQuotationResult(QuotationDataResult quotationResult) {
        for (Plan plan : quotationResult.planes) {

            Log.e(TAG, "idCotizacion: " + plan.idCotizacion);
            Log.e(TAG, "idProducto: " + plan.idProducto);
            Log.e(TAG, "descripcionPlan: " + plan.descripcionPlan);
            Log.e(TAG, "descripcionConcepto: " + plan.descripcionConcepto);
            Log.e(TAG, "+++++++++++++++++++++++++++");

            if (!plan.details.isEmpty()) {
                for (PlanDetail d : plan.details) {
                    Log.e(TAG, "Plan Detail:");
                    Log.e(TAG, "Concepto: " + d.descripcionConcepto);
                    Log.e(TAG, "Valor: " + d.valor);
                    Log.e(TAG, "descripcionPlan: " + d.descripcionPlan);
                    Log.e(TAG, "------------------------");
                }
            }
        }
    }

    private void printAdicionalesOptativos(AdicionalesOptativosData adicionalesOptativos) {
        for (AdicionalesOptativosData.TipoOpcion to : adicionalesOptativos.tipoOpcionList) {

            Log.e(TAG, "tipo: " + to.tipo);
            Log.e(TAG, "title: " + to.titulo);
            Log.e(TAG, "+++++++++++++++++++++++++++");

            if (!to.opciones.isEmpty()) {
                for (AdicionalesOptativosData.OpcionalData op : to.opciones) {

                    Log.e(TAG, "segmento_id: " + op.segmentoId);
                    Log.e(TAG, "codigo: " + op.codigo);
                    Log.e(TAG, "desc plan: " + op.descripcionPlan);
                    Log.e(TAG, "valor: " + op.valor);
                    Log.e(TAG, "prod_id: " + op.productoId);
                    Log.e(TAG, "obligatoirio: " + op.obligatorio);
                    Log.e(TAG, "plan_id: " + op.planId);
                    Log.e(TAG, "------------------------");
                }
            }
        }
    }


}
