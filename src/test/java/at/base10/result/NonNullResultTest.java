package at.base10.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class NonNullResultTest {

    private String nullString(String paramName) {
        return paramName + " is marked non-null but is null";
    }

    @DisplayName("assert NonNull Success function param")
    @ParameterizedTest(name = "{index} => paramName={1}")
    @MethodSource("fnProvider")
    void assertNonNullSuccess(Function<Result<Integer, Integer>, Object> fn, String paramName) {
        assertEquals(nullString(paramName),
                assertThrows(NullPointerException.class, () -> fn.apply(Result.success(42))).getMessage());


    }

    @DisplayName("assert NonNull Failure function param")
    @ParameterizedTest(name = "{index} => paramName={1}")
    @MethodSource("fnProvider")
    void assertNonNullFailure(Function<Result<Integer, Integer>, Object> fn, String paramName) {

        assertEquals(nullString(paramName),
                assertThrows(NullPointerException.class, () -> fn.apply(Result.failure(42))).getMessage());

    }

    record Case<E>(
            Function<Result<E, E>, Object> fn,
            String paramName
    ) {
    }

    private static Stream<Arguments> fnProvider() {


        return Stream.of(
                new Case<>((Result<Integer, Integer> f) -> f.bindFailure(null), "binding"),
                new Case<>((Result<Integer, Integer> f) -> f.bind(null), "binding"),
                new Case<>((Result<Integer, Integer> f) -> f.bindEither(null,e -> Result.success(e,Integer.class)), "binding"),
                new Case<>((Result<Integer, Integer> f) -> f.bindEither(e -> Result.success(e,Integer.class),null), "bindingFailure"),

                new Case<>((Result<Integer, Integer> f) -> f.mapFailure(null), "mapper"),
                new Case<>((Result<Integer, Integer> f) -> f.map(null), "mapper"),
                new Case<>((Result<Integer, Integer> f) -> f.mapEither(null,e -> Result.success(e,Integer.class)), "mapper"),
                new Case<>((Result<Integer, Integer> f) -> f.mapEither(e -> Result.success(e,Integer.class),null), "errMapper"),

                new Case<>((Result<Integer, Integer> f) -> f.peekFailure(null), "consumer"),
                new Case<>((Result<Integer, Integer> f) -> f.peek(null), "consumer"),
                new Case<>((Result<Integer, Integer> f) -> f.peekEither(null,e -> Result.success(e,Integer.class)), "consumer"),
                new Case<>((Result<Integer, Integer> f) -> f.peekEither(e -> Result.success(e,Integer.class),null), "errConsumer"),

                new Case<>((Result<Integer, Integer> f) -> f.either(null,e -> Result.success(e,Integer.class)), "successFn"),
                new Case<>((Result<Integer, Integer> f) -> f.either(e -> Result.success(e,Integer.class),null), "failureFn"),

                new Case<>((Result<Integer, Integer> f) -> f.then(null), "fn"),

                new Case<>((Result<Integer, Integer> f) -> f.recover(null), "recoveryFn"),
                new Case<>((Result<Integer, Integer> f) -> f.orThrow(null), "exceptionFunction"),
                new Case<>((Result<Integer, Integer> f) -> f.orElse(null), "failureMapping")

        ).map(c -> Arguments.of(c.fn(), c.paramName()));
    }

}
