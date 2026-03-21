package software.plusminus.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NonPublicApiException extends RuntimeException {

    public NonPublicApiException() {
        super("User must be authenticated to call non-public endpoint");
    }
}
