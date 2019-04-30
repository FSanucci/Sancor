package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;

/**
 * Created by francisco on 14/8/17.
 */

public class TicketPago implements Serializable {

    public long id = -1L;

    public QuoteOption formaAlta;
    public double planValue = 0f;
    public long fileId;
    public String ticketNumber;
    public Date pagoDate;
    public double importe = 0f;

    public String desNumber;

    public List<AttachFile> ticketPagoFiles = new ArrayList<AttachFile>();



    public String getPagoDate() {
        if (pagoDate != null) {
            return "" + ParserUtils.parseDate(pagoDate, "yyyy-MM-dd");
        }
        return "";
    }
}
