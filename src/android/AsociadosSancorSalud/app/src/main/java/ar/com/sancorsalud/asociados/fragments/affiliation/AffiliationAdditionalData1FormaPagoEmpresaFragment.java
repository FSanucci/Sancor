package ar.com.sancorsalud.asociados.fragments.affiliation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.activity.affiliation.AffiliationActivity;
import ar.com.sancorsalud.asociados.adapter.AttachFilesAdapter;
import ar.com.sancorsalud.asociados.adapter.OnItemClickListener;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseFragment;
import ar.com.sancorsalud.asociados.manager.CardController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.model.AttachFile;
import ar.com.sancorsalud.asociados.model.affiliation.IAdditionalData1;
import ar.com.sancorsalud.asociados.model.affiliation.Pago;
import ar.com.sancorsalud.asociados.model.quotation.QuoteOption;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.ImageProvider;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.rest.core.ParserUtils.DATE_FORMAT;

//TODO poner validacion seleccion copago obligatorio  si lo tiene que renderizar, y ver tema seleccion TC si anda
// TODO Revizar cuil viene de los datos de la persona pero no es oblig  si no viaja va vacio y salta el error en el serv espera cuil_titular
// TODO ver tema deteccion TC i vulve bien a este fragm
public class AffiliationAdditionalData1FormaPagoEmpresaFragment extends BaseFragment implements IAffiliationAdditionalData1Fragment {

    private static final String TAG = "AF_PAGO_EMP_FRG";

    private static final String CREDIT_CARD_FILE_PREFIX = "creditCard";
    private static final String CONSTANCIA_CARD_FILE_PREFIX = "constanciaCard";
    private static final String CONSTANCIA_CBU_FILE_PREFIX = "constanciaCBU";
    private static final String COMPROBANTE_CBU_FILE_PREFIX = "comprobanteCBU";

    protected static final String ARG_ADD_DATA_1 = "additionalData1";
    protected static final String ARG_TITULAR_DNI = "titularDNI";
    protected static final String ARG_CARD_ID = "cardId";
    protected static final String ARG_TITULAR_CUIL = "titularCuil";
    protected static final String ARG_EMPRESA = "empresa";
    protected static final String ARG_FECHA_INI_SERV = "fechaIniServ";

    private ScrollView mScrollView;

    private EditText mFormaPagoEditText;
    private EditText mNroEmpresaEditText;
    private TextView mEmpresaLeyendText;

    //COPAGOS
    private RelativeLayout mCopagosBox;
    private EditText mFormaCoPagoEditText;
    private SpinnerDropDownAdapter mFormasCoPagoAlertAdapter;
    private int mSelectedFormasCoPago = -1;
    private ArrayList<QuoteOption> mFormasCoPago;


    // COPAGO CREDIT CARD
    private View mCardContainerBox;
    private EditText mCardNumberEditText;
    private TextInputLayout mCardTextInputLayout;

    private View mCardTypeBox;
    private TextInputLayout mCardTypeInputLayout;
    private EditText mCardTypeEditText;
    private SpinnerDropDownAdapter mCardTypeAlertAdapter;
    private int mSelectedCardType = -1;
    private ArrayList<QuoteOption> mCardTypes;

    private RelativeLayout mCardDateValidityBox;
    private EditText mCardDateValidityEditText;
    private TextInputLayout mCardDateValidityTextInputLayout;
    private Button mCardCheckValidationButton;

    private LinearLayout mCardAffilliationBox;
    private RadioButton mSiAffiliationCardRadioButton;
    private RadioButton mNoAffiliationCardRadioButton;

    // BANK
    private View mBancoContainerBox;
    private EditText mBancoEditText;
    private int mSelectedBanco = -1;
    private SpinnerDropDownAdapter mBancosAlertAdapter;
    private ArrayList<QuoteOption> mBancos;

    // REINTEGROS number
    private RelativeLayout mCBUContainer;
    private EditText mCBUEditText;
    //private TextInputLayout mCBUTextInputLayout;

    // CBU BANK
    private View mCBUBancoContainerBox;
    private EditText mCBUBancoEditText;
    private SpinnerDropDownAdapter mCBUBancosAlertAdapter;
    private int mCBUSelectedBanco = -1;

    // ACCOUNT
    private RelativeLayout mAccountTypeBox;
    private TextInputLayout mAccountTypeTextInputLayout;
    private EditText mAccountTypeEditText;
    private SpinnerDropDownAdapter mAccountTypeAlertAdapter;
    private int mSelectedAccountType = -1;
    private ArrayList<QuoteOption> mAccountTypes;

    // CBU COPAGOS
    private RelativeLayout mCopagoContainer;
    private EditText mCopagoCBUEditText;
    //private TextInputLayout mCopagoCBUTextInputLayout;
    private View mCopagoCBUBancoContainerBox;
    private EditText mCopagoCBUBancoEditText;
    private int mCopagoCBUSelectedBanco = -1;
    private SpinnerDropDownAdapter mCopagoCBUBancosAlertAdapter;
    private RelativeLayout mCopagoAccountTypeBox;
    private TextInputLayout mCopagoAccountTypeTextInputLayout;
    private SpinnerDropDownAdapter mCopagoAccountTypeAlertAdapter;
    private EditText mCopagoAccountTypeEditText;
    private int mCopagoSelectedAccountType = -1;

    // CBU AFFILIATION QUEST
    private LinearLayout mCBUAffilliationBox;
    private RadioButton mSiAffiliationCBURadioButton;
    private RadioButton mNoAffiliationCBURadioButton;

    // CBU PERSONAL DATA
    private EditText mCUILEditText;
    private TextInputLayout mCUILTextInputLayout;
    private EditText mFirstNameEditText;
    private TextInputLayout mFirstNameTextInputLayout;
    private EditText mLastNameEditText;
    private TextInputLayout mLastNameTextInputLayout;

    // CREDIT CARD  Files
    private RecyclerView mCreditCardRecyclerView;
    private AttachFilesAdapter mCreditCardFileAdapter;
    private Button addCreditCardButton;
    private Button detectCreditCardButton;

    // CONSTANCIA CARD  Files
    private RelativeLayout mConstanciaCardBox;
    private RecyclerView mConstanciaCardRecyclerView;
    private AttachFilesAdapter mConstanciaFileAdapter;
    private Button addConstanciaCardButton;

    //CBU  COMPROBANTES Files
    private RelativeLayout mCBUComprobanteBox;
    private RecyclerView mCBUComprobanteRecyclerView;
    private AttachFilesAdapter mCBUComprobanteFileAdapter;
    private Button addCBUComprobanteButton;

    // CBU  REINTEGROS Files
    private RelativeLayout mCBUConstanciaBox;
    private RecyclerView mCBUConstanciaRecyclerView;
    private AttachFilesAdapter mCBUConstanciaFileAdapter;
    private Button addCBUConstanciaButton;


    public List<AttachFile> tmpCreditCardFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpConstanciaCardFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpConstanciaCBUFiles = new ArrayList<AttachFile>();
    public List<AttachFile> tmpComprobanteCBUFiles = new ArrayList<AttachFile>();

    protected long cardId;
    protected long titularDNI;
    protected QuoteOption mEmpresa;
    protected String titularCuil;
    protected String mFechaIniServ;

    protected IAdditionalData1 additionalData1;
    private SimpleDateFormat mDateFormat;

    protected View view;
    private boolean editableCard = true;
    private View mProgressView;

    //COPAGO LEYEND
    private RadioButton mAffiliationCompanyRadioButton;
    private RadioButton mAffiliationAssociatedRadioButton;
    private QuoteOption formaCopago = new QuoteOption();

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String out = new StringBuilder().append(year).append("-").append(String.format("%02d", monthOfYear + 1)).append("-").append(String.format("%02d", dayOfMonth)).toString();
            Log.e(TAG, "Validity Date: " + out);

            Date today = new Date();
            try {
                Date date = mDateFormat.parse(out);
                if (date.before(today)) {
                    mCardDateValidityEditText.setText(mDateFormat.format(today));
                } else {
                    mCardDateValidityEditText.setText(mDateFormat.format(date));
                    checkShowValidityCardAlert(date);
                }
            } catch (Exception e) {
                mCardDateValidityEditText.setText(mDateFormat.format(today));
            }
            mCardDateValidityEditText.requestFocus();
        }
    };


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.e(TAG, "onViewCreated....");
        attachFilesSubDir = titularDNI + "/affiliation/pago";
        mImageProvider = new ImageProvider(getActivity());
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);

        mFormaPagoEditText = (EditText) mMainContainer.findViewById(R.id.pago_input);
        setTypeTextNoSuggestions(mFormaPagoEditText);

        mNroEmpresaEditText = (EditText) mMainContainer.findViewById(R.id.emp_input);
        mEmpresaLeyendText = (TextView) mMainContainer.findViewById(R.id.emp_leyend);

        // COPAGOS
        mCopagosBox = (RelativeLayout) mMainContainer.findViewById(R.id.copago_box);
        mFormaCoPagoEditText = (EditText) mMainContainer.findViewById(R.id.copago_input);

        // CREDIT CARD
        mCardContainerBox = mMainContainer.findViewById(R.id.card_box);
        mCardTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.card_number_wrapper);

        mCardTypeBox = mMainContainer.findViewById(R.id.card_type_box);
        mCardTypeInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.card_type_wrapper);
        mCardTypeEditText = (EditText) mMainContainer.findViewById(R.id.card_type_input);
        setTypeTextNoSuggestions(mCardTypeEditText);

        mCardNumberEditText = (EditText) mMainContainer.findViewById(R.id.card_number_input);
        mConstanciaCardBox = (RelativeLayout) mMainContainer.findViewById(R.id.constancia_box);

        // BANK
        mBancoContainerBox = mMainContainer.findViewById(R.id.banco_container);
        mBancoEditText = (EditText) mMainContainer.findViewById(R.id.banco_input);
        setTypeTextNoSuggestions(mBancoEditText);

        mCardDateValidityBox = (RelativeLayout) mMainContainer.findViewById(R.id.card_validity_date_box);
        mCardDateValidityEditText = (EditText) mMainContainer.findViewById(R.id.card_validity_date_input);
        mCardDateValidityTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.card_validity_date_wrapper);
        mCardCheckValidationButton = (Button) mMainContainer.findViewById(R.id.card_check_validation_button);

        mCardAffilliationBox = (LinearLayout) mMainContainer.findViewById(R.id.card_affilliation_box);
        mSiAffiliationCardRadioButton = (RadioButton) mMainContainer.findViewById(R.id.card_yes_affiliation_button);
        mNoAffiliationCardRadioButton = (RadioButton) mMainContainer.findViewById(R.id.card_no_affiliation_button);

        // CBU or REINTEGROS
        mCBUContainer = (RelativeLayout) mMainContainer.findViewById(R.id.cbu_number_container);
        mCBUEditText = (EditText) mMainContainer.findViewById(R.id.cbu_number_input);
        //mCBUTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.cbu_number_wrapper);

        mCBUComprobanteBox = (RelativeLayout) mMainContainer.findViewById(R.id.cbu_comprobante_box);
        mCBUConstanciaBox = (RelativeLayout) mMainContainer.findViewById(R.id.cbu_constancia_box); // reintegros

        // CBU Copagos
        mCopagoContainer = (RelativeLayout) mMainContainer.findViewById(R.id.copago_cbu_container);
        //mCopagoCBUTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.copago_cbu_number_wrapper);
        mCopagoCBUEditText = (EditText) mMainContainer.findViewById(R.id.copago_cbu_number_input);
        mCopagoCBUBancoContainerBox = mMainContainer.findViewById(R.id.copago_cbu_banco_container);
        mCopagoCBUBancoEditText = (EditText) mMainContainer.findViewById(R.id.copago_cbu_banco_input);
        mCopagoAccountTypeBox = (RelativeLayout) mMainContainer.findViewById(R.id.copago_account_type_box);
        mCopagoAccountTypeEditText = (EditText) mMainContainer.findViewById(R.id.copago_account_input);
        setTypeTextNoSuggestions(mCopagoAccountTypeEditText);
        mCopagoAccountTypeTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.copago_account_wrapper);

        // CBU BANK
        mCBUBancoContainerBox = mMainContainer.findViewById(R.id.cbu_banco_container);
        mCBUBancoEditText = (EditText) mMainContainer.findViewById(R.id.cbu_banco_input);
        setTypeTextNoSuggestions(mCBUBancoEditText);
        mCBUConstanciaBox = (RelativeLayout) mMainContainer.findViewById(R.id.cbu_constancia_box);

        // ACCOUNT
        mAccountTypeBox = (RelativeLayout) mMainContainer.findViewById(R.id.account_type_box);
        mAccountTypeEditText = (EditText) mMainContainer.findViewById(R.id.account_input);
        setTypeTextNoSuggestions(mAccountTypeEditText);
        mAccountTypeTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.account_wrapper);

        // CBU AFFILIATION QUEST
        mCBUAffilliationBox = (LinearLayout) mMainContainer.findViewById(R.id.cbu_affilliation_box);
        mSiAffiliationCBURadioButton = (RadioButton) mMainContainer.findViewById(R.id.cbu_yes_affiliation_button);
        mNoAffiliationCBURadioButton = (RadioButton) mMainContainer.findViewById(R.id.cbu_no_affiliation_button);

        // CBU PERSONAL DATA
        mCUILEditText = (EditText) mMainContainer.findViewById(R.id.cuit_number_input);
        setTypeTextNoSuggestions(mCUILEditText);
        mCUILTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.cuit_number_wrapper);

        mFirstNameEditText = (EditText) mMainContainer.findViewById(R.id.first_name_input);
        setTypeTextNoSuggestions(mFirstNameEditText);
        mFirstNameTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.first_name_wrapper);

        mLastNameEditText = (EditText) mMainContainer.findViewById(R.id.last_name_input);
        setTypeTextNoSuggestions(mLastNameEditText);
        mLastNameTextInputLayout = (TextInputLayout) mMainContainer.findViewById(R.id.last_name_wrapper);

        mScrollView = (ScrollView) mMainContainer.findViewById(R.id.scroll);
        mProgressView = (ProgressBar) mMainContainer.findViewById(R.id.progress);
        this.view = view;

        //COPAGO LEYEND
        mAffiliationCompanyRadioButton = (RadioButton)  mMainContainer.findViewById(R.id.affiliation_company_button);
        mAffiliationCompanyRadioButton.setClickable(false);
        mAffiliationAssociatedRadioButton = (RadioButton)  mMainContainer.findViewById(R.id.affiliation_associated_button);
        mAffiliationAssociatedRadioButton.setClickable(false);

        editableCard = Storage.getInstance().isCardEditableMode();
        Log.e(TAG, "editableCard: " + editableCard);

        fillArraysData();
        fillAllPhysicalFiles();
    }

    // --- TEMPLATE implementations ------------------------------- //
    @Override
    public void updateCreditCardNumber(String creditCardNumber, String expireDate) {
        Log.e(TAG, "creditCardNumber: " + creditCardNumber != null ? creditCardNumber : "");
        Log.e(TAG, "expireDate: " + expireDate != null ? expireDate : "");

        if (creditCardNumber != null) {
            mCardNumberEditText.setText(creditCardNumber);
        }
        if (expireDate != null) {
            mCardDateValidityEditText.setText(expireDate);
        }
    }

    @Override
    public void updateFileList(final AttachFile attachFile) {
        if (attachFileType.equals(CREDIT_CARD_FILE_PREFIX)) {
            mCreditCardFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CONSTANCIA_CARD_FILE_PREFIX)) {
            mConstanciaFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(CONSTANCIA_CBU_FILE_PREFIX)) {
            mCBUConstanciaFileAdapter.addItem(attachFile);
        } else if (attachFileType.equals(COMPROBANTE_CBU_FILE_PREFIX)) {
            mCBUComprobanteFileAdapter.addItem(attachFile);
        }
    }

    @Override
    public void onRemovedFile(int position) {
        try {
            if (attachFileType.equals(CREDIT_CARD_FILE_PREFIX)) {
                mCreditCardFileAdapter.removeItem(position);
            } else if (attachFileType.equals(CONSTANCIA_CARD_FILE_PREFIX)) {
                mConstanciaFileAdapter.removeItem(position);
            } else if (attachFileType.equals(CONSTANCIA_CBU_FILE_PREFIX)) {
                mCBUConstanciaFileAdapter.removeItem(position);
            } else if (attachFileType.equals(COMPROBANTE_CBU_FILE_PREFIX)) {
                mCBUComprobanteFileAdapter.removeItem(position);
            }
        } catch (Throwable e) {
        }
    }

    // TODO put validate mandory copago selection
    public IAdditionalData1 getAdditionalData1() {
        Log.e(TAG, "----- getAdditionalData1 -------");

        //additionalData1.empresaId = mEmpresa.id;

        Pago pago = additionalData1.getPago();
        //  forma pago is read only comes form quotation data, here is fixed to EF
        pago.cardId = cardId;
        pago.cbu = mCBUEditText.getText().toString().trim();
/*
        if (mSelectedFormasCoPago != -1) { // if user select one formaCopago

            // TODO: si se deja descomentado va a mandar el tipo de pago seleccionado (CBU o TC), si se comenta manda fijo "EF" (debería ser lo segundo)
            //pago.formaPago = mFormasCoPago.get(mSelectedFormasCoPago);
            pago.formaCopago = formaCopago;
            pago.typeCopago = additionalData1.getCopagos();

            //pago.type = "C"; // COPAGO
            pago.type = "S";
            if (pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                if (mSelectedCardType != -1) {
                    pago.cardType = mCardTypes.get(mSelectedCardType);
                }
                pago.cardPagoNumber = mCardNumberEditText.getText().toString().trim();
                if (mSelectedBanco != -1) {
                    pago.bankEmiter = mBancos.get(mSelectedBanco);
                }

                try {
                    pago.validityDate = ParserUtils.parseDate(mCardDateValidityEditText.getText().toString(), DATE_FORMAT);
                } catch (Exception e) {
                    pago.validityDate = null;
                }

                if (mSiAffiliationCardRadioButton.isChecked()) {
                    pago.titularCardAsAffiliation = true;
                } else {
                    pago.titularCardAsAffiliation = false;
                }
            }

            if (pago.formaCopago.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                pago.cbuCopago = mCopagoCBUEditText.getText().toString();
                if (mCopagoCBUSelectedBanco != -1){
                    pago.bankCBUCopago = mBancos.get(mCopagoCBUSelectedBanco);
                }
                if (mCopagoSelectedAccountType != -1){
                    pago.accountTypeCopago = mAccountTypes.get(mCopagoSelectedAccountType);
                }
            }
        } else{
            mFormaPagoEditText.setText(additionalData1.getPago().formaPago.title);
            pago.type = "S"; // SALUD
        }
*/
        if ( mSelectedFormasCoPago != -1) { //If User Select One FormaCopago
            pago.formaCopago = formaCopago;
            pago.cardIdCopago = cardId;
            pago.hasCopagos = true;

            if (pago.formaCopago.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                pago.cardTypeCopago = null;
                pago.bankEmiterCopago = null;
                pago.validityDateCopago = null;

                pago.cbuCopago = mCopagoCBUEditText.getText().toString();
                if (mCopagoCBUSelectedBanco != -1){
                    pago.bankCBUCopago = mBancos.get(mCopagoCBUSelectedBanco);
                }
                if (mCopagoSelectedAccountType != -1){
                    pago.accountTypeCopago = mAccountTypes.get(mCopagoSelectedAccountType);
                }
            }

            if (pago.formaCopago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                pago.cbuCopago = null;
                pago.bankCBUCopago = null;
                pago.accountTypeCopago = null;

                if (mSelectedCardType != -1) {
                    pago.cardTypeCopago = mCardTypes.get(mSelectedCardType);
                }
                pago.cardPagoNumberCopago = mCardNumberEditText.getText().toString().trim();
                if (mSelectedBanco != -1) {
                    pago.bankEmiterCopago = mBancos.get(mSelectedBanco);
                }

                try {
                    pago.validityDateCopago = ParserUtils.parseDate(mCardDateValidityEditText.getText().toString(), DATE_FORMAT);
                } catch (Exception e) {
                    pago.validityDateCopago = null;
                }
            }
        }

        pago.type = "S";
        if (pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
            if (mSelectedCardType != -1) {
                pago.cardType = mCardTypes.get(mSelectedCardType);
            }
            pago.cardPagoNumber = mCardNumberEditText.getText().toString().trim();
            if (mSelectedBanco != -1) {
                pago.bankEmiter = mBancos.get(mSelectedBanco);
            }

            try {
                pago.validityDate = ParserUtils.parseDate(mCardDateValidityEditText.getText().toString(), DATE_FORMAT);
            } catch (Exception e) {
                pago.validityDate = null;
            }

            if (mSiAffiliationCardRadioButton.isChecked()) {
                pago.titularCardAsAffiliation = true;
            } else {
                pago.titularCardAsAffiliation = false;
            }
        }

        if (pago.formaPago.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)){
            pago.cbu = mCBUEditText.getText().toString();
            if (mCBUSelectedBanco != -1){
                pago.bankCBU = mBancos.get(mCBUSelectedBanco);
            }

            if (mSelectedAccountType != -1){
                pago.accountType = mAccountTypes.get(mSelectedAccountType);
            }
        }

        if (mCBUSelectedBanco != -1) {
            pago.bankCBU = mBancos.get(mCBUSelectedBanco);
        }

        if (mSelectedAccountType != -1) {
            pago.accountType = mAccountTypes.get(mSelectedAccountType);
        }

        if (mSiAffiliationCBURadioButton.isChecked()) {
            pago.titularMainCbuAsAffiliation = true;
            pago.accountCuil = titularCuil;
            pago.accountFirstName = null;
            pago.accountLastName =null;
        } else {
            pago.titularMainCbuAsAffiliation = false;
            pago.accountCuil = mCUILEditText.getText().toString().trim();
            pago.accountFirstName = mFirstNameEditText.getText().toString().trim();
            pago.accountLastName = mLastNameEditText.getText().toString().trim();
        }

        // UPDATE FILES
        if (mCBUConstanciaFileAdapter != null) {
            additionalData1.getPago().constanciaCBUFiles = mCBUConstanciaFileAdapter.getAttachFiles();
        }

        // UPDATE FILES
        if (mCreditCardFileAdapter != null) {
            additionalData1.getPago().creditCardFiles = mCreditCardFileAdapter.getAttachFiles();
        }
        if (mConstanciaFileAdapter != null) {
            additionalData1.getPago().constanciaCardFiles = mConstanciaFileAdapter.getAttachFiles();
        }
        if (mCBUConstanciaFileAdapter != null) {
            additionalData1.getPago().constanciaCBUFiles = mCBUConstanciaFileAdapter.getAttachFiles();
        }
        if (mCBUComprobanteFileAdapter != null) {
            additionalData1.getPago().comprobanteCBUFiles = mCBUComprobanteFileAdapter.getAttachFiles();
        }

        return additionalData1;
    }

    @Override
    public boolean isValidSection() {
        return validateForm();
    }

    private void fillArraysData() {

        mFormasCoPago = new ArrayList<>();
        QuoteOption copagoSelection = new QuoteOption("-1", getResources().getString(R.string.field_copagos));
        mFormasCoPago.add(copagoSelection);
        mFormasCoPago.addAll(QuoteOptionsController.getInstance().getFormasCoPago());

        mBancos = new ArrayList<>();
        QuoteOption bancoSelection = new QuoteOption("-1", getResources().getString(R.string.field_bank));
        mBancos.add(bancoSelection);
        mBancos.addAll(QuoteOptionsController.getInstance().getBancos());

        mAccountTypes = new ArrayList<>();
        QuoteOption accountYTypeSelection = new QuoteOption("-1", getResources().getString(R.string.field_account_type));
        mAccountTypes.add(accountYTypeSelection);
        mAccountTypes.addAll(QuoteOptionsController.getInstance().getAccountTypes());

        mCardTypes = new ArrayList<>();
        QuoteOption cardSelection = new QuoteOption("-1", getResources().getString(R.string.field_card_type));
        mCardTypes.add(cardSelection);
        mCardTypes.addAll(QuoteOptionsController.getInstance().getTarjetas());
    }

    private void fillAllPhysicalFiles() {
        ((AffiliationActivity) getActivity()).setActionButtons(false);

        showProgressDialog(R.string.affiliation_file_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "fillAll AddData1 Data PhysicalFiles ....");
                //fillConstanciaCBUFiles();
                fillCreditCardFiles();
            }
        }).start();
    }

    private void fillCreditCardFiles() {

        if (!additionalData1.getPago().creditCardFiles.isEmpty()) {

            Log.e(TAG, "fillcreditCardFiles !!....");

            final AttachFile creditCardFile = additionalData1.getPago().creditCardFiles.remove(0);
            if (creditCardFile.fileNameAndExtension == null || creditCardFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + CREDIT_CARD_FILE_PREFIX, creditCardFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        if (resultFile != null) {
                            Log.e(TAG, "ok getting CreditCard File !!!!");
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpCreditCardFiles.add(resultFile);
                            fillCreditCardFiles();

                        } else {
                            Log.e(TAG, "Null CreditCard file ....");
                            tmpCreditCardFiles.add(creditCardFile);
                            fillCreditCardFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpCreditCardFiles.add(creditCardFile);
                        fillCreditCardFiles();
                    }
                });
            } else {
                tmpCreditCardFiles.add(creditCardFile);
                fillCreditCardFiles();
            }

        } else {
            additionalData1.getPago().creditCardFiles.addAll(tmpCreditCardFiles);
            tmpCreditCardFiles = new ArrayList<AttachFile>();
            fillConstanciaCardFiles();
        }
    }

    private void fillConstanciaCardFiles() {

        if (!additionalData1.getPago().constanciaCardFiles.isEmpty()) {

            Log.e(TAG, "fillconstanciaCardFiles !!....");

            final AttachFile constanciaCardFile = additionalData1.getPago().constanciaCardFiles.remove(0);
            if (constanciaCardFile.fileNameAndExtension == null || constanciaCardFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + CONSTANCIA_CARD_FILE_PREFIX, constanciaCardFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        Log.e(TAG, "ok getting constanciaCardFile File !!!!");

                        if (resultFile != null) {
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpConstanciaCardFiles.add(resultFile);
                            fillConstanciaCardFiles();

                        } else {
                            Log.e(TAG, "Null constanciaCard file ....");
                            tmpConstanciaCardFiles.add(constanciaCardFile);
                            fillConstanciaCardFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpConstanciaCardFiles.add(constanciaCardFile);
                        fillConstanciaCardFiles();
                    }
                });
            } else {
                tmpConstanciaCardFiles.add(constanciaCardFile);
                fillConstanciaCardFiles();
            }

        } else {
            additionalData1.getPago().constanciaCardFiles.addAll(tmpConstanciaCardFiles);
            tmpConstanciaCardFiles = new ArrayList<AttachFile>();
            fillComprobanteCbuFiles();
        }
    }

    private void fillComprobanteCbuFiles() {

        if (!additionalData1.getPago().comprobanteCBUFiles.isEmpty()) {

            Log.e(TAG, "fillComprobantesCBUFiles !!....");

            final AttachFile comprobanteCBUFile = additionalData1.getPago().comprobanteCBUFiles.remove(0);
            if (comprobanteCBUFile.fileNameAndExtension == null || comprobanteCBUFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + COMPROBANTE_CBU_FILE_PREFIX, comprobanteCBUFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        Log.e(TAG, "ok getting comprobanteCBUFile File !!!!");

                        if (resultFile != null) {
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpComprobanteCBUFiles.add(resultFile);
                            fillComprobanteCbuFiles();

                        } else {
                            Log.e(TAG, "Null constanciaCard file ....");
                            tmpComprobanteCBUFiles.add(comprobanteCBUFile);
                            fillComprobanteCbuFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpComprobanteCBUFiles.add(comprobanteCBUFile);
                        fillComprobanteCbuFiles();
                    }
                });
            } else {
                tmpComprobanteCBUFiles.add(comprobanteCBUFile);
                fillComprobanteCbuFiles();
            }

        } else {
            additionalData1.getPago().comprobanteCBUFiles.addAll(tmpComprobanteCBUFiles);
            tmpComprobanteCBUFiles = new ArrayList<AttachFile>();
            fillConstanciaCBUFiles();
        }
    }

    private void fillConstanciaCBUFiles() {

        if (!additionalData1.getPago().constanciaCBUFiles.isEmpty()) {

            Log.e(TAG, "fillconstanciaCbuFiles !!....");

            final AttachFile constanciaCBUFile = additionalData1.getPago().constanciaCBUFiles.remove(0);
            if (constanciaCBUFile.fileNameAndExtension == null || constanciaCBUFile.fileNameAndExtension.isEmpty()) {

                CardController.getInstance().getFile(getActivity().getApplicationContext(), attachFilesSubDir + "/" + CONSTANCIA_CBU_FILE_PREFIX, constanciaCBUFile.id, new Response.Listener<AttachFile>() {
                    @Override
                    public void onResponse(AttachFile resultFile) {

                        Log.e(TAG, "ok getting constanciaCBUFile File !!!!");

                        if (resultFile != null) {
                            Log.e(TAG, "nameAndExtendion:  " + resultFile.fileNameAndExtension);
                            Log.e(TAG, "path:  " + resultFile.filePath);

                            tmpConstanciaCBUFiles.add(resultFile);
                            fillConstanciaCBUFiles();

                        } else {
                            Log.e(TAG, "Null constanciaCbuFile file ....");
                            tmpConstanciaCBUFiles.add(constanciaCBUFile);
                            fillConstanciaCBUFiles();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        tmpConstanciaCBUFiles.add(constanciaCBUFile);
                        fillConstanciaCBUFiles();
                    }
                });
            } else {
                tmpConstanciaCBUFiles.add(constanciaCBUFile);
                fillConstanciaCBUFiles();
            }

        } else {
            additionalData1.getPago().constanciaCBUFiles.addAll(tmpConstanciaCBUFiles);
            tmpConstanciaCBUFiles = new ArrayList<AttachFile>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    showEmpresaLeyend();
                }
            });
        }
    }

    private void showEmpresaLeyend() {
        // cancel previous search
        AppController.getInstance().getRestEngine().cancelPendingRequests();
        HRequest request = RestApiServices.createGetSearchEmpresaRequest(mEmpresa.id, new Response.Listener<ArrayList<QuoteOption>>() {
            @Override
            public void onResponse(ArrayList<QuoteOption> response) {
                mEmpresaLeyendText.setText(response.get(0).extra2.toString().trim());
                initialize();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initialize();
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request);
    }


    private void initialize() {
        initializeForm();
        setupListeners();
        ((AffiliationActivity) getActivity()).setActionButtons(true);
    }


    // --- helper methods ---------------------------------------------------- //

    protected void initializeForm() {
        mScrollView.smoothScrollTo(0, 0);

        initCreditCardFile();
        initConstanciaCardFile();
        initConstanciaCBUFile();
        initComprobanteCBUFile();

        // PAGO
        Pago pago = additionalData1.getPago();
        pago.cardId = cardId;
        String copagos = additionalData1.getCopagos(); // comes from Ficha

        formaCopago = pago.formaCopago;
/*
        if (copagos != null && copagos.equals(ConstantsUtil.COPAGO_ASOCIADO)) {

            Log.e(TAG, "Show combo  COPAGOS: " + copagos);

            mCopagosBox.setVisibility(View.VISIBLE);

            mSelectedFormasCoPago = mFormasCoPago.indexOf(pago.formaPago); // forma pago = TC or CBU
            if (mSelectedFormasCoPago != -1) {
                mFormaCoPagoEditText.setText(pago.formaPago.title);
                QuoteOption opt = mFormasCoPago.get(mSelectedFormasCoPago);

                if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                    showCrediCardData();

                    if (pago.cardType != null) {
                        mSelectedCardType = mCardTypes.indexOf(pago.cardType);
                        if (mSelectedCardType != -1)
                            mCardTypeEditText.setText(pago.cardType.title);
                    }

                    if (pago.cardPagoNumber != null && !pago.cardPagoNumber.isEmpty())
                        mCardNumberEditText.setText(pago.cardPagoNumber);

                    if (pago.bankEmiter != null) {
                        mSelectedBanco = mBancos.indexOf(pago.bankEmiter);
                        if (mSelectedBanco != -1)
                            mBancoEditText.setText(pago.bankEmiter.title);
                    }

                    if (pago.validityDate != null) {
                        mCardDateValidityEditText.setText(pago.getValidityDate());
                    }

                    if (pago.titularCardAsAffiliation) {
                        mSiAffiliationCardRadioButton.setChecked(true);
                        mNoAffiliationCardRadioButton.setChecked(false);
                        mConstanciaCardBox.setVisibility(View.GONE);
                    } else {
                        mSiAffiliationCardRadioButton.setChecked(false);
                        mNoAffiliationCardRadioButton.setChecked(true);
                        mConstanciaCardBox.setVisibility(View.VISIBLE);
                    }

                }else {
                    showCBUData();
                }
            }else{
                // mut not happen always must have a forma copago id available
                showOthersData();
            }

         }else{
            mCopagosBox.setVisibility(View.GONE);
            showOthersData();
        }
*/
        Log.e(TAG, "Show combo  COPAGOS: " + copagos);

        if ((copagos == null) || (copagos.isEmpty()) || (copagos.equals(ConstantsUtil.AFFILIACION_COPAGO_EMPRESA))){

            mAffiliationCompanyRadioButton.setChecked(true);
            mAffiliationAssociatedRadioButton.setChecked(false);

            mCopagosBox.setVisibility(View.GONE);
            showOthersData();
        } else if (copagos.equals(ConstantsUtil.AFFILIACION_COPAGO_ASOCIADO)){

            mAffiliationCompanyRadioButton.setChecked(false);
            mAffiliationAssociatedRadioButton.setChecked(true);

            mCopagosBox.setVisibility(View.VISIBLE);

            mSelectedFormasCoPago = mFormasCoPago.indexOf(pago.formaCopago); // forma pago = TC or CBU
            if (mSelectedFormasCoPago != -1) {
                mFormaCoPagoEditText.setText(pago.formaCopago.title);
                QuoteOption opt = mFormasCoPago.get(mSelectedFormasCoPago);

                if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                    showCrediCardData();

                    if (pago.cardTypeCopago != null) {
                        mSelectedCardType = mCardTypes.indexOf(pago.cardTypeCopago);
                        if (mSelectedCardType != -1)
                            mCardTypeEditText.setText(pago.cardTypeCopago.title);
                    }

                    if (pago.cardPagoNumberCopago != null && !pago.cardPagoNumberCopago.isEmpty())
                        mCardNumberEditText.setText(pago.cardPagoNumberCopago);

                    if (pago.bankEmiterCopago != null) {
                        mSelectedBanco = mBancos.indexOf(pago.bankEmiterCopago);
                        if (mSelectedBanco != -1)
                            mBancoEditText.setText(pago.bankEmiterCopago.title);
                    }

                    if (pago.validityDateCopago != null) {
                        mCardDateValidityEditText.setText(pago.getValidityDateCopago());
                    }
/*
                    if (pago.titularCardAsAffiliation) {
                        mSiAffiliationCardRadioButton.setChecked(true);
                        mNoAffiliationCardRadioButton.setChecked(false);
                        mConstanciaCardBox.setVisibility(View.GONE);
                    } else {
                        mSiAffiliationCardRadioButton.setChecked(false);
                        mNoAffiliationCardRadioButton.setChecked(true);
                        mConstanciaCardBox.setVisibility(View.VISIBLE);
                    }
*/
                }else {
                    showCBUData();

                    if (pago.cbuCopago != null && !pago.cbuCopago.isEmpty()) {
                        mCopagoCBUEditText.setText(pago.cbuCopago);
                    }

                    if (pago.bankCBUCopago != null) {
                        mCopagoCBUSelectedBanco = mBancos.indexOf(pago.bankCBUCopago);
                        if (mCopagoCBUSelectedBanco != -1)
                            mCopagoCBUBancoEditText.setText(pago.bankCBUCopago.title);
                    }

                    if (pago.accountTypeCopago != null) {
                        mCopagoSelectedAccountType = mAccountTypes.indexOf(pago.accountTypeCopago);
                        if (mCopagoSelectedAccountType != -1)
                            mCopagoAccountTypeEditText.setText(pago.accountTypeCopago.title);
                    }
                }
            }else{
                // mut not happen always must have a forma copago id available
                showOthersData();
            }
        } else {
            mAffiliationCompanyRadioButton.setChecked(false);
            mAffiliationAssociatedRadioButton.setChecked(false);

            mCopagosBox.setVisibility(View.GONE);
            DialogHelper.showCopagosErrorMessage(getActivity(), copagos, null);
        }

        QuoteOption optTc = pago.formaPago;
        if (optTc.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
            showCrediCardData();

            if (pago.cardType != null) {
                mSelectedCardType = mCardTypes.indexOf(pago.cardType);
                if (mSelectedCardType != -1)
                    mCardTypeEditText.setText(pago.cardType.title);
            }

            if (pago.cardPagoNumber != null && !pago.cardPagoNumber.isEmpty())
                mCardNumberEditText.setText(pago.cardPagoNumber);

            if (pago.bankEmiter != null) {
                mSelectedBanco = mBancos.indexOf(pago.bankEmiter);
                if (mSelectedBanco != -1)
                    mBancoEditText.setText(pago.bankEmiter.title);
            }

            if (pago.validityDate != null) {
                mCardDateValidityEditText.setText(pago.getValidityDate());
            }

            if (pago.titularCardAsAffiliation) {
                mSiAffiliationCardRadioButton.setChecked(true);
                mNoAffiliationCardRadioButton.setChecked(false);
                mConstanciaCardBox.setVisibility(View.GONE);
            } else {
                mSiAffiliationCardRadioButton.setChecked(false);
                mNoAffiliationCardRadioButton.setChecked(true);
                mConstanciaCardBox.setVisibility(View.VISIBLE);
            }
        }

        // ALL FORMA COPAGOS or null COPAGOS
        //mFormaPagoEditText.setText(additionalData1.getPago().formaPago.title);

        QuoteOption formaPagoInfo = additionalData1.getPago().formaPago;
        if (formaPagoInfo.title != null && !formaPagoInfo.title.isEmpty()){
            mFormaPagoEditText.setText(formaPagoInfo.title);
        } else {
            mFormaPagoEditText.setText(formaPagoInfo.id);
        }

        mNroEmpresaEditText.setText(mEmpresa.id);

        if (pago.cbu != null && !pago.cbu.isEmpty()) {
            mCBUEditText.setText(pago.cbu);
        }

        if (pago.bankCBU != null) {
            mCBUSelectedBanco = mBancos.indexOf(pago.bankCBU);
            if (mCBUSelectedBanco != -1)
                mCBUBancoEditText.setText(pago.bankCBU.title);
        }

        if (pago.accountType != null) {
            mSelectedAccountType = mAccountTypes.indexOf(pago.accountType);
            if (mSelectedAccountType != -1)
                mAccountTypeEditText.setText(pago.accountType.title);
        }


        if (pago.titularMainCbuAsAffiliation) {
            hideAccountPersonalData();
            mSiAffiliationCBURadioButton.setChecked(true);
            mNoAffiliationCBURadioButton.setChecked(false);
        } else {

            mCUILEditText.setText(pago.accountCuil);
            mFirstNameEditText.setText(pago.accountFirstName);
            mLastNameEditText.setText(pago.accountLastName);
            showAccountPersonalData();

            mSiAffiliationCBURadioButton.setChecked(false);
            mNoAffiliationCBURadioButton.setChecked(true);
        }

        checkEditableCardMode();
    }

    private void initCreditCardFile() {

        mCreditCardFileAdapter = new AttachFilesAdapter(additionalData1.getPago().creditCardFiles, true);

        mCreditCardRecyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCreditCardRecyclerView.getContext());
        mCreditCardRecyclerView.setLayoutManager(attachLayoutManager);
        mCreditCardRecyclerView.setAdapter(mCreditCardFileAdapter);
        mCreditCardRecyclerView.setHasFixedSize(true);

        addCreditCardButton = (Button) view.findViewById(R.id.card_button);
        detectCreditCardButton = (Button) view.findViewById(R.id.card_detect_button);
    }

    private void initConstanciaCardFile() {

        mConstanciaFileAdapter = new AttachFilesAdapter(additionalData1.getPago().constanciaCardFiles, true);

        mConstanciaCardRecyclerView = (RecyclerView) view.findViewById(R.id.constancia_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mConstanciaCardRecyclerView.getContext());
        mConstanciaCardRecyclerView.setLayoutManager(attachLayoutManager);
        mConstanciaCardRecyclerView.setAdapter(mConstanciaFileAdapter);
        mConstanciaCardRecyclerView.setHasFixedSize(true);
        addConstanciaCardButton = (Button) view.findViewById(R.id.constancia_button);
    }

    private void initConstanciaCBUFile() {

        mCBUConstanciaFileAdapter = new AttachFilesAdapter(additionalData1.getPago().constanciaCBUFiles, true);

        mCBUConstanciaRecyclerView = (RecyclerView) view.findViewById(R.id.cbu_constancia_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCBUConstanciaRecyclerView.getContext());
        mCBUConstanciaRecyclerView.setLayoutManager(attachLayoutManager);
        mCBUConstanciaRecyclerView.setAdapter(mCBUConstanciaFileAdapter);
        mCBUConstanciaRecyclerView.setHasFixedSize(true);
        addCBUConstanciaButton = (Button) view.findViewById(R.id.cbu_constancia_button);
    }

    private void initComprobanteCBUFile() {

        mCBUComprobanteFileAdapter = new AttachFilesAdapter(additionalData1.getPago().comprobanteCBUFiles, true);

        mCBUComprobanteRecyclerView = (RecyclerView) view.findViewById(R.id.cbu_comprobante_recycler_view);
        LinearLayoutManager attachLayoutManager = new LinearLayoutManager(mCBUComprobanteRecyclerView.getContext());
        mCBUComprobanteRecyclerView.setLayoutManager(attachLayoutManager);
        mCBUComprobanteRecyclerView.setAdapter(mCBUComprobanteFileAdapter);
        mCBUComprobanteRecyclerView.setHasFixedSize(true);
        addCBUComprobanteButton = (Button) view.findViewById(R.id.cbu_comprobante_button);
    }

    protected void checkEditableCardMode() {
        mNroEmpresaEditText.setFocusable(editableCard);

        mCardNumberEditText.setFocusable(editableCard);
        mCBUEditText.setFocusable(editableCard);

        mSiAffiliationCardRadioButton.setEnabled(editableCard);
        mNoAffiliationCardRadioButton.setEnabled(editableCard);

        mSiAffiliationCBURadioButton.setEnabled(editableCard);
        mNoAffiliationCBURadioButton.setEnabled(editableCard);

        mCUILEditText.setFocusable(editableCard);
        mFirstNameEditText.setFocusable(editableCard);
        mLastNameEditText.setFocusable(editableCard);

        if (!editableCard) {
            disableView(mCardCheckValidationButton);
            disableView(addCreditCardButton);
            disableView(detectCreditCardButton);
            disableView(addConstanciaCardButton);
            disableView(addCBUConstanciaButton);
        }
    }

    protected void setupListeners() {
        setupImageProvider();

        if (editableCard) {

            View formaCoPagoButton = mMainContainer.findViewById(R.id.copago_button);
            formaCoPagoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showFormasCoPagoAlert();
                }
            });
            formaCoPagoButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedFormasCoPago = -1;
                    mFormaCoPagoEditText.setText("");
                    mFormaPagoEditText.setText(additionalData1.getPago().formaPago.title);
                    showOthersData();
                    return true;
                }
            });

            // COPAGO TC
            View cardTypeButton = mMainContainer.findViewById(R.id.card_type_button);
            cardTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCardTypesAlert();
                }
            });

            cardTypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedCardType = -1;
                    mCardTypeEditText.setText("");
                    return true;
                }
            });


            View bancoButton = mMainContainer.findViewById(R.id.banco_button);
            bancoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showBancosAlert();
                }
            });
            bancoButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedBanco = -1;
                    mBancoEditText.setText("");
                    return true;
                }
            });

            mCardCheckValidationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Validating card...");

                    if (validateCreditCard()) {

                        String fechaCardVencimiento = mCardDateValidityEditText.getText().toString();
                        QuoteOption cardType = mCardTypes.get(mSelectedCardType);

                        Log.e(TAG, "sFechaInicioServicio:" + mFechaIniServ);
                        Log.e(TAG, "sFechaVencim:" + fechaCardVencimiento);
                        Log.e(TAG, "sCardType id:" + cardType.id);

                        if (AppController.getInstance().isNetworkAvailable()) {
                            CardController.getInstance().verifyCreditCard(Long.valueOf(cardType.id), fechaCardVencimiento, mFechaIniServ, new Response.Listener<Boolean>() {
                                @Override
                                public void onResponse(Boolean response) {
                                    if (response != null) {
                                        if (response) {
                                            Log.e(TAG, "verifyCard  ok .....");
                                            SnackBarHelper.makeSucessful(mMainContainer, R.string.affiliation_valid_credit_card).show();
                                        } else {
                                            DialogHelper.showMessage(getActivity(), getResources().getString(R.string.affiliation_invalid_credit_card));
                                        }
                                    } else {
                                        showProgress(false);
                                        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_credit_card_verification));
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    showProgress(false);
                                    Log.e(TAG, "Error verifying credit card: " + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                    DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_credit_card_verification), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                                }
                            });
                        } else {
                            showProgress(false);
                            DialogHelper.showNoInternetErrorMessage(getActivity(), null);
                        }
                    }
                }
            });

            View cbuBancoButton = mMainContainer.findViewById(R.id.cbu_banco_button);
            cbuBancoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCbuBancosAlert();
                }
            });

            cbuBancoButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCBUSelectedBanco = -1;
                    mCBUBancoEditText.setText("");
                    return true;
                }
            });

            View copagoCbuBancoButton = mMainContainer.findViewById(R.id.copago_cbu_banco_button);
            copagoCbuBancoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCopagoCbuBancosAlert();
                }
            });

            copagoCbuBancoButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCopagoCBUSelectedBanco = -1;
                    mCopagoCBUBancoEditText.setText("");
                    return true;
                }
            });


            View accountTypeButton = mMainContainer.findViewById(R.id.account_button);
            accountTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showAccountTypeAlert();
                }
            });
            accountTypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSelectedAccountType = -1;
                    mAccountTypeEditText.setText("");
                    return true;
                }
            });

            View copagoAccountTypeButton = mMainContainer.findViewById(R.id.copago_account_button);
            copagoAccountTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCopagoAccountTypeAlert();
                }
            });
            copagoAccountTypeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCopagoSelectedAccountType = -1;
                    mCopagoAccountTypeEditText.setText("");
                    return true;
                }
            });

            // COPAGO CREDIT CARD
            View cardValidityButton = mMainContainer.findViewById(R.id.card_validity_button);
            cardValidityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(v);
                    showCalendar(mCardDateValidityEditText.getText().toString().trim(), dateSetListener);
                }
            });
            cardValidityButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCardDateValidityEditText.setText("");
                    return true;
                }
            });

            mSiAffiliationCardRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConstanciaCardBox.setVisibility(View.GONE);
                    mConstanciaFileAdapter.removeAllItems();
                }
            });

            mNoAffiliationCardRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConstanciaCardBox.setVisibility(View.VISIBLE);
                }
            });


            // CBU
            mSiAffiliationCBURadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCBUConstanciaBox.setVisibility(View.GONE);
                    //mCBUConstanciaFileAdapter.removeAllItems();
                    hideAccountPersonalData();

                }
            });

            mNoAffiliationCBURadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCBUConstanciaBox.setVisibility(View.VISIBLE);
                    showAccountPersonalData();

                }
            });


            /*
            addCBUConstanciaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CONSTANCIA_CBU_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CONSTANCIA_CBU_FILE_PREFIX, CONSTANCIA_CBU_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });
            */

            // FILE BUTTONS
            addCreditCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CREDIT_CARD_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CREDIT_CARD_FILE_PREFIX, CREDIT_CARD_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            detectCreditCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageProvider.detectCreditCardNumber();
                }
            });

            addConstanciaCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CONSTANCIA_CARD_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CONSTANCIA_CARD_FILE_PREFIX, CONSTANCIA_CARD_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addCBUConstanciaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachFileType = CONSTANCIA_CBU_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + CONSTANCIA_CBU_FILE_PREFIX, CONSTANCIA_CBU_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });

            addCBUComprobanteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachFileType = COMPROBANTE_CBU_FILE_PREFIX;
                    mImageProvider.showImagePicker(attachFilesSubDir + "/" + COMPROBANTE_CBU_FILE_PREFIX, COMPROBANTE_CBU_FILE_PREFIX + FileHelper.getFilePrefix());
                }
            });


            // ADAPTERS
            mCreditCardFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "creditCardFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CREDIT_CARD_FILE_PREFIX;
                    AttachFile creditCardFile = additionalData1.getPago().creditCardFiles.get(position);
                    if (creditCardFile.id != -1) {
                        removeFile(creditCardFile, position);
                    }
                }
            });

            mConstanciaFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "constanciaCardFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CONSTANCIA_CARD_FILE_PREFIX;
                    AttachFile constanciaCardFile = additionalData1.getPago().constanciaCardFiles.get(position);
                    if (constanciaCardFile.id != -1) {
                        removeFile(constanciaCardFile, position);
                    }
                }
            });

            mCBUConstanciaFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "constanciaCBUFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = CONSTANCIA_CBU_FILE_PREFIX;
                    AttachFile constanciaCBUFile = additionalData1.getPago().constanciaCBUFiles.get(position);
                    if (constanciaCBUFile.id != -1) {
                        removeFile(constanciaCBUFile, position);
                    }
                }
            });

            mCBUComprobanteFileAdapter.setOnItemDeleteClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e(TAG, "comprobanteCBUFile onItemDeleteClick()!!!!!:" + position);
                    attachFileType = COMPROBANTE_CBU_FILE_PREFIX;
                    AttachFile comprobanteCBUFile = additionalData1.getPago().comprobanteCBUFiles.get(position);
                    if (comprobanteCBUFile.id != -1) {
                        removeFile(comprobanteCBUFile, position);
                    }
                }
            });

        }

        // ADAPTERS
        mCreditCardFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile creditCardFile = additionalData1.getPago().creditCardFiles.get(position);
                Log.e(TAG, "creditCardFile path:" + creditCardFile.filePath);
                loadFile(creditCardFile);
            }
        });

        mConstanciaFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile constanciaCardFile = additionalData1.getPago().constanciaCardFiles.get(position);
                Log.e(TAG, "constanciaCardFile path:" + constanciaCardFile.filePath);
                loadFile(constanciaCardFile);
            }
        });

        mCBUConstanciaFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile constanciaCBUFile = additionalData1.getPago().constanciaCBUFiles.get(position);
                Log.e(TAG, "constanciaCBUFile path:" + constanciaCBUFile.filePath);
                loadFile(constanciaCBUFile);
            }
        });

        mCBUComprobanteFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AttachFile comporbanteCBUFile = additionalData1.getPago().comprobanteCBUFiles.get(position);
                Log.e(TAG, "comprobanteCBUFile path:" + comporbanteCBUFile.filePath);
                loadFile(comporbanteCBUFile);
            }
        });

    }

    private boolean validateField(EditText editText, int error, int inputId) {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(inputId);
        if (editText.getText().length() == 0) {
            input.setErrorEnabled(true);
            input.setError(getString(error));
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);

        return true;
    }

    private boolean validateCBUCopago() {
        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(R.id.copago_cbu_number_wrapper);
        if (mCopagoCBUEditText.getText().length() != 22) {
            input.setErrorEnabled(true);
            input.setError(getString(R.string.cbu_lenght_error));
            mCopagoCBUEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else input.setErrorEnabled(false);
        return true;
    }

    private boolean validateCreditCardCopago() {
        boolean validForm = true;

        TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(R.id.card_number_wrapper);
        if (mCardNumberEditText.getText().length() < 15 || mCardNumberEditText.getText().length() > 16){
            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_card_number_error));
            mCardNumberEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            validForm = false;
        } else input.setErrorEnabled(false);

        input = (TextInputLayout) mMainContainer.findViewById(R.id.card_type_wrapper);
        if (mCardTypeEditText.getText().toString().isEmpty()){
            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_tarjeta));
            mCardTypeEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            validForm = false;
        } else input.setErrorEnabled(false);

        input = (TextInputLayout) mMainContainer.findViewById(R.id.banco_wrapper);
        if (mBancoEditText.getText().toString().isEmpty()){
            input.setErrorEnabled(true);
            input.setError(getString(R.string.seleccione_banco_error));
            mBancoEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
            validForm = false;
        } else input.setErrorEnabled(false);

        return validForm;
    }

    private boolean validateReintegros() {
            TextInputLayout input = (TextInputLayout) mMainContainer.findViewById(R.id.cbu_number_wrapper);
            if (!mCBUEditText.getText().toString().isEmpty() && mCBUEditText.getText().length() != 22) {
                //mCBUTextInputLayout.setVisibility(View.VISIBLE);
                input.setErrorEnabled(true);
                input.setError(getString(R.string.reintegros_lenght_error));
                mCBUEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
                return false;
            } else
                input.setErrorEnabled(false);
            return true;
    }

    private boolean validateCreditCard() {
        boolean isValid = true;

        //isValid = isValid & validateField(mBancoEditText, R.string.seleccione_banco_error, R.id.banco_wrapper);
        isValid = isValid & validateField(mCardTypeEditText, R.string.seleccione_tarjeta, R.id.card_type_wrapper);
        isValid = isValid & validateField(mCardDateValidityEditText, R.string.seleccione_validity_date_error, R.id.card_validity_date_wrapper);
        return isValid;
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (mFormaPagoEditText.getText().toString().equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)){
            isValid = isValid & validateCreditCard();
        } else {
            isValid = isValid & validateReintegros();
        }

        if (mSelectedFormasCoPago != -1) {
            QuoteOption opt = mFormasCoPago.get(mSelectedFormasCoPago);

            if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                //isValid = isValid & validateField(mCardNumberEditText, R.string.seleccione_card_number_error, R.id.card_number_wrapper);
                //isValid = isValid & validateField(mBancoEditText, R.string.seleccione_banco_error, R.id.banco_wrapper);
                //isValid = isValid & validateField(mCardDateValidityEditText, R.string.seleccione_validity_date_error, R.id.card_validity_date_wrapper);
                //isValid = isValid & validateReintegros();
                isValid = isValid & validateCreditCardCopago();

            } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                //isValid = isValid & validateField(mCBUEditText, R.string.seleccione_cbu_error, R.id.cbu_number_wrapper);
                isValid = isValid & validateCBUCopago();
            }
        }

        return isValid;
    }



    private void showFormasCoPagoAlert() {

        ArrayList<String> pagoStr = new ArrayList<String>();
        for (QuoteOption q : mFormasCoPago) {
            pagoStr.add(q.optionName());
        }

        mFormasCoPagoAlertAdapter = new SpinnerDropDownAdapter(getActivity(), pagoStr, mSelectedFormasCoPago);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mFormasCoPagoAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedFormasCoPago = i;
                if (i == 0) {
                    mSelectedFormasCoPago = -1;
                    mFormaPagoEditText.setText(additionalData1.getPago().formaPago.title);
                    mFormaCoPagoEditText.setText("");
                    showOthersData();

                } else {

                    QuoteOption opt = mFormasCoPago.get(mSelectedFormasCoPago);
                    formaCopago.title = opt.title;
                    formaCopago.id = opt.id;
                    formaCopago.extra = opt.extra;
                    formaCopago.extra2 = opt.extra2;
                    additionalData1.getPago().formaCopago = formaCopago;

                    if (opt.id.equalsIgnoreCase(ConstantsUtil.TARJETA_CREDITO_FORMA_PAGO)) {
                        //mFormaPagoEditText.setText(opt.title);
                        showCrediCardData();
                    } else if (opt.id.equalsIgnoreCase(ConstantsUtil.CBU_FORMA_PAGO)) {
                        //mFormaPagoEditText.setText(opt.title);
                        showCBUData();

                    } else {
                        //mFormaPagoEditText.setText(additionalData1.getPago().formaPago.title);
                        showOthersData();
                    }

                    mFormaCoPagoEditText.setText(mFormasCoPago.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mFormasCoPagoAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void showBancosAlert() {

        ArrayList<String> bankStr = new ArrayList<String>();
        for (QuoteOption q : mBancos) {
            bankStr.add(q.optionName());
        }

        mBancosAlertAdapter = new SpinnerDropDownAdapter(getActivity(), bankStr, mSelectedBanco);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mBancosAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedBanco = i;

                if (i == 0) {
                    mSelectedBanco = -1;
                    mBancoEditText.setText("");
                } else {
                    mBancoEditText.setText(mBancos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mBancosAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void showCbuBancosAlert() {
        ArrayList<String> arrayStr = new ArrayList<String>();

        for (QuoteOption q : mBancos) {
            arrayStr.add(q.optionName());
        }

        mCBUBancosAlertAdapter = new SpinnerDropDownAdapter(getActivity(), arrayStr, mCBUSelectedBanco);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCBUBancosAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCBUSelectedBanco = i;

                if (i == 0) {
                    mCBUSelectedBanco = -1;
                    mCBUBancoEditText.setText("");
                } else {
                    mCBUBancoEditText.setText(mBancos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCBUBancosAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCopagoCbuBancosAlert() {
        ArrayList<String> arrayStr = new ArrayList<String>();

        for (QuoteOption q : mBancos) {
            arrayStr.add(q.optionName());
        }

        mCopagoCBUBancosAlertAdapter = new SpinnerDropDownAdapter(getActivity(), arrayStr, mCopagoCBUSelectedBanco);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCopagoCBUBancosAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCopagoCBUSelectedBanco = i;

                if (i == 0) {
                    mCopagoCBUSelectedBanco = -1;
                    mCopagoCBUBancoEditText.setText("");
                } else {
                    mCopagoCBUBancoEditText.setText(mBancos.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCopagoCBUBancosAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAccountTypeAlert() {
        ArrayList<String> arrayStr = new ArrayList<String>();
        for (QuoteOption q : mAccountTypes) {
            arrayStr.add(q.optionName());
        }

        mAccountTypeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), arrayStr, mSelectedAccountType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mAccountTypeAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedAccountType = i;

                if (i == 0) {
                    mSelectedAccountType = -1;
                    mAccountTypeEditText.setText("");
                } else {
                    QuoteOption opt = mAccountTypes.get(mSelectedAccountType);
                    mAccountTypeEditText.setText(opt.optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mAccountTypeAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCopagoAccountTypeAlert() {
        ArrayList<String> arrayStr = new ArrayList<String>();
        for (QuoteOption q : mAccountTypes) {
            arrayStr.add(q.optionName());
        }

        mCopagoAccountTypeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), arrayStr, mCopagoSelectedAccountType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCopagoAccountTypeAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCopagoSelectedAccountType = i;

                if (i == 0) {
                    mCopagoSelectedAccountType = -1;
                    mCopagoAccountTypeEditText.setText("");
                } else {
                    QuoteOption opt = mAccountTypes.get(mCopagoSelectedAccountType);
                    mCopagoAccountTypeEditText.setText(opt.optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCopagoAccountTypeAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCardTypesAlert() {
        ArrayList<String> cardTypeStr = new ArrayList<String>();

        for (QuoteOption q : mCardTypes) {
            cardTypeStr.add(q.optionName());
        }

        mCardTypeAlertAdapter = new SpinnerDropDownAdapter(getActivity(), cardTypeStr, mSelectedCardType);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setAdapter(mCardTypeAlertAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedCardType = i;
                if (i == 0) {
                    mSelectedCardType = -1;
                    mCardTypeEditText.setText("");
                } else {
                    mCardTypeEditText.setText(mCardTypes.get(i).optionName());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mCardTypeAlertAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCrediCardData() {

        mCopagoContainer.setVisibility(View.GONE);

        mCardContainerBox.setVisibility(View.VISIBLE);
        mCardTextInputLayout.setVisibility(View.VISIBLE);
        mCardTextInputLayout.setErrorEnabled(false);
        mCardTypeBox.setVisibility(View.VISIBLE);

        mBancoContainerBox.setVisibility(View.VISIBLE);

        mCardDateValidityBox.setVisibility(View.VISIBLE);
        mCardDateValidityTextInputLayout.setErrorEnabled(false);
        mCardCheckValidationButton.setVisibility(View.VISIBLE);

        mCardAffilliationBox.setVisibility(View.VISIBLE);
        hideCreditCardConstanciaBox();
        hideAccountPersonalData();

        // Change hint to REINTEGROS
        mCBUEditText.setHint(R.string.affiliation_reintegro_number);
        //mCBUTextInputLayout.setVisibility(View.VISIBLE);
        //mCBUTextInputLayout.setErrorEnabled(false);
        mCBUContainer.setVisibility(View.VISIBLE);


        mCBUBancoContainerBox.setVisibility(View.VISIBLE);

        mAccountTypeBox.setVisibility(View.VISIBLE);
        mAccountTypeTextInputLayout.setErrorEnabled(false);

        mCBUAffilliationBox.setVisibility(View.VISIBLE);

        mCBUComprobanteBox.setVisibility(View.GONE);
        mCBUConstanciaBox.setVisibility(View.VISIBLE); // reintegros
    }

    private void showCBUData() {

        mCopagoContainer.setVisibility(View.VISIBLE);

        mCardContainerBox.setVisibility(View.GONE);
        mCardTextInputLayout.setVisibility(View.GONE);
        mCardTypeBox.setVisibility(View.GONE);

        mBancoContainerBox.setVisibility(View.GONE);
        mCardDateValidityBox.setVisibility(View.GONE);
        mCardCheckValidationButton.setVisibility(View.GONE);

        mCardAffilliationBox.setVisibility(View.GONE);
        hideCreditCardConstanciaBox();
        hideAccountPersonalData();

        // Change hint to CBU
        mCBUEditText.setHint(R.string.affiliation_cbu_number);
        //mCBUTextInputLayout.setVisibility(View.VISIBLE);
        //mCBUTextInputLayout.setErrorEnabled(false);
        mCBUContainer.setVisibility(View.VISIBLE);


        mCBUBancoContainerBox.setVisibility(View.VISIBLE);

        mAccountTypeBox.setVisibility(View.VISIBLE);
        mAccountTypeTextInputLayout.setVisibility(View.VISIBLE);
        mAccountTypeTextInputLayout.setErrorEnabled(false);

        mCBUAffilliationBox.setVisibility(View.VISIBLE);

        mCBUComprobanteBox.setVisibility(View.VISIBLE); // reintegros
        mCBUConstanciaBox.setVisibility(View.VISIBLE);
    }



    private void showOthersData() {

        mCopagoContainer.setVisibility(View.GONE);

        mCardContainerBox.setVisibility(View.GONE);
        mCardTextInputLayout.setVisibility(View.GONE);
        mCardTypeBox.setVisibility(View.GONE);
        mBancoContainerBox.setVisibility(View.GONE);

        mCardDateValidityBox.setVisibility(View.GONE);
        mCardCheckValidationButton.setVisibility(View.GONE);

        mCardAffilliationBox.setVisibility(View.GONE);
        hideCreditCardConstanciaBox();
        hideAccountPersonalData();

        // Change hint to REINTEGROS
        mCBUEditText.setHint(R.string.affiliation_reintegro_number);
        //mCBUTextInputLayout.setVisibility(View.VISIBLE);
        //mCBUTextInputLayout.setErrorEnabled(false);
        mCBUContainer.setVisibility(View.VISIBLE);

        mCBUBancoContainerBox.setVisibility(View.VISIBLE);

        mAccountTypeBox.setVisibility(View.VISIBLE);
        mAccountTypeTextInputLayout.setErrorEnabled(false);

        mCBUAffilliationBox.setVisibility(View.VISIBLE);

        mCBUComprobanteBox.setVisibility(View.GONE);
        mCBUConstanciaBox.setVisibility(View.VISIBLE); // reintegros
    }

    private void hideCreditCardConstanciaBox() {
        mConstanciaCardBox.setVisibility(View.GONE);

        mSiAffiliationCardRadioButton.setChecked(true);
        mNoAffiliationCardRadioButton.setChecked(false);

        mSiAffiliationCBURadioButton.setChecked(true);
        mNoAffiliationCBURadioButton.setChecked(false);

        mConstanciaFileAdapter.removeAllItems();
    }

    private void hideAccountPersonalData() {
        mCUILTextInputLayout.setVisibility(View.GONE);
        mFirstNameTextInputLayout.setVisibility(View.GONE);
        mLastNameTextInputLayout.setVisibility(View.GONE);
    }

    private void showAccountPersonalData() {
        mCUILTextInputLayout.setVisibility(View.VISIBLE);
        mFirstNameTextInputLayout.setVisibility(View.VISIBLE);
        mLastNameTextInputLayout.setVisibility(View.VISIBLE);
    }

    private void showCalendar(String data, DatePickerDialog.OnDateSetListener listener) {
        Date today = new Date();
        Date date = null;
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

        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay);
        dateDialog.getDatePicker().setMinDate(today.getTime());
        dateDialog.show();
    }

    private void checkShowValidityCardAlert(Date inputDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, 3);

        Date limit = c.getTime();
        if (inputDate.before(limit)) {
            ((BaseActivity) getActivity()).showMessage(getActivity().getResources().getString(R.string.affiliation_card_validity_mssg));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


}
