package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.os.Bundle;

import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2DesreguladoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;


public class AffiliationAdditionalData2DesreguladoEmpresaFragment extends AffiliationAdditionalData2DesreguladoFragment {

    public static AffiliationAdditionalData2DesreguladoEmpresaFragment newInstance(AdditionalData2DesreguladoEmpresa param1, long dni, long cardId, long titularCardId, long conyugeCardId, ConyugeData conyugeData) {
        AffiliationAdditionalData2DesreguladoEmpresaFragment fragment = new AffiliationAdditionalData2DesreguladoEmpresaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_2, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putLong(ARG_TITULAR_CARD_ID, titularCardId);
        args.putLong(ARG_CONYUGE_CARD_ID, conyugeCardId);
        args.putSerializable(ARG_CONYUGE_DATA, conyugeData);

        fragment.setArguments(args);
        return fragment;
    }

}

