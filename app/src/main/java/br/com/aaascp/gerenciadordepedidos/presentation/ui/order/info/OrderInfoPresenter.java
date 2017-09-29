package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info;

import br.com.aaascp.gerenciadordepedidos.entity.Order;

/**
 * Created by andre on 29/09/17.
 */
final class OrderInfoPresenter implements OrderInfoContract.Presenter {

    private final OrderInfoContract.View view;
    private final Order order;

    OrderInfoPresenter(OrderInfoContract.View view, Order order) {

        this.view = view;
        this.order = order;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        setupInfo();
    }

    private void setupInfo() {
        view.setupToolbar(order.id());
        setOrderInfo();

        view.setCustomerInfo(
                String.valueOf(
                        order.customerInfo().id()),
                order.customerInfo().name());

        view.setShipmentInfo(
                order.shipmentInfo().shipType(),
                order.shipmentInfo().address());
    }

    private void setOrderInfo() {
        String size = String.valueOf(order.size());
        String lastModification = order.lastModifiedAt();

        if (order.isProcessed()) {
            view.setProcessedOrderInfo(
                    size,
                    order.processedAt(),
                    lastModification);
        } else {
            view.setNotProcessedOrderInfo(size, lastModification);
        }
    }
}
