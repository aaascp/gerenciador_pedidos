package br.com.aaascp.gerenciadordepedidos.repository.dao;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderVisitor;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderDao extends OrderVisitor {

    void save(Order order);

    List<Order> load(OrderFilterList filterList);

    Order load(int id);
}
