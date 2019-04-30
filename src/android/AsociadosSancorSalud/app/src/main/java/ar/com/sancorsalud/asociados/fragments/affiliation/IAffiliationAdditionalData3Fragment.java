package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.content.Intent;

import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData3;


public interface IAffiliationAdditionalData3Fragment {
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public boolean isValidSection();
    public IAdditionalData3 getAdditionalData3();
}
