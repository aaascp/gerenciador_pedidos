package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 21/09/17.
 */

public class BarcodeProcessorActivity extends BaseActivity
        implements OnItemProcessedListener {

    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    private static final String EXTRA_CODES_TO_PROCESS = "EXTRA_CODES_TO_PROCESS";
    private static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";

    private BarcodeDetector barcodeDetector;

    @BindView(R.id.barcode_processor_preview)
    SurfaceView previewLayout;

    @BindView(R.id.barcode_processor_root)
    View root;

    @BindView(R.id.barcode_processor_title)
    TextView title;

    @BindView(R.id.barcode_processor_items_left)
    TextView itemsLeft;

    private CodesToProcess codesToProcess;
    private int orderId;

    public static Intent getIntentForOrder(
            Context context,
            int orderId,
            CodesToProcess codesToProcess) {

        Intent intent = new Intent(
                context,
                BarcodeProcessorActivity.class);

        intent.putExtra(EXTRA_CODES_TO_PROCESS, codesToProcess);
        intent.putExtra(EXTRA_ORDER_ID, orderId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_processor);

        ButterKnife.bind(this);

        extractExtras();

        setupTitle();
        setupBarcodeDetector();
        setupCamera();
    }

    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra(EXTRA_RESULT, codesToProcess);
        setResult(RESULT_OK, result);

        super.finish();
    }

    @Override
    public void onItemProcessed(String code) {
        if (codesToProcess.process(code)) {
            setItemsLeft();
            checkFinish();
        }
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            codesToProcess = extras.getParcelable(EXTRA_CODES_TO_PROCESS);
            orderId = extras.getInt(EXTRA_ORDER_ID);
        }
    }

    private void setupTitle() {
        title.setText(
                String.format(
                        getString(R.string.barcode_processor_title),
                        orderId));
    }

    private void setItemsLeft() {
        final int itemsLeftCount = codesToProcess.itemsLeft();

        final String itemsLeftText =
                getResources()
                        .getQuantityString(
                                R.plurals.barcode_processor_items_left,
                                itemsLeftCount);

        itemsLeft.post(new Runnable() {
            @Override
            public void run() {
                itemsLeft.setText(
                        String.format(
                                itemsLeftText,
                                itemsLeftCount));
            }
        });
    }

    private void setupBarcodeDetector() {
        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .build();

        if (!barcodeDetector.isOperational()) {
//            showErrorMessage(
//                    R.string.camera_barcode_detector_not_operational,
//                    getString(R.string.camera_barcode_detector_init_error));

            return;
        }

        barcodeDetector.setProcessor(new BarcodeProcessor(this));
    }

    private void setupCamera() {
        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();


        previewLayout.getHolder()
                .addCallback(
                        new CameraPreview(cameraSource));
    }

    private void showErrorMessage(@StringRes int message, String error) {
        Snackbar.make(
                root,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void checkFinish() {
        if (codesToProcess.itemsLeft() == 0) {
            finish();
        }
    }

    @OnClick(R.id.barcode_processor_close)
    void onCloseClick() {
        onBackPressed();
    }
}