package me.nettee.markdown.common;

import java.util.Optional;
import java.util.function.Function;

public class Result<T, E> {

    private enum ResultType {
        OK,
        ERROR,
        ;
    }

    private final ResultType type;

    private final T val;

    private final E err;

    private Result(ResultType type, T val, E err) {
        this.type = type;
        this.val = val;
        this.err = err;
    }

    public static <T> Result<T, T> ok(T val) {
        return new Result<>(ResultType.OK, val, null);
    }

    public static <E> Result<E, E> error(E err) {
        return new Result<>(ResultType.ERROR, null, err);
    }

    public boolean isOk() {
        return type == ResultType.OK;
    }

    public boolean isError() {
        return type == ResultType.ERROR;
    }

    public Optional<T> ok() {
        return Optional.ofNullable(val);
    }

    public Optional<E> error() {
        return Optional.ofNullable(err);
    }

    public <U> Result<U, E> map(Function<T, U> function) {
        U u = Optional.ofNullable(val).map(function).orElse(null);
        return new Result<>(type, u, err);
    }

    public <F> Result<T, F> mapError(Function<E, F> function) {
        F f = Optional.ofNullable(err).map(function).orElse(null);
        return new Result<>(type, val, f);
    }
}
