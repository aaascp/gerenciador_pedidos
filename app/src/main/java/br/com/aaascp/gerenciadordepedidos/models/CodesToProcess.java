package br.com.aaascp.gerenciadordepedidos.models;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.utils.MathUtils;

/**
 * Created by andre on 25/09/17.
 */

@AutoValue
public abstract class CodesToProcess implements Parcelable {

    public enum Status {
        SUCCESS,
        CODE_ALREADY_PROCESSED,
        CODE_INVALID
    }

    public abstract Map<String, Integer> codes();

    public Status process(String code) {
        if(!codes().containsKey(code)) {
            return Status.CODE_INVALID;
        }

        int leftItems = codes().get(code);

        if (leftItems > 0) {
            codes().put(code, leftItems - 1);
            return Status.SUCCESS;
        }

        return Status.CODE_ALREADY_PROCESSED;
    }

    public int itemsLeft() {
        return MathUtils.reduce(codes().values());
    }


    public static CodesToProcess create(Map<String, Integer> codes) {
        return new AutoValue_CodesToProcess(codes);
    }
}
