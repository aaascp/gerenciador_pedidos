package br.com.aaascp.gerenciadordepedidos.presentation.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.custom_views.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseFragment;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.repository.utils.filter.OrderFilter;
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

    @OnClick(R.id.dashboard_to_process)
    void toProcessButton() {
        OrderFilter filter =
                new OrderFilter.Builder()
                        .status(OrderFilter.Status.TO_PROCESS)
                        .build();

        OrdersListActivity.startForContext(getContext(), filter);
    }

    @OnClick(R.id.dashboard_processed)
    void processedButton() {

    }

    @OnClick(R.id.dashboard_all)
    void _Button() {

    }

    @OnClick(R.id.dashboard_find)
    void findButton() {

    }

}
