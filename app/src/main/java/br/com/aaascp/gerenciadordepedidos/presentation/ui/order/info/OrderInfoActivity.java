package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 27/09/17.
 */

public final class OrderInfoActivity extends BaseActivity implements OrderInfoContract.View {

    public static final String EXTRA_ORDER = "EXTRA_ORDER";

    @BindView(R.id.order_info_toolbar)
    Toolbar toolbar;

    @BindView(R.id.info_order_size_value)
    TextView orderSize;

    @BindView(R.id.info_order_processed_at_value)
    TextView orderProcessedAt;

    @BindView(R.id.info_order_last_modification_value)
    TextView orderLastModification;

    @BindView(R.id.info_shipment_type_value)
    TextView shipmentType;

    @BindView(R.id.info_shipment_address_value)
    TextView shipmentAddress;

    @BindView(R.id.info_customer_id_value)
    TextView customerId;

    @BindView(R.id.info_customer_name_value)
    TextView customerName;

    private OrderInfoContract.Presenter presenter;

    public static void startForOrder(Context context, Order order) {
        Intent intent = new Intent(context, OrderInfoActivity.class);

        intent.putExtra(EXTRA_ORDER, order);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_info);
        ButterKnife.bind(this);

        new OrderInfoPresenter(this, getOrderExtra());
    }

    private Order getOrderExtra() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            return extras.getParcelable(EXTRA_ORDER);
        }

        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.start();
    }

    @Override
    public void setupToolbar(int id) {
        toolbar.setTitle(
                String.format(
                        getString(R.string.info_title),
                        id));

        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setProcessedOrderInfo(String size, String processedAt, String lastModification) {
        setOrderInfo(size, processedAt, lastModification);
    }

    @Override
    public void setNotProcessedOrderInfo(String size, String lastModification) {
        setOrderInfo(
                size,
                getString(R.string.info_order_processed_at_empty),
                lastModification);
    }

    @Override
    public void setShipmentInfo(String type, String address) {
        shipmentType.setText(type);
        shipmentAddress.setText(address);
    }

    @Override
    public void setCustomerInfo(String id, String name) {
        customerId.setText(id);
        customerName.setText(name);
    }

    @Override
    public void setPresenter(@NonNull OrderInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void setOrderInfo(String size, String processedAt, String lastModification) {
        orderSize.setText(size);
        orderProcessedAt.setText(processedAt);
        orderLastModification.setText(lastModification);
    }
}
