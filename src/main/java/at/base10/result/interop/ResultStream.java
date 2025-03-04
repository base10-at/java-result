package at.base10.result.interop;

import at.base10.result.Result;

import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;
import static at.base10.result.Operator.mapEither;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

public final class ResultStream {

    private ResultStream() {
    }

    public static <V, S, F> Function<Stream<V>, Result<Stream<S>, Stream<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return s -> s.map(mapping.andThen(mapEitherToStream())).reduce(success(Stream.of()), ResultStream::applicativeReducer);
    }

    public static <V, S, F> Function<Stream<V>, Result<Stream<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return stream -> sequenceMonadic(stream.map(mapping));
    }

    public static <S, F> Result<Stream<S>, Stream<F>> sequenceApplicative(Stream<Result<S, F>> stream) {
        return stream.map(mapEitherToStream()).reduce(success(Stream.of()), ResultStream::applicativeReducer);
    }

    public static <S, F> Result<Stream<S>, F> sequenceMonadic(Stream<Result<S, F>> stream) {
        return stream.map(mapToStream()).reduce(success(Stream.of()), ResultStream::monadicReducer);
    }

    private static <S, F> Result<Stream<S>, Stream<F>> applicativeReducer(Result<Stream<S>, Stream<F>> acc, Result<Stream<S>, Stream<F>> elem) {
        return acc.either(reduceSuccess(elem), reduceFailure(acc, elem));
    }

    private static <S, F> Result<Stream<S>, F> monadicReducer(Result<Stream<S>, F> acc, Result<Stream<S>, F> elem) {
        return acc.either(reduceSuccess(elem), error -> acc);
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

    private static <S, F> Function<Result<S, F>, Result<Stream<S>, F>> mapToStream() {
        return map(Stream::of);
    }
}
