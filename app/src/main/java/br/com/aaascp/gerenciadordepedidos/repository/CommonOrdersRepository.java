package br.com.aaascp.gerenciadordepedidos.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.callback.DataSourceCallback;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersDataSource;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;

/**
 * Created by andre on 18/09/17.
 */

public class CommonOrdersRepository implements OrdersRepository {

    private OrdersDataSource ordersDataSource;

    public CommonOrdersRepository(OrdersDataSource ordersDataSource) {
        this.ordersDataSource = ordersDataSource;
    }

    @Override
    public void getOrder(
            int id,
            final RepositoryCallback<Order> callback) {

        ordersDataSource.load(
                id,
                new DataSourceCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        callback.onError(errors);
                    }
                });
    }

    @Override
    public void getList(
            OrderFilter filter,
            RepositoryCallback<List<Order>> callback) {

        List<OrderFilter> filterList = new ArrayList<>(1);
        filterList.add(filter);

        getList(OrderFilterList.create(filterList), callback);
    }

    @Override
    public void getList(
            OrderFilterList filterList,
            final RepositoryCallback<List<Order>> callback) {

        ordersDataSource.load(
                filterList,
                new DataSourceCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> data) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        callback.onError(errors);
                    }
                });
    }

    @Override
    public void save(Order order) {
        ordersDataSource.save(order);
    }
}
