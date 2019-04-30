package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData1AutonomoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.affiliation.TicketPago;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.ParseNumber;
import ar.com.sancorsalud.asociados.utils.Storage;

public class AffiliationAdditionalData1AutonomoEmpresaFragment extends AffiliationAdditionalData1FormaPagoEmpresaFragment {

    private static final String TAG = "AUT_EMP1_FRG";

    public static AffiliationAdditionalData1AutonomoEmpresaFragment newInstance(AdditionalData1AutonomoEmpresa param1, long dni, long cardId, String cuil, QuoteOption empresa, String fechaInicioServicio ) {
        AffiliationAdditionalData1AutonomoEmpresaFragment fragment = new AffiliationAdditionalData1AutonomoEmpresaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_1, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putString(ARG_TITULAR_CUIL, cuil);
        args.putSerializable(ARG_EMPRESA, empresa);
        args.putString(ARG_FECHA_INI_SERV, fechaInicioServicio);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionalData1 = (AdditionalData1AutonomoEmpresa) getArguments().getSerializable(ARG_ADD_DATA_1);
            titularDNI = getArguments().getLong(ARG_TITULAR_DNI);
            cardId = getArguments().getLong(ARG_CARD_ID);
            titularCuil = getArguments().getString(ARG_TITULAR_CUIL);
            mEmpresa = (QuoteOption) getArguments().getSerializable(ARG_EMPRESA);
            mFechaIniServ = getArguments().getString(ARG_FECHA_INI_SERV);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainContainer = inflater.inflate(R.layout.fragment_affiliation_additional_data_aut_emp1, container, false);
        return mMainContainer;
    }
}

