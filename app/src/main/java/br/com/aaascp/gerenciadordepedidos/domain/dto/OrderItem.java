package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 20/09/17.
 */

@AutoValue
public abstract class OrderItem implements Parcelable {

    public abstract int cod();

    public abstract String description();

    public abstract int quantity();

    public abstract String imageUrl();

    public static Builder builder() {
        return new AutoValue_OrderItem.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder cod(int value);

        public abstract Builder description(String value);

        public abstract Builder quantity(int value);

        public abstract Builder imageUrl(String value);

        public abstract OrderItem build();
    }
}
