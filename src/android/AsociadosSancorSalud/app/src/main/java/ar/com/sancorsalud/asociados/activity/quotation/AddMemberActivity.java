package ar.com.sancorsalud.asociados.activity.quotation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.user.Salesman;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.StringHelper;


public class AddMemberActivity extends BaseActivity {

    private EditText mParentescoEditText;
    private EditText mAgeEditText;
    private EditText mDniEditText;

    private SpinnerDropDownAdapter mParentescoAlertAdapter;
    private int mSelectedParentesco = -1;
    private ArrayList<QuoteOption> mParentescos;

    private boolean unificaAportes = false;
    private boolean filterParentescos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar,R.string.add_member_title);

        mMainContainer = findViewById(R.id.main);

        mParentescoEditText = (EditText) findViewById(R.id.parentesco_input);
        setTypeTextNoSuggestions(mParentescoEditText);

        mAgeEditText = (EditText) findViewById(R.id.age_input);

        mDniEditText = (EditText) findViewById(R.id.dni_input);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            unificaAportes =  bundle.getBoolean(ConstantsUtil.UNIFICA_APORTES, false);
            filterParentescos =  bundle.getBoolean(ConstantsUtil.FILTER_PARENTESCOS, false);


            mParentescos = new ArrayList<>();
            QuoteOption parentescoSelection = new QuoteOption("-1", getResources().getString(R.string.field_parentesco));
            mParentescos.add(parentescoSelection);

            if (filterParentescos) {
                mParentescos.addAll(filterParentescosForAutonomo());
            }else{
                mParentescos.addAll(QuoteOptionsController.getInstance().getParentescos());

            }
        }

        setupListeners();
    }


    private  ArrayList<QuoteOption>  filterParentescosForAutonomo(){
        ArrayList<QuoteOption> filterList = new ArrayList<QuoteOption>();
        ArrayList<QuoteOption> allParentescos = QuoteOptionsController.getInstance().getParentescos();

        if(!allParentescos.isEmpty()) {
           for (QuoteOption parentesco: allParentescos){
               if (!parentesco.id.equals(ConstantsUtil.MENOR_TUTELA_MEMBER) && !parentesco.id.equals(ConstantsUtil.FAMILIAR_A_CARGO_MEMBER)){
                   filterList.add(parentesco);
               }
           }
        }

        return filterList;
    }


    private void setupListeners() {

        View parentescoButton = findViewById(R.id.parentesco_button);
        parentescoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                showParentescoAlert();
            }
        });

        View addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(validateForm()){
                Member member = new Member();
                member.age = Integer.parseInt(mAgeEditText.getText().toString().trim());
                member.dni = (!mDniEditText.getText().toString().trim().isEmpty())  ?  Long.parseLong(mDniEditText.getText().toString().trim()) : -1;
                member.parentesco = mParentescos.get(mSelectedParentesco);

                Intent i = new Intent();
                i.putExtra(ConstantsUtil.RESULT_MEMBER,member);
                setResult(Activity.RESULT_OK,i);
                finish();
            }
            }
        });
    }


    private boolean validateAge(EditText editText, int inputId) {

        TextInputLayout input = (TextInputLayout) findViewById(inputId);
        int age = Integer.parseInt(mAgeEditText.getText().toString().trim());


        QuoteOption parentesco = mParentescos.get(mSelectedParentesco);
        if ((parentesco.id.equals(ConstantsUtil.HIJO_SOLT_MENOR_21_MEMBER) || parentesco.id.equals(ConstantsUtil.HIJO_CONY_SOLT_MENOR_21_MEMBER)) && (age > 21) ){
            input.setError(getString(R.string.member_max_age_error, 21));
            setError(editText, input);
            return false;
        }else if ((parentesco.id.equals(ConstantsUtil.HIJO_SOLT_21_25_MEMBER)) && (age < 21 || age > 25 )){
            input.setError(getString(R.string.member_range_age_error, 21,25));
            setError(editText, input);
            return false;
        }
        else if ((parentesco.id.equals(ConstantsUtil.DISC_MAYOR_25_MEMBER)) &&  (age <= 25) ){
            input.setError(getString(R.string.member_min_age_error, 25));
            setError(editText, input);
            return false;
        }
        else if ((parentesco.id.equals(ConstantsUtil.MENOR_TUTELA_MEMBER)) &&  (age >= 26) ) {
            input.setError(getString(R.string.member_max_age_error, 26));
            setError(editText, input);
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validateConyugeDNI(){
        if (unificaAportes) {
            QuoteOption parentesco = mParentescos.get(mSelectedParentesco);
            if ((parentesco.id.equals(ConstantsUtil.CONYUGE_MEMBER) || parentesco.id.equals(ConstantsUtil.CONCUBINO_MEMBER))) {
                return validateField(mDniEditText, R.string.seleccione_dni_error, R.id.dni_wrapper);
            } else {
                return true;
            }
        }else{
            return true;
        }
    }



    private void setError(EditText editText, TextInputLayout input){
        input.setErrorEnabled(true);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
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
        isValid = isValid & validateField(mParentescoEditText, R.string.seleccione_parentesco, R.id.parentesco_wrapper);
        isValid = isValid & validateField(mAgeEditText, R.string.add_age_error, R.id.age_wrapper);
        //isValid = isValid & validateField(mDniEditText, R.string.add_contact_dni_error, R.id.dni_wrapper);

        if (isValid) {
            isValid = isValid & validateConyugeDNI();
            isValid = isValid & validateAge(mAgeEditText, R.id.parentesco_wrapper);
        }
        return isValid;
    }

    public void showParentescoAlert() {

        ArrayList<String> arrayStr = new ArrayList<String>();
        for (QuoteOption q : mParentescos) {
            if (!q.id.equals(ConstantsUtil.TITULAR_MEMBER)) {
                arrayStr.add(StringHelper.uppercaseFirstCharacter(q.optionName()));
            }
        }

        mParentescoAlertAdapter = new SpinnerDropDownAdapter(this, arrayStr, mSelectedParentesco);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setAdapter(mParentescoAlertAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedParentesco = i;

                        if (i == 0){
                            mSelectedParentesco = -1;
                            mParentescoEditText.setText("");
                        }else {

                            mParentescoEditText.setText(StringHelper.uppercaseFirstCharacter(mParentescos.get(i).optionName()));
                            AddMemberActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    mParentescoAlertAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
