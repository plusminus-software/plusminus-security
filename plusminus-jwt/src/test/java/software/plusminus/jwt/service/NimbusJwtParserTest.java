package software.plusminus.jwt.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;
import software.plusminus.security.Security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NimbusJwtParserTest {

    private final KeyPair keysHolder;
    private final RSAKey publicKey;
    private final JWSSigner signer;
    private final IssuerContext issuerContext;

    private NimbusJwtParser parser;
    private String accessToken;

    NimbusJwtParserTest() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        keysHolder = generator.generateKeyPair();
        signer = new RSASSASigner(keysHolder.getPrivate());
        publicKey = rsakey(keysHolder.getPublic(), "keyId");
        issuerContext = mock(IssuerContext.class);
    }

    private static Date expirationTime(int offset) {
        return new Date(new Date().getTime() + offset * 1000);
    }

    private static RSAKey rsakey(PublicKey publicKey, String kid) {
        return new RSAKey(
                (RSAPublicKey) publicKey,
                KeyUse.SIGNATURE,
                null,
                null,
                kid,
                null,
                null,
                null,
                null,
                null);
    }

    private static JWTClaimsSet claims(String roles, String domain, Date expirationTime) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.subject("test");
        builder.claim("email", "test");
        builder.issueTime(new Date());

        if (!StringUtils.isEmpty(roles)) {
            builder.claim("roles", Collections.singletonList(roles));
        }

        if (!StringUtils.isEmpty(domain)) {
            builder.claim("domain", domain);
        }

        if (expirationTime != null) {
            builder.expirationTime(expirationTime);
        }
        return builder.build();
    }

    @BeforeEach
    void beforeEach() {
        parser = new NimbusJwtParser(
                new ImmutableJWKSet<>(new JWKSet(publicKey)),
                issuerContext);
        JWTClaimsSet claims = claims("test-role", "some_domain", expirationTime(60));
        accessToken = jws(claims, publicKey).serialize();
    }

    @Test
    void testUnauthorized() {
        Security security = parser.parseToken(null);
        assertThat(security).isNull();
    }

    @Test
    void testIncorrectApiKey() {
        Security security = parser.parseToken("foo");
        assertThat(security).isNull();
    }

    @Test
    void testValidAccessToken() {
        Security security = parser.parseToken(accessToken);

        assertThat(security).isNotNull();
        assertThat(security.getUsername()).isEqualTo("test");
        assertThat(security.getRoles()).contains("test-role");
    }

    @Test
    void testInvalidAccessToken() {
        checkInvalidAccessToken(accessToken.toUpperCase());
        checkInvalidAccessToken(" ." + accessToken);
        checkInvalidAccessToken("Vendor " + accessToken);
    }

    @Test
    void testAccessTokenWithUnknownKey() {
        JWTClaimsSet claims = claims(
                "test", "some_domain", expirationTime(60));
        String authorization = jws(
                claims, rsakey(keysHolder.getPublic(), "foo")).serialize();

        Security security = parser.parseToken(authorization);

        assertThat(security).isNull();
    }

    @Test
    void testAccessTokenWithoutRoles() {

        JWTClaimsSet claims = claims(null,
                "_some_domain",
                expirationTime(60));
        String token = jws(claims, publicKey).serialize();

        Security security = parser.parseToken(token);

        assertThat(security).isNotNull();
        assertThat(security.getUsername()).isEqualTo("test");
        assertThat(security.getRoles()).isEmpty();
    }

    @Test
    void testJwtWithExpiredTime() {
        String authorization = jws(
                claims("point-observation",
                        "some_domain",
                        expirationTime(-60)),
                publicKey)
                .serialize();

        Security security = parser.parseToken(authorization);

        assertThat(security).isNull();
    }

    private void checkInvalidAccessToken(String token) {
        Security security = parser.parseToken(token);
        assertThat(security).isNull();
    }

    private JWSObject jws(JWTClaimsSet claims, RSAKey rsaPublicKey) {
        JWSObject jws = new JWSObject(
                new JWSHeader(
                        JWSAlgorithm.RS512,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        rsaPublicKey.getKeyID(),
                        null,
                        null),
                new Payload(claims.toJSONObject()));
        try {
            jws.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalArgumentException(e);
        }
        return jws;
    }
}
