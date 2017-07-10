package br.com.aaascp.gerenciadordepedidos.domain;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.domain.callback.DomainCallback;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.repository.remote.OrdersRemoteRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryError;

/**
 * Created by andre on 10/07/17.
 */

public final class OrdersDomain {

    public static void getOrdersList(final DomainCallback<List<Order>> callback) {
        OrdersRemoteRepository.getOrdersList(
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> data) {
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onError(RepositoryError error) {
                        callback.onError(error.getErrors());
                    }
                });
    }

    public static void getOrder(int id) {

    }
}
