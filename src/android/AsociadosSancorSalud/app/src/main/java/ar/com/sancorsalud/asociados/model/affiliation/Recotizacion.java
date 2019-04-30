package ar.com.sancorsalud.asociados.model.affiliation;

import java.util.ArrayList;

/**
 * Created by sergiocirasa on 7/9/17.
 */

public class Recotizacion {

    public int idCotizacion;
    public int idGrilla;
    public String idPlan;
    public String descripcionPlan;
    public int idProducto;
    public String descripcionProducto;
    public int idConcepto;
    public String descripcionConcepto;
    public int valor;
    public double diferencia_a_pagar;
    public ArrayList<RecotizacionDetalle> detalle;

}
