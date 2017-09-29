package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.util.PermissionUtils;

/**
 * Created by andre on 21/09/17.
 */

final class CameraPreview implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private final CameraSource cameraSource;
    private final BaseActivity activity;

    CameraPreview(CameraSource cameraSource, BaseActivity activity) {
        this.cameraSource = cameraSource;
        this.activity = activity;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if(PermissionUtils.isCameraEnabled(activity)) {
                cameraSource.start(holder);
            } else {
                activity.finish();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
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