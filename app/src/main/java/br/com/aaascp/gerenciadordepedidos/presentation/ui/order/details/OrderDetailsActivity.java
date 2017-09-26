package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.camera.BarcodeProcessorActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list.OrdersListActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
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

    @BindView(R.id.order_details_count_text)
    TextView processedCountView;

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
            return true;
        } else if (id == R.id.menu_order_details_clear) {
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
            setItemsLeft();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(OrdersListActivity.RESULT_ORDER_PROCESS, current++);
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
                        setItemsLeft();
                        showOrder();
                    }
                }
        );
    }


    private void setupToolbar() {
        setupTitle();

        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    private void setItemsLeft() {
        int total = order.size();

        processedCountView.setText(
                String.format(
                        getString(R.string.order_details_count_text),
                        total - codesToProcess.itemsLeft(),
                        total));
    }

    private void showOrder() {
        recyclerView.setAdapter(
                new OrderDetailsAdapter(
                        this,
                        order.items()));
    }

    private void showError() {

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
}
