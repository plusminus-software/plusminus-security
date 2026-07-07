package software.plusminus.login.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("plusminus.login")
public class LoginProperties {
    private String template = "index";
}
