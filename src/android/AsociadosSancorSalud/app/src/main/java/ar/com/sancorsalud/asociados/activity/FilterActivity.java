package ar.com.sancorsalud.asociados.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

public class FilterActivity extends Activity {

    private static final int DELAY = 100;
    private ViewGroup mMainContainer;

    private View mFilterPATitle;
    private View mFillterStateTitle;
    private View mFilterTraceTitle;

    private View firstSeparator;
    private View secondSeparator;

    private ViewGroup mNoScheduledView;
    private ViewGroup mScheduledView;
    private ViewGroup mQuoteView;

    private ViewGroup mCardsInProcessView;
    private ViewGroup mIncorrectCardsView;
    private ViewGroup mSendPromotionControlSupportView;

    private ViewGroup mPendingSendPromotionControlSupportView;
    private ViewGroup mSendPendingControlSupportView;
    private ViewGroup mPendingDocView;

    private View mCloseButton;
    private Interpolator mInterpolator;
    private boolean shouldStartAnimation = true;
    private boolean closeInProgress = false;
    private ProspectiveClient.Filter mSelectedFilter = null;
    private ProspectiveClient.State mSelectedState = null;
    private boolean canceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mMainContainer = (ViewGroup) findViewById(R.id.main_container);

        mFilterPATitle  = findViewById(R.id.filter_pa_title);
        mFillterStateTitle  = findViewById(R.id.filter_state_title);
        mFilterTraceTitle  = findViewById(R.id.filter_trace_title);

        firstSeparator = findViewById(R.id.first_separator);
        secondSeparator = findViewById(R.id.second_separator);

        mNoScheduledView = (ViewGroup) findViewById(R.id.no_scheduled);
        mScheduledView = (ViewGroup) findViewById(R.id.scheduled);
        mQuoteView = (ViewGroup) findViewById(R.id.quoted);

        mCardsInProcessView = (ViewGroup) findViewById(R.id.cards_in_process);
        mIncorrectCardsView = (ViewGroup) findViewById(R.id.incorrect_cards);
        mSendPromotionControlSupportView = (ViewGroup) findViewById(R.id.send_promo_control_support_cards);


        mPendingSendPromotionControlSupportView = (ViewGroup) findViewById(R.id.pending_send_promo_control_support);
        mSendPendingControlSupportView = (ViewGroup) findViewById(R.id.send_control_support);
        mPendingDocView = (ViewGroup) findViewById(R.id.pending_docs);

        mCloseButton = findViewById(R.id.close_button);
        mInterpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        animateButtonsOut();
        setupListeners();
    }

    private void setupListeners() {
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAnimationAndFinish();
            }
        });

        // --- PA MANAGMENT FILTER  ------------------------------------------- //

        mNoScheduledView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedFilter = ProspectiveClient.Filter.NO_SCHEDULED;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });
        mScheduledView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedFilter = ProspectiveClient.Filter.SCHEDULED;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });
        mQuoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedFilter = ProspectiveClient.Filter.QUOTED;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });

        // --- AFFILIATION STATES ----------------------------------------- //

        mCardsInProcessView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.CARDS_IN_PROCESS;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });

        mIncorrectCardsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.INCORRECT_CARD;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });
        
        mSendPromotionControlSupportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });


        // ---- CARDS TRACEABILITY ----------------------------------------------------//

        mPendingSendPromotionControlSupportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.PENDING_SEND_PROMOTION_CONTROL_SUPPORT;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });
        mSendPendingControlSupportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.SENT_CONTROL_SUPPORT;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });
        mPendingDocView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedState = ProspectiveClient.State.PENDING_DOC;
                animateButton(view);
                stopAnimationAndFinish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AppController.getInstance().getRestEngine().cancelPendingRequests();
        if (!closeInProgress) {
            closeInProgress = true;
            canceled = true;
            animateButton(mCloseButton);
            stopAnimationAndFinish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            startAnimation();
    }

    private void startAnimation() {
        if (shouldStartAnimation) {
            shouldStartAnimation = false;
            animateButtonsOut();
            Animator anim = animateRevealColorFromCoordinates(mMainContainer, R.color.colorAccent, mMainContainer.getWidth(), 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animateButtonsIn();
                }
            });
        }
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        viewRoot.setBackgroundColor(ContextCompat.getColor(this, color));
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

    private void stopAnimationAndFinish() {
        float finalRadius = (float) Math.hypot(mMainContainer.getWidth(), mMainContainer.getHeight());
        int x = 0;
        int y = 0;
        mCloseButton.setVisibility(View.GONE);
        Animator anim = ViewAnimationUtils.createCircularReveal(mMainContainer, x, y, finalRadius, 0);
        mMainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMainContainer.setVisibility(View.INVISIBLE);
                Intent resultIntent = new Intent();
                if (canceled) {
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                } else if (mSelectedFilter != null) {
                    resultIntent.putExtra(ConstantsUtil.SELECTED_FILTER, mSelectedFilter);
                    setResult(Activity.RESULT_OK, resultIntent);
                } else if (mSelectedState != null) {
                    resultIntent.putExtra(ConstantsUtil.SELECTED_STATE, mSelectedState);
                    setResult(Activity.RESULT_OK, resultIntent);
                }

                finish();
            }
        });
        anim.start();
    }

    private void animateButtonsIn() {

        animateButtonIn(mFilterPATitle, 1 * DELAY);
        animateButtonIn(mNoScheduledView, 1 * DELAY);
        animateButtonIn(mScheduledView, 2 * DELAY);
        animateButtonIn(mQuoteView, 2 * DELAY);

        animateButtonIn(mFillterStateTitle, 3 * DELAY);
        animateButtonIn(mCardsInProcessView, 3 * DELAY);
        animateButtonIn(mIncorrectCardsView, 4 * DELAY);
        animateButtonIn(mSendPromotionControlSupportView, 4 * DELAY);

        animateButtonIn(mFilterTraceTitle, 5 * DELAY);
        animateButtonIn(mPendingSendPromotionControlSupportView, 5 * DELAY);
        animateButtonIn(mSendPendingControlSupportView, 6 * DELAY);
        animateButtonIn(mPendingDocView, 6 * DELAY);

        animateButtonIn(firstSeparator, 2 * DELAY);
        animateButtonIn(secondSeparator, 4 * DELAY);
    }

    private void animateButtonsOut() {

        animateButtonOut(mFilterPATitle);
        animateButtonOut(mScheduledView);
        animateButtonOut(mNoScheduledView);
        animateButtonOut(mQuoteView);

        animateButtonOut(mFillterStateTitle);
        animateButtonOut(mCardsInProcessView);
        animateButtonOut(mIncorrectCardsView);
        animateButtonOut(mSendPromotionControlSupportView);

        animateButtonOut(mFilterTraceTitle);
        animateButtonOut(mPendingSendPromotionControlSupportView);
        animateButtonOut(mSendPendingControlSupportView);
        animateButtonOut(mPendingDocView);

        animateButtonOut(secondSeparator);
        animateButtonOut(firstSeparator);
    }

    private void animateButtonIn(View child, int delay) {
        child.animate()
                .setStartDelay(delay)
                .setInterpolator(mInterpolator)
                .alpha(1)
                .scaleX(1)
                .scaleY(1);
    }

    private void animateButtonOut(View child) {
        child.animate()
                .setStartDelay(0)
                .setInterpolator(mInterpolator)
                .alpha(0)
                .scaleX(0f)
                .scaleY(0f);
    }

    private void animateButton(View view) {
        view.animate()
                .setStartDelay(1)
                .setInterpolator(mInterpolator)
                .alpha(0)
                .scaleX(0.5f)
                .scaleY(0.5f);

    }
}
