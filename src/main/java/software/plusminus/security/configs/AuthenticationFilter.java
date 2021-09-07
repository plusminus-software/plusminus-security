package software.plusminus.security.configs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.context.Context;
import software.plusminus.security.properties.SecurityProperties;
import software.plusminus.security.util.CookieUtil;

import java.io.IOException;
import javax.annotation.Nullable;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    
    private SecurityProperties properties;
    private AuthenticationService authenticationService;
    private Context<AuthenticationParameters> securityContext;

    @Override
    @SuppressFBWarnings(value = "UNVALIDATED_REDIRECT", 
            justification = "False-positive: the redirect should be validated in controller")
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        AuthenticationParameters parameters = getAuthenticationParameters(request, response);
        if (parameters == null) {
            response.sendRedirect(properties.getLoginPage());
            return;
        }
        try {
            securityContext.set(parameters);
            chain.doFilter(new SecuredRequest(request, parameters), response);
        } finally {
            securityContext.set(null);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {

        return properties.getOpenUrls().stream()
                .anyMatch(request.getRequestURI()::matches);
    }

    @Nullable
    private AuthenticationParameters getAuthenticationParameters(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String cookie = CookieUtil.getValue(request, properties.getCookieName());
        if (cookie != null) {
            AuthenticationParameters securityParameters = authenticationService.parseToken(cookie);
            if (securityParameters == null) {
                CookieUtil.clear(response, properties.getCookieName());
            } else {
                return securityParameters;
            }
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            authorizationHeader = authorizationHeader
                    .replace("Bearer ", "")
                    .replace("bearer ", "");
            AuthenticationParameters securityParameters = authenticationService.parseToken(authorizationHeader);
            if (securityParameters != null) {
                return securityParameters;
            }
        }
        
        return null;
    }
}
