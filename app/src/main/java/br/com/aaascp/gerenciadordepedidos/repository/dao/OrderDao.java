package br.com.aaascp.gerenciadordepedidos.repository.dao;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.models.Order;
import br.com.aaascp.gerenciadordepedidos.models.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.filters.OrderVisitor;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderDao extends OrderVisitor {

    void save(Order order);

    List<Order> load(OrderFilterList filterList);

    Order load(int id);
}
