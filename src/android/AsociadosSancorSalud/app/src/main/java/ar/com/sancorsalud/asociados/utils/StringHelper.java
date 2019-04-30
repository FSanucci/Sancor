package ar.com.sancorsalud.asociados.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sergio on 1/9/17.
 */

public class StringHelper {

    static public String stylizeString(String str) {
        String result = "";
        try {
            if (str.length() == 0)
                return str;

            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(str.split("\\.")));

            for (String object : myList) {
                result = result + uppercaseFirstCharacter(object) + ".";
            }
        } catch (Exception e) {
            result = str;
        }
        return result;
    }

    static public String uppercaseFirstCharacter(String str) {

        str = str.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c <= 122 && c >= 97) {
                StringBuilder result = new StringBuilder(str);
                result.setCharAt(i, Character.toUpperCase(c));
                return result.toString();
            }
        }

        return str;
    }

    static public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    static public boolean isValidPhone(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.PHONE.matcher(target).matches();
        }
    }

    static public boolean isValidNumber(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return TextUtils.isDigitsOnly(target);
        }
    }

    static public boolean isValidPassword(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return target.length() > 4;
        }
    }

    public static String capitalize(String source){
        if(source!=null) {
            StringBuffer res = new StringBuffer();

            String[] strArr = source.toLowerCase().split(" ");
            for (String str : strArr) {
                char[] stringArray = str.trim().toCharArray();
                stringArray[0] = Character.toUpperCase(stringArray[0]);
                str = new String(stringArray);

                res.append(str).append(" ");
            }
            return res.toString().trim();
        }else return  source;
    }
}
