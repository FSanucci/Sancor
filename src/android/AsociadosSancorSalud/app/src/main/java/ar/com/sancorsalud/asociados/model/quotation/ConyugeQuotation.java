package ar.com.sancorsalud.asociados.model.quotation;

import java.io.Serializable;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

public class ConyugeQuotation extends Object implements Serializable {
    public long dni = -1;

    public QuoteOption segmento;

    // Aporte legal
    public QuoteOption osDeregulado;
    public ArrayList<Aporte> aportesLegales;

    // Monotributo
    public Boolean aportaMonotributo;

    public int nroAportantes = 0;
    public boolean afiliado = false;

}
