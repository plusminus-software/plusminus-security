package software.plusminus.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import software.plusminus.context.Context;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EntityScan("software.plusminus.security")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider(Context<Security> securityContext) {
        return new SecurityAuditorAware(securityContext);
    }
}
