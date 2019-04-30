package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;


public class ContactData extends Object implements Serializable {

    public String suggestedPhone;
    public String suggestedDevice;

    public String areaPhone;
    public String phone;
    public String areaDevice;
    public String device;

    public String email;
    public Boolean addInvoice;
    public Boolean dataOnHome;

    public Address alternativeAddress;
}
