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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.FileHelper;


public class HRestSecurityEngine {

    public static final String TAG = "HRestEngine";

    private RequestQueue mRequestQueue;
    private Context mContext;
    private HurlStack mStack;
    private SSLContext mSecureContext;

    private SSLSocketFactory factory;

    public HRestSecurityEngine(Context ctx) {
        mContext = ctx;
    }


    public RequestQueue getRequestQueue() throws Exception {
        Log.e(TAG, "getRequestQueue --------------------");
        if (mRequestQueue == null) {
            setRequestQueue();
        }
        return mRequestQueue;
    }

    public void resetRequestQueue(){
        mRequestQueue = null;
    }


    private void setRequestQueue() throws Exception {
        Log.e(TAG, "setRequestQueue +++++++++++++++++++++++++ ");

        mSecureContext = createSecureContext(mContext);

        //final SSLSocketFactory factory = mSecureContext.getSocketFactory();
        factory = mSecureContext.getSocketFactory();


        mStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);

                httpsURLConnection.setSSLSocketFactory(factory);
                httpsURLConnection.setRequestProperty("charset", "utf-8");


                return httpsURLConnection;
            }

        };
        mRequestQueue = Volley.newRequestQueue(mContext, mStack);
    }


    public SSLSocketFactory getFactory(){
        return factory;
    }


    public <T> void addToRequestQueue(final HRequest<T> req, boolean autologinEnabled) {
        if (req != null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    RestConstants.SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if (autologinEnabled) {
                req.loginErrorListener = new LoginErrorListener<T>() {
                    @Override
                    public void onErrorResponse(HRequest<T> request) {

                        AppController.getInstance().getUserManager().doAutoLogin(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User responseUser) {
                                try {
                                    getRequestQueue().add(req);
                                } catch (Exception e) {
                                    req.notifyError(new VolleyError(e.getMessage()));
                                }
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

            try {
                getRequestQueue().getCache().clear();
                req.setTag(TAG);
                getRequestQueue().add(req);

            } catch (Exception e) {
                req.notifyError(new VolleyError(e.getMessage()));
            }
        }
    }

    public <T> void addToRequestQueue(final HRequest<T> req) {
        addToRequestQueue(req, true);
    }


    public <T> void addToRequestQueueTimeOut(final HRequest<T> req, boolean autologinEnabled, int socketTimeOut) {
        if (req != null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    socketTimeOut,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if (autologinEnabled) {
                req.loginErrorListener = new LoginErrorListener<T>() {
                    @Override
                    public void onErrorResponse(HRequest<T> request) {

                        AppController.getInstance().getUserManager().doAutoLogin(new Response.Listener<User>() {
                            @Override
                            public void onResponse(User responseUser) {
                                try {
                                    getRequestQueue().add(req);
                                } catch (Exception e) {
                                    req.notifyError(new VolleyError(e.getMessage()));
                                }
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

            try {
                getRequestQueue().getCache().clear();
                req.setTag(TAG);
                getRequestQueue().add(req);

            } catch (Exception e) {
                req.notifyError(new VolleyError(e.getMessage()));
            }
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

    public static void setToken(String token) {
        HRequest.authorizationHeaderValue = token;

    }

    private SSLContext createSecureContext(Context ctx) throws Exception {
        KeyStore keyStore = createKeystoreFromP12(ctx);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, null);

        KeyManager[] keyManagers = kmf.getKeyManagers();


        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext secureContext = SSLContext.getInstance("TLS");
        secureContext.init(keyManagers, createTrustManagerWhoTrustAllways(), new SecureRandom());
        return secureContext;
    }

    private KeyStore createKeystoreFromP12(Context ctx) throws Exception {
        KeyStore keyStore = null;

        keyStore = KeyStore.getInstance("PKCS12");

        // get certificate file form app directory
        //File certFile = FileHelper.getAppCertFile(ctx, RestConstants.CERTIFICATE_NAME);
        File certFile = FileHelper.findAppCertificateFile(ctx);

        FileInputStream ins = new FileInputStream(certFile.getPath());

        keyStore.load(ins, RestConstants.P12_PASSWORD.toCharArray());

        return keyStore;
    }

    private TrustManager[] createTrustManagerWhoTrustAllways() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        return;
                    }
                }
        };
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}




