package br.com.aaascp.gerenciadordepedidos.repository.filter;

import br.com.aaascp.gerenciadordepedidos.entity.Order;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderVisitor {

    boolean filter(IdFilter filter, Order order);

    boolean filter(StatusFilter filter, Order order);
}
