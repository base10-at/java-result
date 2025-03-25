package at.base10.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@inheritDoc}
 * Representing the result of an operation failed.
 *
 * @param failure F the failure value
 * @param <S> the type representing a successful result
 * @param <F> the type representing a failure result
 */
public record Failure<S, F>(F failure) implements Result<S, F> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S value() {
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
        return failureFn.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper) {
        return new Failure<>(errMapper.apply(failure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> map(Function<S, S2> mapper) {
        //noinspection unchecked
        return (Result<S2, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper) {
        return new Failure<>(mapper.apply(failure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> bindEither(
            Function<S, Result<S2, F2>> binding,
            Function<F, Result<S2, F2>> bindingFailure
    ) {
        return bindingFailure.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding) {
        //noinspection unchecked
        return (Result<S2, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekEither(Consumer<S> consumer, Consumer<F> errConsumer) {
        errConsumer.accept(failure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peek(Consumer<S> consumer) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekFailure(Consumer<F> consumer) {
        consumer.accept(failure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orThrow() {
        throw new NoSuchElementException("No value present");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) throws E {
        throw exceptionFunction.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding) {
        return binding.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<S> toOptional() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Failure{failure=%s}".formatted(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && failure.equals(((Failure<?, ?>) o).failure);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getCanonicalName() , failure);
    }
}
