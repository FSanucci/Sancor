package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.content.Intent;

import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;

/**
 * Created by francisco on 28/7/17.
 */

public interface IAffiliationAdditionalData2Fragment {
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public boolean isValidSection();
    public IAdditionalData2 getAdditionalData2();
}
