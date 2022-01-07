package software.plusminus.security.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.security.model.User;
import software.plusminus.security.repository.UserRepository;

import javax.annotation.Nullable;

@Service
@Transactional
public class BcryptUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Nullable
    @Override
    public User findUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public void register(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }
}
