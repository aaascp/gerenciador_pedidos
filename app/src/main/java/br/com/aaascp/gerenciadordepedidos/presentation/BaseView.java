package br.com.aaascp.gerenciadordepedidos.presentation;

import android.support.annotation.NonNull;

/**
 * Created by andre on 28/09/17.
 */

public interface BaseView<T> {

    void setPresenter(@NonNull T presenter);
}
