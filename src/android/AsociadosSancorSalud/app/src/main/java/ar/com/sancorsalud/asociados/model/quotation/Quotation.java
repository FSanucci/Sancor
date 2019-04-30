package ar.com.sancorsalud.asociados.model.quotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.Pago;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by sergio on 2/20/17.
 */

public class Quotation extends Object implements Serializable {

    public Client client;

    public String marca;
    public String regionId;

    public String cotizacion;
    public QuoteOption coberturaProveniente;
    public String planSalud;

    public QuoteOption categoria;

    public QuoteOption condicionIva;
    public String regimenId;

    public QuoteOption segmento;
    public QuoteOption previousSegmento;

    public QuoteOption formaIngreso;
    public String aportantesMonotributo;

    public QuoteOption nombreEmpresa;
    public QuoteOption nombreAfinidad;

    public String inputDate;

    public boolean isEmpleadaDomestica = false;
    public Member titular;


    public ArrayList<Member> integrantes = new ArrayList<Member>();

    public Boolean unificaAportes;
    public Boolean titularMainAffilliation;
    public int unificationType = 0;

    public ArrayList<Aporte> aportes;

    public Pago pago;

    public DesreguladoQuotation desregulado;
    public MonotributoQuotation monotributo;

    public List<AdicionalesOptativosData.OpcionalData> opcionales = new ArrayList<AdicionalesOptativosData.OpcionalData>();

    // manual quotation
    public String manualPlanName;
    public Double manualPlanPrice = null;


    public long accountNumber = -1L;
    public long accountSubNumber = -1L;
    public boolean isTransferSegment = false;



    public Member getConyuge(){
        if(integrantes!=null){
            for(Member m : integrantes){
                if(m.parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER))
                    return m;
            }
        }
        return null;
    }

    public Member getConcubino(){
        if(integrantes!=null){
            for(Member m : integrantes){
                if(m.parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))
                    return m;
            }
        }
        return null;
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
}
