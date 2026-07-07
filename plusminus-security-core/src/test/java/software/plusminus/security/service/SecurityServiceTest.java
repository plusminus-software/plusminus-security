package software.plusminus.security.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.security.Security;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {

    @Mock
    private TokenContext tokenContext;
    @Mock
    private TokenProcessor tokenProcessor;
    @Mock
    private CredentialService credentialService;

    private SecurityService service;

    private Security security = Security.builder().username("user").build();

    @Before
    public void setUp() {
        service = new SecurityService(singletonList(tokenContext), singletonList(tokenProcessor),
                singletonList(credentialService), Collections.emptyList());
    }

    @Test
    public void getSecurityReturnsNullWithoutToken() {
        when(tokenContext.getToken()).thenReturn(null);
        assertThat(service.getSecurity()).isNull();
    }

    @Test
    public void getSecurityResolvesToken() {
        when(tokenContext.getToken()).thenReturn("token");
        when(tokenProcessor.getSecurity("token")).thenReturn(security);
        assertThat(service.getSecurity()).isSameAs(security);
    }

    @Test
    public void getSecurityReturnsNullWhenTokenNotResolvable() {
        when(tokenContext.getToken()).thenReturn("token");
        when(tokenProcessor.getSecurity("token")).thenReturn(null);
        assertThat(service.getSecurity()).isNull();
    }

    @Test
    public void createTokenReturnsNullWithoutCredentials() {
        when(credentialService.provideSecurity("user", "pass")).thenReturn(null);
        assertThat(service.createToken("user", "pass")).isNull();
    }

    @Test
    public void createTokenReturnsToken() {
        when(credentialService.provideSecurity("user", "pass")).thenReturn(security);
        when(tokenProcessor.getToken(any(Security.class))).thenReturn("token");
        when(tokenContext.setToken("token")).thenReturn(true);
        assertThat(service.createToken("user", "pass")).isEqualTo("token");
    }

    @Test
    public void createTokenFailsWhenTokenNotGenerated() {
        when(credentialService.provideSecurity("user", "pass")).thenReturn(security);
        when(tokenProcessor.getToken(any(Security.class))).thenReturn(null);
        assertThatThrownBy(() -> service.createToken("user", "pass"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void createTokenFailsWhenTokenNotSet() {
        when(credentialService.provideSecurity("user", "pass")).thenReturn(security);
        when(tokenProcessor.getToken(any(Security.class))).thenReturn("token");
        when(tokenContext.setToken("token")).thenReturn(false);
        assertThatThrownBy(() -> service.createToken("user", "pass"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void clearTokenDelegates() {
        service.clearToken();
        verify(tokenContext).clearToken();
    }
}
