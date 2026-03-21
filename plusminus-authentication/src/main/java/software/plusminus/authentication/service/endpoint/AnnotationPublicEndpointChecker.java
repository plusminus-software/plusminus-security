package software.plusminus.authentication.service.endpoint;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.context.Context;
import software.plusminus.util.AnnotationUtils;

import java.util.Optional;

@AllArgsConstructor
@Component
public class AnnotationPublicEndpointChecker implements PublicEndpointChecker {

    private Context<HandlerMethod> handlerMethodContext;

    @Override
    public boolean isPublicEndpoint() {
        Optional<HandlerMethod> handlerMethod = handlerMethodContext.optional();
        if (!handlerMethod.isPresent()) {
            return false;
        }
        Public publicAnnotation = AnnotationUtils.findAnnotation(
                Public.class,
                handlerMethod.get().getMethod()
        );
        if (publicAnnotation != null) {
            return publicAnnotation.value();
        }
        return false;
    }
}
