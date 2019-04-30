package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;

public class BaseAuthorization extends Object implements Serializable {

    public long paId = -1L;
    public long cardId = -1L;
    public AffiliationState state = new AffiliationState();

    public List<AttachFile> files = new ArrayList<AttachFile>();
    public List<String> commentList = new ArrayList<String>();
    public String comment;
}
