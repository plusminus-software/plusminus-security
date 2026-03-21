package software.plusminus.authentication.service.endpoint;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.context.Context;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class ControllerPublicEndpointChecker implements PublicEndpointChecker {

    private static final List<Class<?>> PUBLIC_CONTROLLERS = Collections.singletonList(ErrorController.class);

    private Context<HandlerMethod> handlerMethodContext;

    @Override
    public boolean isPublicEndpoint() {
        Optional<HandlerMethod> handlerMethod = handlerMethodContext.optional();
        if (!handlerMethod.isPresent()) {
            return false;
        }
        return PUBLIC_CONTROLLERS.stream()
                .anyMatch(p -> p.isAssignableFrom(handlerMethod.get().getBeanType()));
    }
}
