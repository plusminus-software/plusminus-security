package software.plusminus.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties("plusminus.jwt")
public class JwtProperties {

    private Resource privateKey;
    private Resource publicKey;
    private Duration expiration = Duration.ofDays(365L * 50);
    private String keyId;
    private String issuer;

}
