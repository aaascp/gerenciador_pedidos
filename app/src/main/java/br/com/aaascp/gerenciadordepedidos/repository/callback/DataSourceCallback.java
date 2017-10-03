package br.com.aaascp.gerenciadordepedidos.repository.callback;

import java.util.List;

/**
 * Created by andre on 03/10/17.
 */

public abstract class DataSourceCallback<T> {

    public void onSuccess(T data) {
    }

    public void onError(List<String> errors) {
    }
}
