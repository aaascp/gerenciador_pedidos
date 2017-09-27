package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.BaseActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.utils.ImageLoader;
import br.com.aaascp.gerenciadordepedidos.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 27/09/17.
 */

public class OrderItemActivity extends BaseActivity {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    @BindView(R.id.order_item_toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_item_image)
    ImageView imageView;

    @BindView(R.id.order_item_description)
    TextView descriptionView;

    private OrderItem item;

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

        extractExtras();
        setupToolbar();
        bindView();
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            item = extras.getParcelable(EXTRA_ITEM);
        }
    }

    private void setupToolbar() {
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
                        item.code()));
    }

    private void bindView() {
        String imageUrl = item.imageUrl();
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            ImageLoader.loadImage(
                    this,
                    imageUrl,
                    imageView);
        }

        descriptionView.setText(item.description());
    }
}
