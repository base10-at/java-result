package at.base10.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Result<S, F> permits Success, Failure {
    /**
     * Creates a successful Result instance.
     *
     * @param value The success value.
     * @return A Result representing success.
     */
    static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    @SuppressWarnings("unused")
    static <S, F> Result<S, F> success(S value, Class<F> failureType) {
        return new Success<>(value);
    }

    /**
     * Creates a failure Result instance.
     *
     * @param value The failure value.
     * @return A Result representing failure.
     */
    static <S, F> Result<S, F> failure(F value) {
        return new Failure<>(value);
    }

    @SuppressWarnings("unused")
    static <S, F> Result<S, F> failure(F Failure, Class<S> successType) {
        return new Failure<>(Failure);
    }

    /**
     * Converts an Optional into a Result.
     *
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

    S value();

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
     * Transforms the failure value using the provided mapping function.
     *
     * @param <F2>   The type of the new failure value.
     * @param mapper Function to transform the failure value.
     * @return A new Result with the transformed failure value.
     */
    <F2> Result<S, F2> mapFailure(Function<F, F2> mapper);

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

    Optional<S> toOptional();


}
