package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

/**
 * Created by andre on 02/10/17.
 */
public class OrdersFactory {

    public static List<Order> getOrders(int size, double processedProbability) {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double rand = Math.random();
            orders.add(orderFactory(1000 + i, rand < processedProbability));
        }

        return orders;
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

    private static Order orderFactory(int id, boolean processed) {
      return createOrder(
              id,
              ShipmentInfo.builder().shipType("Sedex").address("Endereco").build(),
              CustomerInfo.builder().id(1).name("Customer 1").build(),
              2,
              processed ? DateFormatterUtils.getDateHourInstance().now() : null,
              DateFormatterUtils.getDateHourInstance().now());
    }
}
