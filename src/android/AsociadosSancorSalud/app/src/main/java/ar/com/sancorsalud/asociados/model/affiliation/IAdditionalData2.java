package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by francisco on 14/8/17.
 */

public interface IAdditionalData2 extends Serializable {

    public boolean hasToShowBeneficiarioSUF();

    public BeneficiarioSUF getSufTitular();
    public void setSufTitular(BeneficiarioSUF sufTitular);

    public BeneficiarioSUF getSufConyuge();
    public void setSufConyuge(BeneficiarioSUF sufConyuge);

    // FOR DESREG AND MONOTRIBUTO OS AND CONDICION MONOTRIBUTO persist in same service/ table
    public boolean hasToShowObraSocial();
    public ObraSocial getObraSocial();
    public void setObraSocialId(long osId);

    public boolean hasToUpdateMonotributoFiles();
}
