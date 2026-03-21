package software.plusminus.authorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import software.plusminus.authorization.annotation.AuthorizationAnnotation;
import software.plusminus.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorizationAnnotationVerifier {

    @Autowired
    void init(RequestMappingHandlerMapping handlerMapping,
              List<AnnotationAuthorizerContainer<?>> annotationAuthorizers) {
        Set<Class<? extends Annotation>> supportedAnnotations = annotationAuthorizers.stream()
                .map(AnnotationAuthorizerContainer::getAnnotationType)
                .collect(Collectors.toSet());
        Set<Class<? extends Annotation>> annotationsWithMissedAuthorizers = 
                handlerMapping.getHandlerMethods().values().stream()
                        .map(this::findAuthorizationAnnotations)
                        .flatMap(List::stream)
                        .map(Annotation::annotationType)
                        .filter(annotationType -> !supportedAnnotations.contains(annotationType))
                        .collect(Collectors.toSet());

        if (!annotationsWithMissedAuthorizers.isEmpty()) {
            throw new ApplicationContextException(
                    "Can't find authorizers for the following annotations marked as @AuthorizationAnnotation: "
                            + annotationsWithMissedAuthorizers);
        }
    }
    
    private List<Annotation> findAuthorizationAnnotations(HandlerMethod handlerMethod) {
        return AnnotationUtils.findMergedAnnotationsOnMethodAndClass(handlerMethod.getMethod(),
                annotation -> annotation.annotationType().isAnnotationPresent(AuthorizationAnnotation.class));
    }
}
