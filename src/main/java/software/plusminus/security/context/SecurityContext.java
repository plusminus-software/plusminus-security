package software.plusminus.security.context;

import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.context.Context;

import javax.annotation.Nullable;

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
    
    @Nullable
    public String getParameter(String key) {
        Object value = getOtherParameters().get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    @Nullable
    public <T> T getParameter(String key, Class<T> type) {
        Object value = getOtherParameters().get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }
}
