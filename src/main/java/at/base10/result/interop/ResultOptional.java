package at.base10.result.interop;


import at.base10.result.Result;

import java.util.Optional;
import java.util.function.Function;

import static at.base10.result.Operator.map;
import static at.base10.result.Result.success;

public final class ResultOptional {

    private ResultOptional() {
    }

    public static <V, S, F> Function<Optional<V>, Result<Optional<S>, Optional<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return optional -> sequenceApplicative(optional.map(mapping));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, F> Result<Optional<S>, Optional<F>> sequenceApplicative(Optional<Result<S, F>> optional) {
        return optional.map(map(Optional::of, Optional::of)).orElseGet(ResultOptional::getSuccessOfEmpty);
    }

    public static <V, S, F> Function<Optional<V>, Result<Optional<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return optional -> sequenceMonadic(optional.map(mapping));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, F> Result<Optional<S>, F> sequenceMonadic(Optional<Result<S, F>> optional) {
        return optional.map(map(Optional::of, Function.identity())).orElseGet(ResultOptional::getSuccessOfEmpty);
    }

    private static <S, F> Result<Optional<S>, F> getSuccessOfEmpty() {
        return success(Optional.empty());
    }

}
