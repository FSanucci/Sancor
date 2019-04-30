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

public class EntidadEmpleadora implements Serializable {

    public long id = -1L;
    public long cardId = -1L;
    public long empresaId = -1L;
    public long  personCardId = -1L;
    public String cuit;
    public String razonSocial;
    public Date inputDate;

    public double remuneracion = 0f;
    public String areaPhone;
    public String phone;
    public boolean isTitular = true;

    public List<AttachFile> reciboSueldoFiles = new ArrayList<AttachFile>();

    public String getInputDate() {
        if (inputDate != null) {
            return "" + ParserUtils.parseDate(inputDate, "yyyy-MM-dd");
        }
        return "";
    }

    public void update(EntidadEmpleadora inEE){
        this.id = inEE.id;
        this.cardId = inEE.cardId;

        this.empresaId = inEE.empresaId;
        this.cuit = inEE.cuit;;
        this.razonSocial = inEE.razonSocial;
        this.inputDate =inEE.inputDate;
        this.remuneracion = inEE.remuneracion;;
        this.areaPhone = inEE.areaPhone;
        this.phone = inEE.phone;;
        this.isTitular = inEE.isTitular;

        this.reciboSueldoFiles = inEE.reciboSueldoFiles;

    }
}
