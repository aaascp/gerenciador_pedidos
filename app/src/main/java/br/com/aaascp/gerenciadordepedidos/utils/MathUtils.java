package br.com.aaascp.gerenciadordepedidos.utils;

import java.util.Collection;

/**
 * Created by andre on 23/09/17.
 */

public class MathUtils {

    public static int reduce(Collection<Integer> collection) {
        int acc = 0;

        for(int item : collection) {
            acc += item;
        }

        return  acc;
    }
}