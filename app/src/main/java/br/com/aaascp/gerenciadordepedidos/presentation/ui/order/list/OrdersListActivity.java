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
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import br.com.aaascp.gerenciadordepedidos.repository.callback.RepositoryCallback;
import br.com.aaascp.gerenciadordepedidos.repository.utils.filter.OrderFilter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 09/07/17.
 */

public final class OrdersListActivity extends BaseActivity {

    private static final String ORDER_FILTER_TAG = "ORDER_FILTER_TAG";

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.orders_list_recycler)
    RecyclerView recyclerView;

    public static void startForContext(Context context, OrderFilter orderFilter) {
        Intent intent =
                new Intent(
                        context,
                        OrdersListActivity.class);

        intent.putExtra(ORDER_FILTER_TAG, orderFilter);


        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orders_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            OrderFilter filter = extra.getParcelable(ORDER_FILTER_TAG);
            setupOrdersList(filter);
        }
    }

    private void setupOrdersList(OrderFilter filter) {
        OrdersRepository.getList(
                filter,
                new RepositoryCallback<List<Order>>() {
                    @Override
                    public void onSuccess(List<Order> result) {
                        showOrdersList(result);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        showError();
                    }
                });
    }

    private void showOrdersList(List<Order> orders) {
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
}
