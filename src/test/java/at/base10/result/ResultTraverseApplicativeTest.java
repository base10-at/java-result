package at.base10.result;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static at.base10.result.Operator.mapFailure;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultTraverseApplicativeTest {

    @Test
    void test_allSuccess_Applicative() {

        List<String> list = List.of(
                "1",
                "2",
                "3",
                "4"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), success(List.of(
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

        List<String> list = List.of(
                "1"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), success(List.of(
                1
        )));
    }

    @Test
    void test_oneFailure_Applicative() {

        List<String> list = List.of(
                "X"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), failure(List.of(
                "'X' is not a number"
        )));
    }


    @Test
    void test_MultipleFailure_Applicative() {

        List<String> list = List.of(
                "X",
                "2",
                "3",
                "Y"
        );
        assertEquals(
                Applicative
                        .traverseList(TestHelpers::tryParseInt)
                        .apply(list),
                failure(List.of(
                        "'X' is not a number",
                        "'Y' is not a number"
                )));
    }

    @Test
    void test_OnlyFirstSuccess_Applicative() {

        List<String> list = List.of(
                "1",
                "X",
                "Y",
                "Z"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), failure(List.of(
                "'X' is not a number",
                "'Y' is not a number",
                "'Z' is not a number"
        )));
    }

    @Test
    void test_AllFailure_Applicative() {

        List<String> list = List.of(
                "X",
                "Y",
                "Z"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), failure(List.of(
                "'X' is not a number",
                "'Y' is not a number",
                "'Z' is not a number"
        )));
    }

    @Test
    void test_SingleFailure_Applicative() {


        List<String> list = List.of(
                "X"
        );
        assertEquals(Applicative.traverseList(TestHelpers::tryParseInt).apply(list), failure(List.of(
                "'X' is not a number"
        )));
    }

    @Test
    void test_SingleFailureStream_Applicative() {

        Stream<Result<Integer, String>> stream = Stream.of(
                failure("'X' is not a number")
        );
        assertEquals(Applicative.sequenceStream(stream).then(mapFailure(Stream::toList)), failure(List.of(
                "'X' is not a number"))
        );
    }


    @Test
    void test_FailureOptional_Monadic() {
        Optional<String> optional = Optional.of(
                "X"
        );
        assertEquals(
                Applicative.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                failure(Optional.of("'X' is not a number"))
        );
    }

    @Test
    void test_SuccessOptional_Monadic() {
        Optional<String> optional = Optional.of(
                "1"
        );
        assertEquals(
                Applicative.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                success(Optional.of(1))
        );
    }

    @Test
    void test_EmptyOptional_Monadic() {
        Optional<String> optional = Optional.empty();
        assertEquals(
                Applicative.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                success(Optional.empty())
        );
    }


}
