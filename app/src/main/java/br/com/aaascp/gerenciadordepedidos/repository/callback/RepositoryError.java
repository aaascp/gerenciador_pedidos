package br.com.aaascp.gerenciadordepedidos.repository.callback;

import java.util.List;

/**
 * Created by andre on 10/07/17.
 */


public final class RepositoryError {

    static final int UNAUTHORIZED_TYPE = 401;
    static final int FORBIDDEN_TYPE = 403;
    static final int NOT_FOUND_TYPE = 404;

    private final int type;

    private List<String> errors;

    private RepositoryError(int type) {
        this.type = type;
    }

    public static RepositoryError newForbiddenInstance() {
        return new RepositoryError(FORBIDDEN_TYPE);
    }

    public static RepositoryError newUnauthorizedInstance() {
        return new RepositoryError(UNAUTHORIZED_TYPE);
    }

    public static RepositoryError newNotFoundInstance() {
        return new RepositoryError(NOT_FOUND_TYPE);
    }

    public int getType() {
        return type;
    }

    public RepositoryError withErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "RepositoryError{" +
                "type=" + type +
                ", errors=" + errors +
                '}';
    }
}
