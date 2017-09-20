package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.domain.dto.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 09/07/17.
 */

public final class OrderDetailsActivity extends BaseActivity {

    public static final String ORDER_EXTRA = "ORDER_EXTRA";

    @BindView(R.id.order_details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_details_recycler)
    RecyclerView recyclerView;

    private Order order;

    public static void startForOrder(Context context, Order order) {
        Intent intent = new Intent(
                context,
                OrderDetailsActivity.class);

        intent.putExtra(ORDER_EXTRA, order);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            order = extras.getParcelable(ORDER_EXTRA);
        }

        toolbar.setTitle(
                String.format(
                        getString(R.string.order_details_title),
                        order.id()));

        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupOrder();
    }

    private void setupOrder() {
        if (order != null) {
            showOrder();
        } else {
            showError();
        }
    }

    private void showOrder() {
        recyclerView.setAdapter(
                new OrderDetailsAdapter(
                        this,
                        order.items()));
    }

    private void showError() {

    }
}
