package ar.com.sancorsalud.asociados.utils;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import ar.com.sancorsalud.asociados.R;

/**
 * Created by sergio on 8/14/16.
 */
public class SnackBarHelper {

    public static Snackbar make(View view, int backgroundColor, int title, int titleColor){
        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append(AppController.getInstance().getString(title));
        snackbarText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(AppController.getInstance(), titleColor)), 0, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(view, snackbarText, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(AppController.getInstance(), backgroundColor));
        return snackbar;
    }

    public static Snackbar makeError(View view,int title){
        return make(view, R.color.colorRed,title,R.color.colorWhite);
    }

    public static Snackbar makeSucessful(View view,int title){
        return make(view, R.color.colorGreen,title,R.color.colorWhite);
    }

}
