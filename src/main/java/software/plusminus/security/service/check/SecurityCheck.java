package software.plusminus.security.service.check;

import software.plusminus.security.configs.SecuredRequest;

public interface SecurityCheck {
    
    boolean check(SecuredRequest request, Object handler);
    
}
