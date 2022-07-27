package software.plusminus.security.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.plusminus.security.Security;
import software.plusminus.security.SecurityRequest;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityContextTest {
    
    private static final String USERNAME = "testName";
    private static final String ROLE = "testRole";
    
    @Mock
    private SecurityRequest request;
    @InjectMocks
    private SecurityContext securityContext;
    
    @Before
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
    
    @Test
    public void username() {
        Security security = Security.builder()
                .username(USERNAME)
                .build();
        when(request.getSecurity()).thenReturn(security);
        
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
        when(request.getSecurity()).thenReturn(security);
        
        Set<String> result = securityContext.getRoles();
        
        assertThat(result).containsExactly(ROLE);
    }
    
    @Test
    public void isUserInRoleOnEmptyRoles() {
        Set<String> result = securityContext.getRoles();
        assertThat(result).isEmpty();
    }

}