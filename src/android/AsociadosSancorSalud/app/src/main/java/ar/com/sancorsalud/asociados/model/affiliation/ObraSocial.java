package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;


public class ObraSocial implements Serializable {

    public long id = -1L;
    public long cardId = -1L;
    public long personCardId = -1L;

    public boolean isMonotributo = false;

    public QuoteOption osActual;
    public QuoteOption osQuotation;

    public QuoteOption osState;
    public Date inputOSDate;

    public int mesesImpagos = 0;
    public long osSSSFormNumber = -1L;
    public Boolean empadronado;

    public Boolean hasMedicControl;


    // DESREG AND MONO OPCION
    public List<AttachFile> comprobantesSSSFiles = new ArrayList<AttachFile>();
    public List<AttachFile> comprobantesAfipFiles = new ArrayList<AttachFile>();

    public List<AttachFile> optionChangeFiles = new ArrayList<AttachFile>();
    public List<AttachFile> formFiles = new ArrayList<AttachFile>();
    public List<AttachFile> certChangeFiles = new ArrayList<AttachFile>();

    public List<AttachFile> emailFiles = new ArrayList<AttachFile>();
    public List<AttachFile> form53Files = new ArrayList<AttachFile>();
    public List<AttachFile> form59Files = new ArrayList<AttachFile>();
    public List<AttachFile> modelNotesFiles = new ArrayList<AttachFile>();

    // MONO
    public List<AttachFile> osCodeFiles = new ArrayList<AttachFile>();

    public String getOSInputDate() {
        if (inputOSDate != null) {
            return "" + ParserUtils.parseDate(inputOSDate, "yyyy-MM-dd");
        }else {
            return "";
        }
    }
}
