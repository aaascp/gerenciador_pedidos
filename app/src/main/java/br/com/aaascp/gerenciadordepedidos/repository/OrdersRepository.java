package br.com.aaascp.gerenciadordepedidos.repository;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, List<OrderItem>> items = new HashMap<>();

        OrderItem item1 = OrderItem.builder()
                .code(1234)
                .description("Cerveja 1")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item2 = OrderItem.builder()
                .code(1234)
                .description("Cerveja 1")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item3 = OrderItem.builder()
                .code(1234)
                .description("Cerveja 1")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item4 = OrderItem.builder()
                .code(2345)
                .description("Cerveja 2")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item5 = OrderItem.builder()
                .code(2345)
                .description("Cerveja 2")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item6 = OrderItem.builder()
                .code(3456)
                .description("Cerveja 3")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item7 = OrderItem.builder()
                .code(4567)
                .description("Cerveja 4")
                .imageUrl("")
                .processedAt(null)
                .build();

        OrderItem item8 = OrderItem.builder()
                .code(4567)
                .description("Cerveja 4")
                .imageUrl("")
                .processedAt(null)
                .build();

        items.put("1234", Arrays.asList(item1, item2, item3));
        items.put("2345", Arrays.asList(item4, item5));
        items.put("3456", Collections.singletonList(item6));
        items.put("4567", Arrays.asList(item7, item8));

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
                .size(8)
                .processedAt(null)
                .lastModifiedAt("09/07/2017 às 22:00")
                .build();
    }
}
