package software.plusminus.security.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.plusminus.security.Security;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@AllArgsConstructor
@Service
public class SecurityService {

    private List<TokenContext> tokenContexts;
    private List<TokenProcessor> tokenProcessors;
    private List<CredentialService> credentialServices;
    private List<SecurityParameterProvider> parameterProviders;

    @Nullable
    public Security getSecurity() {
        String token = findFirst(tokenContexts, TokenContext::getToken);
        if (token == null) {
            return null;
        }
        return findFirst(tokenProcessors,
                tokenProcessor -> tokenProcessor.getSecurity(token));
    }

    @Nullable
    public String createToken(String user, String password) {
        Security security = findFirst(credentialServices,
                credentialService -> credentialService.provideSecurity(user, password));
        if (security == null) {
            return null;
        }
        Security processedSecurity = processSecurity(security);
        String token = findFirst(tokenProcessors,
                tokenProcessor -> tokenProcessor.getToken(processedSecurity));
        if (token == null) {
            throw new IllegalStateException("Cannot get token for user " + user);
        }
        boolean tokenSet = tokenContexts.stream()
                .map(tokenManager -> tokenManager.setToken(token))
                .filter(value -> value)
                .findFirst()
                .orElse(Boolean.FALSE);
        if (!tokenSet) {
            throw new IllegalStateException("Cannot set token for user " + user);
        }
        return token;
    }

    public void clearToken() {
        tokenContexts.forEach(TokenContext::clearToken);
    }

    private <T, R> R findFirst(List<T> list, Function<T, R> function) {
        return list.stream()
                .map(function)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Security processSecurity(Security originalSecurity) {
        return Security.builder()
                .username(originalSecurity.getUsername())
                .roles(originalSecurity.getRoles())
                .parameters(getSecurityParameters(originalSecurity))
                .build();
    }

    private Map<String, String> getSecurityParameters(Security originalSecurity) {
        Stream<Map.Entry<String, String>> providedParameters = parameterProviders.stream()
                .map(SecurityParameterProvider::providerParameter)
                .filter(Objects::nonNull);
        return Stream.concat(originalSecurity.getParameters().entrySet().stream(), providedParameters)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                        Collections::unmodifiableMap));
    }
}
