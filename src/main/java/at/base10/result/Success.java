package at.base10.result;

import java.util.NoSuchElementException;
import java.util.function.Function;

final class Success<S, F> extends Result<S, F> {
    private final S value;

    Success(S value) {
        super();
        this.value = value;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public S getValue() {
        return value;
    }

    @Override
    protected F getFailure() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return successFn.apply(value);
    }

    @Override
    public <S2, F2> Result<S2, F2> map(
            Function<S, S2> mapper,
            Function<F, F2> errMapper
    ) {
        return new Success<>(mapper.apply(value));
    }

    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        return new Success<>(mapper.apply(value));
    }

    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        return new Success<>(value);
    }

    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        return binding.apply(value);
    }

    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        return new Success<>(value);
    }

    @Override
    public String toString() {
        return "Success{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        at.base10.result.Success<?, ?> success = (at.base10.result.Success<?, ?>) o;
        return value.equals(success.value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + value.hashCode();
    }
}
