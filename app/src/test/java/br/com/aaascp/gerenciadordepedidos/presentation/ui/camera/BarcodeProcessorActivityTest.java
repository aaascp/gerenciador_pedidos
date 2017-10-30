package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;


import android.app.AlertDialog;
import android.content.DialogInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;

import java.util.HashMap;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 09/10/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BarcodeProcessorActivityTest {

    @Mock
    private BarcodeProcessorContract.Presenter presenter;

    private BarcodeProcessorActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        activity = Robolectric.setupActivity(BarcodeProcessorActivity.class);
        activity.setPresenter(presenter);
    }

    @Test
    public void onItemProcessed() {
        String code = "1234";
        activity.onItemProcessed(code);

        verify(presenter).onItemProcessed(code);
    }

    @Test
    public void setupToolbar() {
        int orderId = 1000;
        activity.setupToolbar(orderId);

        assertThat(
                activity.title.getText().toString(),
                containsString(String.valueOf(orderId)));
    }

    @Test
    public void setItemsLeft() {
        int itemsLeft = 2;
        activity.setItemsLeft(itemsLeft);

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString(String.valueOf(itemsLeft)));
    }

    @Test
    public void setZeroItemsLeft() {
        activity.setZeroItemsLeft();

        assertEquals(
                activity.itemsLeft.getText().toString(),
                activity.getString(R.string.barcode_processor_items_left));
    }

    @Test
    public void showProcessError() {
        activity.showProcessError();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(presenter).onProcessingDone();
    }

    @Test
    public void showProcessSuccess() {
        activity.showProcessSuccess();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(presenter).onProcessingDone();
    }

    @Test
    public void showFinishDialog() {
        activity.showFinishDialog();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.barcode_processor_finish_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.barcode_processor_finish_dialog_message));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(presenter).onFinish();
    }

    @Test
    public void close() {
        Map<String, Integer> codes = new HashMap<>(1);
        codes.put("1234", 1);

        CodesToProcess codesToProcess = CodesToProcess.create(codes, 1000);

        activity.close(codesToProcess);

        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                BarcodeProcessorActivity.RESULT_OK,
                shadowActivity.getResultCode());

        assertEquals(
                shadowActivity.getResultIntent()
                        .getParcelableExtra(
                                BarcodeProcessorActivity.EXTRA_RESULT),
                codesToProcess);

        assertTrue(shadowActivity.isFinishing());
    }
}