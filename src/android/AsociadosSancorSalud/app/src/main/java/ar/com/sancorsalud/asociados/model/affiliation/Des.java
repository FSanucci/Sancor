package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;

/**
 * Created by luciano on 7/14/17.
 */

public class Des extends Object implements Serializable {


    public long id = -1L;
    public long cotizacionId = -1L;
    public long desNumber = -1L;
    public boolean hasPatologia = false;
    public long cardId = -1L;
    public long  requestNumber = -1L;
    public String comments;
    public long doctorId = -1L;

    public List <DesDetail> details = new ArrayList<DesDetail>();

    public List<AttachFile> desFiles = new ArrayList<AttachFile>();
    public List<AttachFile> healthCertFiles = new ArrayList<AttachFile>();
    public List<AttachFile> attachsFiles = new ArrayList<AttachFile>();
    public List<AttachFile> anexoFiles = new ArrayList<AttachFile>();


    public long clientDni;
    public long formaIngresoId;

}
