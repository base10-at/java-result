package at.base10.result.interop;

import at.base10.result.Result;
import at.base10.result.TestHelpers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static at.base10.result.Assert.assertEqualStreamFailure;
import static at.base10.result.Assert.assertEqualStreamSuccess;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultStreamTest {



    @Nested
    class TraverseApplicative {
        @Test
        void test_allSuccess_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "1",
                    "2",
                    "3",
                    "4"
            );
            assertEqualStreamSuccess(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), success(Stream.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_singleSuccess_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "1"
            );
            assertEqualStreamSuccess(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), success(Stream.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "X"
            );
            assertEqualStreamFailure(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), failure(Stream.of(
                    "'X' is not a number"
            )));
        }


        @Test
        void test_MultipleFailure_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "X",
                    "2",
                    "3",
                    "Y"
            );
            assertEqualStreamFailure(
                    ResultStream
                            .traverseApplicative(TestHelpers::tryParseInt)
                            .apply(stream),
                    failure(Stream.of(
                            "'X' is not a number",
                            "'Y' is not a number"
                    )));
        }

        @Test
        void test_OnlyFirstSuccess_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "1",
                    "X",
                    "Y",
                    "Z"
            );
            assertEqualStreamFailure(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), failure(Stream.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_AllFailure_traverseApplicative() {

            Stream<String> stream = Stream.of(
                    "X",
                    "Y",
                    "Z"
            );
            assertEqualStreamFailure(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), failure(Stream.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_SingleFailure_traverseApplicative() {


            Stream<String> stream = Stream.of(
                    "X"
            );
            assertEqualStreamFailure(ResultStream.traverseApplicative(TestHelpers::tryParseInt).apply(stream), failure(Stream.of(
                    "'X' is not a number"
            )));
        }

    }

    @Nested
    class TraverseMonadic {

        @Test
        void test_allSuccess_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "1",
                    "2",
                    "3",
                    "4"
            );
            assertEqualStreamSuccess(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), success(Stream.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_empty_traverseMonadic() {

            Stream<String> stream = Stream.of();
            assertEqualStreamSuccess(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), success(Stream.of()));
        }

        @Test
        void test_singleSuccess_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "1"
            );

            assertEqualStreamSuccess(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), success(Stream.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_traverseMonadic() {
            Stream<String> stream = Stream.of(
                    "1",
                    "X",
                    "3",
                    "4"
            );
            assertEquals(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_MultipleFailure_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "X",
                    "2",
                    "3",
                    "X"
            );
            assertEquals(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_OnlyFirstSuccess_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "1",
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_AllFailure_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailure_traverseMonadic() {

            Stream<String> stream = Stream.of(
                    "X"
            );
            assertEquals(ResultStream.traverseMonadic(TestHelpers::tryParseInt).apply(stream), failure(
                    "'X' is not a number"
            ));
        }

    }

    @Nested
    class SequenceApplicative {

        @Test
        void test_empty_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of();
            assertEqualStreamSuccess(ResultStream.sequenceApplicative(stream), success(Stream.of()));
        }


        @Test
        void test_allSuccess_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    success(2),
                    success(3),
                    success(4)
            );
            assertEqualStreamSuccess(ResultStream.sequenceApplicative(stream), success(Stream.of(
                    1,
                    2,
                    3,
                    4
            )));
        }


        @Test
        void test_singleSuccess_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1)
            );
            assertEqualStreamSuccess(ResultStream.sequenceApplicative(stream), success(Stream.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    failure("'X' is not a number"),
                    success(3),
                    success(4)
            );
            assertEqualStreamFailure(ResultStream.sequenceApplicative(stream), failure(Stream.of(
                    "'X' is not a number"
            )));
        }

        @Test
        void test_MultipleFailure_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number"),
                    success(2),
                    success(3),
                    failure("'y' is not a number")
            );
            assertEqualStreamFailure(ResultStream.sequenceApplicative(stream), failure(Stream.of(
                    "'X' is not a number",
                    "'y' is not a number"
            )));
        }

        @Test
        void test_OnlyFirstSuccess_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEqualStreamFailure(ResultStream.sequenceApplicative(stream), failure(Stream.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }



        @Test
        void test_AllFailure_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );

            assertEqualStreamFailure(ResultStream.sequenceApplicative(stream), failure(Stream.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_SingleFailure_sequenceApplicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number")
            );
            assertEqualStreamFailure(ResultStream.sequenceApplicative(stream), failure(Stream.of(
                    "'X' is not a number"
            )));
        }

    }

    @Nested
    class SequenceMonadic {
        @Test
        void test_empty_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of();
            assertEqualStreamSuccess(ResultStream.sequenceMonadic(stream), success(Stream.of()));
        }


        @Test
        void test_allSuccess_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    success(2),
                    success(3),
                    success(4)
            );
            assertEqualStreamSuccess(ResultStream.sequenceMonadic(stream), success(Stream.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_singleSuccess_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1)
            );
            assertEqualStreamSuccess(ResultStream.sequenceMonadic(stream), success(Stream.of(
                    1
            )));
        }


        @Test
        void test_oneFailure_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    failure("'X' is not a number"),
                    success(3),
                    success(4)
            );
            assertEquals(ResultStream.sequenceMonadic(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_OnlyFirstSuccess_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    success(1),
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultStream.sequenceMonadic(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_AllFailure_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultStream.sequenceMonadic(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailure_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number")
            );
            assertEquals(ResultStream.sequenceMonadic(stream), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_MultipleFailure_sequenceMonadic() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number"),
                    success(2),
                    success(3),
                    failure("'y' is not a number")
            );
            assertEquals(ResultStream.sequenceMonadic(stream), failure(
                    "'X' is not a number"
            ));
        }

    }


}
