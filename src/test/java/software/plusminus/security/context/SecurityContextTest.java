package software.plusminus.security.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.context.Context;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityContextTest {
    
    private static final String USERNAME = "testName";
    private static final String ROLE = "testRole";
    
    @Mock
    private Context<AuthenticationParameters> container;
    @InjectMocks
    private SecurityContext securityContext;
    
    @Test
    public void username() {
        AuthenticationParameters parameters = new AuthenticationParameters();
        parameters.setUsername(USERNAME);
        when(container.get()).thenReturn(parameters);
        
        String username = securityContext.getUsername();
        
        assertThat(username).isEqualTo(USERNAME);
    }
    
    @Test
    public void nullUsername() {
        String username = securityContext.getUsername();
        assertThat(username).isNull();
    }

    @Test
    public void roles() {
        AuthenticationParameters parameters = new AuthenticationParameters();
        parameters.setRoles(Collections.singleton(ROLE));
        when(container.get()).thenReturn(parameters);
        
        Set<String> result = securityContext.getRoles();
        
        assertThat(result).containsExactly(ROLE);
    }
    
    @Test
    public void isUserInRoleOnEmptyRoles() {
        Set<String> result = securityContext.getRoles();
        assertThat(result).isEmpty();
    }

}