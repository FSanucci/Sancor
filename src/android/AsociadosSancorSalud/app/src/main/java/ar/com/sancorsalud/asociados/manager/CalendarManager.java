package ar.com.sancorsalud.asociados.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.model.Appointment;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by francisco on 26/10/16.
 */

public class CalendarManager {

    private static final String TAG = "CAL_MGR";
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static CalendarManager instance = new CalendarManager();

    public static synchronized CalendarManager getInstance() {
        return instance;
    }

    private Context ctx;

    public void init(Context ctx) {
        this.ctx = ctx;
    }

    public Appointment addNewEventToCalendar(ProspectiveClient client) throws SecurityException {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance();
        ContentResolver cr = ctx.getContentResolver();
        ContentValues values = new ContentValues();

        Appointment appointment = client.appointment;
        if(client.appointment==null){
            appointment = new Appointment();
            appointment.address = client.getAddressString();
            appointment.prospectiveClientId = client.id;
            appointment.date = new Date(cal.getTimeInMillis());
            appointment.notes = "Sancord Salud - Entrevista con "+client.getFullName();
        }

        long now = appointment.date.getTime();
        values.put(CalendarContract.Events.DTSTART, now);
        values.put(CalendarContract.Events.DTEND, now + 60 * 60 * 1000);
        values.put(CalendarContract.Events.TITLE, client.getFullName());
        values.put(CalendarContract.Events.DESCRIPTION, appointment.notes);
        values.put(CalendarContract.Events.CALENDAR_ID, ConstantsUtil.CALENDAR_ID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        values.put(CalendarContract.Events.EVENT_LOCATION, appointment.address);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        appointment.scheduleId = Long.parseLong(uri.getLastPathSegment());
        return appointment;
    }

    public Appointment getEventDetails(long eventId) {
        try {
            String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.EVENT_LOCATION,CalendarContract.Events.DESCRIPTION};
            Cursor cursor = ctx.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, CalendarContract.Events._ID  + "=?",new String[]{""+eventId}, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0).equals(""+eventId)){
                        long milliSeconds = Long.valueOf(cursor.getString(2));
                        Appointment appointment = new Appointment();
                        appointment.scheduleId = Long.parseLong(cursor.getString(0));
                        appointment.address = cursor.getString(3);
                        appointment.date = new Date(milliSeconds);
                        appointment.notes = cursor.getString(4);
                        return appointment;
                    }
                } while (cursor.moveToNext());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteAppointment(long eventId){
        ContentResolver cr = ctx.getContentResolver();
        Uri eventsUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(eventId)));
        cr.delete(eventsUri, null, null);
    }

/*
    public String updateEventDate() throws SecurityException {

        String sEventDate = null;
        long milliSeconds = -1;

        Log.e(TAG, "updateEventDate----------------");
        long eventID = Storage.getInstance().getEventId();

        String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};
        Cursor cursor = ctx.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);

        List<String> events = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(Long.toString(eventID))) {
                    Log.e(TAG, cursor.getString(0) + ":" + cursor.getString(1) + ":" + cursor.getString(2) + "   ------------------------------------------------------");

                    milliSeconds = Long.valueOf(cursor.getString(2));
                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

                    // Create a calendar object that will convert the date and time value in milliseconds to date.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(milliSeconds);
                    sEventDate =  formatter.format(calendar.getTime());

                    break;
                }
            } while (cursor.moveToNext());
        }
        return sEventDate;
    }


    public void printEvents() {
        try {
            String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE};

            Cursor cursor = ctx.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, null, null, null);

            List<String> events = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    Log.e(TAG, cursor.getString(0) + ":" + cursor.getString(1));

                } while (cursor.moveToNext());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public long getMaxID(Uri cal_uri) {
        Uri local_uri = cal_uri;
        if (cal_uri == null) {
            local_uri = Uri.parse("content://com.android.calendar/events");
        }

        Cursor cursor = ctx.getContentResolver().query(local_uri, new String[]{ "MAX(_id) as max_id" }, null, null, "_id");

        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));

        return max_val;

    }
*/
}
