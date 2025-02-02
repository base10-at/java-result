package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static at.base10.result.Result.Fn.allMatch;
import static at.base10.result.Result.Fn.anyMatch;
import static at.base10.result.Result.Fn.bind;
import static at.base10.result.Result.Fn.bindError;
import static at.base10.result.Result.Fn.count;
import static at.base10.result.Result.Fn.defaultsTo;
import static at.base10.result.Result.Fn.flip;
import static at.base10.result.Result.Fn.map;
import static at.base10.result.Result.Fn.mapError;
import static at.base10.result.Result.Fn.peek;
import static at.base10.result.Result.Fn.toList;
import static at.base10.result.Result.Fn.toOptional;
import static at.base10.result.Result.Fn.toStream;
import static at.base10.result.Result.error;
import static at.base10.result.Result.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    private static <V, E> void assertResultIsOkAndEquals(V value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Result.Ok.class, result);
        assertEquals(value, result.getValue());
        assertNull(result.getError());
    }

    private static <V, E> void assertResultIsErrorAndEquals(E value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Result.Error.class, result);
        assertEquals(value, result.getError());
        assertNull(result.getValue());
    }


    @Test
    void test_ok() {
        assertResultIsOkAndEquals(42, ok(42));
    }

    @Test
    void test_error() {
        assertResultIsErrorAndEquals(42, error(42));
    }

    @Test
    void test_isOk() {
        assertTrue(ok(42).isOk());
        assertFalse(ok(42).isError());
    }

    @Test
    void test_isError() {
        assertTrue(Result.error(42).isError());
        assertFalse(Result.error(42).isOk());
    }

    @Test
    void test_defaultsTo() {
        assertResultIsOkAndEquals(42, ok(42));
    }

    @Test
    void test_flip_defaultsTo() {
        assertResultIsOkAndEquals(42, error(42).then(flip()));
    }


    @Test
    void test_map() {
        assertResultIsOkAndEquals("421",
                ok(42)
                        .then(map(x -> x + "1"))
        );
    }

    @Test
    void test_map_if_error() {
        assertResultIsErrorAndEquals(42, error(42)
                .then(map(x -> x + "1"))
        );
    }


    @Test
    void test_mapError() {
        assertResultIsErrorAndEquals("421",
                error(42)
                        .then(mapError(x -> x + "1"))

        );
    }


    @Test
    void test_mapError_if_ok() {
        var result = ok(42).then(mapError(x -> x + "1"));
        assertResultIsOkAndEquals(42, result);
    }


    @Test
    void test_bind() {
        var result = Result.<Integer, Integer>ok(42).then(bind(x -> ok(x + "1")));
        assertResultIsOkAndEquals("421", result);
    }

    @Test
    void test_bind_if_error() {
        var result = error(42).then(bind(x -> ok(x + "1")));
        assertResultIsErrorAndEquals(42, result);
    }

    @Test
    void test_bindError() {
        var result = Result.<String, Integer>error(42).then(bindError(x -> ok(x + "1")));
        assertResultIsOkAndEquals("421", result);
    }

    @Test
    void test_bindError_if_ok() {
        var input = Result.<Integer, Integer>ok(42);
        var result = input.then(bindError(x -> ok(x + 1)));
        assertResultIsOkAndEquals(42, result);
    }

    @Test
    void test_anyMatch() {
        assertFalse(Result.<Integer, Integer>error(42).then(anyMatch(x -> x == 42)));
        assertFalse(Result.<Integer, Integer>error(42).then(anyMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>ok(42).then(anyMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>ok(42).then(anyMatch(x -> x == 42)));
    }

    @Test
    void test_allMatch() {
        assertTrue(Result.<Integer, Integer>error(42).then(allMatch(x -> x == 42)));
        assertTrue(Result.<Integer, Integer>error(42).then(allMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>ok(42).then(allMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>ok(42).then(allMatch(x -> x == 42)));
    }

    @Test
    void test_toList() {
        assertEquals(List.of(), Result.<Integer, Integer>error(42).then(toList()));
        assertEquals(List.of(42), Result.<Integer, Integer>ok(42).then(toList()));
    }

    @Test
    void test_toStream() {
        assertEquals(List.of(), Result.<Integer, Integer>error(42).then(toStream()).toList());
        assertEquals(List.of(42), Result.<Integer, Integer>ok(42).then(toStream()).toList());
    }

    @Test
    void test_toOptional() {
        assertEquals(Optional.empty(), Result.<Integer, Integer>error(42).then(toOptional()));
        assertEquals(Optional.of(42), Result.<Integer, Integer>ok(42).then(toOptional()));
    }

    @Test
    void test_defaultsToValue() {
        assertEquals(43, Result.<Integer, Integer>error(42).then(defaultsTo(43)));
        assertEquals(42, Result.<Integer, Integer>ok(42).then(defaultsTo(43)));
    }


    @Test
    void test_defaultsToSupplier() {
        assertEquals(43, Result.<Integer, Integer>error(42).then(defaultsTo(() -> 43)));
        assertEquals(42, Result.<Integer, Integer>ok(42).then(defaultsTo(() -> 43)));
    }

    @Test
    void test_count() {
        assertEquals(0, Result.<Integer, Integer>error(42).then(count()));
        assertEquals(1, Result.<Integer, Integer>ok(42).then(count()));
    }

    @Test
    void test_peek_ok() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertResultIsOkAndEquals(42,
                Result.<Integer, Integer>ok(42)
                        .then(peek(
                                val::set,
                                err::set
                        ))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peek_error() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertResultIsErrorAndEquals(42,
                Result.<Integer, Integer>error(42)
                        .then(peek(
                                val::set,
                                err::set
                        ))
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }

    @Nested
    public class ResultPromiseTest {

        Function<Integer, Result<Integer, String>> delayWithTryCatch(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                return x -> error("FAIL");
            }
            return x -> ok(x * 2);
        }

        CompletableFuture<Result<Integer, String>> promise(int ms, int x) {
            var p = new CompletableFuture<Result<Integer, String>>();


            new Thread(() -> {
                try {
                    Thread.sleep(ms);
                    p.complete(ok(x * 2));
                } catch (InterruptedException e) {
                    p.complete(error("FAIL"));
                }
            }).start();

            return p;
        }


        @Test
        void test_delay_1() {
            assertResultIsOkAndEquals(42,
                    new Result.Ok<Integer, String>(21)
                            .then(bind(delayWithTryCatch(32)))
            );
        }
        @Test
        void test_delay_2() {

            assertResultIsOkAndEquals(42,
                    new Result.Ok<Integer, String>(21)
                            .then(bind(x ->promise(100,x).join()))
            );
        }

    }
}