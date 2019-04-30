package ar.com.sancorsalud.asociados.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdicionalesOptativosData extends Object implements Serializable {


    public List<TipoOpcion> tipoOpcionList = new ArrayList<TipoOpcion>();

    public class TipoOpcion extends Object implements Serializable  {
        public String titulo;
        public String tipo;
        public List<OpcionalData> opciones = new ArrayList<OpcionalData>();
    }

    public class OpcionalData  extends Object implements Serializable {
        public long segmentoId = -1L;
        public String codigo;
        public String descripcionPlan;
        public long valor = -1L;
        public long productoId = -1L;
        public String obligatorio;
        public long planId = -1L;

        public List<Member> capitas = new ArrayList<Member>();
    }



}
