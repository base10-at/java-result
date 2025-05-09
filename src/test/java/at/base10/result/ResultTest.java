package at.base10.result;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void test_success() {
        assertSuccessEquals(42, success(42));
    }

    @Test
    void test_Failure() {
        assertFailureEquals(42, failure(42));
    }

    @Test
    void test_isSuccess() {
        assertTrue(success(42).isSuccess());
        assertFalse(success(42).isFailure());
    }

    @Test
    void test_isSuccessFN() {
        assertTrue(success(42).then(isSuccess()));
        assertFalse(success(42).then(isFailure()));
    }

    @Test
    void test_isFailure() {
        assertTrue(failure(42).isFailure());
        assertFalse(failure(42).isSuccess());
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes", "ConstantValue"})
    @Test
    void test_equals_failure() {
        // this setup is required to hit all branches in equals
        assertFalse(failure(42).equals(List.of()));
        assertFalse(failure(42).equals(null));
        assertFalse(failure(42).equals(new Object()));
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes", "ConstantValue"})
    @Test
    void test_equals_success() {
        // this setup is required to hit all branches in equals
        assertFalse(success(42).equals(List.of()));
        assertFalse(success(42).equals(null));
        assertFalse(success(42).equals(new Object()));
    }

    @Test
    void test_toString() {
        assertEquals("Success{value=" + 42 + '}', success(42).toString());
        assertEquals("Failure{failure=" + 42 + '}', failure(42).toString());
    }

    @Test
    void test_isFailureFN() {
        assertTrue(failure(42).then(isFailure()));
        assertFalse(failure(42).then(isSuccess()));
    }

    @Test
    void test_successEquals() {
        assertSuccessEquals(42, success(42));
    }

    @Test
    void test_flip_defaultsTo() {
        assertSuccessEquals(42, failure(42).then(flip()));
    }

    @Test
    void test_swap_defaultsTo() {
        assertSuccessEquals(42, failure(42).then(swap()));
    }

    @Test
    void test_swap_failure() {
        assertSuccessEquals(42, failure(42).swap());
    }

    @Test
    void test_swap_success() {
        assertFailureEquals(42, success(42).swap());
    }

    @Test
    void test_map() {
        assertSuccessEquals("421",
                success(42)
                        .then(map(x -> x + "1"))
        );
    }

    @Test
    void test_map_if_Failure() {
        assertFailureEquals(42, failure(42)
                .then(map(x -> x + "1"))
        );
    }

    @Test
    void test_mapFailure() {
        assertFailureEquals("421",
                failure(42)
                        .then(mapFailure(x -> x + "1"))

        );
    }

    @Test
    void test_mapFailure_if_success() {
        var result = success(42).then(mapFailure(x -> x + "1"));
        assertSuccessEquals(42, result);
    }

    @Test
    void test_recover_failure() {
        Result<String, Void> result = Result.failure(4, String.class)
                .recover(x -> x + "2");
        assertSuccessEquals("42", result);
    }

    @Test
    void test_recover_success() {
        Result<String, Void> result = Result.success("42", Integer.class)
                .recover(x -> x + "1");
        assertSuccessEquals("42", result);
    }

    @Test
    void test_operation_recover_failure() {
        Result<String, Void> result = Result.failure(4, String.class)
                .then(recover(x -> x + "2"));
        assertSuccessEquals("42", result);
    }

    @Test
    void test_operation_recover_success() {
        Result<String, Void> result = Result.success("42", Integer.class)
                .then(recover(x -> x + "1"));
        assertSuccessEquals("42", result);
    }

    @Test
    void test_anyMatch_op() {
        assertFalse(Result.<Integer, Integer>failure(42).then(anyMatch(x -> x == 42)));
        assertFalse(Result.<Integer, Integer>failure(42).then(anyMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).then(anyMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).then(anyMatch(x -> x == 42)));
        assertTrue(Result.<Integer, Integer>success(42).then(anyMatch(x -> x == 42)));
        //noinspection DataFlowIssue
        assertEquals(

                "predicate is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>success(42).then(anyMatch(null))
                ).getMessage()
        );
    }

    @Test
    void test_allMatch_op() {
        assertTrue(Result.<Integer, Integer>failure(42).then(allMatch(x -> x == 42)));
        assertTrue(Result.<Integer, Integer>failure(42).then(allMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).then(allMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).then(allMatch(x -> x == 42)));

        //noinspection DataFlowIssue
        assertEquals(
                "predicate is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>success(42).then(allMatch(null))
                ).getMessage()
        );
    }

    @Test
    void test_anyMatch() {
        assertFalse(Result.<Integer, Integer>failure(42).anyMatch(x -> x == 42));
        assertFalse(Result.<Integer, Integer>failure(42).anyMatch(x -> x == 43));
        assertFalse(Result.<Integer, Integer>success(42).anyMatch(x -> x == 43));
        assertTrue(Result.<Integer, Integer>success(42).anyMatch(x -> x == 42));
    }

    @Test
    void test_allMatch() {
        assertTrue(Result.<Integer, Integer>failure(42).allMatch(x -> x == 42));
        assertTrue(Result.<Integer, Integer>failure(42).allMatch(x -> x == 43));
        assertFalse(Result.<Integer, Integer>success(42).allMatch(x -> x == 43));
        assertTrue(Result.<Integer, Integer>success(42).allMatch(x -> x == 42));
    }

    @Test
    void test_equals() {
        assertEquals(failure(42), failure(42));
        assertNotEquals(failure(43), failure(42));
        assertNotEquals(null, failure(43));

        assertNotEquals(success(42), failure(42));

        assertEquals(success(42), success(42));
        assertNotEquals(success(43), success(42));
        assertNotEquals(null, success(43));
    }

    @Test
    void test_hashCode() {
        assertEquals(1986982223, failure(42).hashCode());
        assertEquals(failure(42).hashCode(), failure(42).hashCode());
        assertNotEquals(failure(43).hashCode(), failure(42).hashCode());

        assertNotEquals(success(42).hashCode(), failure(42).hashCode());

        assertEquals(-736644618, success(42).hashCode());
        assertEquals(success(42).hashCode(), success(42).hashCode());
        assertNotEquals(success(43).hashCode(), success(42).hashCode());
    }

    @Test
    void test_toList() {
        assertEquals(List.of(), Result.<Integer, Integer>failure(42).then(toList()));
        assertEquals(List.of(42), Result.<Integer, Integer>success(42).then(toList()));
    }

    @Test
    void test_toStream() {
        assertEquals(List.of(), Result.<Integer, Integer>failure(42).then(toStream()).toList());
        assertEquals(List.of(42), Result.<Integer, Integer>success(42).then(toStream()).toList());
    }

    @Test
    void test_toOptional() {
        assertEquals(Optional.empty(), Result.<Integer, Integer>failure(42).then(toOptional()));
        assertEquals(Optional.of(42), Result.<Integer, Integer>success(42).then(toOptional()));
    }

    @Test
    void test_defaultsToValue() {
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsToValue(43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsToValue(43)));
    }

    @Test
    @SuppressWarnings("deprecation")
    void test_defaultsTo() {
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsTo(43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsTo(43)));
    }

    @Test
    void test_defaultsWithSupplier() {
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsWith(() -> 43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsWith(() -> 43)));

        //noinspection DataFlowIssue
        assertEquals(
                "supplier is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>success(42).then(defaultsWith((Supplier<Integer>) null))
                ).getMessage()
        );
    }

    @SuppressWarnings("deprecation")
    @Test
    void test_defaultsToSupplier() {
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsTo(() -> 43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsTo(() -> 43)));

        //noinspection DataFlowIssue
        assertEquals(
                "supplier is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>success(42).then(defaultsTo(null))
                ).getMessage()
        );
    }

    @Test
    void test_count() {
        assertEquals(0, Result.<Integer, Integer>failure(42).then(count()));
        assertEquals(1, Result.<Integer, Integer>success(42).then(count()));
    }

    @Test
    void test_hash() {
        assertNotEquals(success(42).hashCode(), failure(42).hashCode());
        assertNotEquals(success(42).hashCode(), success(43).hashCode());
    }

    @Test
    void testFromOptional() {
        assertSuccessEquals(42, Result.fromOptional(Optional.of(42), () -> "Error"));
        assertSuccessEquals(42, Result.fromOptional(Optional.of(42), () -> null));
        assertSuccessEquals(42, Result.fromOptional(Optional.of(42)));
        assertFailureEquals(null, Result.fromOptional(Optional.empty()));
        assertFailureEquals("Error", Result.fromOptional(Optional.empty(), () -> "Error"));
    }

    @Test
    void testFromPredicate() {
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43));
        assertFailureEquals(null, Result.fromPredicate(42, x -> x > 43));
    }

    @Test
    void testFromPredicateWithSuppliers() {
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43, () -> "Error"));
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43, () -> null));
        assertFailureEquals("Error", Result.fromPredicate(42, x -> x > 43, () -> "Error"));
    }

    @Test
    void testFromBoolean() {
        assertSuccessEquals(true, Result.fromBoolean(true));
        assertFailureEquals(false, Result.fromBoolean(false));
    }

    @Test
    void testFromBooleanWithSuppliers() {
        assertSuccessEquals(42, Result.fromBoolean(true, () -> 42, () -> "Error"));
        assertFailureEquals("Error", Result.fromBoolean(false, () -> 42, () -> "Error"));
    }

    @Test
    void testThrow() {
        assertEquals(42, success(42).orThrow(f -> new IllegalArgumentException("foo")));

        assertEquals(
                "foo",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> failure(42).orThrow(f -> new IllegalArgumentException("foo"))
                ).getMessage()
        );

    }

    @Test
    void testThrowOp() {
        assertEquals(42, success(42).then(orThrow(f -> new IllegalArgumentException("foo"))));
        assertEquals(
                "foo",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> failure(42).then(orThrow(f -> new IllegalArgumentException("foo")))
                ).getMessage()
        );

    }

    @Test
    void testThrowDefault() {
        assertEquals(42, success(42).orThrow());
        assertThrows(NoSuchElementException.class, () -> failure(42).orThrow(), "No value present");
    }

    @Test
    void testThrowDefaultOp() {
        assertEquals(42, success(42).then(orThrow()));
        assertThrows(NoSuchElementException.class, () -> failure(42).then(orThrow()), "No value present");
    }

    @Test
    void testOrElse() {
        assertEquals(42, success(42, Integer.class).orElse(f -> f + 1));
        assertEquals(43, failure(42, Integer.class).orElse(f -> f + 1));
    }

    @Test
    void test_defaultsWith() {
        assertEquals(42, success(42, Integer.class).then(defaultsWith(f -> f + 1)));
        assertEquals(43, failure(42, Integer.class).then(defaultsWith(f -> f + 1)));

        //noinspection DataFlowIssue
        assertEquals(
                "failureMapping is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>success(42).then(defaultsWith((Function<Integer, Integer>) null))
                ).getMessage()
        );
    }

    @Test
    void testNone() {
        assertInstanceOf(None.class, new None());
    }
}