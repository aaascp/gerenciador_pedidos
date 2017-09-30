package br.com.aaascp.gerenciadordepedidos.presentation.ui.main.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.custom.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseFragment;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 18/09/17.
 */

public final class DashboardFragment extends BaseFragment implements DashboardContract.View {

    @BindView(R.id.dashboard_to_process)
    ValueLabelView toProcessButton;

    private DashboardContract.Presenter presenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new DashboardPresenter(this);
    }

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

        presenter.start();
    }

    @Override
    public void setToProcessCount(String count) {
        toProcessButton.setValue(count);
    }

    @Override
    public void navigateToOrdersList(OrderFilterList filters, boolean processAll) {
        OrdersListActivity.startForContext(
                getContext(),
                filters,
                processAll);
    }

    @Override
    public void showGetIdsDialog(DialogUtils.IntValuesListener listener) {
        DialogUtils.showGetIntValues(
                getContext(),
                R.string.dashboard_orders_find_dialog_title,
                R.string.dashboard_orders_find_dialog_message,
                listener);

    }

    @Override
    public void showErrorGettingIds() {
        DialogUtils.showError(
                getContext(),
                getString(R.string.dashboard_orders_find_dialog_error_title),
                getString(R.string.dashboard_orders_find_dialog_erro_message));
    }

    @OnClick(R.id.dashboard_to_process)
    void toProcessButton() {
        presenter.onToProcessButtonClicked();
    }

    @OnClick(R.id.dashboard_processed)
    void processedButton() {
        presenter.onProcessedButtonClicked();
    }

    @OnClick(R.id.dashboard_all)
    void allButton() {
        presenter.onAllButtonClicked();
    }

    @OnClick(R.id.dashboard_find)
    void findButton() {
        presenter.onFindButtonClicked();
    }

    @Override
    public void setPresenter(@NonNull DashboardContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
