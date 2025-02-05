package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Result.Failure;

import static at.base10.result.Operator.allMatch;
import static at.base10.result.Operator.anyMatch;
import static at.base10.result.Operator.bind;
import static at.base10.result.Operator.bindAsync;
import static at.base10.result.Operator.bindFailure;
import static at.base10.result.Operator.bindFailureAsync;
import static at.base10.result.Operator.count;
import static at.base10.result.Operator.defaultsTo;
import static at.base10.result.Operator.flip;
import static at.base10.result.Operator.isFailure;
import static at.base10.result.Operator.isSuccess;
import static at.base10.result.Operator.map;
import static at.base10.result.Operator.mapFailure;
import static at.base10.result.Operator.peek;
import static at.base10.result.Operator.then;
import static at.base10.result.Operator.toList;
import static at.base10.result.Operator.toOptional;
import static at.base10.result.Operator.toStream;
import static at.base10.result.Result.Success;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    private static <V, E> void assertSuccessEquals(V value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Success.class, result);
        assertEquals(value, result.getValue());
        assertThrows(NoSuchElementException.class, result::getFailure, "No value present");
    }

    private static <V, E> void assertFailureEquals(E value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Failure.class, result);
        assertInstanceOf(Failure.class, result);
        assertEquals(value, result.getFailure());
        assertThrows(NoSuchElementException.class, result::getValue, "No value present");
    }


    @Test
    void test_success() {
        assertSuccessEquals(42, success(42));
    }

    @Test
    void test_Failure() {
        assertFailureEquals(42, failure(42));
    }

    @Test
    void test_isSuccess() {
        assertTrue(success(42).isSuccess());
        assertFalse(success(42).isFailure());
    }

    @Test
    void test_isSuccessFN() {
        assertTrue(success(42).thenApply(isSuccess()));
        assertFalse(success(42).thenApply(isFailure()));
    }

    @Test
    void test_isFailure() {
        assertTrue(failure(42).isFailure());
        assertFalse(failure(42).isSuccess());
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes", "ConstantValue"})
    @Test
    void test_equals_failure() {
        // this setup is required to hit all branches in equals
        assertFalse(failure(42).equals(List.of()));
        assertFalse(failure(42).equals(null));
        assertFalse(failure(42).equals(new Object()));
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes", "ConstantValue"})
    @Test
    void test_equals_success() {
        // this setup is required to hit all branches in equals
        assertFalse(success(42).equals(List.of()));
        assertFalse(success(42).equals(null));
        assertFalse(success(42).equals(new Object()));
    }

    @Test
    void test_toString() {
        assertEquals("Success{value=" + 42 + '}', success(42).toString());
        assertEquals("Failure{failure=" + 42 + '}', failure(42).toString());
    }

    @Test
    void test_isFailureFN() {
        assertTrue(failure(42).thenApply(isFailure()));
        assertFalse(failure(42).thenApply(isSuccess()));
    }

    @Test
    void test_defaultsTo() {
        assertSuccessEquals(42, success(42));
    }

    @Test
    void test_flip_defaultsTo() {
        assertSuccessEquals(42, failure(42).thenApply(flip()));
    }


    @Test
    void test_map() {
        assertSuccessEquals("421",
                success(42)
                        .thenApply(map(x -> x + "1"))
        );
    }

    @Test
    void test_map_if_Failure() {
        assertFailureEquals(42, failure(42)
                .thenApply(map(x -> x + "1"))
        );
    }


    @Test
    void test_mapFailure() {
        assertFailureEquals("421",
                failure(42)
                        .thenApply(mapFailure(x -> x + "1"))

        );
    }


    @Test
    void test_mapFailure_if_success() {
        var result = success(42).thenApply(mapFailure(x -> x + "1"));
        assertSuccessEquals(42, result);
    }


    @Test
    void test_bind() {
        var result = Result.<Integer, Integer>success(42).thenApply(bind(x -> success(x + "1")));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_bind_if_Failure() {
        var result = failure(42).thenApply(bind(x -> success(x + "1")));
        assertFailureEquals(42, result);
    }

    @Test
    void test_bindFailure() {
        var result = Result.<String, Integer>failure(42).thenApply(bindFailure(x -> success(x + "1")));
        assertSuccessEquals("421", result);
    }

    @Test
    void test_bindFailure_if_success() {
        var input = Result.<Integer, Integer>success(42);
        var result = input.thenApply(bindFailure(x -> success(x + 1)));
        assertSuccessEquals(42, result);
    }

    @Test
    void test_anyMatch() {
        assertFalse(Result.<Integer, Integer>failure(42).thenApply(anyMatch(x -> x == 42)));
        assertFalse(Result.<Integer, Integer>failure(42).thenApply(anyMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).thenApply(anyMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).thenApply(anyMatch(x -> x == 42)));
    }

    @Test
    void test_allMatch() {
        assertTrue(Result.<Integer, Integer>failure(42).thenApply(allMatch(x -> x == 42)));
        assertTrue(Result.<Integer, Integer>failure(42).thenApply(allMatch(x -> x == 43)));
        assertFalse(Result.<Integer, Integer>success(42).thenApply(allMatch(x -> x == 43)));
        assertTrue(Result.<Integer, Integer>success(42).thenApply(allMatch(x -> x == 42)));
    }

    @Test
    void test_toList() {
        assertEquals(List.of(), Result.<Integer, Integer>failure(42).thenApply(toList()));
        assertEquals(List.of(42), Result.<Integer, Integer>success(42).thenApply(toList()));
    }

    @Test
    void test_toStream() {
        assertEquals(List.of(), Result.<Integer, Integer>failure(42).thenApply(toStream()).toList());
        assertEquals(List.of(42), Result.<Integer, Integer>success(42).thenApply(toStream()).toList());
    }

    @Test
    void test_toOptional() {
        assertEquals(Optional.empty(), Result.<Integer, Integer>failure(42).thenApply(toOptional()));
        assertEquals(Optional.of(42), Result.<Integer, Integer>success(42).thenApply(toOptional()));
    }

    @Test
    void test_defaultsToValue() {
        assertEquals(43, Result.<Integer, Integer>failure(42).thenApply(defaultsTo(43)));
        assertEquals(42, Result.<Integer, Integer>success(42).thenApply(defaultsTo(43)));
    }


    @Test
    void test_defaultsToSupplier() {
        assertEquals(43, Result.<Integer, Integer>failure(42).thenApply(defaultsTo(() -> 43)));
        assertEquals(42, Result.<Integer, Integer>success(42).thenApply(defaultsTo(() -> 43)));
    }

    @Test
    void test_count() {
        assertEquals(0, Result.<Integer, Integer>failure(42).thenApply(count()));
        assertEquals(1, Result.<Integer, Integer>success(42).thenApply(count()));
    }

    @Test
    void test_peek_success() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertSuccessEquals(42,
                Result.<Integer, Integer>success(42)
                        .thenApply(peek(
                                val::set,
                                err::set
                        ))
        );
        assertEquals(42, val.get());
        assertEquals(0, err.get());
    }

    @Test
    void test_peek_Failure() {
        AtomicInteger val = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        assertFailureEquals(42,
                Result.<Integer, Integer>failure(42)
                        .thenApply(then(peek(
                                val::set,
                                err::set
                        )))
        );
        assertEquals(0, val.get());
        assertEquals(42, err.get());
    }

    @Test
    void test_hash() {

        assertNotEquals(success(42).hashCode(), failure(42).hashCode());
    }

    public static Result<Integer, String> tryParseInt(String number) {
        try {
            return success(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            return failure("'" + number + "' is not a number");
        }
    }

    @Nested
    public class ResultPromiseTest {

        Function<Integer, Result<Integer, String>> delayWithTryCatch(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                return x -> failure("FAIL");
            }
            return x -> success(x * 2);
        }

        CompletableFuture<Result<Integer, String>> promiseResultInt(int ms, int x, boolean isSuccess) {
            var p = new CompletableFuture<Result<Integer, String>>();
            CompletableFuture.runAsync(() -> {
                if (isSuccess) {
                    try {
                        Thread.sleep(ms);
                        p.complete(success(x * 2));
                    } catch (InterruptedException e) {
                        p.complete(failure("FAIL"));
                    }
                } else {
                    p.complete(failure("Failure"));
                }
            });
            return p;
        }

        CompletableFuture<Result<Integer, String>> promiseResultString(int ms, String x, boolean isSuccess) {
            var p = new CompletableFuture<Result<Integer, String>>();
            CompletableFuture.runAsync(() -> {
                if (isSuccess) {
                    try {
                        Thread.sleep(ms);
                        p.complete(success(33));
                    } catch (InterruptedException e) {
                        p.complete(failure("FAIL"));
                    }
                } else {
                    p.complete(failure("Failure"));
                }
            });
            return p;
        }


        Stream<Integer> parseInts(Stream<String> numbers) {
            return numbers
                    .map(ResultTest::tryParseInt)
                    .filter(Result::isSuccess)
                    .map(defaultsTo(0));
        }

        private Result<File, String> getFile(String path) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();

                URL url = classLoader.getResource(path);
                if (url == null) {
                    return failure("File not found");
                }
                File file = new File(url.getFile());

                return success(file);
            } catch (NullPointerException e) {
                return failure("No path provided");
            }
        }

        private Result<Stream<String>, String> getFileLines(File file) {
            try (Stream<String> stream = Files.lines(file.toPath())) {
                return success(stream.toList().stream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                return failure("No file provided");
            }
        }


        @Test
        void test_read_and_process_file() {
            var x = Result.<String, String>success("test-files/numbers.txt")
                    .thenApply(bind(this::getFile))
                    .thenApply(bind(this::getFileLines))
                    .thenApply(map(this::parseInts))
                    .thenApply(map(s -> s.mapToInt(Integer::intValue).sum()))
                    .thenApply(peek(s -> {
                    }, System.out::println));

            switch (x) {
                case Result.Success<Integer, String> s -> System.out.println(s.getValue());
                case Result.Failure<Integer, String> f -> System.err.println(f);
                default -> System.out.println("FAIL");
            }

            assertEquals(128, x.thenApply(defaultsTo(-1)));

        }


        @Test
        void test_delay_1() {
            assertSuccessEquals(42,
                    Result.<Integer, String>success(21)
                            .thenApply(bind(delayWithTryCatch(32)))
            );
        }

        @Test
        void test_delay_2() {

            assertSuccessEquals(42,
                    Result.<Integer, String>success(21)
                            .thenApply(bind(x -> promiseResultInt(100, x, true).join()))
            );
        }

        @Test
        void test_bindAsync_to_success_if_Success() {

            assertSuccessEquals(42,
                    Result.<Integer, String>success(22)
                            .thenApply(bindAsync(x -> promiseResultInt(100, x, true)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindAsync_to_success_if_Failure() {

            assertFailureEquals("INIT",
                    Result.<Integer, String>failure("INIT")
                            .thenApply(bindAsync(x -> promiseResultInt(100, x, true)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindAsync_to_Failure_if_Success() {

            assertFailureEquals("Failure",
                    Result.<Integer, String>success(22)
                            .thenApply(bindAsync(x -> promiseResultInt(100, x, false)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindAsync_to_Failure_if_Failure() {

            assertFailureEquals("INIT",
                    Result.<Integer, String>failure("INIT")
                            .thenApply(bindAsync(x -> promiseResultInt(100, x, false)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindFailureAsync_to_success_if_Success() {

            assertSuccessEquals(20,
                    Result.<Integer, String>success(22)
                            .thenApply(bindFailureAsync(x -> promiseResultString(100, x, true)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindFailureAsync_to_success_if_Failure() {

            assertSuccessEquals(42,
                    Result.<Integer, Integer>failure(22)
                            .thenApply(bindFailureAsync(x -> promiseResultInt(100, x, true)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }

        @Test
        void test_bindFailureAsync_to_Failure_if_Success() {

            assertSuccessEquals(20,
                    Result.<Integer, String>success(22)
                            .thenApply(bindFailureAsync(x -> promiseResultString(100, x, false)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }


        @Test
        void test_bindFailureAsync_to_Failure_if_Failure() {

            assertFailureEquals("Failure",
                    Result.<Integer, Integer>failure(22)
                            .thenApply(bindFailureAsync(x -> promiseResultInt(100, x, false)))
                            .thenApply(map(x -> x - 2))
                            .join()
            );
        }


    }

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
                    Applicative.sequenceStream(stream).thenApply(mapFailure(Stream::toList)),
                    failure(List.of("'X' is not a number"))
            );
        }


    }

    @Nested
    public class ResultTraverseApplicativeTest {

        @Test
        void test_allSuccess_Applicative() {

            List<String> list = List.of(
                    "1",
                    "2",
                    "3",
                    "4"
            );
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), success(List.of(
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
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), success(List.of(
                    1
            )));
        }

        @Test
        void test_oneFailure_Applicative() {

            List<String> list = List.of(
                    "X"
            );
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), failure(List.of(
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
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), failure(List.of(
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
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), failure(List.of(
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
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), failure(List.of(
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
            assertEquals(Applicative.traverseList(ResultTest::tryParseInt).apply(list), failure(List.of(
                    "'X' is not a number"
            )));
        }

        @Test
        void test_SingleFailureStream_Applicative() {

            Stream<Result<Integer, String>> stream = Stream.of(
                    failure("'X' is not a number")
            );
            assertEquals(Applicative.sequenceStream(stream).thenApply(mapFailure(Stream::toList)), failure(List.of(
                    "'X' is not a number"))
            );
        }


    }

    @Nested
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
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), success(List.of(
                    1,
                    2,
                    3,
                    4
            )));
        }

        @Test
        void test_empty_Monadic() {

            List<String> list = List.of();
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), success(List.of()));
        }

        @Test
        void test_singleSuccess_Monadic() {

            List<String> list = List.of(
                    "1"
            );

            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), success(List.of(
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
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), failure(
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
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), failure(
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
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), failure(
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
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailure_Monadic() {

            List<String> list = List.of(
                    "X"
            );
            assertEquals(Monadic.traverseList(ResultTest::tryParseInt).apply(list), failure(
                    "'X' is not a number"
            ));
        }

        @Test
        void test_SingleFailureStream_Monadic() {
            Stream<String> stream = Stream.of(
                    "X"
            );
            assertEquals(
                    Monadic.traverseStream(ResultTest::tryParseInt).apply(stream),
                    failure("'X' is not a number")
            );
        }

        @Test
        void test_EmptyStream_Monadic() {
            Stream<String> stream = Stream.of();
            assertEquals(
                    Monadic.traverseStream(ResultTest::tryParseInt).apply(stream).thenApply(map(Stream::toList)),
                    success(List.of())
            );
        }

        @Test
        void test_SingleSuccessStream_Monadic() {
            Stream<String> stream = Stream.of(
                    "1"
            );
            assertEquals(
                    Monadic.traverseStream(ResultTest::tryParseInt).apply(stream).thenApply(map(Stream::toList)),
                    success(List.of(1))
            );
        }

        @Test
        void test_FailureOptional_Monadic() {
            Optional<String> optional = Optional.of(
                    "X"
            );
            assertEquals(
                    Monadic.traverseOptional(ResultTest::tryParseInt).apply(optional),
                    failure("'X' is not a number")
            );
        }

        @Test
        void test_SuccessOptional_Monadic() {
            Optional<String> optional = Optional.of(
                    "1"
            );
            assertEquals(
                    Monadic.traverseOptional(ResultTest::tryParseInt).apply(optional),
                    success(Optional.of(1))
            );
        }

        @Test
        void test_EmptyOptional_Monadic() {
            Optional<String> optional = Optional.empty();
            assertEquals(
                    Monadic.traverseOptional(ResultTest::tryParseInt).apply(optional),
                    success(Optional.empty())
            );
        }


    }


}