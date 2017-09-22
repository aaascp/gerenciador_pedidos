package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrderDao;
import br.com.aaascp.gerenciadordepedidos.repository.utils.filter.OrderFilter;

/**
 * Created by andre on 18/09/17.
 */

public class OrdersRepository {

    private OrderDao orderDao;

    public OrdersRepository() {
        this.orderDao = new OrderDao();
    }

    public void getOrder(
            int id,
            RepositoryCallback<Order> callback) {

        callback.onSuccess(orderDao.load(id));
    }

    public void getList(
            OrderFilter filter,
            RepositoryCallback<List<Order>> callback) {

        callback.onSuccess(orderDao.loadAll());
    }

    public void save(Order order) {
        orderDao.save(order);
    }

    public boolean processItem(Order order, String code) {
        return orderDao.processItem(order.id(), code);
    }
}
