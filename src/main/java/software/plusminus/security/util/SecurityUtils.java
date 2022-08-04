package software.plusminus.security.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.plusminus.security.Security;
import software.plusminus.security.SecurityRequest;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class SecurityUtils {

    public void authenticate(Security security) {
        authenticate(getRequest(), security);
    }
    
    public void authenticate(HttpServletRequest request, Security security) {
        SecurityRequest securityRequest = new SecurityRequest(request, security);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(securityRequest));   
    }
    
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return new EmptyHttpServletRequest();
    }
}
