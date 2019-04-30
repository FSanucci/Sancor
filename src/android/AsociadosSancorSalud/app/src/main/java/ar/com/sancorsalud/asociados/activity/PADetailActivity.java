package ar.com.sancorsalud.asociados.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Calendar;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.SpinnerDropDownAdapter;
import ar.com.sancorsalud.asociados.manager.CalendarManager;
import ar.com.sancorsalud.asociados.manager.ProspectiveClientController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.Appointment;
import ar.com.sancorsalud.asociados.model.CloseReasons;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.model.user.UserRole;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.core.ParserUtils;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.PermissionsHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static ar.com.sancorsalud.asociados.utils.ConstantsUtil.VIEW_EDIT_PROFILE_REQUEST_CODE;

public class PADetailActivity extends BaseActivity {

    private static final String TAG = "PA_DETAIL";

    private ProspectiveClient mClient;

    private EditText mFullNameEditText;
    private EditText mDNIEditText;
    private EditText mBirthdayEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mAddressEditText;
    private EditText mZoneEditText;

    private View mDNITextWrapperView;
    private View mBirthdayTextWrapperView;
    private View mEmailTextWrapperView;
    private View mPhoneTextWrapperView;
    private View mAddressTextWrapperView;
    private View mZoneTextWrapperView;

    private TextView mAppointmentDateTextView;
    private TextView mAppointmentAddressTextView;
    private TextView mAppointmentNotesTextView;

    private TextView mQuoteTextView;

    private View mEditEventButton;
    private View mEditProfileButton;

    private View mCloseButton;
    private View mQuoteButton;
    private View mLoadDataButton;
    private View mSubteButton;

    private TextView quoteTxt;
    private ImageView quoteImg;

    private View mContainer;
    private View mContainerEvent;
    private View mContainerAddEvent;
    private View mContainerQuote;

    private ProgressBar mProgressView;
    private ArrayList<CloseReasons> mReasons;
    private CloseReasons mSelectedReason;
    private SpinnerDropDownAdapter mReasonAlertAdapter;

    private Handler mHandler = new Handler();
    private Boolean editedProfile = false;

    private long mCardId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pa_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        mMainContainer = findViewById(R.id.main);

        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(ConstantsUtil.CLIENT_ARG);
            mCardId = mClient.cardId;
            toolbar.setTitle(R.string.title_profile);
            setupToolbar(toolbar,R.string.title_profile);
        }

        mFullNameEditText = (EditText) findViewById(R.id.fullname_input);
        setTypeTextNoSuggestions(mFullNameEditText);

        mDNIEditText = (EditText) findViewById(R.id.dni_input);
        mBirthdayEditText = (EditText) findViewById(R.id.birthday_input);
        mEmailEditText = (EditText) findViewById(R.id.email_input);
        mPhoneEditText = (EditText) findViewById(R.id.phone_input);

        mAddressEditText = (EditText) findViewById(R.id.address_input);
        setTypeTextNoSuggestions(mAddressEditText);

        mZoneEditText = (EditText) findViewById(R.id.zone_input);
        setTypeTextNoSuggestions(mZoneEditText);

        mDNITextWrapperView = findViewById(R.id.dni_wrapper);
        mBirthdayTextWrapperView = findViewById(R.id.birth_day_wrapper);
        mEmailTextWrapperView = findViewById(R.id.email_wrapper);
        mPhoneTextWrapperView = findViewById(R.id.phone_wrapper);
        mAddressTextWrapperView = findViewById(R.id.address_wrapper);
        mZoneTextWrapperView = findViewById(R.id.zone_code_wrapper);

        mAppointmentDateTextView = (TextView) findViewById(R.id.appointment_date);

        mAppointmentAddressTextView = (TextView) findViewById(R.id.appointment_address);
        setTypeTextNoSuggestions(mAppointmentAddressTextView);

        mAppointmentNotesTextView = (TextView) findViewById(R.id.appointment_notes);
        setTypeTextNoSuggestions(mAppointmentNotesTextView);

        mQuoteTextView = (TextView) findViewById(R.id.quote_text);
        setTypeTextNoSuggestions(mQuoteTextView);

        mContainerQuote = findViewById(R.id.quote_card);

        mCloseButton = findViewById(R.id.close_button);
        mQuoteButton = findViewById(R.id.quote_button);
        mSubteButton = findViewById(R.id.subte_button);

        quoteTxt = (TextView)findViewById(R.id.quote_txt);
        quoteImg = (ImageView)findViewById(R.id.quote_img);
        mLoadDataButton = findViewById(R.id.load_data_button);

        if (mClient.isQuoted()){
            showViewQuotationButton();
            enableLoadButton();

        }else{
            showQuotationButton();
            disableLoadButton();
        }

        mEditEventButton = findViewById(R.id.edit_event);
        mEditProfileButton = findViewById(R.id.edit_profile);

        mProgressView = (ProgressBar) findViewById(R.id.progress);
        mContainer = findViewById(R.id.container);
        mContainerEvent = findViewById(R.id.schedule_detail);
        mContainerAddEvent = findViewById(R.id.schedule_add);

        enableActionButtons(false);
        setupListener();

        // load profile to get all complementary data
        loadProfile();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e (TAG, "onResume ......");

        // TODO NEW
        if (Storage.getInstance().hasToReloadPA()){
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionsHelper.REQUEST_WRITE_CALENDAR_PERMISSION || requestCode == PermissionsHelper.REQUEST_READ_CALENDAR_PERMISSION) {
                makeAnAppointment();
            }
        }else{
            PermissionsHelper.getInstance().showPermissionError(this, R.string.permision_error_no_write_calendar);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsUtil.CALENDAR_REQUEST_CODE){
            if(mClient!=null && mClient.appointment!=null && mClient.appointment.scheduleId!=-1) {
                Appointment appointment = CalendarManager.getInstance().getEventDetails(mClient.appointment.scheduleId);

                if(appointment!=null) {
                    appointment.prospectiveClientId = mClient.id;

                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.HOUR, -1);
                    if (appointment.date.before(now.getTime())) {
                        mClient.appointment = null;
                        CalendarManager.getInstance().deleteAppointment(appointment.scheduleId);
                        SnackBarHelper.makeError(mContainer, R.string.add_appointment_error).show();
                    } else {
                        mClient.appointment = appointment;
                        saveProfile();
                        editedProfile = true;
                    }
                }
            }
        }else if (requestCode == VIEW_EDIT_PROFILE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                mClient = (ProspectiveClient) data.getSerializableExtra(ConstantsUtil.RESULT_PA);
                showProfile();
                editedProfile = true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        AppController.getInstance().getRestEngine().cancelPendingRequests();
        showProgress(false);

        if(editedProfile)
            finishActivityWithCode(ConstantsUtil.RESULT_DATA_UPDATED);
        else finish();
    }


    // --- helper methods -----------------------------------------------------------------//

    private void enableLoadButton(){
        mLoadDataButton.setAlpha(1f);
        mLoadDataButton.setEnabled(true);
    }
    private void disableLoadButton(){
        mLoadDataButton.setAlpha(0.5f);
        mLoadDataButton.setEnabled(false);
    }

    private void showQuotationButton(){
        quoteTxt.setText(getResources().getString(R.string.button_quote));
        quoteImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_footer_quote));
    }

    private void showViewQuotationButton(){
        quoteTxt.setText(getResources().getString(R.string.button_to_quote));
        quoteImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_footer_quotation));
    }


    private void showProfile(){

        mFullNameEditText.setText(mClient.firstname + " "+mClient.lastname);

        if(mClient.dni>0) {
            mDNIEditText.setText("" + mClient.dni);
            mDNITextWrapperView.setVisibility(View.VISIBLE);
        }else{
            mDNITextWrapperView.setVisibility(View.GONE);
        }

        if(mClient.birthday!=null) {
            mBirthdayEditText.setText("" + mClient.getBirthday());
            mBirthdayTextWrapperView.setVisibility(View.VISIBLE);
        }else{
            mBirthdayTextWrapperView.setVisibility(View.GONE);
        }

        if(mClient.email!=null && mClient.email.length()>0) {
            mEmailEditText.setText(mClient.email);
            mEmailTextWrapperView.setVisibility(View.VISIBLE);
        }else mEmailTextWrapperView.setVisibility(View.GONE);

        if((mClient.celularNumber==null || mClient.celularNumber.length()==0) && (mClient.phoneNumber==null || mClient.phoneNumber.length()==0))
            mPhoneTextWrapperView.setVisibility(View.GONE);
        else{
            if(mClient.celularNumber!=null && mClient.celularNumber.length()>0 && mClient.phoneNumber!=null && mClient.phoneNumber.length()>0) {
                mPhoneEditText.setText(mClient.celularNumber + " / "+mClient.phoneNumber);
                mPhoneTextWrapperView.setVisibility(View.VISIBLE);
            }else if(mClient.celularNumber!=null && mClient.celularNumber.length()>0){
                mPhoneEditText.setText(mClient.celularNumber);
                mPhoneTextWrapperView.setVisibility(View.VISIBLE);
            }else if(mClient.phoneNumber!=null && mClient.phoneNumber.length()>0){
                mPhoneEditText.setText(mClient.phoneNumber);
                mPhoneTextWrapperView.setVisibility(View.VISIBLE);
            }
        }

        String address = mClient.getAddressString();
        if(address!=null && address.length()>0) {
            mAddressEditText.setText(address);
            mAddressTextWrapperView.setVisibility(View.VISIBLE);
        }else mAddressTextWrapperView.setVisibility(View.GONE);

        if(mClient.zone!=null && mClient.zone.name!=null){
            mZoneEditText.setText(mClient.zone.name);
            mZoneTextWrapperView.setVisibility(View.VISIBLE);
        }else{
            mZoneTextWrapperView.setVisibility(View.GONE);
        }

        // Oculto zona
        mZoneTextWrapperView.setVisibility(View.GONE);


        if(mClient.tempQuote!=null && mClient.tempQuote.trim().length()>0) {
            mQuoteTextView.setText(mClient.tempQuote);
            mContainerQuote.setVisibility(View.VISIBLE);
        }

        if(mClient.appointment!=null){
            mContainerEvent.setVisibility(View.VISIBLE);
            mContainerAddEvent.setVisibility(View.GONE);

            if(mClient.appointment.date!=null) {
                mAppointmentDateTextView.setText(ParserUtils.printableDate(mClient.appointment.date));
                mAppointmentDateTextView.setVisibility(View.VISIBLE);
            }else{
                mAppointmentDateTextView.setVisibility(View.GONE);
            }

            if(mClient.appointment.address!=null && mClient.appointment.address.length()>0) {
                mAppointmentAddressTextView.setText(mClient.appointment.address);
                mAppointmentAddressTextView.setVisibility(View.VISIBLE);
            }else{
                mAppointmentAddressTextView.setVisibility(View.GONE);
            }

            if(mClient.appointment.notes!=null && mClient.appointment.notes.length()>0) {
                mAppointmentNotesTextView.setText(mClient.appointment.notes);
                mAppointmentNotesTextView.setVisibility(View.VISIBLE);
            }else{
                mAppointmentNotesTextView.setVisibility(View.GONE);
            }

        }else{
            mContainerEvent.setVisibility(View.GONE);
            mContainerAddEvent.setVisibility(View.VISIBLE);
        }

        User user = UserController.getInstance().getSignedUser();
        if(user.role == UserRole.ZONE_LEADER){
            mEditProfileButton.setVisibility(View.GONE);
            mContainerEvent.setVisibility(View.GONE);
            mContainerAddEvent.setVisibility(View.GONE);
        }

    }

    private void setupListener(){

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.gotoEditPAActivity(PADetailActivity.this,mClient, false);
            }
        });

        mContainerAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnAppointment();
            }
        });

        mEditEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnAppointment();
            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mReasons==null)
                    retrieveReasons();
                else showReasonsDialog(mReasons);
            }
        });

        mQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClient.isQuoted()) {
                    showQuoteFilterDialog();
                }else{
                    Log.e (TAG, "to Recotizar ....");
                    IntentHelper.goToRecotizacionActivity(PADetailActivity.this, mClient);
                }
            }
        });

        mLoadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "toLoadData.....");

                if (mClient.state != null && mClient.state == ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT) {
                    Storage.getInstance().setCardEditableMode(false);
                }else{
                    Storage.getInstance().setCardEditableMode(true);
                }
                IntentHelper.goToInitLoadActivity(PADetailActivity.this, mClient, null);
            }
        });

        mSubteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.goToSubteGravActivity(PADetailActivity.this,mClient.id);
                //IntentHelper.goToSubteNoGravActivity(PADetailActivity.this,mClient.id);
            }
        });
    }



    private void loadProfile(){

        // Check back navigation
        showProgress(true);
        enableActionButtons(false);

        final long time = System.currentTimeMillis();
        ProspectiveClientController.getInstance().getProspectiveClientProfile(mClient, new Response.Listener<ProspectiveClient>() {
            @Override
            public void onResponse(ProspectiveClient clientResponse) {
                mClient = clientResponse;
                mClient.cardId =  mCardId;

                Log.e(TAG, "loadProfile: clientID: " + mClient.id + "..................");
                Log.e(TAG, "loadProfile: client cardID: " + mCardId +  "..................");

                showProfile();

                long dif = System.currentTimeMillis() - time;
                //Para que llegue a verse el loader:
                if(dif<1000) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            enableActionButtons(true);
                        }
                    }, 1250L);
                }else {
                    showProgress(false);
                    enableActionButtons(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                enableActionButtons(true);
                DialogHelper.showStandardErrorMessage(PADetailActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        });
    }

    private void saveProfile(){
        showProfile();
        showProgress(true);
        ProspectiveClientController.getInstance().updateProspectiveClientAppointment(mClient, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                showProgress(false);
                SnackBarHelper.makeSucessful(mContainer,R.string.response_successful);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PADetailActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        mContainer.setVisibility(show?View.GONE:View.VISIBLE);

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

    private void makeAnAppointment(){
        if (!permHelper.checkPermissionForWriteCalendar(this)) {
            permHelper.requestPermissioForWriteCalendar(this);
            return;
        }

        if(mClient.appointment==null) {
            mClient.appointment = CalendarManager.getInstance().addNewEventToCalendar(mClient);
        }else{
            if(mClient.appointment.scheduleId==-1 || mClient.appointment.scheduleId==0){
                mClient.appointment = CalendarManager.getInstance().addNewEventToCalendar(mClient);
            }else{
                Appointment ap = CalendarManager.getInstance().getEventDetails(mClient.appointment.scheduleId);
                if(ap==null){
                    mClient.appointment = CalendarManager.getInstance().addNewEventToCalendar(mClient);
                }
            }
        }

        IntentHelper.gotoCalendarActivity(this,mClient.appointment.scheduleId);
    }

    private void retrieveReasons(){
        showProgress(true);

        HRequest request = RestApiServices.createGetCloseReasonsRequest(new Response.Listener<ArrayList<CloseReasons>>() {
            @Override
            public void onResponse(ArrayList<CloseReasons> list) {
                mReasons = list;
                showReasonsDialog(mReasons);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PADetailActivity.this,null);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    private void closeProspectiveClient(CloseReasons reason){
        showProgress(true);

        HRequest request = RestApiServices.createCloseProspectiveClientRequest(mClient.id,reason.reasonId,new Response.Listener<Void>() {
            @Override
            public void onResponse(Void list) {
                finishActivityWithCode(ConstantsUtil.RESULT_DATA_UPDATED);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PADetailActivity.this,null);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    private void showReasonsDialog(ArrayList<CloseReasons> list){
        mSelectedReason = null;
        ArrayList<String> reasonsStr = new ArrayList<String>();
        for(CloseReasons r : list){
            reasonsStr.add(r.reasonDescription);
        }

        mReasonAlertAdapter = new SpinnerDropDownAdapter(this, reasonsStr,-1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.close_reason_title))
                .setSingleChoiceItems(mReasonAlertAdapter,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mReasonAlertAdapter.setSelectedIndex(i);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mReasonAlertAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });


        builder.setPositiveButton(
                R.string.option_confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mReasonAlertAdapter.getSelectedIndex()>=0 && mReasonAlertAdapter.getSelectedIndex()<mReasons.size()){
                            mSelectedReason = mReasons.get(mReasonAlertAdapter.getSelectedIndex());
                            if(mSelectedReason!=null)
                                closeProspectiveClient(mSelectedReason);
                        }else{
                            showProgress(false);
                        }
                    }
                });

        builder.setNegativeButton(
                R.string.option_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProgress(false);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showProgress(false);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendQuote(final String quote){
        showProgress(true);

        HRequest request = RestApiServices.createQuoteProspectiveClientRequest(mClient.id,quote,new Response.Listener<Void>() {
            @Override
            public void onResponse(Void list) {
                mQuoteTextView.setText(mClient.tempQuote);
                mContainerQuote.setVisibility(View.VISIBLE);
                mClient.tempQuote = quote;
                finishActivityWithCode(ConstantsUtil.RESULT_DATA_UPDATED);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                DialogHelper.showStandardErrorMessage(PADetailActivity.this,null);
            }
        });

        AppController.getInstance().getRestEngine().addToRequestQueue(request,false);
    }

    private void showQuoteDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText edittext = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(40, 20, 40, 0);
        edittext.setLayoutParams(layoutParams);
        ll.addView(edittext, layoutParams);

        alert.setTitle("Ingrese un plan y una cotización:");
        alert.setView(ll);

        alert.setPositiveButton("Cotizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                 String quote = edittext.getText().toString();
                 sendQuote(quote);
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }


    // Quote Filter box
    private void showQuoteFilterDialog() {
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.activity_quote_filter, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        inflator.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        inflator.setMinimumHeight((int)(displayRectangle.height() * 0.4f));

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(inflator);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();

        LinearLayout mPlanView = (LinearLayout)inflator.findViewById(R.id.plan_salud);
        LinearLayout mAdditionalOpt = (LinearLayout) inflator.findViewById(R.id.additional_opt);
        LinearLayout mManualQuote = (LinearLayout) inflator.findViewById(R.id.manual_quote);
        ImageView closeButton  = (ImageView) inflator.findViewById(R.id.close_button);


        mPlanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.goToQuoteActivity(PADetailActivity.this,mClient);
                dialog.dismiss();
            }
        });

        mAdditionalOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.goToQuoteNewAdicionalesOptativosActivity(PADetailActivity.this,mClient);
                dialog.dismiss();
            }
        });

        mManualQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.goToManualQuoteActivity(PADetailActivity.this,mClient);
                dialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void enableActionButtons(boolean est){
        mCloseButton.setEnabled(est);
        mQuoteButton.setEnabled(est);
        mSubteButton.setEnabled(est);

        if (est && mClient.isQuoted()) {
            mLoadDataButton.setEnabled(true);
        }else{
            mLoadDataButton.setEnabled(false);
        }
    }
}
