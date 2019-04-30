package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;


public class DesDetail extends Object implements Serializable {

    public long id = -1L;
    public QuoteOption parentesco;
    public String firstname;
    public String lastname;
    public long docTypeID = -1L;

    public int age = 0;
    public long dni = -1L;

    public long cardPeopleId = -1;
    public double weight = -1.f;
    public double height = -1.f;
    public double imc = -1f;
    public long module = -1L;
    public int cantCoutas = 0;
    public String descPatologia;

    public boolean needAuditoria = false;

    public boolean existent = false;
    public boolean active = false;


    public String getFullName() {
        String fullName = "";
        if (firstname != null && lastname != null) {
            fullName = firstname + " " + lastname;
        }
        return fullName;
    }

}