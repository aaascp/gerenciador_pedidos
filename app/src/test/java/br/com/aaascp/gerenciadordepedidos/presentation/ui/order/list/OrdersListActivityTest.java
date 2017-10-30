package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.Arrays;
import java.util.Collections;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 02/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrdersListActivityTest {

    private static Order ORDER_PROCESSED =
            OrdersFactory.createOrder(
                    1000,
                    ShipmentInfo.builder()
                            .address("Endereço")
                            .shipType("Sedex")
                            .build(),
                    CustomerInfo.builder()
                            .id(1)
                            .name("Customer")
                            .build(),
                    5,
                    DateFormatterUtils.getDateHourInstance().now(),
                    DateFormatterUtils.getDateHourInstance().now());

    private static Order ORDER_NOT_PROCESSED =
            OrdersFactory.createOrder(
                    1001,
                    ShipmentInfo.builder()
                            .address("Endereço")
                            .shipType("Transportadora")
                            .build(),
                    CustomerInfo.builder()
                            .id(1)
                            .name("Customer")
                            .build(),
                    3,
                    null,
                    DateFormatterUtils.getDateHourInstance().now());

    private OrdersListActivity activity;

    @Mock
    OrdersListContract.Presenter presenter;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.setupActivity(OrdersListActivity.class);

        activity.setPresenter(presenter);
    }

    @Test
    public void onStart_startPresenter() throws Exception {
        activity.onStart();
        verify(presenter).start();
    }

    @Test
    public void onActivityResult_ok() throws Exception {
        activity.navigateToOrderDetails(1000, 1, 2);

        Intent intent = shadowOf(activity).getNextStartedActivity();

        shadowOf(activity).receiveResult(
                intent,
                OrdersListActivity.RESULT_CODE_OK,
                null);

        verify(presenter).onResultNext();
    }

    @Test
    public void orderDetailsSkipResult_skip() throws Exception {
        activity.navigateToOrderDetails(1000, 1, 2);

        Intent intent = shadowOf(activity).getNextStartedActivity();

        shadowOf(activity).receiveResult(
                intent,
                OrdersListActivity.RESULT_CODE_SKIP,
                null);

        verify(presenter).onResultNext();
    }

    @Test
    public void orderDetailsSkipResult_okUnique() throws Exception {
        activity.navigateToOrderDetails(1000, 1, 1);

        Intent intent = shadowOf(activity).getNextStartedActivity();

        shadowOf(activity).receiveResult(
                intent,
                OrdersListActivity.RESULT_CODE_OK_UNIQUE,
                null);

        verify(presenter).onResultClose();
    }

    @Test
    public void orderDetailsSkipResult_close() throws Exception {
        activity.navigateToOrderDetails(1000, 1, 2);

        Intent intent = shadowOf(activity).getNextStartedActivity();

        shadowOf(activity).receiveResult(
                intent,
                OrdersListActivity.RESULT_CODE_CLOSE,
                null);

        verify(presenter).onResultClose();
    }

    @Test
    public void hideFab() throws Exception {
        activity.hideFab();

        assertEquals(activity.fab.getVisibility(), View.GONE);
    }

    @Test
    public void showFab() throws Exception {
        activity.showFab();

        assertEquals(activity.fab.getVisibility(), View.VISIBLE);
    }

    @Test
    public void showOrdersList() throws Exception {
        activity.showOrdersList(
                Arrays.asList(ORDER_PROCESSED, ORDER_NOT_PROCESSED));

        OrdersListAdapter.ViewHolder holder =
                (OrdersListAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.id.getText().toString(),
                String.valueOf(ORDER_PROCESSED.id()));
        assertEquals(
                holder.shipType.getText().toString(),
                ORDER_PROCESSED.shipmentInfo().shipType());
        assertEquals(
                holder.itemsCount.getText().toString(),
                String.valueOf(ORDER_PROCESSED.size()));
        assertEquals(
                holder.processedAt.getText().toString(),
                ORDER_PROCESSED.processedAt());
        assertEquals(
                holder.processedAt.getCurrentTextColor(),
                ContextCompat.getColor(activity, R.color.green));
        assertEquals(
                holder.lastModifiedAt.getText().toString(),
                ORDER_PROCESSED.lastModifiedAt());
        assertEquals(
                holder.action.getText().toString(),
                String.valueOf(activity.getString(R.string.orders_list_action_details)));

        holder = (OrdersListAdapter.ViewHolder) activity.recyclerView
                .findViewHolderForAdapterPosition(1);

        assertEquals(
                holder.id.getText().toString(),
                String.valueOf(ORDER_NOT_PROCESSED.id()));
        assertEquals(
                holder.shipType.getText().toString(),
                ORDER_NOT_PROCESSED.shipmentInfo().shipType());
        assertEquals(
                holder.itemsCount.getText().toString(),
                String.valueOf(ORDER_NOT_PROCESSED.size()));
        assertEquals(
                holder.processedAt.getText().toString(),
                activity.getString(R.string.order_list_processed_at_empty));
        assertEquals(
                holder.processedAt.getCurrentTextColor(),
                ContextCompat.getColor(activity, R.color.red));
        assertEquals(
                holder.lastModifiedAt.getText().toString(),
                ORDER_NOT_PROCESSED.lastModifiedAt());
        assertEquals(
                holder.action.getText().toString(),
                String.valueOf(activity.getString(R.string.orders_list_action_process)));
    }

    @Test
    public void showEmptyList() throws Exception {
        activity.showEmptyList();

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        String message = activity.getString(R.string.order_list_empty);
        assertEquals(holder.getMessage().getText().toString(), message);
    }

    @Test
    public void showError() throws Exception {
        String error = "Error";
        activity.showError(error);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                error);
    }


    @Test
    public void showCommunicationError() throws Exception {
        activity.showCommunicationError();

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        String message = activity.getString(R.string.error_communication);
        assertEquals(
                holder.getMessage().getText().toString(),
                message);
    }


    @Test
    public void navigateToOrderDetails_unique() throws Exception {
        activity.showOrdersList(
                Collections.singletonList(ORDER_PROCESSED));

        OrdersListAdapter.ViewHolder holder =
                (OrdersListAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        holder.root.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        ShadowActivity.IntentForResult intentForResult =
                shadowActivity.getNextStartedActivityForResult();

        assertEquals(
                intentForResult.requestCode,
                OrdersListActivity.REQUEST_CODE_ORDER_PROCESS);
        assertEquals(
                intentForResult.intent.getIntExtra(
                        OrderDetailsActivity.EXTRA_ORDER_ID, Order.INVALID_ORDER_ID),
                ORDER_PROCESSED.id());
        assertEquals(
                intentForResult.intent.getIntExtra(OrderDetailsActivity.EXTRA_TOTAL, 0),
                2);
        assertEquals(
                intentForResult.intent.getIntExtra(OrderDetailsActivity.EXTRA_CURRENT, 0),
                1);
    }

    @Test
    public void navigateToOrderDetails_withParams() throws Exception {
        activity.navigateToOrderDetails(1000, 2, 3);

        ShadowActivity shadowActivity = shadowOf(activity);
        ShadowActivity.IntentForResult intentForResult =
                shadowActivity.getNextStartedActivityForResult();

        assertEquals(
                intentForResult.requestCode,
                OrdersListActivity.REQUEST_CODE_ORDER_PROCESS);
        assertEquals(
                intentForResult.intent.getIntExtra(
                        OrderDetailsActivity.EXTRA_ORDER_ID, Order.INVALID_ORDER_ID),
                ORDER_PROCESSED.id());
        assertEquals(
                intentForResult.intent.getIntExtra(OrderDetailsActivity.EXTRA_CURRENT, 0),
                2);
        assertEquals(
                intentForResult.intent.getIntExtra(OrderDetailsActivity.EXTRA_TOTAL, 0),
                3);
    }

    @Test
    public void onFabClick() throws Exception {
        activity.onFabClick();
        verify(presenter).onFabCLick();
    }
}