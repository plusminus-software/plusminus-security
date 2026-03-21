package software.plusminus.authorization.service.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.service.AnnotationAuthorizer;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Component
public class RoleAuthorizer implements AnnotationAuthorizer<Role> {

    private Context<Security> securityContext;

    @Override
    public AuthorizationResult authorize(Role role) {
        Set<String> roles = securityContext.get().getRoles().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        boolean containsAnyRole = Stream.of(role.value())
                .anyMatch(r -> roles.contains(r.toLowerCase()));
        if (!containsAnyRole) {
            return AuthorizationResult.error("User '" + securityContext.get().getUsername()
                    + "' does not have at least one of the required roles: " + Arrays.toString(role.value()));
        }
        return AuthorizationResult.ok();
    }
}
