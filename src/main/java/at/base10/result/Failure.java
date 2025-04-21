package at.base10.result;

import lombok.NonNull;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 * Representing the result of an operation failed.
 *
 * @param failure F the failure value
 * @param <S>     the type representing a successful result
 * @param <F>     the type representing a failure result
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
    public <S2> S2 then(@NonNull Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<F, S> swap() {
        return new Success<>(this.failure());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R2> R2 either(@NonNull Function<S, R2> successFn, @NonNull Function<F, R2> failureFn) {
        return failureFn.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> mapEither(@NonNull Function<S, S2> mapper, @NonNull Function<F, F2> errMapper) {
        return new Failure<>(errMapper.apply(failure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> map(@NonNull Function<S, S2> mapper) {
        //noinspection unchecked
        return (Result<S2, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> mapFailure(@NonNull Function<F, F2> mapper) {
        return new Failure<>(mapper.apply(failure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2, F2> Result<S2, F2> bindEither(@NonNull Function<S, Result<S2, F2>> binding, @NonNull Function<F, Result<S2, F2>> bindingFailure) {
        return bindingFailure.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S2> Result<S2, F> bind(@NonNull Function<S, Result<S2, F>> binding) {
        //noinspection unchecked
        return (Result<S2, F>) this;
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
        errConsumer.accept(failure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peek(@NonNull Consumer<S> consumer) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, F> peekFailure(@NonNull Consumer<F> consumer) {
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
    public <E extends RuntimeException> S orThrow(@NonNull Function<F, E> exceptionFunction) throws E {
        throw exceptionFunction.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orElse(@NonNull Function<F, S> failureMapping) {
        return failureMapping.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F2> Result<S, F2> bindFailure(@NonNull Function<F, Result<S, F2>> binding) {
        return binding.apply(failure);
    }

    /**
     * {@inheritDoc}
     */
    public List<S> toList() {
        return List.of();
    }

    /**
     * {@inheritDoc}
     */
    public Stream<S> toStream() {
        return Stream.empty();
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
    public int count() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<S, Void> recover(@NonNull Function<F, S> recoveryFn) {
        return Result.success(recoveryFn.apply(failure));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean anyMatch(Predicate<S> predicate) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allMatch(Predicate<S> predicate) {
        return true;
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
    public int hashCode() {
        return Objects.hash(getClass().getCanonicalName(), failure);
    }
}
