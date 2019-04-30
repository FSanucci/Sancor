package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public interface IAdditionalData3 extends Serializable {

    public ObraSocial getOsMonotributo();

    public void setOSMonotributo(long osId);
}
