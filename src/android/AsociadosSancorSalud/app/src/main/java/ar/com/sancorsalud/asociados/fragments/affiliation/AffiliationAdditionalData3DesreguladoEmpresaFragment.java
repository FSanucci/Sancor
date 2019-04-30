package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.os.Bundle;

import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoEmpresa;

/**
 * Created by francisco on 31/10/17.
 */

public class AffiliationAdditionalData3DesreguladoEmpresaFragment extends AffiliationAdditionalData3DesreguladoFragment {

    public static AffiliationAdditionalData3DesreguladoEmpresaFragment newInstance(AdditionalData3DesreguladoEmpresa param1, long dni, long cardId, long titularCardId) {
        AffiliationAdditionalData3DesreguladoEmpresaFragment fragment = new AffiliationAdditionalData3DesreguladoEmpresaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_3, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putLong(ARG_TITULAR_CARD_ID, titularCardId);
        fragment.setArguments(args);
        return fragment;
    }
}