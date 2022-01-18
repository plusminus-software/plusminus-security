package software.plusminus.security.service.check;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.security.configs.SecuredRequest;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;

@Component
public class RolesAllowedSecurityCheck implements SecurityCheck {
    
    @Override
    public boolean check(SecuredRequest request, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return requestMatchesDeclaredRoles(request, handlerMethod);
    }

    private boolean requestMatchesDeclaredRoles(HttpServletRequest request,
                                                HandlerMethod handler) {

        Collection<String> declaredRolesRoles = getDeclaredRoles(handler);
        return declaredRolesRoles.isEmpty()
                || declaredRolesRoles.stream().anyMatch(request::isUserInRole);
    }

    private Collection<String> getDeclaredRoles(HandlerMethod handler) {
        RolesAllowed methodAnnotation = handler.getMethodAnnotation(RolesAllowed.class);
        return methodAnnotation == null
                ? Collections.emptyList()
                : asList(methodAnnotation.value());
    }
}
