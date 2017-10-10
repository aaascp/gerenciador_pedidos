package br.com.aaascp.gerenciadordepedidos;

import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersDataSource;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersFakeDataSource;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersMemoryDataSource;

/**
 * Created by andre on 03/10/17.
 */

public class Inject {

    public static OrdersRepository provideOrdersRepository() {
        OrdersDataSource ordersDataSource = new OrdersFakeDataSource();
        return new OrdersRepository(ordersDataSource);
    }
}
