package ar.com.sancorsalud.asociados.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.manager.ZoneController;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.model.user.User;
import ar.com.sancorsalud.asociados.model.user.UserRole;
import ar.com.sancorsalud.asociados.model.Zone;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.Storage;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "LOGIN_ACT";

    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    private View mLoginFormView;
    private View mLogoView;
    private View mSmallLogoView;
    private View mStaffLogoView;

    private View mLoginButton;
    private Interpolator mInterpolator;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mMainContainer = findViewById(R.id.all_data);

        mInterpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);

        mLogoView = findViewById(R.id.logo);
        mSmallLogoView = findViewById(R.id.small_logo);
        mStaffLogoView = findViewById(R.id.staff_logo);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLoginButton = findViewById(R.id.login_button);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                attemptLogin();
            }
        });


        // --- TODO JUST FOR TEST --------- //

        // test
        mEmailView.setText("mbenciveng");
        mPasswordView.setText("prueba");

        //test staff
        //mEmailView.setText("dgerardi");
        //mPasswordView.setText("12345");

        //prod
        //mEmailView.setText("pmanfrino");
        //mPasswordView.setText("PAOLA");

        // prod promoter
        //mEmailView.setText("mbenciveng");
        //mPasswordView.setText("0303456");

        // test promotion support
        //mEmailView.setText("fpaz");
        //mPasswordView.setText("prueba");

        //mEmailView.setText("mfrodrigue");
        //mPasswordView.setText("prueba");

        //mEmailView.setText("advera");
        //mPasswordView.setText("Sanc0r");

        //mEmailView.setText("fsalera");
        //mPasswordView.setText("Sanc0r");

        // test zone leader
        //mEmailView.setText("RGUEVARA");
        //mPasswordView.setText("romina10");

        // test / prod zone Leader
        //mEmailView.setText("aperretta");
        //mPasswordView.setText("prueba");

        //mEmailView.setText("MSARALE");
        //mPasswordView.setText("marcos15");

        // prod promoter
        //mEmailView.setText("JSTEFANI");
        //mPasswordView.setText("rosales5838");

        // --- TODO END JUST FOR TEST --------- //
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
        delayLoginForm();
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }else {
                delayLoginForm();
            }
        }
    }

    private void delayLoginForm(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginFormAnimated();
            }
        }, 2000L);
    }


    private void attemptLogin() {

        // Reset errors.
        //mEmailView.setError(null);
        //mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();

        /*
        try{
             email = URLEncoder.encode(email, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        */

        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;

        } /* //TODO: Descomentar
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mLoginFormView.setVisibility(View.INVISIBLE);
            mLoginButton.setVisibility(View.INVISIBLE);

            if(AppController.getInstance().isNetworkAvailable()) {
                UserController.getInstance().login(email, password, new Response.Listener<User>() {
                    @Override
                    public void onResponse(User response) {
                        ZoneController.getInstance().getItems(new Response.Listener<ArrayList<Zone>>() {
                            @Override
                            public void onResponse(ArrayList<Zone> response) {
                                gotoMain();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                showProgress(false);
                                animateForm(0);
                                DialogHelper.showMessage(LoginActivity.this,R.string.error,R.string.error_login_zone);
                            }
                        });

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        animateForm(0);
                        decodeError(error);
                    }
                });
            }else{
                showProgress(false);
                DialogHelper.showNoInternetErrorMessage(LoginActivity.this,null);
            }
        }
    }

    @Override
    protected void onDecodeError(String msg){
        DialogHelper.showMessage(LoginActivity.this, getResources().getString(R.string.error_login) );
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();

        if(cursor!=null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }


    private void gotoMain(){

        User user = UserController.getInstance().getSignedUser();
        if(user.role == UserRole.SALESMAN) {
            IntentHelper.goToSalesmanMainActivity(LoginActivity.this);
        }
        else if(user.role == UserRole.ZONE_LEADER) {
            IntentHelper.goToZoneLeaderMainActivity(LoginActivity.this);
        }
        else{
            DialogHelper.showMessage(LoginActivity.this,R.string.error,R.string.error_invalid_role_login);
            showProgress(false);
            animateForm(0);
        }
    }

    private void showLoginFormAnimated(){

        User user = UserController.getInstance().getSignedUser();
        if(user!=null && AppController.getInstance().isNetworkAvailable()) {
            animateLogo();
            showProgress(true);

            UserController.getInstance().login(user.username, user.password, new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    showProgress(false);
                    gotoMain();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    animateForm(0);
                }
            });
        }else{
            animateLogo();
            animateForm(200);
        }

    }

    private void animateLogo(){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        float yy = px-mLogoView.getY();

        mLogoView.clearAnimation();
        mLogoView.animate()
                .setStartDelay(0)
                .setDuration(300)
                .setInterpolator(mInterpolator)
                .translationY(yy).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator){
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void animateForm(int delay){
        mLoginFormView.setAlpha(0);
        mLoginButton.setAlpha(0);
        mSmallLogoView.setAlpha(0);
        mStaffLogoView.setAlpha(0);

        mLoginFormView.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.VISIBLE);
        mSmallLogoView.setVisibility(View.VISIBLE);
        mStaffLogoView.setVisibility(View.VISIBLE);


        mLoginFormView.animate()
                .setStartDelay(delay)
                .setDuration(600)
                .setInterpolator(mInterpolator)
                .alpha(1);

        mLoginButton.animate()
                .setStartDelay(delay)
                .setDuration(600)
                .setInterpolator(mInterpolator)
                .alpha(1);


        mSmallLogoView.animate()
                .setStartDelay(delay)
                .setDuration(600)
                .setInterpolator(mInterpolator)
                .alpha(1);

        mStaffLogoView.animate()
                .setStartDelay(delay)
                .setDuration(600)
                .setInterpolator(mInterpolator)
                .alpha(1);
    }
}

