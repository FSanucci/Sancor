package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2Monotributo;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData3DesreguladoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData3;
import ar.com.sancorsalud.asociados.utils.ImageProvider;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;

/**
 * Created by francisco on 31/10/17.
 */

public class AffiliationAdditionalData3DesreguladoIndividualFragment extends AffiliationAdditionalData3DesreguladoFragment {

    public static AffiliationAdditionalData3DesreguladoIndividualFragment newInstance(AdditionalData3DesreguladoIndividual param1, long dni, long cardId, long titularCardId) {
        AffiliationAdditionalData3DesreguladoIndividualFragment fragment = new AffiliationAdditionalData3DesreguladoIndividualFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_3, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putLong(ARG_TITULAR_CARD_ID, titularCardId);
        fragment.setArguments(args);
        return fragment;
    }
}