package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by andre on 22/09/17.
 */

final class BarcodeProcessor implements Detector.Processor<Barcode> {

    private final OnItemProcessedListener listener;

    BarcodeProcessor(OnItemProcessedListener listener) {
        this.listener = listener;
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {

        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if (barcodes.size() != 0) {
            final String code = barcodes.valueAt(0).displayValue;
            listener.onItemProcessed(code);
        }
    }

    @Override
    public void release() {

    }
}