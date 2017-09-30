package br.com.aaascp.gerenciadordepedidos.entity;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;

/**
 * Created by andre on 27/09/17.
 */

@AutoValue
public abstract class NullOrderFilterList implements Parcelable {

    public abstract List<OrderFilter> filters();

    public static OrderFilterList create() {
        List<OrderFilter> filters = new ArrayList<>(0);
        return new AutoValue_OrderFilterList(filters);
    }
}