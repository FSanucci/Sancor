package ar.com.sancorsalud.asociados.model.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 23/5/17.
 */
public class Plan extends Object implements Serializable {

    public long idCotizacion = -1;
    public long idProducto = -1;

    public String idPlan;

    public String descripcionPlan;
    public String descripcionProducto;
    public String descripcionConcepto;

    public double valor = -1f;
    public double diferenciaAPagar = -1f;

    public List<PlanDetail> details = new ArrayList<PlanDetail>();

}
