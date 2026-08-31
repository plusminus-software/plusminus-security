package software.plusminus.visible.service;

import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.Security;
import software.plusminus.util.AnnotationUtils;
import software.plusminus.util.FieldUtils;
import software.plusminus.util.MethodUtils;
import software.plusminus.visible.annotation.Visible;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@Component
public class VisibilityService {

    private final Context<Security> securityContext;
    private final Map<Class<?>, Boolean> hasVisibility = new ConcurrentHashMap<>();

    public VisibilityService(Context<Security> securityContext) {
        this.securityContext = securityContext;
    }

    public boolean supportsVisibility(Class<?> type) {
        return hasVisibility.computeIfAbsent(type, t ->
                AnnotationUtils.findAnnotation(Visible.class, t) != null
                        || FieldUtils.getFieldsStream(t).anyMatch(
                                field -> field.isAnnotationPresent(Visible.class))
                        || MethodUtils.getMethodsStream(t).anyMatch(
                                method -> method.isAnnotationPresent(Visible.class))
        );
    }

    @Nullable
    public Set<String> requiredRoles(@Nullable Class<?> declaringType, AnnotatedElement... members) {
        Visible visible = AnnotationUtils.getAnnotation(Visible.class, members);
        if (visible == null && declaringType != null) {
            visible = AnnotationUtils.findAnnotation(Visible.class, declaringType);
        }
        if (visible == null) {
            return null;
        }
        return Arrays.stream(visible.value())
                .map(role -> role.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(@Nullable Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return false;
        }
        if (requiredRoles.contains(Visible.ALL)) {
            return true;
        }
        Security security = securityContext.get();
        if (security == null || security.getRoles() == null) {
            return false;
        }
        return security.getRoles().stream()
                .map(role -> role.toLowerCase(Locale.ROOT))
                .anyMatch(requiredRoles::contains);
    }
}
