package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.Inject;
import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public final class OrdersListActivity extends BaseActivity {

    public static final int REQUEST_CODE_ORDER_PROCESS = 100;

    public static final int RESULT_CODE_OK_UNIQUE = 200;
    public static final int RESULT_CODE_OK = 300;
    public static final int RESULT_CODE_SKIP = 400;
    public static final int RESULT_CODE_CLOSE = 500;


    static final String EXTRA_ORDER_FILTERS = "EXTRA_ORDER_FILTERS";
    static final String EXTRA_PROCESS_ALL = "EXTRA_PROCESS_ALL";

    @BindView(R.id.orders_list_fab)
    FloatingActionButton fab;

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.orders_list_recycler)
    RecyclerView recyclerView;

    private OrdersRepository ordersRepository;
    private OrderFilterList filterList;
    private List<Order> orders;
    private boolean processAll;
    private int current;

    public static void startForContext(
            Context context,
            OrderFilterList filters,
            boolean processAll) {

        Intent intent =
                new Intent(
                        context,
                        OrdersListActivity.class);

        intent.putExtra(EXTRA_ORDER_FILTERS, filters);
        intent.putExtra(EXTRA_PROCESS_ALL, processAll);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orders_list);
        ButterKnife.bind(this);

        ordersRepository = Inject.provideOrdersRepository();

        extractExtras();
        setupToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupFab();
        setupOrdersList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ORDER_PROCESS) {
            switch (resultCode) {
                case RESULT_CODE_OK:
                case RESULT_CODE_SKIP:
                    ++current;
                    break;
                case RESULT_CODE_OK_UNIQUE:
                case RESULT_CODE_CLOSE:
                default:
                    current = -1;
            }

            processNext();
        }
    }

    private void extractExtras() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            filterList = extra.getParcelable(EXTRA_ORDER_FILTERS);
            processAll = extra.getBoolean(EXTRA_PROCESS_ALL, false);
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupOrdersList() {
        current = 0;

        ordersRepository.getList(
                filterList,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> result) {
                        orders = result;
                        showOrdersList();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        if(errors != null) {
                            showError(errors.get(0));
                        } else {
                            showCommunicationError();
                        }
                    }
                });
    }

    private void setupFab() {
        if (processAll) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void showOrdersList() {
        if(orders.size() == 0) {
            showEmptyList();
            return;
        }

        recyclerView.setAdapter(
                new OrdersListAdapter(
                        this,
                        orders,
                        new OrdersListAdapter.OnClickListener() {
                            @Override
                            public void onClick(int orderId) {
                                process(orderId, 1, 1);
                            }
                        }));
    }

    private void showEmptyList() {
        recyclerView.setAdapter(
                new EmptyStateAdapter(
                        this,
                        R.drawable.ic_coffee_black_vector,
                        getString(R.string.order_list_empty)));
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

    private void processNext() {
        if (current >= orders.size() ||
                current < 0) {
            setupOrdersList();
            return;
        }

        process(orders.get(current).id(),
                current + 1,
                orders.size());
    }

    private void process(int orderId, int position, int total) {
        Intent intent =
                OrderDetailsActivity.getIntentForOrder(
                        this,
                        orderId,
                        position,
                        total);

        startActivityForResult(intent, REQUEST_CODE_ORDER_PROCESS);
    }

    @OnClick(R.id.orders_list_fab)
    void onFabClick() {
        processNext();
    }
}