package software.plusminus.visible.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import software.plusminus.visible.annotation.Visible;
import software.plusminus.visible.service.VisibilityService;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps every property of an opted-in class so that {@link Visible} decides, per request, whether
 * it is written.
 *
 * <p>Classes that never mention {@link Visible} are returned untouched - see
 * {@link VisibilityService#supportsVisibility}. Without that, switching this on would empty every DTO in
 * the application at once.
 */
public class VisibleSerializerModifier extends BeanSerializerModifier {

    private static final long serialVersionUID = 1L;

    private final transient VisibilityService visibilityService;

    public VisibleSerializerModifier(VisibilityService visibilityService) {
        this.visibilityService = visibilityService;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDescription,
                                                     List<BeanPropertyWriter> properties) {
        Class<?> beanClass = beanDescription.getBeanClass();
        if (!visibilityService.supportsVisibility(beanClass)) {
            return properties;
        }
        Map<String, BeanPropertyDefinition> definitions = definitions(beanDescription);
        List<BeanPropertyWriter> wrapped = new ArrayList<>(properties.size());
        for (BeanPropertyWriter property : properties) {
            wrapped.add(wrap(property, definitions.get(property.getName()), beanClass));
        }
        return wrapped;
    }

    private BeanPropertyWriter wrap(BeanPropertyWriter property,
                                    BeanPropertyDefinition definition,
                                    Class<?> beanClass) {
        Set<String> roles = visibilityService.requiredRoles(beanClass,
                annotated(definition == null ? null : definition.getField()),
                annotated(definition == null ? null : definition.getGetter()),
                annotated(property.getMember()));
        return new VisiblePropertyWriter(property, visibilityService, roles);
    }

    private Map<String, BeanPropertyDefinition> definitions(BeanDescription beanDescription) {
        Map<String, BeanPropertyDefinition> definitions = new HashMap<>();
        for (BeanPropertyDefinition definition : beanDescription.findProperties()) {
            definitions.put(definition.getName(), definition);
        }
        return definitions;
    }

    private AnnotatedElement annotated(AnnotatedMember member) {
        return member == null ? null : member.getAnnotated();
    }
}
