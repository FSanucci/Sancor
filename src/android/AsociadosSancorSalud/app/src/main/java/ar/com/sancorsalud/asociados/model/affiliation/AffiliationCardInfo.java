package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by luciano on 8/29/17.
 */

public class AffiliationCardInfo extends Object implements Serializable {

    public long id = -1L;
    public long idCotizacion = -1L;

    public QuoteOption segmento;
    public QuoteOption formaIngreso;
    public int cantMembers = 0;

    public Date fechaCarga;
    public Date fechaInicioServicio;

    public Plan plan;

    public ArrayList<AffiliationCardComment> comments;
}
