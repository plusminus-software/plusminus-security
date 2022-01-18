package software.plusminus.security.service.check;

import org.springframework.stereotype.Component;
import software.plusminus.security.configs.SecuredRequest;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class HostSecurityCheck implements SecurityCheck {
    
    @Override
    public boolean check(SecuredRequest request, Object handler) {
        if (!request.getAuthenticationParameters().getOtherParameters().containsKey("host")) {
            return true;
        }
        String allowedHost = request.getAuthenticationParameters().getOtherParameters()
                .get("host").toString();
        
        String currentHost = getHost(request);
        return allowedHost.equals(currentHost);
    }

    private String getHost(SecuredRequest request) {
        URL url;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new SecurityException(e);
        }
        return url.getHost();
    }
}
