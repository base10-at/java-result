package at.base10.result;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;

public final class Monadic {

    private Monadic() {
    }


    public static <V, S, F> Function<Optional<V>, Result<Optional<S>, F>> traverseOptional(Function<V, Result<S, F>> mapping) {
        return optional -> sequenceOptional(optional.map(mapping));
    }

    public static <V, S, F> Function<List<V>, Result<List<S>, F>> traverseList(Function<V, Result<S, F>> mapping) {
        return list -> traverseStream(mapping)
                .apply(list.stream())
                .thenApply(map(Stream::toList));
    }


    public static <V, S, F> Function<Stream<V>, Result<Stream<S>, F>> traverseStream(Function<V, Result<S, F>> mapping) {
        return stream -> sequenceStream(stream.map(mapping));
    }


    public static <S, F> Result<Optional<S>, F> sequenceOptional(Optional<Result<S, F>> optional) {
        return optional
                .map(map(Optional::of, Function.identity()))
                .orElseGet(() -> Result.success(Optional.empty()));
    }

    public static <S, F> Result<List<S>, F> sequenceList(List<Result<S, F>> list) {
        return sequenceStream(list.stream()).thenApply(map(Stream::toList));
    }


    public static <S, F> Result<Stream<S>, F> sequenceStream(Stream<Result<S, F>> stream) {
        return stream
                .map(Operator.map(Stream::of))
                .reduce(Result.success(Stream.of()), Monadic::reducer);
    }


    private static <S, F> Result<Stream<S>, F> reducer(Result<Stream<S>, F> acc, Result<Stream<S>, F> elem) {
        return acc.either(
                (success) -> elem.either(
                        s -> Result.success(Stream.concat(success, s)),
                        Result::failure
                ),
                (error) -> acc
        );
    }
}
