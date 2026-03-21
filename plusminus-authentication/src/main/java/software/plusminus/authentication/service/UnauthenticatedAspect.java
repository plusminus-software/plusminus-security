package software.plusminus.authentication.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import software.plusminus.aspect.Before;
import software.plusminus.authentication.exception.NonPublicApiException;
import software.plusminus.authentication.exception.NotFoundException;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.authentication.service.endpoint.PublicEndpointChecker;
import software.plusminus.context.Context;
import software.plusminus.listener.Stop;
import software.plusminus.security.Security;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@Component
public class UnauthenticatedAspect implements Before {

    private Context<Security> securityContext;
    private Context<Object> handlerContext;
    private Context<HttpServletRequest> httpServletRequestContext;
    private Context<HttpServletResponse> httpServletResponseContext;
    private List<PublicEndpointChecker> publicEndpointCheckers;
    private SecurityProperties properties;

    @Override
    public void before() {
        if (securityContext.optional().isPresent()) {
            return;
        }
        if (handlerContext.get() instanceof ResourceHttpRequestHandler) {
            return;
        }
        boolean isPublicEndpoint = publicEndpointCheckers.stream()
                .anyMatch(PublicEndpointChecker::isPublicEndpoint);
        if (isPublicEndpoint) {
            return;
        }
        if (!isHtmlEndpoint()) {
            throw new NonPublicApiException();
        }
        if (properties.getLoginPage() == null) {
            throw new NotFoundException();
        }
        if (properties.getLoginPage().equals(httpServletRequestContext.get().getRequestURI())) {
            return;
        }
        redirectToLoginPage();
    }

    private boolean isHtmlEndpoint() {
        HttpServletRequest request = httpServletRequestContext.get();
        return Collections.list(request.getHeaders("accept")).stream()
                .anyMatch(header -> header.startsWith("text/html"));
    }

    @SuppressFBWarnings("UNVALIDATED_REDIRECT")
    private void redirectToLoginPage() {
        try {
            httpServletResponseContext.get().sendRedirect(properties.getLoginPage());
            Stop.stop();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
