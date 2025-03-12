package at.base10.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract sealed class Result<S, F> permits Success, Failure {

    public static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    @SuppressWarnings("unused")
    public static <S, F> Result<S, F> success(S value, Class<F> failureType) {
        return new Success<>(value);
    }

    public static <S, F> Result<S, F> failure(F Failure) {
        return new Failure<>(Failure);
    }

    @SuppressWarnings("unused")
    public static <S, F> Result<S, F> failure(F Failure, Class<S> successType) {
        return new Failure<>(Failure);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, F> Result<S, F> fromOptional(Optional<S> optional, Supplier<F> supplier) {
        return optional.map(Result::<S, F>success).orElse(Result.failure(supplier.get()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S> Result<S, Void> fromOptional(Optional<S> optional) {
        return fromOptional(optional, () -> null);
    }

    public static <S, F> Result<S, F> fromPredicate(S value, Predicate<S> predicate, Supplier<F> supplier) {
        return predicate.test(value) ? success(value) : failure(supplier.get());
    }

    public static <S> Result<S, Void> fromPredicate(S value, Predicate<S> predicate) {
        return fromPredicate(value, predicate, () -> null);
    }

    public static <S, F> Result<S, F> fromBoolean(Boolean value, Supplier<S> successFn, Supplier<F> failureFn) {
        return value ? success(successFn.get()) : failure(failureFn.get());
    }

    public static Result<Boolean, Boolean> fromBoolean(Boolean value) {
        return value ? success(true) : failure(false);
    }

    public abstract boolean isSuccess();

    public boolean isFailure() {
        return !isSuccess();
    }

    protected abstract S getValue();

    protected abstract F getFailure();

    public <S2> S2 then(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    abstract public <S2, F2> Result<S2, F2> mapEither(Function<S, S2> mapper, Function<F, F2> errMapper);

    abstract public <S2> Result<S2, F> map(Function<S, S2> mapper);

    abstract public <F2> Result<S, F2> mapFailure(Function<F, F2> mapper);

    abstract public <S2, F2> Result<S2, F2> bindEither(Function<S, Result<S2, F2>> binding, Function<F, Result<S2, F2>> bindingFailure);

    abstract public <S2> Result<S2, F> bind(Function<S, Result<S2, F>> binding);

    abstract public <F2> Result<S, F2> bindFailure(Function<F, Result<S, F2>> binding);

    public abstract <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn);

    public abstract S orThrow();

    public abstract <E extends RuntimeException> S orThrow(Function<F, E> exceptionFunction) throws E;

    public abstract Optional<S> toOptional();


}
