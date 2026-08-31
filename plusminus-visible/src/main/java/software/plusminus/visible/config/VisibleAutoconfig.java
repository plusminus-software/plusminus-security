package software.plusminus.visible.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.visible.jackson.VisibleSerializerModifier;
import software.plusminus.visible.service.VisibilityService;

/**
 * Registers the visibility filter with every {@code ObjectMapper} Spring Boot builds.
 *
 * <p>A {@link Module} bean is picked up by {@code JacksonAutoConfiguration} and applied to the
 * mapper the web layer uses, so nothing has to be wired per controller. An application that
 * builds its own mapper has to register {@link VisibleSerializerModifier} itself - otherwise its
 * responses are unfiltered, which is a quiet way to lose the guarantee.
 */
@Configuration
@ComponentScan("software.plusminus.visible")
public class VisibleAutoconfig {

    @Bean
    public Module visibleModule(VisibilityService visibilityService) {
        SimpleModule module = new SimpleModule("plusminus-visible");
        module.setSerializerModifier(new VisibleSerializerModifier(visibilityService));
        return module;
    }
}
