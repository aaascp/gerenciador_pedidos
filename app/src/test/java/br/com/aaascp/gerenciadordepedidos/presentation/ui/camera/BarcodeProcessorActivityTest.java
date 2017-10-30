package br.com.aaascp.gerenciadordepedidos.presentation.ui.camera;

import android.app.AlertDialog;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 09/10/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BarcodeProcessorActivityTest {
    private static final int ORDER_ID = 1000;
    private static final int CODE_BASE = 1000;
    private static final String CODE_INVALID = "CODE_INVALID";

    private BarcodeProcessorActivity activity;

    private CodesToProcess codesToProcess;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private void init(int... left) {
        Map<String, Integer> codes = new HashMap<>();

        for (int i = 0; i < left.length; i++) {
            codes.put(String.valueOf(CODE_BASE + i), left[i]);
        }

        codesToProcess = CodesToProcess.create(codes, ORDER_ID);

        Intent intent = new Intent();
        intent.putExtra(
                BarcodeProcessorActivity.EXTRA_CODES_TO_PROCESS,
                codesToProcess);
        intent.putExtra(
                BarcodeProcessorActivity.EXTRA_ORDER_ID,
                ORDER_ID);

        activity = Robolectric.buildActivity(BarcodeProcessorActivity.class, intent)
                .create()
                .start()
                .get();
    }

    @Test
    public void onCreate_setupToolbar() {
        init();

        assertThat(
                activity.title.getText().toString(),
                containsString(String.valueOf(ORDER_ID)));
    }

    @Test
    public void onCreate_setItemsLeft() {
        init(1, 2);

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("3"));
    }

    @Test
    public void onCreate_setItemsLeft_finished() {
        init(0);

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("0"));
    }

    @Test
    public void onItemProcessed_success_changesItemsLeft() {
        init(2);

        activity.onItemProcessed(String.valueOf(CODE_BASE));

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("1"));
    }

    @Test
    public void onItemProcessed_success_finish() {
        init(1);

        activity.onItemProcessed(String.valueOf(CODE_BASE));

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.barcode_processor_finish_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.barcode_processor_finish_dialog_message));
    }


    @Test
    public void onItemProcessed_alreadyProcessed_notChangesItemsLeft() {
        init(1);

        activity.onItemProcessed(String.valueOf(CODE_BASE));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        activity.onItemProcessed(String.valueOf(CODE_BASE));

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("0"));
    }

    @Test
    public void onItemProcessed_codeInvalid_notChangesItemsLeft() {
        init(1);

        activity.onItemProcessed(String.valueOf(CODE_INVALID));

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("1"));
    }

    @Test
    public void onItemProcessed_notReady_notProcesses() {
        init(2);

        activity.onItemProcessed(String.valueOf(CODE_BASE));
        activity.onItemProcessed(String.valueOf(CODE_BASE));

        assertThat(
                activity.itemsLeft.getText().toString(),
                containsString("1"));
    }

    @Test
    public void onClose_setsResult() {
        init(1, 1);

        activity.onItemProcessed(String.valueOf(CODE_BASE));
        activity.finish();

        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                BarcodeProcessorActivity.RESULT_OK,
                shadowActivity.getResultCode());
        assertEquals(
                shadowActivity.getResultIntent().getParcelableExtra(
                        BarcodeProcessorActivity.EXTRA_RESULT),
                codesToProcess);

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onFinish_setsResult() {
        init(1);

        activity.onItemProcessed(String.valueOf(CODE_BASE));
        activity.finish();

        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                BarcodeProcessorActivity.RESULT_OK,
                shadowActivity.getResultCode());
        assertEquals(
                shadowActivity.getResultIntent().getParcelableExtra(
                        BarcodeProcessorActivity.EXTRA_RESULT),
                codesToProcess);

        assertTrue(shadowActivity.isFinishing());
    }
}