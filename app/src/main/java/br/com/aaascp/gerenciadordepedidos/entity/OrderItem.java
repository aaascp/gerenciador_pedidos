package br.com.aaascp.gerenciadordepedidos.entity;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 20/09/17.
 */

@AutoValue
public abstract class OrderItem implements Parcelable {

    public abstract int id();

    public abstract String code();

    public abstract String description();

    @Nullable
    public abstract String imageUrl();

    public abstract int quantity();

    public static Builder builder() {
        return new AutoValue_OrderItem.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int value);

        public abstract Builder code(String value);

        public abstract Builder description(String value);

        public abstract Builder imageUrl(String value);

        public abstract Builder quantity(int value);

        public abstract OrderItem build();
    }
}
