package software.plusminus.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import software.plusminus.security.configs.SecurityAuditorAware;
import software.plusminus.security.configs.SecurityAutoconfig;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EntityScan("software.plusminus.security")
@Import(SecurityAutoconfig.class)
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SecurityAuditorAware();
    }
}
