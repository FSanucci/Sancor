package ar.com.sancorsalud.asociados.model.user;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 10/31/16.
 */

public class User extends Object implements Serializable {

    public long id;
    public String token;
    public String email;
    public String password;
    public String username;
    public ArrayList<Integer> zones;
    public String roleName;
    public UserRole role;
    public String firstname;
    public String lastname;
    public Integer company;

}
