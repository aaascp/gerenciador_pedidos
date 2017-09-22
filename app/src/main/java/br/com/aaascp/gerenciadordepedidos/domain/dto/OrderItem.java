package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 20/09/17.
 */

@AutoValue
public abstract class OrderItem implements Parcelable {

    public abstract int code();

    public abstract String description();

    @Nullable
    public abstract String imageUrl();

    @Nullable
    public abstract String processedAt();

    public static Builder builder() {
        return new AutoValue_OrderItem.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(int value);

        public abstract Builder description(String value);

        public abstract Builder imageUrl(String value);

        public abstract Builder processedAt(String value);

        public abstract OrderItem build();
    }
}
