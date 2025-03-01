package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.peek;
import static at.base10.result.Operator.peekFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Nested
class ResultPeekTest {
    @Test
    void test_peekEither_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(peek(val::set, err::set))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekEither_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.<Integer, Integer>failure(42)
                        .then(peek(val::set, err::set))
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }

    @Test
    void test_peekSuccess_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(peek(val::set))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekSuccess_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.<Integer, Integer>failure(42)
                        .then(peek(val::set))
        );
        assertEquals(0, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekFailure_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(peekFailure(err::set))
        );
        assertEquals(0, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekFailure_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.<Integer, Integer>failure(42)
                        .then(peekFailure(err::set))
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }
}
