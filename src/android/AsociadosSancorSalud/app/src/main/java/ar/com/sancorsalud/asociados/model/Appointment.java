package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sergio on 11/17/16.
 */

public class Appointment extends Object implements Serializable {
    public long prospectiveClientId;
    public Date date;
    public String notes;
    public long scheduleId = -1;
    public String address;
}
