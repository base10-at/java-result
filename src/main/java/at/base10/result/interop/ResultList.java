package at.base10.result.interop;


import at.base10.result.Result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;

public final class ResultList {

    private ResultList() {
    }

    public static <V, S, F> Function<List<V>, Result<List<S>, List<F>>> traverseApplicative(Function<V, Result<S, F>> mapping) {
        return list -> ResultStream.traverseApplicative(mapping).andThen(mapEitherToList()).apply(list.stream());
    }

    public static <S, F> Result<List<S>, List<F>> sequenceApplicative(List<Result<S, F>> list) {
        return ResultStream.sequenceApplicative(list.stream()).then(mapEitherToList());
    }

    public static <V, S, F> Function<List<V>, Result<List<S>, F>> traverseMonadic(Function<V, Result<S, F>> mapping) {
        return list -> ResultStream.traverseMonadic(mapping).andThen(mapToList()).apply(list.stream());
    }

    public static <S, F> Result<List<S>, F> sequenceMonadic(List<Result<S, F>> list) {
        return ResultStream.sequenceMonadic(list.stream()).then(mapToList());
    }

    private static <S, F> Function<Result<Stream<S>, Stream<F>>, Result<List<S>, List<F>>> mapEitherToList() {
        return map(Stream::toList, Stream::toList);
    }

    private static <S, F> Function<Result<Stream<S>, F>, Result<List<S>, F>> mapToList() {
        return map(Stream::toList);
    }
}
