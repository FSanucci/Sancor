package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;

/**
 * Created by francisco on 18/10/17.
 */

public class EEMorosidadData extends Object implements Serializable {

    public boolean hasMorosidad = false;


    public long eeId = -1L;
    public String eeAreaPrefix;
    public String  eePhone;
    public String eeDescription;
}
