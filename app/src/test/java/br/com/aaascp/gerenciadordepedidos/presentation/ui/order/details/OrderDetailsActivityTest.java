package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

import java.util.HashMap;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.factories.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item.OrderItemActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 05/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrderDetailsActivityTest {

    private static final Order ORDER =
            OrdersFactory.createOrder(1000, true);

    @Mock
    private OrderDetailsContract.Presenter presenter;

    private OrderDetailsActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.setupActivity(OrderDetailsActivity.class);

        activity.setPresenter(presenter);
    }

    @Test
    public void onActivityResult() throws Exception {
        activity.navigateToProcessor(ORDER.codesToProcess());

        ShadowActivity.IntentForResult intentResult =
                shadowOf(activity).getNextStartedActivityForResult();

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER.codesToProcess().codes().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER.id());

        Intent result = new Intent();
        result.putExtra(
                BarcodeProcessorActivity.EXTRA_RESULT,
                newCodesToProcess);

        shadowOf(activity).receiveResult(
                intentResult.intent,
                Activity.RESULT_OK,
                result);

        assertEquals(
                intentResult.requestCode,
                OrderDetailsActivity.REQUEST_CODE_PROCESS);
        verify(presenter).onProcessorResult(newCodesToProcess);
    }

    @Test
    public void onStart() throws Exception {
        activity.onStart();
        verify(presenter).start();
    }

    @Test
    public void hideClearButton() throws Exception {
        activity.hideClearButton();

        assertEquals(
                activity.toolbar.getMenu().findItem(R.id.menu_order_details_clear).isVisible(),
                false);
    }

    @Test
    public void hideSkipButton() throws Exception {
        activity.hideSkipButton();

        assertEquals(
                activity.toolbar.getMenu().findItem(R.id.menu_order_details_skip).isVisible(),
                false);
    }

    @Test
    public void updateCodesProcessed() throws Exception {
        activity.showOrder(ORDER, ORDER.codesToProcess());

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER.codesToProcess().codes().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess = CodesToProcess.create(codes, ORDER.id());

        activity.updateCodesProcessed(newCodesToProcess);

        OrderDetailsAdapter.ViewHolder holder;
        int i = 0;
        for (OrderItem item : ORDER.items().values()) {
            holder = (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                    .findViewHolderForAdapterPosition(i);

            int itemsLeft = item.quantity() - newCodesToProcess.codes().get(item.code());
            String itemsLeftText =
                    String.format(
                            activity.getString(R.string.order_details_count_text),
                            itemsLeft,
                            item.quantity());

            assertEquals(
                    holder.code.getText().toString(),
                    item.code());
            assertEquals(
                    holder.quantity.getText().toString(),
                    itemsLeftText);
            assertEquals(
                    holder.description.getText().toString(),
                    item.description());

            i++;
        }
    }

    @Test
    public void setupTitle_unique() throws Exception {
        activity.setupTitle(1000, 1, 1);

        assertThat(
                activity.toolbar.getTitle().toString(),
                containsString("#1000"));
    }

    @Test
    public void setupTitle_processAll() throws Exception {
        activity.setupTitle(1000, 1, 2);

        assertThat(
                activity.toolbar.getTitle().toString(),
                containsString("#1000 (1/2)"));
    }

    @Test
    public void setShipType() throws Exception {
        activity.setShipType(ORDER.shipmentInfo().shipType());

        assertEquals(
                activity.shipTypeView.getValue(),
                ORDER.shipmentInfo().shipType());
    }

    @Test
    public void setItemsLeft() throws Exception {
        activity.setItemsLeft(2, 1);

        assertEquals(
                activity.itemsLeftView.getText().toString(),
                "1 / 2");
    }

    @Test
    public void showOrder() throws Exception {
        activity.showOrder(ORDER, ORDER.codesToProcess());

        OrderDetailsAdapter.ViewHolder holder;
        int i = 0;
        for (OrderItem item : ORDER.items().values()) {
            holder = (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                    .findViewHolderForAdapterPosition(i);

            int itemsLeft = item.quantity() - ORDER.codesToProcess().codes().get(item.code());
            String itemsLeftText =
                    String.format(
                            activity.getString(R.string.order_details_count_text),
                            itemsLeft,
                            item.quantity());

            assertEquals(
                    holder.code.getText().toString(),
                    item.code());
            assertEquals(
                    holder.quantity.getText().toString(),
                    itemsLeftText);
            assertEquals(
                    holder.description.getText().toString(),
                    item.description());

            i++;
        }
    }

    @Test
    public void showError() throws Exception {
        String error = "Error";
        activity.showError(error);

        RecyclerView recyclerView =
                (RecyclerView) activity.findViewById(R.id.order_details_recycler);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                error);
    }


    @Test
    public void showCommunicationError() throws Exception {
        activity.showCommunicationError();
        RecyclerView recyclerView =
                (RecyclerView) activity.findViewById(R.id.order_details_recycler);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);

        String message = activity.getString(R.string.error_communication);
        assertEquals(
                holder.getMessage().getText().toString(),
                message);
    }

    @Test
    public void hideLabels() throws Exception {
        activity.hideLabels();

        assertEquals(
                activity.finishRoot.getVisibility(),
                View.GONE);
        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.GONE);
        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.GONE);
    }

    @Test
    public void showFinishLabel() throws Exception {
        activity.showFinishLabel();

        assertEquals(
                activity.finishRoot.getVisibility(),
                View.VISIBLE);
    }

    @Test
    public void showItemsLeftLabel() throws Exception {
        activity.showItemsLeftLabel();

        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.VISIBLE);
    }

    @Test
    public void showAlreadyProcessedLabel() throws Exception {
        activity.showAlreadyProcessedLabel();

        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.VISIBLE);
    }

    @Test
    public void navigateToDetails() throws Exception {
        activity.showOrder(
                ORDER,

                ORDER.codesToProcess());

        OrderDetailsAdapter.ViewHolder holder =
                (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        holder.root.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent intent = shadowActivity.getNextStartedActivity();

        assertEquals(
                intent.getParcelableExtra(OrderItemActivity.EXTRA_ITEM),
                ORDER.items().get(holder.code.getText().toString()));
    }

    @Test
    public void finishSkip() throws Exception {
        activity.finishSkip();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_SKIP,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void finishClose() throws Exception {
        activity.finishClose();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_CLOSE,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void finishOk() throws Exception {
        activity.finishOk();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void finishOkUnique() throws Exception {
        activity.finishOkUnique();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK_UNIQUE,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onBackPressed() throws Exception {
        activity.onBackPressed();

        verify(presenter).onBackPressed();
    }

    @Test
    public void showBackDialog() throws Exception {
        activity.showBackDialog();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_back_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_back_dialog_message));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(presenter).onBackDialogOk();
    }

    @Test
    public void showSkipDialog() throws Exception {
        activity.showSkipDialog();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_skip_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_skip_dialog_message));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(presenter).onSkipDialogOk();
    }

    @Test
    public void showClearDialog() throws Exception {
        activity.showClearDialog();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_clear_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_clear_dialog_message));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(presenter).onClearDialogOk();
    }

    @Test
    public void showCloseDialog() throws Exception {
        activity.showCloseDialog();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_close_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_close_dialog_message));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(presenter).onCloseDialogOk();
    }

    @Test
    public void navigateToProcessor() throws Exception {
        activity.navigateToProcessor(ORDER.codesToProcess());

        ShadowActivity shadowActivity = shadowOf(activity);
        ShadowActivity.IntentForResult intentResult =
                shadowActivity.getNextStartedActivityForResult();

        assertEquals(
                intentResult.intent.getParcelableExtra(
                        BarcodeProcessorActivity.EXTRA_CODES_TO_PROCESS),
                ORDER.codesToProcess());
        assertEquals(
                intentResult.requestCode,
                OrderDetailsActivity.REQUEST_CODE_PROCESS);
    }

    @Test
    public void onFabClick() throws Exception {
        View fab = activity.findViewById(R.id.order_details_fab);
        fab.performClick();

        verify(presenter).onFabClicked();
    }

    @Test
    public void onFinishedClick() throws Exception {
        View onFinishedClick = activity.findViewById(R.id.order_details_finish);
        onFinishedClick.performClick();

        verify(presenter).onFinishClicked();
    }

    @Test
    public void onAlreadyProcessedClick() throws Exception {
        View alreadyProcessedButton = activity.findViewById(R.id.order_details_processed);
        alreadyProcessedButton.performClick();

        verify(presenter).onAlreadyProcessedClicked();
    }

    @Test
    public void onShipTypeClick() throws Exception {
        activity.shipTypeView.performClick();

        verify(presenter).onShipTypeClicked();
    }
}