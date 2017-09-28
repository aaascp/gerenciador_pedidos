package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.models.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 10/07/17.
 */
class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orders;
    private final LayoutInflater layoutInflater;
    private final OnClickListener listener;

    OrdersListAdapter(
            Context context,
            List<Order> orders,
            OnClickListener listener) {

        this.context = context;
        this.orders = orders;
        this.listener = listener;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                layoutInflater.inflate(
                        R.layout.row_orders_list,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Order order = orders.get(position);
        boolean isProcessed = order.isProcessed();

        holder.id.setText(
                String.valueOf(
                        order.id()));

        holder.shipType.setText(order.shipmentInfo().shipType());

        holder.itemsCount.setText(
                String.valueOf(
                        order.items().size()));

        holder.lastModifiedAt.setText(order.lastModifiedAt());

        holder.processedAt.setTextColor(
                ContextCompat.getColor(
                        context,
                        isProcessed ? R.color.green : R.color.red));

        holder.processedAt.setText(getProcessedAt(order));

        holder.action.setText(
                context.getString(
                        isProcessed ? R.string.orders_list_action_details : R.string.orders_list_action_process));

        holder.root.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(order.id());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getProcessedAt(Order order) {
        if (order.isProcessed()) {
            return order.processedAt();
        }

        return context.getString(R.string.order_list_processed_at_empty);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_root)
        View root;

        @BindView(R.id.order_id_value)
        TextView id;

        @BindView(R.id.order_ship_type_value)
        TextView shipType;

        @BindView(R.id.order_size_value)
        TextView itemsCount;

        @BindView(R.id.order_processed_at_value)
        TextView processedAt;

        @BindView(R.id.order_last_modification_date_value)
        TextView lastModifiedAt;

        @BindView(R.id.order_action_text)
        TextView action;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    interface OnClickListener {
        void onClick(int id);
    }
}
