package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CustomerInfo;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.entity.ShipmentInfo;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersFakeDataSource;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasTextColor;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by andre on 02/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class OrdersListActivityTest {

    private static Order ORDER_PROCESSED =
            OrdersFakeDataSource.createOrder(
                    1000,
                    ShipmentInfo.builder().address("Endereço").shipType("Sedex").build(),
                    CustomerInfo.builder().id(1).name("Customer").build(),
                    5,
                    DateFormatterUtils.getDateHourInstance().now(),
                    DateFormatterUtils.getDateHourInstance().now());

    private static Order ORDER_NOT_PROCESSED =
            OrdersFakeDataSource.createOrder(
                    1001,
                    ShipmentInfo.builder().address("Endereço").shipType("Transportadora").build(),
                    CustomerInfo.builder().id(1).name("Customer").build(),
                    3,
                    null,
                    DateFormatterUtils.getDateHourInstance().now());

    private static Order ORDER_ERROR =
            OrdersFakeDataSource.createOrderError();

    private static Order ORDER_COMMUNICATION_ERROR =
            OrdersFakeDataSource.createOrderCommunicationError();

    @Rule
    public ActivityTestRule<OrdersListActivity> ordersListActivity =
            new ActivityTestRule<>(OrdersListActivity.class, false, false);

    private void start(boolean processAll, Order order) {
        if (order != null) {
            OrdersFakeDataSource.addOrder(order);
        }

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(StatusFilter.create(StatusFilter.Status.ALL));

        Intent intent = OrdersListActivity.getIntent(
                OrderFilterList.create(filters),
                processAll);

        ordersListActivity.launchActivity(intent);
    }

    private void startProcessAll(boolean processAll) {
        start(processAll, ORDER_PROCESSED);
    }

    private void startWithOrder(Order order) {
        start(false, order);
    }

    @Test
    public void hideFab() throws Exception {
        startProcessAll(false);

        onView(withId(R.id.orders_list_fab))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void showFab() throws Exception {
        startProcessAll(true);

        onView(withId(R.id.orders_list_fab))
                .check(matches(isDisplayed()));
    }

    @Test
    public void showOrdersList_processedOrder() {
        startWithOrder(ORDER_PROCESSED);
        Context context = ordersListActivity.getActivity();

        String processedAt = ORDER_PROCESSED.processedAt();
        String action = context.getString(R.string.orders_list_action_details);

        onView(withId(R.id.order_id_value)).check(matches(withText(String.valueOf(ORDER_PROCESSED.id()))));
        onView(withId(R.id.order_ship_type_value)).check(matches(withText(ORDER_PROCESSED.shipmentInfo().shipType())));
        onView(withId(R.id.order_size_value)).check(matches(withText(String.valueOf(ORDER_PROCESSED.size()))));
        onView(withId(R.id.order_last_modification_date_value)).check(matches(withText(ORDER_PROCESSED.lastModifiedAt())));
        onView(withId(R.id.order_processed_at_value)).check(matches(withText(processedAt)));
        onView(withId(R.id.order_processed_at_value)).check(matches(hasTextColor(R.color.green)));
        onView(withId(R.id.order_action_text)).check(matches(withText(action)));
    }

    @Test
    public void showOrdersList_notProcessedOrder() {
        startWithOrder(ORDER_NOT_PROCESSED);
        Context context = ordersListActivity.getActivity();

        String processedAt = context.getString(R.string.order_list_processed_at_empty);
        String action = context.getString(R.string.orders_list_action_process);

        onView(withId(R.id.orders_list_recycler)).perform();

        onView(withId(R.id.order_id_value)).check(matches(withText(String.valueOf(ORDER_NOT_PROCESSED.id()))));
        onView(withId(R.id.order_ship_type_value)).check(matches(withText(ORDER_NOT_PROCESSED.shipmentInfo().shipType())));
        onView(withId(R.id.order_size_value)).check(matches(withText(String.valueOf(ORDER_NOT_PROCESSED.size()))));
        onView(withId(R.id.order_last_modification_date_value)).check(matches(withText(ORDER_NOT_PROCESSED.lastModifiedAt())));
        onView(withId(R.id.order_processed_at_value)).check(matches(withText(processedAt)));
        onView(withId(R.id.order_processed_at_value)).check(matches(hasTextColor(R.color.red)));
        onView(withId(R.id.order_action_text)).check(matches(withText(action)));
    }

    @Test
    public void showEmptyList() {
        startWithOrder(null);
        Context context = ordersListActivity.getActivity();

        String message = context.getString(R.string.order_list_empty);
        onView(withId(R.id.empty_state_message)).check(matches(withText(message)));
    }

    @Test
    public void showError() {
        startWithOrder(ORDER_ERROR);

        onView(withId(R.id.empty_state_message)).check(matches(withText("Error")));
    }

    @Test
    public void showCommunicationError() {
        startWithOrder(ORDER_COMMUNICATION_ERROR);

        String message = ordersListActivity.getActivity().getString(R.string.error_communication);
        onView(withId(R.id.empty_state_message)).check(matches(withText(message)));
    }

    @Test
    public void navigateToOrderDetails() {
        startWithOrder(ORDER_NOT_PROCESSED);

        onView(withId(R.id.orders_list_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withText("#"+ORDER_NOT_PROCESSED.id())).check(matches(withParent(withId(R.id.order_details_toolbar))));
    }
}