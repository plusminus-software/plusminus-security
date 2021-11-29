package software.plusminus.security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.context.Context;
import software.plusminus.context.ThreadLocalContext;

@Configuration
@ComponentScan("software.plusminus.security.context")
public class SecurityContextAutoconfig {

    @Bean
    public Context<AuthenticationParameters> parametersContext() {
        return new ThreadLocalContext<>();
    }
}
