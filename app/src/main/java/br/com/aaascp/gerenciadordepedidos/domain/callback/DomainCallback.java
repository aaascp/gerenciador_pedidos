package br.com.aaascp.gerenciadordepedidos.domain.callback;

import java.util.List;

/**
 * Created by andre on 10/07/17.
 */

public interface DomainCallback<T> {

    void onSuccess(T result);

    void onError(List<String> errors);
}
