package ar.com.sancorsalud.asociados.model.affiliation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 15/11/17.
 */

public class VerifyAuthorizationCardResponse {

    public String status;
    public boolean control;
    public List<String> messages = new ArrayList<String>();

}
