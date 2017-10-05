package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.Inject;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.custom.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info.OrderInfoActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.DialogUtils;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public class OrderDetailsActivity extends BaseActivity implements OrderDetailsContract.View {

    static final int REQUEST_CODE_PROCESS = 100;

    public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";
    public static final String EXTRA_TOTAL = "EXTRA_TOTAL";
    public static final String EXTRA_CURRENT = "EXTRA_CURRENT";

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

    OrderDetailsContract.Presenter presenter;
    private OrderDetailsAdapter orderDetailsAdapter;

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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            new OrderDetailsPresenter(
                    this,
                    Inject.provideOrdersRepository(),
                    extras.getInt(EXTRA_ORDER_ID, Order.INVALID_ORDER_ID),
                    extras.getInt(EXTRA_TOTAL, 1),
                    extras.getInt(EXTRA_CURRENT, 1));
        } else {
            new OrderDetailsPresenter(
                    this,
                    Inject.provideOrdersRepository(),
                    Order.INVALID_ORDER_ID,
                    1,
                    1);
        }
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

            CodesToProcess codesToProcess =
                    extras.getParcelable(
                            BarcodeProcessorActivity.EXTRA_RESULT);

            presenter.onProcessorResult(codesToProcess);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.start();
    }

    @Override
    public void setupMenu() {
        toolbar.inflateMenu(R.menu.menu_order_details);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_order_details_more_details) {
                    presenter.onInfoClicked();
                    return true;
                } else if (id == R.id.menu_order_details_clear) {
                    presenter.onClearClicked();
                    return true;
                } else if (id == R.id.menu_order_details_skip) {
                    presenter.onSkipClicked();
                    return true;
                }
                return false;
            }
        });

        presenter.onMenuCreated();
    }

    @Override
    public void hideClearButton() {
        toolbar.getMenu()
                .findItem(R.id.menu_order_details_clear).setVisible(false);
    }

    @Override
    public void hideSkipButton() {
        toolbar.getMenu()
                .findItem(R.id.menu_order_details_skip).setVisible(false);
    }

    @Override
    public void updateCodesProcessed(CodesToProcess codesToProcess) {
        orderDetailsAdapter.updateCodesProcessed(codesToProcess);
    }

    @Override
    public void setupCloseToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_close_white_vector);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.onCloseClicked();
                    }
                });
    }

    @Override
    public void setupBackToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.onBackPressed();
                    }
                });
    }

    @Override
    public void setupTitle(int id, int current, int total) {
        String title =
                getResources().getQuantityString(
                        R.plurals.order_details_title,
                        total);

        String titleFormatted =
                String.format(
                        title,
                        id,
                        current,
                        total);

        toolbar.setTitle(titleFormatted);
    }

    @Override
    public void setShipType(String shipType) {
        shipTypeView.setValue(shipType);
    }

    @Override
    public void setItemsLeft(int total, int itemsLeftCount) {
        itemsLeftView.setText(
                String.format(
                        getString(R.string.order_details_count_text),
                        total - itemsLeftCount,
                        total));
    }

    @Override
    public void showOrder(Order order, CodesToProcess codesToProcess) {
        orderDetailsAdapter =
                new OrderDetailsAdapter(
                        this,
                        order.items(),
                        codesToProcess);

        recyclerView.setAdapter(orderDetailsAdapter);
    }

    @Override
    public void showError(String error) {
        recyclerView.setAdapter(
                new EmptyStateAdapter(
                        this,
                        error));
    }

    @Override
    public void showCommunicationError() {
        showError(
                getString(R.string.error_communication));
    }

    @Override
    public void hideLabels() {
        finishRoot.setVisibility(View.GONE);
        itemsLeftRoot.setVisibility(View.GONE);
        alreadyProcessedRoot.setVisibility(View.GONE);
    }

    @Override
    public void showFinishLabel() {
        finishRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void showItemsLeftLabel() {
        itemsLeftRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAlreadyProcessedLabel() {
        alreadyProcessedRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToDetails(Order order) {
        OrderInfoActivity.startForOrder(this, order);
    }

    @Override
    public void finishSkip() {
        finishOrder(OrdersListActivity.RESULT_CODE_SKIP);
    }

    @Override
    public void finishClose() {
        finishOrder(OrdersListActivity.RESULT_CODE_CLOSE);
    }

    @Override
    public void finishOk() {
        finishOrder(OrdersListActivity.RESULT_CODE_OK);
    }

    @Override
    public void finishOkUnique() {
        finishOrder(OrdersListActivity.RESULT_CODE_OK_UNIQUE);
    }


    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void finishBack() {
        super.onBackPressed();
    }

    @Override
    public void showBackDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_back_dialog_title,
                R.string.order_details_back_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.onBackDialogOk();
                    }
                }
        );
    }

    @Override
    public void showSkipDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_skip_dialog_title,
                R.string.order_details_skip_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.onSkipDialogOk();
                    }
                }
        );
    }

    @Override
    public void showClearDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_clear_dialog_title,
                R.string.order_details_clear_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.onClearDialogOk();
                    }
                }
        );
    }

    @Override
    public void showCloseDialog() {
        DialogUtils.showGenericDialog(
                this,
                R.string.order_details_close_dialog_title,
                R.string.order_details_close_dialog_message,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.onCloseDialogOk();
                    }
                }
        );
    }

    @Override
    public void navigateToProcessor(CodesToProcess codesToProcess) {
        startActivityForResult(
                BarcodeProcessorActivity.getIntentForOrder(
                        this,
                        codesToProcess),
                REQUEST_CODE_PROCESS);
    }

    @OnClick(R.id.order_details_fab)
    void onFabClick() {
        presenter.onFabClicked();
    }

    @OnClick(R.id.order_details_finish)
    void onFinishedClick() {
        presenter.onFinishClicked();
    }

    @OnClick(R.id.order_details_processed)
    void onAlreadyProcessedClick() {
        presenter.onAlreadyProcessedClicked();
    }

    @OnClick(R.id.order_details_ship_type)
    void onShipTypeClick() {
        presenter.onShipTypeClicked();
    }

    @Override
    public void setPresenter(@NonNull OrderDetailsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void finishOrder(int resultCode) {
        Intent intent = new Intent();
        setResult(resultCode, intent);

        finish();
    }
}
