package software.plusminus.authorization.service.rolesallowed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.Collections;
import java.util.HashSet;
import javax.annotation.security.RolesAllowed;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class RolesAllowedAuthorizerTest {

    @Mock
    private Context<Security> securityContext;
    @Mock
    private RolesAllowed rolesAllowed;

    private RolesAllowedAuthorizer rolesAllowedAuthorizer;

    @Before
    public void before() {
        rolesAllowedAuthorizer = new RolesAllowedAuthorizer(securityContext);
        when(rolesAllowed.value())
                .thenReturn(new String[]{"admin", "moderator"});
    }


    @Test
    public void userHasRole() {
        when(securityContext.get())
                .thenReturn(Security.builder()
                        .roles(new HashSet<>(Collections.singletonList("moderator")))
                        .build());

        AuthorizationResult result = rolesAllowedAuthorizer.authorize(rolesAllowed);

        check(result.isOk()).isTrue();
    }

    @Test
    public void userHasNoRole() {
        when(rolesAllowed.value())
                .thenReturn(new String[]{"admin", "moderator"});
        when(securityContext.get())
                .thenReturn(Security.builder()
                        .username("myUser")
                        .roles(new HashSet<>(Collections.singletonList("user")))
                        .build());

        AuthorizationResult result = rolesAllowedAuthorizer.authorize(rolesAllowed);

        check(result.isOk()).isFalse();
        check(result.getErrorMessage())
                .is("User 'myUser' does not have at least one of the required roles: [admin, moderator]");
    }
}