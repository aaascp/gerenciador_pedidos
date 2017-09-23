package br.com.aaascp.gerenciadordepedidos.domain.dto;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.utils.MathUtils;

/**
 * Created by andre on 25/09/17.
 */

@AutoValue
public abstract class CodesToProcess implements Parcelable {

    abstract Map<String, Integer> codes();

    public boolean process(String code) {
        int leftItems = codes().get(code);

        if (leftItems > 0) {
            codes().put(code, leftItems - 1);

            return true;
        }

        return false;
    }

    public int itemsLeft() {
        return MathUtils.reduce(codes().values());
    }


    public static CodesToProcess create(Map<String, Integer> codes) {
        return new AutoValue_CodesToProcess(codes);
    }
}
