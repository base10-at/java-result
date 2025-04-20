package at.base10.result;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An interface representing the result of an operation that can either succeed or fail.
 *
 * <p>The {@code Result} interface is a generic container that encapsulates either a success ({@code S}) or a failure ({@code F}).
 * It provides a functional approach to error handling, reducing the need for exceptions and making error propagation explicit.
 *
 * <p>This interface is sealed, meaning only the permitted subclasses {@link Success} and {@link Failure} can implement it.
 *
 * <p>Typical usage involves calling one of the factory methods such as {@link #success(Object)} or {@link #failure(Object)}
 * to construct an instance and then processing it using various transformation and mapping methods.
 *
 * @param <S> the type representing a successful result
 * @param <F> the type representing a failure result
 * @see Success
 * @see Failure
 */
public sealed interface Result<S, F> permits Success, Failure {
    /**
     * Creates a successful Result instance.
     *
     * @param <S>   the type representing a successful result
     * @param <F>   the type representing a failure result
     * @param value The success value.
     * @return A Result representing success.
     */
    static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    /**
     * Creates a successful Result instance.
     *
     * @param <S>         the type representing a successful result
     * @param <F>         the type representing a failure result
     * @param value       The success value.
     * @param failureType The failure type.
     * @return A Result representing success.
     */
    @SuppressWarnings("unused")
    static <S, F> Result<S, F> success(S value, Class<F> failureType) {
        return new Success<>(value);
    }

    /**
     * Creates a failure Result instance.
     *
     * @param <S>   the type representing a successful result
     * @param <F>   the type representing a failure result
     * @param value The failure value.
     * @return A Result representing failure.
     */
    static <S, F> Result<S, F> failure(F value) {
        return new Failure<>(value);
    }

    /**
     * Creates a failure Result instance.
     *
     * @param <S>         the type representing a successful result
     * @param <F>         the type representing a failure result
     * @param failure     The failure value.
     * @param successType The success type.
     * @return A Result representing failure.
     */
    @SuppressWarnings("unused")
    static <S, F> Result<S, F> failure(F failure, Class<S> successType) {
        return new Failure<>(failure);
    }

    /**
     * Converts an Optional into a Result.
     *
     * @param <S>      the type representing a successful result
     * @param <F>      the type representing a failure result
     * @param optional The optional value.
     * @param supplier The failure supplier if optional is empty.
     * @return A success Result if optional is present, otherwise a failure Result.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <S, F> Result<S, F> fromOptional(Optional<S> optional, Supplier<F> supplier) {
        return optional.map(Result::<S, F>success).orElse(Result.failure(supplier.get()));
    }

    /**
     * Converts an Optional into a Result.
     *
     * @param <S>      the type representing a successful result
     * @param optional The optional value.
     * @return A success Result if optional is present, otherwise a failure Result.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <S> Result<S, Void> fromOptional(Optional<S> optional) {
        return fromOptional(optional, () -> null);
    }

    /**
     * Creates a Result based on a predicate test.
     *
     * @param <S>       the type representing a successful result
     * @param <F>       the type representing a failure result
     * @param value     The value to test.
     * @param predicate The predicate function.
     * @param supplier  The failure supplier if predicate test fails.
     * @return A success Result if predicate test passes, otherwise a failure Result.
     */
    static <S, F> Result<S, F> fromPredicate(S value, Predicate<S> predicate, Supplier<F> supplier) {
        return predicate.test(value) ? success(value) : failure(supplier.get());
    }

    /**
     * Creates a Result based on a predicate test.
     *
     * @param <S>       the type representing a successful result
     * @param value     The value to test.
     * @param predicate The predicate function.
     * @return A success Result if predicate test passes, otherwise a failure Result.
     */
    static <S> Result<S, Void> fromPredicate(S value, Predicate<S> predicate) {
        return fromPredicate(value, predicate, () -> null);
    }

    /**
     * Creates a Result based on a boolean condition.
     *
     * @param <S>       the type representing a successful result
     * @param <F>       the type representing a failure result
     * @param value     The boolean value.
     * @param successFn Supplier for success value.
     * @param failureFn Supplier for failure value.
     * @return A success Result if true, otherwise a failure Result.
     */
    static <S, F> Result<S, F> fromBoolean(Boolean value, Supplier<S> successFn, Supplier<F> failureFn) {
        return value ? success(successFn.get()) : failure(failureFn.get());
    }

    /**
     * Creates a Result based on a boolean condition.
     *
     * @param value The boolean value.
     * @return A success Result if true, otherwise a failure Result.
     */
    static Result<Boolean, Boolean> fromBoolean(Boolean value) {
        return value ? success(true) : failure(false);
    }

    /**
     * Checks if the Result is successful.
     *
     * @return True if success, false otherwise.
     */
    boolean isSuccess();

    /**
     * Checks if the Result represents failure.
     *
     * @return True if failure, false otherwise.
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the success value held by this {@code Result}.
     * <p>
     * If this {@code Result} represents a success, the value is returned.
     * If this {@code Result} represents a failure, calling this method will throw a {@code NoSuchElementException}.
     *
     * @return the success value
     * @throws NoSuchElementException if this Result is a failure
     */
    S value();

    /**
     * Returns the failure value held by this {@code Result}.
     * <p>
     * If this {@code Result} represents a failure, the value is returned.
     * If this {@code Result} represents a success, calling this method will throw a {@code NoSuchElementException}.
     *
     * @return the failure value
     * @throws NoSuchElementException if this Result is a success
     */
    F failure();

    /**
     * Applies a function to this Result instance and returns a new value.
     *
     * @param <S2> The type of the returned value.
     * @param fn   The function to apply to the Result.
     * @return The transformed value.
     */
    <S2> S2 then(Function<Result<S, F>, S2> fn);

    /**
     * Swaps the success and failure values of a Result.
     *
     * @return A function that transforms a Result&lt;S, F&gt; into a Result&lt;F, S&gt;.
     */
    Result<F, S> swap();

    /**
     * Transforms both success and failure values using the provided mapping functions.
     *
     * @param <S2>      The type of the new success value.
     * @param <F2>      The type of the new failure value.
     * @param mapper    Function to transform the success value.
     * @param errMapper Function to transform the failure value.
     * @return A new Result with transformed success and failure values.
     */
    <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper);

    /**
     * Transforms the success value using the provided mapping function.
     *
     * @param <S2>   The type of the new success value.
     * @param mapper Function to transform the success value.
     * @return A new Result with the transformed success value.
     */
    <S2> Result<S2, F> map(Function<S, S2> mapper);

    /**
     * Consumes the failure value using the provided consumer function.
     *
     * @param <F2>   The type of the new failure value.
     * @param mapper Function to transform the failure value.
     * @return The original Result.
     */
    <F2> Result<S, F2> mapFailure(Function<F, F2> mapper);

    /**
     * Consumes both success and failure values using the provided consumer functions.
     *
     * @param consumer    Function to transform the success value.
     * @param errConsumer Function to transform the failure value.
     * @return The original Result.
     */
    Result<S, F> peekEither(Consumer<S> consumer, Consumer<F> errConsumer);

    /**
     * Consumes the success value using the provided consumer function.
     *
     * @param consumer Function to transform the success value.
     * @return The original Result.
     */
    Result<S, F> peek(Consumer<S> consumer);

    /**
     * Transforms the failure value using the provided peekping function.
     *
     * @param consumer Function to transform the failure value.
     * @return A new Result with the transformed failure value.
     */
    Result<S, F> peekFailure(Consumer<F> consumer);

    /**
     * Binds a function to both success and failure values, transforming the Result accordingly.
     *
     * @param <S2>           The type of the new success value.
     * @param <F2>           The type of the new failure value.
     * @param binding        Function to transform the success value into a new Result.
     * @param bindingFailure Function to transform the failure value into a new Result.
     * @return A new Result produced by the applied binding functions.
     */
    <S2, F2> Result<S2, F2> bindEither(Function<S, Result<S2, F2>> binding, Function<F, Result<S2, F2>> bindingFailure);

    /**
     * Binds a function to the success value, transforming the Result accordingly.
     *
     * @param <S2>    The type of the new success value.
     * @param binding Function to transform the success value into a new Result.
     * @return A new Result produced by applying the binding function to the success value.
     */
    <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding);

    /**
     * Binds a function to the failure value, transforming the Result accordingly.
     *
     * @param <F2>    The type of the new failure value.
     * @param binding Function to transform the failure value into a new Result.
     * @return A new Result produced by applying the binding function to the failure value.
     */
    <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding);

    /**
     * Transforms either the success or failure value using the provided mapping functions.
     * If this instance represents a success, it applies {@code mapping}. If it represents a failure, it applies {@code mappingFailure}.
     *
     * @param <S2>           The type of the new success value.
     * @param <F2>           The type of the new failure value.
     * @param mapping        Function to transform the success value into a new Result.
     * @param mappingFailure Function to transform the failure value into a new Result.
     * @return A new Result produced by the applied binding functions.
     */
    <S2, F2> Result<S2, F2> flatMapEither(Function<S, Result<S2, F2>> mapping, Function<F, Result<S2, F2>> mappingFailure);

    /**
     * Transforms the success value using the provided mapping function, which returns a new {@code Result}.
     * If this instance represents a failure, it remains unchanged.
     *
     * @param <S2>    The type of the new success value.
     * @param mapping Function to transform the success value into a new Result.
     * @return A new {@code Result} produced by applying the binding function to the success value.
     */
    <S2> Result<S2, F> flatMap(Function<S, Result<S2, F>> mapping);

    /**
     * Transforms the failure value using the provided mapping function, which returns a new {@code Result}.
     * If this instance represents a success, it remains unchanged.
     *
     * @param <F2>           The type of the new failure value.
     * @param mappingFailure Function to transform the failure value into a new Result.
     * @return A new {@code Result} produced by applying the binding function to the failure value.
     */
    <F2> Result<S, F2> flatMapFailure(Function<F, Result<S, F2>> mappingFailure);

    /**
     * Applies one of two functions based on whether the result is a success or failure.
     *
     * @param <R2>      The return type of the applied function.
     * @param successFn Function applied if the result is successful.
     * @param failureFn Function applied if the result is a failure.
     * @return The result of the applied function.
     */
    <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn);

    /**
     * Retrieves the success value or throws an exception.
     *
     * @return The success value.
     * @throws RuntimeException If the Result is a failure.
     */
    S orThrow();

    /**
     * Retrieves the success value or throws an exception created by the provided function.
     *
     * @param <E>               The type of exception to be thrown, extending RuntimeException.
     * @param exceptionFunction A function that takes the failure value and returns an exception.
     * @return The success value if present.
     * @throws E If the result is a failure, an exception created by the exceptionFunction is thrown.
     */
    <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) throws E;

    /**
     * Retrieves the success value if the result is successful, or applies the given function
     * to transform the failure value into a success value.
     *
     * @param failureMapping a function that maps the failure value to a success value
     * @return the success value if present, otherwise the mapped failure value
     */
    S orElse(Function<F, S> failureMapping);

    /**
     * @return Optional an option of success type
     */
    Optional<S> toOptional();

    /**
     * Recovers from a failure by transforming the failure value into a success value.
     * <p>
     * If this result is a {@code Success}, it is returned as-is.
     * If this result is a {@code Failure}, the given {@code recoveryFn} is applied to the failure value
     * to produce a new {@code Success}.
     * <p>
     * This method is useful for providing a fallback or default value when a computation fails.
     *
     * @param recoveryFn a function that maps the failure value to a success value
     * @return a {@code Success} containing the recovered value if this is a {@code Failure},
     * or this result itself if it is already a {@code Success}
     * @throws NullPointerException if {@code recoveryFn} is {@code null}
     */
    Result<S, Void> recover(Function<F, S> recoveryFn);

}
