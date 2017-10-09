package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.BasePresenter;
import br.com.aaascp.gerenciadordepedidos.presentation.BaseView;

/**
 * Created by andre on 30/09/17.
 */

interface OrderDetailsContract {

    interface View extends BaseView<Presenter> {
        void hideClearButton();

        void hideSkipButton();

        void setupMenu();

        void setupCloseToolbar();

        void setupBackToolbar();

        void setupTitle(int id, int current, int total);

        void setShipType(String shipType);

        void setItemsLeft(int total, int itemsLeft);

        void showOrder(Order order, CodesToProcess codesToProcess);

        void showError(String error);

        void showCommunicationError();

        void finishOkUnique();

        void showBackDialog();

        void showSkipDialog();

        void showClearDialog();

        void showCloseDialog();

        void hideLabels();

        void showFinishLabel();

        void showItemsLeftLabel();

        void showAlreadyProcessedLabel();

        void finishBack();

        void finishSkip();

        void finishClose();

        void finishOk();

        void updateCodesProcessed(CodesToProcess codesToProcess);

        void navigateToDetails(Order order);

        void navigateToProcessor(CodesToProcess codesToProcess);

        void checkPermissionForCamera();
    }

    interface Presenter extends BasePresenter {

        void onMenuCreated();

        void onBackPressed();

        void onInfoClicked();

        void onClearClicked();

        void onSkipClicked();

        void onCloseClicked();

        void onBackDialogOk();

        void onSkipDialogOk();

        void onClearDialogOk();

        void onCloseDialogOk();

        void onFabClicked();

        void onFinishClicked();

        void onAlreadyProcessedClicked();

        void onShipTypeClicked();

        void onProcessorResult(CodesToProcess codesToProcess);

        void onCameraPermissionEnabled();
    }
}
