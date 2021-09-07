package software.plusminus.security.context;

import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.context.Context;

@Component
public class SecurityContext {
    
    private static final AuthenticationParameters EMPTY_CONTEXT = 
            AuthenticationParameters.builder().build();
    
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
