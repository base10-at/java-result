package at.base10.result;

import lombok.NonNull;

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
 * @param <S>   the type representing a successful result
 * @param <F>   the type representing a failure result
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
    public <S2> S2 then(@NonNull Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<F, S> swap() {
        return new Failure<>(this.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R2> R2 either(@NonNull Function<S, R2> successFn, @NonNull Function<F, R2> failureFn) {
        return successFn.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> mapEither(@NonNull Function<S, S2> mapper, @NonNull Function<F, F2> errMapper) {
        return new Success<>(mapper.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> map(@NonNull Function<S, S2> mapper) {
        return new Success<>(mapper.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> mapFailure(@NonNull Function<F, F2> mapper) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> bindEither(@NonNull Function<S, Result<S2, F2>> binding, @NonNull Function<F, Result<S2, F2>> bindingFailure) {
        return binding.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> bind(@NonNull Function<S, Result<S2, F>> binding) {
        return binding.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> bindFailure(@NonNull Function<F, Result<S, F2>> binding) {
        //noinspection unchecked
        return (Result<S, F2>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> flatMapEither(Function<S, Result<S2, F2>> mapping, Function<F, Result<S2, F2>> mappingFailure) {
        return bindEither(mapping, mappingFailure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> flatMap(Function<S, Result<S2, F>> mapping) {
        return bind(mapping);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> flatMapFailure(Function<F, Result<S, F2>> mappingFailure) {
        return bindFailure((mappingFailure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekEither(@NonNull Consumer<S> consumer, @NonNull Consumer<F> errConsumer) {
        consumer.accept(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peek(@NonNull Consumer<S> consumer) {
        consumer.accept(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekFailure(@NonNull Consumer<F> consumer) {
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
    public <E extends RuntimeException> S orThrow(@NonNull Function<F, E> exceptionFunction) {
        return orThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orElse(@NonNull Function<F, S> failureMapping) {
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
    public Result<S, Void> recover(@NonNull Function<F, S> recoveryFn) {
        //noinspection unchecked
        return (Result<S, Void>) this;
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
    public int hashCode() {
        return Objects.hash(getClass().getCanonicalName(), value);
    }
}
