package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;

/**
 * Created by andre on 22/09/17.
 */

final class BarcodeProcessor implements Detector.Processor<Barcode> {

    private final BaseActivity baseActivity;
    private final OnItemProcessedListener listener;

    BarcodeProcessor(
            BaseActivity baseActivity,
            OnItemProcessedListener listener) {

        this.baseActivity = baseActivity;
        this.listener = listener;
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {

        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if (barcodes.size() != 0) {
            final String code = barcodes.valueAt(0).displayValue;

            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onItemProcessed(code);
                }
            });
        }
    }

    @Override
    public void release() {

    }
}