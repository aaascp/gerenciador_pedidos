package br.com.aaascp.gerenciadordepedidos.repository.dao;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.callback.DataSourceCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderVisitor;

/**
 * Created by andre on 27/09/17.
 */

public interface OrdersDataSource extends OrderVisitor {

    void save(Order order);

    void load(OrderFilterList filterList, DataSourceCallback<List<Order>> callback);

    void load(int id, DataSourceCallback<Order> callback);
}
