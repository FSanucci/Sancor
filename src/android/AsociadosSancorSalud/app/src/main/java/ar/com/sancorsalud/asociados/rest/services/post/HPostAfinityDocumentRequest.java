package ar.com.sancorsalud.asociados.rest.services.post;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostAfinityDocumentRequest extends HRequest<Void> {

    private static final String TAG = "HPOST_ADD_AFFINITY";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_AFFINITY_DOCUMENT;

    private long fichaId;
    private long documentId;

    public HPostAfinityDocumentRequest(long fichaId, long documentId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.fichaId = fichaId;
        this.documentId = documentId;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("fichaId", fichaId);
            json.put("documentoId", documentId);

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
        return null;
    }
}
