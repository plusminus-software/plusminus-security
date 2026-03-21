package software.plusminus.security.service;

import software.plusminus.security.Security;

import javax.annotation.Nullable;

public interface CredentialService {

    @Nullable
    Security provideSecurity(String user, String password);

}
