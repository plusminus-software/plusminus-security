package software.plusminus.security.configs;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (requestMatchesDeclaredRoles(request, handlerMethod)) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
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
