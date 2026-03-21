package software.plusminus.authorization.service.localhost;

import software.plusminus.authorization.annotation.AuthorizationAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AuthorizationAnnotation
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalhostOnly {
}
