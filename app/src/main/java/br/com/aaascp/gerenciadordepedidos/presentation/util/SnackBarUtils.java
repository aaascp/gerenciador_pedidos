package br.com.aaascp.gerenciadordepedidos.presentation.util;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by andre on 28/09/17.
 */

public final class SnackBarUtils {

    public static void showWithCenteredText(View parent, String message) {

        Snackbar snackBar = Snackbar.make(
                parent,
                message,
                Snackbar.LENGTH_LONG);

        View view = snackBar.getView();

        TextView snackBarText = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackBar.show();
    }
}
