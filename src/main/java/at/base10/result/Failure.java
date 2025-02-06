package at.base10.result;

import java.util.NoSuchElementException;
import java.util.function.Function;

final class Failure<S, F> extends Result<S, F> {
    private final F failure;

    Failure(F failure) {
        super();
        this.failure = failure;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    protected S getValue() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public F getFailure() {
        return failure;
    }

    @Override
    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return failureFn.apply(failure);
    }

    @Override
    public <S2, F2> Result<S2, F2> map(
            Function<S, S2> mapper,
            Function<F, F2> errMapper
    ) {
        return new Failure<>(errMapper.apply(failure));
    }


    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        return new Failure<>(failure);
    }

    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        return new Failure<>(mapper.apply(failure));
    }

    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        return new Failure<>(failure);
    }

    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        return binding.apply(failure);
    }

    @Override
    public String toString() {
        return "Failure{" +
                "failure=" + failure +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        at.base10.result.Failure<?, ?> failure1 = (at.base10.result.Failure<?, ?>) o;
        return failure.equals(failure1.failure);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + failure.hashCode();
    }
}
