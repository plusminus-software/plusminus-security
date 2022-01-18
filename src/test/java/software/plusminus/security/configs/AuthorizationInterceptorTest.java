package software.plusminus.security.configs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.security.service.check.SecurityCheck;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationInterceptorTest {

    @Mock
    private HttpServletRequest notSecuredRequest;
    @Mock
    private SecuredRequest securedRequest;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HandlerMethod method;
    @Mock
    private SecurityCheck securityCheck;
    @Spy
    private List<SecurityCheck> checks = new ArrayList<>();

    @InjectMocks
    private AuthorizationInterceptor interceptor;

    @Before
    public void before() {
        checks.clear();
        checks.add(securityCheck);
    }

    @Test
    public void preHandle_ReturnsTrue_IfRequestIsNotSecured() throws Exception {
        boolean result = interceptor.preHandle(notSecuredRequest, response, method);
        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsTrue_IfAllSecurityChecksPassed() throws Exception {
        when(securityCheck.check(securedRequest, method))
                .thenReturn(true);
        
        boolean result = interceptor.preHandle(securedRequest, response, method);
        
        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsFalseAndSendsError_IfAtLeastOneCheckIsFailed() throws Exception {
        when(securityCheck.check(securedRequest, method))
                .thenReturn(false);
        
        boolean result = interceptor.preHandle(securedRequest, response, method);

        assertThat(result).isFalse();
        verify(response).sendError(403);
    }

    

}