package software.plusminus.user.service;

import software.plusminus.user.model.User;

import javax.annotation.Nullable;

public interface UserService {

    boolean isRegistered(String username);

    @Nullable
    User findUser(String email, String password);

    void register(User user);

}
