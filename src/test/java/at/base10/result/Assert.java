package at.base10.result;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class Assert {

    public static <V, E> void assertSuccessEquals(V value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Success.class, result);
        assertEquals(value, result.value());
        assertThrows(NoSuchElementException.class, result::failure, "No value present");
    }

    public static <V, E> void assertFailureEquals(E value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Failure.class, result);
        assertInstanceOf(Failure.class, result);
        assertEquals(value, result.failure());
        assertThrows(NoSuchElementException.class, result::value, "No value present");
    }


    public static <E, F,G,H> void assertEqualStreamSuccess(Result<Stream<E>, F> result1, Result<Stream<G>, H> result2) {
        assertEquals(
                result1.map(Stream::toList),
                result2.map(Stream::toList)
        );
    }
    public static <E, F,G,H> void assertEqualStreamFailure(Result<E, Stream<F>> result1, Result<G, Stream<H>> result2) {
        assertEquals(
                result1.mapFailure(Stream::toList),
                result2.mapFailure(Stream::toList)
        );
    }
}
