package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Created by andre on 20/09/17.
 */

@AutoValue
public abstract class ShipmentInfo implements Parcelable {

    public abstract String shipType();

    public abstract String address();

    public static Builder builder() {
        return new AutoValue_ShipmentInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder shipType(String value);

        public abstract Builder address(String value);

        public abstract ShipmentInfo build();
    }
}
