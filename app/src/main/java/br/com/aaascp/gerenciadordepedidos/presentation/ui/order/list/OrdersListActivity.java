package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.models.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.utils.filter.OrderFilter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public final class OrdersListActivity extends BaseActivity {

    public static final String RESULT_ORDER_PROCESS = "RESULT_ORDER_PROCESS";
    private static final int CODE_ORDER_PROCESS = 100;

    private static final String EXTRA_ORDER_FILTER_EXTRA = "EXTRA_ORDER_FILTER_EXTRA";

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.orders_list_recycler)
    RecyclerView recyclerView;

    private OrdersRepository ordersRepository;
    private OrderFilter filter;
    private List<Order> orders;

    public static void startForContext(Context context, OrderFilter orderFilter) {
        Intent intent =
                new Intent(
                        context,
                        OrdersListActivity.class);

        intent.putExtra(EXTRA_ORDER_FILTER_EXTRA, orderFilter);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orders_list);
        ButterKnife.bind(this);

        ordersRepository = new OrdersRepository();

        extractExtras();
        setupToolbar();
        setupOrdersList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        Bundle extras = data.getExtras();
        if (requestCode == CODE_ORDER_PROCESS &&
                extras != null &&
                resultCode == RESULT_OK) {

            processOrderAtPosition(extras.getInt(RESULT_ORDER_PROCESS));
        }
    }

    private void extractExtras() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            filter = extra.getParcelable(EXTRA_ORDER_FILTER_EXTRA);
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
        ordersRepository.getList(
                filter,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> result) {
                        orders = result;
                        showOrdersList();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        showError();
                    }
                });
    }

    private void showOrdersList() {
        recyclerView.setAdapter(
                new OrdersListAdapter(
                        this,
                        orders));
    }

    private void showError() {
//        recyclerView.setAdapter(
//                new OrdersListAdapter(
//                        this,
//                        orders));
    }

    private void processOrderAtPosition(int position) {
        if (position >= orders.size()) {

            setupOrdersList();
            return;
        }

        Intent intent =
                OrderDetailsActivity.getIntentForOrder(
                        this,
                        orders.get(position).id(),
                        position + 1,
                        orders.size());

        startActivityForResult(intent, CODE_ORDER_PROCESS);
    }

    @OnClick(R.id.orders_list_fab)
    void onFabClick() {
        processOrderAtPosition(0);
    }
}
