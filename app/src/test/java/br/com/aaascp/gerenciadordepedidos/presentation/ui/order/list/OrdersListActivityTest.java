package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.NullOrderFilterList;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.factory.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersFakeDataSource;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 02/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrdersListActivityTest {

    private static Order ORDER_PROCESSED =
            OrdersFactory.createOrder(1000, true);

    private static Order ORDER_NOT_PROCESSED =
            OrdersFactory.createOrder(1001, false);

    private static List<Order> ORDERS =
            Arrays.asList(ORDER_PROCESSED, ORDER_NOT_PROCESSED);

    private static List<Order> EMPTY_LIST = Collections.emptyList();

    private static List<Order> ERRORS =
            Collections.singletonList(
                    OrdersFakeDataSource.createOrderError());

    private static List<Order> COMMUNICATION_ERROR =
            Collections.singletonList(
                    OrdersFakeDataSource.createOrderCommunicationError());

    private OrdersListActivity activity;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private void init(boolean processAll) {
        Intent intent = new Intent();
        intent.putExtra(
                OrdersListActivity.EXTRA_PROCESS_ALL, processAll);
        intent.putExtra(OrdersListActivity.EXTRA_ORDER_FILTERS,
                NullOrderFilterList.create());

        activity = Robolectric.buildActivity(OrdersListActivity.class, intent)
                .create()
                .start()
                .get();
    }

    private void init(List<Order> orders) {
        OrdersFakeDataSource.clear();

        for (Order order : orders) {
            OrdersFakeDataSource.addOrder(order);
        }

        init(false);

        activity.recyclerView.measure(0, 0);
        activity.recyclerView.layout(0, 0, 100, 1000);
    }

    @Test
    public void setupFab_processAll() throws Exception {
        init(true);

        assertEquals(activity.fab.getVisibility(), View.VISIBLE);
    }

    @Test
    public void setupFab_notProcessAll() throws Exception {
        init(false);

        assertEquals(activity.fab.getVisibility(), View.GONE);
    }

    @Test
    public void setupOrdersList_success() throws Exception {
        init(ORDERS);

        OrdersListAdapter.ViewHolder holder =
                (OrdersListAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.id.getText().toString(),
                String.valueOf(ORDERS.get(0).id()));
        assertEquals(
                holder.shipType.getText().toString(),
                ORDERS.get(0).shipmentInfo().shipType());
        assertEquals(
                holder.itemsCount.getText().toString(),
                String.valueOf(ORDERS.get(0).size()));
        assertEquals(
                holder.processedAt.getText().toString(),
                ORDERS.get(0).processedAt());
        assertEquals(
                holder.processedAt.getCurrentTextColor(),
                ContextCompat.getColor(activity, R.color.green));
        assertEquals(
                holder.lastModifiedAt.getText().toString(),
                ORDERS.get(0).lastModifiedAt());
        assertEquals(
                holder.action.getText().toString(),
                String.valueOf(activity.getString(R.string.orders_list_action_details)));

        holder = (OrdersListAdapter.ViewHolder) activity.recyclerView
                .findViewHolderForAdapterPosition(1);

        assertEquals(
                holder.id.getText().toString(),
                String.valueOf(ORDERS.get(1).id()));
        assertEquals(
                holder.shipType.getText().toString(),
                ORDERS.get(1).shipmentInfo().shipType());
        assertEquals(
                holder.itemsCount.getText().toString(),
                String.valueOf(ORDERS.get(1).size()));
        assertEquals(
                holder.processedAt.getText().toString(),
                activity.getString(R.string.order_list_processed_at_empty));
        assertEquals(
                holder.processedAt.getCurrentTextColor(),
                ContextCompat.getColor(activity, R.color.red));
        assertEquals(
                holder.lastModifiedAt.getText().toString(),
                ORDERS.get(1).lastModifiedAt());
        assertEquals(
                holder.action.getText().toString(),
                String.valueOf(activity.getString(R.string.orders_list_action_process)));
    }

    @Test
    public void setupOrdersList_empty() throws Exception {
        init(EMPTY_LIST);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                activity.getString(R.string.order_list_empty));
    }

    @Test
    public void setupOrdersList_error() throws Exception {
        init(ERRORS);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                OrdersFakeDataSource.ERROR_MESSAGE);
    }

    @Test
    public void setupOrdersList_communicationError() throws Exception {
        init(COMMUNICATION_ERROR);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                activity.getString(R.string.error_communication));
    }

    @Test
    public void processAll_startsDetailsForListWithFirstOrder() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processAll_resultOk_startsDetailsForNext() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        Intent startedIntent = shadowActivity.getNextStartedActivity();

        shadowActivity.receiveResult(
                startedIntent,
                OrdersListActivity.RESULT_CODE_OK,
                null);

        Intent nextIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                2);
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(1).id());
    }

    @Test
    public void processAll_resultOk_last_resetsCurrent() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        for (int i = 0; i < ORDERS.size(); i++) {
            shadowActivity.receiveResult(
                    shadowActivity.getNextStartedActivity(),
                    OrdersListActivity.RESULT_CODE_OK,
                    null);
        }

        activity.fab.performClick();

        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processAl_resultSkip_startsDetailsForNext() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        Intent startedIntent = shadowActivity.getNextStartedActivity();

        shadowActivity.receiveResult(
                startedIntent,
                OrdersListActivity.RESULT_CODE_SKIP,
                null);

        Intent nextIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                2);
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(1).id());
    }

    @Test
    public void processAll_resultSkip_last_resetsCurent() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        for (int i = 0; i < ORDERS.size(); i++) {
            shadowActivity.receiveResult(
                    shadowActivity.getNextStartedActivity(),
                    OrdersListActivity.RESULT_CODE_SKIP,
                    null);
        }

        activity.fab.performClick();

        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processAll_resultClose_resetsCurrent() throws Exception {
        init(ORDERS);

        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        shadowActivity.receiveResult(
                shadowActivity.getNextStartedActivity(),
                OrdersListActivity.RESULT_CODE_CLOSE,
                null);

        activity.fab.performClick();
        Intent nextIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                nextIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processUnique_startsDetails() throws Exception {
        init(ORDERS);

        activity.recyclerView.getChildAt(0).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processUnique_resultOkUnique_resetsCurrent() throws Exception {
        init(ORDERS);

        activity.recyclerView.getChildAt(0).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        shadowActivity.receiveResult(
                shadowActivity.getNextStartedActivity(),
                OrdersListActivity.RESULT_CODE_OK_UNIQUE,
                null);

        activity.fab.performClick();

        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }

    @Test
    public void processUnique_resultClose_resetsCurrent() throws Exception {
        init(ORDERS);

        activity.recyclerView.getChildAt(0).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);

        shadowActivity.receiveResult(
                shadowActivity.getNextStartedActivity(),
                OrdersListActivity.RESULT_CODE_CLOSE,
                null);

        activity.fab.performClick();

        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_TOTAL),
                ORDERS.size());
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_CURRENT),
                1);
        assertEquals(
                startedIntent.getExtras().getInt(OrderDetailsActivity.EXTRA_ORDER_ID),
                ORDERS.get(0).id());
    }
}
