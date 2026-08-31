package software.plusminus.visible.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import software.plusminus.visible.annotation.Visible;
import software.plusminus.visible.service.VisibleService;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps restricted properties so that {@link Visible} decides per request whether to write them.
 *
 * <p>Unrestricted properties are left as they are. So are all properties of a class that does not
 * use {@link Visible} are visible by default - see {@link VisibleService#hasVisibility}.
 */
public class VisibleSerializerModifier extends BeanSerializerModifier {

    private static final long serialVersionUID = 1L;

    private final transient VisibleService visibleService;

    public VisibleSerializerModifier(VisibleService visibleService) {
        this.visibleService = visibleService;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDescription,
                                                     List<BeanPropertyWriter> properties) {
        if (!visibleService.hasVisibility(beanDescription.getBeanClass())) {
            return properties;
        }
        Map<String, BeanPropertyDefinition> definitions = definitions(beanDescription);
        List<BeanPropertyWriter> wrapped = new ArrayList<>(properties.size());
        for (BeanPropertyWriter property : properties) {
            wrapped.add(wrap(property, definitions.get(property.getName())));
        }
        return wrapped;
    }

    private BeanPropertyWriter wrap(BeanPropertyWriter property, BeanPropertyDefinition definition) {
        Set<String> roles = visibleService.requiredRoles(
                annotated(definition == null ? null : definition.getField()),
                annotated(definition == null ? null : definition.getGetter()),
                annotated(property.getMember()));
        if (roles == null) {
            return property;
        }
        return new VisiblePropertyWriter(property, visibleService, roles);
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
