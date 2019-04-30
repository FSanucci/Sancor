package ar.com.sancorsalud.asociados.activity.quotation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Aporte;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;

public class AddAporteActivity extends BaseActivity {

    private static final String TAG = "ADD_APORTE_ACT";

    private EditText mAporteLegalEditText;
    private SpinnerDropDownAdapter mAporteLegalAlertAdapter;
    private int mSelectedAporteLegal=-1;

    private EditText mMontoEditText;

    private TextInputLayout mRemuBrutaTextInputLayout;
    private EditText mRemuBrutaEditText;

    private ArrayList<QuoteOption> mTipoAporteLegal;
    private View aporteHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aporte);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar,R.string.quote_add_aporte_title);

        mMainContainer = findViewById(R.id.main);

        mAporteLegalEditText = (EditText) findViewById(R.id.aporte_legal_input);
        setTypeTextNoSuggestions(mAporteLegalEditText);

        mRemuBrutaEditText = (EditText) findViewById(R.id.remuneracion_bruta_input);

        mRemuBrutaTextInputLayout = (TextInputLayout) findViewById(R.id.remuneracion_bruta_wrapper);
        mMontoEditText = (EditText) findViewById(R.id.monto_input);

        aporteHelp = findViewById(R.id.aporte_help);

        mTipoAporteLegal = new ArrayList<>();
        QuoteOption aporteSelection = new QuoteOption("-1", getResources().getString(R.string.field_aporte_legal));
        mTipoAporteLegal.add(aporteSelection);
        mTipoAporteLegal.addAll(QuoteOptionsController.getInstance().getAporteLegal());


        setupListeners();
    }

    private void setupListeners() {

        mMontoEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                updateFields();
                                return true; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });

        mMontoEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                updateFields();
            }
        });

        aporteHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
               if (mSelectedAporteLegal == 1) {
                    DialogHelper.showMessage(AddAporteActivity.this, getResources().getString(R.string.help_aporte_os));
                }else if (mSelectedAporteLegal == 2)  {
                    DialogHelper.showMessage(AddAporteActivity.this, getResources().getString(R.string.help_remuneracion_bruta));
                }
            }
        });

        View aporteLegalButton = findViewById(R.id.aporte_legal_button);
        aporteLegalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showAporteLegalAlert();
                updateFields();
            }
        });

        aporteLegalButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSelectedAporteLegal = -1;
                mAporteLegalEditText.setText("");
                mMontoEditText.setText("");
                mRemuBrutaTextInputLayout.setVisibility(View.GONE);
                updateFields();

                return true;
            }
        });


        View btn = findViewById(R.id.add_salary);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    Aporte aporte = new Aporte();
                    aporte.tipoAporte = mTipoAporteLegal.get(mSelectedAporteLegal);
                    aporte.monto = Double.parseDouble(mMontoEditText.getText().toString());

                    Intent i = new Intent();
                    i.putExtra(ConstantsUtil.RESULT_APORTE,aporte);
                    setResult(Activity.RESULT_OK,i);
                    finish();
                }
            }
        });
    }

    private void updateFields(){

        if(mSelectedAporteLegal == -1 || mMontoEditText.getText().toString()==null || mMontoEditText.getText().toString().length()==0) {
            mRemuBrutaEditText.setText("");
            return;
        }

        mRemuBrutaTextInputLayout.setVisibility(View.GONE);

        if(mSelectedAporteLegal == 1) {
            mRemuBrutaTextInputLayout.setVisibility(View.VISIBLE);
        }

        double m = Double.parseDouble(mMontoEditText.getText().toString());
        mRemuBrutaEditText.setText(String.format("%.2f", Aporte.remuneracionBruta(m)).replace(",", "."));
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

    public boolean validateForm(){

        boolean isValid = true;
        isValid = isValid & validateField(mAporteLegalEditText, R.string.seleccione_aporte_legal, R.id.aporte_legal_wrapper);
        isValid = isValid & validateField(mMontoEditText, R.string.seleccione_monto, R.id.monto_wrapper);
        return isValid;
    }

    private void showAporteLegalAlert(){

        ArrayList<String> arrayStr = new ArrayList<String>();

        for (QuoteOption q : mTipoAporteLegal) {
            arrayStr.add(q.optionName());
        }

        mAporteLegalAlertAdapter = new SpinnerDropDownAdapter(this, arrayStr, mSelectedAporteLegal);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setAdapter(mAporteLegalAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedAporteLegal = i;

                        if (i == 0){
                            mSelectedAporteLegal = -1;
                            aporteHelp.setVisibility(View.GONE);

                            mAporteLegalEditText.setText("");
                            mMontoEditText.setText("");
                            mRemuBrutaTextInputLayout.setVisibility(View.GONE);
                        }else {
                            aporteHelp.setVisibility(View.VISIBLE);
                            Log.e (TAG, "indice: " + i );

                            mAporteLegalEditText.setText(mTipoAporteLegal.get(i).optionName());
                            updateFields();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mAporteLegalAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

}
