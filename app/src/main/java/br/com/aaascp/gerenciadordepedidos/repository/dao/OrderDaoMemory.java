package br.com.aaascp.gerenciadordepedidos.repository.dao;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.models.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.models.Order;
import br.com.aaascp.gerenciadordepedidos.models.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.models.OrderItem;
import br.com.aaascp.gerenciadordepedidos.models.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.repository.filters.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filters.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filters.StatusFilter;
import br.com.aaascp.gerenciadordepedidos.utils.DateFormatterUtils;
import br.com.aaascp.gerenciadordepedidos.utils.StringUtils;

/**
 * Created by andre on 22/09/17.
 */

public final class OrderDaoMemory implements OrderDao {

    private static SparseArray<Order> orders = new SparseArray<>();

    public void save(Order order) {
        orders.put(order.id(), order);
    }

    public List<Order> load(OrderFilterList filterList) {
        List<OrderFilter> filters = filterList.filters();
        List<Order> filteredOrders = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.valueAt(i);
            boolean isFiltered;

            for (OrderFilter filter : filters) {
                isFiltered = filter.accept(this, order);

                if (isFiltered) {
                    filteredOrders.add(load(order.id()));
                    break;
                }
            }
        }

        return filteredOrders;
    }

    public Order load(int id) {
        Order order = orders.get(id);

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

    public static void initialize() {
        orders.append(1000, orderFactory(1000, false));
        orders.append(1001, orderFactory(1001, true));
        orders.append(1002, orderFactory(1002, false));
        orders.append(1003, orderFactory(1003, false));
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
//        items.put("5012345678900", item2);
//        items.put("7898357410015", item3);

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