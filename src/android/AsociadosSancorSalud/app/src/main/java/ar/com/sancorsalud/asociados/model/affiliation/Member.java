package ar.com.sancorsalud.asociados.model.affiliation;

import android.provider.SyncStateContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

public class Member extends Object implements Serializable {

    public QuoteOption parentesco;
    public long personCardId =-1L;

    public boolean hasPersonCardId = false;

    public String firstname;
    public String lastname;
    public QuoteOption sex;
    public QuoteOption docType;
    public long dni =-1L;
    public int age = 0;
    public String cuil;
    public QuoteOption nationality;

    public Date birthday;
    public Boolean hasDisability;
    public Boolean beneficiarioSUF;
    public boolean aportaMonotributo = false;;
    public boolean existent = false;

    public List<AttachFile> certDiscapacidadFiles = new ArrayList<AttachFile>();
    public List<AttachFile> dniFrontFiles = new ArrayList<AttachFile>();
    public List<AttachFile> dniBackFiles = new ArrayList<AttachFile>();
    public List<AttachFile> cuilFiles = new ArrayList<AttachFile>();
    public List<AttachFile> partidaNacimientoFiles = new ArrayList<AttachFile>();
    public List<AttachFile> actaMatrimonioFiles = new ArrayList<AttachFile>();

    // CONYUGE MONOTRIBUTO
    public List<AttachFile> conyugeForm184Files = new ArrayList<AttachFile>();
    public List<AttachFile> conyugeThreeMonthFiles = new ArrayList<AttachFile>();


    public String getBirthday() {
        if (birthday != null) {
            //return "" + ParserUtils.parseDate(birthday, "dd-MM-yyyy");
            return "" + ParserUtils.parseDate(birthday, "yyyy-MM-dd");
        }
        return "";
    }

    public boolean isConyuge(){
        if(parentesco==null)
            return false;

        if(parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER))
            return true;

        return false;
    }

    public boolean isConcubino(){
        if(parentesco==null)
            return false;

        if(parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))
            return true;

        return false;
    }
}
