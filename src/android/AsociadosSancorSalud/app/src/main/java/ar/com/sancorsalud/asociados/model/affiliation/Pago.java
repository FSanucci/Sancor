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

public class Pago implements Serializable {

    // PAGO

    public long id = -1L;
    public long cardId = -1L;

    // TODO nuevo
    public String type; // S(Salud) or C(Copago)
    public boolean hasCopagos = false;

    public QuoteOption formaPago;
    public String cardPagoNumber;
    public QuoteOption cardType;
    public QuoteOption bankEmiter;
    public QuoteOption bank;
    public Date validityDate;
    public long fileId;

    public String cbu;
    public QuoteOption bankCBU;
    public QuoteOption accountType;

    public boolean titularCardAsAffiliation = false;
    public boolean titularMainCbuAsAffiliation = false;
    public String accountCuil;
    public String accountFirstName;
    public String accountLastName;

    // TODO Not in use
    public String accountNumber;

    public List<AttachFile> creditCardFiles = new ArrayList<AttachFile>();
    public List<AttachFile> constanciaCardFiles = new ArrayList<AttachFile>();

    public List<AttachFile> constanciaCBUFiles = new ArrayList<AttachFile>();
    public List<AttachFile> comprobanteCBUFiles = new ArrayList<>();

    // COPAGO

    public long idCopago = -1L;
    public long cardIdCopago = -1L;

    public String typeCopago;

    public QuoteOption formaCopago;

    public String cardPagoNumberCopago;
    public QuoteOption cardTypeCopago;
    public QuoteOption bankEmiterCopago;
    public Date validityDateCopago;

    public String cbuCopago;
    public QuoteOption bankCBUCopago;
    public QuoteOption accountTypeCopago;
/*
    public List<AttachFile> creditCardFilesCopago = new ArrayList<AttachFile>();
    public List<AttachFile> constanciaCardFilesCopago = new ArrayList<AttachFile>();

    public List<AttachFile> constanciaCBUFilesCopago = new ArrayList<AttachFile>();
    public List<AttachFile> comprobanteCBUFilesCopago = new ArrayList<>();

    public boolean titularCardAsAffiliationCopago = false;
    public boolean titularMainCbuAsAffiliationCopago = false;
    public String accountCuilCopago;
    public String accountFirstNameCopago;
    public String accountLastNameCopago;
*/
    //Methods

    public String getValidityDate() {
        if (validityDate != null) {
            return "" + ParserUtils.parseDate(validityDate, "yyyy-MM-dd");
        }
        return "";
    }

    public String getValidityDateCopago() {
        if (validityDateCopago != null) {
            return "" + ParserUtils.parseDate(validityDateCopago, "yyyy-MM-dd");
        }
        return "";
    }

}
