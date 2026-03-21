package software.plusminus.authorization.config;

import lombok.Getter;
import software.plusminus.authorization.service.AnnotationAuthorizer;

import java.lang.annotation.Annotation;

@Getter
public class AnnotationAuthorizerContainer<A extends Annotation> {

    private Class<A> annotationType;
    private AnnotationAuthorizer<A> annotationAuthorizer;

    AnnotationAuthorizerContainer(Class<A> annotationType,
                                  AnnotationAuthorizer<A> annotationAuthorizer) {
        this.annotationType = annotationType;
        this.annotationAuthorizer = annotationAuthorizer;
    }
}
