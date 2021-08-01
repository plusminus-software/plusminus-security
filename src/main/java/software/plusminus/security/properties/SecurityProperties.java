package software.plusminus.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import software.plusminus.security.controller.SecurityController;

import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.Pattern;

@Data
@Configuration
@ConfigurationProperties("security")
public class SecurityProperties {

    private String cookieName = "JWT-TOKEN";
    @Pattern(regexp = SecurityController.RELATIVE_URI_REGEX)
    private String loginPage = "/";
    private String loginRedirect = "/unimarket/index.html";
    private List<String> openUrls = Arrays.asList(
            loginPage,
            "/h2-console",
            "/login",
            "/favicon.ico",
            "/home.*",
            "/",
            "/index",
            "/health");

}

