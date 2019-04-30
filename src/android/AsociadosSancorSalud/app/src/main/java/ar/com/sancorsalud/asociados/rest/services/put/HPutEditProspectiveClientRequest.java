package ar.com.sancorsalud.asociados.rest.services.put;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 2/1/17.
 */

public class HPutEditProspectiveClientRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.EDIT_PROSPECTIVE_CLIENT;
    private ProspectiveClient mClient;
    private boolean mIsSalesman;
    private String mIsCompany;
    private boolean mLoadFromQr;

    public HPutEditProspectiveClientRequest(boolean isSalesman,String isCompany, boolean loadFromQr, ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, PATH, listener, errorListener);
        mClient = client;
        mIsSalesman = isSalesman;
        mIsCompany = isCompany;
        mLoadFromQr = loadFromQr;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        //  headers.put("Content-Type", "application/json; charset=utf-8");

        if (mHttpMethod == Method.PATCH) {
            headers.put("X-HTTP-Method-Override", "PATCH");
        }

        headers.put("Authorization", "Basic " + RestConstants.API_KEY);

        if (HRequest.authorizationHeaderValue != null)
            headers.put("token", HRequest.authorizationHeaderValue);
        return headers;
    }


    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mClient.id);
            json.put("apellido", mClient.lastname);
            json.put("nombre", mClient.firstname);
            json.put("dni", mClient.dni);
            json.put("fecha_nac", ParserUtils.parseDate(mClient.birthday));
            json.put("cp", mClient.zip);
            json.put("tel", mClient.phoneNumber);
            json.put("cel", mClient.celularNumber);
            json.put("mail", mClient.email);
            json.put("calle", mClient.street );

            if(mClient.streetNumber!=-1){
                json.put("puerta", mClient.streetNumber);
            }
            if(mClient.floorNumber!=-1){
                json.put("piso", mClient.floorNumber);
            }

            if(mClient.department!=null)
                json.put("departamento", mClient.department);

            json.put("es_empresa", mIsCompany);
            json.put("nro_zona", mClient.zone.id);

            json.put("cargado_por_qr", mLoadFromQr);

        }catch(Exception e){

        }

        String body = json.toString();
        return body.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    protected Void parseObject(Object obj) {
        if(obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject)obj);
            if(dic!=null){

            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj){
        return ParserUtils.parseError((JSONObject)obj);
    }
}
