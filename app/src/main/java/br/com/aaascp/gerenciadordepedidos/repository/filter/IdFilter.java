package br.com.aaascp.gerenciadordepedidos.repository.filter;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.HashSet;

import br.com.aaascp.gerenciadordepedidos.entity.Order;

/**
 * Created by andre on 27/09/17.
 */

@AutoValue
public abstract class IdFilter implements OrderFilter, Parcelable {

    public abstract HashSet<Integer> ids();

    @Override
    public boolean accept(OrderVisitor visitor, Order order) {
        return visitor.filter(this, order);
    }

    public static IdFilter create(HashSet<Integer> ids) {
        return new AutoValue_IdFilter(ids);
    }
}
