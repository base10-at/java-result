package at.base10.result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Operator {

    private Operator() {
    }

    public static <S, F> Function<Result<S, F>, Result<F, S>> flip() {
        return r -> r.either(
                Result::failure,
                Result::success
        );
    }

    public static <S, S2, F, F2> Function<Result<S, F>, Result<S2, F2>> map(
            Function<S, S2> mapper,
            Function<F, F2> errMapper
    ) {
        return r -> r.map(mapper, errMapper);
    }

    public static <S, S2, F> Function<Result<S, F>, Result<S2, F>> map(Function<S, S2> mapper) {
        return r -> r.map(mapper);
    }

    public static <S, F, F2> Function<Result<S, F>, Result<S, F2>> mapFailure(Function<F, F2> mapper) {
        return r -> r.mapFailure(mapper);
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
        return r -> r.then(fn);
    }

    public static <S, S2, F> Function<Result<S, F>, Result<S2, F>> bind(Function<S, Result<S2, F>> binding) {
        return r -> r.bind(binding);
    }

    public static <S, S2, F, F2> Function<Result<S, F>, Result<S2, F2>> bind(
            Function<S, Result<S2, F2>> binding,
            Function<F, Result<S2, F2>> bindingFailure
    ) {
        return r -> r.bind(binding, bindingFailure);
    }

    public static <S, F, F2> Function<Result<S, F>, Result<S, F2>> bindFailure(Function<F, Result<S, F2>> binding) {
        return r -> r.bindFailure(binding);
    }

    public static <S, S2, F> Function<Result<S, F>, CompletableFuture<Result<S2, F>>> bindAsync(Function<S, CompletableFuture<Result<S2, F>>> binding) {
        return r -> r.either(
                binding,
                e -> CompletableFuture.completedFuture(Result.failure(e))
        );
    }

    public static <S, F, F2> Function<Result<S, F>, CompletableFuture<Result<S, F2>>> bindFailureAsync(Function<F, CompletableFuture<Result<S, F2>>> binding) {
        return r -> r.either(
                e -> CompletableFuture.completedFuture(Result.success(e)),
                binding
        );
    }

    private static <T, C> Function<T, C> toConst(C constant) {
        return t -> constant;
    }

    public static <S, F> Function<Result<S, F>, Boolean> anyMatch(Predicate<S> predicate) {
        return r -> r.either(predicate::test, toConst(false));
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
