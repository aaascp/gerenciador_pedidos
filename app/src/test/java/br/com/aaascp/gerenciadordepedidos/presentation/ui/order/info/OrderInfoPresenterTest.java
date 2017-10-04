package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

import static org.mockito.Mockito.verify;

/**
 * Created by andre on 04/10/17.
 */
public class OrderInfoPresenterTest {

    private static final Order ORDER_PROCESSED =
            OrdersFactory.createOrder(
                    1000,
                    ShipmentInfo.builder().shipType("Sedex").address("Endereco").build(),
                    CustomerInfo.builder().id(1).name("Customer 1").build(),
                    2,
                    DateFormatterUtils.getDateHourInstance().now(),
                    DateFormatterUtils.getDateHourInstance().now());

    private static final Order ORDER_NOT_PROCESSED =
            OrdersFactory.createOrder(
                    1000,
                    ShipmentInfo.builder().shipType("Sedex").address("Endereco").build(),
                    CustomerInfo.builder().id(1).name("Customer 1").build(),
                    2,
                    null,
                    DateFormatterUtils.getDateHourInstance().now());

    @Mock
    OrderInfoContract.View view;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private void start(Order order) {
        OrderInfoPresenter presenter = new OrderInfoPresenter(view, order);
        verify(view).setPresenter(presenter);

        presenter.start();
    }

    @Test
    public void start_setupToolbar() {
        start(ORDER_PROCESSED);
        verify(view).setupToolbar(ORDER_PROCESSED.id());
    }

    @Test
    public void start_setCustomerInfo() {
        start(ORDER_PROCESSED);
        verify(view).setCustomerInfo(
                String.valueOf(ORDER_PROCESSED.customerInfo().id()),
                ORDER_PROCESSED.customerInfo().name());
    }

    @Test
    public void start_setShipmentInfo() {
        start(ORDER_PROCESSED);
        verify(view).setShipmentInfo(
                ORDER_PROCESSED.shipmentInfo().shipType(),
                ORDER_PROCESSED.shipmentInfo().address());
    }

    @Test
    public void start_setOrderInfo_processed() {
        start(ORDER_PROCESSED);
        view.setProcessedOrderInfo(
                String.valueOf(ORDER_PROCESSED.size()),
                ORDER_PROCESSED.processedAt(),
                ORDER_PROCESSED.lastModifiedAt());
    }

    @Test
    public void start_setOrderInfo_notProcessed() {
        start(ORDER_NOT_PROCESSED);
        view.setNotProcessedOrderInfo(
                String.valueOf(ORDER_NOT_PROCESSED.size()),
                ORDER_NOT_PROCESSED.lastModifiedAt());
    }


}