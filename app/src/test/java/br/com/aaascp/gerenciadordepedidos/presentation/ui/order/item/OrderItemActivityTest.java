package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item;


import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.util.ImageLoader;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by andre on 04/10/17.
 */
@PrepareForTest({ImageLoader.class})
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrderItemActivityTest {

    private static final OrderItem ITEM =
            OrderItem
                    .builder()
                    .id(1)
                    .code("1234")
                    .description("Product")
                    .imageUrl(null)
                    .quantity(2)
                    .build();

    @Mock
    OrderItemContract.Presenter presenter;

    private OrderItemActivity activity;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.setupActivity(OrderItemActivity.class);
        activity.setPresenter(presenter);
    }

    @Test
    public void onStart_startPresenter() throws Exception {
        activity.onStart();
        verify(presenter).start();
    }

    @Test
    public void setupToolbar() throws Exception {
        activity.setupToolbar(ITEM.code());
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.order_item_toolbar);
        assertThat(toolbar.getTitle().toString(), containsString(ITEM.code()));
    }

    @Test
    public void loadImage() throws Exception {
        activity.loadImage(ITEM.imageUrl());
        ImageView imageView = (ImageView) activity.findViewById(R.id.order_item_image);

        mockStatic(ImageLoader.class);
        ImageLoader.loadImage(activity, ITEM.imageUrl(), imageView);
        verifyStatic(times(1));
    }

    @Test
    public void setDescription() throws Exception {
        activity.setDescription(ITEM.description());
        TextView descriptionView = (TextView) activity.findViewById(R.id.order_item_description);
        assertEquals(descriptionView.getText().toString(), ITEM.description());
    }
}