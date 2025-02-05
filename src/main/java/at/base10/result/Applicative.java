package at.base10.result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;

public final class Applicative {

    private Applicative() {
    }

    public static <V, S, F> Function<List<V>, Result<List<S>, List<F>>> traverseList(Function<V, Result<S, F>> mapping) {
        return list -> traverseStream(mapping)
                .apply(list.stream())
                .thenApply(map(Stream::toList, Stream::toList));
    }

    public static <V, S, F> Function<Stream<V>, Result<Stream<S>, Stream<F>>> traverseStream(Function<V, Result<S, F>> mapping) {
        return stream -> stream
                .map(mapping)
                .map(Operator.map(Stream::of, Stream::of))
                .reduce(Result.success(Stream.of()), Applicative::reducer);
    }

    public static <S, F> Result<List<S>, List<F>> sequenceList(List<Result<S, F>> list) {
        return sequenceStream(list.stream())
                .thenApply(map(Stream::toList, Stream::toList));
    }

    public static <S, F> Result<Stream<S>, Stream<F>> sequenceStream(Stream<Result<S, F>> stream) {
        return stream.map(Operator.map(Stream::of, Stream::of))
                .reduce(Result.success(Stream.of()), Applicative::reducer);
    }

    private static <S, F> Result<Stream<S>, Stream<F>> reducer(Result<Stream<S>, Stream<F>> acc, Result<Stream<S>, Stream<F>> elem) {
        return acc.either(
                (success) -> elem.either(
                        s -> Result.success(Stream.concat(success, s)),
                        Result::failure
                ),
                (error) -> elem.either(
                        s -> acc,
                        f -> Result.failure(Stream.concat(error, f))
                )
        );
    }
}
