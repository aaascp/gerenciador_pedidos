package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 09/07/17.
 */

public class OrdersListActivity extends BaseActivity {

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

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
    }
}
