package ar.com.sancorsalud.asociados.manager;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.sancorsalud.asociados.manager.quote.AccountTypeOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.AddressAtributeOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.AltaTypeOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.BancoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.CategoriasOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.CivilStatusOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.CoberturasOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.CondicionIvaOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.DocTypeOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.FormaIngresoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.FormasCoPagoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.FormasPagoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.NationalityOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.OrientationOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.OsDesregulaOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.OsStateController;
import ar.com.sancorsalud.asociados.manager.quote.ParentescoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.PriorityOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.PromotersOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.SegmentoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.SexoOptionsController;
import ar.com.sancorsalud.asociados.manager.quote.TarjetaOptionsController;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.FileHelper;

/**
 * Created by sergio on 2/3/17.
 */

public class QuoteOptionsController {

    private static final String TAG = "OPTION_CONTR";

    private BancoOptionsController bancoOptionsController;
    private CategoriasOptionsController categoriasOptionsController;
    private CoberturasOptionsController coberturasOptionsController;
    private OsDesregulaOptionsController osDesregulaOptionsController;
    private OsStateController osStateController;
    private CondicionIvaOptionsController condicionIvaOptionsController;
    private FormaIngresoOptionsController formaIngresoOptionsController;
    private FormasPagoOptionsController formasPagoOptionsController;
    private FormasCoPagoOptionsController formasCoPagoOptionsController;
    private ParentescoOptionsController parentescoOptionsController;
    private SegmentoOptionsController segmentoOptionsController;
    private SexoOptionsController sexoOptionsController;
    private CivilStatusOptionsController civilStatusOptionsController;
    private DocTypeOptionsController docTypeOptionsController;
    private NationalityOptionsController nationalityOptionsController;
    private TarjetaOptionsController tarjetaOptionsController;
    private OrientationOptionsController orientationOptionsController;
    private AddressAtributeOptionsController addressOptionsController;
    private AccountTypeOptionsController accountTypeOptionsController;
    private AltaTypeOptionsController altaTypeOptionsController;
    private PriorityOptionsController priorityOptionsController;
    private PromotersOptionsController promotersOptionsController;

    private ArrayList<QuoteOption> mAporteLegal;
    private ArrayList<QuoteOption> mTipoUnificacion;
    private ArrayList<QuoteOption> mAportantesGrupo;


    private ArrayList<QuoteOption> mOSDesreguladoList;
    private ArrayList<QuoteOption> mOSCondicionMonotributoList;

    private static QuoteOptionsController mInstance = new QuoteOptionsController();

    public static synchronized QuoteOptionsController getInstance() {
        return mInstance;
    }

    public QuoteOptionsController() {

        bancoOptionsController = new BancoOptionsController();
        categoriasOptionsController = new CategoriasOptionsController();
        sexoOptionsController = new SexoOptionsController();
        civilStatusOptionsController = new CivilStatusOptionsController();
        docTypeOptionsController = new DocTypeOptionsController();
        nationalityOptionsController = new NationalityOptionsController();
        coberturasOptionsController = new CoberturasOptionsController();
        osDesregulaOptionsController = new OsDesregulaOptionsController();
        osStateController = new OsStateController();
        condicionIvaOptionsController = new CondicionIvaOptionsController();
        formaIngresoOptionsController = new FormaIngresoOptionsController();
        formasPagoOptionsController = new FormasPagoOptionsController();
        formasCoPagoOptionsController = new FormasCoPagoOptionsController();
        parentescoOptionsController = new ParentescoOptionsController();
        segmentoOptionsController = new SegmentoOptionsController();
        tarjetaOptionsController = new TarjetaOptionsController();
        orientationOptionsController = new OrientationOptionsController();
        addressOptionsController = new AddressAtributeOptionsController();
        accountTypeOptionsController = new AccountTypeOptionsController();
        altaTypeOptionsController = new AltaTypeOptionsController();
        priorityOptionsController = new PriorityOptionsController();
        promotersOptionsController = new  PromotersOptionsController();

        try {
            String jsonString = FileHelper.loadJSONFromAsset("options.json");
            JSONObject jsonObj = new JSONObject(jsonString);

            mAporteLegal = new ArrayList<>();
            try {
                mAporteLegal = parseArray(jsonObj.getJSONArray("aporte_legal"));
            } catch (JSONException jEx) {
            }


            mTipoUnificacion = new ArrayList<>();
            try {
                mTipoUnificacion = parseArray(jsonObj.getJSONArray("conyuge_tipo_uni"));
            } catch (JSONException jEx) {
            }

            mAportantesGrupo = new ArrayList<>();
            try {
                mAportantesGrupo = parseArray(jsonObj.getJSONArray("aportantes_grupo"));
            } catch (JSONException jEx) {
            }

            // OS will be resetted and reloaded on Promoter Login SalesmaActivity because there are f(staff= f(promoter))
            mOSDesreguladoList = new ArrayList<>();
            mOSCondicionMonotributoList = new ArrayList<>();

        } catch (Exception je) {
        }
    }

    private ArrayList<QuoteOption> parseArray(JSONArray array) {
        ArrayList<QuoteOption> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject dic = array.optJSONObject(i);
            QuoteOption opt = new QuoteOption();
            opt.title = ParserUtils.optString(dic, "title");
            opt.id = ParserUtils.optString(dic, "id");
            list.add(opt);
        }
        return list;
    }

    public ArrayList<QuoteOption> getCoberturas() {
        return coberturasOptionsController.getItems();
    }


    public ArrayList<QuoteOption> getCategorias() {
        return categoriasOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getCondicionIva() {
        return condicionIvaOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getSegmentos() {
        return segmentoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getFormasIngreso() {
        return formaIngresoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getParentescos() {
        return parentescoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getFormasPago() {
        return formasPagoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getFormasPago(boolean flag) {
        return formasPagoOptionsController.getItems(flag);
    }

    public ArrayList<QuoteOption> getFormasCoPago() {
        return formasCoPagoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getTarjetas() {
        return tarjetaOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getAporteLegal() {
        return mAporteLegal;
    }

    public ArrayList<QuoteOption> getObraSociales() {
        return coberturasOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getOSDesreguladas() {
        return mOSDesreguladoList;
    }

    public ArrayList<QuoteOption> getOSCondMonotrinbuto() {
        return mOSCondicionMonotributoList;
    }

    public ArrayList<QuoteOption> getOSDesreguladas(String type) {
        ArrayList<QuoteOption> filterOptions = new ArrayList<QuoteOption>();
        for (QuoteOption option : getOSDesreguladas()) {
            if (option.extra.equals(type)) {
                filterOptions.add(option);
            }
        }
        return filterOptions;
    }


    public ArrayList<QuoteOption> getOSCondMonotrinbuto(String type) {
        ArrayList<QuoteOption> filterOptions = new ArrayList<QuoteOption>();
        for (QuoteOption option : getOSCondMonotrinbuto()) {
            if (option.extra.equals(type)) {
                filterOptions.add(option);
            }
        }
        return filterOptions;
    }

    public ArrayList<QuoteOption> getOSStates() {
        return osStateController.getItems();
    }

    public ArrayList<QuoteOption> getOSStates(String type) {
        ArrayList<QuoteOption> filterOptions = new ArrayList<QuoteOption>();
        for (QuoteOption option : getOSStates()) {
            if (option.extra.equals(type)) {
                filterOptions.add(option);
            }
        }
        return filterOptions;
    }

    public ArrayList<QuoteOption> getTipoUnificacion() {
        return mTipoUnificacion;
    }

    public ArrayList<QuoteOption> getAportantesGrupo() {
        return mAportantesGrupo;
    }

    public ArrayList<QuoteOption> getBancos() {
        return bancoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getSexos() {
        return sexoOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getCivilStatus() {
        return civilStatusOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getDocTypes() {
        return docTypeOptionsController.getItems();
    }

    public QuoteOption getDocType(String id) {
        for (QuoteOption opt : getDocTypes()) {
            if (opt.id.equalsIgnoreCase(id)) {
                return opt;
            }
        }
        return null;
    }

    public ArrayList<QuoteOption> getNationalities() {
        return nationalityOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getOrientations() {
        return orientationOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getAddressAttributes() {
        return addressOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getAccountTypes() {
        return accountTypeOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getAltaTypes() {
        return altaTypeOptionsController.getItems();
    }


    public ArrayList<QuoteOption> getPriorities() {
        return priorityOptionsController.getItems();
    }

    public ArrayList<QuoteOption> getPromoters() {
        return promotersOptionsController.getItems();
    }

    public QuoteOption getAltaType(String id) {

        for (QuoteOption opt : getAltaTypes()) {
            if (opt.id.equalsIgnoreCase(id)) {
                return opt;

            }
        }
        return null;
    }

    public String getSegmentoName(String id) {
        String name = "";
        for (QuoteOption option : getSegmentos()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getFormaIngresoName(String id) {
        String name = "";
        for (QuoteOption option : getFormasIngreso()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getCategoriaName(String id) {
        String name = "";
        for (QuoteOption option : getCategorias()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getCondicionIvaName(String id) {
        String name = "";
        for (QuoteOption option : getCondicionIva()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getAporteLegalName(String id) {
        String name = "";
        for (QuoteOption option : getAporteLegal()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getParentescoName(String id) {
        String name = "";
        for (QuoteOption option : getParentescos()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getCoberturaName(String id) {
        String name = "";
        for (QuoteOption option : getCoberturas()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getFormaPagoName(String id) {
        String name = "";
        for (QuoteOption option : getFormasPago()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getOSDesreguladaName(String id) {
        String name = "";
        for (QuoteOption option : getOSDesreguladas()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getOSCondMonotributoName(String id) {
        String name = "";
        for (QuoteOption option : getOSCondMonotrinbuto()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public QuoteOption getOSById(String id){
        QuoteOption result = null;
        result = getOSDesreguladaById(id);

        if(result == null) {
            result = getOSCondicionMonotributoById(id);
        }
        return result;
    }

    public QuoteOption getOSDesreguladaById(String id){
        QuoteOption result = null;
        for (QuoteOption option : getOSDesreguladas()) {
            if (option.id.equals(id)) {
                result = option;
                break;
            }
        }
        return result;
    }

    public QuoteOption getOSCondicionMonotributoById(String id){
            QuoteOption result = null;
            for (QuoteOption option : getOSDesreguladas()) {
                if (option.id.equals(id)) {
                    result = option;
                    break;
                }
            }
            return result;
    }

    public QuoteOption cloneQuoteOption(QuoteOption in){
        return new QuoteOption(in.id, in.title, in.extra);
    }

    public String getOSDesregulaType(String id) {
        String osType = "";
        for (QuoteOption option : getOSDesreguladas()) {
            if (option.id.equals(id)) {
                osType = option.extra;
                break;
            }
        }
        return osType;
    }

    public String getOSCondMonotributoType(String id) {
        String osType = "";
        for (QuoteOption option : getOSCondMonotrinbuto()) {
            if (option.id.equals(id)) {
                osType = option.extra;
                break;
            }
        }
        return osType;
    }





    public String getOSStateName(String id) {
        String name = "";
        for (QuoteOption option : getOSStates()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getAportantesMonoGrupoName(String id) {
        String name = "";
        for (QuoteOption option : getAportantesGrupo()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getSexoName(String id) {
        String name = "";
        for (QuoteOption option : getSexos()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getDocTypeName(String id) {
        String name = "";
        for (QuoteOption option : getDocTypes()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getCivilStatusName(String id) {
        String name = "";
        for (QuoteOption option : getCivilStatus()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getNationalityName(String id) {
        String name = "";
        for (QuoteOption option : getNationalities()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getOrientationName(String id) {
        String name = "";
        for (QuoteOption option : getOrientations()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getAddressAtributeName(String id) {
        String name = "";
        for (QuoteOption option : getAddressAttributes()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getTarjetaName(String id) {
        String name = "";
        for (QuoteOption option : getTarjetas()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }

    public String getBancoName(String id) {
        String name = "";
        for (QuoteOption option : getBancos()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public String getAccountTypeName(String id) {
        String name = "";
        for (QuoteOption option : getAccountTypes()) {
            if (option.id.equals(id)) {
                name = option.title;
                break;
            }
        }
        return name;
    }


    public void resetOSList(){
        // Each time Promoter is logged reset this data to delete prvious promter OS data
        mOSDesreguladoList = new ArrayList<>();
        mOSCondicionMonotributoList = new ArrayList<>();
    }

    // This methods lazy load OS for particular salesman
    // This is loaded in early in SalesmanMainActivity
    public void loadOSDesreguladas(){
        HRequest request = RestApiServices.createGetOSDesregulaListRequest(Integer.valueOf(ConstantsUtil.DESREGULADO_SEGMENTO), new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> quoteOptions) {
                mOSDesreguladoList = new ArrayList<>(quoteOptions);
                Log.e(TAG, "Loaded OS List OK ......");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mOSDesreguladoList = new ArrayList<>();
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,true);
    }

    public void loadOsCondicionMonotributo(){
        HRequest request = RestApiServices.createGetOSDesregulaListRequest(Integer.valueOf(ConstantsUtil.MONOTRIBUTO_SEGMENTO), new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> quoteOptions) {
                mOSCondicionMonotributoList = new ArrayList<>(quoteOptions);
                Log.e(TAG, "Loaded OSCondicion Monotributo  List OK ......");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mOSCondicionMonotributoList = new ArrayList<>();
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request,true);

    }
}
