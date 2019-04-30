package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;


public class TitularDoc extends Object implements Serializable {

    public List<AttachFile> dniFrontFiles = new ArrayList<AttachFile>();
    public List<AttachFile> dniBackFiles = new ArrayList<AttachFile>();
    public List<AttachFile> cuilFiles = new ArrayList<AttachFile>();
    public List<AttachFile> ivaFiles = new ArrayList<AttachFile>();
    public List<AttachFile> coverageFiles = new ArrayList<AttachFile>();
    public List<AttachFile> planFiles = new ArrayList<AttachFile>();

    // MONOTRIBUTO
    public List<AttachFile> form184Files = new ArrayList<AttachFile>();
    public List<AttachFile> threeMonthFiles = new ArrayList<AttachFile>();

    public boolean cuilInFront = false;
}
