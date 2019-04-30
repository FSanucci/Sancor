package ar.com.sancorsalud.asociados.fragments.quote.client;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AdicionalesOptativosData;
import ar.com.sancorsalud.asociados.model.client.Client;
import ar.com.sancorsalud.asociados.model.Member;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.Quotation;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.model.quotation.QuotedClientData;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.StringHelper;

import static android.app.Activity.RESULT_OK;


public class ExistentClientDataFragment extends BaseFragment {

    private static final String TAG = "QUOTED_FRG";

    public static final int QRCODE_REQUEST = 100;
    private static final String ARG_PARAM1 = "quotation";
    private static final String ARG_PARAM2 = "dni";
    private static final String ARG_PARAM3 = "optativosData";

    protected PermissionsHelper permHelper = PermissionsHelper.getInstance();

    private Button mQrCodeButton;
    private Button mDniSearchButton;
    private EditText mDniEditText;

    private TextView mTitularTitleTextView;
    private TextView mQuotedTitleTextView;

    private TextView mTitularTextView;
    private TextView mQuotedTextView;

    private Quotation mQuotation;
    private String  mDni;
    private AdicionalesOptativosData mOptativosData;

    private ProgressBar progressBar;

    public ExistentClientDataFragment() {
        // Required empty public constructor
    }

    public static ExistentClientDataFragment newInstance() {
        ExistentClientDataFragment fragment = new ExistentClientDataFragment();
        return fragment;
    }

    public static ExistentClientDataFragment newInstance(Quotation quotation, String dni) {
        return newInstance(quotation, dni, null);
    }

    public static ExistentClientDataFragment newInstance(Quotation quotation, String dni, AdicionalesOptativosData mOptativosData) {
        ExistentClientDataFragment fragment = newInstance();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, quotation);
        args.putSerializable(ARG_PARAM2, dni);

        if (mOptativosData  != null){
            args.putSerializable(ARG_PARAM3, mOptativosData);
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuotation = (Quotation) getArguments().getSerializable(ARG_PARAM1);
            mDni = getArguments().getString(ARG_PARAM2);
            mOptativosData = (AdicionalesOptativosData) getArguments().getSerializable(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_additional_user_data, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        return view;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PermissionsHelper.REQUEST_CAMERA_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentHelper.goToQrCodeScan(getActivity(), QRCODE_REQUEST);
                } else {
                    DialogHelper.showMessage(getActivity(), getString(R.string.error_cam_permission));
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == QRCODE_REQUEST) {
                String message = data.getStringExtra(ConstantsUtil.QRCODE_DATA);
                if (message != null && !message.isEmpty()) {
                    qrCodeCaptured(message);
                }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Quotation getActualQuotation(){
        return mQuotation;
    }

    public String getDNI(){
        return mDni;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mQrCodeButton = (Button) view.findViewById(R.id.qrcode_button);
        mDniSearchButton = (Button) view.findViewById(R.id.dni_button);
        mDniEditText = (EditText) view.findViewById(R.id.dni_input);

        mTitularTitleTextView = (TextView) view.findViewById(R.id.titular_title);
        mQuotedTitleTextView = (TextView) view.findViewById(R.id.quoted_title);

        mTitularTextView = (TextView) view.findViewById(R.id.titular_description);
        mQuotedTextView = (TextView) view.findViewById(R.id.quoted_description);

        showContent(false);
        setupListeners();
    }

    public void captureQRCode() {
        if (!permHelper.checkPermissionForCamera(getActivity())) {
            permHelper.requestPermissionForCamera(getActivity());
        } else {
            IntentHelper.goToQrCodeScan(getActivity(), QRCODE_REQUEST);
        }
    }

    public void qrCodeCaptured(String data) {

        String[] dataArray = data.split("@");
        try {

            if (dataArray.length < 10) {
                mDniEditText.setText(dataArray[4]);
            } else {
                mDniEditText.setText(dataArray[1]);
            }

            mDni = mDniEditText.getText().toString();

            if (!mDni.isEmpty()) {
                fillQuotationParameters(Long.valueOf(mDni));
            }else{
                showContent(false);
            }

        } catch (Exception e) {
        }
    }

    private void setupListeners() {
        mQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContent(false);

                mDniEditText.setText("");
                mDni = null;
                captureQRCode();
            }
        });

        mDniSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);

                mDni = mDniEditText.getText().toString();
                if (!mDni.isEmpty()) {
                    fillQuotationParameters(Long.valueOf(mDni));
                }else{
                    showContent(false);
                }
            }
        });
    }


    public boolean isValidSection() {

        if (mQuotation == null || mQuotation.client == null)
            return false;
        else
            return true;
    }

    public AdicionalesOptativosData getAdicionalesOptativos(){
        return mOptativosData;
    }

    private void initializeForm(String location) {

        if (mDni != null && ! mDni.isEmpty()){
            mDniEditText.setText(mDni);
        }

        if (mQuotation == null || mQuotation.client == null)
            return;

        Client client = mQuotation.client;

        StringBuffer txt = new StringBuffer();
        txt.append("Nombre y Apellido:  <b>" + client.getFullName() + "</b><br>");
        txt.append("DNI: " +( client.dni != -1 ? Long.valueOf(client.dni): "" ) + "<br>");
        if (mQuotation.titular != null  && mQuotation.titular.age > 0) {
            txt.append("Edad: " +  mQuotation.titular.age + " años<br>");
        }
        if (location != null){
            txt.append("Localidad: " + location + "<br>");
        }
        txt.append("Fecha de Ingreso: " + (mQuotation.titular != null ?  mQuotation.titular.inputDate : "") +"<br>");


        txt.append("Categoria: " + ((mQuotation.categoria!= null && mQuotation.categoria.id != null ) ?  mQuotation.categoria.title : "" )+ "<br>");
        txt.append("Iva: " +((mQuotation.condicionIva != null && mQuotation.condicionIva.id != null ) ?  mQuotation.condicionIva.title: "" )+ "<br>");


        mTitularTextView.setText(Html.fromHtml(txt.toString()));

        List<QuotedClientData> quotedClientDataList = client.quotedDataList;
        StringBuffer sb = new StringBuffer();
        for (QuotedClientData data : quotedClientDataList){
            //sb.append("<b>ID: " + ((data.id != -1) ? data.id: "") + "</b><br>");
            sb.append("<b>Planes Cotizados</b><br>");
            for (Plan plan : data.planes){
                if ( plan.descripcionPlan != null) {
                    sb.append("&nbsp;&nbsp;" + plan.descripcionPlan + "<br>");
                }
            }

            sb.append("<br><b>Capitas: </b><br>");
            for (Member member : data.integrantes){
                sb.append("<b>&nbsp;&nbsp;" + StringHelper.uppercaseFirstCharacter(member.parentesco.optionName()) + "</b><br>&nbsp;&nbsp;Edad: " + member.age + " años DNI: " + member.dni + "<br><br>");
            }
        }

        mQuotedTextView.setText(Html.fromHtml(sb.toString()));
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public View itemViewContainer = null;
        public TextView itemTitle = null;
        public View lineView = null;

        public ViewHolder(View view) {
            super(view);
            itemViewContainer = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            lineView = view.findViewById(R.id.item_separator);
        }
    }


    private void fillQuotationParameters(final long dni) {

        if (AppController.getInstance().isNetworkAvailable()) {

            progressBar.setVisibility(View.VISIBLE);

            QuotationController.getInstance().quotationParametesForExistentClient(dni, false, new Response.Listener<Quotation>() {
                @Override
                public void onResponse(Quotation quotation) {
                    Log.e(TAG, "quotation parameters  ok .....");
                    progressBar.setVisibility(View.GONE);
                    mQuotation = quotation;

                    if (mQuotation != null &&  mQuotation.client != null && mQuotation.client.firstname != null) {

                        if (mQuotation.client.zip != null) {
                            findLocationByZipCode(mQuotation.client.zip);

                        }else {
                            initializeForm(null);
                            showContent(true);
                        }

                        // get opcionales optativos ready to use if late user selec to add opcionales optativos
                        fillAdicionalesOptativosData();
                    }else{
                        showContent(false);
                        DialogHelper.showMessage(getActivity(), getString(R.string.error_client_empty_data));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);

                    Log.e(TAG,((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_client_empty_data));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
        }
    }

    private void fillAdicionalesOptativosData() {

        if (AppController.getInstance().isNetworkAvailable()) {

            QuotationController.getInstance().getAdicionalesOptativos(mQuotation.segmento, new Response.Listener<AdicionalesOptativosData>() {
                @Override
                public void onResponse(AdicionalesOptativosData adicionalesOptativos) {
                    Log.e(TAG, "optativos ok .....");
                    //dismissProgressDialog();

                    if (adicionalesOptativos != null && !adicionalesOptativos.tipoOpcionList.isEmpty()) {
                        mOptativosData = adicionalesOptativos;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //dismissProgressDialog();
                    Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_quote_optativos), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                }
            });
        } else {
            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
        }
    }

    private String findLocationByZipCode(final String zipCode){

        String location = null;

        String realZipCode = zipCode.substring(0, (zipCode.length()) -3);
        Log.e (TAG, "REAL ZIP CODE :" + realZipCode + "--------------");

        searchZipCode(realZipCode, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {

                if (response != null && response.size() > 0) {
                    ArrayList<QuoteOption> locationDataArray = response;

                    for (QuoteOption option: locationDataArray){
                        Log.e (TAG, "id:" + option.id +  "title " + option.title);

                        if (option.id.equals(zipCode)) {

                            String location = option.title;
                            initializeForm(location);
                            showContent(true);
                            break;
                        }
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        return location;
    }

    private void searchZipCode(String query, Response.Listener<ArrayList<QuoteOption>> listener, Response.ErrorListener errorListener) {
        HRequest request = RestApiServices.createSearchLocationRequest(query,  listener, errorListener);
        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }

    private void showContent(boolean est) {

        mTitularTitleTextView.setVisibility(est ? View.VISIBLE : View.GONE);
        mQuotedTitleTextView.setVisibility(est ? View.VISIBLE : View.GONE);

        mTitularTextView.setVisibility(est ? View.VISIBLE : View.GONE);
        mQuotedTextView.setVisibility(est ? View.VISIBLE : View.GONE);
    }
}
