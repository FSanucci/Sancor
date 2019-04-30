package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;


public class Address extends Object implements Serializable {

    public String street;
    public QuoteOption orientation;
    public int number = -1;
    public int floor = -1;
    public String dpto;

    public QuoteOption adressCode1;
    public QuoteOption adressCode2;
    public String adressCode1Description;
    public String adressCode2Description;

    public String code;
    public String barrio;

    public String location;
    public String zipCode;
}
