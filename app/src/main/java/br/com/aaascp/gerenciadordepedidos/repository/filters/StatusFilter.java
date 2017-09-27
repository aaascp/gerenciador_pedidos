package br.com.aaascp.gerenciadordepedidos.repository.filters;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import br.com.aaascp.gerenciadordepedidos.models.Order;

/**
 * Created by andre on 27/09/17.
 */

@AutoValue
public abstract class StatusFilter implements OrderFilter, Parcelable {

    public enum Status {
        PROCESSED,
        TO_PROCESS,
        ALL
    }

    public abstract Status status();

    @Override
    public boolean accept(OrderVisitor visitor, Order order) {
        return visitor.filter(this, order);
    }

    public static StatusFilter create(Status status) {
        return new AutoValue_StatusFilter(status);
    }
}