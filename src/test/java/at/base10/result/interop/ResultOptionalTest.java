package at.base10.result.interop;

import at.base10.result.Result;
import at.base10.result.TestHelpers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultOptionalTest {
    @Nested
    public class SequenceMonadic {
        @Test
        void test_SingleFailureOptional_sequenceMonadic() {
            Optional<Result<Integer, String>> optional = Optional.of(
                    failure("'X' is not a number")
            );
            assertEquals(
                    ResultOptional.sequenceMonadic(optional),
                    failure("'X' is not a number")
            );
        }


        @Test
        void test_SingleSuccessOptional_sequenceMonadic() {
            Optional<Result<Integer, String>> optional = Optional.of(
                    success(1)
            );
            assertEquals(
                    ResultOptional.sequenceMonadic(optional),
                    success(Optional.of(1))
            );
        }
        @Test
        void test_emptyOptional_sequenceMonadic() {
            Optional<Result<Integer, String>> optional = Optional.empty();

            assertEquals(
                    ResultOptional.sequenceMonadic(optional),
                    success(Optional.empty())
            );
        }
    }

    @Nested
    public class SequenceApplicative{
        @Test
        void test_SingleFailureOptional_sequenceApplicative() {
            Optional<Result<Integer, String>> optional = Optional.of(
                    failure("'X' is not a number")
            );
            assertEquals(
                    ResultOptional.sequenceApplicative(optional),
                    failure(Optional.of("'X' is not a number"))
            );
        }

        @Test
        void test_SingleSuccessOptional_sequenceApplicative() {
            Optional<Result<Integer, String>> optional = Optional.of(
                    success(1)
            );
            assertEquals(
                    ResultOptional.sequenceApplicative(optional),
                    success(Optional.of(1))
            );
        }
        @Test
        void test_EmptyOptional_sequenceApplicative() {
            Optional<Result<Integer, String>> optional = Optional.empty();
            assertEquals(
                    ResultOptional.sequenceApplicative(optional),
                    success(Optional.empty())
            );
        }
    }

    @Nested
    public class TraverseMonadic {
        @Test
        void test_FailureOptional_traverseMonadic() {
            Optional<String> optional = Optional.of(
                    "X"
            );
            assertEquals(
                    ResultOptional.traverseMonadic(TestHelpers::tryParseInt).apply(optional),
                    failure("'X' is not a number")
            );
        }

        @Test
        void test_SuccessOptional_traverseMonadic() {
            Optional<String> optional = Optional.of(
                    "1"
            );
            assertEquals(
                    ResultOptional.traverseMonadic(TestHelpers::tryParseInt).apply(optional),
                    success(Optional.of(1))
            );
        }

        @Test
        void test_EmptyOptional_traverseMonadic() {
            Optional<String> optional = Optional.empty();
            assertEquals(
                    ResultOptional.traverseMonadic(TestHelpers::tryParseInt).apply(optional),
                    success(Optional.empty())
            );
        }

    }

    @Nested
    public class TraverseApplicative {
        @Test
        void test_FailureOptional_traverseApplicative() {
            Optional<String> optional = Optional.of(
                    "X"
            );
            assertEquals(
                    ResultOptional.traverseApplicative(TestHelpers::tryParseInt).apply(optional),
                    failure(Optional.of("'X' is not a number"))
            );
        }

        @Test
        void test_SuccessOptional_traverseApplicative() {
            Optional<String> optional = Optional.of(
                    "1"
            );
            assertEquals(
                    ResultOptional.traverseApplicative(TestHelpers::tryParseInt).apply(optional),
                    success(Optional.of(1))
            );
        }

        @Test
        void test_EmptyOptional_traverseApplicative() {
            Optional<String> optional = Optional.empty();
            assertEquals(
                    ResultOptional.traverseApplicative(TestHelpers::tryParseInt).apply(optional),
                    success(Optional.empty())
            );
        }


    }




}
