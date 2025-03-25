package at.base10.result;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public record Success<S, F>(S value) implements Result<S, F> {

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public F failure() {
        throw new NoSuchElementException("No value present");
    }


    @Override
    public <S2> S2 then(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    @Override
    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return successFn.apply(value);
    }

    @Override
    public <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper) {
        return new Success<>(mapper.apply(value));
    }

    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        return new Success<>(mapper.apply(value));
    }

    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    @Override
    public <S2, F2> Result<S2, F2> bindEither(Function<S, Result<S2, F2>> binding, Function<F, Result<S2, F2>> bindingFailure) {
        return binding.apply(value);
    }

    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        return binding.apply(value);
    }

    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    @Override
    public S orThrow() {
        return value;
    }

    @Override
    public <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) {
        return orThrow();
    }

    @Override
    public Optional<S> toOptional() {
        return Optional.of(value);
    }

    @Override
    public String toString() {
        return "Success{value=%s}".formatted(value);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && value.equals(((Success<?, ?>) o).value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + value.hashCode();
    }

}
