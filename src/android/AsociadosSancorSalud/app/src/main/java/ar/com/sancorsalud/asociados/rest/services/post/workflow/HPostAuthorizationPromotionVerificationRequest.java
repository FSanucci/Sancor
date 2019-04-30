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


public class HPostAuthorizationPromotionVerificationRequest extends HRequest<WorkflowAuthorization> {

    private static final String TAG = "HPOST_VERIF_PROMO";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_VERIFICATION_AUTHORIZATION_PROMO;

    private long mPaId;

    public HPostAuthorizationPromotionVerificationRequest(long paId, Response.Listener<WorkflowAuthorization> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mPaId = paId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mPaId);
            Log.e(TAG, "JSON VERIF_PROMO: "  +  json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG ,  "Error verifing promotion...");
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
                    Log.e (TAG, "VERIF_PROMO RESPONSE: " + dic.toString());

                    workflowAuthorization = new WorkflowAuthorization();
                    workflowAuthorization.state = dic.optInt("estado");
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
