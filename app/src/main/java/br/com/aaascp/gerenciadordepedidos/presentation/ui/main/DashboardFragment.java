package br.com.aaascp.gerenciadordepedidos.presentation.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.custom.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseFragment;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 18/09/17.
 */

public final class DashboardFragment extends BaseFragment {

    @BindView(R.id.dashboard_to_process)
    ValueLabelView toProcessButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupToProcessButton();
    }

    private void setupToProcessButton() {
        OrdersRepository orderRepository = new OrdersRepository();
        StatusFilter filter = StatusFilter.create(StatusFilter.Status.TO_PROCESS);

        orderRepository.getList(
                filter,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> data) {
                        Log.d("Andre", data.toString());
                        Log.d("Andre", String.valueOf(data.size()));
                        toProcessButton.setValue(
                                String.valueOf(data.size()));
                    }

                    @Override
                    public void onError(List<String> errors) {
                        toProcessButton.setValue("?");
                    }
                });

    }

    private void navigateToOrdersList(OrderFilter filter, boolean processAll) {
        List<OrderFilter> filters = new ArrayList<>();
        filters.add(filter);

        OrdersListActivity.startForContext(
                getContext(),
                OrderFilterList.create(filters),
                processAll);
    }

    private void getIds() {
        DialogUtils.showGetIntValues(
                getContext(),
                getString(R.string.dashboard_orders_find_dialog_title),
                getString(R.string.dashboard_orders_find_dialog_message),
                new DialogUtils.IntValuesListener() {
                    @Override
                    public void onValues(HashSet<Integer> values) {
                        navigateToOrdersList(
                                IdFilter.create(values),
                                values.size() > 1);
                    }

                    @Override
                    public void onError() {
                        showErrorGettingIds();
                    }
                });
    }

    private void showErrorGettingIds() {
        DialogUtils.showError(
                getContext(),
                getString(R.string.dashboard_orders_find_dialog_error_title),
                getString(R.string.dashboard_orders_find_dialog_erro_message));
    }

    @OnClick(R.id.dashboard_to_process)
    void toProcessButton() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.TO_PROCESS),
                true);
    }

    @OnClick(R.id.dashboard_processed)
    void processedButton() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.PROCESSED),
                false);
    }

    @OnClick(R.id.dashboard_all)
    void allButton() {
        navigateToOrdersList(
                StatusFilter.create(StatusFilter.Status.ALL),
                false);
    }

    @OnClick(R.id.dashboard_find)
    void findButton() {
        getIds();
    }
}
