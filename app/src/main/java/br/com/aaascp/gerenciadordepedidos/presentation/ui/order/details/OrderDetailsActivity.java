package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.custom_views.ValueLabelView;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.utils.DateFormatterUtils;
import br.com.aaascp.gerenciadordepedidos.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public final class OrderDetailsActivity extends BaseActivity {

    private static final int REQUEST_CODE = 100;

    private static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";
    private static final String EXTRA_TOTAL = "EXTRA_TOTAL";
    private static final String EXTRA_CURRENT = "EXTRA_CURRENT";

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

    private CodesToProcess codesToProcess;
    private OrdersRepository ordersRepository;
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

    public static void startForOrder(Context context, int orderId) {
        Intent intent = getIntentForOrder(
                context,
                orderId,
                1,
                1);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        ordersRepository = new OrdersRepository();

        extractExtras();
        setupOrder();
        setupToolbar();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        Bundle extras = data.getExtras();
        if (resultCode == RESULT_OK &&
                requestCode == REQUEST_CODE &&
                extras != null) {

            codesToProcess = extras.getParcelable(BarcodeProcessorActivity.EXTRA_RESULT);
            checkFinish();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(OrdersListActivity.RESULT_ORDER_PROCESS, current + 1);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getInt(EXTRA_ORDER_ID, 0);
            total = extras.getInt(EXTRA_TOTAL, 1);
            current = extras.getInt(EXTRA_CURRENT, 1);
        }
    }

    private void setupOrder() {
        ordersRepository.getOrder(
                orderId,
                new RepositoryCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        order = data;
                        codesToProcess = order.codesToProcess();
                        checkFinish();
                        showOrder();
                    }
                }
        );
    }

    private void finishOrder() {
        ordersRepository.save(
                order.withProcessedAt(
                        DateFormatterUtils
                                .getDateHourInstance()
                                .now()));
        finish();
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

    private void setItemsLeft(int itemsLeftCount) {
        int total = order.size();

        itemsLeftView.setText(
                String.format(
                        getString(R.string.order_details_count_text),
                        total - itemsLeftCount,
                        total));
    }

    private void showOrder() {
        recyclerView.setAdapter(
                new OrderDetailsAdapter(
                        this,
                        order.items()));
    }

    private void showDetails() {
        if (order == null) {
            return;
        }

        OrderMoreDetailsActivity.startForOrder(this, order);
    }

    @Override
    public void onBackPressed() {
        if (order.isProcessed()) {
            super.onBackPressed();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.order_details_back_dialog_title)
                .setMessage(R.string.order_details_back_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                OrderDetailsActivity.super.onBackPressed();
                            }
                        })
                .setNegativeButton(
                        R.string.dialog_cancel,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        builder.show();
    }

    private void showSkipDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.order_details_skip_dialog_title)
                .setMessage(R.string.order_details_skip_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                .setNegativeButton(
                        R.string.dialog_cancel,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        builder.show();
    }

    private void showClearDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.order_details_clear_dialog_title)
                .setMessage(R.string.order_details_clear_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                setupOrder();
                            }
                        })
                .setNegativeButton(
                        R.string.dialog_cancel,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        builder.show();
    }

    private void showCloseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.order_details_close_dialog_title)
                .setMessage(R.string.order_details_close_dialog_message)
                .setPositiveButton(
                        R.string.dialog_ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                current = total;
                                finish();
                            }
                        })
                .setNegativeButton(
                        R.string.dialog_cancel,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        builder.show();
    }

    @OnClick(R.id.order_details_fab)
    void onFabClick() {
        startActivityForResult(
                BarcodeProcessorActivity.getIntentForOrder(
                        this,
                        orderId,
                        codesToProcess),
                REQUEST_CODE);
    }

    @OnClick(R.id.order_details_finish)
    void onFinishedClick() {
        finishOrder();
    }

    @OnClick(R.id.order_details_processed)
    void onAlreadyProcessedClick() {
        finish();
    }

    @OnClick(R.id.order_details_ship_type)
    void onShipTypeClick() {
        showDetails();
    }
}
