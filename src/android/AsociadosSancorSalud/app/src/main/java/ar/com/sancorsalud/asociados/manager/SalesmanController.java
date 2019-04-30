package ar.com.sancorsalud.asociados.manager;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;

public class SalesmanController {

    private static final long UPDATE_EACH_SECONDS = 3600;
    private long mLastUpdate = 0;
    private ArrayList<Salesman> mList;
    private static SalesmanController instance  = new SalesmanController();

    public static synchronized SalesmanController getInstance() {
        return instance;
    }

    public  SalesmanController(){
        mList = new ArrayList<Salesman>();
    }

    public void clear(){
        mList = new ArrayList<Salesman>();
        mLastUpdate = 0;
    }

    private boolean shouldUpdateAllProspectiveClients(){
        if(mList ==null || mList.size()==0 || mLastUpdate ==0) {
            return true;
        }

        long today = (new Date()).getTime();

        if((today - mLastUpdate)/1000 > UPDATE_EACH_SECONDS) {
            return true;
        }

        return false;
    }

    public void getSalesman(boolean force, final Response.Listener<ArrayList<Salesman>> listener, final Response.ErrorListener errorListener) {
        if(!force && !shouldUpdateAllProspectiveClients()){
            listener.onResponse(mList);
            return;
        }

        HRequest request = RestApiServices.createGetSalesmanRequest(new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> list) {
                mList = list;
                mLastUpdate = (new Date()).getTime();
                listener.onResponse(mList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }


    public void getSalesmanListByZone(long zoneId, final Response.Listener<ArrayList<Salesman>> listener, final Response.ErrorListener errorListener) {

        HRequest request = RestApiServices.createGetSalesmanByZoneRequest(zoneId, new Response.Listener<ArrayList<Salesman>>() {
            @Override
            public void onResponse(ArrayList<Salesman> list) {
                mList = list;
                mLastUpdate = (new Date()).getTime();
                listener.onResponse(mList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }

    public void assignSalesman(long salesmanId, ArrayList<ProspectiveClient> clients, final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {

        HRequest request = RestApiServices.createAssingSalesmanRequest(salesmanId,clients,new Response.Listener<Void>() {
            @Override
            public void onResponse(Void v) {

                listener.onResponse(v);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,true);
    }
}
