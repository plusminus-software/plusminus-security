package software.plusminus.jwt.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.plusminus.jwt.service.IssuerContext;
import software.plusminus.jwt.service.JwtParser;
import software.plusminus.jwt.service.NimbusJwtParser;

import java.security.interfaces.RSAPublicKey;

@Configuration
@ComponentScan("software.plusminus.jwt")
public class JwtAutoconfig {

    @Bean
    RSAKey rsaJwk(RSAPublicKey publicKey, JwtProperties jwtProperties) {
        RSAKey key = new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .build();
        return new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(resolveKeyId(key, jwtProperties))
                .build();
    }

    @Bean
    JwtParser jwtParser(RSAKey rsaJwk,
                        IssuerContext issuerContext) {
        return new NimbusJwtParser(
                new ImmutableJWKSet<>(new JWKSet(rsaJwk)),
                issuerContext);
    }

    private String resolveKeyId(RSAKey key, JwtProperties jwtProperties) {
        if (StringUtils.hasText(jwtProperties.getKeyId())) {
            return jwtProperties.getKeyId();
        }
        try {
            return key.computeThumbprint().toString();
        } catch (JOSEException e) {
            throw new SecurityException(e);
        }
    }
}
