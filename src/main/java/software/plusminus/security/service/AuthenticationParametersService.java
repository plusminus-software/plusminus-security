package software.plusminus.security.service;

import org.springframework.stereotype.Service;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.security.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthenticationParametersService {
    
    public AuthenticationParameters createParameters(User user, Map<String, Object> additionalParameters) {
        Map<String, Object> otherParameters = new LinkedHashMap<>();
        otherParameters.put("tenant", user.getTenant());
        otherParameters.put("email", user.getEmail());
        otherParameters.putAll(additionalParameters);
        return AuthenticationParameters.builder()
                .username(user.getUsername())
                .roles(user.getRoles())
                .otherParameters(otherParameters)
                .build();
    }
}
