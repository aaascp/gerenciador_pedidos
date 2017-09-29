package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;

/**
 * Created by andre on 29/09/17.
 */

interface BarcodeProcessorContract {

    interface View extends BaseView<Presenter> {
        void setupToolbar(int orderId);

        void setItemsLeft(int itemsLeftCount);

        void setZeroItemsLeft();

        void showProcessError();

        void showProcessSuccess();

        void showMessage(String message);

        void showFinishDialog();

        void close(CodesToProcess codesProcessed);
    }

    interface Presenter extends BasePresenter {
        void onItemProcessed(String code);

        void onProcessingDone();

        void onFinish();
    }
}
