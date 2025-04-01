package at.base10.result;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.base10.result.Operator.defaultsTo;
import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

public class PromiseHelper {
    public static Function<Integer, Result<Integer, String>> delayWithTryCatch(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            return x -> failure("FAIL");
        }
        return x -> success(x * 2);
    }

    public static CompletableFuture<Result<Integer, String>> promiseResultInt(int ms, int x, boolean isSuccess) {
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

    public static CompletableFuture<Result<Integer, String>> promiseResultString(int ms, boolean isSuccess) {
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


    public static Stream<Integer> parseInts(Stream<String> numbers) {
        return numbers
                .map(TestHelpers::tryParseInt)
                .filter(Result::isSuccess)
                .map(defaultsTo(0));
    }

    public static Result<File, String> getFile(String path) {
        try {
            ClassLoader classLoader = PromiseHelper.class.getClassLoader();

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

    public static Result<Stream<String>, String> getLines(File file) {
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return success(stream.toList().stream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            return failure("No file provided");
        }
    }

}
