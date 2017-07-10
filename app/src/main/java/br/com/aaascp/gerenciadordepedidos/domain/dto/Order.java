package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 10/07/17.
 */
@AutoValue
public abstract class Order implements Parcelable {

    public abstract int id();

    public abstract String shipType();

    public abstract int itemsCount();

    public abstract String lastModifiedAt();

    public static Builder builder() {
        return new AutoValue_Order.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int value);

        public abstract Builder shipType(String value);

        public abstract Builder itemsCount(int value);

        public abstract Builder lastModifiedAt(String value);

        public abstract Order build();
    }
}
