package br.com.aaascp.gerenciadordepedidos.repository.dao;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.domain.dto.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.domain.dto.OrderItem;
import br.com.aaascp.gerenciadordepedidos.domain.dto.ShipmentInfo;

/**
 * Created by andre on 22/09/17.
 */

public class OrderDao {

    private static SparseArray<Order> orders = new SparseArray<>();

    public void save(Order order) {
        orders.put(order.id(), order);
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

    public List<Order> loadAll() {
        List<Order> allOrders = new ArrayList<>(orders.size());

        for (int i = 0; i < orders.size(); i++) {
            allOrders.add(orders.valueAt(i));
        }

        return allOrders;
    }

    public static void initialize() {
        orders.append(1000, orderFactory(1000));
        orders.append(1001, orderFactory(1001));
        orders.append(1002, orderFactory(1002));
        orders.append(1003, orderFactory(1003));
    }


    private static Order orderFactory(int id) {
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
                .code("127035240760")
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

        OrderItem item4 = OrderItem.builder()
                .id(id * 10 + 7)
                .code("9789563530766")
                .description("Cerveja 4")
                .imageUrl("")
                .quantity(2)
                .build();


        items.put("314159265359", item1);
        items.put("127035240760", item2);
        items.put("7898357410015", item3);
        items.put("9789563530766", item4);

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