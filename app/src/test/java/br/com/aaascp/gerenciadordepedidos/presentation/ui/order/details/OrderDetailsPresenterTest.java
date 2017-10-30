package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by andre on 05/10/17.
 */

public class OrderDetailsPresenterTest {

    private static final Order ORDER_PROCESSED =
            OrdersFactory.createOrder(
                    1000,
                    ShipmentInfo.builder()
                            .shipType("Sedex")
                            .address("Address")
                            .build(),
                    CustomerInfo.builder()
                            .id(1)
                            .name("Customer")
                            .build(),
                    2,
                    DateFormatterUtils.getDateHourInstance().now(),
                    DateFormatterUtils.getDateHourInstance().now());


    private static final Order ORDER_NOT_PROCESSED =
            OrdersFactory.createOrder(
                    2000,
                    ShipmentInfo.builder()
                            .shipType("Sedex")
                            .address("Address")
                            .build(),
                    CustomerInfo.builder()
                            .id(1)
                            .name("Customer")
                            .build(),
                    2,
                    "",
                    DateFormatterUtils.getDateHourInstance().now());

    private static final Order ORDER_NOT_PROCESSED_FINISH =
            OrdersFactory.createOrder(
                    3000,
                    ShipmentInfo.builder()
                            .shipType("Sedex")
                            .address("Address").build(),
                    CustomerInfo.builder()
                            .id(1)
                            .name("Customer")
                            .build(),
                    0,
                    "",
                    DateFormatterUtils.getDateHourInstance().now());


    @Mock
    private OrderDetailsContract.View view;

    @Mock
    private OrdersRepository ordersRepository;

    @Captor
    private ArgumentCaptor<RepositoryCallback<Order>>
            repositoryCallbackArgumentCaptor;

    private OrderDetailsPresenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private void init(Order order, int total, int current) {
        presenter = new OrderDetailsPresenter(
                view,
                ordersRepository,
                order.id(),
                total,
                current);

        verify(view).setPresenter(presenter);
        verify(view).setupMenu();

        presenter.start();

        verify(ordersRepository).getOrder(
                anyInt(),
                repositoryCallbackArgumentCaptor.capture());

        repositoryCallbackArgumentCaptor.getValue().onSuccess(order);
    }

    private void init() {
        init(ORDER_PROCESSED, 1, 1);
    }

    private void init(int total, int current) {
        init(ORDER_PROCESSED, total, current);
    }

    private void init(Order order) {
        init(order, 1, 1);
    }

    @Test
    public void initForUnique() throws Exception {
        int total = 1;
        int current = 1;

        init(ORDER_PROCESSED, total, current);

        verify(view).setupTitle(ORDER_PROCESSED.id(), current, total);
        verify(view).setupBackToolbar();
    }

    @Test
    public void initProcessAll() throws Exception {
        int total = 3;
        int current = 2;

        init(ORDER_PROCESSED, total, current);

        verify(view).setupTitle(ORDER_PROCESSED.id(), current, total);
        verify(view).setupCloseToolbar();
    }

    @Test
    public void start_orderProcessed() throws Exception {
        init(ORDER_PROCESSED, 1, 1);

        verify(view).setShipType(ORDER_PROCESSED.shipmentInfo().shipType());
        verify(view).showOrder(ORDER_PROCESSED, ORDER_PROCESSED.codesToProcess());
        verify(view).hideClearButton();
        verify(view).showAlreadyProcessedLabel();
    }

    @Test
    public void start_orderNotProcessed_finish() throws Exception {
        init(ORDER_NOT_PROCESSED_FINISH, 1, 1);

        verify(view).setShipType(ORDER_NOT_PROCESSED_FINISH.shipmentInfo().shipType());
        verify(view).showOrder(
                ORDER_NOT_PROCESSED_FINISH,
                ORDER_NOT_PROCESSED_FINISH.codesToProcess());
        verify(view).showFinishLabel();
    }

    @Test
    public void start_orderNotProcessed_itemsLeft() throws Exception {
        init(ORDER_NOT_PROCESSED, 1, 1);

        verify(view).setShipType(ORDER_NOT_PROCESSED.shipmentInfo().shipType());
        verify(view).showOrder(
                ORDER_NOT_PROCESSED,
                ORDER_NOT_PROCESSED.codesToProcess());
        verify(view).showItemsLeftLabel();
        verify(view).setItemsLeft(
                ORDER_NOT_PROCESSED.size(),
                ORDER_NOT_PROCESSED.codesToProcess().itemsLeft());
    }

    @Test
    public void start_error() throws Exception {
        init();

        String error = "Error";
        repositoryCallbackArgumentCaptor.getValue()
                .onError(Collections.singletonList(error));

        verify(view).showError(error);
    }

    @Test
    public void start_communicationError() throws Exception {
        init();

        repositoryCallbackArgumentCaptor.getValue().onError(null);

        verify(view).showCommunicationError();
    }

    @Test
    public void onMenuCreated_finish() throws Exception {
        init(2, 2);

        presenter.onMenuCreated();

        verify(view).hideSkipButton();
    }

    @Test
    public void onMenuCreated_itemsLeft() throws Exception {
        init(3, 2);

        presenter.onMenuCreated();

        verify(view, never()).hideSkipButton();
    }

    @Test
    public void onBackPressed_orderProcessed() throws Exception {
        init(ORDER_PROCESSED);

        presenter.onBackPressed();

        verify(view).finishBack();
        verify(view, never()).showBackDialog();
    }

    @Test
    public void onBackPressed_orderNotProcessed() throws Exception {
        init(ORDER_NOT_PROCESSED);

        presenter.onBackPressed();

        verify(view).showBackDialog();
        verify(view, never()).finishBack();
    }

    @Test
    public void onInfoClicked() throws Exception {
        init(ORDER_PROCESSED);

        presenter.onInfoClicked();

        verify(view).navigateToDetails(ORDER_PROCESSED);
    }

    @Test
    public void onProcessorResult_itemsLeft() throws Exception {
        init(ORDER_NOT_PROCESSED);

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_PROCESSED.items().keySet()) {
            codes.put(code, 1);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_PROCESSED.id());

        presenter.onProcessorResult(newCodesToProcess);

        verify(view).updateCodesProcessed(newCodesToProcess);
        verify(view, times(2)).showItemsLeftLabel();
    }

    @Test
    public void onProcessorResult_finish() throws Exception {
        init(ORDER_NOT_PROCESSED);

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_PROCESSED.items().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_PROCESSED.id());

        presenter.onProcessorResult(newCodesToProcess);

        verify(view).updateCodesProcessed(newCodesToProcess);
        verify(view).showFinishLabel();
    }

    @Test
    public void onProcessorResult_alreadyProcessed() throws Exception {
        init(ORDER_PROCESSED);

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_PROCESSED.items().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_PROCESSED.id());

        presenter.onProcessorResult(newCodesToProcess);

        verify(view).updateCodesProcessed(newCodesToProcess);
        verify(view, times(2)).showAlreadyProcessedLabel();
    }

    @Test
    public void onClearClicked() throws Exception {
        init();

        presenter.onClearClicked();

        verify(view).showClearDialog();
    }

    @Test
    public void onSkipClicked() throws Exception {
        init();

        presenter.onSkipClicked();

        verify(view).showSkipDialog();
    }

    @Test
    public void onCloseClicked() throws Exception {
        init();

        presenter.onCloseClicked();

        verify(view).showCloseDialog();
    }

    @Test
    public void onBackDialogOk() throws Exception {
        init();

        presenter.onBackDialogOk();

        verify(view).finishBack();
    }

    @Test
    public void onSkipDialogOk() throws Exception {
        init();

        presenter.onSkipDialogOk();

        verify(view).finishSkip();
    }

    @Test
    public void onClearDialogOk() throws Exception {
        init();

        presenter.onClearDialogOk();

        verify(ordersRepository, times(2)).getOrder(
                anyInt(),
                any(RepositoryCallback.class));
    }

    @Test
    public void onCloseDialogOk() throws Exception {
        init();

        presenter.onCloseDialogOk();

        verify(view).finishClose();
    }

    @Test
    public void onFabClicked_checkPermission() throws Exception {
        init(ORDER_NOT_PROCESSED);

        presenter.onFabClicked();

        verify(view).checkPermissionForCamera();
    }

    @Test
    public void onPermissionEnabled_nothingProcessed() throws Exception {
        init(ORDER_NOT_PROCESSED);

        presenter.onCameraPermissionEnabled();

        verify(view).navigateToProcessor(ORDER_NOT_PROCESSED.codesToProcess());
    }

    @Test
    public void onPermissionEnabled_codesProcessed() throws Exception {
        init(ORDER_NOT_PROCESSED);

        String code = ORDER_NOT_PROCESSED.items().keySet().iterator().next();
        Map<String, Integer> codes = new HashMap<>(2);
        codes.put(code, ORDER_NOT_PROCESSED.items().get(code).quantity() - 1);
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_PROCESSED.id());

        presenter.onProcessorResult(newCodesToProcess);
        presenter.onCameraPermissionEnabled();

        verify(view).navigateToProcessor(newCodesToProcess);
    }

    @Test
    public void onFinishClicked() throws Exception {
        init(ORDER_NOT_PROCESSED);

        presenter.onFinishClicked();

        verify(ordersRepository).save(
                ORDER_NOT_PROCESSED.withProcessedAt(
                        DateFormatterUtils.getDateHourInstance().now()));
    }

    @Test
    public void onFinishClicked_unique() throws Exception {
        init(ORDER_NOT_PROCESSED, 1, 1);

        presenter.onFinishClicked();

        verify(view).finishOkUnique();
        verify(view, never()).finishOk();
    }

    @Test
    public void onFinishClicked_processAll() throws Exception {
        init(ORDER_NOT_PROCESSED, 3, 1);

        presenter.onFinishClicked();

        verify(view).finishOk();
        verify(view, never()).finishOkUnique();
    }

    @Test
    public void onAlreadyProcessedClicked() throws Exception {
        init(ORDER_PROCESSED);

        presenter.onAlreadyProcessedClicked();

        verify(view).finishClose();
    }

    @Test
    public void onShipTypeClicked() throws Exception {
        init(ORDER_PROCESSED);

        presenter.onShipTypeClicked();

        verify(view).navigateToDetails(ORDER_PROCESSED);
    }

    @Test
    public void checkFinish_alreadyProcessed() throws Exception {
        init(ORDER_PROCESSED);

        verify(view).hideLabels();
        verify(view).hideClearButton();
        verify(view).showAlreadyProcessedLabel();
        verify(view, never()).showFinishLabel();
        verify(view, never()).showItemsLeftLabel();
        verify(view, never()).setItemsLeft(anyInt(), anyInt());
    }

    @Test
    public void checkFinish_notProcessed() throws Exception {
        init(ORDER_NOT_PROCESSED);

        verify(view).hideLabels();
        verify(view).showItemsLeftLabel();
        verify(view).setItemsLeft(
                ORDER_NOT_PROCESSED.size(),
                ORDER_NOT_PROCESSED.codesToProcess().itemsLeft());
        verify(view, never()).hideClearButton();
        verify(view, never()).showAlreadyProcessedLabel();
        verify(view, never()).showFinishLabel();
    }

    @Test
    public void checkFinish_notProcessed_finish() throws Exception {
        init(ORDER_NOT_PROCESSED_FINISH);

        verify(view).hideLabels();
        verify(view).showFinishLabel();
        verify(view, never()).setItemsLeft(anyInt(), anyInt());
        verify(view, never()).hideClearButton();
        verify(view, never()).showAlreadyProcessedLabel();
        verify(view, never()).showItemsLeftLabel();
    }
}