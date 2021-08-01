package software.plusminus.security.service;

import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.context.Context;

@Service
public class SecurityContext {
    
    private static final AuthenticationParameters EMPTY_CONTEXT = new AuthenticationParameters();
    
    @Autowired
    private Context<AuthenticationParameters> container;
    
    @Delegate
    private AuthenticationParameters getParameters() {
        AuthenticationParameters parameters = container.get();
        if (parameters == null) {
            return EMPTY_CONTEXT;
        }
        return parameters;
    }
}
