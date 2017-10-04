package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import com.google.common.collect.Lists;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersListFactory;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by andre on 02/10/17.
 */
public class OrdersListPresenterTest {

    private static int ORDERS_SIZE = 2;
    private static List<Order> ORDERS = createOrdersList(ORDERS_SIZE, 0.5);
    private static List<Order> ORDERS_EMPTY = new ArrayList<>(0);
    private static List<String> ERRORS = Lists.newArrayList("Error");
    private static List<String> ERRORS_EMPTY = null;

    @Mock
    private OrdersListContract.View ordersListView;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderFilterList filters;

    @Captor
    private ArgumentCaptor<RepositoryCallback<List<Order>>> repositoryCallbackArgumentCaptor;

    private OrdersListPresenter ordersListPresenter;

    private static List<Order> createOrdersList(int size, double processedProbability) {
        return OrdersListFactory.getOrders(size, processedProbability);
    }

    private void setProcessAllPresenter() {
        ordersListPresenter = new OrdersListPresenter(
                ordersListView,
                filters,
                ordersRepository,
                true);
    }

    private void setNotProcessAllPresenter() {
        ordersListPresenter = new OrdersListPresenter(
                ordersListView,
                filters,
                ordersRepository,
                false);
    }

    private void start(List<Order> orders) {
        ordersListPresenter.start();

        verify(ordersRepository).getList(
                eq(filters),
                repositoryCallbackArgumentCaptor.capture());

        repositoryCallbackArgumentCaptor.getValue().onSuccess(orders);
    }

    private void startError(List<String> errors) {
        ordersListPresenter.start();

        verify(ordersRepository).getList(
                eq(filters),
                repositoryCallbackArgumentCaptor.capture());

        repositoryCallbackArgumentCaptor.getValue().onError(errors);
    }

    @Before
    public void setupOrdersListPresenter() {
        MockitoAnnotations.initMocks(this);

        setProcessAllPresenter();
    }

    @Test
    public void onConstruction_setPresenter() {
        verify(ordersListView).setPresenter(ordersListPresenter);
    }

    @Test
    public void onConstruction_setupFab_processAll() {
        verify(ordersListView).showFab();
    }

    @Test
    public void onConstruction_setupFab_notProcessAll() {
        setNotProcessAllPresenter();
        verify(ordersListView).hideFab();
    }

    @Test
    public void onStart_setupOrders_success() {
        start(ORDERS);

        verify(ordersListView).showOrdersList(ORDERS);
    }

    @Test
    public void onStart_setupOrders_empty() {
        start(ORDERS_EMPTY);

        verify(ordersListView).showEmptyList();
    }

    @Test
    public void onStart_setupOrders_error() {
        startError(ERRORS);

        verify(ordersListView).showError(ERRORS.get(0));
    }

    @Test
    public void onStart_setupOrders_communicationError() {
        startError(ERRORS_EMPTY);

        verify(ordersListView).showCommunicationError();
    }

    @Test
    public void onOrderClicked_navigateToOrderDetails_withOrderId() {
        start(ORDERS);

        int orderId = ORDERS.get(0).id();
        ordersListPresenter.onOrderClicked(orderId);

        verify(ordersListView).navigateToOrderDetails(orderId, 1, 1);
    }

    @Test
    public void onResultNext_finish_reloadOrders() {
        start(ORDERS);

        ordersListPresenter.onResultNext();
        ordersListPresenter.onResultNext();

        verify(ordersListView).showOrdersList(ORDERS);
    }

    @Test
    public void onResultNext_notFinish_navigateToOrderDetails() {
        start(ORDERS);

        ordersListPresenter.onResultNext();

        verify(ordersListView).navigateToOrderDetails(
                ORDERS.get(1).id(),
                2,
                ORDERS_SIZE);
    }

    @Test
    public void onResultClose() {
        start(ORDERS);

        ordersListPresenter.onResultClose();
        verify(ordersListView).showOrdersList(ORDERS);
    }
}