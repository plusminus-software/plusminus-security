package software.plusminus.security.service;

import software.plusminus.security.model.User;

import javax.annotation.Nullable;

public interface UserService {

    boolean isRegistered(String username);

    @Nullable
    User findUser(String username, String password);

    void register(User user);

}
