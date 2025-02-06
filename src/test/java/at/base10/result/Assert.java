package at.base10.result;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Assert {

    public static <V, E> void assertSuccessEquals(V value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Success.class, result);
        assertEquals(value, result.getValue());
        assertThrows(NoSuchElementException.class, result::getFailure, "No value present");
    }

    public static <V, E> void assertFailureEquals(E value, Result<V, E> result) {
        assertNotNull(result);
        assertInstanceOf(Result.class, result);
        assertInstanceOf(Failure.class, result);
        assertInstanceOf(Failure.class, result);
        assertEquals(value, result.getFailure());
        assertThrows(NoSuchElementException.class, result::getValue, "No value present");
    }

}
