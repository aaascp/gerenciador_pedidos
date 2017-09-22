package br.com.aaascp.gerenciadordepedidos.utils;

/**
 * Created by andre on 22/09/17.
 */

final public class StringUtils {

    public static boolean isNullOrEmpty(String string) {
        return string == null ||
                string.equals("");
    }
}
