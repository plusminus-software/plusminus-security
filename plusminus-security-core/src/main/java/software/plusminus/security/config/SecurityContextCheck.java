package software.plusminus.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenContext;
import software.plusminus.security.service.TokenProcessor;

import java.util.List;

@Profile("!test")
@Component
public class SecurityContextCheck {

    @Autowired
    void check(List<TokenProcessor> tokenProcessors,
               List<CredentialService> credentialServices,
               List<TokenContext> tokenContexts) {
        if (tokenProcessors.isEmpty()) {
            throw new IllegalStateException("At least one TokenService is expected to be present in the context");
        }
        if (credentialServices.isEmpty()) {
            throw new IllegalStateException("At least one CredentialService is expected to be present in the context");
        }
        if (tokenContexts.isEmpty()) {
            throw new IllegalStateException("At least one TokenContext is expected to be present in the context");
        }
    }
}
