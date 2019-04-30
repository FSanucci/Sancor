package ar.com.sancorsalud.asociados.model.quotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.plan.Plan;


public class QuotedClientData extends Object implements Serializable {

    public long id;

    public List<Plan> planes;
    public ArrayList<Member> integrantes;

}
