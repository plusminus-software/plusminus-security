package software.plusminus.security.service;

import javax.annotation.Nullable;

public interface TokenContext {

    @Nullable
    String getToken();

    boolean setToken(String token);

    void clearToken();

}
