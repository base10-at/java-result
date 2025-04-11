package at.base10.result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A collection of utility functions
 */
public sealed interface Operator permits None {


    /**
     * Swaps the success and failure values of a Result.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that transforms a Result&lt;S, F&gt; into a Result&lt;F, S&gt;.
     */
    static <S, F> Function<Result<S, F>, Result<F, S>> flip() {
        return r -> r.either(Result::failure, Result::success);
    }

    /**
     * Transforms both success and failure values of a Result using the provided mapping functions.
     *
     * @param <S>       The type of the original success value.
     * @param <S2>      The type of the new success value.
     * @param <F>       The type of the original failure value.
     * @param <F2>      The type of the new failure value.
     * @param mapper    Function to transform the success value.
     * @param errMapper Function to transform the failure value.
     * @return A function that applies the transformations to the Result.
     */
    static <S, S2, F, F2> Function<Result<S, F>, Result<S2, F2>> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper) {
        return r -> r.mapEither(mapper, errMapper);
    }

    /**
     * Transforms the success value of a Result using the provided mapping function.
     *
     * @param <S>    The type of the original success value.
     * @param <S2>   The type of the new success value.
     * @param <F>    The type of the failure value.
     * @param mapper Function to transform the success value.
     * @return A function that applies the transformation to the Result.
     */
    static <S, S2, F> Function<Result<S, F>, Result<S2, F>> map(Function<S, S2> mapper) {
        return r -> r.map(mapper);
    }

    /**
     * Transforms the failure value of a Result using the provided mapping function.
     *
     * @param <S>    The type of the success value.
     * @param <F>    The type of the original failure value.
     * @param <F2>   The type of the new failure value.
     * @param mapper Function to transform the failure value.
     * @return A function that applies the transformation to the Result.
     */
    static <S, F, F2> Function<Result<S, F>, Result<S, F2>> mapFailure(Function<F, F2> mapper) {
        return r -> r.mapFailure(mapper);
    }

    /**
     * Performs side effects on both success and failure values without modifying the Result.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the failure value.
     * @param success Consumer to process the success value.
     * @param failure Consumer to process the failure value.
     * @return A function that applies the side effects and returns the original Result.
     */
    static <S, F> Function<Result<S, F>, Result<S, F>> peekEither(Consumer<S> success, Consumer<F> failure) {
        return r -> {
            r.either(ConsumerToVoidFunction(success), ConsumerToVoidFunction(failure));
            return r;
        };
    }

    /**
     * Performs a side effect on the success value without modifying the Result.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the failure value.
     * @param success Consumer to process the success value.
     * @return A function that applies the side effect and returns the original Result.
     */
    static <S, F> Function<Result<S, F>, Result<S, F>> peek(Consumer<S> success) {
        return r -> {
            r.either(ConsumerToVoidFunction(success), f -> null);
            return r;
        };
    }

    /**
     * Executes the given consumer if the {@code Result} is successful, then returns the same {@code Result}.
     * This allows for side effects without altering the result itself.
     * This is an alias for {@link #peek(Consumer)}.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the failure value.
     * @param success A consumer to execute if the {@code Result} is successful.
     * @return A function that applies the consumer on success and returns the original {@code Result}.
     */
    static <S, F> Function<Result<S, F>, Result<S, F>> ifSuccess(Consumer<S> success) {
        return peek(success);
    }

    /**
     * Executes the given consumer if the {@code Result} is a failure, then returns the same {@code Result}.
     * This allows for side effects without altering the result itself.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the failure value.
     * @param failure A consumer to execute if the {@code Result} is a failure.
     * @return A function that applies the consumer on failure and returns the original {@code Result}.
     */
    static <S, F> Function<Result<S, F>, Result<S, F>> peekFailure(Consumer<F> failure) {
        return r -> {
            r.either(f -> null, ConsumerToVoidFunction(failure));
            return r;
        };
    }

    /**
     * Executes the given consumer if the {@code Result} is a failure, then returns the same {@code Result}.
     * This is an alias for {@link #peekFailure(Consumer)}.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the failure value.
     * @param failure A consumer to execute if the {@code Result} is a failure.
     * @return A function that applies the consumer on failure and returns the original {@code Result}.
     */
    static <S, F> Function<Result<S, F>, Result<S, F>> ifFailure(Consumer<F> failure) {
        return peekFailure(failure);
    }


    /**
     * Extracts the success value or throws an exception if the Result represents failure.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that extracts the success value or throws an exception.
     */
    static <S, F> Function<Result<S, F>, S> orThrow() {
        return Result::orThrow;
    }

    /**
     * Extracts the success value or throws an exception created by the provided function.
     *
     * @param <S>               The type of the success value.
     * @param <F>               The type of the failure value.
     * @param <E>               The type of the exception to be thrown.
     * @param exceptionFunction Function to create an exception from the failure value.
     * @return A function that extracts the success value or throws an exception.
     */
    static <S, F, E extends RuntimeException> Function<Result<S, F>, S> orThrow(Function<F, E> exceptionFunction) {
        return r -> r.orThrow(exceptionFunction);
    }

    private static <T> Function<T, Void> ConsumerToVoidFunction(Consumer<T> consumer) {
        return v -> {
            consumer.accept(v);
            return null;
        };
    }

    /**
     * Transforms the success value of a {@code Result} using the provided binding function,
     * flattening the nested {@code Result} structure.
     *
     * @param <S>     The type of the original success value.
     * @param <S2>    The type of the new success value.
     * @param <F>     The type of the failure value.
     * @param binding A function that transforms a success value into another {@code Result}.
     * @return A function that applies the binding transformation if the {@code Result} is successful.
     */
    static <S, S2, F> Function<Result<S, F>, Result<S2, F>> bind(Function<S, Result<S2, F>> binding) {
        return r -> r.bind(binding);
    }

    /**
     * Transforms both success and failure values of a {@code Result} using the provided binding functions,
     * flattening the nested {@code Result} structure.
     *
     * @param <S>            The type of the original success value.
     * @param <S2>           The type of the new success value.
     * @param <F>            The type of the original failure value.
     * @param <F2>           The type of the new failure value.
     * @param binding        A function that transforms a success value into another {@code Result}.
     * @param bindingFailure A function that transforms a failure value into another {@code Result}.
     * @return A function that applies the appropriate transformation based on success or failure.
     */
    static <S, S2, F, F2> Function<Result<S, F>, Result<S2, F2>> bindEither(Function<S, Result<S2, F2>> binding, Function<F, Result<S2, F2>> bindingFailure) {
        return r -> r.bindEither(binding, bindingFailure);
    }

    /**
     * Transforms the failure value of a {@code Result} using the provided binding function,
     * flattening the nested {@code Result} structure.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the original failure value.
     * @param <F2>    The type of the new failure value.
     * @param binding A function that transforms a failure value into another {@code Result}.
     * @return A function that applies the binding transformation if the {@code Result} is a failure.
     */
    static <S, F, F2> Function<Result<S, F>, Result<S, F2>> bindFailure(Function<F, Result<S, F2>> binding) {
        return r -> r.bindFailure(binding);
    }

    /**
     * Recovers from a failure by transforming the failure value into a success value.
     * <p>
     * If this result is a {@code Success}, it is returned as-is.
     * If this result is a {@code Failure}, the given {@code recoveryFn} is applied to the failure value
     * to produce a new {@code Success}.
     * <p>
     * This method is useful for providing a fallback or default value when a computation fails.
     * *
     *
     * @param <S>        The type of the success value.
     * @param <F>        The type of the original failure value.
     * @param recoveryFn a function that maps the failure value to a success value
     * @return a {@code Success} containing the recovered value if this is a {@code Failure},
     * or this result itself if it is already a {@code Success}
     * @throws NullPointerException if {@code recoveryFn} is {@code null}
     */
    static <S, F> Function<Result<S, F>, Result<S, Void>> recover(Function<F, S> recoveryFn) {
        return r -> r.recover(recoveryFn);
    }

    /**
     * Asynchronously transforms the success value of a {@code Result} using the provided binding function.
     *
     * @param <S>     The type of the original success value.
     * @param <S2>    The type of the new success value.
     * @param <F>     The type of the failure value.
     * @param binding A function that asynchronously transforms a success value into another {@code Result}.
     * @return A function that applies the binding transformation asynchronously if the {@code Result} is successful.
     */
    static <S, S2, F> Function<Result<S, F>, CompletableFuture<Result<S2, F>>> bindAsync(Function<S, CompletableFuture<Result<S2, F>>> binding) {
        return r -> r.either(binding, e -> CompletableFuture.completedFuture(Result.failure(e)));
    }

    /**
     * Asynchronously transforms the failure value of a {@code Result} using the provided binding function.
     *
     * @param <S>     The type of the success value.
     * @param <F>     The type of the original failure value.
     * @param <F2>    The type of the new failure value.
     * @param binding A function that asynchronously transforms a failure value into another {@code Result}.
     * @return A function that applies the binding transformation asynchronously if the {@code Result} is a failure.
     */
    static <S, F, F2> Function<Result<S, F>, CompletableFuture<Result<S, F2>>> bindFailureAsync(Function<F, CompletableFuture<Result<S, F2>>> binding) {
        return r -> r.either(e -> CompletableFuture.completedFuture(Result.success(e)), binding);
    }

    private static <T, C> Function<T, C> toConst(C constant) {
        return t -> constant;
    }

    /**
     * Checks if the success value matches the given predicate.
     *
     * @param <S>       The type of the success value.
     * @param <F>       The type of the failure value.
     * @param predicate The predicate to test the success value.
     * @return A function that returns true if the success value matches the predicate otherwise returns false.
     */
    static <S, F> Function<Result<S, F>, Boolean> anyMatch(Predicate<S> predicate) {
        return r -> r.either(predicate::test, toConst(false));
    }

    /**
     * Checks if all success values match the given predicate.
     *
     * @param <S>       The type of the success value.
     * @param <F>       The type of the failure value.
     * @param predicate The predicate to test the success value.
     * @return A function that returns true if all success values match the predicate or on failure otherwise returns false.
     */
    static <S, F> Function<Result<S, F>, Boolean> allMatch(Predicate<S> predicate) {
        return r -> r.either(predicate::test, toConst(true));
    }

    /**
     * Converts a Result into a List containing the success value, or an empty list if failure.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that converts the Result into a List.
     */
    static <S, F> Function<Result<S, F>, List<S>> toList() {
        return r -> r.either(List::of, toConst(List.of()));
    }

    /**
     * Converts a Result into a Stream containing the success value, or an empty stream if failure.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that converts the Result into a Stream.
     */
    static <S, F> Function<Result<S, F>, Stream<S>> toStream() {
        return r -> r.either(Stream::of, toConst(Stream.empty()));
    }

    /**
     * Converts a Result into an Optional containing the success value, or empty if failure.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that converts the Result into an Optional.
     */
    static <S, F> Function<Result<S, F>, Optional<S>> toOptional() {
        return Result::toOptional;
    }

    /**
     * Returns the success value or a default value if failure.
     *
     * @param <S>          The type of the success value.
     * @param <F>          The type of the failure value.
     * @param defaultValue The default value to return if failure.
     * @return A function that returns the success value or the default value.
     */
    static <S, F> Function<Result<S, F>, S> defaultsTo(S defaultValue) {
        return r -> r.either(Function.identity(), toConst(defaultValue));
    }

    /**
     * Returns the success value or a value supplied by the given Supplier if failure.
     *
     * @param <S>      The type of the success value.
     * @param <F>      The type of the failure value.
     * @param supplier The supplier providing a default value in case of failure.
     * @return A function that returns the success value or a supplied default.
     */
    static <S, F> Function<Result<S, F>, S> defaultsTo(Supplier<S> supplier) {
        return r -> r.either(Function.identity(), toConst(supplier.get()));
    }

    /**
     * Counts the number of successful results.
     * <p>
     * Returns 1 if the Result represents success, otherwise returns 0.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that returns 1 if the Result is successful, otherwise 0.
     */
    static <S, F> Function<Result<S, F>, Integer> count() {
        return r -> r.either(toConst(1), toConst(0));
    }

    /**
     * Checks if the Result represents success.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that returns true if the Result is a success.
     */
    static <S, F> Function<Result<S, F>, Boolean> isSuccess() {
        return Result::isSuccess;
    }

    /**
     * Checks if the Result represents failure.
     *
     * @param <S> The type of the success value.
     * @param <F> The type of the failure value.
     * @return A function that returns true if the Result is a failure.
     */
    static <S, F> Function<Result<S, F>, Boolean> isFailure() {
        return Result::isFailure;
    }


}
