package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;

/**
 * Created by andre on 18/09/17.
 */

public interface OrdersRepository {

    void getOrder(
            int id,
            final RepositoryCallback<Order> callback);

    void getList(
            OrderFilter filter,
            RepositoryCallback<List<Order>> callback);

    void getList(
            OrderFilterList filterList,
            final RepositoryCallback<List<Order>> callback);

    void save(Order order);
}
