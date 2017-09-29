package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item;

import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.util.StringUtils;

/**
 * Created by andre on 28/09/17.
 */

final class OrderItemPresenter implements OrderItemContract.Presenter {

    private final OrderItemContract.View view;
    private final OrderItem item;

    OrderItemPresenter(
            OrderItemContract.View view,
            OrderItem item) {

        this.view = view;
        this.item = item;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        setupOrder();
    }

    private void setupOrder() {
        view.setupToolbar(item.code());
        view.setDescription(item.description());
        setupImage();
    }

    private void setupImage() {
        String imageUrl = item.imageUrl();
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            view.loadImage(imageUrl);
        }
    }
}
