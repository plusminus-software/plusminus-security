package software.plusminus.security;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import software.plusminus.context.Context;

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
