package ar.com.sancorsalud.asociados.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.interfaces.ClickListener;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.RecyclerTouchListener;


public abstract class BaseListActivity extends BaseActivity {

    protected View mErrorView;
    protected View mNetworkErrorView;
    protected View mEmptyDataView;
    protected View nListContainerView;
    private View mRetry1;
    private View mRetry2;
    private View mRetry3;
    protected ProgressBar mProgressView;

    protected LinearLayoutManager mLinearLayoutManager;
    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected View mHeader;
    protected ImageView mHeaderIcon;
    protected TextView mHeaderText;

    protected abstract RecyclerView.Adapter getAdapter();
    protected abstract void reloadData();


    //---- abstract template methods ------- //
    protected abstract void onRefreshData();
    protected abstract void didClickItem(int position);
    protected abstract void didLongClickItem(int position);

    protected Interpolator mInterpolator;

    protected void setupLayouts(){

        mNetworkErrorView = findViewById(R.id.error_network);
        mErrorView = findViewById(R.id.error_data);
        mEmptyDataView = findViewById(R.id.empty_data);
        nListContainerView = findViewById(R.id.all_data);

        mRetry1 = findViewById(R.id.no_data_retry);
        mRetry2 = findViewById(R.id.no_connection_retry);
        mRetry3 = findViewById(R.id.error_retry);
        mProgressView = (ProgressBar) findViewById(R.id.progress);

        mHeader =  findViewById(R.id.header);
        mHeaderIcon = (ImageView) findViewById(R.id.header_icon);
        mHeaderText = (TextView) findViewById(R.id.header_text);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        if(mRefreshLayout!=null) {
            mRefreshLayout.setDistanceToTriggerSync(ConstantsUtil.DISTANCE_TO_TRIGGER_SYNC_LIST);
            mRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setHasFixedSize(true);
    }
    protected void setupListeners(){
        if(mRefreshLayout!=null) {
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onRefreshData();
                }
            });
        }
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(BaseListActivity.this,
                mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                didClickItem(position);
            }

            @Override
            public void onLongClick(View view, int position, float xx, float yy) {
                didLongClickItem(position);
            }
        }));

        mRetry1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadData();
            }
        });

        mRetry2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadData();
            }
        });

        mRetry3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadData();
            }
        });
    }

    protected void onEndedRefresh() {
        if(mRefreshLayout!=null) {
            mRefreshLayout.setRefreshing(false);
        }
        mLinearLayoutManager.scrollToPosition(0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(200).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void setupHeaderList(int iconId, int textId){
        if(mHeaderIcon!=null){
            mHeaderIcon.setVisibility(View.VISIBLE);
            mHeaderIcon.setImageResource(iconId);
        }
        setupHeaderList(textId);
    }

    protected void setupHeaderList(int textId){
        if(mHeaderText!=null){
            mHeaderText.setText(textId);
        }
    }

    protected void setupHeaderList(String text){
        if(mHeaderText!=null){
            mHeaderText.setText(text);
        }
    }

    protected void showContainerAnimated(View v){
        if(this==null){
            v.setAlpha(1);
            v.setVisibility(View.VISIBLE);
            return;
        }

        if(mInterpolator==null)
            mInterpolator = AnimationUtils.loadInterpolator(BaseListActivity.this, android.R.interpolator.linear_out_slow_in);

        v.setAlpha(0);
        v.setVisibility(View.VISIBLE);
        v.animate()
                .setStartDelay(0)
                .setDuration(300)
                .setInterpolator(mInterpolator)
                .alpha(1);
    }

    protected void showEmptyListLayout(){
        mNetworkErrorView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        nListContainerView.setVisibility(View.GONE);
        showContainerAnimated(mEmptyDataView);
    }

    protected void showDataListLayout() {
        mNetworkErrorView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mEmptyDataView.setVisibility(View.GONE);
        showContainerAnimated(nListContainerView);
    }

    protected void showErrorListLayout() {
        mNetworkErrorView.setVisibility(View.GONE);
        mEmptyDataView.setVisibility(View.GONE);
        nListContainerView.setVisibility(View.GONE);
        showContainerAnimated(mErrorView);
    }

    protected void showNetworkErrorListLayout() {
        mErrorView.setVisibility(View.GONE);
        mEmptyDataView.setVisibility(View.GONE);
        nListContainerView.setVisibility(View.GONE);
        showContainerAnimated(mNetworkErrorView);
    }

    protected void hideAll() {
        mNetworkErrorView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mEmptyDataView.setVisibility(View.GONE);
        nListContainerView.setVisibility(View.GONE);
    }

}
