package ar.com.sancorsalud.asociados.fragments.salesman;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.adapter.ProspectiveClientsAdapter;
import ar.com.sancorsalud.asociados.fragments.base.BaseListWithSearchFragment;
import ar.com.sancorsalud.asociados.manager.ProspectiveClientController;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.manager.QuoteOptionsController;
import ar.com.sancorsalud.asociados.manager.UserController;
import ar.com.sancorsalud.asociados.model.ExistenceStatus;
import ar.com.sancorsalud.asociados.model.client.ProspectiveClient;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.rest.services.RestApiServices;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DateUtils;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.IntentHelper;
import ar.com.sancorsalud.asociados.utils.LoadResourceUriCallback;
import ar.com.sancorsalud.asociados.utils.Storage;


public class MyAssignedPAFragment extends BaseListWithSearchFragment {

    private static final String TAG = "ASSIGNED_FRG";

    private ProspectiveClientsAdapter mAdapter = null;
    private ArrayList<ProspectiveClient> mList = new ArrayList<ProspectiveClient>();
    private ArrayList<ProspectiveClient> mFilterList = new ArrayList<ProspectiveClient>();

    private View mFilterHeader;
    private TextView mFilterTextView;
    private ImageView mFilterIcon;
    private View mClearFilterView;
    private ProspectiveClient.Filter mSelectedFilter = ProspectiveClient.Filter.NO_SCHEDULED;
    private ProspectiveClient.State mSelectedState = null;

    private ProspectiveClient mClient;

    private Boolean isFilter = false;
    private String mFilterText = ""; // esta variable se para filtrar por PA por api

    private boolean reloadData = false;
    private boolean cardsInProcess = false;

    private Handler handler = new Handler();

    public static MyAssignedPAFragment newInstance(boolean reloadData, boolean cardsInProcess) {
        MyAssignedPAFragment fragment = new MyAssignedPAFragment();
        Bundle args = new Bundle();
        args.putBoolean(ConstantsUtil.RELOAD_DATA, reloadData);
        args.putBoolean(ConstantsUtil.CARDS_IN_PROCESS, cardsInProcess);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reloadData = getArguments().getBoolean(ConstantsUtil.RELOAD_DATA, false);
            cardsInProcess = getArguments().getBoolean(ConstantsUtil.CARDS_IN_PROCESS, false);

            Log.e(TAG, "Reload Data: " + reloadData);
            Log.e(TAG, "cardsInProcess: " + cardsInProcess);

            if (cardsInProcess) {
                mSelectedFilter = null;
                mSelectedState = ProspectiveClient.State.CARDS_IN_PROCESS;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_assigned_prospective_client, container, false);
        setupLayouts(view);
        setHasOptionsMenu(true);

        mFilterHeader = view.findViewById(R.id.filter_header);
        mFilterTextView = (TextView) view.findViewById(R.id.filter_text);
        mFilterIcon = (ImageView) view.findViewById(R.id.filter_icon);

        mClearFilterView = view.findViewById(R.id.ic_close_filter_icon);

        setupLayouts(view);
        setupListeners();
        setHasOptionsMenu(true);

        // TODO NOT PAGGING
        /*if (reloadData) {
            Log.e(TAG, "reloadData!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            reloadData();
        } else {
            reloadData(false);
        }*/


        // always force reload service data because is pagginated
        // TODO PAGGING
        reloadData();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtil.SELECT_FILTER_OR_STATE) {

            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            } else if (resultCode == Activity.RESULT_OK) {
                boolean filter = mSelectedFilter != null;
                mSelectedState = null;
                mSelectedFilter = null;
                actualPage = 1;
                isFilter = true;

                if (data != null && data.getSerializableExtra(ConstantsUtil.SELECTED_STATE) != null) {
                    mSelectedState = (ProspectiveClient.State) data.getSerializableExtra(ConstantsUtil.SELECTED_STATE);
                    reloadData();
                } else if (data != null && data.getSerializableExtra(ConstantsUtil.SELECTED_FILTER) != null) {
                    mSelectedFilter = (ProspectiveClient.Filter) data.getSerializableExtra(ConstantsUtil.SELECTED_FILTER);
                    reloadData();
                }
            }

        } else if (requestCode == ConstantsUtil.VIEW_DETAIL_REQUEST_CODE) {
            if (resultCode == ConstantsUtil.RESULT_DATA_UPDATED) {
                reloadData();
            }
        } else if (requestCode == ConstantsUtil.VIEW_CARD_DETAIL) {
            if (data != null && resultCode == ConstantsUtil.CHANGED_STATE) {
                long id = data.getLongExtra(ConstantsUtil.ID, -1);
                for (ProspectiveClient client : mList) {
                    if (client.id == id) {
                        mList.remove(client);
                        mAdapter.onRefresh(mList, true);
                        return;
                    }
                }
            }
        } else if (requestCode == ConstantsUtil.VIEW_AUTH_COBRZ) {
            if (data != null && data.getBooleanExtra(ConstantsUtil.RESULT_AUTH_COBRZ, false)) {
                toPADetailActivity(mClient);
            }
        }
    }


    @Override
    protected void reloadData() {
        hideAll();
        showProgress(true);
        updateData(true);
    }

    @Override
    protected void onRefreshData() {
        onCancellAllRequets();
        showProgress(false);
        updateData(true);
    }


    protected void reloadData(boolean force) {
        //hideAll();
        showProgress(true);
        updateData(force);
    }


    // TODO PAGGING

    @Override
    protected void toNextPage() {
        Log.e(TAG, "toNextPage ----------");

        showProgress(true);
        updateData(false);
    }

    @Override
    protected void toPreviousPage() {
        Log.e(TAG, "toPreviousPage ----------");
        showProgress(true);
        updateData(false);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (Storage.getInstance().hasChangePAQuantityList()) {
            Storage.getInstance().setHasChangePAQuantityList(false);

            Log.e(TAG, "onResume-----------------------------");
            AppController.getInstance().updateSalesmanAssigmentData();
            reloadData(true);
        } else if (Storage.getInstance().hasToReloadPA()) {
            Storage.getInstance().setReloadPA(false);
            reloadData(true);
        }
    }

    @Override
    protected void setupListeners() {
        super.setupListeners();
        mClearFilterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFilter = false;
                mSelectedFilter = ProspectiveClient.Filter.NO_SCHEDULED;
                //mFilterText = "";
                actualPage = 1;
                mSelectedState = null;
                hideViewAnimated(mRecyclerView);
                hideFilterHeader();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_and_filter_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        setupSearch(searchItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter: {
                IntentHelper.gotoFilterActivity(getActivity());
                return true;
            }
        }
        return super.onOptionsItemSelected(item); // important line
    }

    protected RecyclerView.Adapter getAdapter() {
        if (mAdapter == null)
            mAdapter = new ProspectiveClientsAdapter(mList);

        return mAdapter;
    }


    /**
     * el código comentado es porque antes el filtrado era local, hoy es por API.
     *
     * @param filterText
     */
    protected void filterDataByText(String filterText) {
        mFilterText = (filterText != null || filterText != "") ? filterText : "";
        actualPage = 1;
        showProgress(true);
        updateData(false);

        /*mFilterList = new ArrayList<ProspectiveClient>();

        if (!filterText.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                ProspectiveClient assignment = mList.get(i);
                if (assignment.firstname.toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(assignment);
                } else if (assignment.lastname.toLowerCase().contains(filterText.toLowerCase())) {
                    mFilterList.add(assignment);
                }
            }

            mAdapter.onRefresh(mFilterList);
        } else mAdapter.onRefresh(mList);
        onEndedRefresh();*/
    }


    @Override
    protected void didClickItem(int position) {
        mClient = mAdapter.getItemAtIndex(position);

        // Reset all previous data
        Storage.getInstance().setAffiliationCardId(null);
        Log.e(TAG, "didClickItem: Reset Prious cardID ***************");

        //ProspectiveClient.State state = mClient.state;
        mClient.state = null;
        mClient.filter = null;

        if (mSelectedState != null) {
            mClient.state = mSelectedState;
            switch (mSelectedState) {

                // 2 filter ROW
                case CARDS_IN_PROCESS:
                case SEND_PROMOTION_CONTROL_SUPPORT: // Read Only Data
                case INCORRECT_CARD:
                    IntentHelper.gotoPACardDetailActivity(this.getActivity(), mClient);
                    break;

                // 3 filter ROW
                case PENDING_SEND_PROMOTION_CONTROL_SUPPORT:
                    IntentHelper.goToTraceCardActivity(this.getActivity(), mClient, true);
                    break;

                case SENT_CONTROL_SUPPORT:
                case PENDING_DOC:
                    IntentHelper.goToTraceCardActivity(this.getActivity(), mClient, false);
                    break;
            }
        }

        //ProspectiveClient.Filter filter = mClient.filter;
        if (mSelectedFilter != null) {
            mClient.filter = mSelectedFilter;
            switch (mSelectedFilter) {
                // 1 filter ROW
                case NO_SCHEDULED:
                    checkLoadWorkflowCobranzaForPA(mClient);
                    break;

                case SCHEDULED:
                case QUOTED:
                    IntentHelper.gotoPADetailActivity(this.getActivity(), mClient);
                    break;
                default:
                    break;
            }
        }

        if (!mClient.isAssigned()) {
            IntentHelper.gotoPADetailActivity(this.getActivity(), mClient);
        }
    }

    @Override
    protected void didLongClickItem(int position) {

        Log.e(TAG, "didLongClickItem....");
        ProspectiveClient client = mAdapter.getItemAtIndex(position);

        if (client.quotatedLink != null && !client.quotatedLink.isEmpty()) {
            Log.e(TAG, "To show Link....");

            if (AppController.getInstance().isNetworkAvailable()) {
                showProgressDialog(R.string.quoting_download);

                Log.e(TAG, "quotedLink: " + client.quotatedLink);

                QuotationController.getInstance().getDownloadFile(getActivity(), ConstantsUtil.QUOTED_LIST_DIR, client.quotatedLink, new Response.Listener<File>() {
                    @Override
                    public void onResponse(File resultFile) {
                        dismissProgressDialog();
                        Log.e(TAG, "download resource ok .....");

                        File file = resultFile;
                        donwloadLinkAndViewFile(file);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, "Error downloading resource:" + ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_download_file), ((error != null && error.getMessage() != null) ? error.getMessage() : ""));
                    }
                });
            } else {
                DialogHelper.showNoInternetErrorMessage(getActivity(), null);
            }

        }
    }

    private void hideFilterHeader() {
        mFilterHeader.clearAnimation();
        mFilterHeader.animate()
                .setStartDelay(0)
                .setDuration(200)
                .setInterpolator(mInterpolator)
                .alpha(0).translationX(mFilterHeader.getWidth()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mFilterHeader.animate()
                        .setStartDelay(0)
                        .setDuration(0)
                        .setInterpolator(mInterpolator)
                        .alpha(1).translationX(0).setListener(null);

                reloadData();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private boolean thereIsFilterOrStateSelected() {
        if (mSelectedFilter == null && mSelectedState == null) {
            return false;
        } else return true;
    }

    private void updateHeader() {
        updateHeaderTitle();

        if (!isFilter && mSelectedFilter == ProspectiveClient.Filter.NO_SCHEDULED && mSelectedState == null) {
            showViewAnimated(mHeader);
            showViewAnimated(mRecyclerView);
        } else {
            mHeader.setVisibility(View.GONE);
            showViewAnimated(mFilterHeader);
            showViewAnimated(mRecyclerView);
        }
    }


    private void updateHeaderTitle() {
        if (!isFilter && mSelectedFilter == ProspectiveClient.Filter.NO_SCHEDULED && mSelectedState == null) {
            int count = 0;
            for (ProspectiveClient client : mList) {
                if (DateUtils.isToday(client.assignedDate))
                    count++;
            }

            String name = UserController.getInstance().getSignedUser().firstname + " " + UserController.getInstance().getSignedUser().lastname;
            String title = getResources().getString(R.string.no_new_assigments, name);
            if (count == 1)
                title = getResources().getString(R.string.my_new_assigment, name);
            else if (count > 1)
                title = getResources().getString(R.string.my_new_assigments, name, count);

            setupHeaderList(title);

        } else if (mSelectedFilter != null) {
            if (mSelectedFilter == ProspectiveClient.Filter.NO_SCHEDULED) {
                mFilterTextView.setText(R.string.filter_no_scheduled);
                mFilterIcon.setImageResource(R.drawable.ic_event_not_scheduled);

            } else if (mSelectedFilter == ProspectiveClient.Filter.SCHEDULED) {
                mFilterTextView.setText(R.string.filter_scheduled);
                mFilterIcon.setImageResource(R.drawable.ic_event_scheduled);

            } else if (mSelectedFilter == ProspectiveClient.Filter.QUOTED) {
                mFilterTextView.setText(R.string.filter_quoted);
                mFilterIcon.setImageResource(R.drawable.ic_quoted);
            }
            /*
            } else if (mSelectedFilter == ProspectiveClient.Filter.PENDING_CARDS) {
                mFilterTextView.setText(R.string.filter_pending_cards);
                mFilterIcon.setImageResource(R.drawable.ic_cards_in_process);
            } else if (mSelectedFilter == ProspectiveClient.Filter.INCORRECT_CARDS) {
                mFilterTextView.setText(R.string.filter_incorrect_cards);
                mFilterIcon.setImageResource(R.drawable.ic_cards_to_correct);
            } else if (mSelectedFilter == ProspectiveClient.Filter.FINISHED_CARDS) {
                mFilterTextView.setText(R.string.filter_finished_cards);
                mFilterIcon.setImageResource(R.drawable.ic_cards_prom_control_support);
            }
            */
        } else if (mSelectedState != null) {

            if (mSelectedState == ProspectiveClient.State.CARDS_IN_PROCESS) {
                mFilterTextView.setText(R.string.filter_process_cards);
                mFilterIcon.setImageResource(R.drawable.ic_cards_in_process);

            } else if (mSelectedState == ProspectiveClient.State.INCORRECT_CARD) {
                mFilterTextView.setText(R.string.filter_incorrect_cards);
                mFilterIcon.setImageResource(R.drawable.ic_cards_to_correct);

            } else if (mSelectedState == ProspectiveClient.State.SEND_PROMOTION_CONTROL_SUPPORT) {
                mFilterTextView.setText(R.string.filter_prom_control_support);
                mFilterIcon.setImageResource(R.drawable.ic_cards_prom_control_support);

            } else if (mSelectedState == ProspectiveClient.State.PENDING_SEND_PROMOTION_CONTROL_SUPPORT) {
                mFilterTextView.setText(R.string.pending_send_control_support);
                mFilterIcon.setImageResource(R.drawable.ic_cards_in_process);

            } else if (mSelectedState == ProspectiveClient.State.SENT_CONTROL_SUPPORT) {
                mFilterTextView.setText(R.string.send_control_support_title);
                mFilterIcon.setImageResource(R.drawable.ic_cards_to_correct);

            } else if (mSelectedState == ProspectiveClient.State.PENDING_DOC) {
                mFilterTextView.setText(R.string.pending_docs_title);
                mFilterIcon.setImageResource(R.drawable.ic_cards_prom_control_support);
            }
        }
    }

    private void updateData(boolean force) {

        if (!AppController.getInstance().isNetworkAvailable()) {
            showProgress(false);
            showNetworkErrorListLayout();
            mSelectedFilter = null;
            return;
        }

        ProspectiveClientController.getInstance().cancelRequest();


        // ---- TODO NEW PAGGING -------------------------------------------------------------------- //


        final int pageSize = 10;

        // me aseguro que el filtro nunca sea null
        if (mFilterText == null)
            mFilterText = "";

        // reemplazo los espacios por %20 para la url;
        mFilterText = mFilterText.replaceAll(" ", "%20");


        String condition = null;

        // el queryString 'nombre' es para filtar PA por api
        if (thereIsFilterOrStateSelected()) {
            if (mSelectedState != null) {
                condition = "?size=" + pageSize + "&page=" + actualPage + "&filtroEstadoFicha=" + mSelectedState.getValue() + "&nombre=" + mFilterText;
            } else if (mSelectedFilter != null) {
                condition = "?size=" + pageSize + "&page=" + actualPage + "&filtroEstadoPotencial=" + mSelectedFilter.getValue() + "&nombre=" + mFilterText;
            }
        } else {
            condition = "?size=" + pageSize + "&page=" + actualPage;
        }

        ProspectiveClientController.getInstance().getMyPAList(condition, new Response.Listener<ArrayList<ProspectiveClient>>() {
            @Override
            public void onResponse(ArrayList<ProspectiveClient> var1) {
                mList = var1;
                showProgress(false);
                if (mList == null || mList.isEmpty()) {
                    showEmptyListLayout();
                } else {
                    showDataListLayout();
                    updateHeader();


                    // esto es porque la api ya no envia mas el estado y el filtro en cada PA
                    for (int i = 0; i < mList.size(); i++) {
                        ProspectiveClient client = mList.get(i);
                        if (mSelectedFilter != null) {
                            client.filter = mSelectedFilter;
                            client.state = null;
                        } else if (mSelectedState != null) {
                            client.state = mSelectedState;
                            client.filter = null;
                        }
                    }

                    checkShowPagintationFooter(mList.size(), pageSize);
                    mAdapter.onRefresh(mList, true);
                }
                onEndedRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError var1) {
                showProgress(false);
                showErrorListLayout();
            }
        });

        // ---- TODO END  PAGGING -------------------------------------------------------------------- //


        // ---- TODO NO PAGGING -------------------------------------------------------------------- //
        /*if (!thereIsFilterOrStateSelected()) {
            ProspectiveClientController.getInstance().getMyAssignedProspectiveClients(true, new Response.Listener<ArrayList<ProspectiveClient>>() {
                @Override
                public void onResponse(ArrayList<ProspectiveClient> var1) {
                    mList = var1;
                    showProgress(false);

                    if (mList == null || mList.isEmpty()) {
                        showEmptyListLayout();
                    } else {
                        updateHeader();
                        showDataListLayout();
                        mAdapter.onRefresh(mList);
                    }
                    onEndedRefresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError var1) {
                    if (getActivity() != null) {
                        showProgress(false);
                        showErrorListLayout();
                        onEndedRefresh();
                    }
                }
            });
        } else if (mSelectedState != null) {

            ProspectiveClientController.getInstance().getMyAssignedProspectiveClients(TAG, mSelectedState, new Response.Listener<ArrayList<ProspectiveClient>>() {
                @Override
                public void onResponse(ArrayList<ProspectiveClient> var1) {
                    mList = var1;
                    showProgress(false);
                    if (mList == null || mList.isEmpty()) {
                        showEmptyListLayout();
                    } else {
                        showDataListLayout();
                        updateHeader();
                        mAdapter.onRefresh(mList, true);
                    }
                    onEndedRefresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError var1) {
                    showProgress(false);
                    showErrorListLayout();
                }
            });

        } else if (mSelectedFilter != null) {

            ProspectiveClientController.getInstance().getMyAssignedProspectiveClients(force, mSelectedFilter, new Response.Listener<ArrayList<ProspectiveClient>>() {
                @Override
                public void onResponse(ArrayList<ProspectiveClient> var1) {
                    mList = var1;
                    showProgress(false);
                    if (mList == null || mList.isEmpty()) {
                        showEmptyListLayout();
                    } else {
                        showDataListLayout();
                        updateHeader();
                        mAdapter.onRefresh(mList);
                    }
                    onEndedRefresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError var1) {
                    showProgress(false);
                    showErrorListLayout();
                }
            });
        }*/

        // ---- TODO END NO PAGGING -------------------------------------------------------------------- //
    }


    private void donwloadLinkAndViewFile(File file) {

        if (file.getPath() != null && !file.getPath().isEmpty()) {
            FileHelper.loadUriFromFile(getActivity(), file.getPath(), new LoadResourceUriCallback() {
                @Override
                public void onSuccesLoadUri(final Uri uri) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                viewFile(uri);
                            }
                        });
                    } catch (Throwable e) {
                    }
                }

                @Override
                public void onErrorLoadUri(String error) {
                    try {
                        Log.e(TAG, "Error loading resource...");
                        DialogHelper.showMessage(getActivity(), getResources().getString(R.string.error_download_file));
                    } catch (Throwable e) {
                    }
                }
            });
        }
    }

    private void viewFile(Uri uri) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        getActivity().startActivity(intent);
    }


    private void checkLoadWorkflowCobranzaForPA(final ProspectiveClient client) {

        showProgressDialog(R.string.validating_data);
        HRequest request = RestApiServices.createCheckDniRequest(Long.valueOf(client.dni).toString(), new Response.Listener<ExistenceStatus>() {
            @Override
            public void onResponse(ExistenceStatus response) {
                dismissProgressDialog();

                if (response != null) {
                    // guardable will be always as this stage false becasuse we already create the PA, so we just control the workflow status
                    if (response.workflow) {
                        toWorkFlowCobranzaForPAActivity(client);
                    } else {
                        // OK CLINET
                        toPADetailActivity(client);
                    }
                } else {
                    DialogHelper.showStandardErrorMessage(getActivity(), null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                if (error != null && error.getMessage() != null && !error.getMessage().isEmpty()) {
                    Log.e(TAG, (error != null && error.getMessage() != null ? error.getMessage() : ""));
                    DialogHelper.showMessage(getActivity(), (error != null && error.getMessage() != null ? error.getMessage() : ""));
                }
            }
        });
        AppController.getInstance().getRestEngine().addToRequestQueue(request, false);
    }


    private void toPADetailActivity(ProspectiveClient client) {
        IntentHelper.gotoPADetailActivity(this.getActivity(), client);
    }

    private void toWorkFlowCobranzaForPAActivity(ProspectiveClient client) {
        IntentHelper.gotoWorkFlowCobranzaForPAActivity(this.getActivity(), client);
    }
}
