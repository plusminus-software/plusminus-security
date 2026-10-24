package software.plusminus.authentication.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.model.AuthenticatedRequest;
import software.plusminus.context.Context;
import software.plusminus.scope.AroundScope;
import software.plusminus.security.Security;

import java.util.Optional;
import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

/**
 * Runs the scope with the current request replaced by an {@link AuthenticatedRequest},
 * when the current user is authenticated. The rest of the filter chain gets the replaced request too.
 *
 * <p>The scope also wraps the ASYNC and ERROR dispatches of a request. If the security cannot be resolved
 * on an ERROR dispatch, the error page is still rendered, for an unauthenticated user.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@Component
public class AuthenticatedRequestAroundScope implements AroundScope {

    private Context<HttpServletRequest> httpServletRequestContext;
    private Context<Security> securityContext;

    @Override
    public boolean supports(Object source) {
        return true;
    }

    @Override
    public void around(Runnable runnable) {
        Optional<HttpServletRequest> request = httpServletRequestContext.optional();
        Security security = findSecurity(request.orElse(null));
        if (security == null || !request.isPresent()) {
            runnable.run();
            return;
        }
        HttpServletRequest authenticatedRequest = new AuthenticatedRequest(request.get(), security);
        Context.where(httpServletRequestContext, authenticatedRequest)
                .where(securityContext, security)
                .run(runnable);
    }

    @Nullable
    private Security findSecurity(@Nullable HttpServletRequest request) {
        try {
            return securityContext.get();
        } catch (RuntimeException e) {
            if (request != null && request.getDispatcherType() == DispatcherType.ERROR) {
                log.warn("Cannot resolve security on the error dispatch, continuing as unauthenticated", e);
                return null;
            }
            throw e;
        }
    }
}
