package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static at.base10.result.Operator.mapFailure;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Nested
public class ResultSequenceApplicativeTest {

    @Test
    void test_allSuccess_Applicative() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                success(2),
                success(3),
                success(4)
        );
        assertEquals(Applicative.sequenceList(list), success(List.of(
                1,
                2,
                3,
                4
        )));
    }

    @Test
    void test_empty_Applicative() {

        List<Result<Integer, String>> list = List.of();
        assertEquals(Applicative.sequenceList(list), success(List.of()));
    }

    @Test
    void test_singleSuccess_Applicative() {

        List<Result<Integer, String>> list = List.of(
                success(1)
        );
        assertEquals(Applicative.sequenceList(list), success(List.of(
                1
        )));
    }

    @Test
    void test_oneFailure_Applicative() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                failure("'X' is not a number"),
                success(3),
                success(4)
        );
        assertEquals(Applicative.sequenceList(list), failure(List.of(
                "'X' is not a number"
        )));
    }

    @Test
    void test_MultipleFailure_Applicative() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number"),
                success(2),
                success(3),
                failure("'y' is not a number")
        );
        assertEquals(Applicative.sequenceList(list), failure(List.of(
                "'X' is not a number",
                "'y' is not a number"
        )));
    }

    @Test
    void test_OnlyFirstSuccess_Applicative() {

        List<Result<Integer, String>> list = List.of(
                success(1),
                failure("'X' is not a number"),
                failure("'Y' is not a number"),
                failure("'Z' is not a number")
        );
        assertEquals(Applicative.sequenceList(list), failure(List.of(
                "'X' is not a number",
                "'Y' is not a number",
                "'Z' is not a number"
        )));
    }

    @Test
    void test_AllFailure_Applicative() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number"),
                failure("'Y' is not a number"),
                failure("'Z' is not a number")
        );
        assertEquals(Applicative.sequenceList(list), failure(List.of(
                "'X' is not a number",
                "'Y' is not a number",
                "'Z' is not a number"
        )));
    }

    @Test
    void test_SingleFailure_Applicative() {

        List<Result<Integer, String>> list = List.of(
                failure("'X' is not a number")
        );
        assertEquals(Applicative.sequenceList(list), failure(List.of(
                "'X' is not a number"
        )));
    }

    @Test
    void test_SingleFailureStream_Applicative() {

        Stream<Result<Integer, String>> stream = Stream.of(
                failure("'X' is not a number")
        );
        assertEquals(
                Applicative.sequenceStream(stream).then(mapFailure(Stream::toList)),
                failure(List.of("'X' is not a number"))
        );
    }

    @Test
    void test_SingleFailureOptional_Applicative() {
        Optional<Result<Integer, String>> optional = Optional.of(
                failure("'X' is not a number")
        );
        assertEquals(
                Applicative.sequenceOptional(optional),
                failure(Optional.of("'X' is not a number"))
        );
    }

    @Test
    void test_SingleSuccessOptional_Monadic() {
        Optional<Result<Integer, String>> optional = Optional.of(
                success(1)
        );
        assertEquals(
                Applicative.sequenceOptional(optional),
                success(Optional.of(1))
        );
    }

}
