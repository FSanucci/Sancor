package ar.com.sancorsalud.asociados.utils;

import java.util.regex.Pattern;

/**
 * Created by sergiocirasa on 5/9/17.
 */

public class ParseNumber {

    public static double parseDouble(String nro){

        String replaced = nro.replaceAll(Pattern.quote(","),".");
        try{
            return Double.parseDouble(replaced);
        }catch(NumberFormatException nfe){

        }
        return 0;
    }
}
