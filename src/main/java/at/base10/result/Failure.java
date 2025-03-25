package at.base10.result;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public record Failure<S, F>(F failure) implements Result<S, F> {

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public S value() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public <S2> S2 then(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    @Override
    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return failureFn.apply(failure);
    }

    @Override
    public <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper) {
        return new Failure<>(errMapper.apply(failure));
    }

    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        //noinspection unchecked
        return (Result<S2, F>) this;
    }

    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        return new Failure<>(mapper.apply(failure));
    }

    @Override
    public <S2, F2> Result<S2, F2> bindEither(
            Function<S, Result<S2, F2>> binding,
            Function<F, Result<S2, F2>> bindingFailure
    ) {
        return bindingFailure.apply(failure);
    }

    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        //noinspection unchecked
        return (Result<S2, F>) this;
    }

    @Override
    public S orThrow() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) throws E {
        throw exceptionFunction.apply(failure);
    }

    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        return binding.apply(failure);
    }

    @Override
    public Optional<S> toOptional() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Failure{failure=%s}".formatted(failure);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && failure.equals(((Failure<?, ?>) o).failure);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + failure.hashCode();
    }

}
