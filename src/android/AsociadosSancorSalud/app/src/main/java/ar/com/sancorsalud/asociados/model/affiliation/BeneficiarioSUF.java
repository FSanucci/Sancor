package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;

/**
 * Created by francisco on 14/8/17.
 */

public class BeneficiarioSUF implements Serializable {

    public long id = -1L;

    public QuoteOption docType;
    public long dni = 0L;
    public String firstname;
    public String lastname;
    public Date birthday;

}
