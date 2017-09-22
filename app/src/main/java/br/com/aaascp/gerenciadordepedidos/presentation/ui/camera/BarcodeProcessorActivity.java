package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 21/09/17.
 */

public class BarcodeProcessorActivity extends BaseActivity {

    public static final String ORDER_EXTRA = "ORDER_EXTRA";
    private static final String TAG = BarcodeProcessor.class.getSimpleName();

    private BarcodeDetector barcodeDetector;

    @BindView(R.id.barcode_processor_preview)
    SurfaceView previewLayout;

    @BindView(R.id.barcode_processor_root)
    View root;

    @BindView(R.id.barcode_processor_title)
    TextView title;

    @BindView(R.id.barcode_processor_items_left)
    TextView itemsLeft;

    private Order order;

    public static Intent getIntentForOrder(Context context, Order order) {

        Intent intent = new Intent(
                context,
                BarcodeProcessorActivity.class);

        intent.putExtra(ORDER_EXTRA, order);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_processor);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            order = extras.getParcelable(ORDER_EXTRA);
        }

        setupTitle();
        setupItemsLeft();

        setupBarcodeDetector();
    }

    @OnClick(R.id.barcode_processor_close)
    void onCloseClick() {
        onBackPressed();
    }

    private void setupTitle() {
        if (order == null) {
            return;
        }

        title.setText(
                String.format(
                        getString(R.string.barcode_processor_title),
                        order.id()));
    }

    private void setupItemsLeft() {
        if (order == null) {
            return;
        }

        String itemsLeftText =
                getResources()
                        .getQuantityString(
                                R.plurals.barcode_processor_items_left,
                                order.items().size());

        itemsLeft.setText(
                String.format(
                        itemsLeftText,
                        order.size()));
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

//        barcodeDetector.setProcessor(new BarcodeProcessor(codeText));

        setupCamera();
    }

    private void setupCamera() {
        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();


        previewLayout.getHolder().addCallback(new CameraPreview(cameraSource));
    }

    private void showErrorMessage(@StringRes int message, String error) {
        Log.d(TAG, error);

        Snackbar.make(
                root,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    protected static final class BarcodeProcessor implements Detector.Processor<Barcode> {

        private TextView codeText;

        BarcodeProcessor(TextView codeText) {
            this.codeText = codeText;
        }


        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {

            final SparseArray<Barcode> barcodes = detections.getDetectedItems();

            if (barcodes.size() != 0) {

                final String code = barcodes.valueAt(0).displayValue;
                codeText.post(new Runnable() {
                    public void run() {
                        codeText.setText(code);
                    }
                });
            }
        }

        @Override
        public void release() {

        }

    }
}