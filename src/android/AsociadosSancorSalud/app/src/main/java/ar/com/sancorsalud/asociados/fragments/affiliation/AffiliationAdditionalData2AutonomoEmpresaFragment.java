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
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2AutonomoIndividual;
import ar.com.sancorsalud.asociados.model.affiliation.BeneficiarioSUF;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;
import ar.com.sancorsalud.asociados.model.affiliation.Member;
import ar.com.sancorsalud.asociados.model.affiliation.TitularData;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;


public class AffiliationAdditionalData2AutonomoEmpresaFragment extends AffiliationAdditionalData2AutonomoFragment {

    private static final String TAG = "AF_AUT_EMP_FRG";

    public static AffiliationAdditionalData2AutonomoEmpresaFragment newInstance(AdditionalData2AutonomoEmpresa param1, TitularData tData, Member conyugeOConcubino) {
        AffiliationAdditionalData2AutonomoEmpresaFragment fragment = new AffiliationAdditionalData2AutonomoEmpresaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_1, param1);
        args.putSerializable(ARG_ADD_DATA_2, tData);
        args.putSerializable(ARG_ADD_DATA_3,conyugeOConcubino);
        fragment.setArguments(args);
        return fragment;
    }

}
