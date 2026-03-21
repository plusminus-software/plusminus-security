package software.plusminus.authorization.service.rolesallowed;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.service.AnnotationAuthorizer;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;

@AllArgsConstructor
@Component
public class RolesAllowedAuthorizer implements AnnotationAuthorizer<RolesAllowed> {

    private Context<Security> securityContext;

    @Override
    public AuthorizationResult authorize(RolesAllowed annotation) {
        List<String> declaredRoles = Arrays.asList(annotation.value());
        Set<String> allowedRoles = securityContext.get().getRoles();
        boolean rolePresent = declaredRoles.stream().anyMatch(allowedRoles::contains);
        if (!rolePresent) {
            return AuthorizationResult.error("User '" + securityContext.get().getUsername()
                    + "' does not have at least one of the required roles: " + declaredRoles);
        }
        return AuthorizationResult.ok();
    }
}
