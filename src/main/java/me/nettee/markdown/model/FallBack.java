package me.nettee.markdown.model;

import java.util.Optional;

public class FallBack<T, T1 extends T, T2 extends T> {

    private final T1 value1;

    private final T2 value2;

    private FallBack(T1 value1, T2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public static <T, T1 extends T, T2 extends T> FallBack<T, T1, T2> primary(T1 value1) {
        return new FallBack<>(value1, null);
    }

    public static <T, T1 extends T, T2 extends T> FallBack<T, T1, T2> secondary(T2 value2) {
        return new FallBack<>(null, value2);
    }

    public Optional<T> get() {
        if (value1 != null) {
            return Optional.of(value1);
        }
        if (value2 != null) {
            return Optional.of(value2);
        }
        return Optional.empty();
    }

    public T nullSafeGet() {
        return get().orElseThrow(IllegalStateException::new);
    }
}
