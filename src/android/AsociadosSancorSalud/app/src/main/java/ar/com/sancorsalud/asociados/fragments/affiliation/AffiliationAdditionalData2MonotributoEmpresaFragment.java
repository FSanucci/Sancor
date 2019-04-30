package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.AdditionalData2MonotributoEmpresa;
import ar.com.sancorsalud.asociados.model.affiliation.ConyugeData;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData2;
import ar.com.sancorsalud.asociados.model.affiliation.ObraSocial;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;


public class AffiliationAdditionalData2MonotributoEmpresaFragment extends AffiliationAdditionalData2MonotributoFragment {


    public static AffiliationAdditionalData2MonotributoEmpresaFragment newInstance(AdditionalData2MonotributoEmpresa param1,  long dni, long cardId, long titularCardId, long conyugeCardId , ConyugeData conyugeData) {
        AffiliationAdditionalData2MonotributoEmpresaFragment fragment = new AffiliationAdditionalData2MonotributoEmpresaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADD_DATA_2, param1);
        args.putLong(ARG_TITULAR_DNI, dni);
        args.putLong(ARG_CARD_ID, cardId);
        args.putLong(ARG_TITULAR_CARD_ID, titularCardId);
        args.putLong(ARG_CONYUGE_CARD_ID, conyugeCardId);
        args.putSerializable(ARG_CONYUGE_DATA, conyugeData);
        fragment.setArguments(args);
        return fragment;
    }
}
