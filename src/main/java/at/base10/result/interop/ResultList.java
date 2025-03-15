package at.base10.result.interop;


import at.base10.result.Result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;
import static at.base10.result.Operator.mapEither;

public final class ResultList {

    private ResultList() {
    }

    /**
     * Applies a mapping function to each element in the list and collects the results into a single {@code Result}.
     * Uses an applicative approach, meaning all elements are processed independently, and failures are accumulated.
     *
     * @param <V>     The type of elements in the input list.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to each element, producing a {@code Result<S, F>}.
     * @return A function that transforms a list of {@code V} into a {@code Result<List<S>, List<F>>}.
     */
    public static <V, S, F> Function<List<V>, Result<List<S>, List<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return list -> ResultStream.traverseApplicative(mapping).andThen(mapEitherToList()).apply(list.stream());
    }

    /**
     * Converts a list of {@code Result} objects into a single {@code Result} containing lists of success and failure values.
     * Uses an applicative approach, meaning all elements are processed independently, and failures are accumulated.
     *
     * @param <S>  The success type of the result.
     * @param <F>  The failure type of the result.
     * @param list The list of {@code Result<S, F>} values.
     * @return A {@code Result} containing a list of success values if all succeed, or a list of failures otherwise.
     */
    public static <S, F> Result<List<S>, List<F>> sequenceApplicative(List<Result<S, F>> list) {
        return ResultStream.sequenceApplicative(list.stream()).then(mapEitherToList());
    }

    /**
     * Applies a mapping function to each element in the list and collects the results into a single {@code Result}.
     * Uses a monadic approach, meaning failures are short-circuited and the first failure encountered is returned.
     *
     * @param <V>     The type of elements in the input list.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to each element, producing a {@code Result<S, F>}.
     * @return A function that transforms a list of {@code V} into a {@code Result<List<S>, F>}, stopping at the first failure.
     */
    public static <V, S, F> Function<List<V>, Result<List<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return list -> ResultStream.traverseMonadic(mapping).andThen(mapToList()).apply(list.stream());
    }

    /**
     * Converts a list of {@code Result} objects into a single {@code Result} containing a list of success values.
     * Uses a monadic approach, meaning failures are short-circuited and the first failure encountered is returned.
     *
     * @param <S>  The success type of the result.
     * @param <F>  The failure type of the result.
     * @param list The list of {@code Result<S, F>} values.
     * @return A {@code Result} containing a list of success values if all succeed, or the first encountered failure.
     */
    public static <S, F> Result<List<S>, F> sequenceMonadic(List<Result<S, F>> list) {
        return ResultStream.sequenceMonadic(list.stream()).then(mapToList());
    }

    private static <S, F> Function<Result<Stream<S>, Stream<F>>, Result<List<S>, List<F>>> mapEitherToList() {
        return mapEither(Stream::toList, Stream::toList);
    }

    private static <S, F> Function<Result<Stream<S>, F>, Result<List<S>, F>> mapToList() {
        return map(Stream::toList);
    }
}
