package ar.com.sancorsalud.asociados.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.CommentAdapter;
import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardComment;
import ar.com.sancorsalud.asociados.model.affiliation.AffiliationCardInfo;
import ar.com.sancorsalud.asociados.model.affiliation.DesValidation;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

public class TraceCardActivity extends BaseActivity {

    private static final String TAG = "CardDetail";
    public static final String PA = "pa";
    public static final String SHOW_EDIT_MODE = "edit_mode";

    private ProspectiveClient mClient;
    private boolean mShowEditMode = true;

    private View mHeaderContainer;
    private ImageView mHeaderIcon;
    private TextView mHeaderText;

    private View mEditContainer;
    private EditText mCommentEditText;
    private TextInputLayout mCommentTextInputLayout;

    private EditText mRequestEditText;

    private EditText mDesNumberEditText;
    private TextInputLayout mDesNumberTextInputLayout;

    private RecyclerView mHistoryRecyclerView;
    private TextView mHistoryNoContent;

    private View mValidateButton;
    private View mSendButton;

    private View mMainContainer;
    private ProgressBar mProgressView;

    private AffiliationCardInfo mCard;

    private boolean mHasError = false;
    private boolean mCardRetrieved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_card_detail);

        if (getIntent().getExtras() != null) {
            mClient = (ProspectiveClient) getIntent().getSerializableExtra(PA);
            mShowEditMode = getIntent().getBooleanExtra(SHOW_EDIT_MODE, false);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, mClient.getFullName());

        mMainContainer = findViewById(R.id.main);
        mHeaderContainer = findViewById(R.id.header);
        mHeaderIcon = (ImageView) findViewById(R.id.header_icon);
        mHeaderText = (TextView) findViewById(R.id.header_text);
        mEditContainer = findViewById(R.id.edit_container);

        mCommentEditText = (EditText) findViewById(R.id.comment_input);
        setTypeTextNoSuggestions(mCommentEditText);
        mCommentTextInputLayout = (TextInputLayout) findViewById(R.id.comment_wrapper);


        mRequestEditText = (EditText) findViewById(R.id.request_number_input);


        mDesNumberEditText = (EditText) findViewById(R.id.des_number_input);
        mDesNumberTextInputLayout = (TextInputLayout) findViewById(R.id.des_number_wrapper);

        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.history);
        mHistoryNoContent = (TextView) findViewById(R.id.history_no_content);

        mMainContainer = findViewById(R.id.scroll);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        mValidateButton = findViewById(R.id.validate_button);
        mSendButton = findViewById(R.id.send_button);

        mSendButton.setEnabled(false);
        mSendButton.setAlpha(0.5f);

        if (mClient != null && mClient.cardId != -1L) {
            mRequestEditText.setText(Long.valueOf(mClient.cardId).toString());
        }
        setupListener();
        retrieveContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subte_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_subte:
                gotoSubte();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupListener() {

        mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateDES()) {
                    mValidateButton.setEnabled(false);
                    hideSoftKeyboard(mMainContainer);

                    long desNumber = Long.valueOf(mDesNumberEditText.getText().toString());
                    showProgress(true);

                    HRequest request = RestApiServices.validateDESRequest(desNumber, new Response.Listener<DesValidation>() {
                        @Override
                        public void onResponse(DesValidation desValidation) {
                            showProgress(false);
                            mValidateButton.setEnabled(true);

                            if (desValidation != null && desValidation.cardId != -1L) {
                                mSendButton.setEnabled(true);
                                mSendButton.setAlpha(1f);
                            } else {
                                SnackBarHelper.makeError(mMainContainer, R.string.error_trace_validate_des).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            mValidateButton.setEnabled(true);
                            DialogHelper.showStandardErrorMessage(TraceCardActivity.this, null);

                        }
                    });
                    AppController.getInstance().getRestEngine().addToRequestQueue(request);
                }
            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateCommnets()) {

                    mSendButton.setEnabled(false);
                    hideSoftKeyboard(mMainContainer);
                    showProgress(true);

                    HRequest request = RestApiServices.createChangeCardStateRequest(mClient.cardId, ProspectiveClient.State.SENT_CONTROL_SUPPORT, null, mCommentEditText.getText().toString(), new Response.Listener<ExistenceStatus>() {
                        @Override
                        public void onResponse(ExistenceStatus statusResponse) {
                            showProgress(false);

                            if (statusResponse != null && statusResponse.status.equals("success")) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(ConstantsUtil.ID, mClient.id);
                                setResult(ConstantsUtil.CHANGED_STATE, resultIntent);
                                finish();
                            }else{
                                SnackBarHelper.makeError(mMainContainer, R.string.error_trace_change_status).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mSendButton.setEnabled(true);
                            DialogHelper.showStandardErrorMessage(TraceCardActivity.this, null);
                            showProgress(false);
                        }
                    });
                    AppController.getInstance().getRestEngine().addToRequestQueue(request);
                }
            }
        });
    }

    private void initialize() {

        if (mClient.state == ProspectiveClient.State.PENDING_SEND_PROMOTION_CONTROL_SUPPORT) {
            mHeaderText.setText(R.string.pending_send_control_support_title);
            mHeaderIcon.setImageResource(R.drawable.ic_cards_in_process);
        } else if (mClient.state == ProspectiveClient.State.SENT_CONTROL_SUPPORT) {
            mHeaderText.setText(R.string.send_control_support_title);
            mHeaderIcon.setImageResource(R.drawable.ic_cards_to_correct);
        } else if (mClient.state == ProspectiveClient.State.PENDING_DOC) {
            mHeaderText.setText(R.string.pending_docs_title);
            mHeaderIcon.setImageResource(R.drawable.ic_cards_prom_control_support);
        }

        if (mShowEditMode) {
            mEditContainer.setVisibility(View.VISIBLE);
        } else {
            mEditContainer.setVisibility(View.GONE);
        }

        if (mCard.comments == null || mCard.comments.size() == 0) {
            mHistoryRecyclerView.setVisibility(View.GONE);
            mHistoryNoContent.setVisibility(View.VISIBLE);
        } else {
            mHistoryRecyclerView.setVisibility(View.VISIBLE);
            mHistoryNoContent.setVisibility(View.GONE);
            mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ArrayList<AffiliationCardComment> list = mCard.comments;
            Collections.sort(list, new Comparator<AffiliationCardComment>() {
                public int compare(AffiliationCardComment o1, AffiliationCardComment o2) {
                    return o2.postDate.compareTo(o1.postDate);
                }
            });

            CommentAdapter adapter = new CommentAdapter(list);
            mHistoryRecyclerView.setAdapter(adapter);
            mHistoryRecyclerView.setHasFixedSize(true);
            mHistoryRecyclerView.setNestedScrollingEnabled(false);

        }
    }

    private void retrieveContent() {
        if (!AppController.getInstance().getRestEngine().isNetworkAvailable()) {
            DialogHelper.showNoInternetErrorMessage(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }
        showProgress(true);

        //GET CARD
        HRequest request1 = RestApiServices.createGetCardInfoRequest(mClient.cardId, new Response.Listener<AffiliationCardInfo>() {
            @Override
            public void onResponse(AffiliationCardInfo response) {
                mCard = response;
                mCardRetrieved = true;
                Log.e("request", "OK1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mHasError = true;
                mCardRetrieved = true;
                Log.e("request", "ERROR1");
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request1);

        checkIfDataHasBeenRetrieved();
    }

    private void checkIfDataHasBeenRetrieved() {
        if (!mCardRetrieved) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfDataHasBeenRetrieved();
                }
            }, 1000);
            return;
        }

        if (mHasError) {
            DialogHelper.showStandardErrorMessage(TraceCardActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }

        showProgress(false);
        initialize();
    }

    private boolean validateDES() {

        boolean error = false;

        if (mDesNumberEditText.getText().length() == 0) {
            error = true;
            mDesNumberTextInputLayout.setError(getString(R.string.trace_des_error));
        } else {
            mDesNumberTextInputLayout.setErrorEnabled(false);
        }
        return !error;
    }

    private boolean validateCommnets(){

        boolean error = false;
        if (mCommentEditText.getText().length() == 0) {
            error = true;
            mCommentTextInputLayout.setError(getString(R.string.trace_comment_error));
        } else {
            mCommentTextInputLayout.setErrorEnabled(false);
        }
        return !error;
    }

    private void gotoSubte() {
        if (mCard != null) {
            if (mCard.segmento.id.equalsIgnoreCase(ConstantsUtil.DESREGULADO_SEGMENTO)) {
                IntentHelper.goToSubteNoGravActivity(this, mClient.id);
            } else {
                IntentHelper.goToSubteGravActivity(this, mClient.id);
            }
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
