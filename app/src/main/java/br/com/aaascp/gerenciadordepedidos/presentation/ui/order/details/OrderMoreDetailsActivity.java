package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 27/09/17.
 */

public class OrderMoreDetailsActivity extends BaseActivity {

    private static final String EXTRA_ORDER = "EXTRA_ORDER";


    @BindView(R.id.order_details_more_details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.more_details_order_size_value)
    TextView orderSize;

    @BindView(R.id.more_details_order_processed_at_value)
    TextView orderProcessedAt;

    @BindView(R.id.more_details_order_last_modification_value)
    TextView orderLastModification;

    @BindView(R.id.more_details_shipment_type_value)
    TextView shipmentType;

    @BindView(R.id.more_details_shipment_address_value)
    TextView shipmentAddress;

    @BindView(R.id.more_details_customer_id_value)
    TextView customerId;

    @BindView(R.id.more_details_customer_name_value)
    TextView customerName;

    private Order order;


    public static void startForOrder(Context context, Order order) {
        Intent intent = new Intent(context, OrderMoreDetailsActivity.class);

        intent.putExtra(EXTRA_ORDER, order);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details_more_details);
        ButterKnife.bind(this);

        extractExtras();
        bindView();
        setupTitle();
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            order = extras.getParcelable(EXTRA_ORDER);
        }
    }

    private void setupTitle() {
        toolbar.setTitle(
                String.format(
                        getString(R.string.more_details_title),
                        order.id()));
        toolbar.setNavigationIcon(R.drawable.ic_back_white_vector);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void bindView() {
        orderSize.setText(String.valueOf(order.size()));
        orderProcessedAt.setText(getProcessedAt());
        orderLastModification.setText(order.lastModifiedAt());

        shipmentType.setText(order.shipmentInfo().shipType());
        shipmentAddress.setText(order.shipmentInfo().address());

        customerId.setText(
                String.valueOf(
                        order.customerInfo().id()));
        customerName.setText(order.customerInfo().name());
    }

    private String getProcessedAt() {
        if (order.isProcessed()) {
            return order.processedAt();
        }

        return getString(R.string.more_details_order_processed_at_empty);
    }
}
