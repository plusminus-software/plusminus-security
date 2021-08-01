package software.plusminus.security.configs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationInterceptorTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HandlerMethod method;
    @Mock
    private Object object;
    @Mock
    private RolesAllowed rolesAllowed;

    @InjectMocks
    private AuthorizationInterceptor interceptor;

    @Test
    public void preHandle_ReturnsTrue_IfMethodIsNotHandlerMethod() throws Exception {
        boolean result = interceptor.preHandle(request, response, object);
        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsTrue_IfMethodHasNoRolesAllowedAnnotation() throws Exception {
        boolean result = interceptor.preHandle(request, response, method);
        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsTrue_IfUserIsInRole() throws Exception {
        when(method.getMethodAnnotation(RolesAllowed.class))
                .thenReturn(rolesAllowed);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"role_value"});
        when(request.isUserInRole("role_value"))
                .thenReturn(true);

        boolean result = interceptor.preHandle(request, response, method);

        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsFalse_IfUserHasNoRole() throws Exception {
        when(method.getMethodAnnotation(RolesAllowed.class))
                .thenReturn(rolesAllowed);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"role_value"});

        boolean result = interceptor.preHandle(request, response, method);

        assertThat(result).isFalse();
    }

    @Test
    public void preHandle_SendsError_IfUserHasNoRole() throws Exception {
        when(method.getMethodAnnotation(RolesAllowed.class))
                .thenReturn(rolesAllowed);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"role_value"});

        interceptor.preHandle(request, response, method);

        verify(response).sendError(403);
    }

}