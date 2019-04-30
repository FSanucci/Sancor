package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by francisco on 1/11/17.
 */

public class ConyugeData implements Serializable {

    public QuoteOption segmento;
    public ObraSocial obraSocial;
    public ObraSocial osMonotributo;

    public long cardId = -1L;
    public long conyugeCardId = -1L;
    public long titularDNI;

    // For condicion monotributo
    public List<AttachFile> form184Files = new ArrayList<AttachFile>();
    public List<AttachFile> threeMonthFiles = new ArrayList<AttachFile>();

}
