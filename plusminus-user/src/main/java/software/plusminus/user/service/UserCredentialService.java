package software.plusminus.user.service;

import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.user.model.User;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class UserCredentialService implements CredentialService {
    
    private TenantWrapper tenantWrapper;
    private UserService userService;
    
    @Nullable
    @Override
    public Security provideSecurity(String userString, String password) {
        User user = tenantWrapper.callWithTenantIfNeeded(userString,
                () -> userService.findUser(userString, password));
        if (user == null) {
            return null;
        }
        return Security.builder()
                .username(user.getUsername())
                .roles(user.getRoles())
                .parameters(getParameters(user))
                .build();
    }
    
    private Map<String, String> getParameters(User user) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("tenant", user.getTenant());
        parameters.put("email", user.getEmail());
        return Collections.unmodifiableMap(parameters);
    }
}
