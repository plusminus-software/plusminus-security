package software.plusminus.security.service;

import software.plusminus.security.Security;

import javax.annotation.Nullable;

public interface TokenProcessor {

    @Nullable
    String getToken(Security security);

    @Nullable
    Security getSecurity(String token);

}
