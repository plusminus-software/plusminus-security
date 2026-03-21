package software.plusminus.authorization.service;

import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import software.plusminus.aspect.Before;
import software.plusminus.authorization.exception.AuthorizationException;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.context.Context;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@AllArgsConstructor
@Component
public class AuthorizationAspect implements Before {

    private Context<Object> handlerContext;
    private List<Authorizer> authorizers;

    @Override
    public void before() {
        if (handlerContext.get() instanceof ResourceHttpRequestHandler) {
            return;
        }
        authorizers.forEach(this::run);
    }

    private void run(Authorizer authorizer) {
        AuthorizationResult result = authorizer.authorize();
        if (!result.isOk()) {
            throw new AuthorizationException(result.getErrorMessage());
        }
    }
}
