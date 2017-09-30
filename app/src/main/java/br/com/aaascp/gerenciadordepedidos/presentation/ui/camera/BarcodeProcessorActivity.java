package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
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
import br.com.aaascp.gerenciadordepedidos.util.PermissionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by andre on 21/09/17.
 */

public final class BarcodeProcessorActivity extends BaseActivity
        implements OnItemProcessedListener, BarcodeProcessorContract.View {


    private static final int REQUEST_CODE_CAMERA_PERMISSION = 100;
    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    private static final String EXTRA_CODES_TO_PROCESS = "EXTRA_CODES_TO_PROCESS";

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

    private BarcodeProcessorContract.Presenter presenter;

    public static Intent getIntentForOrder(
            Context context,
            CodesToProcess codesToProcess) {

        Intent intent = new Intent(
                context,
                BarcodeProcessorActivity.class);

        intent.putExtra(EXTRA_CODES_TO_PROCESS, codesToProcess);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_processor);

        ButterKnife.bind(this);

        new BarcodeProcessorPresenter(
                this,
                getCodesToProcessExtra());

        setupBarcodeDetector();
        setupCamera();
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            presenter.onPermissionGranted();
        } else {
            presenter.onPermissionDenied();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupOrientation();
        presenter.start();
    }

    @Override
    public void setupCamera() {
        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        previewLayout.getHolder()
                .addCallback(
                        new CameraPreview(cameraSource, this));
    }

    @Override
    public void onItemProcessed(String code) {
        presenter.onItemProcessed(code);
    }

    @Override
    public void setupToolbar(int orderId) {
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

    @Override
    public void setItemsLeft(int itemsLeftCount) {
        String itemsLeftText =
                getResources()
                        .getQuantityString(
                                R.plurals.barcode_processor_items_left,
                                itemsLeftCount);

        itemsLeft.setText(
                String.format(
                        itemsLeftText,
                        itemsLeftCount));
    }

    @Override
    public void setZeroItemsLeft() {
        itemsLeft.setText(
                getString(R.string.barcode_processor_items_left));
    }

    @Override
    public void showCameraPermission() {
        PermissionUtils.requestPermissionForCamera(
                this,
                REQUEST_CODE_CAMERA_PERMISSION);
    }

    @Override
    public void showProcessError() {
        showProcessing(R.color.red);
    }

    @Override
    public void showProcessSuccess() {
        showProcessing(R.color.green);
    }

    @Override
    public void showMessage(String message) {
        SnackBarUtils.showWithCenteredText(
                root,
                message);
    }

    @Override
    public void showFinishDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.barcode_processor_finish_dialog_title)
                .setMessage(R.string.barcode_processor_finish_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                presenter.onFinish();
                            }
                        });

        builder.show();
    }

    @Override
    public void close(CodesToProcess codesToProcess) {
        Intent result = new Intent();
        result.putExtra(EXTRA_RESULT, codesToProcess);
        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    public void setPresenter(@NonNull BarcodeProcessorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private CodesToProcess getCodesToProcessExtra() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getParcelable(EXTRA_CODES_TO_PROCESS);
        }

        return null;
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
                presenter.onProcessingDone();
            }
        }, PROCESSING_TIME);
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

    private void setupBarcodeDetector() {
        barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .build();

        if (!barcodeDetector.isOperational()) {
            return;
        }

        barcodeDetector.setProcessor(
                new BarcodeProcessor(this, this));
    }
}