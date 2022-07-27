package software.plusminus.security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
@ComponentScan("software.plusminus.security")
public class SecurityAutoconfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SecurityAuditorAware();
    }
    
}

