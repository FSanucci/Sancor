package ar.com.sancorsalud.asociados.rest.core;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


/**
 * Created by sergio on 12/2/15.
 */
public class ParserUtils {


    public static final String TAG = "PARSE_UTILS";

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";


    public static String parseToJSON(Object obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T parseFromJSON(String json, Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }

    public static <T> T parseFromJSON(String json, Type cls) {
        return new Gson().fromJson(json, cls);
    }

    public static Integer optInt(JSONObject json, String key) {
        if (json.isNull(key))
            return -1;
        else
            return json.optInt(key, -1);
    }

    // Fixed bug:  "null"  are replaced by null --> http://code.google.com/p/android/issues/detail?id=13830
    public static String optString(JSONObject json, String key) {
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }

    public static String optString(JSONObject json, String key, String fallback) {
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, fallback);
    }

    public static Date parseDateTime(JSONObject jsonObj, String key) {
        return ParserUtils.parseDate(jsonObj, key, DATE_TIME_FORMAT);
    }

    public static Date parseDate(JSONObject jsonObj, String key, String dateFormat) {
        String dateStr = optString(jsonObj, key);
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDateTime(String dateStr) {
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDate(String dateStr) {
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());

                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDate(String dateStr, String dateFormat) {
        if (dateStr != null ) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());

                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static String parseDate(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        //format.setTimeZone(TimeZone.getDefault());

        return format.format(date);
    }

    public static String parseDate(Date date) {
        return ParserUtils.parseDate(date, DATE_FORMAT);
    }

    public static String parseDateTime(Date date) {
        return ParserUtils.parseDate(date, DATE_TIME_FORMAT);
    }

    public static String parseTime(Date date) {
        return ParserUtils.parseDate(date, TIME_FORMAT);
    }

    public static String printableDate(Date date){
        /*
        Date today = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        int month1 = cal1.get(Calendar.MONTH);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int month2 = cal2.get(Calendar.MONTH);

        if(month1 != month2)*/
        return ParserUtils.parseDate(date,"dd 'de' MMMM 'de' yyyy', 'HH:mm' hs'");
        //else return StringHelper.uppercaseFirstCharacter(ParserUtils.parseDate(date,"EEEE dd', 'hh:mm' hs'"));
    }


    public static HVolleyError parseError(JSONObject obj){
        if(obj==null)
            return new HVolleyError("Error Inesperado",0);

        String status = ParserUtils.optString(obj,"status");
        if(status==null)
            return new HVolleyError("Error Inesperado",0);

        if(!status.equalsIgnoreCase("success")) {
            if(ParserUtils.optString(obj,"message")!=null ){
                //return new HVolleyError(ParserUtils.optString(obj,"message"), obj.optInt("errorCode",0), ParserUtils.optString(obj,"data") );
                return new HVolleyError(ParserUtils.optString(obj,"message"), obj.optInt("errorCode",0) );

            }else return new HVolleyError("Error Inesperado", obj.optInt("errorCode",0));
        }
        return null;
    }

    public static JSONObject parseResponse(JSONObject obj){
        if(obj==null)
            return null;

        try {
            return obj.getJSONObject("data");
        }catch (JSONException e){
            return null;
        }
    }

    public static ArrayList<QuoteOption> parseQuestionOptions(JSONArray jsonArray, String objId, String objDesc, String objExtra1) {
        return parseQuestionOptions(jsonArray, objId, objDesc, objExtra1, null);
    }


    public static ArrayList<QuoteOption> parseQuestionOptions(JSONArray jsonArray, String objId, String objDesc, String objExtra1, String objExtra2){
        ArrayList<QuoteOption> array = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.optJSONObject(i);
            QuoteOption q = new QuoteOption();

            try {
                Object aObj = obj.get(objId);
                if(aObj instanceof Integer) {
                    q.id = "" + obj.optInt(objId);
                }else if(aObj instanceof String){
                    q.id = ParserUtils.optString(obj, objId).trim();
                }
            }catch (Exception e){}

            if(q.id!=null) {
                q.title = ParserUtils.optString(obj, objDesc).trim();

                if (objExtra1 != null)
                    q.extra = ParserUtils.optString(obj, objExtra1).trim();

                if (objExtra2 != null){
                    q.extra2 = ParserUtils.optString(obj, objExtra2).trim();
                }

                array.add(q);
            }

        }
        return array;
    }

    public static ArrayList<QuoteOption> parseBancos(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("bancos");
            return ParserUtils.parseQuestionOptions(jsonArray,"banco_id","banco_descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseFormasPago(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("formas");

            // TODO check THIS !
            // Remove elements with ¨visible = false¨
            JSONArray visibleList = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject xobj = jsonArray.getJSONObject(i);
                if(xobj.getBoolean("visible"))
                {
                    visibleList.put(xobj);
                }
            }

            return ParserUtils.parseQuestionOptions(visibleList,"forma_id","forma_descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseFormasCoPago(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("formas");
            return ParserUtils.parseQuestionOptions(jsonArray,"forma_id","forma_descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }




    public static ArrayList<QuoteOption> parseTarjetas(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("tarjetas");
            return ParserUtils.parseQuestionOptions(jsonArray,"tarjeta_id","tarjeta_tipo","tarjeta_descripcion");  // en tipo viene la descr esta invertido

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseCoberturas(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("coberturas");
            return ParserUtils.parseQuestionOptions(jsonArray,"codigo_os","nombre_os",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseOSDesregula(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("os_desregula");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion","tipo");

        } catch (Exception e) {
        }
        return null;
    }


    public static ArrayList<QuoteOption> parseOSState(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("os_estados");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion","marca");

        } catch (Exception e) {
        }
        return null;
    }




    public static ArrayList<QuoteOption> parseCategorias(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("categorias");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseCondicionIva(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("condiciones_iva");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseSegmentos(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("segmento");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseFormasIngreso(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("forma_de_ingresos");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseParentesco(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("parentescos");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseSexo(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("sexo");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseCivilStatus(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("estados_civiles");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion", null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseDocTypes(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("tipos_documentos");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion", null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseNationalities(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("nacionalidades");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion", null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseOrientations(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("orientacion");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion", null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseAddressAttributes(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("atributos_domicilio");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion", null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseAccountTypes(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("tipo_cuenta");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parseAltaTypes(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("tipo_alta");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parsePriority(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("priority");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<QuoteOption> parsePromoters(JSONObject obj){
        JSONObject data = ParserUtils.parseResponse(obj);
        try {
            JSONArray jsonArray = data.getJSONArray("promoters");
            return ParserUtils.parseQuestionOptions(jsonArray,"id","descripcion",null);

        } catch (Exception e) {
        }
        return null;
    }
}
