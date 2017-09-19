package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;

/**
 * Created by andre on 18/09/17.
 */

public class OrdersRepository {

    public static void getList(RepositoryCallback<List<Order>> callback) {
        List<Order> orders = new ArrayList<>();

        Order order1 = Order.builder()
                .id(1000)
                .shipType("Sedex")
                .itemsCount(5)
                .lastModifiedAt("09/07/2017 às 22:00")
                .build();

        Order order2 = Order.builder()
                .id(1002)
                .shipType("Sedex")
                .itemsCount(10)
                .lastModifiedAt("11/07/2017 às 10:23")
                .build();

        Order order3 = Order.builder()
                .id(1003)
                .shipType("PAC")
                .itemsCount(2)
                .lastModifiedAt("16/07/2017 às 13:54")
                .build();

        Order order4 = Order.builder()
                .id(1004)
                .shipType("Transportadora")
                .itemsCount(1)
                .lastModifiedAt("25/07/2017 às 21:30")
                .build();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        callback.onSuccess(orders);
    }
}
