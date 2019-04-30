package ar.com.sancorsalud.asociados.manager;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by sergio on 10/31/16.
 */

public class UserController {
    private static final String TAG = "USER_MANAGER";
    private static final String USER_KEY = "current_user";

    private static UserController mInstance = new UserController();
    public static synchronized UserController getInstance() {
        return mInstance;
    }

    private User mUser;

    public UserController(){
        mUser = loadUser();
    }

    public User getSignedUser(){
        return mUser;
    }


    public void login(String email, String password, final Response.Listener<User> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.createLoginRequest(email,password,new Response.Listener<User>() {
            @Override
            public void onResponse(User user) {
                Log.e("Login","Successs");
                mUser = user;
                saveUser(mUser);
                HRequest.authorizationHeaderValue = mUser.token;
                listener.onResponse(mUser);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Login","Failed");
                errorListener.onErrorResponse(volleyError);
            }
        });

        //AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
        AppController.getInstance().getRestEngine().addToRequestQueueTimeOut(request,false, RestConstants.LOGIN_TIMEOUT_MS);

    }

    public void isUserLogin(final Response.Listener<Boolean> listener, final Response.ErrorListener errorListener){
        HRequest request = RestApiServices.isUserLoginRequest(new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean isUserLogin) {
                Log.e("isUserLogin","Successs");
                listener.onResponse(isUserLogin);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("isUserLogin","Failed");
                errorListener.onErrorResponse(volleyError);
            }
        });

        // NOTO enque request to avoifd infinitive lopp , just send the resposne
    }


    public void doAutoLogin(Response.Listener<User> listener, Response.ErrorListener errorListener){
        if(mUser==null) {
            Log.e("user", "null");
        } else {
            login(mUser.username, mUser.password, listener, errorListener);
        }
    }

    public void passRecovery(String email){
        //TODO: Pass Recovery: Servicio no disponible
    }

    public void logout(){
        NotificationController.getInstance().clear();
        ProspectiveClientController.getInstance().clear();
        SalesmanController.getInstance().clear();
        HRequest.authorizationHeaderValue = null;
        mUser = null;
        removeUser();
        Storage.getInstance().setCardEditableMode(false);
    }


    private void saveUser(User user) {
        String json = ParserUtils.parseToJSON(user);
        Storage.getInstance().putPreferences(USER_KEY,json);
    }

    private User loadUser(){
        String json = Storage.getInstance().getPreferences(USER_KEY);

        if(json!=null) {
            User user = ParserUtils.parseFromJSON(json,User.class);
            if(user!=null)
                return user;
        }
        return null;
    }

    private void removeUser(){
        Storage.getInstance().remove(USER_KEY);
    }

}
