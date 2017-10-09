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

        void setupCamera();

        void setItemsLeft(int itemsLeftCount);

        void setZeroItemsLeft();

        void showCameraPermission();

        void showProcessError();

        void showProcessSuccess();

        void showSuccessMessage(String code);

        void showCodeAlreadyProcessedMessage(String code);

        void showCodeInvalidMessage(String code);

        void showFinishDialog();

        void close(CodesToProcess codesProcessed);
    }

    interface Presenter extends BasePresenter {
        void onItemProcessed(String code);

        void onProcessingDone();

        void onFinish();

        void onPermissionGranted();

        void onPermissionDenied();
    }
}
