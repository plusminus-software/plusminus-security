package software.plusminus.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import software.plusminus.security.configs.SecurityAutoconfig;

@SpringBootApplication
@EntityScan("software.plusminus.security")
@Import(SecurityAutoconfig.class)
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
