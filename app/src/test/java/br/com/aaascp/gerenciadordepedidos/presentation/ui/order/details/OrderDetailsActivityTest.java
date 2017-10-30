package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.HashMap;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.BuildConfig;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.factory.OrdersFactory;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info.OrderInfoActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item.OrderItemActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.repository.dao.OrdersFakeDataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by andre on 02/10/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OrderDetailsActivityTest {

    private static final Order ORDER_NOT_FINISHED =
            OrdersFactory.createOrder(1000, false);

    private static final Order ORDER_FINISHED =
            OrdersFactory.createOrder(1001, true);

    private static final Order ORDER_ERROR =
            OrdersFakeDataSource.createOrderError();

    private static final Order ORDER_COMMUNICATION_ERROR =
            OrdersFakeDataSource.createOrderCommunicationError();

    private OrderDetailsActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private void init(Order order, int current, int total) {
        OrdersFakeDataSource.clear();
        OrdersFakeDataSource.addOrder(order);

        Intent intent = new Intent();
        intent.putExtra(
                OrderDetailsActivity.EXTRA_ORDER_ID,
                order.id());
        intent.putExtra(
                OrderDetailsActivity.EXTRA_TOTAL,
                total);
        intent.putExtra(
                OrderDetailsActivity.EXTRA_CURRENT,
                current);

        activity = Robolectric.buildActivity(OrderDetailsActivity.class, intent)
                .create()
                .start()
                .get();

        activity.recyclerView.measure(0, 0);
        activity.recyclerView.layout(0, 0, 100, 1000);
    }

    private void init(Order order) {
        init(order, 1, 1);
    }

    @Test
    public void setupTitle_unique() throws Exception {
        int current = 1;
        int total = 1;
        init(ORDER_FINISHED, current, total);

        assertThat(
                activity.toolbar.getTitle().toString(),
                containsString(String.valueOf(ORDER_FINISHED.id())));
    }

    @Test
    public void setupTitle_processAll() throws Exception {
        int current = 1;
        int total = 2;
        init(ORDER_FINISHED, current, total);

        assertThat(
                activity.toolbar.getTitle().toString(),
                containsString(
                        String.format("%d (%d/%d)", ORDER_FINISHED.id(),
                                current,
                                total)));
    }

    @Test
    public void setShipType() throws Exception {
        init(ORDER_FINISHED);

        assertEquals(
                activity.shipTypeView.getValue(),
                ORDER_FINISHED.shipmentInfo().shipType());
    }

    @Test
    public void setItemsLeft() throws Exception {
        init(ORDER_NOT_FINISHED);

        assertEquals(
                activity.itemsLeftView.getText().toString(),
                "0 / 2");
    }

    @Test
    public void showOrder() throws Exception {
        init(ORDER_FINISHED);

        OrderDetailsAdapter.ViewHolder holder;
        int i = 0;
        for (OrderItem item : ORDER_FINISHED.items().values()) {
            holder = (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                    .findViewHolderForAdapterPosition(i);

            int itemsLeft =
                    item.quantity() - ORDER_FINISHED.codesToProcess().codes().get(item.code());

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
        init(ORDER_ERROR);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        assertEquals(
                holder.getMessage().getText().toString(),
                OrdersFakeDataSource.ERROR_MESSAGE);
    }

    @Test
    public void showCommunicationError() throws Exception {
        init(ORDER_COMMUNICATION_ERROR);

        EmptyStateAdapter.ViewHolder holder =
                (EmptyStateAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        String message = activity.getString(R.string.error_communication);
        assertEquals(
                holder.getMessage().getText().toString(),
                message);
    }

    @Test
    public void showItemsLeftLabel() throws Exception {
        init(ORDER_NOT_FINISHED);

        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.VISIBLE);
    }

    @Test
    public void showAlreadyProcessedLabel() throws Exception {
        init(ORDER_FINISHED);

        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.VISIBLE);
    }

    @Test
    public void navigateToDetails() throws Exception {
        init(ORDER_FINISHED);

        OrderDetailsAdapter.ViewHolder holder =
                (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                        .findViewHolderForAdapterPosition(0);

        holder.root.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent intent = shadowActivity.getNextStartedActivity();

        OrderItem item = ORDER_FINISHED.items().get(holder.code.getText().toString());
        assertEquals(intent.getParcelableExtra(OrderItemActivity.EXTRA_ITEM), item);
    }

    @Test
    public void finishOk_save() throws Exception {
        init(ORDER_NOT_FINISHED);

        activity.onFinishedClick();
        Order order = OrdersFakeDataSource.load(ORDER_NOT_FINISHED.id());

        assertTrue(order.isProcessed());
    }

    @Test
    public void finishOk_list() throws Exception {
        init(ORDER_FINISHED, 1, 2);

        activity.onFinishedClick();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void finishOk_unique() throws Exception {
        init(ORDER_FINISHED, 1, 1);

        activity.onFinishedClick();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK_UNIQUE,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onBackPressed_orderFinished_finish() throws Exception {
        init(ORDER_FINISHED);

        activity.onBackPressed();
        assertTrue(shadowOf(activity).isFinishing());
    }

    @Test
    public void onBackPressed_orderNotFinished_showBackDialog() throws Exception {
        init(ORDER_NOT_FINISHED);
        activity.onBackPressed();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_back_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_back_dialog_message));
    }

    @Test
    public void showBackDialogOk_finish() throws Exception {
        init(ORDER_NOT_FINISHED);
        activity.onBackPressed();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertTrue(shadowOf(activity).isFinishing());
    }

    @Test
    public void showSkipDialogOk_finishSkip() throws Exception {
        init(ORDER_FINISHED, 1, 2);

        ShadowActivity shadowActivity = shadowOf(activity);
        MenuItem menuItem = new RoboMenuItem(R.id.menu_order_details_skip);
        activity.onOptionsItemSelected(menuItem);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertEquals(
                OrdersListActivity.RESULT_CODE_SKIP,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void showClearDialogOk_refreshOrder() throws Exception {
        init(ORDER_NOT_FINISHED);

        MenuItem menuItem = new RoboMenuItem(R.id.menu_order_details_clear);
        activity.onOptionsItemSelected(menuItem);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        shadowOf(activity).grantPermissions(Manifest.permission.CAMERA);
        activity.onFabClick();

        ShadowActivity.IntentForResult intentResult =
                shadowOf(activity).getNextStartedActivityForResult();

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_FINISHED.codesToProcess().codes().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_FINISHED.id());

        Intent result = new Intent();
        result.putExtra(BarcodeProcessorActivity.EXTRA_RESULT, newCodesToProcess);

        shadowOf(activity).receiveResult(
                intentResult.intent,
                Activity.RESULT_OK,
                result);

        activity.recyclerView.measure(0, 0);
        activity.recyclerView.layout(0, 0, 100, 1000);

        OrderDetailsAdapter.ViewHolder holder;
        int i = 0;
        for (OrderItem item : ORDER_NOT_FINISHED.items().values()) {
            holder = (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                    .findViewHolderForAdapterPosition(i);

            int itemsLeft =
                    item.quantity() - newCodesToProcess.codes().get(item.code());

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
    public void onMoreDetailsClick_showDetails() throws Exception {
        init(ORDER_FINISHED);

        MenuItem menuItem =
                new RoboMenuItem(R.id.menu_order_details_more_details);
        activity.onOptionsItemSelected(menuItem);

        Intent startedIntent = shadowOf(activity).getNextStartedActivity();

        assertEquals(
                startedIntent.getParcelableExtra(OrderInfoActivity.EXTRA_ORDER),
                ORDER_FINISHED);
    }

    @Test
    public void onClearClick_showClearDialog() throws Exception {
        init(ORDER_FINISHED);

        MenuItem menuItem = new RoboMenuItem(R.id.menu_order_details_clear);
        activity.onOptionsItemSelected(menuItem);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_clear_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_clear_dialog_message));
    }

    @Test
    public void onSkipClick_showSkipDialog() throws Exception {
        init(ORDER_FINISHED);

        MenuItem menuItem = new RoboMenuItem(R.id.menu_order_details_skip);
        activity.onOptionsItemSelected(menuItem);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(dialog);

        assertEquals(
                shadowDialog.getTitle().toString(),
                activity.getString(R.string.order_details_skip_dialog_title));
        assertEquals(
                shadowDialog.getMessage().toString(),
                activity.getString(R.string.order_details_skip_dialog_message));
    }

    @Test
    public void onFinishedClick_finishOk_list() throws Exception {
        init(ORDER_FINISHED, 1, 2);

        ShadowActivity shadowActivity = shadowOf(activity);
        View onFinishedClick = activity.findViewById(R.id.order_details_finish);
        onFinishedClick.performClick();

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onFinishedClick_finishOk_unique() throws Exception {
        init(ORDER_FINISHED, 1, 1);

        ShadowActivity shadowActivity = shadowOf(activity);
        View onFinishedClick = activity.findViewById(R.id.order_details_finish);
        onFinishedClick.performClick();

        assertEquals(
                OrdersListActivity.RESULT_CODE_OK_UNIQUE,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onAlreadyProcessedClick_finishClose() throws Exception {
        init(ORDER_FINISHED, 1, 2);

        ShadowActivity shadowActivity = shadowOf(activity);
        View onAlreadyProcessedClick = activity.findViewById(R.id.order_details_processed);
        onAlreadyProcessedClick.performClick();

        assertEquals(
                OrdersListActivity.RESULT_CODE_CLOSE,
                shadowActivity.getResultCode());

        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void onShipTypeClick_showDetails() throws Exception {
        init(ORDER_FINISHED);

        View onShipTypeClick = activity.findViewById(R.id.order_details_ship_type);
        onShipTypeClick.performClick();

        Intent startedIntent = shadowOf(activity).getNextStartedActivity();

        assertEquals(
                startedIntent.getParcelableExtra(OrderInfoActivity.EXTRA_ORDER),
                ORDER_FINISHED);
    }

    @Test
    public void onFabClick_navigateToProcessor() throws Exception {
        init(ORDER_NOT_FINISHED);

        shadowOf(activity).grantPermissions(Manifest.permission.CAMERA);

        activity.onFabClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        ShadowActivity.IntentForResult intentResult =
                shadowActivity.getNextStartedActivityForResult();

        assertEquals(
                intentResult.intent.getParcelableExtra(
                        BarcodeProcessorActivity.EXTRA_CODES_TO_PROCESS),
                ORDER_NOT_FINISHED.codesToProcess());

        assertEquals(intentResult.requestCode, OrderDetailsActivity.REQUEST_CODE_PROCESS);
    }

    @Test
    public void onActivityResult_refreshCodesProcessed() throws Exception {
        init(ORDER_NOT_FINISHED);

        shadowOf(activity).grantPermissions(Manifest.permission.CAMERA);
        activity.onFabClick();

        ShadowActivity.IntentForResult intentResult =
                shadowOf(activity).getNextStartedActivityForResult();

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_FINISHED.codesToProcess().codes().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_FINISHED.id());

        Intent result = new Intent();
        result.putExtra(BarcodeProcessorActivity.EXTRA_RESULT, newCodesToProcess);

        shadowOf(activity).receiveResult(
                intentResult.intent,
                Activity.RESULT_OK,
                result);

        assertEquals(
                intentResult.requestCode,
                OrderDetailsActivity.REQUEST_CODE_PROCESS);

        activity.recyclerView.measure(0, 0);
        activity.recyclerView.layout(0, 0, 100, 1000);

        OrderDetailsAdapter.ViewHolder holder;
        int i = 0;
        for (OrderItem item : ORDER_NOT_FINISHED.items().values()) {
            holder = (OrderDetailsAdapter.ViewHolder) activity.recyclerView
                    .findViewHolderForAdapterPosition(i);

            int itemsLeft =
                    item.quantity() - newCodesToProcess.codes().get(item.code());

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
    public void onShowOrder_alreadyProcessed() throws Exception {
        init(ORDER_FINISHED);

        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.VISIBLE);
        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.GONE);
        assertEquals(
                activity.finishRoot.getVisibility(),
                View.GONE);
    }

    @Test
    public void onShowOrder_finished() throws Exception {
        init(ORDER_NOT_FINISHED);

        shadowOf(activity).grantPermissions(Manifest.permission.CAMERA);
        activity.onFabClick();

        ShadowActivity.IntentForResult intentResult =
                shadowOf(activity).getNextStartedActivityForResult();

        Map<String, Integer> codes = new HashMap<>();
        for (String code : ORDER_NOT_FINISHED.codesToProcess().codes().keySet()) {
            codes.put(code, 0);
        }
        CodesToProcess newCodesToProcess =
                CodesToProcess.create(codes, ORDER_NOT_FINISHED.id());

        Intent result = new Intent();
        result.putExtra(
                BarcodeProcessorActivity.EXTRA_RESULT,
                newCodesToProcess);

        shadowOf(activity).receiveResult(
                intentResult.intent,
                Activity.RESULT_OK,
                result);

        assertEquals(
                activity.finishRoot.getVisibility(),
                View.VISIBLE);
        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.GONE);
        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.GONE);
    }

    @Test
    public void onShowOrder_showItemsLeft() throws Exception {
        init(ORDER_NOT_FINISHED);

        assertEquals(
                activity.itemsLeftRoot.getVisibility(),
                View.VISIBLE);
        assertEquals(
                activity.alreadyProcessedRoot.getVisibility(),
                View.GONE);
        assertEquals(
                activity.finishRoot.getVisibility(),
                View.GONE);
    }
}
