package br.com.aaascp.gerenciadordepedidos.repository.filters;

import br.com.aaascp.gerenciadordepedidos.models.Order;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderFilter {

     boolean accept(OrderVisitor visitor, Order order);
}
