package at.base10.result.interop;

import at.base10.result.Result;
import at.base10.result.TestHelpers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ResultListTest {

    @Test
    void testNone() {
        assertInstanceOf(None.class, new None());
    }

    @Nested
    class TraverseApplicative {
        @Test
        void test_allSuccess_traverseApplicative() {

            List<String> list = List.of(
                    "1",
                    "2",
                    "3",
                    "4"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), success(List.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_singleSuccess_traverseApplicative() {

            List<String> list = List.of(
                    "1"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), success(List.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_traverseApplicative() {

            List<String> list = List.of(
                    "X"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), failure(List.of(
                    "'X' is not a number"
            )));
        }


        @Test
        void test_MultipleFailure_traverseApplicative() {

            List<String> list = List.of(
                    "X",
                    "2",
                    "3",
                    "Y"
            );
            assertEquals(
                    ResultList
                            .traverseApplicative(TestHelpers::tryParseInt)
                            .apply(list),
                    failure(List.of(
                            "'X' is not a number",
                            "'Y' is not a number"
                    )));
        }

        @Test
        void test_OnlyFirstSuccess_traverseApplicative() {

            List<String> list = List.of(
                    "1",
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), failure(List.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_AllFailure_traverseApplicative() {

            List<String> list = List.of(
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), failure(List.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_SingleFailure_traverseApplicative() {


            List<String> list = List.of(
                    "X"
            );
            assertEquals(ResultList.traverseApplicative(TestHelpers::tryParseInt).apply(list), failure(List.of(
                    "'X' is not a number"
            )));
        }

    }

    @Nested
    class TraverseMonadic {

        @Test
        void test_allSuccess_traverseMonadic() {

            List<String> list = List.of(
                    "1",
                    "2",
                    "3",
                    "4"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), success(List.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_empty_traverseMonadic() {

            List<String> list = List.of();
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), success(List.of()));
        }

        @Test
        void test_singleSuccess_traverseMonadic() {

            List<String> list = List.of(
                    "1"
            );

            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), success(List.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_traverseMonadic() {
            List<String> list = List.of(
                    "1",
                    "X",
                    "3",
                    "4"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_MultipleFailure_traverseMonadic() {

            List<String> list = List.of(
                    "X",
                    "2",
                    "3",
                    "X"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_OnlyFirstSuccess_traverseMonadic() {

            List<String> list = List.of(
                    "1",
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_AllFailure_traverseMonadic() {

            List<String> list = List.of(
                    "X",
                    "Y",
                    "Z"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailure_traverseMonadic() {

            List<String> list = List.of(
                    "X"
            );
            assertEquals(ResultList.traverseMonadic(TestHelpers::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

    }

    @Nested
    class SequenceApplicative {

        @Test
        void test_empty_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of();
            assertEquals(ResultList.sequenceApplicative(list), success(List.of()));
        }


        @Test
        void test_allSuccess_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    success(2),
                    success(3),
                    success(4)
            );
            assertEquals(ResultList.sequenceApplicative(list), success(List.of(
                    1,
                    2,
                    3,
                    4
            )));
        }


        @Test
        void test_singleSuccess_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    success(1)
            );
            assertEquals(ResultList.sequenceApplicative(list), success(List.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    failure("'X' is not a number"),
                    success(3),
                    success(4)
            );
            assertEquals(ResultList.sequenceApplicative(list), failure(List.of(
                    "'X' is not a number"
            )));
        }

        @Test
        void test_MultipleFailure_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number"),
                    success(2),
                    success(3),
                    failure("'y' is not a number")
            );
            assertEquals(ResultList.sequenceApplicative(list), failure(List.of(
                    "'X' is not a number",
                    "'y' is not a number"
            )));
        }

        @Test
        void test_OnlyFirstSuccess_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultList.sequenceApplicative(list), failure(List.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_AllFailure_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultList.sequenceApplicative(list), failure(List.of(
                    "'X' is not a number",
                    "'Y' is not a number",
                    "'Z' is not a number"
            )));
        }

        @Test
        void test_SingleFailure_sequenceApplicative() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number")
            );
            assertEquals(ResultList.sequenceApplicative(list), failure(List.of(
                    "'X' is not a number"
            )));
        }

    }

    @Nested
    class SequenceMonadic {
        @Test
        void test_empty_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of();
            assertEquals(ResultList.sequenceMonadic(list), success(List.of()));
        }


        @Test
        void test_allSuccess_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    success(2),
                    success(3),
                    success(4)
            );
            assertEquals(ResultList.sequenceMonadic(list), success(List.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_singleSuccess_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    success(1)
            );
            assertEquals(ResultList.sequenceMonadic(list), success(List.of(
                    1
            )));
        }


        @Test
        void test_oneFailure_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    failure("'X' is not a number"),
                    success(3),
                    success(4)
            );
            assertEquals(ResultList.sequenceMonadic(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_OnlyFirstSuccess_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    success(1),
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultList.sequenceMonadic(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_AllFailure_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number"),
                    failure("'Y' is not a number"),
                    failure("'Z' is not a number")
            );
            assertEquals(ResultList.sequenceMonadic(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailure_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number")
            );
            assertEquals(ResultList.sequenceMonadic(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_MultipleFailure_sequenceMonadic() {

            List<Result<Integer, String>> list = List.of(
                    failure("'X' is not a number"),
                    success(2),
                    success(3),
                    failure("'y' is not a number")
            );
            assertEquals(ResultList.sequenceMonadic(list), failure(
                    "'X' is not a number"
            ));
        }

    }


}
