package software.plusminus.security.configs;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.Optional;

@AllArgsConstructor
public class SecurityAuditorAware implements AuditorAware<String> {

    private Context<Security> securityContext;
    
    @Override
    public Optional<String> getCurrentAuditor() {
        return securityContext.optional()
                .map(Security::getUsername);
    }
}
