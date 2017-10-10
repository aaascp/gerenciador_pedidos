package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.Inject;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.custom.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.util.DateFormatterUtils;
import br.com.aaascp.gerenciadordepedidos.util.PermissionUtils;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info.OrderInfoActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public final class OrderDetailsActivity extends BaseActivity {

    private static final int REQUEST_CODE_PROCESS = 100;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;

    public static final String EXTRA_TOTAL = "EXTRA_TOTAL";
    public static final String EXTRA_CURRENT = "EXTRA_CURRENT";
    public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";

    private static final int INVALID_ORDER_ID = -1;
    private static final int MENU_ITEM_SKIP = 0;

    @BindView(R.id.order_details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_details_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.order_details_items_left)
    TextView itemsLeftView;

    @BindView(R.id.order_details_ship_type)
    ValueLabelView shipTypeView;

    @BindView(R.id.order_details_finish_root)
    View finishRoot;

    @BindView(R.id.order_details_processed_root)
    View alreadyProcessedRoot;

    @BindView(R.id.order_details_items_left_root)
    View itemsLeftRoot;

    private OrdersRepository ordersRepository;
    private OrderDetailsAdapter orderDetailsAdapter;

    private CodesToProcess codesToProcess;
    private Order order;
    private int orderId;
    private int total;
    private int current;

    public static Intent getIntentForOrder(
            Context context,
            int orderId,
            int current,
            int total) {

        Intent intent = new Intent(
                context,
                OrderDetailsActivity.class);

        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_TOTAL, total);
        intent.putExtra(EXTRA_CURRENT, current);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        ordersRepository = Inject.provideOrdersRepository();

        extractExtras();
        setupOrder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        Bundle extras = data.getExtras();
        if (resultCode == RESULT_OK &&
                requestCode == REQUEST_CODE_PROCESS &&
                extras != null) {

            codesToProcess = extras.getParcelable(BarcodeProcessorActivity.EXTRA_RESULT);
            updateCodesProcessed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_details, menu);

        if (total == current) {
            MenuItem item = menu.getItem(MENU_ITEM_SKIP);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_order_details_more_details) {
            showDetails();
            return true;
        } else if (id == R.id.menu_order_details_clear) {
            showClearDialog();
            return true;
        } else if (id == R.id.menu_order_details_skip) {
            showSkipDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getInt(EXTRA_ORDER_ID, INVALID_ORDER_ID);
            total = extras.getInt(EXTRA_TOTAL, 1);
            current = extras.getInt(EXTRA_CURRENT, 1);
        }
    }

    private void setupOrder() {
        if (order != null) {
            showOrder();
            return;
        }

        ordersRepository.getOrder(
                orderId,
                new RepositoryCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        order = data;
                        codesToProcess = order.codesToProcess();

                        showOrder();
                        setupToolbar();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        if (errors != null) {
                            showError(errors.get(0));
                        } else {
                            showCommunicationError();
                        }
                    }
                }
        );
    }

    private void refreshOrder() {
        order = null;
        setupOrder();
    }

    private void updateCodesProcessed() {
        orderDetailsAdapter.updateCodesProcessed(codesToProcess);
        checkFinish();
    }

    private void setupToolbar() {
        setupTitle();
        setSupportActionBar(toolbar);

        if (total > 1) {
            toolbar.setNavigationIcon(R.drawable.ic_close_white_vector);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCloseDialog();
                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        shipTypeView.setValue(order.shipmentInfo().shipType());
    }

    private void setupTitle() {
        String title =
                getResources().getQuantityString(
                        R.plurals.order_details_title,
                        total);

        toolbar.setTitle(
                String.format(
                        title,
                        orderId,
                        current,
                        total));
    }

    private void setItemsLeft(int itemsLeftCount) {
        int total = order.size();

        itemsLeftView.setText(
                String.format(
                        getString(R.string.order_details_count_text),
                        total - itemsLeftCount,
                        total));
    }

    private void showOrder() {
        checkFinish();

        orderDetailsAdapter =
                new OrderDetailsAdapter(
                        this,
                        order.items(),
                        codesToProcess);

        recyclerView.setAdapter(orderDetailsAdapter);
    }

    private void showCommunicationError() {
        showError(
                getString(R.string.error_communication));
    }

    private void showError(String error) {
        recyclerView.setAdapter(
                new EmptyStateAdapter(
                        this,
                        error));
    }

    private void showDetails() {
        if (order == null) {
            return;
        }

        OrderInfoActivity.startForOrder(this, order);
    }

    private void checkFinish() {
        int itemsLeftCount = codesToProcess.itemsLeft();

        finishRoot.setVisibility(View.GONE);
        itemsLeftRoot.setVisibility(View.GONE);
        alreadyProcessedRoot.setVisibility(View.GONE);

        if (order.isProcessed()) {
            alreadyProcessedRoot.setVisibility(View.VISIBLE);
        } else if (itemsLeftCount == 0) {
            finishRoot.setVisibility(View.VISIBLE);
        } else {
            itemsLeftRoot.setVisibility(View.VISIBLE);
            setItemsLeft(itemsLeftCount);
        }
    }

    private void finishOrder(int resultCode) {
        Intent intent = new Intent();
        setResult(resultCode, intent);

        finish();
    }

    private void finishSkip() {
        finishOrder(OrdersListActivity.RESULT_CODE_SKIP);
    }

    private void finishClose() {
        finishOrder(OrdersListActivity.RESULT_CODE_CLOSE);
    }

    private void finishOk() {
        ordersRepository.save(
                order.withProcessedAt(
                        DateFormatterUtils
                                .getDateHourInstance()
                                .now()));

        if (total == 1) {
            finishOrder(OrdersListActivity.RESULT_CODE_OK_UNIQUE);
        } else {
            finishOrder(OrdersListActivity.RESULT_CODE_OK);
        }
    }

    @Override
    public void onBackPressed() {
        if (order.isProcessed()) {
            super.onBackPressed();
            return;
        }

        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_back_dialog_title,
                R.string.order_details_back_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        OrderDetailsActivity.super.onBackPressed();
                    }
                });
    }

    private void showSkipDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_skip_dialog_title,
                R.string.order_details_skip_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finishSkip();
                    }
                });
    }

    private void showClearDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_clear_dialog_title,
                R.string.order_details_clear_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        refreshOrder();
                    }
                });
    }

    private void showCloseDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_close_dialog_title,
                R.string.order_details_close_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finishClose();
                    }
                });
    }

    private void checkPermissionForCamera() {
        if (PermissionUtils.isCameraEnabled(this)) {
            navigateToCamera();
        } else {
            PermissionUtils.requestPermissionForCamera(this, REQUEST_CODE_CAMERA_PERMISSION);
        }
    }

    private void navigateToCamera() {
        startActivityForResult(
                BarcodeProcessorActivity.getIntentForOrder(
                        this,
                        order.id(),
                        codesToProcess),
                REQUEST_CODE_PROCESS);
    }

    @OnClick(R.id.order_details_fab)
    void onFabClick() {
        checkPermissionForCamera();
    }

    @OnClick(R.id.order_details_finish)
    void onFinishedClick() {
        finishOk();
    }

    @OnClick(R.id.order_details_processed)
    void onAlreadyProcessedClick() {
        finishClose();
    }

    @OnClick(R.id.order_details_ship_type)
    void onShipTypeClick() {
        showDetails();
    }
}