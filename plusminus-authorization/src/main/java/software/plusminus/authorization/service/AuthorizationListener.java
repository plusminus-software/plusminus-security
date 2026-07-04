package software.plusminus.authorization.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.authorization.exception.AuthorizationException;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.scope.events.InvocationStartedEvent;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@AllArgsConstructor
@Component
public class AuthorizationListener {

    private List<Authorizer> authorizers;

    @EventListener
    public void authorize(InvocationStartedEvent<HandlerMethod> event) {
        authorizers.forEach(this::run);
    }

    private void run(Authorizer authorizer) {
        AuthorizationResult result = authorizer.authorize();
        if (!result.isOk()) {
            throw new AuthorizationException(result.getErrorMessage());
        }
    }
}
