package br.com.aaascp.gerenciadordepedidos.presentation.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.models.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.custom_views.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseFragment;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.repository.filters.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filters.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filters.StatusFilter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 18/09/17.
 */

public class DashboardFragment extends BaseFragment {

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toProcessButton.setValue("10");
    }

    private void navigateToOrdersList(OrderFilter filter, boolean processAll) {
        List<OrderFilter> filters = new ArrayList<>();
        filters.add(filter);

        OrdersListActivity.startForContext(
                getContext(),
                OrderFilterList.create(filters),
                processAll);
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
        HashSet<Integer> ids = new HashSet<>();
        ids.add(1000);

        navigateToOrdersList(
                IdFilter.create(ids),
                false);
    }
}
