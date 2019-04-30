package ar.com.sancorsalud.asociados.rest.services.delete;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

public class HDeleteRemoveFileAmazonRequest extends HRequest<Void> {

    private static final String PATH = RestConstants.HOST_DIGITALIZATION + RestConstants.DELETE_AMAZON_FILE;

    private long attachFileId;

    public HDeleteRemoveFileAmazonRequest(long attachFileId, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.DELETE, PATH, listener, errorListener);

        this.attachFileId = attachFileId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("archivoId", attachFileId);
            json.put("appId", RestConstants.APP_ID_FOR_AMAZON);

            Log.d("Delete amazon file","Entró ---------------------------------");

            String body = json.toString();
            byte bytes[] = body.getBytes(Charset.forName("UTF-8"));
            return bytes;

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
                    Log.e(TAG, dic.toString());

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
