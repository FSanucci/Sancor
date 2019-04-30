package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by sergio on 2/3/17.
 */

public class Member extends Object implements Serializable {

    public QuoteOption parentesco;
    public String firstname;
    public String lastname;
    public int age = 0;
    public long dni = -1L;
    public boolean mainAffiliation = false;
    public String inputDate;
    public int cant = -1;

    public boolean readOnly = false;
    public boolean existent = false;
    public boolean active = false;

    public boolean loadFromQR;

    // for DES
    public double weight = -1.f;
    public double height = -1.f;
    public int imc = -1;

    public List<FamiliarACargo> familiaresACargoList = new ArrayList<FamiliarACargo>();


    public String getFullName(){
        String fullName = "";
        if (firstname != null && lastname!= null ) {
            fullName =  firstname + " " + lastname;
        }
        return fullName;
    }

}
