package br.com.aaascp.gerenciadordepedidos.repository.callback;

import java.util.List;

/**
 * Created by andre on 10/07/17.
 */

public abstract class RepositoryCallback<T> {

    public void onSuccess(T data) {
    }

    public void onError(List<String> error) {
    }
}
