package br.com.aaascp.gerenciadordepedidos.repository.dao;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.repository.callback.DataSourceCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

/**
 * Created by andre on 03/10/17.
 */

public class OrdersFakeDataSource implements OrdersDataSource {

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
                callback.onError(Collections.singletonList("Error"));
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

        callback.onSuccess(copyOrder(order));
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

    public static void addOrder(Order order) {
        ORDERS_DATA.put(order.id(), order);
    }

    public static Order createOrderError() {
        return createOrder(ORDER_ERROR_ID);
    }

    public static Order createOrderCommunicationError() {
        return createOrder(ORDER_COMMUNICATION_ERROR_ID);
    }

    public static Order createOrder(int id) {
        return createOrder(
                id,
                ShipmentInfo.builder().address("Endereço").shipType("Transportadora").build(),
                CustomerInfo.builder().id(1).name("Customer").build(),
                0,
                null,
                DateFormatterUtils.getDateHourInstance().now());
    }

    public static Order createOrder(
            int id,
            ShipmentInfo shipmentInfo,
            CustomerInfo customerInfo,
            int size,
            String processedAt,
            String lastModifiedAt) {

        List<OrderItem> itemsCatalog = new ArrayList<>(3);

        itemsCatalog.add(
                OrderItem.builder()
                        .id(1234)
                        .code("314159265359")
                        .description("Cerveja 1")
                        .imageUrl("")
                        .quantity(0)
                        .build());

        itemsCatalog.add(
                OrderItem.builder()
                        .id(2345)
                        .code("5012345678900")
                        .description("Cerveja 2")
                        .imageUrl("")
                        .quantity(0)
                        .build());

        itemsCatalog.add(
                OrderItem.builder()
                        .id(3456)
                        .code("7898357410015")
                        .description("Cerveja 3")
                        .imageUrl("")
                        .quantity(0)
                        .build());

        Map<String, OrderItem> items = new HashMap<>();

        int index = 0;
        for (int i = 0; i < size; i++) {
            OrderItem item = itemsCatalog.get(index);
            OrderItem newItem =
                    OrderItem.builder()
                            .id(item.id())
                            .code(item.code())
                            .description(item.description())
                            .imageUrl(item.imageUrl())
                            .quantity(item.quantity() + 1)
                            .build();

            items.put(item.code(), newItem);
            itemsCatalog.set(index, newItem);

            index = index < itemsCatalog.size() - 1 ? index + 1 : 0;
        }

        return Order.builder()
                .id(id)
                .shipmentInfo(shipmentInfo)
                .customerInfo(customerInfo)
                .items(items)
                .size(size)
                .processedAt(processedAt)
                .lastModifiedAt(lastModifiedAt)
                .build();
    }
}
