package software.plusminus.visible.service;

import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.Security;
import software.plusminus.util.AnnotationUtils;
import software.plusminus.util.FieldUtils;
import software.plusminus.util.MethodUtils;
import software.plusminus.visible.annotation.Visible;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@Component
public class VisibleService {

    private final Context<Security> securityContext;
    private final Map<Class<?>, Boolean> typesWithVisibility = new ConcurrentHashMap<>();

    public VisibleService(Context<Security> securityContext) {
        this.securityContext = securityContext;
    }

    public boolean hasVisibility(Class<?> type) {
        return typesWithVisibility.computeIfAbsent(type, t ->
                AnnotationUtils.findAnnotation(Visible.class, t) != null
                        || FieldUtils.getFieldsStream(t).anyMatch(
                                field -> field.isAnnotationPresent(Visible.class))
                        || MethodUtils.getMethodsStream(t).anyMatch(
                                method -> method.isAnnotationPresent(Visible.class))
        );
    }

    @Nullable
    public Set<String> requiredRoles(AnnotatedElement... members) {
        Visible visible = AnnotationUtils.getAnnotation(Visible.class, members);
        if (visible == null) {
            visible = findVisible(members);
        }
        if (visible == null) {
            return null;
        }
        return Arrays.stream(visible.value())
                .map(role -> role.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(@Nullable Set<String> requiredRoles) {
        if (requiredRoles == null) {
            return true;
        }
        if (requiredRoles.isEmpty()) {
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

    @Nullable
    private static Visible findVisible(AnnotatedElement... members) {
        Class<?> chosen = null;
        Visible visible = null;
        for (AnnotatedElement member : members) {
            if (!(member instanceof Member)) {
                continue;
            }
            Class<?> declaringType = ((Member) member).getDeclaringClass();
            Visible declared = declaringType.getDeclaredAnnotation(Visible.class);
            if (declared != null && (chosen == null || chosen.isAssignableFrom(declaringType))) {
                chosen = declaringType;
                visible = declared;
            }
        }
        return visible;
    }
}
