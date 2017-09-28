package br.com.aaascp.gerenciadordepedidos.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.utils.DialogUtils;

/**
 * Created by andre on 28/09/17.
 */

public class PermissionUtils {

    public static void requestPermissionForCamera(BaseActivity activity, int requestCode) {
        requestPermissionRationaleForCamera(activity, requestCode);
    }

    public static boolean isCameraEnabled(BaseActivity activity) {
        return isEnabled(activity, Manifest.permission.CAMERA);
    }

    private static void requestPermissionRationaleForCamera(BaseActivity activity, int requestCode) {
        requestPermissionRational(
                activity,
                Manifest.permission.CAMERA,
                activity.getString(R.string.permission_name_camera),
                requestCode);
    }

    private static void requestPermissionRational(
            BaseActivity activity,
            String permission,
            String permissionName,
            int requestCode) {

        if (!isEnabled(activity, permission)) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                DialogUtils.permissionError(activity, permissionName);
            } else {
                requestPermission(activity, permission, requestCode);
            }
        }
    }

    private static boolean shouldShowRequestPermissionRationale(
            BaseActivity activity,
            String permission) {

        return ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission);
    }

    private static void requestPermission(
            BaseActivity activity,
            String permission,
            int requestCode) {

        ActivityCompat.requestPermissions(
                activity,
                new String[]{permission},
                requestCode);
    }

    private static boolean isEnabled(Context context, String permission) {
        return isPermissionEnabled(
                ContextCompat.checkSelfPermission(
                        context,
                        permission));
    }

    private static boolean isPermissionEnabled(int permissionCheck) {
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }


}
