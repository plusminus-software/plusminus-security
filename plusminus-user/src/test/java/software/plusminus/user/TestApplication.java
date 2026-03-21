package software.plusminus.user;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import software.plusminus.user.config.UserAutoconfig;

@SpringBootApplication
@Import(UserAutoconfig.class)
public class TestApplication {
}
