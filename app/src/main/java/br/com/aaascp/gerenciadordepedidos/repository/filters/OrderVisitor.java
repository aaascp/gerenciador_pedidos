package br.com.aaascp.gerenciadordepedidos.repository.filters;

import br.com.aaascp.gerenciadordepedidos.models.Order;

/**
 * Created by andre on 27/09/17.
 */

public interface OrderVisitor {

    boolean filter(IdFilter filter, Order order);

    boolean filter(StatusFilter filter, Order order);
}
