package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

/**
 * Created by andre on 02/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class OrdersListActivityTest {

    @Rule
    public ActivityTestRule<OrdersListActivity> ordersListActivity =
            new ActivityTestRule<>(OrdersListActivity.class, false, false);

    private void start(boolean processAll) {
        List<OrderFilter> filters = new ArrayList<>();
        filters.add(StatusFilter.create(StatusFilter.Status.ALL));

        Intent intent = OrdersListActivity.getIntent(
                OrderFilterList.create(filters),
                processAll);

        ordersListActivity.launchActivity(intent);
    }

    @Test
    public void hideFab() throws Exception {
        start(false);

        onView(withId(R.id.orders_list_fab))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void showFab() throws Exception {
        start(true);

        onView(withId(R.id.orders_list_fab))
                .check(matches(isDisplayed()));
    }

    @Test
    public void showOrdersList(List<Order> orders) {
        start(true);



    }

//    @Test
//    public void showEmptyList() {
//    }
//
//    @Test
//    public void showError(String error) {
//    }
//
//    @Test
//    public void showCommunicationError() {
//    }
//
//    @Test
//    public void navigateToOrderDetails(int orderId, int position, int total) {
//
//    }
}