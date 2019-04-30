package ar.com.sancorsalud.asociados.rest.services.get;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.HVolleyError;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestConstants;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

import static ar.com.sancorsalud.asociados.model.user.UserRole.SALESMAN;
import static ar.com.sancorsalud.asociados.model.user.UserRole.ZONE_LEADER;
import static ar.com.sancorsalud.asociados.model.user.UserRole.UNKNOW;

/**
 * Created by sergio on 12/27/16.
 */

public class HLoginRequest extends HRequest<User> {

    private static final String PATH = RestConstants.HOST + RestConstants.LOGIN_SERVICE;
    private String mEmail;
    private String mPassword;

    public HLoginRequest(String email, String password, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, PATH, listener, errorListener);
        mEmail = email;
        mPassword = password;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("username", mEmail);
        headers.put("password", mPassword);

        return headers;
    }

    @Override
    protected User parseObject(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject dic = ParserUtils.parseResponse((JSONObject) obj);

            if (dic != null) {
                try {
                    JSONObject userDic = dic.optJSONObject("user");
                    User user = new User();
                    user.id = userDic.getLong("_id");
                    user.email = ParserUtils.optString(userDic, "email");

                    user.password = mPassword;
                    user.token = ParserUtils.optString(dic, "token");
                    user.username = ParserUtils.optString(userDic, "username");

                    user.company = ParserUtils.optInt(userDic, "empresa");

                    user.zones = new ArrayList<>();
                    try {
                        JSONArray jsonList = userDic.getJSONArray("zone_id");

                        for (int i = 0; i < jsonList.length(); i++) {
                            Integer id = jsonList.optInt(i, -1);
                            if (id != -1)
                                user.zones.add(id);
                        }
                    } catch (JSONException jEx) {
                    }

                    user.roleName = ParserUtils.optString(userDic, "role_name");
                    user.firstname = ParserUtils.optString(userDic, "name");
                    user.lastname = ParserUtils.optString(userDic, "lastname");

                    int roleId = userDic.optInt("role_id");

                    switch (roleId) {
                        case ConstantsUtil.SALESMAN_ID:
                            user.role = SALESMAN;
                            break;
                        case ConstantsUtil.ZONE_LEADER_ID:
                            user.role = ZONE_LEADER;
                            break;

                        default:
                            user.role = UNKNOW;
                            break;
                    }

                    Log.e (TAG, "JSON" + dic.toString());
                    Log.e(TAG, "User ROLE id: " + roleId);

                    return user;
                } catch (JSONException e) {
                    return null;
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
