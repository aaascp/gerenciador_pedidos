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
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 21/09/17.
 */

public class BarcodeProcessorActivity extends BaseActivity
        implements OnItemProcessedListener {

    public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";

    private BarcodeDetector barcodeDetector;

    @BindView(R.id.barcode_processor_preview)
    SurfaceView previewLayout;

    @BindView(R.id.barcode_processor_root)
    View root;

    @BindView(R.id.barcode_processor_title)
    TextView title;

    @BindView(R.id.barcode_processor_items_left)
    TextView itemsLeft;

    private OrdersRepository ordersRepository;
    private Order order;

    private int orderId;
    private int processedCount;

    public static Intent getIntentForOrder(
            Context context,
            int orderId) {

        Intent intent = new Intent(
                context,
                BarcodeProcessorActivity.class);

        intent.putExtra(EXTRA_ORDER_ID, orderId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_processor);

        ButterKnife.bind(this);

        ordersRepository = new OrdersRepository();
        extractExtras();

        setupBarcodeDetector();
    }

    @Override
    public void onItemProcessed(String code) {
        if (ordersRepository.processItem(order, code)) {
            processedCount++;
            setItemsLeft();
            checkFinish();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(OrderDetailsActivity.RESULT_ORDER_PROCESS, processedCount);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getInt(EXTRA_ORDER_ID, 0);
            setupOrder();
        }
    }

    private void setupOrder() {
        ordersRepository
                .getOrder(orderId,
                        new RepositoryCallback<Order>() {
                            @Override
                            public void onSuccess(Order data) {
                                order = data;
                                processedCount = 0;
                                setupTitle();
                                setItemsLeft();
                            }
                        });
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

    private void setItemsLeft() {
        if (order == null) {
            return;
        }

        final int itemsLeftCount = order.size() - processedCount;

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

        setupCamera();
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
        if (processedCount == order.size()) {
            finish();
        }
    }

    @OnClick(R.id.barcode_processor_close)
    void onCloseClick() {
        onBackPressed();
    }
}