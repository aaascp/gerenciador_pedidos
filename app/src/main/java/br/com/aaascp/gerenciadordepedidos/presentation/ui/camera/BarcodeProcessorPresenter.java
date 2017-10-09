package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;

/**
 * Created by andre on 29/09/17.
 */

final class BarcodeProcessorPresenter implements BarcodeProcessorContract.Presenter {

    private final BarcodeProcessorContract.View view;
    private final CodesToProcess codesToProcess;

    private boolean ready;

    BarcodeProcessorPresenter(
            BarcodeProcessorContract.View view,
            CodesToProcess codesToProcess) {

        this.view = view;
        this.codesToProcess = codesToProcess;

        ready = true;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        setup();
    }

    private void setup() {
        view.setupToolbar(codesToProcess.orderId());
        setItemsLeft();
    }

    private void setItemsLeft() {
        int itemsLeft = codesToProcess.itemsLeft();

        if (codesToProcess.itemsLeft() > 0) {
            view.setItemsLeft(itemsLeft);
        } else {
            view.setZeroItemsLeft();
        }
    }

    @Override
    public void onItemProcessed(String code) {
        if (!ready) {
            return;
        }
        ready = false;

        CodesToProcess.Status status = codesToProcess.process(code);

        if (status == CodesToProcess.Status.SUCCESS) {
            onProcessSuccess();
        } else {
            view.showProcessError();
        }

        showProcessMessage(status, code);
    }

    @Override
    public void onProcessingDone() {
        ready = true;
    }

    @Override
    public void onFinish() {
        view.close(codesToProcess);
    }

    @Override
    public void onPermissionGranted() {
        view.setupCamera();
    }

    @Override
    public void onPermissionDenied() {
        view.showCameraPermission();
    }

    private void onProcessSuccess() {
        view.showProcessSuccess();
        setItemsLeft();
        checkFinish();
    }

    private void showProcessMessage(CodesToProcess.Status status, String code) {
        switch (status) {
            case SUCCESS:
                view.showSuccessMessage(code);
                break;
            case CODE_ALREADY_PROCESSED:
                view.showCodeAlreadyProcessedMessage(code);
                break;
            case CODE_INVALID:
                view.showCodeInvalidMessage(code);
                break;
        }
    }

    private void checkFinish() {
        if (codesToProcess.itemsLeft() == 0) {
            view.showFinishDialog();
        }
    }
}
