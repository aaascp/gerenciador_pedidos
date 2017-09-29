package br.com.aaascp.gerenciadordepedidos.repository.filter;

import br.com.aaascp.gerenciadordepedidos.entity.Order;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderFilter {

     boolean accept(OrderVisitor visitor, Order order);
}
