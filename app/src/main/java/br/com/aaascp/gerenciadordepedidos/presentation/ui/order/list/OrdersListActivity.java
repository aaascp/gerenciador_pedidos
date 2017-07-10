package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.OrdersDomain;
import br.com.aaascp.gerenciadordepedidos.domain.callback.DomainCallback;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 09/07/17.
 */

public final class OrdersListActivity extends BaseActivity {

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.orders_list_recycler)
    RecyclerView recyclerView;

    public static void startForContext(Context context) {
        context.startActivity(
                new Intent(
                        context,
                        OrdersListActivity.class));
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

        setupOrdersList();
    }

    private void setupOrdersList() {
        OrdersDomain.getOrdersList(new DomainCallback<List<Order>>() {
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
