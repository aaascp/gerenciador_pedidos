package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.utils.ImageLoader;
import br.com.aaascp.gerenciadordepedidos.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 20/09/17.
 */

class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private final Context context;
    private final Map<String, OrderItem> items;
    private final List<String> index;
    private final LayoutInflater layoutInflater;

    OrderDetailsAdapter(
            Context context,
            Map<String, OrderItem> items) {

        this.context = context;
        this.items = items;

        index = new ArrayList<>();
        index.addAll(items.keySet());

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                layoutInflater.inflate(
                        R.layout.row_order_items,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String code = index.get(position);
        final OrderItem item = items.get(code);

        String imageUrl = item.imageUrl();
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            ImageLoader.loadImage(
                    context,
                    imageUrl,
                    holder.image);
        }

        holder.code.setText(
                String.valueOf(item.code()));

        holder.quantity.setText(
                String.valueOf(item.quantity()));

        holder.description.setText(item.description());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderItemActivity.startForItem(context, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_item_root)
        View root;

        @BindView(R.id.order_item_image)
        ImageView image;

        @BindView(R.id.order_item_code_value)
        TextView code;

        @BindView(R.id.order_item_quantity_value)
        TextView quantity;

        @BindView(R.id.order_item_description_value)
        TextView description;


        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
