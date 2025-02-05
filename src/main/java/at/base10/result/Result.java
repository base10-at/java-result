package at.base10.result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static at.base10.result.Result.Operator.map;

public abstract sealed class Result<S, F> permits Result.Success, Result.Failure {

    public abstract boolean isSuccess();


    protected abstract S getValue();

    protected abstract F getFailure();


    public <S2> S2 thenApply(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return this.isSuccess() ? successFn.apply(getValue()) : failureFn.apply(getFailure());
    }

    public static final class Success<S, F> extends Result<S, F> {
        private final S value;

        public Success(S value) {
            super();
            this.value = value;
        }

        public boolean isSuccess() {
            return true;
        }

        public S getValue() {
            return value;
        }

        protected F getFailure() {
            return null;
        }

        @Override
        public String toString() {
            return "Success{" +
                    "value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            Success<?, ?> success = (Success<?, ?>) o;
            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() + value.hashCode();
        }
    }

    public static final class Failure<S, F> extends Result<S, F> {
        private final F failure;

        public Failure(F Failure) {
            super();
            this.failure = Failure;
        }

        public boolean isSuccess() {
            return false;
        }

        protected S getValue() {
            return null;
        }

        public F getFailure() {
            return failure;
        }

        @Override
        public String toString() {
            return "Failure{" +
                    "failure=" + failure +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            Failure<?, ?> failure1 = (Failure<?, ?>) o;
            return failure.equals(failure1.failure);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() + failure.hashCode();
        }
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }


    public static <S, F> Result<S, F> failure(F Failure) {
        return new Failure<>(Failure);
    }


    public final static class Operator {

        private Operator() {
        }

        public static <S, F> Function<Result<S, F>, Result<F, S>> flip() {
            return r -> r.either(
                    Failure::new,
                    Success::new
            );
        }

        public static <S, S2, F, F2> Function<Result<S, F>, Result<S2, F2>> map(
                Function<S, S2> mapper,
                Function<F, F2> errMapper
        ) {
            return r -> r.either(
                    v -> success(mapper.apply(v)),
                    e -> failure(errMapper.apply(e))
            );
        }


        public static <S, S2, F> Function<Result<S, F>, Result<S2, F>> map(Function<S, S2> mapper) {
            return map(mapper, Function.identity());
        }

        public static <S, F, F2> Function<Result<S, F>, Result<S, F2>> mapFailure(Function<F, F2> mapper) {
            return map(Function.identity(), mapper);
        }

        public static <S, F> Function<Result<S, F>, Result<S, F>> peek(
                Consumer<S> success,
                Consumer<F> Failure
        ) {
            return r -> {
                r.either(v -> {
                            success.accept(v);
                            return null;
                        },
                        e -> {
                            Failure.accept(e);
                            return null;
                        });
                return r;
            };
        }


        public static <S, S2, F> Function<Result<S, F>, S2> then(Function<Result<S, F>, S2> fn) {
            return r -> r.thenApply(fn);
        }

        public static <S, S2, F> Function<Result<S, F>, Result<S2, F>> bind(Function<S, Result<S2, F>> binding) {
            return r -> r.either(
                    binding,
                    Failure::new
            );
        }

        public static <S, S2, F> Function<Result<S, F>, CompletableFuture<Result<S2, F>>> bindAsync(Function<S, CompletableFuture<Result<S2, F>>> binding) {
            return r -> r.either(
                    binding,
                    e -> CompletableFuture.completedFuture(failure(e))
            );
        }

        public static <S, F, F2> Function<Result<S, F>, CompletableFuture<Result<S, F2>>> bindFailureAsync(Function<F, CompletableFuture<Result<S, F2>>> binding) {
            return r -> r.either(
                    e -> CompletableFuture.completedFuture(success(e)),
                    binding
            );
        }

        public static <S, F, F2> Function<Result<S, F>, Result<S, F2>> bindFailure(Function<F, Result<S, F2>> binding) {
            return r -> r.either(
                    Success::new,
                    binding
            );
        }

        private static <T, C> Function<T, C> toConst(C constant) {
            return t -> constant;
        }

        public static <S, F> Function<Result<S, F>, Boolean> anyMatch(Predicate<S> predicate) {
            return r -> r.either(
                    predicate::test,
                    toConst(false)
            );
        }

        public static <S, F> Function<Result<S, F>, Boolean> allMatch(Predicate<S> predicate) {
            return r -> r.either(
                    predicate::test,
                    toConst(true)
            );
        }

        public static <S, F> Function<Result<S, F>, List<S>> toList() {
            return r -> r.either(
                    List::of,
                    toConst(List.of())
            );
        }

        public static <S, F> Function<Result<S, F>, Stream<S>> toStream() {
            return r -> r.either(
                    Stream::of,
                    toConst(Stream.empty())
            );
        }

        public static <S, F> Function<Result<S, F>, Optional<S>> toOptional() {
            return r -> r.either(
                    Optional::of,
                    toConst(Optional.empty())
            );
        }

        public static <S, F> Function<Result<S, F>, S> defaultsTo(S defaultValue) {
            return r -> r.either(
                    Function.identity(),
                    toConst(defaultValue)
            );
        }

        public static <S, F> Function<Result<S, F>, S> defaultsTo(Supplier<S> supplier) {
            return r -> r.either(
                    Function.identity(),
                    toConst(supplier.get())
            );
        }

        public static <S, F> Function<Result<S, F>, Integer> count() {
            return r -> r.either(
                    toConst(1),
                    toConst(0)
            );
        }

        public static <S, F> Function<Result<S, F>, Boolean> isSuccess() {
            return Result::isSuccess;
        }

        public static <S, F> Function<Result<S, F>, Boolean> isFailure() {
            return Result::isFailure;
        }
    }


    public final static class Applicative {

        private Applicative() {
        }

        public static <V, S, F> Function<List<V> ,Result<List<S>, List<F>>> traverseList( Function<V, Result<S, F>> mapping) {
            return list->traverseStream(mapping)
                    .apply(list.stream())
                    .thenApply(map(Stream::toList, Stream::toList));
        }

        public static <V, S, F> Function<Stream<V>, Result<Stream<S>, Stream<F>>> traverseStream(Function<V, Result<S, F>> mapping) {
            return stream -> stream
                    .map(mapping)
                    .map(Result.Operator.map(Stream::of, Stream::of))
                    .reduce(success(Stream.of()), Applicative::reducer);
        }

        public static <S, F> Result<List<S>, List<F>> sequenceList(List<Result<S, F>> list) {
            return sequenceStream(list.stream())
                    .thenApply(map(Stream::toList, Stream::toList));
        }

        public static <S, F> Result<Stream<S>, Stream<F>> sequenceStream(Stream<Result<S, F>> stream) {
            return stream.map(Result.Operator.map(Stream::of, Stream::of))
                    .reduce(success(Stream.of()), Applicative::reducer);
        }

        private static <S, F> Result<Stream<S>, Stream<F>> reducer(Result<Stream<S>, Stream<F>> acc, Result<Stream<S>, Stream<F>> elem) {
            return acc.either(
                    (success) -> elem.either(
                            s -> success(Stream.concat(success, s)),
                            Result::failure
                    ),
                    (error) -> elem.either(
                            s -> acc,
                            f -> failure(Stream.concat(error, f))
                    )
            );
        }
    }

    public final static class Monadic {

        private Monadic() {
        }


        public static <V, S, F> Function<Optional<V>, Result<Optional<S>, F>> traverseOptional(Function<V, Result<S, F>> mapping) {
            return optional -> sequenceOptional(optional.map(mapping));
        }

        public static <V, S, F> Function<List<V>, Result<List<S>, F>> traverseList(Function<V, Result<S, F>> mapping) {
            return list -> traverseStream(mapping)
                    .apply(list.stream())
                    .thenApply(map(Stream::toList));
        }


        public static <V, S, F> Function<Stream<V>, Result<Stream<S>, F>> traverseStream(Function<V, Result<S, F>> mapping) {
            return stream -> sequenceStream(stream.map(mapping));
        }


        public static <S, F> Result<Optional<S>, F> sequenceOptional(Optional<Result<S, F>> optional) {
            return optional
                    .map(map(Optional::of, Function.identity()))
                    .orElseGet(() -> success(Optional.empty()));
        }

        public static <S, F> Result<List<S>, F> sequenceList(List<Result<S, F>> list) {
            return sequenceStream(list.stream()).thenApply(map(Stream::toList));
        }


        public static <S, F> Result<Stream<S>, F> sequenceStream(Stream<Result<S, F>> stream) {
            return stream
                    .map(Result.Operator.map(Stream::of))
                    .reduce(success(Stream.of()), Monadic::reducer);
        }


        private static <S, F> Result<Stream<S>, F> reducer(Result<Stream<S>, F> acc, Result<Stream<S>, F> elem) {
            return acc.either(
                    (success) -> elem.either(
                            s -> success(Stream.concat(success, s)),
                            Result::failure
                    ),
                    (error) -> acc
            );
        }
    }

}
