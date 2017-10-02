package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;

/**
 * Created by andre on 30/09/17.
 */

final class OrdersListPresenter implements OrdersListContract.Presenter {

    private final OrdersListContract.View view;
    private final OrdersRepository ordersRepository;
    private final OrderFilterList filters;
    private final boolean processAll;

    private List<Order> orders;
    private int current;

    OrdersListPresenter(
            OrdersListContract.View view,
            OrderFilterList filters,
            OrdersRepository ordersRepository,
            boolean processAll) {

        this.view = view;
        this.filters = filters;
        this.ordersRepository = ordersRepository;
        this.processAll = processAll;

        view.setPresenter(this);
        setupFab();
    }

    @Override
    public void start() {
        setupOrders();
    }

    @Override
    public void onResultNext() {
        ++current;
        processNext();
    }

    @Override
    public void onResultClose() {
        current = -1;
        processNext();
    }

    @Override
    public void onOrderClicked(int orderId) {
        view.navigateToOrderDetails(orderId, 1, 1);
    }

    @Override
    public void onFabCLick() {
        processNext();
    }

    private void setupOrders() {
        if (orders != null) {
            return;
        }

        current = 0;

        ordersRepository.getList(
                filters,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> result) {
                        orders = result;
                        if (result.size() == 0) {
                            view.showEmptyList();
                        } else {
                            view.showOrdersList(orders);
                        }
                    }

                    @Override
                    public void onError(List<String> errors) {
                        if (errors != null) {
                            view.showError(errors.get(0));
                        } else {
                            view.showCommunicationError();
                        }
                    }
                });
    }

    private void processNext() {
        if (current >= orders.size() ||
                current < 0) {

            orders = null;
            setupOrders();
            return;
        }

        view.navigateToOrderDetails(
                orders.get(current).id(),
                current + 1,
                orders.size());
    }

    private void setupFab() {
        if (processAll) {
            view.showFab();
        } else {
            view.hideFab();
        }
    }
}
