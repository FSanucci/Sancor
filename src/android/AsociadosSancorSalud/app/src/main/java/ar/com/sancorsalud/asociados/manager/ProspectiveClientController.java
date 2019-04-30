package ar.com.sancorsalud.asociados.manager;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by sergio on 11/2/16.
 */

public class ProspectiveClientController {

    private static final long UPDATE_EACH_SECONDS = 1800;

    private long mLastUpdateOfMyAssignedProspectiveClient = 0;
    private long mLastUpdateOfAllProspectiveClient = 0;
    private static ProspectiveClientController mInstance = new ProspectiveClientController();
    private ArrayList<ProspectiveClient> mMyProspectiveClientList;
    private ArrayList<ProspectiveClient> mAllProspectiveClientList;


    public static synchronized ProspectiveClientController getInstance() {
        return mInstance;
    }

    public  ProspectiveClientController(){
        mMyProspectiveClientList = new ArrayList<ProspectiveClient>();
        mAllProspectiveClientList = new ArrayList<ProspectiveClient>();
    }

    public void clear(){
        mMyProspectiveClientList = new ArrayList<ProspectiveClient>();
        mAllProspectiveClientList = new ArrayList<ProspectiveClient>();
        mLastUpdateOfMyAssignedProspectiveClient = 0;
        mLastUpdateOfAllProspectiveClient = 0;
    }



    private ArrayList<ProspectiveClient>filterList(ArrayList<ProspectiveClient>clients , ProspectiveClient.Filter filter){
        ArrayList<ProspectiveClient> list = new ArrayList<>();

        if(clients==null)
            return list;

        for(ProspectiveClient client : clients){
            if(client.filter == filter) {
                if(filter == ProspectiveClient.Filter.SCHEDULED){
                    if(client.appointment!=null)
                        list.add(client);
                }else list.add(client);
            }
        }
        return list;
    }

    private boolean shouldUpdateMyProspectiveClients(){
        if(mMyProspectiveClientList ==null || mMyProspectiveClientList.size()==0 || mLastUpdateOfMyAssignedProspectiveClient ==0) {
            return true;
        }

        long today = (new Date()).getTime();

        if((today - mLastUpdateOfMyAssignedProspectiveClient)/1000 > UPDATE_EACH_SECONDS) {
            return true;
        }

        return false;
    }

    private boolean shouldUpdateAllProspectiveClients(){
        if(mAllProspectiveClientList ==null || mAllProspectiveClientList.size()==0 || mLastUpdateOfAllProspectiveClient ==0) {
            return true;
        }

        long today = (new Date()).getTime();

        if((today - mLastUpdateOfAllProspectiveClient)/1000 > UPDATE_EACH_SECONDS) {
            return true;
        }

        return false;
    }

    public void cancelRequest(){
        AppController.getInstance().getRestEngine().cancelPendingRequests();

    }

    public void getAllProspectiveClients(boolean force, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {

        if(!force && !shouldUpdateAllProspectiveClients()){
            listener.onResponse(mAllProspectiveClientList);
            return;
        }

        HRequest request = RestApiServices.createGetAllProspectiveClientsRequest(new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> list) {
                mAllProspectiveClientList = list;
                mLastUpdateOfAllProspectiveClient = (new Date()).getTime();
                listener.onResponse(mAllProspectiveClientList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }

    public void getMyAssignedProspectiveClients(boolean force, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {

        if(!force && !shouldUpdateMyProspectiveClients()){
            listener.onResponse(mMyProspectiveClientList);
            return;
        }

        HRequest request = RestApiServices.createGetMyProspectiveClientsRequest(new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> list) {
                mMyProspectiveClientList = list;
                mLastUpdateOfMyAssignedProspectiveClient = (new Date()).getTime();
                listener.onResponse(mMyProspectiveClientList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });


        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }



    //  ---- TODO NEW ---------------------//


    public void getMyPAList( String condition, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {
        Storage.getInstance().setTotalListItem(0);

        HRequest request = RestApiServices.createPAListRequest(condition, new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> list) {

                mMyProspectiveClientList = list;
                for(int i = 0; i < mMyProspectiveClientList.size(); i++){
                    mMyProspectiveClientList.get(i).myFilter = 1;
                }
                mLastUpdateOfMyAssignedProspectiveClient = (new Date()).getTime();
                listener.onResponse(mMyProspectiveClientList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }


    //  ---- TODO END NEW --------------------------- //






    public void getMyAssignedProspectiveClients(boolean force, final ProspectiveClient.Filter filter, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {
        getMyAssignedProspectiveClients(force, new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> response) {
                listener.onResponse(response);
                //listener.onResponse(filterList(response, filter));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        });
    }

    public void getMyAssignedProspectiveClients(String tag, final ProspectiveClient.State state, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetProspectiveClientsByStateRequest(state,new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> list) {
                listener.onResponse(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });
        request.setTag(tag);
        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }

    public void getMyPAList(String tag, final ProspectiveClient.State state, final Response.Listener<ArrayList<ProspectiveClient>> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetProspectiveClientsByStateRequest(state,new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> list) {
                listener.onResponse(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });
        request.setTag(tag);
        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LIST_TIMEOUT_MS);
    }

    public void getProspectiveClientProfile(ProspectiveClient client , final Response.Listener<ProspectiveClient> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createGetProspectiveClientProfileRequest(client,new Response.Listener<ProspectiveClient>() {
            @Override
            public void onResponse(ProspectiveClient client) {
                listener.onResponse(client);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,true);
    }

    public void updateProspectiveClientAppointment(ProspectiveClient client , final Response.Listener<Void> listener, final Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createAddScheduleAppointmentRequest(client,new Response.Listener<Void>() {
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