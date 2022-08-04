package software.plusminus.security.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.security.Security;
import software.plusminus.security.util.SecurityUtils;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SecurityContextTest {
    
    private static final String USERNAME = "testName";
    private static final String ROLE = "testRole";
    
    @InjectMocks
    private SecurityContext securityContext;
    
    @Test
    public void username() {
        Security security = Security.builder()
                .username(USERNAME)
                .build();
        SecurityUtils.authenticate(security);
        
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
        Security security = Security.builder()
                .roles(Collections.singleton(ROLE))
                .build();
        SecurityUtils.authenticate(security);
        
        Set<String> result = securityContext.getRoles();
        
        assertThat(result).containsExactly(ROLE);
    }
    
    @Test
    public void isUserInRoleOnEmptyRoles() {
        Set<String> result = securityContext.getRoles();
        assertThat(result).isEmpty();
    }

}