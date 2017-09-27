package br.com.aaascp.gerenciadordepedidos.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 20/09/17.
 */

@AutoValue
public abstract class CustomerInfo implements Parcelable {

    public abstract int id();

    public abstract String name();

    public static Builder builder() {
        return new AutoValue_CustomerInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int value);

        public abstract Builder name(String value);

        public abstract CustomerInfo build();
    }
}
