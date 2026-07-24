package software.plusminus.jwt.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.jwt.config.JwtProperties;
import software.plusminus.security.Security;

import java.security.PrivateKey;
import java.time.OffsetDateTime;
import java.util.Date;

@AllArgsConstructor
@Component
public class NimbusJwtGenerator implements JwtGenerator {

    private PrivateKey privateKey;
    private IssuerContext issuerContext;
    private JwtProperties jwtProperties;
    private RSAKey rsaJwk;

    @Override
    public String generateAccessToken(Security security) {
        JWSSigner signer = new RSASSASigner(privateKey);
        OffsetDateTime issuedAt = OffsetDateTime.now();
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(security.getUsername())
                .issuer(issuerContext.get())
                .issueTime(Date.from(issuedAt.toInstant()))
                .expirationTime(Date.from(issuedAt.plus(jwtProperties.getExpiration())
                                .toInstant()))
                .claim("roles", security.getRoles());
        security.getParameters().forEach(claimsSetBuilder::claim);
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaJwk.getKeyID())
                .build();
        SignedJWT signedJwt = new SignedJWT(header, claimsSetBuilder.build());
        try {
            signedJwt.sign(signer);
        } catch (JOSEException e) {
            throw new SecurityException(e);
        }
        return signedJwt.serialize();
    }
}
