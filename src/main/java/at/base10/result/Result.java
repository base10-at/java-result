package at.base10.result;

import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract sealed class Result<S, F> permits Result.Success, Result.Failure {

    public abstract boolean isSuccess();


    protected abstract S getValue();

    protected abstract F getFailure();


    public <S2> S2 thenApply(Function<Result<S, F>, S2> fn) {
        return fn.apply(this);
    }

    public abstract <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn);

    public static final class Success<S, F> extends Result<S, F> {
        private final S value;

        private Success(S value) {
            super();
            this.value = value;
        }


        public boolean isSuccess() {
            return true;
        }

        public S getValue() {
            return value;
        }

        protected F getFailure() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
            return successFn.apply(value);
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

            Success<?, ?> success = (Success<?, ?>) o;
            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() + value.hashCode();
        }
    }

    public static final class Failure<S, F> extends Result<S, F> {
        private final F failure;

        private Failure(F failure) {
            super();
            this.failure = failure;
        }

        public boolean isSuccess() {
            return false;
        }

        protected S getValue() {
            throw new NoSuchElementException("No value present");
        }

        public F getFailure() {
            return failure;
        }

        @Override
        public <R2> R2 either(Function<S, R2> successFn, Function<F, R2> failureFn) {
            return failureFn.apply(failure);
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

            Failure<?, ?> failure1 = (Failure<?, ?>) o;
            return failure.equals(failure1.failure);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() + failure.hashCode();
        }
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }


    public static <S, F> Result<S, F> failure(F Failure) {
        return new Failure<>(Failure);
    }


}
