package ar.com.sancorsalud.asociados.model.client;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.Appointment;
import ar.com.sancorsalud.asociados.utils.DateUtils;

/**
 * Created by sergio on 11/2/16.
 */

public class ProspectiveClient extends Client {

    public Date assignedDate;
    public Date expirationDate;
    public Filter filter;
    public State state;
    public String statusComment;
    public Appointment appointment;
    public int myFilter;

    public int countOfDaysAssignedToSalesman=0;
    public int countOfDaysAssignedToLeader=0;

    public long salesmanId;
    public String salesmanName;
    public String tempQuote;

    public String quotatedLink;

    public boolean loadFromQR;

    public enum State implements Serializable {
        CARDS_IN_PROCESS(1),
        PENDING_AUDITOR_DOC(2),
        INCORRECT_CARD(3),
        PENDING_SEND_PROMOTION_CONTROL_SUPPORT(4),
        SEND_PROMOTION_CONTROL_SUPPORT(5),
        SENT_CAR(6),
        SENT_FILE(7),
        PENDING_DOC(8),
        SENT_CONTROL_SUPPORT(9),
        CLOSED_CARD(10),
        SENT_COMERCIAL_CONTROL(11),
        SEND_COBRANZA(12);

        private final int value;

        private State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State getState(int state) {
            if (state == 1)
                return State.CARDS_IN_PROCESS;
            else if (state == 2)
                return State.PENDING_AUDITOR_DOC;
            else if (state == 3)
                return State.INCORRECT_CARD;
            else if (state == 4)
                return State.PENDING_SEND_PROMOTION_CONTROL_SUPPORT;
            else if (state == 5)
                return State.SEND_PROMOTION_CONTROL_SUPPORT;
            else if (state == 6)
                return State.SENT_CAR;
            else if (state == 7)
                return State.SENT_FILE;
            else if (state == 8)
                return State.PENDING_DOC;
            else if (state == 9)
                return State.SENT_CONTROL_SUPPORT;
            else if (state == 10)
                return State.CLOSED_CARD;
            else if (state == 11)
                return State.SENT_COMERCIAL_CONTROL;
            else if (state == 11)
                return State.SEND_COBRANZA;
            else
                return null;
        }
    }


    public enum Filter implements Serializable{
        NO_SCHEDULED("S", 35),
        SCHEDULED("A", 8),
        QUOTED("C", 5);

        // not in use replace by card state
        //PENDING_CARDS("FP"),
        //INCORRECT_CARDS("FC"),
        //FINISHED_CARDS("FT");

        private final String text;
        private final int value;

        private Filter(final String text, int value) {
            this.text = text;
            this.value = value;
        }

        @Override
        public String toString() {
            return text;
        }

        public int getValue() {
            return value;
        }

        public  static Filter getFilter(String filtro){
            if(filtro!=null){

                if(filtro.equals("S"))
                    return  ProspectiveClient.Filter.NO_SCHEDULED;
                else if(filtro.equals("A"))
                    return  ProspectiveClient.Filter.SCHEDULED;
                else if(filtro.equals("C"))
                    return ProspectiveClient.Filter.QUOTED;

                /*
                else if(filtro.equals("FP"))
                    return  ProspectiveClient.Filter.PENDING_CARDS;
                else if(filtro.equals("FC"))
                    return  ProspectiveClient.Filter.INCORRECT_CARDS;
                else if(filtro.equals("FT"))
                    return  ProspectiveClient.Filter.FINISHED_CARDS;
                    */

            }
            return null;
        }

        public static Filter getFilterByValue(int value) {
            if (value == 100)
                return ProspectiveClient.Filter.NO_SCHEDULED;
            else if (value == 8)
                return ProspectiveClient.Filter.SCHEDULED;
            else if (value == 5)
                return ProspectiveClient.Filter.QUOTED;
            else return null;
        }
    }

    public String getAddressString(){
        String str = "";
        if(street!=null) {
            str = street + " ";

            if(streetNumber!=-1)
                str = str + streetNumber;

            if(department!=null)
                str = str + " - " + (floorNumber != -1 ? floorNumber: "") + department;
        }

        return str;
    }

    public long getDaysToExpire() {
        if(expirationDate==null && assignedDate!=null){
            Calendar c = Calendar.getInstance();
            c.setTime(assignedDate);
            c.add(Calendar.DATE, 30);
            expirationDate = c.getTime();
        }

        if(expirationDate==null || DateUtils.isToday(expirationDate))
            return 0;

        Date today = new Date();
        long diff =  expirationDate.getTime() - today.getTime();
        long days = (long) Math.ceil(diff /86400000.0);
        return days==0?1:days;
    }

    public boolean isAssigned() {
        return salesmanId != -1;
    }

    public boolean isPending(){
        return (filter == Filter.NO_SCHEDULED);
    }

    public boolean isQuoted(){
        return (filter == Filter.QUOTED);
    }

    public boolean isScheduled(){
        return (filter == Filter.SCHEDULED);
    }

    public void setQuoted(){
        filter = Filter.QUOTED;
    }


}
