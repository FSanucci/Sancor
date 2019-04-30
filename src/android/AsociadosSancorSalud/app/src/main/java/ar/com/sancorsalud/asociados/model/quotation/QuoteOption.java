package ar.com.sancorsalud.asociados.model.quotation;

import java.io.Serializable;

/**
 * Created by sergio on 2/3/17.
 */

public class QuoteOption extends Object implements Serializable {
    public String title;
    public String id;
    public String extra;
    public String extra2;

    public QuoteOption(){}
    public QuoteOption(String id, String title){
        this.id = id;
        this.title = title;
    }

    public QuoteOption(String id, String title, String extra){
        this.id = id;
        this.title = title;
        this.extra = extra;
    }

    public String optionName(){
        return title;
    }

    public boolean equals(Object obj) {
        QuoteOption q = (QuoteOption) obj;
        return (this.id.equalsIgnoreCase(q.id));
    }

}
