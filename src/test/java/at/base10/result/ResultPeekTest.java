package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultPeekTest {
@Nested
class OperatorTest {
    @Test
    void test_peekEither_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(peekEither(val::set, err::set))
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
                        .then(peekEither(val::set, err::set))
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
    void test_ifSuccess_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.success(42, Integer.class)
                        .then(ifSuccess(val::set))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekSuccess_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.failure(42, Integer.class)
                        .then(peek(val::set))
        );
        assertEquals(0, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_ifFailure_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(ifFailure(err::set))
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


    @Test
    void test_orTrow_success() {
        assertEquals(42,
                Result.<Integer, Integer>success(42)
                        .orThrow(f -> new IllegalArgumentException("e"))
        );
    }

    @Test
    void test_orTrow_failure() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Result
                        .failure(42)
                        .orThrow(f -> new IllegalArgumentException("illegal:" + f)),
                "illegal:42"
        );
    }

}
@Nested
class ResultTest {
    @Test
    void test_peekEither_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .peekEither(val::set, err::set)
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
                        .peekEither(val::set, err::set)
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
                        .peek(val::set)
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_ifSuccess_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.success(42, Integer.class)
                        .then(ifSuccess(val::set))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peekSuccess_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.failure(42, Integer.class)
                        .peek(val::set)
        );
        assertEquals(0, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_ifFailure_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .then(ifFailure(err::set))
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
                        .peekFailure(err::set)
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
                        .peekFailure(err::set)
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }


    @Test
    void test_orTrow_success() {
        assertEquals(42,
                Result.<Integer, Integer>success(42)
                        .orThrow(f -> new IllegalArgumentException("e"))
        );
    }

    @Test
    void test_orTrow_failure() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Result
                        .failure(42)
                        .orThrow(f -> new IllegalArgumentException("illegal:" + f)),
                "illegal:42"
        );
    }

}
}
