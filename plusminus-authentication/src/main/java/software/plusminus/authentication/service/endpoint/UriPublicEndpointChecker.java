package software.plusminus.authentication.service.endpoint;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.context.Context;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Component
public class UriPublicEndpointChecker implements PublicEndpointChecker {

    private Context<HttpServletRequest> httpServletRequestContext;
    private SecurityProperties properties;

    @Override
    public boolean isPublicEndpoint() {
        Optional<String> requestUri = httpServletRequestContext.optional()
                .map(HttpServletRequest::getRequestURI);
        if (!requestUri.isPresent()) {
            return false;
        }
        String uri = requestUri.get();
        return properties.getOpenUris().stream()
                .anyMatch(pattern -> fullMatch(pattern, uri));
    }

    private boolean fullMatch(String pattern, String uri) {
        try {
            return Pattern.compile(pattern).matcher(uri).matches();
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
