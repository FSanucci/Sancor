package ar.com.sancorsalud.asociados.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;

/**
 * Created by sergiocirasa on 29/3/17.
 */

public class Notification implements Serializable {
    public long id;
    public long notificationId;
    public String title;
    public Date date;
    public boolean isRead;
    public String description;
    public String link;
    public String owner;
    public Priority priority;

    public List<AttachFile> files = new ArrayList<AttachFile>();


    public enum Priority implements Serializable {
        HIGHT,
        MEDIUM,
        LOW;
    }


    public static Notification.Priority getPriority(String priority) {
        if (priority.equals("ALTA")) {
            return Priority.HIGHT;
        } else if (priority.equals("MEDIA")) {
            return Priority.MEDIUM;
        } else if (priority.equals("BAJA")) {
            return Priority.LOW;
        } else
            return null;
    }

    public static String getPriority(Priority priority) {
        if (priority == Priority.HIGHT ) {
            return "ALTA";
        } else if (priority == Priority.MEDIUM ) {
            return "MEDIA";
        } else if(priority == Priority.LOW ) {
            return "BAJA";
        } else
            return null;
    }


}



