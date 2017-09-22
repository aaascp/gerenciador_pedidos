package br.com.aaascp.gerenciadordepedidos.domain.dto;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 10/07/17.
 */
@AutoValue
public abstract class Order implements Parcelable {

    public abstract int id();

    public abstract ShipmentInfo shipmentInfo();

    public abstract CustomerInfo customerInfo();

    public abstract List<OrderItem> items();

    @Nullable
    public abstract String processedAt();

    public abstract String lastModifiedAt();

    public List<OrderItem> itemsExpaded() {
        List<OrderItem> itemsExpanded = new ArrayList<>();

        for (OrderItem item : items()) {
            for (int i = 0; i < item.quantity(); i++) {

                itemsExpanded.add(
                        OrderItem.builder()
                                .cod(item.cod())
                                .imageUrl(item.imageUrl())
                                .quantity(1)
                                .description(item.description())
                                .processedAt(null)
                                .build());
            }
        }


        return itemsExpanded;
    }

    public static Builder builder() {
        return new AutoValue_Order.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int value);

        public abstract Builder shipmentInfo(ShipmentInfo value);

        public abstract Builder customerInfo(CustomerInfo value);

        public abstract Builder items(List<OrderItem> value);

        public abstract Builder processedAt(String value);

        public abstract Builder lastModifiedAt(String value);

        public abstract Order build();
    }
}
