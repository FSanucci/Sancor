package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;

/**
 * Created by sergiocirasa on 28/9/17.
 */

public class Car extends Object implements Serializable {
    public long id;
    public String description;

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Car) {
            return ((Car) obj).id == id;
        }
        return false;
    }
}
