package ar.com.sancorsalud.asociados.rest.services.get;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

public class HGetMyProspectiveClientsRequest extends HRequest<ArrayList<ProspectiveClient>> {

    //private static final String PATH = RestConstants.HOST + RestConstants.GET_MY_PROSPECTIVE_CLIENT;
    private static final String PATH = RestConstants.HOST + RestConstants.GET_PAGING_PA_LIST + "?size=1000&page=1&filtroEstadoPotencial=35";


    public HGetMyProspectiveClientsRequest(Response.Listener<ArrayList<ProspectiveClient>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
    }

    @Override
    protected ArrayList<ProspectiveClient> parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);
            //TODO: Ver lo q devuelve el WS
            if(dic!=null){
                try {
                    JSONArray reasons = dic.getJSONArray("potenciales");
                    if(reasons!=null) {
                        ArrayList<ProspectiveClient> list = new ArrayList<>();
                        for (int i = 0; i < reasons.length(); i++) {
                            //TODO: Ver los ids devueltos por el WS
                            JSONObject pc = reasons.optJSONObject(i);

                            ProspectiveClient client = new ProspectiveClient();
                            client.id = pc.optInt("potencial_id",-1);
                            client.firstname = ParserUtils.optString(pc, "nombre");
                            client.lastname = ParserUtils.optString(pc, "apellido");
                            client.age = pc.optInt("edad",0);
                            client.description = ParserUtils.optString(pc,"descripcion");
                            client.countOfDaysAssignedToLeader = pc.optInt("dias_asig_ref",0);
                            client.countOfDaysAssignedToSalesman = pc.optInt("dias_asig_pro",0);
                            client.dni = pc.optLong("dni",0);

                            client.assignedDate = ParserUtils.parseDate(pc,"fecha_asig_prom","yyyy-MM-dd");
                            client.birthday = ParserUtils.parseDate(pc,"fecha_nac","yyyy-MM-dd");
                            client.phoneNumber = ParserUtils.optString(dic,"telefono");

                            client.quotatedLink = ParserUtils.optString(pc, "link_cotizacion") != null ? ParserUtils.optString(pc, "link_cotizacion").trim(): null;
                            client.cardId = pc.optLong("ficha_id", -1);
                            client.salesmanId = pc.optLong("promotor_id",-1);
                            client.cardId = pc.optLong("ficha_id",-1);

                            //TODO: Descomentar
                            Date date = ParserUtils.parseDateTime(ParserUtils.optString(pc,"fecha_agenda"));
                            if(date!=null){
                                client.appointment = new Appointment();
                                client.appointment.date = date;
                                client.appointment.notes = ParserUtils.optString(pc,"notas");
                                client.appointment.prospectiveClientId = pc.optLong("potencial_id",-1);;
                                client.appointment.scheduleId = pc.optInt("calendario_id",-1);
                                client.appointment.address = ParserUtils.optString(pc,"direccion_encuentro");
                            }

                            int state = pc.optInt("estado_id",-1);
                            client.state = ProspectiveClient.State.getState(state);

                            String filtro = ParserUtils.optString(pc, "filtro");
                            client.filter = ProspectiveClient.Filter.getFilter(filtro);

                            list.add(client);
                        }
                        return list;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }

}


