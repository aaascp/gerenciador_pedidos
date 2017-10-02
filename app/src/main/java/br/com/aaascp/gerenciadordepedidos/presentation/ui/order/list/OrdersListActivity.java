package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.NullOrderFilterList;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.entity.OrderFilterList;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.EmptyStateAdapter;
import br.com.aaascp.gerenciadordepedidos.repository.OrdersRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andre on 09/07/17.
 */

public final class OrdersListActivity extends BaseActivity implements OrdersListContract.View {

    public static final int REQUEST_CODE_ORDER_PROCESS = 100;

    public static final int RESULT_CODE_OK_UNIQUE = 200;
    public static final int RESULT_CODE_OK = 300;
    public static final int RESULT_CODE_SKIP = 400;
    public static final int RESULT_CODE_CLOSE = 500;

    private static final String EXTRA_ORDER_FILTERS = "EXTRA_ORDER_FILTERS";
    private static final String EXTRA_PROCESS_ALL = "EXTRA_PROCESS_ALL";

    @BindView(R.id.orders_list_fab)
    FloatingActionButton fab;

    @BindView(R.id.orders_list_toolbar)
    Toolbar toolbar;

    @BindView(R.id.orders_list_recycler)
    RecyclerView recyclerView;

    OrdersListContract.Presenter presenter;

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

        Bundle extras = getIntent().getExtras();
        if(extras  != null) {
            new OrdersListPresenter(
                    this,
                    getOrderFilterListExtra(extras),
                    new OrdersRepository(),
                    getProcessAllExtra(extras));
        } else {
            new OrdersListPresenter(
                    this,
                    NullOrderFilterList.create(),
                    new OrdersRepository(),
                    false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ORDER_PROCESS) {
            switch (resultCode) {
                case RESULT_CODE_OK:
                case RESULT_CODE_SKIP:
                    presenter.onResultNext();
                    break;
                case RESULT_CODE_OK_UNIQUE:
                case RESULT_CODE_CLOSE:
                default:
                    presenter.onResultClose();
            }
        }
    }

    private OrderFilterList getOrderFilterListExtra(Bundle extras) {
        return extras.getParcelable(EXTRA_ORDER_FILTERS);
    }

    private boolean getProcessAllExtra(Bundle extras) {
        return extras.getBoolean(EXTRA_PROCESS_ALL, false);
    }

    @Override
    public void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void showFab() {
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFab() {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void showOrdersList(List<Order> orders) {
      recyclerView.setAdapter(
                new OrdersListAdapter(
                        this,
                        orders,
                        new OrdersListAdapter.OnClickListener() {
                            @Override
                            public void onClick(int orderId) {
                                presenter.onOrderClicked(orderId);
                            }
                        }));
    }

    @Override
    public void showEmptyList() {
        recyclerView.setAdapter(
                new EmptyStateAdapter(
                        this,
                        R.drawable.ic_coffee_black_vector,
                        getString(R.string.order_list_empty)));
    }

    @Override
    public void showCommunicationError() {
        showError(
                getString(R.string.error_communication));
    }

    @Override
    public void showError(String error) {
        recyclerView.setAdapter(
                new EmptyStateAdapter(
                        this,
                        error));
    }

    @Override
    public void navigateToOrderDetails(int orderId, int position, int total) {
        Intent intent =
                OrderDetailsActivity.getIntentForOrder(
                        this,
                        orderId,
                        position,
                        total);

        startActivityForResult(intent, REQUEST_CODE_ORDER_PROCESS);
    }

    @Override
    public void setPresenter(@NonNull OrdersListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @OnClick(R.id.orders_list_fab)
    void onFabClick() {
        presenter.onFabCLick();
    }
}
