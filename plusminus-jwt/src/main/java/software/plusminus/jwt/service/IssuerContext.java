package software.plusminus.jwt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.plusminus.context.Context;
import software.plusminus.jwt.config.JwtProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Component
public class IssuerContext implements Context<String> {

    private Optional<Context<HttpServletRequest>> httpServletRequestContext;
    private JwtProperties jwtProperties;

    @Override
    public String get() {
        if (StringUtils.hasText(jwtProperties.getIssuer())) {
            return jwtProperties.getIssuer();
        }
        return httpServletRequestContext
                .map(Context::optional)
                .flatMap(Function.identity())
                .map(HttpServletRequest::getRequestURL)
                .map(Objects::toString)
                .map(requestUrl -> {
                    try {
                        return new URL(requestUrl);
                    } catch (MalformedURLException e) {
                        throw new SecurityException(e);
                    }
                })
                .map(URL::getHost)
                .orElse(null);
    }
}
