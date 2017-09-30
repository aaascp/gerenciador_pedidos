package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;

/**
 * Created by andre on 30/09/17.
 */

interface OrdersListContract {
    interface View extends BaseView<Presenter> {
        void setupToolbar();

        void showOrdersList(List<Order> orders);

        void showEmptyList();

        void showError(String error);

        void showCommunicationError();

        void showFab();

        void hideFab();

        void navigateToOrderDetails(int orderId, int position, int total);
    }

    interface Presenter extends BasePresenter {
        void onResultNext();

        void onResultClose();

        void onOrderClicked(int orderId);

        void onFabCLick();

    }
}
