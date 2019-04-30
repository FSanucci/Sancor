package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData2Autonomo implements IAdditionalData2, Serializable {

    public BeneficiarioSUF sufTitular;
    public BeneficiarioSUF sufConyuge;
    public Date inicioServicioDate;

    @Override
    public boolean hasToShowBeneficiarioSUF(){return true;}

    @Override
    public BeneficiarioSUF getSufTitular() {
        return sufTitular;
    }
    @Override
    public void setSufTitular(BeneficiarioSUF sufTitular) {
        this.sufTitular = sufTitular;
    }

    @Override
    public BeneficiarioSUF getSufConyuge() {
        return sufConyuge;
    }

    @Override
    public void setSufConyuge(BeneficiarioSUF sufConyuge) {
        this.sufConyuge = sufConyuge;
    }

    @Override
    public boolean hasToShowObraSocial(){return false;}
    @Override
    public ObraSocial getObraSocial(){return null;}

    @Override
    public void setObraSocialId(long osId){
    }

    @Override
    public boolean hasToUpdateMonotributoFiles(){return false;}
}
