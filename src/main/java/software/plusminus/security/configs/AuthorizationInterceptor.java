package software.plusminus.security.configs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import software.plusminus.security.service.check.SecurityCheck;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@AllArgsConstructor
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private List<SecurityCheck> checks;
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!(request instanceof SecuredRequest)) {
            return true;
        }
        
        SecuredRequest securedRequest = (SecuredRequest) request;
        List<SecurityCheck> failedChecks = checks.stream()
                .filter(check -> !check.check(securedRequest, handler))
                .collect(Collectors.toList());
        if (!failedChecks.isEmpty()) {
            log.info("Failed SecurityChecks: {}", failedChecks);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

}
