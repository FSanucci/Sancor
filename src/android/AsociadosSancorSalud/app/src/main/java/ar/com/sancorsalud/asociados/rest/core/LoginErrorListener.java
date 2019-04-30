package ar.com.sancorsalud.asociados.rest.core;

/**
 * Created by sergio on 8/11/16.
 */
public interface LoginErrorListener<T>  {
    public void onErrorResponse(HRequest<T> request);
}