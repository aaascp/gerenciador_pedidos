package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.SnackBarUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by andre on 21/09/17.
 */

public final class BarcodeProcessorActivity extends BaseActivity
        implements OnItemProcessedListener {

    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";
    public static final String EXTRA_CODES_TO_PROCESS = "EXTRA_CODES_TO_PROCESS";

    private static final int PROCESSING_TIME = 3 * 1000;

    private BarcodeDetector barcodeDetector;

    @BindView(R.id.barcode_processor_toolbar)
    Toolbar toolbar;

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

        setupToolbar();
        setItemsLeft();
        setupBarcodeDetector();
        setupCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupOrientation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_CODES_TO_PROCESS, codesToProcess);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        codesToProcess = savedInstanceState.getParcelable(EXTRA_CODES_TO_PROCESS);
        setItemsLeft();
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

    private void setupToolbar() {
        title.setText(
                String.format(
                        getString(R.string.barcode_processor_title),
                        orderId));

        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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


        guide.getBackground().setTint(highlightColor);

        root.postDelayed(new Runnable() {
            @Override
            public void run() {
                guide.getBackground().setTint(normalColor);
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

        SnackBarUtils.showWithCenteredText(
                root,
                String.format(
                        message,
                        code));
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

    public void setupOrientation() {
        int orientation = getResources().getConfiguration().orientation;

        switch (orientation) {
            case ORIENTATION_LANDSCAPE:
                guide.setBackgroundResource(R.drawable.gradient_vertical_white_transparent);
                break;
            case ORIENTATION_PORTRAIT:
                guide.setBackgroundResource(R.drawable.gradient_horizontal_white_transparent);
                break;
        }
    }
}