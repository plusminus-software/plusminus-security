package software.plusminus.authorization.model;

import lombok.Getter;

@SuppressWarnings("java:S1845")
@Getter
public class AuthorizationResult {

    private static final AuthorizationResult OK;

    static {
        OK = new AuthorizationResult();
        OK.ok = true;
    }

    private boolean ok;
    private String errorMessage;

    public static AuthorizationResult ok() {
        return OK;
    }

    public static AuthorizationResult error(String errorMessage) {
        AuthorizationResult result = new AuthorizationResult();
        result.errorMessage = errorMessage;
        return result;
    }
}
