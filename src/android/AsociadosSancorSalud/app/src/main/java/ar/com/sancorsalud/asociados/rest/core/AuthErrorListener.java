package ar.com.sancorsalud.asociados.rest.core;


/**
 * Created by sergio on 10/24/15.
 */
public interface AuthErrorListener<T>  {
    void onErrorResponse(HRequest<T> request);
}