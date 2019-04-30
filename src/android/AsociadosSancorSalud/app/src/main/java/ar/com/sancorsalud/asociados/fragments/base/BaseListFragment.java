package ar.com.sancorsalud.asociados.fragments.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.interfaces.ClickListener;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.RecyclerTouchListener;
import ar.com.sancorsalud.asociados.utils.Storage;

/**
 * Created by francisco on 28/10/16.
 */

public abstract class BaseListFragment extends BaseFragment {



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

    // --- TODO Pagging ----- //
    protected View mFooter;
    protected View mNextArrow;
    protected View mPrevArrow;
    protected int actualPage = 1;

    protected abstract RecyclerView.Adapter getAdapter();
    protected abstract void reloadData();


    //---- abstract template methods ------- //
    protected abstract void onRefreshData();
    protected abstract void didClickItem(int position);
    protected abstract void didLongClickItem(int position);

    // --- pagings methods ------------- //
    // --- TODO Pagging ----- //
    protected void toNextPage(){
        Log.e (TAG, "toNextPage ----------");
    }
    protected void toPreviousPage(){
        Log.e (TAG, "toPreviousPage ----------");
    }

    protected void setupLayouts(View view){


        mNetworkErrorView = view.findViewById(R.id.error_network);
        mErrorView = view.findViewById(R.id.error_data);
        mEmptyDataView = view.findViewById(R.id.empty_data);
        nListContainerView = view.findViewById(R.id.all_data);

        mRetry1 = view.findViewById(R.id.no_data_retry);
        mRetry2 = view.findViewById(R.id.no_connection_retry);
        mRetry3 = view.findViewById(R.id.error_retry);
        mProgressView = (ProgressBar) view.findViewById(R.id.progress);

        mHeader =  view.findViewById(R.id.header);
        mHeaderIcon = (ImageView) view.findViewById(R.id.header_icon);
        mHeaderText = (TextView) view.findViewById(R.id.header_text);

        // --- TODO Pagging ----- //
        mFooter = view.findViewById(R.id.footer_container);
        if (mFooter != null) {
            mNextArrow = view.findViewById(R.id.next_arrow);
            mPrevArrow = view.findViewById(R.id.prev_arrow);
        }

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        if(mRefreshLayout!=null) {
            mRefreshLayout.setDistanceToTriggerSync(ConstantsUtil.DISTANCE_TO_TRIGGER_SYNC_LIST);
            mRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
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

        // --- TODO Pagging ----- //
        if (mFooter != null && mFooter.getVisibility() == View.VISIBLE) {

            mNextArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "mNextArrow click!");

                    actualPage += 1;
                    toNextPage();
                }
            });

            mPrevArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "mPrevArrow click!");
                    actualPage -= 1;
                    if (actualPage > 0) {
                        toPreviousPage();
                    }
                }
            });
        }
    }

    protected void onEndedRefresh() {
        if(mRefreshLayout!=null) {
            mRefreshLayout.setRefreshing(false);
        }
        mLinearLayoutManager.scrollToPosition(0);
    }

    // --- TODO Pagging ----- //
    protected void checkShowPagintationFooter(int size, int pageSize) {
        if (mFooter != null) {

            int totalItems = Storage.getInstance().getTotalListItem();
            if (actualPage * pageSize < totalItems) {

                if (actualPage == 1) {
                    if (size < pageSize) {
                        mFooter.setVisibility(View.GONE);
                    } else {
                        mFooter.setVisibility(View.VISIBLE);
                        mNextArrow.setVisibility(View.VISIBLE);
                        mPrevArrow.setVisibility(View.GONE);
                    }

                } else {
                    mFooter.setVisibility(View.VISIBLE);
                    mNextArrow.setVisibility(View.VISIBLE);
                    mPrevArrow.setVisibility(View.VISIBLE);
                }

            } else if (actualPage * pageSize == totalItems) {

                if (actualPage == 1) {
                    mFooter.setVisibility(View.GONE);
                }else{
                    mFooter.setVisibility(View.VISIBLE);
                    mNextArrow.setVisibility(View.GONE);
                    mPrevArrow.setVisibility(View.VISIBLE);
                }

            } else if (actualPage == 1 && totalItems <= pageSize){
                mFooter.setVisibility(View.GONE);
                mNextArrow.setVisibility(View.GONE);
                mPrevArrow.setVisibility(View.GONE);
            }
            else {
                mFooter.setVisibility(View.VISIBLE);
                mNextArrow.setVisibility(View.GONE);
                mPrevArrow.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void hideFooter() {
        mFooter.setVisibility(View.GONE);
    }

    protected void showFooter() {
        mFooter.setVisibility(View.VISIBLE);
    }

    protected void hideNextArrow() {
        mNextArrow.setVisibility(View.GONE);
    }

    protected void showNextArrow() {
        mNextArrow.setVisibility(View.VISIBLE);
    }

    protected void hidePrevArrow() {
        mPrevArrow.setVisibility(View.GONE);
    }

    protected void showPrevArrow() {
        mPrevArrow.setVisibility(View.VISIBLE);
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
        if(getActivity()==null){
            v.setAlpha(1);
            v.setVisibility(View.VISIBLE);
            return;
        }

        if(mInterpolator==null)
            mInterpolator = AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear_out_slow_in);

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
