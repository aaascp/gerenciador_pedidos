package br.com.aaascp.gerenciadordepedidos.presentation.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by andre on 27/09/17.
 */

public final class ImageLoader {

    public static void loadImage(
            Context context,
            String url,
            ImageView imageView) {

        Picasso.with(context)
                .load(url)
                .into(imageView);
    }
}
