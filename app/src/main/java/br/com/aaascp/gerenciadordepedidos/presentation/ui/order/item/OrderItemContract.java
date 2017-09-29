package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item;

import android.support.annotation.NonNull;

import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;

/**
 * Created by andre on 28/09/17.
 */

interface OrderItemContract {
    interface View extends BaseView<Presenter> {
        void setupToolbar(String code);

        void loadImage(@NonNull String imageUrl);

        void setDescription(String description);
    }

    interface Presenter extends BasePresenter {

    }

}
