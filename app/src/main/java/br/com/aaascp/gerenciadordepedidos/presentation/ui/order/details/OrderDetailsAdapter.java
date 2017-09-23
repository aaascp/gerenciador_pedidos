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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 20/09/17.
 */

class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private final Map<String, OrderItem> items;
    private final List<String> index;
    private final LayoutInflater layoutInflater;

    OrderDetailsAdapter(
            Context context,
            Map<String, OrderItem> items) {

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
        OrderItem orderItem = items.get(code);

        holder.cod.setText(
                String.valueOf(orderItem.code()));

        holder.quantity.setText(
                String.valueOf(orderItem.quantity()));

        holder.description.setText(orderItem.description());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_item_image)
        ImageView image;

        @BindView(R.id.order_item_code_value)
        TextView cod;

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
