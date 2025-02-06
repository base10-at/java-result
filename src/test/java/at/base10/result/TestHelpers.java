package at.base10.result;

import static at.base10.result.Result.failure;
import static at.base10.result.Result.success;

public class TestHelpers {

    public static Result<Integer, String> tryParseInt(String number) {
        try {
            return success(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            return failure("'" + number + "' is not a number");
        }
    }
}
