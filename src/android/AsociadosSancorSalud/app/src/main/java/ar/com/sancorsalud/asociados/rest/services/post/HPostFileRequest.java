package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;


public class HPostFileRequest extends HRequest<Long> {

    private static final String TAG = "HPOST_FILE";
    private static final String PATH = RestConstants.HOST + RestConstants.POST_ADD_FILE;
    //private static final String PATH = RestConstants.HOST_DIGITALIZATION + RestConstants.POST_ADD_AMAZON_FILE;

    private AttachFile inputFile;
    private String encodedImage;


    public HPostFileRequest(AttachFile inputFile, String encodedImage , Response.Listener<Long> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.inputFile = inputFile;
        this.encodedImage = encodedImage;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {

            // esto es viejo: body perteneciente a la url ServicioFichas/Alta_Archivo
            json.put("imagen", encodedImage);
            json.put("nombre_imagen", inputFile.fileName);
            json.put("extension_imagen", inputFile.fileExtension);


            // nuevo body de amazon
            /*json.put("appId", RestConstants.APP_ID_FOR_AMAZON);
            json.put("stringImagen", encodedImage);
            json.put("nombreArchivo", inputFile.fileName);
            json.put("extensionArchivo", inputFile.fileExtension);*/

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Long parseObject(Object obj) {

        Long fileId = -1L;
        if (obj instanceof JSONObject) {

            // esto es viejo: perteneciente a la url ServicioFichas/Alta_Archivo
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);

            //respuesta del servicio nuevo: amazon
            //JSONObject dic = (JSONObject) obj;
            if (dic != null) {
                try {
                    Log.e (TAG, dic.toString());

                    // esto es viejo: perteneciente a la url ServicioFichas/Alta_Archivo
                     fileId = dic.optLong("archivo_id", -1L);

                    //respuesta del servicio nuevo: amazon
                    //fileId = dic.optLong("id", -1L);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return fileId;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        // esto es viejo: perteneciente a la url ServicioFichas/Alta_Archivo
        return ParserUtils.parseError((JSONObject) obj);

        //respuesta del servicio nuevo: amazon
        //return null;
    }
}
