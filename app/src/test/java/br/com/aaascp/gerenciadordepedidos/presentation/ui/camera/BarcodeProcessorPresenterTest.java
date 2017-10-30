package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by andre on 09/10/17.
 */
public class BarcodeProcessorPresenterTest {

    @Mock
    private BarcodeProcessorContract.View view;

    private BarcodeProcessorPresenter presenter;


    private static CodesToProcess getCodesToProcess(
            String code,
            int itemsLeft) {

        Map<String, Integer> codes = new HashMap<>();
        codes.put(code, itemsLeft);

        return CodesToProcess.create(codes, 1000);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private void init(CodesToProcess codesToProcess) {
        presenter = new BarcodeProcessorPresenter(view, codesToProcess);
    }

    @Test
    public void onConstruction_setPresenter() {
        CodesToProcess codesToProcess = getCodesToProcess("1234", 1);
        init(codesToProcess);

        verify(view).setPresenter(presenter);
    }

    @Test
    public void onStart_setupToolbar() {
        CodesToProcess codesToProcess = getCodesToProcess("1234", 1);
        init(codesToProcess);

        presenter.start();

        verify(view).setupToolbar(codesToProcess.orderId());
    }

    @Test
    public void onStart_setItemsLeft() {
        CodesToProcess codesToProcess = getCodesToProcess("1234", 1);
        init(codesToProcess);

        presenter.start();

        verify(view).setItemsLeft(codesToProcess.itemsLeft());
    }

    @Test
    public void onStart_setItemsLeft_finish() {
        CodesToProcess codesToProcess = getCodesToProcess("1234", 0);
        init(codesToProcess);


        presenter.start();

        verify(view).setZeroItemsLeft();
    }

    @Test
    public void onFinish() {
        CodesToProcess codesToProcess = getCodesToProcess("1234", 1);
        init(codesToProcess);

        presenter.onFinish();

        verify(view).close(codesToProcess);
    }

    @Test
    public void onItemProcessed_invalidCode() {
        String code = "1234";
        String invalidCode = "1235";

        CodesToProcess codesToProcess = getCodesToProcess(code, 1);
        init(codesToProcess);

        presenter.onItemProcessed(invalidCode);

        verify(view).showProcessError();
        verify(view).showCodeInvalidMessage(invalidCode);
    }

    @Test
    public void onItemProcessed_alreadyProcessedCode() {
        String code = "1234";
        int codeQuantity = 2;

        CodesToProcess codesToProcess = getCodesToProcess(code, codeQuantity);
        init(codesToProcess);

        for (int i = 0; i <= codeQuantity; i++) {
            presenter.onItemProcessed(code);
            presenter.onProcessingDone();
        }

        verify(view).showProcessError();
        verify(view).showCodeAlreadyProcessedMessage(code);
    }

    @Test
    public void onItemProcessed_success() {
        String code = "1234";
        int codeQuantity = 2;

        CodesToProcess codesToProcess = getCodesToProcess(code, codeQuantity);
        init(codesToProcess);

        presenter.onItemProcessed(code);

        verify(view).showProcessSuccess();
        verify(view).setItemsLeft(codesToProcess.itemsLeft());
        verify(view).showSuccessMessage(code);
    }

    @Test
    public void onItemProcessed_success_finish() {
        String code = "1234";
        int codeQuantity = 2;

        CodesToProcess codesToProcess = getCodesToProcess(code, codeQuantity);
        init(codesToProcess);

        for (int i = 0; i < codeQuantity; i++) {
            presenter.onItemProcessed(code);
            presenter.onProcessingDone();
        }

        verify(view, times(codeQuantity)).showProcessSuccess();
        verify(view, times(codeQuantity)).showSuccessMessage(code);
        verify(view).setZeroItemsLeft();
        verify(view).showFinishDialog();
    }

    @Test
    public void onItemProcessed_notReady_notProcess() {
        String code = "1234";
        int codeQuantity = 2;

        CodesToProcess codesToProcess = getCodesToProcess(code, codeQuantity);
        init(codesToProcess);

        presenter.onItemProcessed(code);
        presenter.onItemProcessed(code);

        verify(view, times(1)).showProcessSuccess();
    }
}