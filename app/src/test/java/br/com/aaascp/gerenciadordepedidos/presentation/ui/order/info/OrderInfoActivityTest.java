package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;


/**
 * Created by andre on 04/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrderInfoActivityTest {

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
    OrderInfoContract.Presenter presenter;

    private OrderInfoActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        activity = Robolectric.setupActivity(OrderInfoActivity.class);
        activity.setPresenter(presenter);
    }

    @Test
    public void start_startPresenter() {
        activity.onStart();

        verify(presenter).start();
    }

    @Test
    public void setupToolbar() {
        activity.setupToolbar(ORDER_PROCESSED.id());

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.order_info_toolbar);
        assertThat(toolbar.getTitle().toString(), containsString(String.valueOf(ORDER_PROCESSED.id())));
    }

    @Test
    public void setProcessedOrderInfo() {
        activity.setProcessedOrderInfo(
                String.valueOf(ORDER_PROCESSED.id()),
                ORDER_PROCESSED.processedAt(),
                ORDER_PROCESSED.lastModifiedAt());

        TextView orderSize = (TextView) activity.findViewById(R.id.info_order_size_value);
        TextView orderProcessedAt= (TextView) activity.findViewById(R.id.info_order_processed_at_value);
        TextView orderLastModification = (TextView) activity.findViewById(R.id.info_order_last_modification_value);

        assertEquals(orderSize.getText().toString(), String.valueOf(ORDER_PROCESSED.id()));
        assertEquals(orderProcessedAt.getText().toString(), ORDER_PROCESSED.processedAt());
        assertEquals(orderLastModification.getText().toString(), ORDER_PROCESSED.lastModifiedAt());
    }

    @Test
    public void setNotProcessedOrderInfo() {
        activity.setNotProcessedOrderInfo(
                String.valueOf(ORDER_NOT_PROCESSED.id()),
                ORDER_NOT_PROCESSED.lastModifiedAt());

        TextView orderSize = (TextView) activity.findViewById(R.id.info_order_size_value);
        TextView orderProcessedAt= (TextView) activity.findViewById(R.id.info_order_processed_at_value);
        TextView orderLastModification = (TextView) activity.findViewById(R.id.info_order_last_modification_value);

        assertEquals(orderSize.getText().toString(), String.valueOf(ORDER_PROCESSED.id()));
        assertEquals(orderProcessedAt.getText().toString(), activity.getString(R.string.info_order_processed_at_empty));
        assertEquals(orderLastModification.getText().toString(), ORDER_PROCESSED.lastModifiedAt());
    }

    @Test
    public void setShipmentInfo() {
        activity.setShipmentInfo(
                ORDER_PROCESSED.shipmentInfo().shipType(),
                ORDER_PROCESSED.shipmentInfo().address());

        TextView shipType = (TextView) activity.findViewById(R.id.info_shipment_type_value);
        TextView address= (TextView) activity.findViewById(R.id.info_shipment_address_value);

        assertEquals(shipType.getText().toString(), ORDER_PROCESSED.shipmentInfo().shipType());
        assertEquals(address.getText().toString(), ORDER_PROCESSED.shipmentInfo().address());
    }

    @Test
    public void setCustomerInfo() {
        activity.setCustomerInfo(
                String.valueOf(ORDER_PROCESSED.customerInfo().id()),
                ORDER_PROCESSED.customerInfo().name());

        TextView id = (TextView) activity.findViewById(R.id.info_customer_id_value);
        TextView name= (TextView) activity.findViewById(R.id.info_customer_name_value);

        assertEquals(id.getText().toString(), String.valueOf(ORDER_PROCESSED.customerInfo().id()));
        assertEquals(name.getText().toString(), ORDER_PROCESSED.customerInfo().name());
    }
}