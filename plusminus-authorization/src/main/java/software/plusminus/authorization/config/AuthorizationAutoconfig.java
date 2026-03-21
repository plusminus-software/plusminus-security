package software.plusminus.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.authorization.service.AnnotationAuthorizer;
import software.plusminus.spring.SpringUtil;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("java:S1452")
@Configuration
@ComponentScan("software.plusminus.authorization")
public class AuthorizationAutoconfig {

    @Bean
    List<AnnotationAuthorizerContainer<? extends Annotation>> annotationAuthorizers(
            List<AnnotationAuthorizer<?>> authorizers) {
        return authorizers.stream()
                .map(this::createContainer)
                .collect(Collectors.toList());
    }

    private <A extends Annotation> AnnotationAuthorizerContainer<A> createContainer(
            AnnotationAuthorizer<A> authorizer) {
        Class<A> annotationType = SpringUtil.resolveGenericType(authorizer, AnnotationAuthorizer.class);
        return new AnnotationAuthorizerContainer<>(annotationType, authorizer);
    }
}
