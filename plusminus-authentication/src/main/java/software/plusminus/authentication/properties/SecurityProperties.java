package software.plusminus.authentication.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import software.plusminus.authentication.annotation.Regex;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties("plusminus.security")
public class SecurityProperties {
    private List<@Regex String> openUris = Collections.emptyList();
    private String loginPage;
    private Duration cookieMaxAge = Duration.ofDays(365L * 50);
}
