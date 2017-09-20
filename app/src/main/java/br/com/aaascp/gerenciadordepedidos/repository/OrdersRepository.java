package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.domain.dto.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.domain.dto.OrderItem;
import br.com.aaascp.gerenciadordepedidos.domain.dto.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.utils.filter.OrderFilter;

/**
 * Created by andre on 18/09/17.
 */

public class OrdersRepository {

    public static void getOrder(
            int id,
            RepositoryCallback<Order> callback) {

        callback.onSuccess(orderFactory(1000));
    }

    public static void getList(
            OrderFilter filter,
            RepositoryCallback<List<Order>> callback) {

        List<Order> orders = new ArrayList<>();

        Order order1 = orderFactory(1000);
        Order order2 = orderFactory(1001);
        Order order3 = orderFactory(1002);
        Order order4 = orderFactory(1003);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        callback.onSuccess(orders);
    }

    private static Order orderFactory(int id) {
        List<OrderItem> items = new ArrayList<>();

        OrderItem item1 = OrderItem.builder()
                .cod(1234)
                .description("Cerveja 1")
                .imageUrl("")
                .quantity(5)
                .build();

        OrderItem item2 = OrderItem.builder()
                .cod(2345)
                .description("Cerveja 2")
                .imageUrl("")
                .quantity(3)
                .build();

        OrderItem item3 = OrderItem.builder()
                .cod(3456)
                .description("Cerveja 3")
                .imageUrl("")
                .quantity(4)
                .build();

        OrderItem item4 = OrderItem.builder()
                .cod(4567)
                .description("Cerveja 4")
                .imageUrl("")
                .quantity(2)
                .build();

        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

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
                .processedAt("09/07/2017 às 22:00")
                .lastModifiedAt("09/07/2017 às 22:00")
                .build();
    }
}
