package software.plusminus.authorization.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.utils.AuthorizationUtils;
import software.plusminus.context.Context;
import software.plusminus.spring.SpringUtil;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

@AllArgsConstructor
@Component
public class AnnotationAuthorizerRunner implements Authorizer {

    private Context<HandlerMethod> handlerMethodContext;
    private List<AnnotationAuthorizer<? extends Annotation>> authorizers;
    
    @Override
    public AuthorizationResult authorize() {
        return authorizers.stream()
                .map(this::runAnnotationAuthorizer)
                .filter(Objects::nonNull)
                .filter(result -> !result.isOk())
                .findFirst()
                .orElse(AuthorizationResult.ok());
    }

    @Nullable
    private <A extends Annotation> AuthorizationResult runAnnotationAuthorizer(AnnotationAuthorizer<A> authorizer) {
        Class<A> annotationType = SpringUtil.resolveGenericType(authorizer, AnnotationAuthorizer.class);
        A annotation = AuthorizationUtils.findAnnotation(handlerMethodContext.get(), annotationType);
        if (annotation == null) {
            return null;
        }
        return authorizer.authorize(annotation);
    }
}
