package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.affiliation.Address;
import ar.com.sancorsalud.asociados.model.affiliation.AuthorizationPromotion;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AffiliationTitularAddressDataFragment extends AffiliationAddressDataFragment {

    private static final String TAG = "AF_LOC_FRG";

    private static final String ARG_TITULAR_ADDRESS = "titularAddress";
    private static final String ARG_CARD_ID = "cardId";

    private Address mTitularAddress;
    private long mCardId;


    public static AffiliationTitularAddressDataFragment newInstance(Address param1, long cardId) {
        AffiliationTitularAddressDataFragment fragment = new AffiliationTitularAddressDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TITULAR_ADDRESS, param1);
        args.putLong(ARG_CARD_ID, cardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitularAddress = (Address) getArguments().getSerializable(ARG_TITULAR_ADDRESS);
            mCardId = getArguments().getLong(ARG_CARD_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_address_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);
        checkEditableCardMode();

        initializeForm();
        setupListeners();
    }

    public Address getAddressData() {

        if (mTitularAddress == null) {
            mTitularAddress = new Address();
        }

        // UPDATE ORIGINAL DATA
        mTitularAddress.street = mStreetEditText.getText().toString().trim();

        if (mSelectedOrientation != -1) {
            mTitularAddress.orientation = mOrientations.get(mSelectedOrientation);
        } else {
            mTitularAddress.orientation = null;
        }

        if (!mNumberEditText.getText().toString().trim().isEmpty()) {
            mTitularAddress.number = Integer.parseInt(mNumberEditText.getText().toString().trim());
        } else {
            mTitularAddress.number = -1;
        }

        if (!mFloorEditText.getText().toString().trim().isEmpty()) {
            mTitularAddress.floor = Integer.parseInt(mFloorEditText.getText().toString().trim());
        } else {
            mTitularAddress.floor = -1;
        }

        mTitularAddress.dpto = mDptoEditText.getText().toString().trim();

        if (mSelectedAddressAttribute1 != -1) {
            mTitularAddress.adressCode1 = mAddressAttributes.get(mSelectedAddressAttribute1);
        } else {
            mTitularAddress.adressCode1 = null;
        }
        if (mSelectedAddressAttribute2 != -1) {
            mTitularAddress.adressCode2 = mAddressAttributes.get(mSelectedAddressAttribute2);
        } else {
            mTitularAddress.adressCode2 = null;
        }

        mTitularAddress.adressCode1Description = mAddressCode1DescriptionEditText.getText().toString().trim();
        mTitularAddress.adressCode2Description = mAddressCode2DescriptionEditText.getText().toString().trim();

        mTitularAddress.barrio = mBarrioEditText.getText().toString().trim();

        if (!mLocationEditText.getText().toString().isEmpty()) {
            updateZipCode(new Response.Listener<QuoteOption>() {
                @Override
                public void onResponse(QuoteOption response) {
                    mTitularAddress.zipCode = response.id;
                }
            });
        }

        return mTitularAddress;
    }

    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    private void initializeForm() {
        if (mTitularAddress == null)
            return;

        if (mTitularAddress.street != null && !mTitularAddress.street.isEmpty())
            mStreetEditText.setText(mTitularAddress.street);

        if (mTitularAddress.orientation != null) {
            mSelectedOrientation = mOrientations.indexOf(mTitularAddress.orientation);
            if (mSelectedOrientation != -1)
                mOrientationEditText.setText(mTitularAddress.orientation.title);
        }

        if (mTitularAddress.number != -1) {
            mNumberEditText.setText(Integer.valueOf(mTitularAddress.number).toString());
        } else {
            mNumberEditText.setText("");
        }

        if (mTitularAddress.floor != -1) {
            mFloorEditText.setText((Integer.valueOf(mTitularAddress.floor).toString()));
        } else {
            mFloorEditText.setText("");
        }

        if (mTitularAddress.dpto != null && !mTitularAddress.dpto.isEmpty())
            mDptoEditText.setText(mTitularAddress.dpto);

        if (mTitularAddress.adressCode1 != null) {
            mSelectedAddressAttribute1 = mAddressAttributes.indexOf(mTitularAddress.adressCode1);
            if (mSelectedAddressAttribute1 != -1)
                mAddressCode1EditText.setText(mTitularAddress.adressCode1.title);
        }

        if (mTitularAddress.adressCode2 != null) {
            mSelectedAddressAttribute2 = mAddressAttributes.indexOf(mTitularAddress.adressCode2);
            if (mSelectedAddressAttribute2 != -1)
                mAddressCode2EditText.setText(mTitularAddress.adressCode2.title);
        }

        if (mTitularAddress.adressCode1Description != null && !mTitularAddress.adressCode1Description.isEmpty())
            mAddressCode1DescriptionEditText.setText(mTitularAddress.adressCode1Description);

        if (mTitularAddress.adressCode2Description != null && !mTitularAddress.adressCode2Description.isEmpty())
            mAddressCode2DescriptionEditText.setText(mTitularAddress.adressCode2Description);

        if (mTitularAddress.barrio != null && !mTitularAddress.barrio.isEmpty())
            mBarrioEditText.setText(mTitularAddress.barrio);

        if (mTitularAddress.zipCode != null && !mTitularAddress.zipCode.isEmpty()) {
            Log.e(TAG, "Client zip: " + mTitularAddress.zipCode);

            String location = findLocationByZipCode(mTitularAddress.zipCode);
            if (location != null) {
                mLocationEditText.setText(location);
            }
        }
    }


    private boolean validateForm() {
        return true;
    }
}
