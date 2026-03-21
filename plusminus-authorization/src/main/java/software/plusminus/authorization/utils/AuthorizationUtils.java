package software.plusminus.authorization.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class AuthorizationUtils {

    public String getHost(HttpServletRequest request) {
        URL url;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new SecurityException(e);
        }
        return url.getHost();
    }
    
    @Nullable
    public <A extends Annotation> A findAnnotation(HandlerMethod method, Class<A> annotationType) {
        A methodAnnotation = method.getMethodAnnotation(annotationType);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return findAnnotation(method.getBeanType(), annotationType);
    }
    
    @Nullable
    private <A extends Annotation> A findAnnotation(Class<?> type, Class<A> annotationType) {
        Class<?> current = type;
        while (current != null) {
            A annotation = current.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
