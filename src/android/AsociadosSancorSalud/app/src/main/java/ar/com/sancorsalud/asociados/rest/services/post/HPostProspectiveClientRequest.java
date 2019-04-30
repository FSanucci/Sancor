package ar.com.sancorsalud.asociados.rest.services.post;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergio on 1/6/17.
 */

public class HPostProspectiveClientRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST + RestConstants.ADD_PROSPECTIVE_CLIENT;
    private ProspectiveClient mClient;
    private long mUserId;
    private boolean mIsSalesman;
    private String mIsCompany;
    private boolean mLoadFromQr;

    public HPostProspectiveClientRequest(long userId, boolean isSalesman, String isCompany, boolean loadFromQr, ProspectiveClient client, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        mClient = client;
        mUserId = userId;
        mIsSalesman = isSalesman;
        mIsCompany = isCompany;
        mLoadFromQr = loadFromQr;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("apellido", mClient.lastname);
            json.put("nombre", mClient.firstname);
            json.put("dni", mClient.dni);

            if (mClient.birthday != null)
                json.put("fecha_nac", ParserUtils.parseDate(mClient.birthday));

            if (mClient.location != null)
                json.put("ciudad", mClient.location);

            if (mClient.zip != null)
                json.put("cp", mClient.zip);

            if (mClient.phoneNumber != null)
                json.put("tel", mClient.phoneNumber);

            if (mClient.celularNumber != null)
                json.put("cel", mClient.celularNumber);

            if (mClient.email != null)
                json.put("mail", mClient.email);

            if (mClient.street != null)
                json.put("calle", mClient.street);

            if (mClient.streetNumber != -1) {
                json.put("puerta", mClient.streetNumber);
            }
            if (mClient.floorNumber != -1) {
                json.put("piso", mClient.floorNumber);
            }

            if (mClient.department != null)
                json.put("departamento", mClient.department);

            if (mIsSalesman) {
                json.put("promotor_id", mUserId);
            } else {
                json.put("referente_id", mUserId);
            }

            json.put("es_empresa", mIsCompany);

            if (mClient.zone != null)
                json.put("nro_zona", mClient.zone.id);

            json.put("cargado_por_qr", mLoadFromQr);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Void parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {

            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
