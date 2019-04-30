package ar.com.sancorsalud.asociados.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "QR_CODE";
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e(TAG, rawResult.getText()); // Prints scan results
        Log.e(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        mScannerView.resumeCameraPreview(this);

        Intent intent=new Intent();
        intent.putExtra(ConstantsUtil.QRCODE_DATA, rawResult.getText() );
        setResult(RESULT_OK,intent);
        finish();
    }

}
