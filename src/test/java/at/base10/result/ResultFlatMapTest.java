package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Nested
public class ResultFlatMapTest {
    @Test
    void test_flatMap() {
        var result = Result.<Integer, Integer>success(42).then(flatMap(x -> success(x + "1")));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_flatMap_if_Failure() {
        var result = failure(42).then(flatMap(x -> success(x + "1")));
        assertFailureEquals(42, result);
    }

    @Test
    void test_flatMap_either() {

        assertSuccessEquals(43, Result.<Integer, Integer>success(42).then(
                flatMapEither(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
        assertFailureEquals(41, Result.<Integer, Integer>failure(42).then(
                flatMapEither(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
    }

    @Test
    void test_flatMapFailure() {
        Result<String, Void> result = Result.<String, Integer>failure(42).then(flatMapFailure(x -> success(x + "1", Void.class)));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_flatMapFailure2() {
        var result = Result.<String, Integer>failure(42)
                .then(flatMapFailure(x -> success(x + "1")))
                .then(flatMap(x -> success(x + "1")))
                .then(flatMap(x -> success(x + "1")));

        assertSuccessEquals("42111", result);
    }

    @Test
    void test_flatMapFailure_with_null() {
        //noinspection DataFlowIssue
        assertEquals(
                "mapping is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<String, Integer>failure(42)
                                .then(flatMapFailure(null))
                ).getMessage()
        );
    }

    @Test
    void test_flatMapEither_with_null() {
        //noinspection DataFlowIssue
        assertEquals(
                "mapping is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>failure(42)
                                .then(flatMapEither(null, Result::success))
                ).getMessage()
        );

        //noinspection DataFlowIssue
        assertEquals(
                "mappingFailure is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.<Integer, Integer>failure(42)
                                .then(flatMapEither(Result::success, null))
                ).getMessage()
        );
    }

    @Test
    void test_flatMapFailure3() {

        var result = Result.failure(42, String.class)
                .then(flatMapFailure(x -> failure(x + "1")))
                .then(flatMapFailure(x -> failure(x + "1")))
                .then(flatMap(x -> failure(x.trim() + 1, Void.class)))
                .then(flatMap(x -> success(1)));

        assertFailureEquals("4211", result);
    }

    @Test
    void test_flatMap_with_null() {


        //noinspection DataFlowIssue
        assertEquals(
                "mapping is marked non-null but is null",
                assertThrows(
                        NullPointerException.class,
                        () -> Result.failure(42, String.class)
                                .then(flatMap(null))
                ).getMessage()
        );

        var result = Result.failure(42, String.class)
                .then(flatMapFailure(x -> failure(x + "1")))
                .then(flatMapFailure(x -> failure(x + "1")))
                .then(flatMap(x -> failure(x.trim() + 1, Void.class)))
                .then(flatMap(x -> success(1)));

        assertFailureEquals("4211", result);
    }

    @Test
    void test_flatMapFailure_if_success() {
        var input = Result.<Integer, Integer>success(42);
        var result = input.then(flatMapFailure(x -> success(x + 1)));
        assertSuccessEquals(42, result);
    }
}
