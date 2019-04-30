package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;

/**
 * Created by francisco on 2/8/17.
 */

public class MembersAndUnificationData extends Object implements Serializable {


    public Boolean unificaAportes;
    public int unificationType = 0;  // 0 U: N/N (Unificacion Nuevo con Nuevo), 1 U: N/E (Unificacion Nuevo con Asociado)

    //public Client conyugeClient;
    //public Member conyugeTitular;

    public ArrayList<Member> integrantes = new ArrayList<Member>();
    public Boolean titularMainAffilliation;

    public boolean isEmpleadaDomestica = false;
    public Quotation conyugeQuotation;

}
