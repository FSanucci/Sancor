package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.DecodedFile;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostGetFileImageRequest extends HRequest<DecodedFile> {

    private static final String TAG = "HPOST_GET_FILE_IMAGE";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_GET_FILE_IMAGE;

    private long resourceId;

    public HPostGetFileImageRequest(long resourceId, Response.Listener<DecodedFile> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.resourceId = resourceId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("archivo_id", resourceId);

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    protected DecodedFile parseObject(Object obj) {

        DecodedFile decodedFile = null;
        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {

                Log.e(TAG, "DECODED FILE : "  + dic.toString());
                decodedFile = new DecodedFile();

                try {
                    Log.e (TAG, dic.toString());
                    decodedFile.fileNameAndExtension = ParserUtils.optString(dic, "nombre_imagen") != null ? ParserUtils.optString(dic, "nombre_imagen").trim() : null;
                    decodedFile.fileImage = ParserUtils.optString(dic, "imagen") != null ? ParserUtils.optString(dic, "imagen"): null;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

            }
        }
        return decodedFile;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
