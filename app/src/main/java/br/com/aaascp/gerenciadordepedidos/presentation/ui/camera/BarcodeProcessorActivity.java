package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.models.CodesToProcess;
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

    private static final int PROCESSING_TIME = 3 * 1000;

    private BarcodeDetector barcodeDetector;

    @BindView(R.id.barcode_processor_preview)
    SurfaceView previewLayout;

    @BindView(R.id.barcode_processor_root)
    View root;

    @BindView(R.id.barcode_processor_guide)
    View guide;

    @BindView(R.id.barcode_processor_title)
    TextView title;

    @BindView(R.id.barcode_processor_items_left)
    TextView itemsLeft;

    private CodesToProcess codesToProcess;
    private int orderId;
    private boolean ready;

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

        ready = true;

        setupTitle();
        setItemsLeft();
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
        if (!ready) {
            return;
        }

        ready = false;

        CodesToProcess.Status status = codesToProcess.process(code);
        showMessage(status, code);

        if (status == CodesToProcess.Status.SUCCESS) {
            showProcessing(R.color.green);
            setItemsLeft();
            checkFinish();
        } else {
            showProcessing(R.color.red);
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
        int itemsLeftCount = codesToProcess.itemsLeft();

        String itemsLeftText =
                getResources()
                        .getQuantityString(
                                R.plurals.barcode_processor_items_left,
                                itemsLeftCount);

        if (itemsLeftCount == 0) {
            itemsLeftText = getString(R.string.barcode_processor_items_left);
        }

        itemsLeft.setText(
                String.format(
                        itemsLeftText,
                        itemsLeftCount));
    }

    private void setupBarcodeDetector() {
        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .build();

        if (!barcodeDetector.isOperational()) {
            return;
        }

        barcodeDetector.setProcessor(new BarcodeProcessor(this, this));
    }

    private void setupCamera() {
        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        previewLayout.getHolder()
                .addCallback(
                        new CameraPreview(cameraSource, this));
    }

    private void showProcessing(final @ColorRes int color) {
        final int normalColor =
                ContextCompat.getColor(
                        this,
                        android.R.color.white);

        final int highlightColor =
                ContextCompat.getColor(
                        this,
                        color);


        guide.setBackgroundColor(highlightColor);

        root.postDelayed(new Runnable() {
            @Override
            public void run() {
                guide.setBackgroundColor(normalColor);
                ready = true;
            }
        }, PROCESSING_TIME);
    }

    private void showMessage(CodesToProcess.Status status, String code) {
        String message = "%s: ";

        switch (status) {
            case SUCCESS:
                message += "Sucesso";
                break;
            case CODE_ALREADY_PROCESSED:
                message += "Código já processado";
                break;
            case CODE_INVALID:
                message += "Código Inválido";
                break;
            default:
                message += "Erro desconhecido";
        }

        Snackbar.make(
                root,
                String.format(message, code),
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void checkFinish() {
        if (codesToProcess.itemsLeft() == 0) {
            showFinishDialog();
        }
    }

    private void showFinishDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.barcode_processor_finish_dialog_title)
                .setMessage(R.string.barcode_processor_finish_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });

        builder.show();
    }

    @OnClick(R.id.barcode_processor_close)
    void onCloseClick() {
        onBackPressed();
    }
}