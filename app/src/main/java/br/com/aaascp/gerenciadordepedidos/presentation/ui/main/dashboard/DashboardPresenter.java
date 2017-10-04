package br.com.aaascp.gerenciadordepedidos.presentation.ui.main.dashboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.Inject;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;

/**
 * Created by andre on 28/09/17.
 */

final class DashboardPresenter implements DashboardContract.Presenter {

    private final DashboardContract.View view;
    private final OrdersRepository ordersRepository;

    DashboardPresenter(
            DashboardContract.View view,
            OrdersRepository ordersRepository) {

        this.view = view;
        this.ordersRepository = ordersRepository;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        setupToProcessButton();
    }

    private void setupToProcessButton() {
        StatusFilter filter = StatusFilter.create(StatusFilter.Status.TO_PROCESS);

        ordersRepository.getList(
                filter,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> data) {
                        view.setToProcessCount(
                                String.valueOf(data.size()));
                    }

                    @Override
                    public void onError(List<String> errors) {
                        view.setToProcessCount("?");
                    }
                });

    }

    @Override
    public void onToProcessButtonClicked() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.TO_PROCESS),
                true);
    }

    @Override
    public void onProcessedButtonClicked() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.PROCESSED),
                false);
    }

    @Override
    public void onAllButtonClicked() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.ALL),
                false);
    }

    @Override
    public void onFindButtonClicked() {
        view.showGetIdsDialog(new DialogUtils.IntValuesListener() {
            @Override
            public void onValues(HashSet<Integer> values) {
                navigateToOrdersList(
                        IdFilter.create(values),
                        values.size() > 1);
            }

            @Override
            public void onError() {
                view.showErrorGettingIds();
            }
        });
    }

    private void navigateToOrdersList(OrderFilter filter, boolean showProcessAll) {
        List<OrderFilter> filters = new ArrayList<>();
        filters.add(filter);

        view.navigateToOrdersList(
                OrderFilterList.create(filters),
                showProcessAll);
    }
}