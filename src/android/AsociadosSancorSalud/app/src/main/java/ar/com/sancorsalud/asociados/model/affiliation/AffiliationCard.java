package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;


public class AffiliationCard extends Object implements Serializable {

    public long id = -1L;
    public long idCotizacion = -1L;


    public TitularData titularData;
    public Address titularAddress;
    public ContactData contactData;
    public TitularDoc document;
    public List<Member> members = new ArrayList<Member>();
    public int cantMembers = 0;

    public IAdditionalData1 additionalData1;
    public IAdditionalData2 additionalData2;
    public IAdditionalData3 additionalData3;

    public TicketPago ticketPago;

    // mix data
    public Plan plan;

    public ConyugeData conyugeData;

    //public int stateId = -1;

    public Member getConyuge(){
        if(members==null)
            return null;

        for(Member m : members){
            if(m.isConyuge())
                return m;
        }
        return null;
    }

    public Member getConcubino(){
        if(members==null)
            return null;

        for(Member m : members){
            if(m.isConcubino())
                return m;
        }
        return null;
    }

}
