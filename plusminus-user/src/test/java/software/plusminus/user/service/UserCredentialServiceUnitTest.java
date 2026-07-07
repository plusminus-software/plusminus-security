package software.plusminus.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.security.Security;
import software.plusminus.user.model.User;

import java.util.Collections;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserCredentialServiceUnitTest {

    @Mock
    private TenantWrapper tenantWrapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserCredentialService service;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(tenantWrapper.callWithTenantIfNeeded(any(), any()))
                .thenAnswer(invocation -> ((Callable<User>) invocation.getArgument(1)).call());
    }

    @Test
    public void provideSecurityUserNotFound() {
        when(userService.findUser("email", "password")).thenReturn(null);
        assertThat(service.provideSecurity("email", "password")).isNull();
    }

    @Test
    public void provideSecurityUserFound() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@email.com");
        user.setTenant("tenant1");
        user.setRoles(Collections.singleton("admin"));
        when(userService.findUser(eq("john@email.com"), eq("password"))).thenReturn(user);

        Security security = service.provideSecurity("john@email.com", "password");

        assertThat(security).isNotNull();
        assertThat(security.getUsername()).isEqualTo("john");
        assertThat(security.getRoles()).containsExactly("admin");
        assertThat(security.getParameters().get("tenant")).isEqualTo("tenant1");
        assertThat(security.getParameters().get("email")).isEqualTo("john@email.com");
    }
}
