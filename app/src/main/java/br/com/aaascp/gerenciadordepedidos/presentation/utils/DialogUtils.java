package br.com.aaascp.gerenciadordepedidos.presentation.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import java.util.HashSet;

import br.com.aaascp.gerenciadordepedidos.R;

/**
 * Created by andre on 28/09/17.
 */

public class DialogUtils {

    public static void getIntValues(
            Context context,
            String title,
            String message,
            final IntValuesListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));

        builder.setTitle(title)
                .setMessage(message)
                .setView(editText);

        builder.setPositiveButton(
                R.string.dialog_ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String[] valuesText = editText.getText()
                                .toString()
                                .split(",");

                        HashSet<Integer> values = new HashSet<>();
                        boolean error = false;

                        for (String value : valuesText) {
                            try {
                                int intValue = Integer.valueOf(value);
                                values.add(intValue);
                            } catch (NumberFormatException exception) {
                                listener.onError();
                                error = true;
                                break;
                            }
                        }

                        if (!error) {
                            listener.onValues(values);
                        }
                    }
                });

        builder.setNegativeButton(
                R.string.dialog_cancel,
                new DismissListener());

        builder.create().show();
    }

    public static void showError(
            Context context,
            String title,
            String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message);

        builder.setPositiveButton(
                R.string.dialog_ok,
                new DismissListener());

        builder.create().show();
    }

    public static void permissionError(Context context, String permission) {
        showError(
                context,
                context.getString(R.string.error_permission_title),
                String.format(
                        context.getString(R.string.error_permission_message),
                        permission));
    }

    public interface IntValuesListener {
        void onValues(HashSet<Integer> values);

        void onError();
    }

    private static final class DismissListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
        }
    }
}
