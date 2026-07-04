package software.plusminus.authentication.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.model.AuthenticatedRequest;
import software.plusminus.context.Context;
import software.plusminus.context.WritableContext;
import software.plusminus.scope.events.ScopeStartedEvent;
import software.plusminus.security.Security;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@Component
public class AuthenticatedRequestListener {

    private WritableContext<HttpServletRequest> httpServletRequestContext;
    private Context<Security> securityContext;

    @EventListener
    public void scopeStarted(ScopeStartedEvent event) {
        Security security = securityContext.get();
        if (security != null) {
            AuthenticatedRequest authenticatedRequest =
                    new AuthenticatedRequest(httpServletRequestContext.get(), security);
            httpServletRequestContext.replace(authenticatedRequest);
        }
    }
}
