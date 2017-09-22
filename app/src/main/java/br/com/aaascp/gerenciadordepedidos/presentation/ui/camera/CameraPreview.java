package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by andre on 21/09/17.
 */

final class CameraPreview implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private CameraSource cameraSource;

    CameraPreview(CameraSource cameraSource) {
        this.cameraSource = cameraSource;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            cameraSource.start(holder);
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage());
//        }
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {

        //
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }
}