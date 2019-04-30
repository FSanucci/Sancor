package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData2Monotributo implements IAdditionalData2, Serializable {

    public ObraSocial obraSocial;
    public List<AttachFile> form184Files = new ArrayList<AttachFile>();
    public List<AttachFile> threeMonthFiles = new ArrayList<AttachFile>();

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
    public boolean hasToUpdateMonotributoFiles(){return true;}
}
