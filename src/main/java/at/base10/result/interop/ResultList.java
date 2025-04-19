package at.base10.result.interop;


import at.base10.result.Result;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.mapEither;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

/**
 * A utility class providing functional operations on {@code List} values in the context of {@code Result}.
 *
 * <p>The {@code ResultList} class offers methods to transform and sequence {@code List} values that contain
 * {@code Result} instances, supporting both applicative and monadic traversal.
 *
 * <p>These utilities enable functional programming patterns for handling collections of computations
 * that may succeed or fail, integrating seamlessly with the {@link Result} type and streams.
 *
 * <p>All methods in this class are static, and the constructor is private to prevent instantiation.
 *
 *
 * <p><b>Usage Examples:</b></p>
 *
 * <blockquote><pre>{@code
 * // traverseApplicative
 * // Input: Stream.of(1, 2, 3)
 * Result<Stream<Integer>, Stream<String>> result = ResultStream.traverseApplicative(
 *     i -> i % 2 == 0 ? Result.success(i) : Result.failure("Odd number: " + i)
 * ).apply(Stream.of(1, 2, 3));
 *
 * // Expected Output: Failure(["Odd number: 1", "Odd number: 3"])
 *
 *
 * // sequenceApplicative
 * // Input: Stream.of(Result.success(1), Result.success(2), Result.success(3))
 * Result<Stream<Integer>, Stream<String>> sequenceResult = ResultStream.sequenceApplicative(
 *     Stream.of(Result.success(1), Result.success(2), Result.success(3))
 * );
 *
 * // Expected Output: Success([1, 2, 3])
 *
 *
 * // traverseMonadic
 * // Input: Stream.of(1, 2, 3)
 * Result<Stream<Integer>, String> monadicResult = ResultStream.traverseMonadic(
 *     i -> i % 2 == 0 ? Result.success(i) : Result.failure("Odd number found")
 * ).apply(Stream.of(1, 2, 3));
 *
 * // Expected Output: Failure("Odd number found")
 *
 *
 * // sequenceMonadic
 * // Input: Stream.of(Result.success(1), Result.success(2), Result.failure("Error"))
 * Result<Stream<Integer>, String> sequenceMonadicResult = ResultStream.sequenceMonadic(
 *     Stream.of(Result.success(1), Result.success(2), Result.failure("Error"))
 * );
 *
 * // Expected Output: Failure("Error")
 * }</pre></blockquote>
 *
 * @see Result
 * @see ResultStream
 */
public sealed interface ResultList permits None {


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
    static <V, S, F> Function<List<V>, Result<List<S>, List<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
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
    static <S, F> Result<List<S>, List<F>> sequenceApplicative(List<Result<S, F>> list) {
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
    static <V, S, F> Function<List<V>, Result<List<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return list -> sequenceMonadic(list.stream().map(mapping)::iterator);
    }

    /**
     * Converts a list of {@code Result} objects into a single {@code Result} containing a list of success values.
     * Uses a monadic approach, meaning failures are short-circuited and the first failure encountered is returned.
     *
     * @param <S>        The success type of the result.
     * @param <F>        The failure type of the result.
     * @param iteratable The list of {@code Result<S, F>} values.
     * @return A {@code Result} containing a list of success values if all succeed, or the first encountered failure.
     */
    static <S, F> Result<List<S>, F> sequenceMonadic(Iterable<Result<S, F>> iteratable) {
        val result = new ArrayList<S>();
        for (final Result<S, F> item : iteratable) {
            if (item.isFailure()) {
                return failure(item.failure());
            }
            result.add(item.value());
        }
        return success(result);
    }

    private static <S, F> Function<Result<Stream<S>, Stream<F>>, Result<List<S>, List<F>>> mapEitherToList() {
        return mapEither(Stream::toList, Stream::toList);
    }
}
