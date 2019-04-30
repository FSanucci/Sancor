package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.List;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData2Desregulado implements IAdditionalData2, Serializable {

    public ObraSocial obraSocial;


    @Override
    public boolean hasToShowBeneficiarioSUF(){return false;}

    @Override
    public BeneficiarioSUF getSufTitular() {
        return null;
    }
    @Override
    public void setSufTitular(BeneficiarioSUF sufTitular) {
    }

    @Override
    public BeneficiarioSUF getSufConyuge() {
        return null;
    }
    @Override
    public void setSufConyuge(BeneficiarioSUF sufConyuge) {

    }

    @Override
    public boolean hasToShowObraSocial(){return true;}
    @Override
    public ObraSocial getObraSocial(){return obraSocial;}

    @Override
    public void setObraSocialId(long osId){
        obraSocial.id = osId;
    }


    @Override
    public boolean hasToUpdateMonotributoFiles(){return false;}
}
