package br.com.aaascp.gerenciadordepedidos.repository.remote.rest.body;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by andre on 10/07/17.
 */

@AutoValue
public abstract class OrderResponseBody {

    public abstract int id();

    public abstract String shipType();

    public abstract int itemsCount();

    public abstract String lastModifiedAt();

    public static TypeAdapter<OrderResponseBody> typeAdapter(Gson gson) {
        return new AutoValue_OrderResponseBody.GsonTypeAdapter(gson);
    }
}
