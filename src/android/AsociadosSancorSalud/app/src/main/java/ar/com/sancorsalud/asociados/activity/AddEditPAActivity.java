package ar.com.sancorsalud.asociados.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.manager.ZoneController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.model.user.UserRole;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;
import ar.com.sancorsalud.asociados.utils.StringHelper;

public class AddEditPAActivity extends BaseActivity {

    private static final String TAG = "ADD_EDIT_PA_ACT";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String INIT_BIRTH_DAY = "01-01-1999";
    private static final String OK_PA = "El potencial se puede agregar";

    private static final String DNI_QRCODE_FILE_PREFIX = "dniQRCode";

    public static final int QRCODE_REQUEST = 100;

    private Button mQrCodeButton;
    private ImageView photoImg;

    private Button mAddProspectiveClientButton;
    private SimpleDateFormat mDateFormat;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mDniEditText;
    private EditText mBirthdayEditText;
    private AutoCompleteTextView mLocationEditText;

    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mCellPhoneEditText;
    private EditText mAddressEditText;
    private EditText mAddressNumberEditText;
    private EditText mAddressFloorEditText;
    private EditText mAddressDptoEditText;

    private CheckBox mCompanyCheckbox;
    private View mContainerView;

    private View mZoneContainer;
    private int mSelectedZone;
    private EditText mZoneCodeEditText;

    private ArrayList<Zone> mAvailableZones;
    private SpinnerDropDownAdapter mZonesAlertAdapter;
    private ProspectiveClient mClient;
    private Boolean editMode = false;
    private Boolean mReadOnlyMode = false;

    private QuoteOption mLocationDaTaSelected;
    private ArrayList<QuoteOption> locationDataArray = new ArrayList<QuoteOption>();

    private boolean loadFromQr = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.menu_item_add_prospective_client);

        mMainContainer = findViewById(R.id.all_data);

        attachFilesSubDir = "pa/file";
        mImageProvider = new ImageProvider(this);

        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
            mReadOnlyMode = getIntent().getBooleanExtra(ConstantsUtil.CLIENT_READ_ONLY, false);

            if (mClient != null) {
                setupToolbar(toolbar, mClient.getFullName());
                // PA is to be updated not created
                editMode = true;
            }
        }


        Log.e (TAG, "editMode: " + editMode);


        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mQrCodeButton = (Button) findViewById(R.id.qrcode_button);
        photoImg = (ImageView) findViewById(R.id.photo_button);

        mContainerView = findViewById(R.id.main_container);
        mFirstNameEditText = (EditText) findViewById(R.id.firstname_input);
        setTypeTextNoSuggestions(mFirstNameEditText);

        mLastNameEditText = (EditText) findViewById(R.id.lastname_input);
        setTypeTextNoSuggestions(mLastNameEditText);

        mDniEditText = (EditText) findViewById(R.id.dni_input);
        mBirthdayEditText = (EditText) findViewById(R.id.birthday_input);

        mLocationEditText = (AutoCompleteTextView) findViewById(R.id.location_input);
        setTypeTextNoSuggestions(mLocationEditText);

        mEmailEditText = (EditText) findViewById(R.id.email_input);

        mPhoneEditText = (EditText) findViewById(R.id.phone_input);
        mCellPhoneEditText = (EditText) findViewById(R.id.cellphone_input);

        mAddressEditText = (EditText) findViewById(R.id.address_input);
        setTypeTextNoSuggestions(mAddressEditText);

        mAddressNumberEditText = (EditText) findViewById(R.id.address_number_input);
        mAddressFloorEditText = (EditText) findViewById(R.id.address_floor_input);
        mAddressDptoEditText = (EditText) findViewById(R.id.address_dpto_input);

        mCompanyCheckbox = (CheckBox) findViewById(R.id.company_input);
        mAddProspectiveClientButton = (Button) findViewById(R.id.add_button);

        mZoneContainer = findViewById(R.id.zone_container);
        mZoneCodeEditText = (EditText) findViewById(R.id.zone_input);
        mSelectedZone = -1;

        final User user = UserController.getInstance().getSignedUser();
        if (user.role == UserRole.SALESMAN || (user.zones != null && user.zones.size() == 1)) {
            mZoneContainer.setVisibility(View.GONE);
            mAddProspectiveClientButton.setText(R.string.save_button_title);
        }

        ZoneController.getInstance().getItems(new Response.Listener<ArrayList<Zone>>() {
            @Override
            public void onResponse(ArrayList<Zone> response) {
                mAvailableZones = response;
                /*
                if (user.zones!=null && user.zones.size()==1) {
                    Integer zoneId = user.zones.get(0);
                    int i = 0;
                    for(Zone zone : mAvailableZones){
                        if(zone.id==zoneId){
                            mSelectedZone = i;
                            return;
                        }
                        i++;
                    }
                }
                */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAvailableZones = null;
            }
        });

        setupImageProvider();
        setupListeners();

        if (editMode) {
            initializeForm();
            mZoneContainer.setVisibility(View.GONE);
        } else {
            mBirthdayEditText.setText(INIT_BIRTH_DAY);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == QRCODE_REQUEST) {
            String message = data.getStringExtra(ConstantsUtil.QRCODE_DATA);
            Log.e(TAG, "Result:" + message);

            if (message != null && !message.isEmpty()) {
                qrCodeCaptured(message);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PermissionsHelper.REQUEST_CAMERA_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "OK perm for CAMERA .......");
                    IntentHelper.goToQrCodeScan(this, QRCODE_REQUEST);
                } else {
                    Log.e(TAG, "No permission GRANTED for CAMERA .......");
                    showMessage(getResources().getString(R.string.error_cam_permission),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void updateFileList(final AttachFile attachFile) {
        Log.e(TAG, "Attach File: " + attachFile.filePath);
        if (attachFileType.equals(DNI_QRCODE_FILE_PREFIX)) {
            showProgressDialog(R.string.decode_image_file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    decodeQRCode(attachFile);
                }
            }).start();
        }
    }

    @Override
    public void onDecodedFile(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                if (text != null) {
                    qrCodeCaptured(text);
                } else {
                    SnackBarHelper.makeError(mContainerView, R.string.error_decoding_file).show();
                }
            }
        });
    }

    private void initializeForm() {
        if (mClient.firstname != null)
            mFirstNameEditText.setText(mClient.firstname);

        if (mClient.lastname != null)
            mLastNameEditText.setText(mClient.lastname);

        if (mClient.dni > 0) {
            mDniEditText.setText("" + mClient.dni);
            mDniEditText.setEnabled(false);
        } else {
            mDniEditText.setEnabled(true);
        }

        if (mClient.birthday != null) {
            mBirthdayEditText.setText(mClient.getBirthday());
        }


        if (mClient.location != null) {
            mLocationEditText.setText(mClient.location);
        } else if (mClient.zip != null && !mClient.zip.isEmpty()) {
            Log.e(TAG, "Client zip: " + mClient.zip);

            findLocationByZipCode(mClient.zip);
            /*
            String location = findLocationByZipCode(mClient.zip);
            if (location != null) {
                mLocationEditText.setText(location);
            }
            */
        }

        if (mClient.email != null)
            mEmailEditText.setText(mClient.email);

        if (mClient.phoneNumber != null)
            mPhoneEditText.setText(mClient.phoneNumber);

        if (mClient.celularNumber != null)
            mCellPhoneEditText.setText(mClient.celularNumber);

        if (mClient.street != null)
            mAddressEditText.setText(mClient.street);

        if (mClient.streetNumber != -1)
            mAddressNumberEditText.setText("" + mClient.streetNumber);

        if (mClient.floorNumber != -1)
            mAddressFloorEditText.setText("" + mClient.floorNumber);

        if (mClient.department != null)
            mAddressDptoEditText.setText(mClient.department);

        if (mClient.zone != null && mClient.zone.name != null)
            mZoneCodeEditText.setText(mClient.zone.name);

        checkReadOnlyMode();

    }

    private void checkReadOnlyMode() {
        // When referent zone has already assigned a PA, can not change data, only read it
        if (mReadOnlyMode) {

            mFirstNameEditText.setFocusable(false);
            mLastNameEditText.setFocusable(false);
            mDniEditText.setFocusable(false);
            mBirthdayEditText.setFocusable(false);
            mLocationEditText.setFocusable(false);
            mEmailEditText.setFocusable(false);
            mPhoneEditText.setFocusable(false);
            mCellPhoneEditText.setFocusable(false);
            mAddressEditText.setFocusable(false);
            mAddressEditText.setFocusable(false);
            mAddressNumberEditText.setFocusable(false);
            mAddressFloorEditText.setFocusable(false);
            mAddressDptoEditText.setFocusable(false);
            mZoneCodeEditText.setFocusable(false);

            mCompanyCheckbox.setEnabled(false);
            mQrCodeButton.setVisibility(View.GONE);
            mAddProspectiveClientButton.setVisibility(View.GONE);
        }
    }


    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else
            input.setErrorEnabled(false);

        return true;
    }


    private boolean validateForm() {
        // Opcionales: cll o phone, floor dpto
        boolean isValid = true;

        isValid = isValid & validateField(mFirstNameEditText, R.string.add_first_name_error, R.id.first_name_wrapper);
        isValid = isValid & validateField(mLastNameEditText, R.string.add_last_name_error, R.id.last_name_wrapper);

        String data = mDniEditText.getText().toString().trim();
        if (data.length() == 0 || data.length() < 7 || data.length() > 8) {
            isValid = false;

            TextInputLayout input = (TextInputLayout) findViewById(R.id.dni_wrapper);
            input.setErrorEnabled(true);
            input.setError(getString(R.string.add_dni_invalid));
            mDniEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        }


        if (mEmailEditText.getText() != null && !mEmailEditText.getText().toString().isEmpty() && !StringHelper.isValidEmail(mEmailEditText.getText())) {
            isValid = false;
            TextInputLayout input = (TextInputLayout) findViewById(R.id.email_wrapper);
            input.setErrorEnabled(true);
            input.setError(getString(R.string.add_email2_error));
            mEmailEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
        }

        if (mPhoneEditText.getText().toString().isEmpty() && mCellPhoneEditText.getText().toString().isEmpty()) {
            isValid = isValid & validateField(mPhoneEditText, R.string.add_phone_error, R.id.phone_wrapper);
            isValid = isValid & validateField(mCellPhoneEditText, R.string.add_phone_error, R.id.cellphone_wrapper);
        } else {
            TextInputLayout phoneWrapper = (TextInputLayout) findViewById(R.id.phone_wrapper);
            phoneWrapper.setErrorEnabled(false);

            TextInputLayout cellPhoneWrapper = (TextInputLayout) findViewById(R.id.cellphone_wrapper);
            cellPhoneWrapper.setErrorEnabled(false);
        }

        final User user = UserController.getInstance().getSignedUser();
        if (user.role == UserRole.ZONE_LEADER && (user.zones != null && user.zones.size() > 1)) {
            if (mSelectedZone == -1)
                isValid = isValid & validateField(mZoneCodeEditText, R.string.add_zone_error, R.id.zone_code_wrapper);
        }
        return isValid;
    }


    private void checkDni() {
        showProgressDialog(R.string.validating_data);

        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        HRequest request = RestApiServices.createCheckDniRequest(mDniEditText.getText().toString(), new Response.Listener<ExistenceStatus>() {
            @Override
            public void onResponse(ExistenceStatus response) {
                dismissProgressDialog();

                if (response == null ){
                    printDniError( getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText());
                } else {
                  if (!response.guardable)  {
                      printDniError(response.message != null ? response.message : (getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText()));
                  }else{
                      if (!mLocationEditText.getText().toString().isEmpty()) {
                          checkZipCode();
                      } else {
                          sendForm(null);
                      }
                  }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                if (error != null && error.getMessage() != null && !error.getMessage().isEmpty()) {
                    Log.e(TAG, (error != null && error.getMessage() != null ? error.getMessage() : ""));
                    printDniError(error.getMessage());
                } else {
                    String mssg = getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText();
                    printDniError(mssg);
                }
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    private void checkZipCode() {

        updateLocation(new Response.Listener<QuoteOption>() {
            @Override
            public void onResponse(QuoteOption response) {

                String zipCode = response.id;
                if (response.id == null) {
                    Log.e(TAG, "zip code error ,,,,,,,,,,,");
                    new android.support.v7.app.AlertDialog.Builder(AddEditPAActivity.this).setMessage(getString(R.string.add_incorrect_postal_code)).setPositiveButton(R.string.option_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

                } else {
                    sendForm(zipCode);
                }
            }
        });
    }

    private void sendForm(String zipCode) {

        mAddProspectiveClientButton.setEnabled(false);

        showProgressDialog(R.string.sending_data);
        User user = UserController.getInstance().getSignedUser();

        if (mClient == null)
            mClient = new ProspectiveClient();

        mClient.firstname = mFirstNameEditText.getText().toString();
        mClient.lastname = mLastNameEditText.getText().toString();
        mClient.dni = Long.parseLong(mDniEditText.getText().toString().trim());
        mClient.birthday = ParserUtils.parseDate(mBirthdayEditText.getText().toString(), "dd-MM-yyyy");

        mClient.location = mLocationEditText.getText().toString();

        mClient.email = mEmailEditText.getText().toString();
        mClient.phoneNumber = mPhoneEditText.getText().toString();
        mClient.celularNumber = mCellPhoneEditText.getText().toString();
        mClient.street = mAddressEditText.getText().toString();

        if (mAddressNumberEditText.getText().length() > 0)
            mClient.streetNumber = Integer.parseInt(mAddressNumberEditText.getText().toString().trim());
        else mClient.streetNumber = -1;

        if (mAddressFloorEditText.getText().length() > 0)
            mClient.floorNumber = Integer.parseInt(mAddressFloorEditText.getText().toString().trim());
        else mClient.floorNumber = -1;

        mClient.department = mAddressDptoEditText.getText().toString();


        mClient.zip = zipCode;
        if (zipCode != null) {
            Log.e(TAG, "Zip Code:::" + zipCode);
        }

        if (mSelectedZone != -1)
            mClient.zone = mAvailableZones.get(mSelectedZone);

        String isCompany = mCompanyCheckbox.isChecked() ? "S" : "N";

        if (!editMode) {
            // ADD or CREATE MODE
            HRequest request = RestApiServices.createAddProspectiveClientRequest(user.id, user.role == UserRole.SALESMAN, isCompany, loadFromQr, mClient, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void v) {
                    dismissProgressDialog();
                    mAddProspectiveClientButton.setEnabled(true);

                    SnackBarHelper.makeSucessful(mContainerView, R.string.add_contact_success).show();

                    Storage.getInstance().setHasChangePAQuantityList(true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000L);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mAddProspectiveClientButton.setEnabled(true);
                    dismissProgressDialog();
                    DialogHelper.showStandardErrorMessage(AddEditPAActivity.this, null);
                }
            });

            AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
        } else {

            // EDIT MODE
            HRequest request = RestApiServices.createEditProspectiveClientRequest(user.role == UserRole.SALESMAN, isCompany, loadFromQr, mClient, new Response.Listener<Void>() {
                @Override
                public void onResponse(Void v) {
                    mAddProspectiveClientButton.setEnabled(true);
                    dismissProgressDialog();
                    SnackBarHelper.makeSucessful(mContainerView, R.string.edit_contact_success).show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goBack();
                        }
                    }, 2000L);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mAddProspectiveClientButton.setEnabled(true);
                    dismissProgressDialog();
                    DialogHelper.showStandardErrorMessage(AddEditPAActivity.this, null);
                }
            });

            AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
        }
    }

    private void goBack() {
        Intent output = new Intent();
        output.putExtra(ConstantsUtil.RESULT_PA, mClient);
        setResult(RESULT_OK, output);
        finish();
    }

    private void setupListeners() {

        if (!mReadOnlyMode) {

            mQrCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    captureQRCode();
                }
            });

            photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "take photo....");

                    setHasToAdddFile(false);
                    attachFileType = DNI_QRCODE_FILE_PREFIX;
                    mImageProvider.detectQRCodeFromFile(attachFilesSubDir + "/" + DNI_QRCODE_FILE_PREFIX, DNI_QRCODE_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mAddProspectiveClientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateForm()) {
                        if (!editMode) {
                            checkDni();
                        } else {
                            if (!mLocationEditText.getText().toString().isEmpty()) {
                                checkZipCode();
                            } else {
                                sendForm(mClient.zip);
                            }
                        }
                    }
                }
            });


            if (!editMode) {
                mDniEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        Log.e(TAG, "dni lenght: " + s.length());

                        // cancel previous search
                        AppController.getInstance().getRestEngine().cancelPendingRequests();

                        if (!s.toString().isEmpty() && s.length() >= 8) {
                            HRequest request = RestApiServices.createCheckDniRequest(mDniEditText.getText().toString(), new Response.Listener<ExistenceStatus>() {
                                @Override
                                public void onResponse(ExistenceStatus response) {
                                    dismissProgressDialog();

                                    if (response != null){
                                        if (!response.guardable){
                                            printDniError(response.message != null ? response.message : getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText());
                                        }
                                        else {
                                            clearDniError();
                                        }
                                    }else{
                                        printDniError(getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText());
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dismissProgressDialog();
                                    if (error != null && error.getMessage() != null && !error.getMessage().isEmpty()) {
                                        Log.e(TAG,  error.getMessage() != null ? error.getMessage() : "");
                                        printDniError(error.getMessage());
                                    } else {
                                        printDniError(getString(R.string.add_dni_no_valid) + " " + mDniEditText.getText());
                                    }
                                }
                            });
                            AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
                        } else {
                            clearDniError();
                        }
                    }
                });
            }

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


                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item, options);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mLocationEditText.setAdapter(adapter);
                                        mLocationEditText.showDropDown();
                                    }
                                });
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error on search loacations");
                        }
                    });
                }
            });

            mLocationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        updateLocation(null);
                    }
                }
            });

            View birthdayButton = findViewById(R.id.birthday_button);
            birthdayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar();
                }
            });

            View btn = findViewById(R.id.zone_button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showZoneAlert();
                }
            });
        }
    }

    private void printDniError(String mssg) {
        TextInputLayout input = (TextInputLayout) findViewById(R.id.dni_wrapper);
        input.setErrorEnabled(true);
        input.setError(mssg);
        mDniEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }

    private void clearDniError() {
        TextInputLayout input = (TextInputLayout) findViewById(R.id.dni_wrapper);
        input.setErrorEnabled(false);
    }

    public void showZoneAlert() {
        if (mAvailableZones == null || mAvailableZones.size() == 0) {
            DialogHelper.showMessage(AddEditPAActivity.this, getString(R.string.error_no_zones));
            return;
        }

        ArrayList<String> reasonsStr = new ArrayList<String>();

        for (Zone r : mAvailableZones) {
            reasonsStr.add(r.name);
        }

        mZonesAlertAdapter = new SpinnerDropDownAdapter(this, reasonsStr, mSelectedZone);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.add_zone_title))
                .setAdapter(mZonesAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedZone = i;
                        mZoneCodeEditText.setText(mAvailableZones.get(i).name);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mZonesAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void qrCodeCaptured(String data) {
        Log.e(TAG, "QRCODE: " + data);

        // parse data
        String[] dataArray = data.split("@");
        try {

            if (dataArray.length < 10) {
                mFirstNameEditText.setText(StringHelper.capitalize(dataArray[2]));
                mLastNameEditText.setText(StringHelper.capitalize(dataArray[1]));
                mDniEditText.setText(dataArray[4]);
                String birthDay = dataArray[6].replace("/", "-");
                mBirthdayEditText.setText(birthDay);
            } else {
                mFirstNameEditText.setText(StringHelper.capitalize(dataArray[5]));
                mLastNameEditText.setText(StringHelper.capitalize(dataArray[4]));
                mDniEditText.setText(dataArray[1]);
                String birthDay = dataArray[7].replace("/", "-");
                mBirthdayEditText.setText(birthDay);
            }

            mFirstNameEditText.setEnabled(false);
            mLastNameEditText.setEnabled(false);
            mDniEditText.setEnabled(false);
            mBirthdayEditText.setEnabled(false);
            loadFromQr = true;

        } catch (Exception e) {

        }
    }

    public void captureQRCode() {
        if (!permHelper.checkPermissionForCamera(this)) {
            permHelper.requestPermissionForCamera(this);
        } else {
            IntentHelper.goToQrCodeScan(this, QRCODE_REQUEST);
        }
    }

    private void searchLocation(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {

        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();

        HRequest request = RestApiServices.createSearchLocationRequest(query, listener, errorListener);
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }


    private void findLocationByZipCode(final String zipCode) {

        String realZipCode = zipCode.substring(0, (zipCode.length()) - 3);
        Log.e(TAG, "REAL ZIP CODE :" + realZipCode + "--------------");

        searchLocation(realZipCode, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                if (response != null && response.size() > 0) {
                    locationDataArray = response;

                    for (QuoteOption option : locationDataArray) {
                        Log.e(TAG, "id:" + option.id + "  title " + option.title);

                        if (option.id.equals(zipCode)) {
                            mLocationEditText.setText(option.title);
                            break;
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error on search loacations");
            }
        });


    }


    public void updateLocation(final Response.Listener<QuoteOption> listener) {

        final String detail = mLocationEditText.getText().toString().trim();
        if (detail != null && !detail.isEmpty()) {
            QuoteOption option = findLocation(detail);

            if (option != null) {
                mLocationDaTaSelected = option;
            } else {
                mLocationDaTaSelected = new QuoteOption();
                mLocationDaTaSelected.title = detail; // loacation desc
                mLocationDaTaSelected.id = null;  // zipCode
            }
            if (listener != null)
                listener.onResponse(mLocationDaTaSelected);
        }
    }

    private QuoteOption findLocation(String detail) {
        QuoteOption result = null;
        for (QuoteOption option : locationDataArray) {
            if (option.title.equals(detail)) {
                result = option;
                break;
            }
        }
        return result;
    }

    private void showCalendar() {
        Date date = null;
        String data = mBirthdayEditText.getText().toString().trim();
        if (data.isEmpty()) {
            date = new Date();
        } else {
            try {
                data = data.replace("-", "/");
                date = mDateFormat.parse(data);
            } catch (Exception e) {
                date = new Date();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, mYear, mMonth, mDay);
        dateDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String out = new StringBuilder().append(String.format("%02d", dayOfMonth)).append("/").append(String.format("%02d", monthOfYear + 1)).append("/").append(year).toString();
            Log.e(TAG, "Birthday: " + out);
            Date today = new Date();

            try {
                Date date = mDateFormat.parse(out);
                if (date.after(today)) {
                    mBirthdayEditText.setText(mDateFormat.format(today).replace("/", "-"));
                } else {
                    mBirthdayEditText.setText(mDateFormat.format(date).replace("/", "-"));
                }

            } catch (Exception e) {
                mBirthdayEditText.setText(mDateFormat.format(today).replace("/", "-"));
            }
            mBirthdayEditText.requestFocus();
        }
    };

}
