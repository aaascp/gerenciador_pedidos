package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

/**
 * Created by andre on 30/09/17.
 */

final class OrderDetailsPresenter implements OrderDetailsContract.Presenter {

    private final OrderDetailsContract.View view;

    private final OrdersRepository ordersRepository;

    private final int orderId;
    private final int total;
    private final int current;

    private CodesToProcess codesToProcess;
    private Order order;

    OrderDetailsPresenter(
            OrderDetailsContract.View view,
            OrdersRepository ordersRepository,
            int orderId,
            int total,
            int current) {

        this.view = view;
        this.ordersRepository = ordersRepository;

        this.orderId = orderId;
        this.total = total;
        this.current = current;


        view.setPresenter(this);
        setupToolbar();
    }

    @Override
    public void start() {
        setupOrder();
    }

    @Override
    public void onMenuCreated() {
        if (total == current) {
            view.hideSkipButton();
        }
    }

    @Override
    public void onBackPressed() {
        if (order.isProcessed()) {
            view.finishBack();
        } else {
            view.showBackDialog();
        }
    }

    @Override
    public void onInfoClicked() {
        if (order != null) {
            view.navigateToDetails(order);
        }
    }

    @Override
    public void onProcessorResult(CodesToProcess codesToProcess) {
        this.codesToProcess = codesToProcess;
        view.updateCodesProcessed(codesToProcess);
        checkFinish();
    }

    @Override
    public void onClearClicked() {
        view.showClearDialog();
    }

    @Override
    public void onSkipClicked() {
        view.showSkipDialog();
    }

    @Override
    public void onCloseClicked() {
        view.showCloseDialog();
    }

    @Override
    public void onBackDialogOk() {
        view.finishBack();
    }

    @Override
    public void onSkipDialogOk() {
        view.finishSkip();
    }

    @Override
    public void onClearDialogOk() {
        order = null;
        setupOrder();
    }

    @Override
    public void onCloseDialogOk() {
        view.finishClose();
    }

    @Override
    public void onFabClicked() {
        view.navigateToProcessor(codesToProcess);
    }

    @Override
    public void onFinishClicked() {
        ordersRepository.save(
                order.withProcessedAt(
                        DateFormatterUtils
                                .getDateHourInstance()
                                .now()));

        if (total == 1) {
            view.finishOkUnique();
        } else {
            view.finishOk();
        }
    }

    @Override
    public void onAlreadyProcessedClicked() {
        view.finishClose();
    }

    @Override
    public void onShipTypeClicked() {
        view.navigateToDetails(order);
    }

    private void setupOrder() {
        if (order != null) {
            view.showOrder(order, codesToProcess);
            return;
        }

        ordersRepository.getOrder(
                orderId,
                new RepositoryCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        order = data;
                        codesToProcess = order.codesToProcess();

                        showOrder();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        if (errors != null) {
                            view.showError(errors.get(0));
                        } else {
                            view.showCommunicationError();
                        }
                    }
                }
        );
    }

    private void showOrder() {
        checkFinish();

        view.setShipType(order.shipmentInfo().shipType());
        view.showOrder(order, codesToProcess);
    }

    private void checkFinish() {
        int itemsLeft = codesToProcess.itemsLeft();

        view.hideLabels();

        if (order.isProcessed()) {
            view.hideClearButton();
            view.showAlreadyProcessedLabel();
        } else if (itemsLeft == 0) {
            view.showFinishLabel();
        } else {
            view.showItemsLeftLabel();
            view.setItemsLeft(order.size(), itemsLeft);
        }
    }

    private void setupToolbar() {
        view.setupMenu();
        view.setupTitle(orderId, current, total);

        if (total > 1) {
            view.setupCloseToolbar();
        } else {
            view.setupBackToolbar();
        }
    }
}
