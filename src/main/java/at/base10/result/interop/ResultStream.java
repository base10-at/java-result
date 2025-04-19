package at.base10.result.interop;

import at.base10.result.Result;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.mapEither;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

/**
 * A utility class providing functional operations on {@code Stream} values in the context of {@code Result}.
 *
 * <p>The {@code ResultStream} class offers methods to transform and sequence {@code Stream} values that contain
 * {@code Result} instances, supporting both applicative and monadic traversal.
 *
 * <p>These utilities facilitate functional programming patterns for handling streams of computations
 * that may succeed or fail, integrating seamlessly with the {@link Result} type.
 *
 * <p>All methods in this class are static, and the constructor is private to prevent instantiation.
 *
 * @see Result
 */
public sealed interface ResultStream permits None {

    /**
     * Applies a mapping function to each element in the stream and collects the results into a single {@code Result}.
     * Uses an applicative approach, meaning all elements are processed independently, and failures are accumulated.
     *
     * @param <V>     The type of elements in the input stream.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to each element, producing a {@code Result<S, F>}.
     * @return A function that transforms a stream of {@code V} into a {@code Result<Stream<S>, Stream<F>>}.
     */
    static <V, S, F> Function<Stream<V>, Result<Stream<S>, Stream<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return s -> s.map(mapping.andThen(mapEitherToStream())).reduce(success(Stream.of()), ResultStream::applicativeReducer);
    }

    /**
     * Applies a mapping function to each element in the stream and collects the results into a single {@code Result}.
     * Uses a monadic approach, meaning failures are short-circuited and the first failure encountered is returned.
     *
     * @param <V>     The type of elements in the input stream.
     * @param <S>     The success type of the result.
     * @param <F>     The failure type of the result.
     * @param mapping The function to apply to each element, producing a {@code Result<S, F>}.
     * @return A function that transforms a stream of {@code V} into a {@code Result<Stream<S>, F>}, stopping at the first failure.
     */
    static <V, S, F> Function<Stream<V>, Result<Stream<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return stream -> sequenceMonadic(stream.map(mapping));
    }

    /**
     * Converts a stream of {@code Result} objects into a single {@code Result} containing streams of success and failure values.
     * Uses an applicative approach, meaning all elements are processed independently, and failures are accumulated.
     *
     * @param <S>    The success type of the result.
     * @param <F>    The failure type of the result.
     * @param stream The stream of {@code Result<S, F>} values.
     * @return A {@code Result} containing a stream of success values if all succeed, or a stream of failures otherwise.
     */
    static <S, F> Result<Stream<S>, Stream<F>> sequenceApplicative(Stream<Result<S, F>> stream) {
        return stream.map(mapEitherToStream()).reduce(success(Stream.of()), ResultStream::applicativeReducer);
    }

    /**
     * Converts a stream of {@code Result} objects into a single {@code Result} containing a stream of success values.
     * Uses a monadic approach, meaning failures are short-circuited and the first failure encountered is returned.
     *
     * @param <S>    The success type of the result.
     * @param <F>    The failure type of the result.
     * @param stream The stream of {@code Result<S, F>} values.
     * @return A {@code Result} containing a stream of success values if all succeed, or the first encountered failure.
     */
    static <S, F> Result<Stream<S>, F> sequenceMonadic(Stream<Result<S, F>> stream) {
        return ResultList.sequenceMonadic(stream::iterator).map(Collection::stream);
    }

    private static <S, F> Result<Stream<S>, Stream<F>> applicativeReducer(Result<Stream<S>, Stream<F>> acc, Result<Stream<S>, Stream<F>> elem) {
        return acc.either(reduceSuccess(elem), reduceFailure(acc, elem));
    }

    private static <S, F> Function<Stream<F>, Result<Stream<S>, Stream<F>>> reduceFailure(Result<Stream<S>, Stream<F>> acc, Result<Stream<S>, Stream<F>> elem) {
        return (error) -> elem.either(s -> acc, f -> failure(Stream.concat(error, f)));
    }

    private static <S, F> Function<Stream<S>, Result<Stream<S>, F>> reduceSuccess(Result<Stream<S>, F> elem) {
        return success -> elem.either(s -> success(Stream.concat(success, s)), Result::failure);
    }

    private static <S, F> Function<Result<S, F>, Result<Stream<S>, Stream<F>>> mapEitherToStream() {
        return mapEither(Stream::of, Stream::of);
    }
}
