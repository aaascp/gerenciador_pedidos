package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info;

import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;

/**
 * Created by andre on 29/09/17.
 */

interface OrderInfoContract {

    interface View extends BaseView<Presenter> {
        void setupToolbar(int id);

        void setProcessedOrderInfo(String size, String processedAt, String lastModification);

        void setNotProcessedOrderInfo(String size, String lastModification);

        void setShipmentInfo(String type, String address);

        void setCustomerInfo(String id, String name);
    }

    interface Presenter extends BasePresenter {

    }
}
