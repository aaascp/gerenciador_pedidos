package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.models.Order;
import br.com.aaascp.gerenciadordepedidos.models.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrderDao;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrderDaoMemory;

/**
 * Created by andre on 18/09/17.
 */

public class OrdersRepository {

    private OrderDao orderDao;

    public OrdersRepository() {
        this.orderDao = new OrderDaoMemory();
    }

    public void getOrder(
            int id,
            RepositoryCallback<Order> callback) {

        callback.onSuccess(orderDao.load(id));
    }

    public void getList(
            OrderFilterList filterList,
            RepositoryCallback<List<Order>> callback) {

        callback.onSuccess(
                orderDao.load(filterList));
    }

    public void save(Order order) {
        orderDao.save(order);
    }
}
