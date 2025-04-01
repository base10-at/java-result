package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static at.base10.result.PromiseHelper.delayWithTryCatch;
import static at.base10.result.PromiseHelper.promiseResultInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Nested
public class ResultPromiseTest {
    @Test
    void test_read_and_process_file() {
        var x = Result.<String, String>success("test-files/numbers.txt")
                .then(bind(PromiseHelper::getFile))
                .then(bind(PromiseHelper::getLines))
                .then(map(PromiseHelper::parseInts))
                .then(map(s -> s.mapToInt(Integer::intValue).sum()))
                .then(peekEither(s -> {
                }, System.out::println));
        assertEquals(128, x.then(defaultsTo(-1)));
    }


    @Test
    void test_delay_1() {
        assertSuccessEquals(42,
                Result.<Integer, String>success(21)
                        .then(bind(delayWithTryCatch(2)))
        );
    }

    @Test
    void test_delay_2() {

        assertSuccessEquals(42,
                Result.<Integer, String>success(21)
                        .then(bind(x -> promiseResultInt(2, x, true).join()))
        );
    }

}
