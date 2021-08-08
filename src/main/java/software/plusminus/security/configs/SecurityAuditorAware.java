package software.plusminus.security.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import software.plusminus.security.context.SecurityContext;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Autowired
    private SecurityContext securityContext;
    
    @Override
    public Optional<String> getCurrentAuditor() {
        String username = securityContext.getUsername();
        if (username == null) {
            return Optional.empty();
        }
        return Optional.of(username);
    }

}
