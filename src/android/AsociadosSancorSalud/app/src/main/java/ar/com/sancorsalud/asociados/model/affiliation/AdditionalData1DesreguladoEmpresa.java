package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData1DesreguladoEmpresa implements IAdditionalData1, Serializable {

    public Pago pago;
    public String empresaId;
    public String copagos;

    public  List<EntidadEmpleadora> entidadEmpleadoraArray =  new ArrayList<EntidadEmpleadora>();;

    @Override
    public Pago getPago(){
        return pago;
    }

    @Override
    public String getCopagos(){
        return copagos;
    }

    /*
    @Override
    public boolean hasToShowEntidadEmpleadora(){
        return false;
    }
    @Override
    public List<EntidadEmpleadora> getEntidadesEmpleadora(){return entidadEmpleadoraArray;}
    */
}
