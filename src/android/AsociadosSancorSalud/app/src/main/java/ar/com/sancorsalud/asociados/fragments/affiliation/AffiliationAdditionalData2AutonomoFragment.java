package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2Autonomo;
import ar.com.sancorsalud.asociados.model.affiliation.BeneficiarioSUF;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.Storage;
import ar.com.sancorsalud.asociados.utils.StringHelper;

/**
 * Created by sergiocirasa on 6/9/17.
 */

public class AffiliationAdditionalData2AutonomoFragment extends BaseFragment implements IAffiliationAdditionalData2Fragment {

    private static final String TAG = "AF_AUT2_FRG";
    protected static final String ARG_ADD_DATA_1 = "additionalData2";
    protected static final String ARG_ADD_DATA_2 = "titularData";
    protected static final String ARG_ADD_DATA_3 = "member_conyuge_o_concubino";

    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public static final int QRCODE_TITULAR_REQUEST = 700;
    public static final int QRCODE_CONYUGE_REQUEST = 701;

    private ScrollView mScrollView;

    // TITULAR SUF
    private Button mTitularQrCodeButton;
    private EditText mTitularDNITypeEditText;
    private View mTitularDNITypeButton;
    private EditText mTitularDNIEditText;
    private EditText mTitularFirstNameEditText;
    private EditText mTitularLastNameEditText;
    private EditText mTitularBirthdayEditText;
    private View mTitularBirthdayButton;

    private Date mTitularBirthday;
    private int mTitularSelectedDniType = -1;
    private View mConyugeButton;

    private SpinnerDropDownAdapter mTitularDniAdapter;

    // CONYUGE SUF
    private RelativeLayout mConyugeBox;
    private Button mConyugeQrCodeButton;

    private EditText mConyugeDNITypeEditText;
    private View mConyugeDNITypeButton;
    private EditText mConyugeDNIEditText;
    private EditText mConyugeFirstNameEditText;
    private EditText mConyugeLastNameEditText;
    private EditText mConyugeBirthdayEditText;
    private View mConyugeBirthdayButton;

    private Date mConyugeBirthday;
    private int mConyugeSelectedDniType = -1;
    private View mTitularButton;

    private SpinnerDropDownAdapter mConyugeDniAdapter;

    private ArrayList<QuoteOption> mDniTypes;
    private View mMainContainer;

    private TitularData mTitularData;
    private Member mConyugeOConcubino;

    private AdditionalData2Autonomo additionalData2;
    private SimpleDateFormat mDateFormat;

    private boolean editableCard = true;

    private int scan = 0;
    private int TITULAR_QR_SCAN = 1;
    private int CONYUGE_QR_SCAN = 2;


    private DatePickerDialog.OnDateSetListener titularDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();

            Log.e(TAG, "Validity Date: " + out);
            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                if (date.before(today)) {
                    mTitularBirthday = date;
                    mTitularBirthdayEditText.setText(mDateFormat.format(date));
                } else {
                    mTitularBirthday = today;
                    mTitularBirthdayEditText.setText(mDateFormat.format(today));
                }
            } catch (Exception e) {
                mTitularBirthday = today;
                mTitularBirthdayEditText.setText(mDateFormat.format(today));
            }
            mTitularBirthdayEditText.requestFocus();
        }
    };


    private DatePickerDialog.OnDateSetListener conyugeDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();

            Log.e(TAG, "Validity Date: " + out);
            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                if (date.before(today)) {
                    mConyugeBirthday = date;
                    mConyugeBirthdayEditText.setText(mDateFormat.format(date));
                } else {
                    mConyugeBirthday = today;
                    mConyugeBirthdayEditText.setText(mDateFormat.format(today));
                }
            } catch (Exception e) {
                mConyugeBirthday = today;
                mConyugeBirthdayEditText.setText(mDateFormat.format(today));
            }
            mConyugeBirthdayEditText.requestFocus();
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionalData2 = (AdditionalData2Autonomo) getArguments().getSerializable(ARG_ADD_DATA_1);
            mTitularData = (TitularData) getArguments().getSerializable(ARG_ADD_DATA_2);
            mConyugeOConcubino = (Member) getArguments().getSerializable(ARG_ADD_DATA_3);

            mDniTypes = new ArrayList<>();
            QuoteOption docTypeSelection = new QuoteOption("-1", getResources().getString(R.string.field_docType));
            mDniTypes.add(docTypeSelection);
            mDniTypes.addAll(QuoteOptionsController.getInstance().getDocTypes());

            mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_additional_data_aut2, container, false);
        return mMainContainer;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");

        // TITULAR
        mTitularQrCodeButton = (Button) view.findViewById(R.id.titular_qrcode_button);
        mTitularDNITypeEditText = (EditText) view.findViewById(R.id.titular_dni_type_input);
        setTypeTextNoSuggestions(mTitularDNITypeEditText);

        mTitularDNITypeButton = view.findViewById(R.id.titular_dni_type_button);
        mTitularDNIEditText = (EditText) view.findViewById(R.id.titular_dni_number_input);

        mTitularFirstNameEditText = (EditText) view.findViewById(R.id.titular_first_name_input);
        setTypeTextNoSuggestions(mTitularFirstNameEditText);

        mTitularLastNameEditText = (EditText) view.findViewById(R.id.titular_last_name_input);
        setTypeTextNoSuggestions(mTitularLastNameEditText);

        mTitularBirthdayEditText = (EditText) view.findViewById(R.id.titular_birthDay_input);

        mTitularBirthdayButton = view.findViewById(R.id.titular_birthDay_button);
        mConyugeButton = view.findViewById(R.id.conyuge_button);


        // CONYUGE
        mConyugeBox = (RelativeLayout) view.findViewById(R.id.suf_conyuge_box);
        mConyugeQrCodeButton = (Button) view.findViewById(R.id.conyuge_qrcode_button);
        mConyugeDNITypeEditText = (EditText) view.findViewById(R.id.conyuge_dni_type_input);
        setTypeTextNoSuggestions(mConyugeDNITypeEditText);

        mConyugeDNITypeButton = view.findViewById(R.id.conyuge_dni_type_button);
        mConyugeDNIEditText = (EditText) view.findViewById(R.id.conyuge_dni_number_input);

        mConyugeFirstNameEditText = (EditText) view.findViewById(R.id.conyuge_first_name_input);
        setTypeTextNoSuggestions(mConyugeFirstNameEditText);

        mConyugeLastNameEditText = (EditText) view.findViewById(R.id.conyuge_last_name_input);
        setTypeTextNoSuggestions(mConyugeLastNameEditText);

        mConyugeBirthdayEditText = (EditText) view.findViewById(R.id.conyuge_birthDay_input);

        mConyugeBirthdayButton = view.findViewById(R.id.conyuge_birthDay_button);
        mTitularButton = view.findViewById(R.id.titular_button);

        mScrollView = (ScrollView) view.findViewById(R.id.scroll);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);
        checkEditableCardMode();

        setupListener();
        initializeForm();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsHelper.REQUEST_CAMERA_PERMISSION ) {
            if (scan == TITULAR_QR_SCAN){
                scan = 0;
                captureTitularQRCode();
            }else{
                scan = 0;
                captureConyugeQRCode();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == QRCODE_TITULAR_REQUEST || requestCode == QRCODE_CONYUGE_REQUEST) && resultCode == Activity.RESULT_OK) {
            String message = data.getStringExtra(ConstantsUtil.QRCODE_DATA);
            Log.e(TAG, "Result:" + message);
            if (message != null && !message.isEmpty()) {

                if (requestCode == QRCODE_TITULAR_REQUEST) {
                    qrTitularCodeCaptured(message);
                }else{
                    qrConyugeCodeCaptured(message);
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public IAdditionalData2 getAdditionalData2() {
        Log.e(TAG, "----- getAdditionalData2 -------");
        if (additionalData2 == null) {
            additionalData2 = new AdditionalData2Autonomo();
        }

        // TITULAR
        BeneficiarioSUF sufTitular = new BeneficiarioSUF();

        if (mTitularSelectedDniType != -1)
            sufTitular.docType = mDniTypes.get(mTitularSelectedDniType);


        if (!mTitularDNIEditText.getText().toString().isEmpty()) {
            sufTitular.dni = Integer.parseInt(mTitularDNIEditText.getText().toString());
        }

        if (!mTitularFirstNameEditText.getText().toString().isEmpty()) {
            sufTitular.firstname = mTitularFirstNameEditText.getText().toString();
        }

        if (!mTitularLastNameEditText.getText().toString().isEmpty()) {
            sufTitular.lastname = mTitularLastNameEditText.getText().toString();
        }

        if (mTitularBirthday != null)
            sufTitular.birthday = mTitularBirthday;


        additionalData2.setSufTitular(sufTitular);


        // CONYUGE
        BeneficiarioSUF sufConyuge = new BeneficiarioSUF();

        if (mConyugeSelectedDniType != -1)
            sufConyuge.docType = mDniTypes.get(mConyugeSelectedDniType);


        if (!mConyugeDNIEditText.getText().toString().isEmpty()) {
            sufConyuge.dni = Integer.parseInt(mConyugeDNIEditText.getText().toString());
        }

        if (!mConyugeFirstNameEditText.getText().toString().isEmpty()) {
            sufConyuge.firstname = mConyugeFirstNameEditText.getText().toString();
        }

        if (!mConyugeLastNameEditText.getText().toString().isEmpty()) {
            sufConyuge.lastname = mConyugeLastNameEditText.getText().toString();
        }

        if (mConyugeBirthday != null)
            sufConyuge.birthday = mConyugeBirthday;

        additionalData2.setSufConyuge(sufConyuge);

        return additionalData2;
    }

    @Override
    public boolean isValidSection() {
        return validateForm();
    }

    private void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);


        if (additionalData2 != null && additionalData2.getSufTitular() != null) {
            BeneficiarioSUF sufTitular =  additionalData2.getSufTitular();

            if (sufTitular.docType != null) {
                for (int i = 0; i < mDniTypes.size(); i++) {
                    QuoteOption opt = mDniTypes.get(i);
                    if (opt.equals(sufTitular.docType)) {
                        mTitularSelectedDniType = i;
                        break;
                    }
                }

                mTitularDNITypeEditText.setText(sufTitular.docType.optionName());
            }
            if (sufTitular.dni > 0L)
                mTitularDNIEditText.setText("" + sufTitular.dni);

            if (sufTitular.firstname != null && !sufTitular.firstname.isEmpty())
                mTitularFirstNameEditText.setText(sufTitular.firstname);

            if (sufTitular.lastname != null && !sufTitular.lastname.isEmpty())
                mTitularLastNameEditText.setText(sufTitular.lastname);

            if (sufTitular.birthday != null && sufTitular.birthday != null) {
                mTitularBirthday = sufTitular.birthday;
                mTitularBirthdayEditText.setText(ParserUtils.parseDate(sufTitular.birthday, DATE_FORMAT));
            }
        }


        if (mConyugeOConcubino == null) {
            mConyugeButton.setVisibility(View.GONE);
            mConyugeBox.setVisibility(View.GONE);
        }else{
            mConyugeButton.setVisibility(View.VISIBLE);
            mConyugeBox.setVisibility(View.VISIBLE);

            if (additionalData2 != null && additionalData2.getSufConyuge() != null) {
                BeneficiarioSUF sufConyuge =  additionalData2.getSufConyuge();

                if (sufConyuge.docType != null) {
                    for (int i = 0; i < mDniTypes.size(); i++) {
                        QuoteOption opt = mDniTypes.get(i);
                        if (opt.equals(sufConyuge.docType)) {
                            mConyugeSelectedDniType = i;
                            break;
                        }
                    }

                    mConyugeDNITypeEditText.setText(sufConyuge.docType.optionName());
                }
                if (sufConyuge.dni > 0L)
                    mConyugeDNIEditText.setText("" + sufConyuge.dni);

                if (sufConyuge.firstname != null && !sufConyuge.firstname.isEmpty())
                    mConyugeFirstNameEditText.setText(sufConyuge.firstname);

                if (sufConyuge.lastname != null && !sufConyuge.lastname.isEmpty())
                    mConyugeLastNameEditText.setText(sufConyuge.lastname);

                if (sufConyuge.birthday != null && sufConyuge.birthday != null) {
                    mConyugeBirthday = sufConyuge.birthday;
                    mConyugeBirthdayEditText.setText(ParserUtils.parseDate(sufConyuge.birthday, DATE_FORMAT));
                }
            }

        }
    }

    private void checkEditableCardMode() {

        if (editableCard) {
            mTitularQrCodeButton.setVisibility(View.VISIBLE);
            if (mConyugeOConcubino != null) {
                mConyugeQrCodeButton.setVisibility(View.VISIBLE);
            }
        } else {
            mTitularQrCodeButton.setVisibility(View.GONE);
            if (mConyugeOConcubino != null) {
                mConyugeQrCodeButton.setVisibility(View.GONE);
            }
        }

        mTitularDNIEditText.setFocusable(editableCard);
        mTitularFirstNameEditText.setFocusable(editableCard);
        mTitularLastNameEditText.setFocusable(editableCard);

        mConyugeDNIEditText.setFocusable(editableCard);
        mConyugeFirstNameEditText.setFocusable(editableCard);
        mConyugeLastNameEditText.setFocusable(editableCard);
    }

    private void setupListener() {

        if (editableCard) {

            mTitularQrCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scan = TITULAR_QR_SCAN;
                    captureTitularQRCode();
                }
            });

            mTitularDNITypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showDNITypeTitularAlert();
                }
            });
            mTitularDNITypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mTitularSelectedDniType = -1;
                    mTitularDNITypeEditText.setText("");
                    return true;
                }
            });



            mTitularBirthdayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);

                    String data = mTitularBirthdayEditText.getText().toString().trim();
                    if (!data.isEmpty()) {

                        try {
                            mTitularBirthday = mDateFormat.parse(data);
                        } catch (Exception e) {
                            mTitularBirthday = new Date();
                        }
                    }
                    showCalendar(mTitularBirthday, titularDateSetListener, null, new Date());
                }
            });
            mTitularBirthdayButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mTitularBirthdayEditText.setText("");
                    return true;
                }
            });

            mConyugeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < mDniTypes.size(); i++) {
                        QuoteOption opt = mDniTypes.get(i);
                        if (opt.equals(mConyugeOConcubino.docType)) {
                            mTitularSelectedDniType = i;
                            break;
                        }
                    }
                    mTitularDNITypeEditText.setText(mConyugeOConcubino.docType.optionName());

                    mTitularDNIEditText.setText("" + (mConyugeOConcubino.dni > 0L ? mConyugeOConcubino.dni : ""));
                    mTitularFirstNameEditText.setText((mConyugeOConcubino.firstname != null && !mConyugeOConcubino.firstname.isEmpty()) ? mConyugeOConcubino.firstname : "");
                    mTitularLastNameEditText.setText((mConyugeOConcubino.lastname != null && !mConyugeOConcubino.lastname.isEmpty()) ? mConyugeOConcubino.lastname : "");

                    mTitularBirthday = mConyugeOConcubino.birthday;
                    if (mTitularBirthday != null) {
                        mTitularBirthdayEditText.setText(ParserUtils.parseDate(mConyugeOConcubino.birthday, DATE_FORMAT));
                    }
                }
            });



            // CONYUGE BOX

            mConyugeQrCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scan = CONYUGE_QR_SCAN;
                    captureConyugeQRCode();
                }
            });

            mConyugeDNITypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showDNITypeConyugeAlert();
                }
            });
            mConyugeDNITypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mConyugeSelectedDniType = -1;
                    mConyugeDNITypeEditText.setText("");
                    return true;
                }
            });


            mConyugeBirthdayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);

                    String data = mConyugeBirthdayEditText.getText().toString().trim();
                    if (!data.isEmpty()) {

                        try {
                            mConyugeBirthday = mDateFormat.parse(data);
                        } catch (Exception e) {
                            mConyugeBirthday = new Date();
                        }
                    }
                    showCalendar(mConyugeBirthday, conyugeDateSetListener, null, new Date());
                }
            });
            mConyugeBirthdayButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mConyugeBirthdayEditText.setText("");
                    return true;
                }
            });

            mTitularButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < mDniTypes.size(); i++) {
                        QuoteOption opt = mDniTypes.get(i);
                        if (opt.equals(mTitularData.docType)) {
                            mConyugeSelectedDniType = i;
                            break;
                        }
                    }
                    mConyugeDNITypeEditText.setText(mTitularData.docType.optionName());

                    mConyugeDNIEditText.setText("" + (mTitularData.dni > 0L ? mTitularData.dni : ""));
                    mConyugeFirstNameEditText.setText((mTitularData.firstname != null && !mTitularData.firstname.isEmpty()) ? mTitularData.firstname : "");
                    mConyugeLastNameEditText.setText((mTitularData.lastname != null && !mTitularData.lastname.isEmpty()) ? mTitularData.lastname : "");

                    mConyugeBirthday = mTitularData.birthday;
                    if (mConyugeBirthday != null) {
                        mConyugeBirthdayEditText.setText(ParserUtils.parseDate(mTitularData.birthday, DATE_FORMAT));
                    }
                }
            });

        }
    }

    private boolean validateForm() {
        return true;
    }

    private void showDNITypeTitularAlert() {

        ArrayList<String> dniTypeStr = new ArrayList<String>();
        for (QuoteOption q : mDniTypes) {
            dniTypeStr.add(q.optionName());
        }

        mTitularDniAdapter = new SpinnerDropDownAdapter(getActivity(), dniTypeStr, mTitularSelectedDniType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mTitularDniAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTitularSelectedDniType = i;
                        if (i == 0){
                            mTitularSelectedDniType = -1;
                            mTitularDNITypeEditText.setText("");
                        }else {
                            QuoteOption opt = mDniTypes.get(mTitularSelectedDniType);
                            mTitularDNITypeEditText.setText(opt.optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mTitularDniAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDNITypeConyugeAlert() {

        ArrayList<String> dniConyTypeStr = new ArrayList<String>();
        for (QuoteOption q : mDniTypes) {
            dniConyTypeStr.add(q.optionName());
        }

        mConyugeDniAdapter = new SpinnerDropDownAdapter(getActivity(), dniConyTypeStr, mConyugeSelectedDniType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mConyugeDniAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mConyugeSelectedDniType = i;
                        if (i == 0){
                            mConyugeSelectedDniType = -1;
                            mConyugeDNITypeEditText.setText("");
                        }else {
                            QuoteOption opt = mDniTypes.get(mConyugeSelectedDniType);

                            mConyugeDNITypeEditText.setText(opt.optionName());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mConyugeDniAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCalendar(Date date, DatePickerDialog.OnDateSetListener listener, Date minDate, Date maxDate) {
        Calendar cal = Calendar.getInstance();
        if (date == null)
            cal.setTime(new Date());
        else cal.setTime(date);

        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay);
        if (maxDate != null)
            dateDialog.getDatePicker().setMaxDate(maxDate.getTime());
        if (minDate != null)
            dateDialog.getDatePicker().setMinDate(minDate.getTime());

        dateDialog.show();
    }

    private void captureTitularQRCode() {
        if (!PermissionsHelper.getInstance().checkPermissionForCamera(getActivity())) {
            PermissionsHelper.getInstance().requestPermissionForCamera(getActivity());
        } else {
            IntentHelper.goToQrCodeScan(getActivity(), QRCODE_TITULAR_REQUEST);
        }
    }

    private void captureConyugeQRCode() {
        if (!PermissionsHelper.getInstance().checkPermissionForCamera(getActivity())) {
            PermissionsHelper.getInstance().requestPermissionForCamera(getActivity());
        } else {
            IntentHelper.goToQrCodeScan(getActivity(), QRCODE_CONYUGE_REQUEST);
        }
    }


    private void qrTitularCodeCaptured(String data) {
        Log.e(TAG, "TIULAR QRCODE: " + data);
        // parse data
        String[] dataArray = data.split("@");
        try {

            String birthDay = "";
            if (dataArray.length < 10) {
                mTitularFirstNameEditText.setText(StringHelper.capitalize(dataArray[2]));
                mTitularLastNameEditText.setText(StringHelper.capitalize(dataArray[1]));
                mTitularDNIEditText.setText(dataArray[4]);
                birthDay = dataArray[6];
            } else {
                mTitularFirstNameEditText.setText(StringHelper.capitalize(dataArray[5]));
                mTitularLastNameEditText.setText(StringHelper.capitalize(dataArray[4]));
                mTitularDNIEditText.setText(dataArray[1]);
                birthDay = dataArray[7];
            }

            Date date = ParserUtils.parseDate(birthDay, "dd/MM/yyyy");
            mTitularBirthdayEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));

        } catch (Exception e) {

        }
    }

    private void qrConyugeCodeCaptured(String data) {
        Log.e(TAG, "CONYUGE QRCODE: " + data);
        // parse data
        String[] dataArray = data.split("@");
        try {

            String birthDay = "";
            if (dataArray.length < 10) {
                mConyugeFirstNameEditText.setText(StringHelper.capitalize(dataArray[2]));
                mConyugeLastNameEditText.setText(StringHelper.capitalize(dataArray[1]));
                mConyugeDNIEditText.setText(dataArray[4]);
                birthDay = dataArray[6];
            } else {
                mConyugeFirstNameEditText.setText(StringHelper.capitalize(dataArray[5]));
                mConyugeLastNameEditText.setText(StringHelper.capitalize(dataArray[4]));
                mConyugeDNIEditText.setText(dataArray[1]);
                birthDay = dataArray[7];
            }

            Date date = ParserUtils.parseDate(birthDay, "dd/MM/yyyy");
            mConyugeBirthdayEditText.setText(ParserUtils.parseDate(date, DATE_FORMAT));

        } catch (Exception e) {

        }
    }
}
