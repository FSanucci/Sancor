package ar.com.sancorsalud.asociados.model.quotation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.plan.Plan;

public class QuotationDataResult extends Object implements Serializable {

    public long id = -1;
    public String link;

    public List<Plan> planes = new ArrayList<Plan>();

    public String clientFullName;
    public long clientId = -1L;
    public String quoteType;
    public boolean hidePlanValue = false;


}
