package software.plusminus.authorization.service.role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RoleAuthorizerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @SpyBean
    private RoleAuthorizer roleAuthorizer;
    @SpyBean
    private Context<Security> securityContext;

    @Test
    void roleAllowed() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("admin"))
                .build();
        when(securityContext.get()).thenReturn(security);
        String url = "http://localhost:" + port + "/";

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        check(result.getBody()).is("test");
        check(result.getStatusCode()).is(HttpStatus.OK);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    @Test
    void roleIsCaseInsensitive() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("ADMIN"))
                .build();
        when(securityContext.get()).thenReturn(security);

        ResponseEntity<String> result = restTemplate.getForEntity("/", String.class);

        check(result.getBody()).is("test");
        check(result.getStatusCode()).is(HttpStatus.OK);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    @Test
    void roleNotAllowed() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("user"))
                .build();
        when(securityContext.get()).thenReturn(security);

        ResponseEntity<Map> response = restTemplate.getForEntity("/", Map.class);

        checkTimestamp(response.getBody());
        check(response.getBody()).is("/forbidden-message.json");
        check(response.getStatusCode()).is(HttpStatus.FORBIDDEN);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    private void checkTimestamp(Map<String, String> errors) {
        String timestamp = errors.get("timestamp");
        ZonedDateTime time = ZonedDateTime.parse(timestamp.replace("+0000", "Z"));
        check(time).isRecent();
        errors.remove("timestamp");
    }
}