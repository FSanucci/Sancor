package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by sergio on 2/20/17.
 */

public class Pago extends Object implements Serializable {

    public QuoteOption formaPago;
    public QuoteOption tarjeta;
    public QuoteOption banco;
    public Boolean conCopago;
}
