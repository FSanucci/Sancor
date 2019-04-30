package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by francisco on 10/7/17.
 */

public class FamiliarACargo extends Object implements Serializable {

    public QuoteOption parentesco;
    public int age = 0;
    public int cant = 1;
}
