package ar.com.sancorsalud.asociados.utils;

/**
 * Created by sergiocirasa on 23/4/17.
 */

public interface ImageProviderListener {

    public void didSelectImage(String path);

    public void didDetectCreditCardNumber(String creditCardNumbern, String expireDate);

}
