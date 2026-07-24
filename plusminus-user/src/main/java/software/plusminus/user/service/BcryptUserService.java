package software.plusminus.user.service;

import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.user.model.User;
import software.plusminus.user.model.UserStatus;
import software.plusminus.user.repository.UserRepository;

import javax.annotation.Nullable;

@AllArgsConstructor
@Transactional
@Service
public class BcryptUserService implements UserService {

    @SuppressWarnings("java:S6437")
    private static final String DUMMY_HASH = BCrypt.hashpw("timing-uniformity-placeholder", BCrypt.gensalt());

    private UserRepository userRepository;

    @Override
    public boolean isRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Nullable
    @Override
    public User findUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.getStatus() == UserStatus.DEACTIVATED) {
            runDummyCheck(password);
            return null;
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public void register(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }

    private void runDummyCheck(String password) {
        BCrypt.checkpw(password, DUMMY_HASH);
    }
}
