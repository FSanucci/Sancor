package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Date;

import ar.com.sancorsalud.asociados.model.Appointment;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HGetProspectiveClientProfileRequest extends HRequest<ProspectiveClient> {

    private static final String PATH = RestConstants.HOST + RestConstants.GET_PROSPECTIVE_CLIENT_PROFILE;
    private ProspectiveClient mClient;

    public HGetProspectiveClientProfileRequest(ProspectiveClient client, Response.Listener<ProspectiveClient> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH + client.id, listener, errorListener);
        mClient = client;
    }

    public HGetProspectiveClientProfileRequest(long potencialId, Response.Listener<ProspectiveClient> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH + potencialId, listener, errorListener);
        mClient = new ProspectiveClient();
    }


    @Override
    protected ProspectiveClient parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                mClient.firstname = ParserUtils.optString(dic, "nombre");
                mClient.lastname = ParserUtils.optString(dic, "apellido");
                mClient.age = dic.optInt("edad", 0);
                mClient.statusComment = ParserUtils.optString(dic, "comentario");
                Date date = ParserUtils.parseDate(dic, "fecha_asig_prom", "yyyy-MM-dd");
                mClient.assignedDate = date;

                mClient.assignedDate = ParserUtils.parseDate(ParserUtils.optString(dic, "fechaAsignadoPromotor"));
                mClient.dni = dic.optInt("dni", 0);

                date = ParserUtils.parseDateTime(ParserUtils.optString(dic, "fechaAgenda"));
                if (date != null) {
                    if (mClient.appointment == null)
                        mClient.appointment = new Appointment();


                    mClient.appointment.date = date;
                    mClient.appointment.notes = ParserUtils.optString(dic, "notas");
                    mClient.appointment.prospectiveClientId = mClient.id;
                    mClient.appointment.scheduleId = dic.optInt("calendario", -1);
                    String str = ParserUtils.optString(dic, "direccion_encuentro");
                    if (str != null)
                        mClient.appointment.address = str;
                }

                mClient.tempQuote = ParserUtils.optString(dic, "cotizacion_temp");
                mClient.phoneNumber = ParserUtils.optString(dic, "telefono");
                mClient.celularNumber = ParserUtils.optString(dic, "celular");
                mClient.email = ParserUtils.optString(dic, "mail");
                mClient.street = ParserUtils.optString(dic, "calle");
                mClient.streetNumber = dic.optInt("puerta", -1);
                mClient.floorNumber = dic.optInt("piso", -1);
                mClient.department = ParserUtils.optString(dic, "departamento");
                mClient.zip = ParserUtils.optString(dic, "cp");

                mClient.cardId = dic.optLong("ficha_id", -1);

                mClient.loadFromQR = dic.optBoolean("cargado_por_qr", false);

                Log.e(TAG, "PotencialAsociado_Perfil: " + dic.toString());

                return mClient;
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
