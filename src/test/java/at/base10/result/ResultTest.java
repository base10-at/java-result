package at.base10.result;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
    void test_defaultsTo() {
        assertSuccessEquals(42, success(42));
    }

    @Test
    void test_flip_defaultsTo() {
        assertSuccessEquals(42, failure(42).then(flip()));
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
    void test_bind() {
        var result = Result.<Integer, Integer>success(42).then(bind(x -> success(x + "1")));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_bind_if_Failure() {
        var result = failure(42).then(bind(x -> success(x + "1")));
        assertFailureEquals(42, result);
    }

    @Test
    void test_bind_either() {

        assertSuccessEquals(43, Result.<Integer, Integer>success(42).then(
                bind(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
        assertFailureEquals(41, Result.<Integer, Integer>failure(42).then(
                bind(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
    }

    @Test
    void test_bindFailure() {
        var result = Result.<String, Integer>failure(42).then(bindFailure(x -> success(x + "1")));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_bindFailure_if_success() {
        var input = Result.<Integer, Integer>success(42);
        var result = input.then(bindFailure(x -> success(x + 1)));
        assertSuccessEquals(42, result);
    }

    @Test
    void test_anyMatch() {
        assertFalse(Result.<Integer, Integer>failure(42).then(anyMatch(x -> x == 42)));
        assertFalse(Result.<Integer, Integer>failure(42).then(anyMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).then(anyMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).then(anyMatch(x -> x == 42)));
    }

    @Test
    void test_allMatch() {
        assertTrue(Result.<Integer, Integer>failure(42).then(allMatch(x -> x == 42)));
        assertTrue(Result.<Integer, Integer>failure(42).then(allMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).then(allMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).then(allMatch(x -> x == 42)));
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
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsTo(43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsTo(43)));
    }


    @Test
    void test_defaultsToSupplier() {
        assertEquals(43, Result.<Integer, Integer>failure(42).then(defaultsTo(() -> 43)));
        assertEquals(42, Result.<Integer, Integer>success(42).then(defaultsTo(() -> 43)));
    }

    @Test
    void test_count() {
        assertEquals(0, Result.<Integer, Integer>failure(42).then(count()));
        assertEquals(1, Result.<Integer, Integer>success(42).then(count()));
    }

    @Test
    void test_peek_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(peek(
                                val::set,
                                err::set
                        ))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peek_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.<Integer, Integer>failure(42)
                        .then(then(peek(
                                val::set,
                                err::set
                        )))
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }

    @Test
    void test_hash() {

        assertNotEquals(success(42).hashCode(), failure(42).hashCode());
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
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43, () -> "Error"));
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43, () -> null));
        assertSuccessEquals(42, Result.fromPredicate(42, x -> x < 43));
        assertFailureEquals(null, Result.fromPredicate(42, x -> x > 43));
        assertFailureEquals("Error", Result.fromPredicate(42, x -> x > 43, () -> "Error"));
    }
}