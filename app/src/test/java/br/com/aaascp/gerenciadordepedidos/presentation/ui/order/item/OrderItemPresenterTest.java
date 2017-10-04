package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by andre on 04/10/17.
 */
public class OrderItemPresenterTest {

    private static final OrderItem ITEM_IMAGE_NULL = OrderItem
            .builder()
            .id(1)
            .code("1234")
            .description("Product")
            .imageUrl(null)
            .quantity(2)
            .build();

    private static final OrderItem ITEM_IMAGE_EMPTY = OrderItem
            .builder()
            .id(1)
            .code("1234")
            .description("Product")
            .imageUrl("")
            .quantity(2)
            .build();

    private static final OrderItem ITEM_WITH_IMAGE = OrderItem
            .builder()
            .id(1)
            .code("1234")
            .description("Product")
            .imageUrl("imageUrl")
            .quantity(2)
            .build();

    @Mock
    private OrderItemContract.View view;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private void start(OrderItem item) {
        OrderItemPresenter ordersItemPresenter =
                new OrderItemPresenter(view, item);

        ordersItemPresenter.start();
        verify(view).setPresenter(ordersItemPresenter);
    }

    @Test
    public void start() {
        start(ITEM_WITH_IMAGE);
        verify(view).setupToolbar(ITEM_WITH_IMAGE.code());
        verify(view).setDescription(ITEM_WITH_IMAGE.description());
    }

    @Test
    public void setupImage_withImage_loadsImage() {
        start(ITEM_WITH_IMAGE);
        verify(view).loadImage(ITEM_WITH_IMAGE.imageUrl());
    }

    @Test
    public void setupImage_withNullImage_notLoadsImage() {
        start(ITEM_IMAGE_NULL);
        verify(view, never()).loadImage(anyString());
    }

    @Test
    public void setupImage_withEmptyImage_notLoadsImage() {
        start(ITEM_IMAGE_EMPTY);
        verify(view, never()).loadImage(anyString());
    }
}