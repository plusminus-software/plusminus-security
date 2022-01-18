package software.plusminus.security.service.check;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.security.configs.SecuredRequest;

import javax.annotation.security.RolesAllowed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RolesAllowedSecurityCheckTest {

    @Mock
    private SecuredRequest request;
    @Mock
    private HandlerMethod method;
    @Mock
    private RolesAllowed rolesAllowed;
    
    private RolesAllowedSecurityCheck check = new RolesAllowedSecurityCheck();

    @Test
    public void preHandle_ReturnsTrue_IfUserIsInRole() throws Exception {
        when(method.getMethodAnnotation(RolesAllowed.class))
                .thenReturn(rolesAllowed);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"role_value"});
        when(request.isUserInRole("role_value"))
                .thenReturn(true);

        boolean result = check.check(request, method);

        assertThat(result).isTrue();
    }

    @Test
    public void preHandle_ReturnsFalse_IfUserHasNoRole() throws Exception {
        when(method.getMethodAnnotation(RolesAllowed.class))
                .thenReturn(rolesAllowed);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"role_value"});

        boolean result = check.check(request, method);

        assertThat(result).isFalse();
    }
}