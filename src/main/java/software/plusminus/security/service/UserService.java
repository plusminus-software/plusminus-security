package software.plusminus.security.service;

import software.plusminus.security.model.User;

public interface UserService {

    boolean isRegistered(String username);

    User findUser(String username, String password);

    void register(User user);

}
