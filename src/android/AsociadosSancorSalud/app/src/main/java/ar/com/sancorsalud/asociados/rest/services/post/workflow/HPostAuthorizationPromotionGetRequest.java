package ar.com.sancorsalud.asociados.rest.services.post.workflow;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationState;
import ar.com.sancorsalud.asociados.model.affiliation.BaseAuthorization;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;


public class HPostAuthorizationPromotionGetRequest extends HRequest<List<AuthorizationPromotion>> {

    private static final String TAG = "HPOST_GET_AUTH_PROM";
    private static final String PATH = RestConstants.HOST + RestConstants.GET_AUTHORIZATION_PROMOTION;

    private long mPaId;


    public HPostAuthorizationPromotionGetRequest(long paId, Response.Listener<List<AuthorizationPromotion>> listener, Response.ErrorListener errorListener) {
        super(Method.POST, PATH, listener, errorListener);
        this.mPaId = paId;
    }

    @Override
    public byte[] getBody() {

        JSONObject json = new JSONObject();
        try {
            json.put("potencial_id", mPaId);
            Log.e(TAG, "JSON GET_AUTH_CBRZ: " + json.toString());

            String body = json.toString();
            return body.getBytes(Charset.forName("UTF-8"));

        } catch (Exception e) {

            Log.e(TAG, "Error GET_AUTH_CBRZ...");

            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<AuthorizationPromotion> parseObject(Object obj) {

        List<AuthorizationPromotion> authArray = new ArrayList<AuthorizationPromotion>();

        if (obj instanceof JSONObject) {
            JSONObject dic = (JSONObject) ParserUtils.parseResponse((JSONObject) obj);
            if (dic != null) {
                Log.e(TAG, "GET_AUTH_CBRZ RESP: " + dic.toString());
                try {
                    JSONArray jAuthArray = dic.getJSONArray("autorizaciones");
                    if (jAuthArray != null) {
                        for (int k = 0; k < jAuthArray.length(); k++) {

                            AuthorizationPromotion auth = new AuthorizationPromotion();
                            JSONObject jAuth = jAuthArray.getJSONObject(k);

                            auth.cardId = jAuth.optLong("idFicha", -1L);

                            JSONObject jState = jAuth.optJSONObject("ultimoEstado");
                            if (jState != null) {
                                auth.state = new AffiliationState();
                                auth.state.id = jState.optLong("id", -1L);
                                auth.state.description = ParserUtils.optString(jState, "descripcion") != null ? ParserUtils.optString(jState, "descripcion").trim() : null;
                            }

                            //ATTACHS FILES
                            try {
                                JSONArray jFilesIdArray = jAuth.getJSONArray("archivos");
                                if (jFilesIdArray != null) {
                                    for (int i = 0; i < jFilesIdArray.length(); i++) {

                                        JSONObject jFile = jFilesIdArray.getJSONObject(i);
                                        if (jFile != null) {
                                            AttachFile file = new AttachFile();
                                            file.id = jFile.optLong("archivo_id");
                                            file.status = ConstantsUtil.SYNC_STATE;
                                            auth.files.add(file);
                                        }

                                    }
                                }
                            } catch (JSONException jEx) {
                            }

                            // COMMENTS
                            try {
                                JSONArray jCommentsArray = jAuth.getJSONArray("comentarios");
                                if (jCommentsArray != null) {
                                    for (int s = 0; s < jCommentsArray.length(); s++) {

                                        JSONObject jComment = jCommentsArray.getJSONObject(s);
                                        if (jComment != null) {
                                            String comment = jComment.optString("comentario");
                                            auth.commentList.add(comment);
                                        }
                                    }
                                }
                            } catch (JSONException jEx) {
                            }

                            authArray.add(auth);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    return null;
                }
            }
        }
        return authArray;
    }

    @Override
    protected HVolleyError parseError(Object obj) {
        return ParserUtils.parseError((JSONObject) obj);
    }
}
