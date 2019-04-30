package ar.com.sancorsalud.asociados.model.plan;

import java.io.Serializable;

/**
 * Created by francisco on 23/5/17.
 */
public class PlanDetail extends Object implements Serializable {
    public String descripcionConcepto;
    public String idPlan;
    public String  descripcionPlan;
    public double valor = -1f;

}
