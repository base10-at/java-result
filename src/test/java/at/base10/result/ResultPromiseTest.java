package at.base10.result;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Assert.assertFailureEquals;
import static at.base10.result.Assert.assertSuccessEquals;
import static at.base10.result.Operator.*;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    CompletableFuture<Result<Integer, String>> promiseResultString(int ms, boolean isSuccess) {
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
                .map(TestHelpers::tryParseInt)
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
                .then(bind(this::getFile))
                .then(bind(this::getFileLines))
                .then(map(this::parseInts))
                .then(map(s -> s.mapToInt(Integer::intValue).sum()))
                .then(peek(s -> {
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

    @Test
    void test_bindAsync_to_success_if_Success() {

        assertSuccessEquals(42,
                Result.<Integer, String>success(22)
                        .then(bindAsync(x -> promiseResultInt(2, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_bindAsync_to_success_if_Failure() {

        assertFailureEquals("INIT",
                Result.<Integer, String>failure("INIT")
                        .then(bindAsync(x -> promiseResultInt(100, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_bindAsync_to_Failure_if_Success() {

        assertFailureEquals("Failure",
                Result.<Integer, String>success(22)
                        .then(bindAsync(x -> promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_bindAsync_to_Failure_if_Failure() {

        assertFailureEquals("INIT",
                Result.<Integer, String>failure("INIT")
                        .then(bindAsync(x -> promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_bindFailureAsync_to_success_if_Success() {

        assertSuccessEquals(20,
                Result.<Integer, String>success(22)
                        .then(bindFailureAsync(x -> promiseResultString(100, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @SuppressWarnings("ConstantValue")
    @Test
    void test_bindFailureAsync_to_success_if_Failure() {

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
                        .then(bindFailureAsync(x -> promiseResultInt(2, x, true)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }

    @Test
    void test_bindFailureAsync_to_Failure_if_Success() {

        assertSuccessEquals(20,
                Result.<Integer, String>success(22)
                        .then(bindFailureAsync(x -> promiseResultString(100, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }


    @Test
    void test_bindFailureAsync_to_Failure_if_Failure() {

        assertFailureEquals("Failure",
                Result.<Integer, Integer>failure(22)
                        .then(bindFailureAsync(x -> promiseResultInt(100, x, false)))
                        .thenApply(map(x -> x - 2))
                        .join()
        );
    }


}
