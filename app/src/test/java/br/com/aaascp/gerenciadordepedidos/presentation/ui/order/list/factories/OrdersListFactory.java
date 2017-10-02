package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.factories;

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
public class OrdersListFactory {

    public static List<Order> getOrders(int size, double processedProbability) {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double rand = Math.random();
            orders.add(orderFactory(1000 + i, rand < processedProbability));
        }

        return orders;
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
