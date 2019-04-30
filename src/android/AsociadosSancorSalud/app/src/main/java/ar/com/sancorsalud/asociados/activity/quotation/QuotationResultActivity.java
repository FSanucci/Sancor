package ar.com.sancorsalud.asociados.activity.quotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.activity.main.SalesmanMainActivity;
import ar.com.sancorsalud.asociados.adapter.PlanAdapter;
import ar.com.sancorsalud.asociados.manager.QuotationController;
import ar.com.sancorsalud.asociados.model.plan.Plan;
import ar.com.sancorsalud.asociados.model.quotation.QuotationDataResult;
import ar.com.sancorsalud.asociados.utils.AppController;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.DialogHelper;
import ar.com.sancorsalud.asociados.utils.FileHelper;
import ar.com.sancorsalud.asociados.utils.LoadResourceUriCallback;
import ar.com.sancorsalud.asociados.utils.SnackBarHelper;

public class QuotationResultActivity extends BaseActivity {

    private static final String TAG = "QuoteResultAct";
    public static final int SHARE_REQUEST = 100;

    private TextView quote_type_txt;

    private ScrollView mScrollView;
    private RecyclerView planRecyclerView;
    private PlanAdapter planAdapter;

    private ArrayList<Integer> mSelectedPlanes;

    private Button saveShareButton;
    private Button saveButton;

    private QuotationDataResult quotationResult;

    private File file = null;
    private boolean fromShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mMainContainer = findViewById(R.id.main);

        mScrollView = (ScrollView) findViewById(R.id.scroll_box);
        quote_type_txt = (TextView) findViewById(R.id.quote_type_txt);

        saveShareButton = (Button) findViewById(R.id.save_share_button);
        saveButton = (Button) findViewById(R.id.save_button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        file = null;

        if (bundle != null) {
            quotationResult = (QuotationDataResult) bundle.getSerializable(ConstantsUtil.QUOTATION_RESULT);

            if (quotationResult != null && quotationResult.clientFullName != null)
                setupToolbar(toolbar, quotationResult.clientFullName);

            String quoteType = quotationResult.quoteType;
            if (quoteType == null || quoteType.equals(ConstantsUtil.QUOTE_TYPE_GENERAL)) {
                quote_type_txt.setText(getResources().getString(R.string.quote_type_general));
            } else {
                quote_type_txt.setText(getResources().getString(R.string.quote_type_manual));
            }
        }

        fillPlans();
        setupListeners();
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        super.setupToolbar(toolbar, title);

        toolbar.setNavigationIcon(R.drawable.ic_menu_ppal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            Log.e(TAG, "onActivityResult -----");

            // TODO check on Back to cancel Share and keep on Activity
            if (requestCode == SHARE_REQUEST) {
                Log.e(TAG, "Result: Share OK ....");
                toMain();
            }
    }

    /*
    @Override
    public void onBackPressed() {

        Log.e(TAG, "onBackPressed -----");
        if (!fromShare) {
            super.onBackPressed();
        }
    }
    */

    private void donwloadQuotationLinkAndShareFile() {

        if (file == null) {

            if (quotationResult.link != null) {
                Log.e(TAG, "Quotation link:" + quotationResult.link);

                if (AppController.getInstance().isNetworkAvailable()) {
                    showProgressDialog(R.string.quoting_download);

                    QuotationController.getInstance().getDownloadFile(getApplicationContext(), ConstantsUtil.QUOTED_DIR, quotationResult.link, new Response.Listener<File>() {
                        @Override
                        public void onResponse(File resultFile) {
                            dismissProgressDialog();
                            Log.e(TAG, "download resource ok!!!!!! .....");
                            file = resultFile;

                            fromShare = false;
                            shareFile();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgressDialog();
                            Log.e(TAG, "Error downloading resource:" + ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                            DialogHelper.showMessage(QuotationResultActivity.this, getResources().getString(R.string.error_download_file), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        }
                    });
                } else {
                    DialogHelper.showNoInternetErrorMessage(QuotationResultActivity.this, null);
                }

            } else {
                SnackBarHelper.makeError(mScrollView, R.string.quote_empty_link).show();
            }
        } else {
            shareFile();
        }
    }


    private void fillPlans() {

        planRecyclerView = (RecyclerView) findViewById(R.id.plan_recycler_view);
        planAdapter = new PlanAdapter(this, quotationResult.planes,  mSelectedPlanes, quotationResult.hidePlanValue);
        LinearLayoutManager planLayoutManager = new LinearLayoutManager(planRecyclerView.getContext());
        planRecyclerView.setLayoutManager(planLayoutManager);
        planRecyclerView.setAdapter(planAdapter);
        planRecyclerView.setHasFixedSize(true);
    }

    private void setupListeners() {

        saveShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                saveQuotation(true);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                saveQuotation(false);
            }
        });
    }


    private void saveQuotation(final boolean hasToshare) {

        Log.e(TAG, "save_quotation....");

        //int index = planAdapter.getSelectedIndex();
        ArrayList<Integer> selectedPlanIndexes = planAdapter.getSelectedIndexes();

        if (selectedPlanIndexes.isEmpty()){
            DialogHelper.showMessage(QuotationResultActivity.this, getResources().getString(R.string.quote_save_data_error));
        } else {

            //Plan plan = quotationResult.planes.get(index);
            //Log.e(TAG, "Cotizacion_id: " + quotationResult.id);
            //Log.e(TAG, "cotizacionSelectedId: " + plan.idCotizacion);

            List<Long> planesCotizIdList = new ArrayList<Long>();

            // N planes
            Log.e(TAG, "Cotizacion_id: " + quotationResult.id);
            for(Integer index :selectedPlanIndexes){
                Plan plan = quotationResult.planes.get(index);
                Log.e(TAG, "plan-IdCotizacion: " + plan.idCotizacion);
                planesCotizIdList.add(plan.idCotizacion);
            }

            if (AppController.getInstance().isNetworkAvailable()) {

                showProgressDialog(R.string.quoting_data);

                QuotationController.getInstance().saveQuotationData(quotationResult.id, planesCotizIdList, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void v) {
                        dismissProgressDialog();
                        Log.e(TAG, "save Quotation ok ...........");
                        SnackBarHelper.makeSucessful(mScrollView, R.string.quote_saved_success).show();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (hasToshare) {
                                    donwloadQuotationLinkAndShareFile();
                                }else{
                                    // quotation link will be loaded from PA list data service
                                    toMain();
                                }
                            }
                        }, 2000L);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        Log.e(TAG, "Error saving result:" +((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                        DialogHelper.showMessage(QuotationResultActivity.this, getResources().getString(R.string.error_quote), ((error != null && error.getMessage() != null) ? error.getMessage(): ""));
                    }
                });
            } else {
                DialogHelper.showNoInternetErrorMessage(QuotationResultActivity.this, null);
            }
        }
    }


    private void toMain() {

        Intent intent = new Intent(this, SalesmanMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle params = new Bundle();
        params.putBoolean(ConstantsUtil.RELOAD_DATA, true);
        intent.putExtras(params);

        startActivity(intent);
        finish();
    }

    private void shareContent(Uri uri) {
        fromShare = true;

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("application/pdf");

        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.quotation_file));
        startActivityForResult(Intent.createChooser(sharingIntent, getResources().getString(R.string.option_share)), SHARE_REQUEST);
    }

    private void shareFile() {

        if (file.getPath() != null && !file.getPath().isEmpty()) {

            Log.e (TAG, "share File !!!!" + file.getPath());

            FileHelper.loadUriFromFile(this, file.getPath(), new LoadResourceUriCallback() {
                @Override
                public void onSuccesLoadUri(final Uri uri) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                shareContent(uri);
                            }
                        });
                    } catch (Throwable e) {
                    }
                }

                @Override
                public void onErrorLoadUri(String error) {
                    try {
                        Log.e(TAG, "Error loading resource...");
                        DialogHelper.showMessage(QuotationResultActivity.this, getResources().getString(R.string.error_download_file));
                    } catch (Throwable e) {
                    }
                }
            });
        }
    }
}
