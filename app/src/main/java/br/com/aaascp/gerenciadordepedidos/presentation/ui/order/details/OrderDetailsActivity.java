package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 09/07/17.
 */

public final class OrderDetailsActivity extends BaseActivity {

    public static final String ORDER_ID_EXTRA = "ORDER_ID_EXTRA";

    @BindView(R.id.order_details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_details_recycler)
    RecyclerView recyclerView;

    private int orderId;

    public static void startForOrder(Context context, int orderId) {
        Intent intent = new Intent(
                context,
                OrderDetailsActivity.class);

        intent.putExtra(ORDER_ID_EXTRA, orderId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        orderId = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getInt(ORDER_ID_EXTRA, 0);
        }

        setupOrder();
    }

    private void setupOrder() {

    }
}
