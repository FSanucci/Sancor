package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;


public class AffiliationAddressDataFragment extends BaseFragment {

    private static final String TAG = "AF_BASE_ADDRR_FRG";

    protected ScrollView mScrollView;

    protected EditText mStreetEditText;
    protected EditText mOrientationEditText;
    protected EditText mNumberEditText;
    protected EditText mFloorEditText;
    protected EditText mDptoEditText;

    protected EditText mAddressCode1EditText;
    protected EditText mAddressCode2EditText;
    protected EditText mAddressCode1DescriptionEditText;
    protected EditText mAddressCode2DescriptionEditText;

    protected EditText mBarrioEditText;
    protected AutoCompleteTextView mLocationEditText;

    protected ArrayList<QuoteOption> mAddressAttributes;
    protected SpinnerDropDownAdapter mmAddressAttributeAlertAdapter;
    protected int mSelectedAddressAttribute1= -1;
    protected int mSelectedAddressAttribute2= -1;

    protected ArrayList<QuoteOption> mOrientations;
    protected SpinnerDropDownAdapter mOrientationAlertAdapter;
    protected int mSelectedOrientation = -1;

    protected boolean searching = false;
    protected QuoteOption mLocationDaTaSelected;
    protected ArrayList<QuoteOption> locationDataArray = new ArrayList<QuoteOption>();

    protected boolean editableCard = true;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mStreetEditText = (EditText) view.findViewById(R.id.street_input);
        setTypeTextNoSuggestions(mStreetEditText);

        mOrientationEditText = (EditText) view.findViewById(R.id.orientation_input);
        setTypeTextNoSuggestions(mOrientationEditText);

        mNumberEditText = (EditText) view.findViewById(R.id.number_input);
        mFloorEditText = (EditText) view.findViewById(R.id.floor_input);

        mDptoEditText = (EditText) view.findViewById(R.id.depto_input);
        setTypeTextNoSuggestions(mDptoEditText);

        mAddressCode1EditText = (EditText) view.findViewById(R.id.address_code1_input);
        setTypeTextNoSuggestions(mAddressCode1EditText);

        mAddressCode2EditText = (EditText) view.findViewById(R.id.address_code2_input);
        setTypeTextNoSuggestions(mAddressCode2EditText);

        mAddressCode1DescriptionEditText = (EditText) view.findViewById(R.id.address_code1_desc_input);
        setTypeTextNoSuggestions(mAddressCode1DescriptionEditText);

        mAddressCode2DescriptionEditText = (EditText) view.findViewById(R.id.address_code2_desc_input);
        setTypeTextNoSuggestions(mAddressCode2DescriptionEditText);

        mBarrioEditText = (EditText) view.findViewById(R.id.barrio_input);
        setTypeTextNoSuggestions(mBarrioEditText);

        mLocationEditText = (AutoCompleteTextView) view.findViewById(R.id.location_input);
        setTypeTextNoSuggestions(mLocationEditText);

        mLocationEditText.setThreshold(1);

        mScrollView = (ScrollView) view.findViewById(R.id.scroll);
        mScrollView.smoothScrollTo(0, 0);

        fillArraysData();
    }

    // --- helper methods ---------------------------------------------------- //


    private void fillArraysData(){

        mAddressAttributes = new ArrayList<>();
        QuoteOption attributesSelection = new QuoteOption("-1", getResources().getString(R.string.field_address_atribute));
        mAddressAttributes.add(attributesSelection);
        mAddressAttributes.addAll(QuoteOptionsController.getInstance().getAddressAttributes());

        mOrientations = new ArrayList<>();
        QuoteOption orientationSelection = new QuoteOption("-1", getResources().getString(R.string.field_address_orientation));
        mOrientations.add(orientationSelection);
        mOrientations.addAll(QuoteOptionsController.getInstance().getOrientations());
    }


    protected void checkEditableCardMode() {

        mStreetEditText.setFocusable(editableCard);
        mNumberEditText.setFocusable(editableCard);
        mFloorEditText.setFocusable(editableCard);
        mDptoEditText.setFocusable(editableCard);
        mBarrioEditText.setFocusable(editableCard);
        mLocationEditText.setFocusable(editableCard);

        mAddressCode1DescriptionEditText.setFocusable(editableCard);
        mAddressCode2DescriptionEditText.setFocusable(editableCard);
    }

    protected void setupListeners() {
        if (editableCard) {

            View orientationButton = mMainContainer.findViewById(R.id.orientation_button);
            orientationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showOrientationAlert();
                }
            });
            orientationButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedOrientation = -1;
                    mOrientationEditText.setText("");
                    return true;
                }
            });

            mLocationEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.e(TAG, "onTextChanged-----: " + s.toString() + "-----");
                    mLocationDaTaSelected = null;

                    searchLocation(s.toString(), new Response.Listener<ArrayList<QuoteOption>>() {
                        @Override
                        public void onResponse(ArrayList<QuoteOption> response) {

                            if (response != null && response.size() > 0) {
                                locationDataArray = response;

                                ArrayList<String> options = new ArrayList<String>();
                                for (QuoteOption q : response) {
                                    options.add(q.title);
                                }
                                searching = false;

                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
                                AffiliationAddressDataFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        mLocationEditText.setAdapter(adapter);
                                        mLocationEditText.showDropDown();
                                    }
                                });
                            } else {
                                searching = false;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            searching = false;
                        }
                    });
                }
            });
            mLocationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        updateZipCode(null);
                    }
                }
            });

            View addresCode1Button = mMainContainer.findViewById(R.id.address_code1_button);
            addresCode1Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showAddressAttributeAlert1();
                }
            });
            addresCode1Button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedAddressAttribute1 = -1;
                    mAddressCode1EditText.setText("");
                    return true;
                }
            });

            View addresCode2Button = mMainContainer.findViewById(R.id.address_code2_button);
            addresCode2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showAddressAttributeAlert2();
                }
            });
            addresCode2Button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedAddressAttribute2 = -1;
                    mAddressCode2EditText.setText("");
                    return true;
                }
            });
        }
    }


    protected void showOrientationAlert() {
        ArrayList<String> orientationStr = new ArrayList<String>();
        for (QuoteOption q : mOrientations) {
            orientationStr.add(q.optionName());
        }

        mOrientationAlertAdapter = new SpinnerDropDownAdapter(getActivity(), orientationStr, mSelectedOrientation);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mOrientationAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedOrientation = i;

                        if (i == 0){
                            mSelectedOrientation = -1;
                            mOrientationEditText.setText("");
                        }else {
                            mOrientationEditText.setText(mOrientations.get(i).optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mOrientationAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    protected void showAddressAttributeAlert1() {
        ArrayList<String> addressAttStr = new ArrayList<String>();
        for (QuoteOption q : mAddressAttributes) {
            addressAttStr.add(q.optionName());
        }

        mmAddressAttributeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), addressAttStr, mSelectedAddressAttribute1);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mmAddressAttributeAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedAddressAttribute1 = i;
                        if (i == 0){
                            mSelectedAddressAttribute1 = -1;
                            mAddressCode1EditText.setText("");
                        }else {
                            mAddressCode1EditText.setText(mAddressAttributes.get(i).optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mmAddressAttributeAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    protected void showAddressAttributeAlert2() {
        ArrayList<String> addressAttStr = new ArrayList<String>();
        for (QuoteOption q : mAddressAttributes) {
            addressAttStr.add(q.optionName());
        }

        mmAddressAttributeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), addressAttStr, mSelectedAddressAttribute2);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mmAddressAttributeAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedAddressAttribute2 = i;

                        if (i == 0){
                            mSelectedAddressAttribute2 = -1;
                            mAddressCode2EditText.setText("");
                        }else {
                            mAddressCode2EditText.setText(mAddressAttributes.get(i).optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mmAddressAttributeAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    protected String findLocationByZipCode(final String zipCode){

        String location = null;

        String realZipCode = zipCode.substring(0, (zipCode.length()) -3);
        Log.e (TAG, "REAL ZIP CODE :" + realZipCode + "--------------");

        searchLocation(realZipCode, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                if (response != null && response.size() > 0) {
                    locationDataArray = response;

                    for (QuoteOption option: locationDataArray){
                        Log.e (TAG, "id:" + option.id +  "title " + option.title);

                        if (option.id.equals(zipCode)) {
                            mLocationEditText.setText(option.title);
                            break;
                        }
                    }
                    searching = false;
                }else{
                    searching = false;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                searching = false;
            }
        });

        return location;
    }

    protected void searchLocation(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        if (searching)
            return;

        searching = true;
        HRequest request = RestApiServices.createSearchLocationRequest(query,  listener, errorListener);
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }


    protected void updateZipCode(final Response.Listener<QuoteOption> listener) {

        final String detail = mLocationEditText.getText().toString().trim();
        if (detail != null && !detail.isEmpty()) {
            QuoteOption option = findZipCode(detail);

            if (option != null) {
                mLocationDaTaSelected = option;
            } else {
                mLocationDaTaSelected = new QuoteOption();
                mLocationDaTaSelected.title = detail;
                mLocationDaTaSelected.id = null;
            }
            if (listener != null)
                listener.onResponse(mLocationDaTaSelected);
        }
    }

    private QuoteOption findZipCode(String detail ){
        QuoteOption result = null;
        for (QuoteOption option: locationDataArray) {
            if (option.title.equals(detail)){
                result = option;
                break;
            }
        }
        return result;
    }
}
