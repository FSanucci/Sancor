package ar.com.sancorsalud.asociados.model.user;

public class Salesman {

    public long id = -1;
    public String firstname = "";
    public String lastname = "";
    public String docNumber = "";
    public String zone = "";
    public long zoneId = -1;

    public int pendingAssignments = 0;
    public int totalAssignments = 0;
    public String description = "";

    public int totalManualQuotes = 0;

    public String getFullName(){
        return firstname+" "+lastname;
    }
}
