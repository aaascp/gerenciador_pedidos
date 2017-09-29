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

    private void onProcessSuccess() {
        view.showProcessSuccess();
        setItemsLeft();
        checkFinish();
    }

    private void showProcessMessage(CodesToProcess.Status status, String code) {
        String message = "%s: ";

        switch (status) {
            case SUCCESS:
                message += "Sucesso";
                break;
            case CODE_ALREADY_PROCESSED:
                message += "Código já processado";
                break;
            case CODE_INVALID:
                message += "Código Inválido";
                break;
            default:
                message += "Erro desconhecido";
        }

        view.showMessage(String.format(message, code));
    }

    private void checkFinish() {
        if (codesToProcess.itemsLeft() == 0) {
            view.showFinishDialog();
        }
    }
}
