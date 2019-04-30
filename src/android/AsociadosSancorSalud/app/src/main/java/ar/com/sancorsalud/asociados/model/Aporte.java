package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by sergio on 2/16/17.
 */

public class Aporte extends Object implements Serializable{
    public QuoteOption tipoAporte;
    public double monto = 0;

    public double remuneracionBruta(){
        return remuneracionBruta(monto);
    }

    public static double remuneracionBruta(double monto){
        return monto/0.03;
    }

    public static double aporteOS(double monto){
        return monto * 0.03;
    }

    public double  aporteOS(){
        return aporteOS(monto);
    }

    public boolean isRemuneracionBruta(){
        if(tipoAporte.id.equalsIgnoreCase(ConstantsUtil.APORTE_LEGAL_REM_BRUTA))
            return true;

        return false;
    }
}
