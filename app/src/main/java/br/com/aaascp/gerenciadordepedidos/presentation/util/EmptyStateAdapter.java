package br.com.aaascp.gerenciadordepedidos.presentation.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 28/09/17.
 */

public final class EmptyStateAdapter extends RecyclerView.Adapter<EmptyStateAdapter.ViewHolder> {

    private final static int NULL_DRAWABLE = -1;
    @DrawableRes
    private final int image;

    private final String message;
    private final LayoutInflater inflater;

    public EmptyStateAdapter(
            Context context,
            @DrawableRes int image,
            String message) {

        this.image = image;
        this.message = message;

        inflater = LayoutInflater.from(context);
    }

    public EmptyStateAdapter(
            Context context,
            String message) {

        this.image = NULL_DRAWABLE;
        this.message = message;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(
                        R.layout.row_empty_state,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (image != NULL_DRAWABLE) {
            holder.image.setImageResource(image);
        }

        holder.message.setText(message);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.empty_state_image)
        ImageView image;

        @BindView(R.id.empty_state_message)
        TextView message;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getMessage() {
            return message;
        }
    }
}
