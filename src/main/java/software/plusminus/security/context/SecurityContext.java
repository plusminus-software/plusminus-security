package software.plusminus.security.context;

import lombok.experimental.Delegate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.plusminus.security.Security;
import software.plusminus.security.SecurityRequest;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityContext {
    
    private static final Security EMPTY_SECURITY = Security.builder().build();
    
    @Delegate
    private Security getSecurity() {
        HttpServletRequest request = getRequest();
        if (request instanceof SecurityRequest) {
            SecurityRequest securityRequest = (SecurityRequest) request;
            Security security = securityRequest.getSecurity();
            if (security != null) {
                return security;
            }
        }
        return EMPTY_SECURITY;
    }
    
    @Nullable
    public String get(String key) {
        return getOthers().get(key);
    }
    
    @Nullable
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
