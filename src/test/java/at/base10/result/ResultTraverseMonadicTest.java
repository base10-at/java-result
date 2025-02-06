package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static at.base10.result.Operator.map;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Nested
public class ResultTraverseMonadicTest {

    @Test
    void test_allSuccess_Monadic() {

        List<String> list = List.of(
                "1",
                "2",
                "3",
                "4"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), success(List.of(
                1,
                2,
                3,
                4
        )));
    }

    @Test
    void test_empty_Monadic() {

        List<String> list = List.of();
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), success(List.of()));
    }

    @Test
    void test_singleSuccess_Monadic() {

        List<String> list = List.of(
                "1"
        );

        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), success(List.of(
                1
        )));
    }

    @Test
    void test_oneFailure_Monadic() {
        List<String> list = List.of(
                "1",
                "X",
                "3",
                "4"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_MultipleFailure_Monadic() {

        List<String> list = List.of(
                "X",
                "2",
                "3",
                "X"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_OnlyFirstSuccess_Monadic() {

        List<String> list = List.of(
                "1",
                "X",
                "Y",
                "Z"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_AllFailure_Monadic() {

        List<String> list = List.of(
                "X",
                "Y",
                "Z"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_SingleFailure_Monadic() {

        List<String> list = List.of(
                "X"
        );
        assertEquals(Monadic.traverseList(TestHelpers::tryParseInt).apply(list), failure(
                "'X' is not a number"
        ));
    }

    @Test
    void test_SingleFailureStream_Monadic() {
        Stream<String> stream = Stream.of(
                "X"
        );
        assertEquals(
                Monadic.traverseStream(TestHelpers::tryParseInt).apply(stream),
                failure("'X' is not a number")
        );
    }

    @Test
    void test_EmptyStream_Monadic() {
        Stream<String> stream = Stream.of();
        assertEquals(
                Monadic.traverseStream(TestHelpers::tryParseInt).apply(stream).then(map(Stream::toList)),
                success(List.of())
        );
    }

    @Test
    void test_SingleSuccessStream_Monadic() {
        Stream<String> stream = Stream.of(
                "1"
        );
        assertEquals(
                Monadic.traverseStream(TestHelpers::tryParseInt).apply(stream).then(map(Stream::toList)),
                success(List.of(1))
        );
    }

    @Test
    void test_FailureOptional_Monadic() {
        Optional<String> optional = Optional.of(
                "X"
        );
        assertEquals(
                Monadic.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                failure("'X' is not a number")
        );
    }

    @Test
    void test_SuccessOptional_Monadic() {
        Optional<String> optional = Optional.of(
                "1"
        );
        assertEquals(
                Monadic.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                success(Optional.of(1))
        );
    }

    @Test
    void test_EmptyOptional_Monadic() {
        Optional<String> optional = Optional.empty();
        assertEquals(
                Monadic.traverseOptional(TestHelpers::tryParseInt).apply(optional),
                success(Optional.empty())
        );
    }


}
