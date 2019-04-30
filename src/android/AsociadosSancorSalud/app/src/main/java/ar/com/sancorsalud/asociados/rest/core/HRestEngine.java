package ar.com.sancorsalud.asociados.rest.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;

/**
 * Created by sergio on 10/24/15.
 */
public class HRestEngine{

    public static final String TAG = HRestEngine.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private Context mContext;
    private HurlStack mStack;

    private SSLSocketFactory factory;


    public HRestEngine(Context ctx){
        mContext = ctx;
       // mRequestQueue = Volley.newRequestQueue(ctx);
        //SSLSocketFactoryExtended factory = null;

        try {
            factory = new SSLSocketFactoryExtended();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        //final SSLSocketFactoryExtended finalFactory = factory;

        mStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    //httpsURLConnection.setSSLSocketFactory(finalFactory);

                    httpsURLConnection.setSSLSocketFactory(factory);
                    httpsURLConnection.setRequestProperty("charset", "utf-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }

        };

        mRequestQueue = Volley.newRequestQueue(mContext, mStack);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void resetRequestQueue(){
        mRequestQueue = null;
    }

    public SSLSocketFactory getFactory(){
        return factory;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                RestConstants.SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().getCache().clear();
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(final HRequest<T> req, boolean autologinEnabled) {
        if(req!=null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    RestConstants.SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            // TODO mejora envez de hacer autologin ....
            // TODO mejora interceptar los 403  y reemplazar el token viejo por el nuevo
            // TODO Guardar el nuevo token y ahi recien seguir con el request
            // TODO isLogin  --> false  llamar al servicio de recrear token

            /*
            AppController.getInstance().getUserManager().isUserLogin(new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean isUserLogin) {
                    Log.e (TAG, "USER IS LOGIN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                    getRequestQueue().add(req);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    req.notifyError(error);
                    // call service to reconstruct token
                }
            });
            */


            if(autologinEnabled) {
                req.loginErrorListener = new LoginErrorListener<T>() {
                    @Override
                    public void onErrorResponse(HRequest<T> request) {

                        AppController.getInstance().getUserManager().doAutoLogin(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User responseUser) {
                                getRequestQueue().add(req);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                req.notifyError(error);
                            }
                        });

                    }
                };
            }

            getRequestQueue().getCache().clear();
            req.setTag(TAG);
            getRequestQueue().add(req);

        }
    }

    public <T> void addToRequestQueue(final HRequest<T> req) {
        addToRequestQueue(req,true);
    }



    public <T> void addToRequestQueueTimeOut(final HRequest<T> req, boolean autologinEnabled, int socketTimeOut) {
        if(req!=null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    socketTimeOut,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if(autologinEnabled) {
                req.loginErrorListener = new LoginErrorListener<T>() {
                    @Override
                    public void onErrorResponse(HRequest<T> request) {

                        AppController.getInstance().getUserManager().doAutoLogin(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User responseUser) {
                                getRequestQueue().add(req);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                req.notifyError(error);
                            }
                        });

                    }
                };
            }


            getRequestQueue().getCache().clear();
            req.setTag(TAG);
            getRequestQueue().add(req);
        }
    }


    public void cancelPendingRequests() {
        cancelPendingRequests(TAG);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static void setToken(String token){
        HRequest.authorizationHeaderValue = token;

    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

}

