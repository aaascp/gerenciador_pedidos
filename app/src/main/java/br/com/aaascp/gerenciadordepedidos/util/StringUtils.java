package br.com.aaascp.gerenciadordepedidos.util;

/**
 * Created by andre on 22/09/17.
 */

public final class StringUtils {

    public static boolean isNullOrEmpty(String string) {
        return string == null ||
                string.equals("");
    }
}
