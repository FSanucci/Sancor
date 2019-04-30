package ar.com.sancorsalud.asociados.rest.services.post;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Date;

import ar.com.sancorsalud.asociados.model.Car;
import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;

/**
 * Created by sergiocirasa on 28/9/17.
 */

public class HChangeCardStateRequest extends HRequest<ExistenceStatus> {

    private static final String TAG = "HPostDefaultCar";
    private static final String PATH = RestConstants.HOST + RestConstants.CHANGE_STATE_CARD;
    private long mCardId;
    private ProspectiveClient.State mState;
    private Car mCar;
    private String mComments;

    public HChangeCardStateRequest(long cardId, ProspectiveClient.State state, Car car,String comments, Response.Listener<ExistenceStatus> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, PATH, listener, errorListener);
        this.mCardId = cardId;
        this.mCar = car;
        this.mState = state;
        this.mComments = comments;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("ficha_id", mCardId  != -1L ?  mCardId : JSONObject.NULL);
            json.put("car_id", mCar != null ? mCar.id:  JSONObject.NULL );
            json.put("fecha_carga", ParserUtils.parseDate(new Date(),"yyyy-MM-dd"));
            json.put("estado_id", mState != null ? mState.getValue():  JSONObject.NULL);
            json.put("comentarios", mComments != null ?  mComments:  JSONObject.NULL  );

            String body = json.toString();

            Log.e(TAG, "HChangeCardStateRequest: "  +  json.toString());

            return body.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ExistenceStatus parseObject(Object obj) {

        ExistenceStatus response = null;

        if (obj instanceof JSONObject) {

            JSONObject dic = (JSONObject) obj;
            if (dic != null) {
                try {
                    Log.e (TAG, "Change State response: " + dic.toString());

                    response = new ExistenceStatus();
                    response.status = ParserUtils.optString(dic, "status");
                    response.errorCode = dic.optInt("errorCode",-1);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return response;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }


}
