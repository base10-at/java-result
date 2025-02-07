package at.base10.result;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ResultSequenceMonadicTest {

    @Test
    void test_allSuccess_Monadic() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                success(2),
                success(3),
                success(4)
        );
        assertEquals(Monadic.sequenceList(list), success(List.of(
                1,
                2,
                3,
                4
        )));
    }

    @Test
    void test_empty_Monadic() {

        List<Result<Integer, String>> list = List.of();
        assertEquals(Monadic.sequenceList(list), success(List.of()));
    }

    @Test
    void test_singleSuccess_Monadic() {

        List<Result<Integer, String>> list = List.of(
                success(1)
        );
        assertEquals(Monadic.sequenceList(list), success(List.of(
                1
        )));
    }

    @Test
    void test_oneFailure_Monadic() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                failure("'X' is not a number"),
                success(3),
                success(4)
        );
        assertEquals(Monadic.sequenceList(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_MultipleFailure_Monadic() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number"),
                success(2),
                success(3),
                failure("'y' is not a number")
        );
        assertEquals(Monadic.sequenceList(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_OnlyFirstSuccess_Monadic() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                failure("'X' is not a number"),
                failure("'Y' is not a number"),
                failure("'Z' is not a number")
        );
        assertEquals(Monadic.sequenceList(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_AllFailure_Monadic() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number"),
                failure("'Y' is not a number"),
                failure("'Z' is not a number")
        );
        assertEquals(Monadic.sequenceList(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_SingleFailure_Monadic() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number")
        );
        assertEquals(Monadic.sequenceList(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_SingleFailureStream_Monadic() {
        Stream<Result<Integer, String>> stream = Stream.of(
                failure("'X' is not a number")
        );
        assertEquals(
                Monadic.sequenceStream(stream),
                failure("'X' is not a number")
        );
    }

    @Test
    void test_SingleFailureOptional_Monadic() {
        Optional<Result<Integer, String>> optional = Optional.of(
                failure("'X' is not a number")
        );
        assertEquals(
                Monadic.sequenceOptional(optional),
                failure("'X' is not a number")
        );
    }


    @Test
    void test_SingleSuccessOptional_Monadic() {
        Optional<Result<Integer, String>> optional = Optional.of(
                success(1)
        );
        assertEquals(
                Monadic.sequenceOptional(optional),
                success(Optional.of(1))
        );
    }

}
