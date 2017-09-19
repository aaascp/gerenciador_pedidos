package br.com.aaascp.gerenciadordepedidos.presentation.ui.order.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.aaascp.gerenciadordepedidos.R;
import br.com.aaascp.gerenciadordepedidos.domain.dto.Order;
import br.com.aaascp.gerenciadordepedidos.presentation.ui.order.details.OrderDetailsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 10/07/17.
 */
class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private LayoutInflater layoutInflater;

    OrdersListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Order order = orders.get(position);

        holder.id.setText(
                String.valueOf(
                        order.id()));

        holder.shipType.setText(order.shipType());

        holder.itemsCount.setText(
                String.valueOf(
                        order.itemsCount()));

        holder.lastModifiedAt.setText(order.lastModifiedAt());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDetailsActivity.startForOrder(
                        context,
                        order.id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_root)
        View root;

        @BindView(R.id.order_id_value)
        TextView id;

        @BindView(R.id.order_ship_type_value)
        TextView shipType;

        @BindView(R.id.order_size_value)
        TextView itemsCount;

        @BindView(R.id.order_last_modification_date_value)
        TextView lastModifiedAt;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}