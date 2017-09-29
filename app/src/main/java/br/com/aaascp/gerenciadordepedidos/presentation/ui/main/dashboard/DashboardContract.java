package br.com.aaascp.gerenciadordepedidos.presentation.ui.main.dashboard;

import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;

/**
 * Created by andre on 28/09/17.
 */

interface DashboardContract {
    interface View extends BaseView<Presenter> {
        void setToProcessCount(String count);

        void showGetIdsDialog(DialogUtils.IntValuesListener listener);

        void showErrorGettingIds();

        void navigateToOrdersList(OrderFilterList filter, boolean showProcessAll);
    }

    interface Presenter extends BasePresenter {

        void onToProcessButtonClicked();

        void onProcessedButtonClicked();

        void onAllButtonClicked();

        void onFindButtonClicked();
    }
}