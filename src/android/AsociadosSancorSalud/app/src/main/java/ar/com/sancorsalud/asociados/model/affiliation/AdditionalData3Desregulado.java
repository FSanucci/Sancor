package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;

/**
 * Created by francisco on 14/8/17.
 */

public class AdditionalData3Desregulado implements IAdditionalData3, Serializable {

    public List<AttachFile> form184Files = new ArrayList<AttachFile>();
    public List<AttachFile> threeMonthFiles = new ArrayList<AttachFile>();

    public ObraSocial osMonotributo;

    @Override
    public ObraSocial getOsMonotributo(){return osMonotributo;}

    @Override
    public void setOSMonotributo(long osId){
        osMonotributo.id = osId;
    }


}

