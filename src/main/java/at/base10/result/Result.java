package at.base10.result;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract sealed class Result<V, E> {


    public abstract boolean isOk();


    protected abstract V getValue();

    protected abstract E getError();


    public <V2> V2 then(Function<Result<V, E>, V2> fn) {
        return fn.apply(this);
    }

    public <V2> V2 either(Function<V, V2> okFn, Function<E, V2> errorFn) {
        return this.isOk() ? okFn.apply(getValue()) : errorFn.apply(getError());
    }

    public static final class Ok<V, E> extends Result<V, E> {
        private final V value;

        public Ok(V value) {
            super();
            this.value = value;
        }

        public boolean isOk() {
            return true;
        }

        protected V getValue() {
            return value;
        }

        protected E getError() {
            return null;
        }
    }

    public static final class Error<V, E> extends Result<V, E> {
        private final E error;

        public Error(E error) {
            super();
            this.error = error;
        }

        public boolean isOk() {
            return false;
        }

        protected V getValue() {
            return null;
        }

        protected E getError() {
            return error;
        }
    }

    public boolean isError() {
        return !isOk();
    }

    public static <V, E> Result<V, E> ok(V value) {
        return new Ok<>(value);
    }

    public static <V, E> Result<V, E> error(E error) {
        return new Error<>(error);
    }


    public final static class Fn {


        public static <V, E> Function<Result<V, E>, Result<E, V>> flip() {
            return r -> r.either(
                    Error::new,
                    Ok::new
            );
        }

        public static <V, V2, E, E2> Function<Result<V, E>, Result<V2, E2>> map(
                Function<V, V2> mapper,
                Function<E, E2> errMapper
        ) {
            return r -> r.either(
                    v -> ok(mapper.apply(v)),
                    e -> error(errMapper.apply(e))
            );
        }

        public static <V, V2, E> Function<Result<V, E>, Result<V2, E>> map(Function<V, V2> mapper) {
            return map(mapper, Function.identity());
        }

        public static <V, E, E2> Function<Result<V, E>, Result<V, E2>> mapError(Function<E, E2> mapper) {
            return map(Function.identity(), mapper);
        }

        public static <V, E> Function<Result<V, E>, Result<V, E>> peek(
                Consumer<V> ok,
                Consumer<E> error
        ) {
            return r -> {
                r.either(v -> {
                            ok.accept(v);
                            return null;
                        }
                        , e -> {
                            error.accept(e);
                            return null;
                        });
                return r;
            };
        }


        public static <V, V2, E, E2> Function<Result<V, E>, Result<V2, E2>> bind(
                Function<V, Result<V2, E2>> binding,
                Function<E, Result<V2, E2>> errorBinding
        ) {
            return r -> r.either(
                    binding,
                    errorBinding
            );
        }

        public static <V, V2, E> Function<Result<V, E>, Result<V2, E>> bind(Function<V, Result<V2, E>> binding) {
            return bind(binding, Error::new);
        }

        public static <V, E, E2> Function<Result<V, E>, Result<V, E2>> bindError(Function<E, Result<V, E2>> binding) {
            return bind(Ok::new, binding);
        }

        private static <T, C> Function<T, C> toConst(C constant) {
            return t -> constant;
        }

        public static <V, E> Function<Result<V, E>, Boolean> anyMatch(Predicate<V> predicate) {
            return r -> r.either(
                    predicate::test,
                    toConst(false)
            );
        }

        public static <V, E> Function<Result<V, E>, Boolean> allMatch(Predicate<V> predicate) {
            return r -> r.either(
                    predicate::test,
                    toConst(true)
            );
        }

        public static <V, E> Function<Result<V, E>, List<V>> toList() {
            return r -> r.either(
                    List::of,
                    toConst(List.of())
            );
        }

        public static <V, E> Function<Result<V, E>, Stream<V>> toStream() {
            return r -> r.either(
                    Stream::of,
                    toConst(Stream.empty())
            );
        }

        public static <V, E> Function<Result<V, E>, Optional<V>> toOptional() {
            return r -> r.either(
                    Optional::of,
                    toConst(Optional.empty())
            );
        }

        public static <V, E> Function<Result<V, E>, V> defaultsTo(V defaultValue) {
            return r -> r.either(
                    Function.identity(),
                    toConst(defaultValue)
            );
        }

        public static <V, E> Function<Result<V, E>, V> defaultsTo(Supplier<V> supplier) {
            return r -> r.either(
                    Function.identity(),
                    toConst(supplier.get())
            );
        }

        public static <V, E> Function<Result<V, E>, Integer> count() {
            return r -> r.either(
                    toConst(1),
                    toConst(0)
            );
        }


    }


}
