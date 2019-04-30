package ar.com.sancorsalud.asociados.model.quotation;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.ConyugeQuotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by sergio on 2/20/17.
 */

public class MonotributoQuotation extends Object implements Serializable {

    public Boolean unificaAportes = false;
    // TODO NEW
    public int unificationType = -1;
    public int nroAportantes = 0;

    public ConyugeQuotation conyuge;
    public Boolean titularMainAffilliation;
}
