package ar.com.sancorsalud.asociados.utils;

import android.net.Uri;


public interface LoadResourceUriCallback {

    public void onSuccesLoadUri(Uri uri);
    public void onErrorLoadUri(String error);
}
