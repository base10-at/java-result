package at.base10.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class NonNullResultOperatorTest {

    private static Stream<Arguments> fnProvider() {


        //noinspection DataFlowIssue
        return Stream.of(
                new Case(() -> Operator.bindFailureAsync(null), "binding"),
                new Case(() -> Operator.bindFailure(null), "binding"),
                new Case(() -> Operator.bind(null), "binding"),
                new Case(() -> Operator.bindAsync(null), "binding"),
                new Case(() -> Operator.bindEither(null, e -> Result.success(e, Integer.class)), "binding"),
                new Case(() -> Operator.bindEither(e -> Result.success(e, Integer.class), null), "bindingFailure"),

                new Case(() -> Operator.mapFailure(null), "mapper"),
                new Case(() -> Operator.map(null), "mapper"),
                new Case(() -> Operator.mapEither(null, e -> Result.success(e, Integer.class)), "mapper"),
                new Case(() -> Operator.mapEither(e -> Result.success(e, Integer.class), null), "errMapper"),

                new Case(() -> Operator.peekFailure(null), "failure"),
                new Case(() -> Operator.peek(null), "success"),
                new Case(() -> Operator.peekEither(null, e -> Result.success(e, Integer.class)), "success"),
                new Case(() -> Operator.peekEither(e -> Result.success(e, Integer.class), null), "failure"),


                new Case(() -> Operator.recover(null), "recoveryFn"),
                new Case(() -> Operator.ifSuccess(null), "success"),
                new Case(() -> Operator.ifFailure(null), "failure"),

                new Case(() -> Operator.orThrow(null), "exceptionFunction")

        ).map(c -> Arguments.of(c.fn(), c.paramName()));
    }

    private String nullString(String paramName) {
        return paramName + " is marked non-null but is null";
    }

    @DisplayName("assert NonNull Failure function param")
    @ParameterizedTest(name = "{index} => paramName={1}")
    @MethodSource("fnProvider")
    void assertNonNullFailure(Supplier<Object> fn, String paramName) {

        assertEquals(nullString(paramName),
                assertThrows(NullPointerException.class, fn::get).getMessage());

    }

    record Case(
            Supplier<Object> fn,
            String paramName
    ) {
    }


}
