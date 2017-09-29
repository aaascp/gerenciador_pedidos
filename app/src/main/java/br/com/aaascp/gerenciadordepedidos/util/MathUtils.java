package br.com.aaascp.gerenciadordepedidos.util;

import java.util.Collection;

/**
 * Created by andre on 23/09/17.
 */

public final class MathUtils {

    public static int reduce(Collection<Integer> collection) {
        int acc = 0;

        for(int item : collection) {
            acc += item;
        }

        return  acc;
    }
}
