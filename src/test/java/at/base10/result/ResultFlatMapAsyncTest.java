package at.base10.result;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ResultFlatMapAsyncTest {


    @Test
    void test_flatMapAsync_to_success_if_Success() {

        assertSuccessEquals(42,
                Result.<Integer, String>success(22)
                        .then(flatMapAsync(x -> PromiseHelper.promiseResultInt(2, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_flatMapAsync_to_success_if_Failure() {

        assertFailureEquals("INIT",
                Result.<Integer, String>failure("INIT")
                        .then(flatMapAsync(x -> PromiseHelper.promiseResultInt(100, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_flatMapAsync_to_Failure_if_Success() {

        assertFailureEquals("Failure",
                Result.<Integer, String>success(22)
                        .then(flatMapAsync(x -> PromiseHelper.promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_flatMapAsync_to_Failure_if_Failure() {

        assertFailureEquals("INIT",
                Result.<Integer, String>failure("INIT")
                        .then(flatMapAsync(x -> PromiseHelper.promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_flatMapFailureAsync_to_success_if_Success() {

        assertSuccessEquals(20,
                Result.<Integer, String>success(22)
                        .then(flatMapFailureAsync(x -> PromiseHelper.promiseResultString(100, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @SuppressWarnings("ConstantValue")
    @Test
    void test_flatMapFailureAsync_to_success_if_Failure() {

        Optional.of(4).flatMap(x -> x % 2 == 0 ? Optional.of(x) : Optional.empty());

        assertEquals(
                Optional.of(4),
                Optional.of(4)
                        .flatMap(x -> x % 2 == 0
                                ? Optional.of(x)
                                : Optional.empty()
                        ));

        assertEquals(
                Optional.empty(),
                Optional.of(3)
                        .flatMap(x -> x % 2 == 0
                                ? Optional.of(x)
                                : Optional.empty()
                        ));


        assertSuccessEquals(42,
                Result.<Integer, Integer>failure(22)
                        .then(flatMapFailureAsync(x -> PromiseHelper.promiseResultInt(2, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_flatMapFailureAsync_to_Failure_if_Success() {

        assertSuccessEquals(20,
                Result.<Integer, String>success(22)
                        .then(flatMapFailureAsync(x -> PromiseHelper.promiseResultString(100, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }


    @Test
    void test_flatMapFailureAsync_to_Failure_if_Failure() {

        assertFailureEquals("Failure",
                Result.<Integer, Integer>failure(22)
                        .then(flatMapFailureAsync(x -> PromiseHelper.promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

}
