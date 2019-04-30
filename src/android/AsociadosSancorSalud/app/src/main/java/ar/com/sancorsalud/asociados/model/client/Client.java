package ar.com.sancorsalud.asociados.model.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.DateUtils;

/**
 * Created by sergiocirasa on 3/4/17.
 */

public class Client extends Object implements Serializable{

    public long id;
    public String firstname;
    public String lastname;
    public int age = 0;
    public long dni =0L;
    public Date birthday;

    public String location;
    public String description;

    public String street;
    public int streetNumber=-1;
    public int floorNumber=-1;
    public String department;
    public String zip;

    public String email;
    public String phoneNumber;
    public String celularNumber;

    public Zone zone;
    public long cardId = -1L;

    // already quoted data for that client
    public List<QuotedClientData> quotedDataList = new ArrayList<QuotedClientData>();

    public Client(){
    }

    public String getFullName(){
        String fullName = "";
        if (firstname != null && lastname!= null ) {
            fullName =  firstname + " " + lastname;
        }
        return fullName;
    }

    public String getAge(){
        if(age>0)
            return ""+age;

        if(birthday!=null){
            return "" + DateUtils.getAge(birthday);
        }

        return "";
    }

    public String getBirthday(){
        if(birthday!=null){
            return "" + ParserUtils.parseDate(birthday,"dd-MM-yyyy");
        }
        return "";
    }

}
