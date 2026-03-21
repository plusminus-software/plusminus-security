package software.plusminus.authentication.service;

import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.plusminus.aspect.Before;
import software.plusminus.authentication.model.AuthenticatedRequest;
import software.plusminus.context.Context;
import software.plusminus.context.WritableContext;
import software.plusminus.http.HttpFilter;
import software.plusminus.listener.Joinpoint;
import software.plusminus.security.Security;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@Component
public class AuthenticationAspect implements Before {

    private WritableContext<HttpServletRequest> httpServletRequestContext;
    private Context<Security> securityContext;

    @Override
    public Joinpoint joinpoint() {
        return HttpFilter.JOINPOINT;
    }

    @Override
    public void before() {
        Security security = securityContext.get();
        if (security != null) {
            httpServletRequestContext.setOrReplace(
                    new AuthenticatedRequest(
                            httpServletRequestContext.get(),
                            security
                    )
            );
        }
    }
}
