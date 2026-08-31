package software.plusminus.visible.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.plusminus.visible.service.VisibilityService;

import java.util.Set;

/**
 * One property, written only if the caller of this request may see it.
 *
 * <p>The check has to happen here rather than by dropping properties in the serializer modifier:
 * Jackson caches a class's serializer, so the first caller would fix the shape for everyone after.
 *
 * <p>Returning before {@code super} means the getter is never called, so a hidden lazy JPA
 * association is not fetched.
 */
public class VisiblePropertyWriter extends BeanPropertyWriter {

    private static final long serialVersionUID = 1L;

    private final transient VisibilityService visibilityService;
    private final Set<String> requiredRoles;

    VisiblePropertyWriter(BeanPropertyWriter base, VisibilityService visibilityService, Set<String> requiredRoles) {
        super(base);
        this.visibilityService = visibilityService;
        this.requiredRoles = requiredRoles;
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider provider) throws Exception {
        if (!visibilityService.isAllowed(requiredRoles)) {
            return;
        }
        super.serializeAsField(bean, gen, provider);
    }

    /** In an array a position cannot be dropped without shifting the rest, so it becomes null. */
    @Override
    public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider provider) throws Exception {
        if (!visibilityService.isAllowed(requiredRoles)) {
            serializeAsPlaceholder(bean, gen, provider);
            return;
        }
        super.serializeAsElement(bean, gen, provider);
    }
}
