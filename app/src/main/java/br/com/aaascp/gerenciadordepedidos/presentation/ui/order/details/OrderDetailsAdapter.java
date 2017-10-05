package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import br.com.aaascp.gerenciadordepedidos.entity.CodesToProcess;
import br.com.aaascp.gerenciadordepedidos.entity.OrderItem;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.item.OrderItemActivity;
import br.com.aaascp.gerenciadordepedidos.presentation.util.ImageLoader;
import br.com.aaascp.gerenciadordepedidos.util.StringUtils;
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

    private CodesToProcess codesProcessed;

    OrderDetailsAdapter(
            Context context,
            Map<String, OrderItem> items,
            CodesToProcess codesProcessed) {

        this.context = context;
        this.items = items;
        this.codesProcessed = codesProcessed;

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

        int quantity = item.quantity();
        int itemsLeft = quantity - codesProcessed.codes().get(item.code());

        String imageUrl = item.imageUrl();
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            ImageLoader.loadImage(
                    context,
                    imageUrl,
                    holder.image);
        }

        holder.code.setText(
                String.valueOf(item.code()));

        holder.quantity.setTextColor(
                ContextCompat.getColor(
                        context,
                        itemsLeft == quantity ? R.color.green : R.color.red));

        holder.quantity.setText(
                String.format(
                        context.getString(R.string.order_details_count_text),
                        itemsLeft,
                        quantity));

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

    public void updateCodesProcessed(CodesToProcess codesProcessed) {
        this.codesProcessed = codesProcessed;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

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
