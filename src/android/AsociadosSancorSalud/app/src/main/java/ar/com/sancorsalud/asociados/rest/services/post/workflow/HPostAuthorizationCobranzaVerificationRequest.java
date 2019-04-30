package ar.com.sancorsalud.asociados.rest.services.post.workflow;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.affiliation.WorkflowAuthorization;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostAuthorizationCobranzaVerificationRequest extends HRequest<WorkflowAuthorization> {

    private static final String TAG = "HPOST_VERIF_CBRZ";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFICATION_AUTHORIZATION_COBRZ;

    private long mPaId;
    private boolean mForCarga = false;

    public HPostAuthorizationCobranzaVerificationRequest(long paId, boolean forCarga, Response.Listener<WorkflowAuthorization> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mPaId = paId;
        this.mForCarga = forCarga;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mPaId);
            json.put("carga", mForCarga);

            Log.e(TAG, "JSON VERIF_CBRZ: "  +  json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG ,  "Error verifing cobranza...");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected WorkflowAuthorization parseObject(Object obj) {

        WorkflowAuthorization  workflowAuthorization = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    Log.e (TAG, "VERIF_CBRZ RESPONSE: " + dic.toString());

                    workflowAuthorization = new WorkflowAuthorization();
                    workflowAuthorization.state = dic.optInt("estado", -1);
                    workflowAuthorization.authorization = dic.optBoolean("autorizacion", false);
                    workflowAuthorization.control = dic.optBoolean("control", false);
                    workflowAuthorization.mssg = dic.optString("mensaje");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return workflowAuthorization;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
