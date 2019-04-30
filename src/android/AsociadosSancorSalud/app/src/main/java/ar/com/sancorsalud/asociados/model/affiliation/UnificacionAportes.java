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

public class UnificacionAportes implements Serializable {

    public long id = -1L;

    public double aportesValues = 0f;
    public String cuil;
    public String razonSocial;
    public String areaPhone;
    public String phone;
    public String empNumber;
    public Date empInputDate;
    public List<AttachFile> recibosFiles = new ArrayList<AttachFile>();

    public String getEmpInputDate() {
        if (empInputDate != null) {
            return "" + ParserUtils.parseDate(empInputDate, "yyyy-MM-dd");
        }
        return "";
    }
}
