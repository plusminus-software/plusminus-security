package software.plusminus.authorization.service;

import software.plusminus.authorization.model.AuthorizationResult;

import java.lang.annotation.Annotation;

public interface AnnotationAuthorizer<A extends Annotation> {
    
    AuthorizationResult authorize(A annotation);

}
