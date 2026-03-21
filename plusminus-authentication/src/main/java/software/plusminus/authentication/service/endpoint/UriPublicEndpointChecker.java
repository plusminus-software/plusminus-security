package software.plusminus.authentication.service.endpoint;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.context.Context;

import java.util.Optional;
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
        return properties.getOpenUris().stream()
                .anyMatch(requestUri.get()::matches);
    }
}
