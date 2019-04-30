package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.affiliation.Address;
import ar.com.sancorsalud.asociados.model.affiliation.ContactData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.Storage;


public class AffiliationTitularContactDataFragment extends AffiliationAddressDataFragment {

    private static final String TAG = "AF_CONT_FRG";

    private static final String ARG_CONTACT_DATA = "contactData";

    // Contact DATA
    private TextView suggestedDataTxt;
    private EditText mSuggestedPhoneEditText;
    private EditText mSuggestedDeviceEditText;

    private EditText mAreaPhoneEditText;
    private EditText mPhoneEditText;
    private EditText mAreaDeviceEditText;
    private EditText mDeviceEditText;
    private EditText mEmailEditText;

    private RadioButton mYesInvoiceRadioButton;
    private RadioButton mNoInvoiceRadioButton;

    private RadioButton mYesDataHomeRadioButton;
    private RadioButton mNoDataHomeRadioButton;

    // Alternative Address Data
    private LinearLayout mAlternativeAddressBox;
    private Address mAlternativeAddress;
    private ContactData contactData;

    public static AffiliationTitularContactDataFragment newInstance(ContactData param1) {
        AffiliationTitularContactDataFragment fragment = new AffiliationTitularContactDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT_DATA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contactData = (ContactData) getArguments().getSerializable(ARG_CONTACT_DATA);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_contact_data, container, false);
        return mMainContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        suggestedDataTxt = (TextView) view.findViewById(R.id.suggested_data_title);
        setTypeTextNoSuggestions(suggestedDataTxt);

        mSuggestedPhoneEditText = (EditText) view.findViewById(R.id.suggested_phone_input);
        setTypeTextNoSuggestions(mSuggestedPhoneEditText);

        mSuggestedDeviceEditText = (EditText) view.findViewById(R.id.suggested_device_input);
        setTypeTextNoSuggestions(mSuggestedDeviceEditText);

        mAreaPhoneEditText = (EditText) view.findViewById(R.id.area_phone_input);
        mPhoneEditText = (EditText) view.findViewById(R.id.phone_input);
        mAreaDeviceEditText = (EditText) view.findViewById(R.id.area_device_input);
        mDeviceEditText = (EditText) view.findViewById(R.id.device_input);
        mEmailEditText = (EditText) view.findViewById(R.id.email_input);

        mYesInvoiceRadioButton = (RadioButton) view.findViewById(R.id.yes_invoice);
        mNoInvoiceRadioButton = (RadioButton) view.findViewById(R.id.no_invoice);

        mYesDataHomeRadioButton = (RadioButton) view.findViewById(R.id.yes_data_home);
        mNoDataHomeRadioButton = (RadioButton) view.findViewById(R.id.no_data_home);

        // Alternative Adress
        mAlternativeAddressBox = (LinearLayout) view.findViewById(R.id.alternative_address_box);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);
        checkEditableCardMode();

        initializeForm();
        setupListeners();
    }

    public ContactData getContactData() {

        if (contactData == null) {
            contactData = new ContactData();
        }

        // UPDATE ORIGINAL DATA
        contactData.areaPhone = mAreaPhoneEditText.getText().toString().trim();
        contactData.phone = mPhoneEditText.getText().toString().trim();

        contactData.areaDevice = mAreaDeviceEditText.getText().toString().trim();
        contactData.device = mDeviceEditText.getText().toString().trim();

        contactData.email = mEmailEditText.getText().toString().trim();

        if (!mYesInvoiceRadioButton.isChecked() && !mNoInvoiceRadioButton.isChecked()) {
            contactData.addInvoice = null;
        } else {
            contactData.addInvoice = mYesInvoiceRadioButton.isChecked();
        }

        // ALTERNATIVE ADDRESS
        if (mNoDataHomeRadioButton.isChecked()){
            contactData.alternativeAddress = new Address();
            buildAlternativeAddress();
        }else{
            contactData.alternativeAddress = null;
        }

        return contactData;
    }

    public boolean isValidSection() {
        return validateForm();
    }

    // --- helper methods ---------------------------------------------------- //

    protected  void checkEditableCardMode(){
        super.checkEditableCardMode();

        mAreaPhoneEditText.setFocusable(editableCard);
        mPhoneEditText.setFocusable(editableCard);
        mAreaDeviceEditText .setFocusable(editableCard);
        mDeviceEditText.setFocusable(editableCard);
        mEmailEditText .setFocusable(editableCard);

        mYesDataHomeRadioButton.setEnabled(editableCard);
        mNoDataHomeRadioButton.setEnabled(editableCard);

        mYesInvoiceRadioButton.setEnabled(editableCard);
        mNoInvoiceRadioButton.setEnabled(editableCard);
    }



    private void initializeForm() {
        if (contactData == null)
            return;

        // Check Suggested Data
        if (contactData.suggestedPhone == null && contactData.suggestedDevice == null) {
            suggestedDataTxt.setVisibility(View.GONE);
            mSuggestedPhoneEditText.setVisibility(View.GONE);
            mSuggestedDeviceEditText.setVisibility(View.GONE);
        } else {
            suggestedDataTxt.setVisibility(View.VISIBLE);
            mSuggestedPhoneEditText.setVisibility(View.VISIBLE);
            mSuggestedDeviceEditText.setVisibility(View.VISIBLE);
        }

        if (contactData.suggestedPhone != null && !contactData.suggestedPhone.isEmpty())
            mSuggestedPhoneEditText.setText(contactData.suggestedPhone);

        if (contactData.suggestedDevice != null && !contactData.suggestedDevice.isEmpty())
            mSuggestedDeviceEditText.setText(contactData.suggestedDevice);

        // Real Data
        if (contactData.areaPhone != null && !contactData.areaPhone.isEmpty())
            mAreaPhoneEditText.setText(contactData.areaPhone);
        if (contactData.phone != null && !contactData.phone.isEmpty())
            mPhoneEditText.setText(contactData.phone);

        if (contactData.areaDevice != null && !contactData.areaDevice.isEmpty())
            mAreaDeviceEditText.setText(contactData.areaDevice);
        if (contactData.device != null && !contactData.device.isEmpty())
            mDeviceEditText.setText(contactData.device);

        if (contactData.email != null && !contactData.email.isEmpty())
            mEmailEditText.setText(contactData.email);

        if (contactData.addInvoice != null) {
            if (contactData.addInvoice) {
                mYesInvoiceRadioButton.setChecked(true);
                mNoInvoiceRadioButton.setChecked(false);
            } else {
                mNoInvoiceRadioButton.setChecked(true);
                mYesInvoiceRadioButton.setChecked(false);
            }
        } else {
            mNoInvoiceRadioButton.setChecked(true);
            mYesInvoiceRadioButton.setChecked(false);
        }

        // Alternative address
        if (contactData.alternativeAddress != null) {
            mYesDataHomeRadioButton.setChecked(false);
            mNoDataHomeRadioButton.setChecked(true);
            mAlternativeAddressBox.setVisibility(View.VISIBLE);

            fillAlternativeAddress();
        } else {
            mYesDataHomeRadioButton.setChecked(true);
            mNoDataHomeRadioButton.setChecked(false);
            mAlternativeAddressBox.setVisibility(View.GONE);
        }
    }

    private void fillAlternativeAddress(){

        if (contactData.alternativeAddress.street != null  && !contactData.alternativeAddress.street.isEmpty())
            mStreetEditText.setText(contactData.alternativeAddress.street);

        if (contactData.alternativeAddress.orientation != null) {
            mSelectedOrientation = mOrientations.indexOf(contactData.alternativeAddress.orientation);
            if (mSelectedOrientation != -1)
                mOrientationEditText.setText(contactData.alternativeAddress.orientation.title);
        }

        if (contactData.alternativeAddress.number != -1)
            mNumberEditText.setText(Integer.valueOf(contactData.alternativeAddress.number).toString());

        if (contactData.alternativeAddress.floor != -1)
            mFloorEditText.setText((Integer.valueOf(contactData.alternativeAddress.floor).toString()));

        if (contactData.alternativeAddress.dpto != null  && !contactData.alternativeAddress.dpto.isEmpty())
            mDptoEditText.setText(contactData.alternativeAddress.dpto);

        if (contactData.alternativeAddress.adressCode1 != null) {
            mSelectedAddressAttribute1 = mAddressAttributes.indexOf(contactData.alternativeAddress.adressCode1);
            if (mSelectedAddressAttribute1 != -1)
                mAddressCode1EditText.setText(contactData.alternativeAddress.adressCode1.title);
        }

        if (contactData.alternativeAddress.adressCode2 != null) {
            mSelectedAddressAttribute2 = mAddressAttributes.indexOf(contactData.alternativeAddress.adressCode2);
            if (mSelectedAddressAttribute2 != -1)
                mAddressCode2EditText.setText(contactData.alternativeAddress.adressCode2.title);
        }

        if (contactData.alternativeAddress.adressCode1Description != null  && !contactData.alternativeAddress.adressCode1Description.isEmpty())
            mAddressCode1DescriptionEditText.setText(contactData.alternativeAddress.adressCode1Description);

        if (contactData.alternativeAddress.adressCode2Description != null  && !contactData.alternativeAddress.adressCode2Description.isEmpty())
            mAddressCode2DescriptionEditText.setText(contactData.alternativeAddress.adressCode2Description);

        if (contactData.alternativeAddress.barrio != null  && !contactData.alternativeAddress.barrio.isEmpty())
            mBarrioEditText.setText(contactData.alternativeAddress.barrio);

        if (contactData.alternativeAddress.zipCode  != null &&  !contactData.alternativeAddress.zipCode.isEmpty()){
            Log.e (TAG, "Client zip: " + contactData.alternativeAddress.zipCode);

            String location = findLocationByZipCode(contactData.alternativeAddress.zipCode);
            if (location != null){
                mLocationEditText.setText(location);
            }
        }
    }

    private void buildAlternativeAddress(){

        contactData.alternativeAddress.street = mStreetEditText.getText().toString().trim();

        if (mSelectedOrientation != -1) {
            contactData.alternativeAddress.orientation = mOrientations.get(mSelectedOrientation);
        } else {
            contactData.alternativeAddress.orientation = null;
        }

        if (!mNumberEditText.getText().toString().trim().isEmpty()){
            contactData.alternativeAddress.number = Integer.parseInt(mNumberEditText.getText().toString().trim());
        }else{
            contactData.alternativeAddress.number = -1;
        }

        if (!mFloorEditText.getText().toString().trim().isEmpty()){
            contactData.alternativeAddress.floor = Integer.parseInt(mFloorEditText.getText().toString().trim());
        }else{
            contactData.alternativeAddress.floor = -1;
        }

        contactData.alternativeAddress.dpto = mDptoEditText.getText().toString().trim();

        if (mSelectedAddressAttribute1 != -1) {
            contactData.alternativeAddress.adressCode1 = mAddressAttributes.get(mSelectedAddressAttribute1);
        } else {
            contactData.alternativeAddress.adressCode1 = null;
        }
        if (mSelectedAddressAttribute2 != -1) {
            contactData.alternativeAddress.adressCode2 = mAddressAttributes.get(mSelectedAddressAttribute2);
        } else {
            contactData.alternativeAddress.adressCode2 = null;
        }

        contactData.alternativeAddress.adressCode1Description = mAddressCode1DescriptionEditText.getText().toString().trim();
        contactData.alternativeAddress.adressCode2Description = mAddressCode2DescriptionEditText.getText().toString().trim();

        contactData.alternativeAddress.barrio = mBarrioEditText.getText().toString().trim();

        if(!mLocationEditText.getText().toString().isEmpty()) {
            updateZipCode(new Response.Listener<QuoteOption>() {
                @Override
                public void onResponse(QuoteOption response) {
                    contactData.alternativeAddress.zipCode  = response.id;
                }
            });
        }
    }


    protected void setupListeners() {
        super.setupListeners();

        if (editableCard) {
            mYesDataHomeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlternativeAddressBox.setVisibility(View.GONE);
                }
            });

            mNoDataHomeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlternativeAddressBox.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private boolean validateForm() {

        boolean isValid = true;

        if (mYesInvoiceRadioButton.isChecked() && editableCard) {
            isValid = isValid & validateField(mEmailEditText, R.string.add_email_error, R.id.email_wrapper);
        }
        return isValid;
    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }
}
