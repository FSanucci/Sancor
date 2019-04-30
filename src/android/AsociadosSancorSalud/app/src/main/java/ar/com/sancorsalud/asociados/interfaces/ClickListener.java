package ar.com.sancorsalud.asociados.interfaces;

import android.view.View;

/**
 * Created by sergio on 11/2/16.
 */

public interface ClickListener {
    public void onClick(View view, int position);
    public void onLongClick(View view, int position, float xx, float yy);
}
