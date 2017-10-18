package br.com.aaascp.gerenciadordepedidos.repository.dao;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.factory.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.repository.callback.DataSourceCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;

/**
 * Created by andre on 03/10/17.
 */

public class OrdersFakeDataSource implements OrdersDataSource {

    public static final String ERROR_MESSAGE = "ERROR";
    private static final int ORDER_ERROR_ID = -1000;
    private static final int ORDER_COMMUNICATION_ERROR_ID = -2000;

    private static Map<Integer, Order> ORDERS_DATA = new ArrayMap<>();

    @Override
    public void save(Order order) {
        ORDERS_DATA.put(order.id(), order);
    }

    @Override
    public void load(
            OrderFilterList filterList,
            final DataSourceCallback<List<Order>> callback) {

        for (Order order : ORDERS_DATA.values()) {
            if (order.id() == ORDER_ERROR_ID) {
                callback.onError(Collections.singletonList(ERROR_MESSAGE));
                return;
            } else if (order.id() == ORDER_COMMUNICATION_ERROR_ID) {
                callback.onError(null);
                return;
            }
        }

        List<Order> orders = new ArrayList<>(ORDERS_DATA.size());
        orders.addAll(ORDERS_DATA.values());
        callback.onSuccess(orders);

    }

    @Override
    public void load(int id, final DataSourceCallback<Order> callback) {
        final Order order = ORDERS_DATA.get(id);

        if (order.id() == ORDER_ERROR_ID) {
            callback.onError(Collections.singletonList(ERROR_MESSAGE));
            return;
        } else if (order.id() == ORDER_COMMUNICATION_ERROR_ID) {
            callback.onError(null);
            return;
        }

        callback.onSuccess(copyOrder(order));
    }

    public static Order load(int id) {
        return ORDERS_DATA.get(id);
    }

    @Override
    public boolean filter(IdFilter filter, Order order) {
        return true;
    }

    @Override
    public boolean filter(StatusFilter filter, Order order) {
        return true;
    }

    private Order copyOrder(Order order) {
        return Order.builder()
                .id(order.id())
                .items(order.items())
                .processedAt(order.processedAt())
                .shipmentInfo(order.shipmentInfo())
                .customerInfo(order.customerInfo())
                .lastModifiedAt(order.lastModifiedAt())
                .size(order.size())
                .build();
    }

    public static void clear() {
        ORDERS_DATA.clear();
    }

    public static void addOrder(Order order) {
        ORDERS_DATA.put(order.id(), order);
    }

    public static Order createOrderError() {
        return OrdersFactory.createOrder(ORDER_ERROR_ID, false);
    }

    public static Order createOrderCommunicationError() {
        return OrdersFactory.createOrder(ORDER_COMMUNICATION_ERROR_ID, false);
    }
}
