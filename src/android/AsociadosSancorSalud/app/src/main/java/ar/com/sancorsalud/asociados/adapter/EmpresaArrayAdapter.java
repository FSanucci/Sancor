package ar.com.sancorsalud.asociados.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;

/**
 * Created by francisco on 1/9/17.
 */

public class EmpresaArrayAdapter extends ArrayAdapter<String> {

    public ArrayList<QuoteOption> quoteOptions;

    public EmpresaArrayAdapter(Context context, int resource, ArrayList<String> options, ArrayList<QuoteOption> quoteOptions){
        super(context, resource, options);
        this.quoteOptions = quoteOptions;
    }




}
