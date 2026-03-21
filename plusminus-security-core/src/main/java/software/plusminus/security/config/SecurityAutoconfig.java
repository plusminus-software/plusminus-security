package software.plusminus.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.context.Context;
import software.plusminus.security.Security;
import software.plusminus.security.service.SecurityService;

@Configuration
@ComponentScan("software.plusminus.security")
public class SecurityAutoconfig {

    @Bean
    Context<Security> securityContext(SecurityService service) {
        return Context.of(service::getSecurity);
    }
}
