package ar.com.sancorsalud.asociados.activity.affiliation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.WorkflowController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.EEMorosidadData;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class AddEntidadEmpleadoraActivity extends BaseActivity {

    private static final String TAG = "ADD_EE_ACT";
    private static final String RECIBOS_FILE_PREFIX = "recibo";


    private ScrollView mScrollView;
    private EditText mEntityRemuneracionText;
    private EditText mCuitText;
    private EditText mRazonSocialText;
    private EditText mAreaPhoneEditText;
    private EditText mPhoneEditText;
    private EditText mEmpresaEditText;
    private EditText mInputDateEditText;

    // RECIBOS Files
    private RecyclerView mRecibosRecyclerView;
    private AttachFilesAdapter mRecibosFileAdapter;
    private Button addRecibosdButton;
    private List<AttachFile> tmpRecibosFiles = new ArrayList<AttachFile>();

    private View editButton;

    private long titularDNI;
    private int index;
    private EntidadEmpleadora ee;

    private SimpleDateFormat mDateFormat;
    private boolean editableCard = true;


    private DatePickerDialog.OnDateSetListener entityDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "EE Date: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                if (date.after(today)) {
                    mInputDateEditText.setText(mDateFormat.format(today));
                } else {
                    mInputDateEditText.setText(mDateFormat.format(date));
                }
            } catch (Exception e) {
                mInputDateEditText.setText(mDateFormat.format(today));
            }
            mInputDateEditText.requestFocus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entidad_empleadora);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar,R.string.affiliation_ee);

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        mMainContainer = findViewById(R.id.main_content);

        mScrollView = (ScrollView)findViewById(R.id.scroll);
        mEntityRemuneracionText = (EditText) findViewById(R.id.entidad_remuneracion_input);

        mCuitText = (EditText) findViewById(R.id.entidad_cuit_input);
        mRazonSocialText = (EditText) findViewById(R.id.entidad_razon_social_input);
        setTypeTextNoSuggestions(mRazonSocialText);

        mAreaPhoneEditText = (EditText) findViewById(R.id.entidad_area_phone_input);
        mPhoneEditText = (EditText) findViewById(R.id.entidad_phone_input);

        mEmpresaEditText = (EditText) findViewById(R.id.entidad_emp_number);
        setTypeTextNoSuggestions(mEmpresaEditText);

        mInputDateEditText = (EditText) findViewById(R.id.entidad_input_date);

        editButton = findViewById(R.id.edit_button);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            titularDNI = getIntent().getLongExtra(ConstantsUtil.AFFILIATION_TITULAR_DNI, -1L);
            index = bundle.getInt(ConstantsUtil.AFFILIATION_EE_INDEX, -1);
            ee = (EntidadEmpleadora) bundle.getSerializable(ConstantsUtil.AFFILIATION_ENTIDAD_EMPLEADORA);

            attachFilesSubDir = titularDNI + "/affiliation/ee/" + index ;
            mImageProvider = new ImageProvider(this);
            fillAllPhysicalFiles();
        }
    }

    @Override
    public void updateFileList(AttachFile attachFile) {
        if (attachFileType.equals(RECIBOS_FILE_PREFIX)) {
            mRecibosFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position){
        if (attachFileType.equals(RECIBOS_FILE_PREFIX)) {
            mRecibosFileAdapter.removeItem(position);
        }
    }

    private void fillAllPhysicalFiles() {

        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll AddData1 Data PhysicalFiles ....");
                fillTitularRecibosFiles();
            }
        }).start();
    }


    private void fillTitularRecibosFiles() {

        if (!ee.reciboSueldoFiles.isEmpty()) {

            Log.e(TAG, "fill Titular Recibos sueldo !!....");

            final AttachFile titularReciboFile = ee.reciboSueldoFiles.remove(0);
            if (titularReciboFile.filePath == null || titularReciboFile.filePath.isEmpty()) {

                CardController.getInstance().getFile(AddEntidadEmpleadoraActivity.this, attachFilesSubDir + "/" + RECIBOS_FILE_PREFIX, titularReciboFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {
                        Log.e(TAG, "ok getting Ttiular Recibo File !!!!");

                        if (resultFile != null) {
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpRecibosFiles.add(resultFile);
                            fillTitularRecibosFiles();

                        } else {
                            Log.e(TAG, "Null Ttiular Recibo File ....");
                            tmpRecibosFiles.add(titularReciboFile);
                            fillTitularRecibosFiles();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpRecibosFiles.add(titularReciboFile);
                        fillTitularRecibosFiles();
                    }
                });
            } else {
                tmpRecibosFiles.add(titularReciboFile);
                fillTitularRecibosFiles();
            }
        } else {
            ee.reciboSueldoFiles.addAll(tmpRecibosFiles);
            tmpRecibosFiles = new ArrayList<AttachFile>();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    initialize();
                }
            });
        }
    }


    private void initialize() {
        initializeForm();
        setupListeners();
    }

    private void initializeForm(){
        mScrollView.smoothScrollTo(0, 0);
        initRecibosFiles();

        if (ee.remuneracion != -1)
            mEntityRemuneracionText.setText(String.format("%.2f", ee.remuneracion).replace(",", "."));

        if (ee.cuit != null && !ee.cuit.isEmpty())
            mCuitText.setText(ee.cuit);

        if (ee.razonSocial != null && !ee.razonSocial.isEmpty())
            mRazonSocialText.setText(ee.razonSocial);

        if (ee.areaPhone != null && !ee.areaPhone.isEmpty())
            mAreaPhoneEditText.setText(ee.areaPhone);

        if (ee.phone != null && !ee.phone.isEmpty())
            mPhoneEditText.setText(ee.phone);

        if (ee.empresaId != -1)
            mEmpresaEditText.setText(Long.valueOf(ee.empresaId).toString());

        if (ee.inputDate != null)
            mInputDateEditText.setText(ee.getInputDate());

        checkEditableCardMode();

    }


    private void initRecibosFiles() {

        mRecibosFileAdapter = new AttachFilesAdapter(ee.reciboSueldoFiles, true);

        mRecibosRecyclerView = (RecyclerView) findViewById(R.id.recibo_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mRecibosRecyclerView.getContext());
        mRecibosRecyclerView.setLayoutManager(attachLayoutManager);
        mRecibosRecyclerView.setAdapter(mRecibosFileAdapter);
        mRecibosRecyclerView.setHasFixedSize(true);
        addRecibosdButton = (Button) findViewById(R.id.recibo_button);
    }


    private void checkEditableCardMode() {

        mEntityRemuneracionText.setFocusable(editableCard);
        mCuitText.setFocusable(editableCard);
        mRazonSocialText.setFocusable(editableCard);
        mAreaPhoneEditText.setFocusable(editableCard);
        mPhoneEditText.setFocusable(editableCard);
        mEmpresaEditText.setFocusable(editableCard);

        if (!editableCard){
            disableView(addRecibosdButton);
            editButton.setVisibility(View.GONE);
        }
    }


    private void setupListeners() {

        setupImageProvider();

        if (editableCard) {


            mCuitText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mCuitText.getText().length() >= 11) {

                        WorkflowController.getInstance().verifiyMorosidadRequest(Long.valueOf(mCuitText.getText().toString().trim()), new Response.Listener<EEMorosidadData>() {
                            @Override
                            public void onResponse(EEMorosidadData result) {
                                if (result != null && result.hasMorosidad) {
                                    Log.e(TAG, "EE detail is morosa ?" + result.hasMorosidad);

                                    mRazonSocialText.setText(result.eeDescription);
                                    mAreaPhoneEditText.setText(result.eeAreaPrefix);
                                    mPhoneEditText.setText(result.eePhone);

                                   if (result.eeId != -1) {
                                       mEmpresaEditText.setText(Long.valueOf(result.eeId).toString());
                                   }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "verifiyEEMorosidad detail error .....");
                            }
                        });
                    }
                }
            });


            // FILES
            addRecibosdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = RECIBOS_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + RECIBOS_FILE_PREFIX, RECIBOS_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            mRecibosFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "ee onItemDeleteClick()!!!!!:" + position);
                    attachFileType = RECIBOS_FILE_PREFIX;
                    AttachFile reciboFile = ee.reciboSueldoFiles.get(position);
                    if (reciboFile.id != -1) {
                        removeFile(reciboFile, position);
                    }
                }
            });

            View dateButton = mMainContainer.findViewById(R.id.entidad_date_button);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar(entityDateSetListener);
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (validateForm()) {

                        Log.e(TAG, "update EE....remuneracion: " + ee.remuneracion);

                        if (!mEntityRemuneracionText.getText().toString().trim().isEmpty())
                            ee.remuneracion = Double.valueOf(mEntityRemuneracionText.getText().toString().trim().replace(",", "."));

                        ee.cuit = mCuitText.getText().toString().trim();
                        ee.razonSocial = mRazonSocialText.getText().toString().trim();
                        ee.areaPhone = mAreaPhoneEditText.getText().toString().trim();
                        ee.phone = mPhoneEditText.getText().toString().trim();

                        try {
                            ee.inputDate = ParserUtils.parseDate(mInputDateEditText.getText().toString(), "yyyy-MM-dd");
                        } catch (Exception e) {
                        }

                        if (!mEmpresaEditText.getText().toString().trim().isEmpty()) {
                            ee.empresaId = Long.valueOf(mEmpresaEditText.getText().toString().trim());
                        } else {
                            ee.empresaId = -1L;
                        }

                        if (mRecibosFileAdapter != null) {
                            ee.reciboSueldoFiles = mRecibosFileAdapter.getAttachFiles();
                        }

                        // if user fill this screen try to add/ Update EE , reset accountCuil if error
                        // if usr do not fill this screen add/update operation will be done in Affiliation activity in silence way
                        if (ee != null && AppController.getInstance().isNetworkAvailable()) {

                            hideSoftKeyboard(mMainContainer);

                            showProgressDialog(R.string.adding_ee_loading);
                            CardController.getInstance().addEntidadEmpleadora(ee, new Response.Listener<Long>() {
                                @Override
                                public void onResponse(Long empId) {
                                    dismissProgressDialog();
                                    SnackBarHelper.makeSucessful(mMainContainer, R.string.success_saved_ee).show();

                                    if (empId != null && empId != -1L){
                                        ee.empresaId = empId;
                                    }

                                    AppController.getInstance().storage.setEntidadEmpleadoraAvailable(true);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent();
                                            i.putExtra(ConstantsUtil.RESULT_EE, ee);
                                            setResult(Activity.RESULT_OK, i);
                                            finish();
                                        }
                                    }, 1500L);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dismissProgressDialog();
                                    Log.e(TAG, "addEntidadEmpleadora  error .....");

                                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                    DialogHelper.showMessage(AddEntidadEmpleadoraActivity.this, getResources().getString(R.string.error_ee_request), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                    AppController.getInstance().storage.setEntidadEmpleadoraAvailable(false);

                                }
                            });
                        } else {
                            SnackBarHelper.makeError(mMainContainer, R.string.no_internet_coneccion).show();
                        }
                    }
                }
            });
        }

        // ADAPTERS
        mRecibosFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile reciboFile = ee.reciboSueldoFiles.get(position);
                Log.e(TAG, "ee file  path:" + reciboFile.filePath);
                loadFile(reciboFile);
            }
        });
    }

    private void setError(EditText editText, TextInputLayout input){
        input.setErrorEnabled(true);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }

    public boolean validateForm(){

        boolean isValid = true;
        //isValid = isValid & validateField(mParentescoEditText, R.string.seleccione_parentesco, R.id.parentesco_wrapper);
        //isValid = isValid & validateField(mAgeEditText, R.string.add_age_error, R.id.age_wrapper);
        //isValid = isValid & validateField(mDniEditText, R.string.add_contact_dni_error, R.id.dni_wrapper);

        return isValid;
    }


    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private void showCalendar(DatePickerDialog.OnDateSetListener listener) {
        //Date today = new Date();
        Date date = null;
        String data = mInputDateEditText.getText().toString().trim();
        if (data.isEmpty()) {
            date = new Date();
        } else {
            try {
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

        DatePickerDialog dateDialog = new DatePickerDialog(AddEntidadEmpleadoraActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay);
        //dateDialog.getDatePicker().setMinDate(today.getTime());
        dateDialog.show();
    }
}