package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 27/09/17.
 */

public final class OrderItemActivity extends BaseActivity implements OrderItemContract.View {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";

    @BindView(R.id.order_item_toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_item_image)
    ImageView imageView;

    @BindView(R.id.order_item_description)
    TextView descriptionView;

    private OrderItemContract.Presenter presenter;

    public static void startForItem(Context context, OrderItem item) {
        Intent intent = new Intent(context, OrderItemActivity.class);

        intent.putExtra(EXTRA_ITEM, item);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_item);
        ButterKnife.bind(this);

        new OrderItemPresenter(this, getOrderItemExtra());
    }

    private OrderItem getOrderItemExtra() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            return extras.getParcelable(EXTRA_ITEM);
        }

        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.start();
    }

    @Override
    public void setupToolbar(String code) {
        toolbar.setNavigationIcon(R.drawable.ic_close_white_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setTitle(
                String.format(
                        getString(R.string.order_item_title),
                        code));
    }

    @Override
    public void loadImage(@NonNull String imageUrl) {
        ImageLoader.loadImage(
                this,
                imageUrl,
                imageView);
    }

    @Override
    public void setDescription(String description) {
        descriptionView.setText(description);
    }

    @Override
    public void setPresenter(@NonNull OrderItemContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
