package ar.com.sancorsalud.asociados.model.affiliation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AffiliationDataResult extends Object implements Serializable {

    public long cardId = -1L;
    public long desId = -1L;

    public List<Person> personList = new ArrayList<Person>();
}
