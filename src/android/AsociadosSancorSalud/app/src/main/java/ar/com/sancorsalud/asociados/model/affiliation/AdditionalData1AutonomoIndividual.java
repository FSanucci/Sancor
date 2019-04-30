package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData1AutonomoIndividual implements IAdditionalData1, Serializable {

   public Pago pago;

   @Override
   public Pago getPago(){
      return pago;
   }

   @Override
   public String getCopagos(){return null;}

   /*
   @Override
   public boolean hasToShowEntidadEmpleadora(){
      return false;
   }
   @Override
   public List<EntidadEmpleadora> getEntidadesEmpleadora(){return null;}
   */

}
