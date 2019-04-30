package ar.com.sancorsalud.asociados.rest.services.post.workflow;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationCobranza;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostAuthorizationCobranzaSaveRequest extends HRequest<Void> {

    private static final String TAG = "HPOST_SAVE_AUTH_COBRZ";
    private static final String PATH = RestConstants.HOST + RestConstants.SAVE_AUTHORIZATION_COBRANZA;

    private AuthorizationCobranza mAuthorization;

    public HPostAuthorizationCobranzaSaveRequest(AuthorizationCobranza authorization, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mAuthorization = authorization;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mAuthorization.paId != -1L ?  mAuthorization.paId : JSONObject.NULL );
            json.put("caso",  "R");

            JSONArray jFilesArray = new JSONArray();
            if (!mAuthorization.files.isEmpty()) {
                for (AttachFile file : mAuthorization.files) {
                    jFilesArray.put(file.id);
                }
            }
            json.put("archivos", jFilesArray);

            if (mAuthorization.comment != null && !mAuthorization.comment.isEmpty()) {
                json.put("comentario", mAuthorization.comment);
            }

            Log.e(TAG, "SAVE JSON: " + json.toString());
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
                try {
                    Log.e (TAG, dic.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
