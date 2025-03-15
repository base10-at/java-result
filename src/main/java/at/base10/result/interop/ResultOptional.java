package at.base10.result.interop;


import at.base10.result.Result;

import java.util.Optional;
import java.util.function.Function;

import static at.base10.result.Operator.map;
import static at.base10.result.Operator.mapEither;
import static at.base10.result.Result.success;

public final class ResultOptional {

    private ResultOptional() {
    }

    /**
     * Applies a mapping function to an {@code Optional} value and transforms the result into a {@code Result}.
     * Uses an applicative approach, meaning the function is applied if the value is present,
     * and an empty {@code Optional} results in a successful {@code Result} with an empty {@code Optional}.
     *
     * @param <V>     The type of the input value inside the {@code Optional}.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to the value inside the {@code Optional}, producing a {@code Result<S, F>}.
     * @return A function that transforms an {@code Optional<V>} into a {@code Result<Optional<S>, Optional<F>>}.
     */
    public static <V, S, F> Function<Optional<V>, Result<Optional<S>, Optional<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return optional -> sequenceApplicative(optional.map(mapping));
    }

    /**
     * Converts an {@code Optional} containing a {@code Result} into a single {@code Result} containing an {@code Optional}.
     * Uses an applicative approach, meaning that if the {@code Optional} is empty, a success result with an empty {@code Optional} is returned.
     *
     * @param <S>      The success type of the result.
     * @param <F>      The failure type of the result.
     * @param optional An {@code Optional} containing a {@code Result<S, F>}.
     * @return A {@code Result} containing an {@code Optional<S>} if successful, or an {@code Optional<F>} if failed.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, F> Result<Optional<S>, Optional<F>> sequenceApplicative(Optional<Result<S, F>> optional) {
        return optional.map(mapEither(Optional::of, Optional::of)).orElseGet(ResultOptional::getSuccessOfEmpty);
    }

    /**
     * Applies a mapping function to an {@code Optional} value and transforms the result into a {@code Result}.
     * Uses a monadic approach, meaning failures are short-circuited and the first encountered failure is returned.
     *
     * @param <V>     The type of the input value inside the {@code Optional}.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to the value inside the {@code Optional}, producing a {@code Result<S, F>}.
     * @return A function that transforms an {@code Optional<V>} into a {@code Result<Optional<S>, F>}, stopping at the first failure.
     */
    public static <V, S, F> Function<Optional<V>, Result<Optional<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return optional -> sequenceMonadic(optional.map(mapping));
    }

    /**
     * Converts an {@code Optional} containing a {@code Result} into a single {@code Result} containing an {@code Optional}.
     * Uses a monadic approach, meaning that if the {@code Optional} is empty, a success result with an empty {@code Optional} is returned.
     *
     * @param <S>      The success type of the result.
     * @param <F>      The failure type of the result.
     * @param optional An {@code Optional} containing a {@code Result<S, F>}.
     * @return A {@code Result} containing an {@code Optional<S>} if successful, or the first encountered failure.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, F> Result<Optional<S>, F> sequenceMonadic(Optional<Result<S, F>> optional) {
        return optional.map(map(Optional::of)).orElseGet(ResultOptional::getSuccessOfEmpty);
    }

    private static <S, F> Result<Optional<S>, F> getSuccessOfEmpty() {
        return success(Optional.empty());
    }

}
