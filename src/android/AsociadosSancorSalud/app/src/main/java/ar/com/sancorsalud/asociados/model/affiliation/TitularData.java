package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class TitularData extends Object implements Serializable {

    public long  personCardId = -1L;
    public long promotorId = -1L;
    public String needAuditoria;

    public long documentoAfinidadId = -1L;
    public long documentoAgreementId = -1L;

    public QuoteOption segmento;
    public QuoteOption formaIngreso;
    public QuoteOption categoria;
    public QuoteOption docType;
    public QuoteOption entity;
    public QuoteOption dateroNumber;
    public QuoteOption civilStatus;
    public QuoteOption nationality;
    public QuoteOption coberturaProveniente;

    public QuoteOption nombreAfinidad;
    public QuoteOption nombreEmpresa;


    public boolean hasDisability = false;

    public QuoteOption condicionIva;
    public QuoteOption sex;
    public int age = -1;
    public QuoteOption parentesco;
    public boolean aportaMonotributo = false;

    public String firstname;
    public String lastname;

    public long dni = 0L;
    public Date birthday;
    public String cuil;

    public Date fechaCarga;
    public Date fechaInicioServicio;

    public boolean existent = false;
    public Boolean beneficiarioSUF;




    public String getBirthday() {
        if (birthday != null) {
            return "" + ParserUtils.parseDate(birthday, "yyyy-MM-dd");
        }
        return "";
    }

    public ConstantsUtil.Segmento getSegmento(){

        if(segmento==null)
            return null;

        if(segmento.id.equalsIgnoreCase(ConstantsUtil.AUTONOMO_SEGMENTO)){
            return ConstantsUtil.Segmento.AUTONOMO;
        }else if(segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)){
            return ConstantsUtil.Segmento.DESREGULADO;
        }else if(segmento.id.equalsIgnoreCase(ConstantsUtil.MONOTRIBUTO_SEGMENTO)) {
            return ConstantsUtil.Segmento.MONOTRIBUTO;
        }

        return null;
    }


    public ConstantsUtil.FormaIngreso getFormaIngreso(){

        if(formaIngreso==null)
            return null;

        if(formaIngreso.id.equalsIgnoreCase(ConstantsUtil.INDIVIDUAL_FORMA_INGRESO)){
            return ConstantsUtil.FormaIngreso.INDIVIDUAL;
        }else if(formaIngreso.id.equalsIgnoreCase(ConstantsUtil.EMPRESA_FORMA_INGRESO)){
            return ConstantsUtil.FormaIngreso.EMPRESA;
        }else if(formaIngreso.id.equalsIgnoreCase(ConstantsUtil.AFINIDAD_FORMA_INGRESO)) {
            return ConstantsUtil.FormaIngreso.AFINIDAD;
        }

        return null;
    }

}
