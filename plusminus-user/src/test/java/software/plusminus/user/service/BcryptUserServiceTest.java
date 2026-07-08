package software.plusminus.user.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.user.model.User;
import software.plusminus.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BcryptUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @InjectMocks
    private BcryptUserService service;

    @Test
    public void isRegisteredTrue() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(new User());
        assertThat(service.isRegistered("a@b.com")).isTrue();
    }

    @Test
    public void isRegisteredFalse() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(null);
        assertThat(service.isRegistered("a@b.com")).isFalse();
    }

    @Test
    public void findUserUnknownEmail() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(null);
        assertThat(service.findUser("a@b.com", "secret")).isNull();
    }

    @Test
    public void findUserCorrectPassword() {
        User user = new User();
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        when(userRepository.findByEmail("a@b.com")).thenReturn(user);

        assertThat(service.findUser("a@b.com", "secret")).isSameAs(user);
    }

    @Test
    public void findUserWrongPassword() {
        User user = new User();
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        when(userRepository.findByEmail("a@b.com")).thenReturn(user);

        assertThat(service.findUser("a@b.com", "wrong")).isNull();
    }

    @Test
    @SuppressFBWarnings(value = "HARD_CODE_PASSWORD",
            justification = "Test literal password verifies BCrypt hashing, not a real credential.")
    public void registerHashesPassword() {
        String raw = "secret";
        User user = new User();
        user.setPassword(raw);

        service.register(user);

        verify(userRepository).save(userCaptor.capture());
        String stored = userCaptor.getValue().getPassword();
        assertThat(stored).isNotEqualTo(raw);
        assertThat(BCrypt.checkpw(raw, stored)).isTrue();
    }
}
