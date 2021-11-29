package software.plusminus.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import software.plusminus.security.configs.SecurityAutoconfig;
import software.plusminus.security.configs.SecurityContextAutoconfig;

@SpringBootApplication
@Import({SecurityAutoconfig.class, SecurityContextAutoconfig.class})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
