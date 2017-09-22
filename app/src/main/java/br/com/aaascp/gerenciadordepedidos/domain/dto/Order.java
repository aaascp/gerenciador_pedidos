package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by andre on 10/07/17.
 */
@AutoValue
public abstract class Order implements Parcelable {

    public abstract int id();

    public abstract ShipmentInfo shipmentInfo();

    public abstract CustomerInfo customerInfo();

    public abstract Map<String, List<OrderItem>> items();

    public abstract int size();

    @Nullable
    public abstract String processedAt();

    public abstract String lastModifiedAt();

    public static Builder builder() {
        return new AutoValue_Order.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int value);

        public abstract Builder shipmentInfo(ShipmentInfo value);

        public abstract Builder customerInfo(CustomerInfo value);

        public abstract Builder items(Map<String, List<OrderItem>> value);

        public abstract Builder processedAt(String value);

        public abstract Builder lastModifiedAt(String value);

        public abstract Builder size(int value);

        public abstract Order build();
    }
}
