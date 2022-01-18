package software.plusminus.security.service.populator;

import org.springframework.lang.Nullable;

import java.util.Map;

public interface LoginParameter {
    
    @Nullable
    Map.Entry<String, Object> authenticationParameter();
    
}
