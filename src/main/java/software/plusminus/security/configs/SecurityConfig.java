package software.plusminus.security.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.context.Context;
import software.plusminus.context.ThreadLocalContext;
import software.plusminus.security.properties.SecurityProperties;

@Configuration
@ComponentScan({"software.plusminus.security", "software.plusminus.jwt"})
@EntityScan("software.plusminus.security")
@EnableJpaRepositories("software.plusminus.security")
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public Context<AuthenticationParameters> parametersContext() {
        return new ThreadLocalContext<>();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(SecurityProperties properties,
                                                     AuthenticationService authenticationService,
                                                     Context<AuthenticationParameters> parametersContext) {
        return new AuthenticationFilter(properties, authenticationService, parametersContext);
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistration(AuthenticationFilter authenticationFilter) {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor());
    }
}

