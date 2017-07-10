package br.com.aaascp.gerenciadordepedidos.repository.callback;

/**
 * Created by andre on 10/07/17.
 */

public interface RepositoryCallback<T> {

    void onSuccess(T data);

    void onError(RepositoryError error);
}
