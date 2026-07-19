package software.plusminus.authorization.service.host;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.service.Authorizer;
import software.plusminus.authorization.utils.AuthorizationUtils;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Component
public class HostAuthorizer implements Authorizer {

    private Context<Security> securityContext;
    private Context<HttpServletRequest> httpServletRequestContext;

    @Override
    public AuthorizationResult authorize() {
        Optional<String> allowedHost = securityContext.optional()
                .map(Security::getParameters)
                .map(parameters -> parameters.get("host"));
        if (!allowedHost.isPresent()) {
            return AuthorizationResult.ok();
        }
        
        String currentHost = AuthorizationUtils.getHost(httpServletRequestContext.get());
        if (!allowedHost.get().equals(currentHost)) {
            return AuthorizationResult.error("The current host '" + currentHost
                    + "' does not equal to host '" + allowedHost.get() + "' from token");
        }
        return AuthorizationResult.ok();
    }
}
