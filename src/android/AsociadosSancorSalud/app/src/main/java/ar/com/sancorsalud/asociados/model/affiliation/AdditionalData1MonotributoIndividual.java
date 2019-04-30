package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData1MonotributoIndividual implements IAdditionalData1, Serializable {

    public Pago pago;
    public List<EntidadEmpleadora> entidadEmpleadoraArray = new ArrayList<EntidadEmpleadora>();

    @Override
    public Pago getPago(){
        return pago;
    }

    @Override
    public String getCopagos(){return null;}

    /*
    @Override
    public boolean hasToShowEntidadEmpleadora(){
        return true;
    }
    @Override
    public List<EntidadEmpleadora> getEntidadesEmpleadora(){return entidadEmpleadoraArray;}
    */
}
