package br.com.aaascp.gerenciadordepedidos.repository.dao;

import android.os.Handler;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.repository.callback.DataSourceCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

/**
 * Created by andre on 22/09/17.
 */

public final class OrdersMemoryDataSource implements OrdersDataSource {

    private static final int NETWORK_DELAY = 2000;
    private static SparseArray<Order> ORDERS_DATA = new SparseArray<>();

    static {
        ORDERS_DATA = new SparseArray<>(4);
        addOrder(1000, false);
        addOrder(1001, false);
        addOrder(1002, true);
        addOrder(1003, false);
    }

    @Override
    public void save(Order order) {
        ORDERS_DATA.put(order.id(), order);
    }

    @Override
    public void load(
            final OrderFilterList filterList,
            final DataSourceCallback<List<Order>> callback) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                List<OrderFilter> filters = filterList.filters();
                final List<Order> filteredOrders = new ArrayList<>();

                for (int i = 0; i < ORDERS_DATA.size(); i++) {
                    Order order = ORDERS_DATA.valueAt(i);
                    boolean isFiltered;

                    for (OrderFilter filter : filters) {
                        isFiltered = filter.accept(this, order);

                        if (isFiltered) {
                            filteredOrders.add(copyOrder(order));
                            break;
                        }
                    }
                }

                callback.onSuccess(filteredOrders);
            }
        }, NETWORK_DELAY);
    }

    @Override
    public void load(final int id, final DataSourceCallback<Order> callback) {


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Order order = ORDERS_DATA.get(id);
                callback.onSuccess(copyOrder(order));
            }
        }, NETWORK_DELAY);
    }

    @Override
    public boolean filter(IdFilter filter, Order order) {
        return filter.ids().contains(order.id());
    }

    @Override
    public boolean filter(StatusFilter filter, Order order) {
        boolean isProcessed = order.isProcessed();

        switch (filter.status()) {
            case PROCESSED:
                return isProcessed;
            case TO_PROCESS:
                return !isProcessed;
            case ALL:
                return true;
            default:
                throw new IllegalArgumentException();
        }
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

    private static void addOrder(int id, boolean processed) {
        ORDERS_DATA.append(id, orderFactory(id, processed));
    }

    private static Order orderFactory(int id, boolean processed) {
        Map<String, OrderItem> items = new HashMap<>();

        OrderItem item1 = OrderItem.builder()
                .id(id * 10 + 1)
                .code("314159265359")
                .description("Cerveja 1")
                .imageUrl("")
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(id * 10 + 4)
                .code("5012345678900")
                .description("Cerveja 2")
                .imageUrl("")
                .quantity(1)
                .build();

        OrderItem item3 = OrderItem.builder()
                .id(id * 10 + 6)
                .code("7898357410015")
                .description("Cerveja 3")
                .imageUrl("")
                .quantity(3)
                .build();

        items.put("314159265359", item1);
        items.put("5012345678900", item2);
        items.put("7898357410015", item3);

        ShipmentInfo shipmentInfo = ShipmentInfo.builder()
                .shipType("Sedex")
                .address("Avenida Engenheiro Max de Souza, 1293, Coqueiros")
                .build();

        CustomerInfo customerInfo = CustomerInfo.builder()
                .id(1000)
                .name("André Alex Araujo")
                .build();

        return Order.builder()
                .id(id)
                .shipmentInfo(shipmentInfo)
                .customerInfo(customerInfo)
                .items(items)
                .size(2)
                .processedAt(processed ? DateFormatterUtils.getDateHourInstance().now() : null)
                .lastModifiedAt("09/07/2017 às 22:00")
                .build();
    }
}