package at.base10.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@inheritDoc}
 * Representing the result of an operation succeeded.
 *
 * @param value the success value
 * @param <S> the type representing a successful result
 * @param <F> the type representing a failure result
 */
public record Success<S, F>(S value) implements Result<S, F> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccess() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public F failure() {
        throw new NoSuchElementException("No value present");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> S2 then(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
        return successFn.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper) {
        return new Success<>(mapper.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        return new Success<>(mapper.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> bindEither(Function<S, Result<S2, F2>> binding, Function<F, Result<S2, F2>> bindingFailure) {
        return binding.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        return binding.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekEither(Consumer<S> consumer, Consumer<F> errConsumer) {
        consumer.accept(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peek(Consumer<S> consumer) {
        consumer.accept(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekFailure(Consumer<F> consumer) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orThrow() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) {
        return orThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orElse(Function<F, S> failureMapping) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<S> toOptional() {
        return Optional.of(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Success{value=%s}".formatted(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && value.equals(((Success<?, ?>) o).value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getCanonicalName() , value);
    }
}
