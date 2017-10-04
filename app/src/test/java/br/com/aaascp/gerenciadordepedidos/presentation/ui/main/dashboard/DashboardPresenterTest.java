package br.com.aaascp.gerenciadordepedidos.presentation.ui.main.dashboard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.filter.IdFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.OrderFilter;
import br.com.aaascp.gerenciadordepedidos.repository.filter.StatusFilter;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;


/**
 * Created by andre on 04/10/17.
 */
public class DashboardPresenterTest {

    private static List<Order> ORDERS = OrdersFactory.getOrders(2, 0);

    @Mock
    private DashboardContract.View view;

    @Mock
    private OrdersRepository ordersRepository;

    @Captor
    private ArgumentCaptor<RepositoryCallback<List<Order>>> repositoryCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<DialogUtils.IntValuesListener> intValuesListenerArgumentCaptor;

    private DashboardPresenter presenter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        presenter = new DashboardPresenter(view, ordersRepository);
        verify(view).setPresenter(presenter);
    }

    @Test
    public void start_setupToProcessButton_success() {
        presenter.start();

        verify(ordersRepository).getList(
                eq(StatusFilter.create(StatusFilter.Status.TO_PROCESS)),
                repositoryCallbackArgumentCaptor.capture());

        repositoryCallbackArgumentCaptor.getValue().onSuccess(ORDERS);

        verify(view).setToProcessCount(String.valueOf(ORDERS.size()));
    }

    @Test
    public void start_setupToProcessButton_error() {
        presenter.start();

        verify(ordersRepository).getList(
                eq(StatusFilter.create(StatusFilter.Status.TO_PROCESS)),
                repositoryCallbackArgumentCaptor.capture());

        repositoryCallbackArgumentCaptor.getValue().onError(anyList());

        verify(view).setToProcessCount("?");
    }

    @Test
    public void onToProcessButtonClicked() {
        presenter.onToProcessButtonClicked();

        presenter.onProcessedButtonClicked();

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(StatusFilter.create(StatusFilter.Status.TO_PROCESS));

        verify(view).navigateToOrdersList(OrderFilterList.create(filters), true);

    }

    @Test
    public void onProcessedButtonClicked() {
        presenter.onProcessedButtonClicked();

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(StatusFilter.create(StatusFilter.Status.PROCESSED));

        verify(view).navigateToOrdersList(OrderFilterList.create(filters), false);
    }

    @Test
    public void onAllButtonClicked() {
        presenter.onAllButtonClicked();

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(StatusFilter.create(StatusFilter.Status.ALL));

        verify(view).navigateToOrdersList(OrderFilterList.create(filters), false);
    }

    @Test
    public void onFindButtonClicked_success() {
        presenter.onFindButtonClicked();

        verify(view).showGetIdsDialog(intValuesListenerArgumentCaptor.capture());

        HashSet<Integer> values = new HashSet<>(3);
        values.addAll(Arrays.asList(1000, 1001, 1002));

        intValuesListenerArgumentCaptor.getValue().onValues(values);

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(IdFilter.create(values));

        verify(view).navigateToOrdersList(OrderFilterList.create(filters), true);
    }

    @Test
    public void onFindButtonClicked_success_unique() {
        presenter.onFindButtonClicked();

        verify(view).showGetIdsDialog(intValuesListenerArgumentCaptor.capture());

        HashSet<Integer> values = new HashSet<>(1);
        values.add(1000);

        intValuesListenerArgumentCaptor.getValue().onValues(values);

        List<OrderFilter> filters = new ArrayList<>();
        filters.add(IdFilter.create(values));

        verify(view).navigateToOrdersList(OrderFilterList.create(filters), false);
    }

    @Test
    public void onFindButtonClicked_error() {
        presenter.onFindButtonClicked();

        verify(view).showGetIdsDialog(intValuesListenerArgumentCaptor.capture());

        intValuesListenerArgumentCaptor.getValue().onError();

        verify(view).showErrorGettingIds();
    }

}