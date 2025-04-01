package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

@Nested
public class ResultBindTest {
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
                bindEither(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
        assertFailureEquals(41, Result.<Integer, Integer>failure(42).then(
                bindEither(
                        x -> success(x + 1),
                        x -> failure(x - 1)
                )));
    }

    @Test
    void test_bindFailure() {
        Result<String, Void> result = Result.<String, Integer>failure(42).then(bindFailure(x -> success(x + "1", Void.class)));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_bindFailure2() {
        var result = Result.<String, Integer>failure(42)
                .then(bindFailure(x -> success(x + "1")))
                .then(bind(x -> success(x + "1")))
                .then(bind(x -> success(x + "1")));

        assertSuccessEquals("42111", result);
    }

    @Test
    void test_bindFailure3() {

        var result = Result.failure(42, String.class)
                .then(bindFailure(x -> failure(x + "1")))
                .then(bindFailure(x -> failure(x + "1")))
                .then(bind(x -> failure(x.trim() + 1, Void.class)))
                .then(bind(x -> success(1)));

        assertFailureEquals("4211", result);
    }

    @Test
    void test_bindFailure_if_success() {
        var input = Result.<Integer, Integer>success(42);
        var result = input.then(bindFailure(x -> success(x + 1)));
        assertSuccessEquals(42, result);
    }
}
